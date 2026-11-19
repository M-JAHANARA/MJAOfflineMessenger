package com.mja.messenger.net

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.mja.messenger.data.NetStatus
import com.mja.messenger.data.Peer
import com.mja.messenger.data.Repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException

/**
 * LocalNet — the real offline transport of MJA.
 *
 * - UDP broadcast discovery of MJA peers on the local network (no internet)
 * - TCP message transport with ACK (store-and-forward in the offline queue)
 * - Local-only hotspot control (with a graceful hand-off to system settings)
 * - Connected-device inspection (ARP + discovered peers)
 */
object LocalNet {

    private const val DISC_PORT = 47777
    private const val MSG_PORT = 47778
    private const val TAG = "MJA"
    private const val PEER_TTL = 9_000L

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _status = MutableStateFlow(NetStatus())
    private val _peers = MutableStateFlow<List<Peer>>(emptyList())

    val status: StateFlow<NetStatus> = _status.asStateFlow()
    val peers: StateFlow<List<Peer>> = _peers.asStateFlow()

    private var appContext: Context? = null
    private var discSocket: DatagramSocket? = null
    private var serverSocket: ServerSocket? = null
    private var running = false
    private var hostStartedAt = 0L
    private var reservation: WifiManager.LocalOnlyHotspotReservation? = null
    private var wifiReceiver: BroadcastReceiver? = null

    private val selfId: String by lazy {
        android.provider.Settings.Secure.getString(
            appContext?.contentResolver, android.provider.Settings.Secure.ANDROID_ID
        ) ?: "mja-device"
    }

    /* ------------------------------- lifecycle -------------------------- */

    fun start(context: Context) {
        if (running) return
        appContext = context.applicationContext
        running = true

        registerWifiReceiver()
        refreshStatus()
        startDiscovery()
        startServer()
        hostStartedAt = System.currentTimeMillis()

        scope.launch {
            while (isActive) {
                refreshStatus()
                expirePeers()
                delay(2_000)
            }
        }
    }

    fun stop() {
        running = false
        runCatching { discSocket?.close() }
        runCatching { serverSocket?.close() }
        discSocket = null
        serverSocket = null
        wifiReceiver?.let { runCatching { appContext?.unregisterReceiver(it) } }
        wifiReceiver = null
        stopHotspot()
    }

    private fun registerWifiReceiver() {
        val ctx = appContext ?: return
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) { refreshStatus() }
        }
        runCatching {
            ctx.registerReceiver(receiver, IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION))
            wifiReceiver = receiver
        }
    }

    /* ------------------------------ status ------------------------------ */

    fun refreshStatus() {
        val ctx = appContext ?: return
        val wm = ctx.getSystemService(Context.WIFI_SERVICE) as? WifiManager ?: return
        var ssid = "—"
        var ip = "0.0.0.0"
        var signal = 0
        var connected = false
        var hotspot = false
        try {
            val info = @Suppress("DEPRECATION") wm.connectionInfo
            ssid = @Suppress("DEPRECATION") info?.ssid?.removeSurrounding("\"") ?: "—"
            if (ssid == "<unknown ssid>" || ssid.isBlank()) ssid = "شبکه محلی"
            signal = if (info != null) WifiManager.calculateSignalLevel(info.rssi, 100) else 0
            connected = info != null && info.networkId != -1
            @Suppress("DEPRECATION")
            ip = intToIp(wm.dhcpInfo?.ipAddress ?: 0)
            hotspot = reservation != null || ip.startsWith("192.168.43.") ||
                ssid.startsWith("AndroidAP") || ssid.contains("MJA", ignoreCase = true) && reservation != null
        } catch (_: SecurityException) {
            connected = false
            ssid = "بدون دسترسی"
        }
        if (ip == "0.0.0.0") ip = primaryIp()
        _status.value = NetStatus(
            wifiEnabled = wm.isWifiEnabled,
            connected = connected || reservation != null,
            ssid = ssid,
            ip = ip,
            signal = signal,
            isHotspot = hotspot,
            hostActive = serverSocket?.isClosed == false,
            serverIp = ip,
            users = _peers.value.size,
            uptime = if (hostStartedAt == 0L) 0 else System.currentTimeMillis() - hostStartedAt,
            scanning = running,
        )
    }

    fun isConnected(): Boolean =
        _status.value.connected || _peers.value.isNotEmpty()

    fun hasRealPeer(): Boolean = _peers.value.isNotEmpty()

    fun primaryIp(): String = runCatching {
        NetworkInterface.getNetworkInterfaces()?.toList()?.firstOrNull { nfi ->
            nfi.isUp && !nfi.isLoopback && nfi.inetAddresses.any { it is Inet4Address && !it.isLoopbackAddress }
        }?.inetAddresses?.firstOrNull { it is Inet4Address && !it.isLoopbackAddress }?.hostAddress ?: "0.0.0.0"
    }.getOrDefault("0.0.0.0")

    /* ---------------------------- discovery ----------------------------- */

    private fun startDiscovery() {
        scope.launch {
            runCatching {
                val socket = DatagramSocket().apply {
                    broadcast = true
                    soTimeout = 2_000
                }
                discSocket = socket
                var lastSend = 0L
                while (isActive && running) {
                    val now = System.currentTimeMillis()
                    if (now - lastSend > 2_000) {
                        lastSend = now
                        val payload = "$TAG|$selfId|${me()}|${_status.value.ip}"
                            .toByteArray(Charsets.UTF_8)
                        runCatching {
                            socket.send(
                                DatagramPacket(
                                    payload, payload.size,
                                    InetAddress.getByName("255.255.255.255"), DISC_PORT,
                                )
                            )
                        }
                    }
                    val buf = ByteArray(512)
                    val packet = DatagramPacket(buf, buf.size)
                    runCatching { socket.receive(packet) }
                    if (packet.length > 0) {
                        val msg = String(buf, 0, packet.length, Charsets.UTF_8)
                        handleDiscovery(msg, packet.address?.hostAddress ?: "")
                    }
                }
            }
        }
    }

    private fun handleDiscovery(msg: String, fromIp: String) {
        val parts = msg.split("|")
        if (parts.size < 3 || parts[0] != TAG) return
        val id = parts[1]
        if (id == selfId) return
        val name = parts[2]
        val ip = parts.getOrNull(3)?.ifBlank { null } ?: fromIp
        val username = parts.getOrNull(4)?.ifBlank { null } ?: "mja_user"
        val updated = _peers.value.toMutableList()
        val idx = updated.indexOfFirst { it.id == id || it.ip == ip }
        val peer = Peer(id, name, username, ip, System.currentTimeMillis())
        if (idx >= 0) updated[idx] = peer else updated.add(peer)
        _peers.value = updated
        Repo.addUserIfMissing(name, username, ip)
        refreshStatus()
    }

    private fun expirePeers() {
        val now = System.currentTimeMillis()
        val before = _peers.value.size
        val alive = _peers.value.filter { now - it.lastSeen < PEER_TTL }
        if (alive.size != before) {
            _peers.value = alive
            refreshStatus()
        }
    }

    private fun me(): String {
        val u = Repo.currentUser()
        return "${u.name.ifBlank { "کاربر MJA" }}~${u.username}"
    }

    /* ------------------------------ transport --------------------------- */

    private fun startServer() {
        scope.launch {
            runCatching {
                val server = ServerSocket()
                server.reuseAddress = true
                server.bind(InetSocketAddress(MSG_PORT))
                serverSocket = server
                while (isActive && running) {
                    val client = try { server.accept() } catch (_: Exception) { break }
                    scope.launch { handleClient(client) }
                }
            }
        }
    }

    private fun handleClient(socket: Socket) {
        runCatching {
            socket.soTimeout = 8_000
            val input = DataInputStream(socket.getInputStream())
            val output = DataOutputStream(socket.getOutputStream())
            val line = input.readUTF()
            val parts = line.split("|")
            if (parts.size >= 4 && parts[0] == "MSG") {
                val fromName = parts[1]
                val fromUser = parts[2]
                val text = parts[3]
                val fromIp = socket.inetAddress?.hostAddress ?: ""
                mainHandler.post {
                    Repo.addUserIfMissing(fromName, fromUser, fromIp)
                    Repo.receiveMessage(fromName, fromUser, text)
                }
                output.writeUTF("ACK")
                output.flush()
            } else {
                output.writeUTF("ERR")
                output.flush()
            }
            socket.close()
        }.onFailure { runCatching { socket.close() } }
    }

    /** Best-effort delivery to a real peer. Returns true when the peer ACKed. */
    fun send(ip: String, fromName: String, fromUser: String, text: String): Boolean {
        if (ip.isBlank() || ip == "0.0.0.0") return false
        return runCatching {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, MSG_PORT), 1_500)
                socket.soTimeout = 2_500
                val out = DataOutputStream(socket.getOutputStream())
                val inp = DataInputStream(socket.getInputStream())
                out.writeUTF("MSG|$fromName|$fromUser|$text")
                out.flush()
                inp.readUTF() == "ACK"
            }
        }.getOrDefault(false)
    }

    /* ------------------------------- hotspot ---------------------------- */

    fun hotspotSupported(): Boolean = true

    /** Attempts to start the local-only hotspot (Android 8+). */
    fun startHotspot(onResult: (started: Boolean, detail: String?) -> Unit) {
        val ctx = appContext
        val wm = ctx?.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        if (wm == null) { onResult(false, "Wi-Fi در دسترس نیست"); return }
        if (reservation != null) { onResult(true, null); return }
        try {
            @Suppress("DEPRECATION")
            wm.startLocalOnlyHotspot(object : WifiManager.LocalOnlyHotspotCallback() {
                override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation?) {
                    this@LocalNet.reservation = reservation
                    refreshStatus()
                    onResult(true, null)
                }

                override fun onStopped() {
                    this@LocalNet.reservation = null
                    refreshStatus()
                    onResult(false, "Hotspot متوقف شد")
                }

                override fun onFailed(reason: Int) {
                    val why = when (reason) {
                        ERROR_NO_CHANNEL -> "کانال خالی برای Hotspot وجود ندارد"
                        ERROR_GENERIC -> "خطای سیستم در راه‌اندازی Hotspot"
                        ERROR_TETHERING_DISALLOWED -> "سیستم‌عامل راه‌اندازی Hotspot را مجاز نمی‌داند"
                        else -> "Hotspot روشن نشد"
                    }
                    onResult(false, why)
                }
            }, mainHandler)
        } catch (se: SecurityException) {
            onResult(false, "مجوز موقعیت مکانی برای ساخت شبکه لازم است")
        } catch (e: Exception) {
            onResult(false, e.message ?: "Hotspot در دسترس نیست")
        }
    }

    fun stopHotspot() {
        val r = reservation ?: return
        reservation = null
        runCatching { r.stop() }
        refreshStatus()
    }

    fun hotspotActive(): Boolean = reservation != null

    /* --------------------------- devices / arp -------------------------- */

    fun connectedDevices(): List<Peer> {
        val arp = arpTable()
        val known = _peers.value.associateBy { it.ip }
        val list = mutableListOf<Peer>()
        arp.forEach { ip ->
            val peer = known[ip]
            list.add(
                peer ?: Peer(
                    id = "arp_$ip", name = "دستگاه ناشناس", username = "unknown",
                    ip = ip, lastSeen = System.currentTimeMillis(),
                )
            )
        }
        _peers.value.forEach { p -> if (list.none { it.ip == p.ip }) list.add(p) }
        return list
    }

    private fun arpTable(): List<String> {
        val ips = mutableSetOf<String>()
        runCatching {
            java.io.File("/proc/net/arp").useLines { lines ->
                lines.drop(1).forEach { line ->
                    val p = line.split(Regex("\\s+"))
                    if (p.size >= 4 && p[2] != "0x0") ips.add(p[0])
                }
            }
        }
        return ips.toList()
    }
}
