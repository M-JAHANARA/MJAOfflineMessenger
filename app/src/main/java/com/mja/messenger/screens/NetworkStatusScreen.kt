package com.mja.messenger.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.NetStatus
import com.mja.messenger.net.LocalNet
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 29 — Network Status
 */
@Composable
fun NetworkStatusScreen(
    onBack: () -> Unit,
    onOpenHotspot: () -> Unit,
    onOpenDevices: () -> Unit,
    onOpenHost: () -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(refreshKey) { delay(500); loading = false }
    LaunchedEffect(Unit) { LocalNet.refreshStatus() }

    val statusFlow = LocalNet.status
    val status = statusFlow.collectAsState().value

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            ScreenHeader(title = "وضعیت شبکه", onBack = onBack) {
                MjaIconButton(icon = Icons_Refresh, onClick = { refreshKey++ }, contentDescription = "تازه‌سازی")
            }

            Column(Modifier.padding(horizontal = 20.dp)) {
                when {
                    loading -> SkeletonList(count = 6, showAvatar = false)

                    else -> {
                        // Big status card
                        MjaCard(modifier = Modifier.fillMaxWidth(), elevated = true) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(84.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            if (status.connected) MjaSuccess.copy(alpha = 0.14f) else MjaWarning.copy(alpha = 0.14f),
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        if (status.connected) Icons_Wifi else Icons_WifiOff,
                                        contentDescription = null,
                                        tint = if (status.connected) MjaSuccess else MjaWarning,
                                        modifier = Modifier.size(40.dp),
                                    )
                                }
                                Spacer(Modifier.height(14.dp))
                                Text(
                                    if (status.connected) "متصل به شبکه محلی" else "اتصال برقرار نیست",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (status.connected) MjaSuccess else MjaWarning,
                                )
                                Spacer(Modifier.height(6.dp))
                                StatusPill(
                                    if (status.scanning) "در حال جست‌وجوی هم‌شبکه‌ها" else "جست‌وجو غیرفعال",
                                    color = if (status.scanning) MjaBrand else MjaTextDisabled,
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        // Error state when not connected
                        if (!status.connected) {
                            ErrorState(
                                title = "به شبکه محلی متصل نیست.",
                                description = "به Hotspot مجاور متصل شوید یا سرور محلی را فعال کنید.",
                                actionText = "تلاش مجدد",
                                onAction = { LocalNet.refreshStatus(); refreshKey++ },
                            )
                            Spacer(Modifier.height(16.dp))
                        }

                        // Details
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                InfoRow("نام شبکه", status.ssid, valueLtr = true)
                                InfoRow("آدرس IP محلی", status.ip, valueLtr = true)
                                InfoRow("تعداد کاربران", "${status.users}")
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("قدرت سیگنال", style = MaterialTheme.typography.bodyMedium, color = MjaTextSecondary)
                                    Spacer(Modifier.weight(1f))
                                    LtrText("${status.signal}%", style = MaterialTheme.typography.bodyMedium, color = MjaTextPrimary)
                                }
                                ProgressBar(
                                    progress = status.signal / 100f,
                                    color = if (status.signal > 55) MjaSuccess else if (status.signal > 25) MjaWarning else MjaError,
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("وضعیت سرور", style = MaterialTheme.typography.bodyMedium, color = MjaTextSecondary)
                                    Spacer(Modifier.weight(1f))
                                    StatusPill(
                                        if (status.hostActive) "فعال" else "غیرفعال",
                                        color = if (status.hostActive) MjaSuccess else MjaTextDisabled,
                                        filled = status.hostActive,
                                    )
                                }
                                InfoRow(
                                    "زمان فعالیت سرور",
                                    if (status.uptime > 0) formatUptime(status.uptime) else "—",
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        // Action cards
                        Text("دسترسی سریع", style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary)
                        Spacer(Modifier.height(10.dp))

                        ActionCard(
                            icon = Icons_Wifi,
                            title = "مدیریت Hotspot",
                            subtitle = "شروع/توقف سرور محلی و تنظیم نام شبکه",
                            onClick = onOpenHotspot,
                        )
                        Spacer(Modifier.height(10.dp))
                        ActionCard(
                            icon = Icons_Device,
                            title = "دستگاه‌های متصل",
                            subtitle = "${status.users} دستگاه در حال حاضر متصل است",
                            onClick = onOpenDevices,
                        )
                        Spacer(Modifier.height(10.dp))
                        ActionCard(
                            icon = Icons_Server,
                            title = "مدیریت میزبان",
                            subtitle = "گزارش فعالیت، نشست‌ها و محدودیت اتصال",
                            onClick = onOpenHost,
                        )
                        Spacer(Modifier.height(28.dp))
                    }
                }
            }
        }
    }
}

private fun formatUptime(ms: Long): String {
    val totalMin = ms / 60_000
    val h = totalMin / 60
    val m = totalMin % 60
    return if (h > 0) "$h ساعت و $m دقیقه" else "$m دقیقه"
}

@Composable
private fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MjaCard)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(46.dp).clip(RoundedCornerShape(13.dp)).background(MjaElevated),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = MjaBrand, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary)
            Spacer(Modifier.height(3.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary)
        }
        Icon(Icons_Chevron, contentDescription = null, tint = MjaTextDisabled, modifier = Modifier.size(18.dp))
    }
}
