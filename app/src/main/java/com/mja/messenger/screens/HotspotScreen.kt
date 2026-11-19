package com.mja.messenger.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mja.messenger.net.LocalNet
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 30 — Hotspot / Local Server management
 */
@Composable
fun HotspotScreen(
    onBack: () -> Unit,
    onOpenDevices: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var starting by remember { mutableStateOf(false) }
    var resultMsg by remember { mutableStateOf<Pair<Boolean, String?>?>(null) }
    var toast by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) { delay(500); loading = false; LocalNet.refreshStatus() }
    LaunchedEffect(toast) { if (toast != null) { delay(2500); toast = null } }

    val statusFlow = LocalNet.status
    val status = statusFlow.collectAsState().value
    val peers by LocalNet.peers.collectAsState()
    val active = LocalNet.hotspotActive() || status.hostActive

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        val allGranted = grants.values.all { it }
        toast = if (allGranted) "مجوزها اعطا شد؛ دوباره تلاش کنید" else "مجوزها رد شدند"
    }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            ScreenHeader(title = "مدیریت Hotspot", onBack = onBack)

            Column(Modifier.padding(horizontal = 20.dp)) {
                when {
                    loading -> SkeletonList(count = 5, showAvatar = false)

                    else -> {
                        // Status card
                        MjaCard(modifier = Modifier.fillMaxWidth(), elevated = true) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(
                                            if (active) MjaSuccess.copy(alpha = 0.14f) else MjaElevated,
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        Icons_Wifi,
                                        contentDescription = null,
                                        tint = if (active) MjaSuccess else MjaTextDisabled,
                                        modifier = Modifier.size(30.dp),
                                    )
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    StatusPill(
                                        if (active) "فعال" else "غیرفعال",
                                        color = if (active) MjaSuccess else MjaTextDisabled,
                                        filled = active,
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    LtrText(
                                        status.ssid.ifBlank { "MJA_Host" },
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MjaTextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))

                        // Details
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                InfoRow("نام شبکه", status.ssid.ifBlank { "MJA_Host" }, valueLtr = true)
                                InfoRow("تعداد کاربران متصل", "${peers.size}")
                                InfoRow("وضعیت Local Server", if (status.hostActive) "فعال" else "غیرفعال")
                                InfoRow("آدرس IP میزبان", if (status.ip != "0.0.0.0") status.ip else "192.168.43.1", valueLtr = true)
                            }
                        }
                        Spacer(Modifier.height(14.dp))

                        // Result banner
                        resultMsg?.let { (ok, detail) ->
                            if (ok) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MjaSuccess.copy(alpha = 0.12f))
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(Icons_CheckCircle, contentDescription = null, tint = MjaSuccess, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(10.dp))
                                    Text("Hotspot فعال شد.", style = MaterialTheme.typography.bodyMedium, color = MjaSuccess)
                                }
                            } else {
                                ErrorState(
                                    title = "Hotspot روشن نشد.",
                                    description = detail ?: "خطای ناشناخته در راه‌اندازی.",
                                    actionText = "هدایت به تنظیمات Android",
                                    onAction = onOpenSettings,
                                )
                            }
                            Spacer(Modifier.height(14.dp))
                        }

                        // Primary action
                        MjaButton(
                            text = when {
                                starting -> "در حال راه‌اندازی..."
                                active -> "توقف سرور"
                                else -> "شروع سرور"
                            },
                            style = if (active) MjaButtonStyle.Destructive else MjaButtonStyle.Primary,
                            icon = if (active) Icons_Close else Icons_Wifi,
                            loading = starting,
                            onClick = {
                                if (active) {
                                    LocalNet.stopHotspot()
                                    resultMsg = null
                                    toast = "سرور محلی متوقف شد"
                                } else {
                                    starting = true
                                    LocalNet.startHotspot { ok, detail ->
                                        starting = false
                                        resultMsg = ok to detail
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(14.dp))

                        // Secondary rows
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                SettingsRow(
                                    title = "مدیریت کاربران",
                                    icon = Icons_People,
                                    subtitle = "${peers.size} کاربر متصل",
                                    onClick = onOpenDevices,
                                )
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(
                                    title = "تنظیمات",
                                    icon = Icons_Settings,
                                    onClick = onOpenSettings,
                                )
                            }
                        }
                        Spacer(Modifier.height(14.dp))

                        // Compliance note
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MjaWarning.copy(alpha = 0.10f))
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Icon(Icons_InfoOutlined, contentDescription = null, tint = MjaWarning, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "برخی نسخه‌های اندروید اجازه کنترل خودکار Hotspot را نمی‌دهند؛ در این حالت به تنظیمات سیستم هدایت می‌شوید.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MjaTextSecondary,
                                lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
                            )
                        }

                        // Permission request
                        Spacer(Modifier.height(14.dp))
                        MjaButton(
                            text = "ارائه مجوزهای لازم",
                            style = MjaButtonStyle.Ghost,
                            icon = Icons_Security,
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.NEARBY_WIFI_DEVICES,
                                    ),
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(28.dp))
                    }
                }
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }
    }
}
