package com.mja.messenger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.Repo
import com.mja.messenger.data.timeLabel
import com.mja.messenger.net.LocalNet
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 31 — Connected Devices
 */
@Composable
fun ConnectedDevicesScreen(onBack: () -> Unit, onOpenProfile: (String) -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var refreshKey by remember { mutableStateOf(0) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(refreshKey) { delay(600); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2000); toast = null } }

    val peers by LocalNet.peers.collectAsState()
    val statusFlow = LocalNet.status
    val status = statusFlow.collectAsState().value
    val users by Repo.users.collectAsState()

    // Snapshot of connected devices (real peers + ARP)
    val devices = remember(refreshKey, peers, users) { LocalNet.connectedDevices() }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(title = "دستگاه‌های متصل", onBack = onBack) {
                MjaIconButton(icon = Icons_Refresh, onClick = { refreshKey++ }, contentDescription = "تازه‌سازی")
            }

            Column(Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusPill(
                        "${devices.size} کاربر متصل",
                        color = if (devices.isNotEmpty()) MjaSuccess else MjaTextSecondary,
                        filled = devices.isNotEmpty(),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "سرویس: ${if (status.hostActive) "فعال" else "غیرفعال"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MjaTextSecondary,
                    )
                }
                Spacer(Modifier.height(10.dp))
            }

            when {
                loading -> SkeletonList(count = 5)

                devices.isEmpty() -> EmptyState(
                    icon = Icons_WifiOff,
                    title = "دستگاهی متصل نیست.",
                    description = "پس از فعال‌شدن Hotspot و اتصال کاربران دیگر، دستگاه‌ها اینجا نمایش داده می‌شوند.",
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                ) {
                    items(devices, key = { it.id }) { device ->
                        val knownUser = users.firstOrNull { it.username == device.username || it.id == device.id }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MjaCard)
                                .clickable {
                                    if (knownUser != null) onOpenProfile(knownUser.id)
                                    else toast = "کاربر MJA برای این دستگاه شناسایی نشد"
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier.size(46.dp).clip(RoundedCornerShape(13.dp)).background(MjaElevated),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons_Device, contentDescription = null, tint = MjaBrand, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    knownUser?.deviceName?.ifBlank { null } ?: device.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MjaTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(Modifier.height(3.dp))
                                if (knownUser != null) {
                                    Text(
                                        knownUser.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MjaTextSecondary,
                                        maxLines = 1,
                                    )
                                    Spacer(Modifier.height(3.dp))
                                }
                                Row {
                                    LtrText(
                                        "@${device.username}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MjaTextDisabled,
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    LtrText(
                                        "آدرس IP: ${device.ip}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MjaTextDisabled,
                                    )
                                }
                                Spacer(Modifier.height(5.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "زمان اتصال: ${timeLabel(device.lastSeen)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MjaTextDisabled,
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    StatusPill(
                                        if (System.currentTimeMillis() - device.lastSeen < 15_000) "نشست فعال" else "در انتظار",
                                        color = if (System.currentTimeMillis() - device.lastSeen < 15_000) MjaSuccess else MjaWarning,
                                    )
                                }
                            }
                            Icon(Icons_Chevron, contentDescription = null, tint = MjaTextDisabled, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }
    }
}
