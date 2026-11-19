package com.mja.messenger.screens

import androidx.compose.foundation.background
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
import com.mja.messenger.data.Repo
import com.mja.messenger.net.LocalNet
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 32 — Host / Server Admin
 */
@Composable
fun HostAdminScreen(
    onBack: () -> Unit,
    onOpenDevices: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var reportOpen by remember { mutableStateOf(false) }
    var limitOpen by remember { mutableStateOf(false) }
    var sessionsOpen by remember { mutableStateOf(false) }
    var stopConfirm by remember { mutableStateOf(false) }
    var connectionLimit by remember { mutableStateOf("نامحدود") }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(500); loading = false; LocalNet.refreshStatus() }
    LaunchedEffect(toast) { if (toast != null) { delay(2200); toast = null } }

    val statusFlow = LocalNet.status
    val status = statusFlow.collectAsState().value
    val peers by LocalNet.peers.collectAsState()
    val stats = remember { Repo.storageStats() }

    val reportLines = remember {
        listOf(
            "21:45  اتصال کاربر ali_mmd",
            "21:46  دریافت پیام از سارا جمشیدی",
            "21:48  ارسال موفق به گروه پروژه",
            "21:52  اتصال کاربر parsa_c",
            "21:55  ارسال فایل گزارش_نهایی.pdf",
            "22:01  قطع اتصال reza_k",
        )
    }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            ScreenHeader(title = "مدیریت میزبان", onBack = onBack) {
                MjaIconButton(icon = Icons_Close, onClick = { stopConfirm = true }, contentDescription = "توقف سرور")
            }

            Column(Modifier.padding(horizontal = 20.dp)) {
                when {
                    loading -> SkeletonList(count = 5, showAvatar = false)

                    else -> {
                        // Two status cards
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AdminStatCard(
                                title = "سرور",
                                value = if (status.hostActive) "فعال" else "غیرفعال",
                                sub = if (status.uptime > 0) "فعالیت: ${status.uptime / 60_000} دقیقه" else "متوقف",
                                ok = status.hostActive,
                                modifier = Modifier.weight(1f),
                            )
                            AdminStatCard(
                                title = "شبکه",
                                value = status.ssid.ifBlank { "—" },
                                sub = "${peers.size} کاربر متصل",
                                ok = status.connected,
                                ltr = true,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Spacer(Modifier.height(14.dp))

                        // Not running warning
                        if (!status.hostActive) {
                            ErrorState(
                                title = "سرور محلی در حال اجرا نیست.",
                                description = "برای پذیرش اتصال کاربران، ابتدا سرور را فعال کنید.",
                                actionText = "شروع سرور",
                                onAction = onOpenSettings,
                            )
                            Spacer(Modifier.height(14.dp))
                        }

                        // Menu
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                SettingsRow(
                                    title = "مدیریت کاربران",
                                    subtitle = "${peers.size} کاربر متصل",
                                    icon = Icons_People,
                                    onClick = onOpenDevices,
                                )
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(
                                    title = "گزارش فعالیت",
                                    subtitle = "${reportLines.size} رویداد امروز",
                                    icon = Icons_Terminal,
                                    onClick = { reportOpen = true },
                                )
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(
                                    title = "محدودیت اتصال",
                                    value = connectionLimit,
                                    icon = Icons_Speed,
                                    onClick = { limitOpen = true },
                                )
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(
                                    title = "مدیریت نشست‌ها",
                                    subtitle = "${peers.size} نشست فعال",
                                    icon = Icons_Device,
                                    onClick = { sessionsOpen = true },
                                )
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(
                                    title = "مدیریت فضای ذخیره‌سازی",
                                    subtitle = "استفاده: ${com.mja.messenger.data.humanSize(stats.total)}",
                                    icon = Icons_Storage,
                                    onClick = onOpenSettings,
                                )
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(
                                    title = "تنظیمات سرور",
                                    icon = Icons_Settings,
                                    onClick = onOpenSettings,
                                )
                            }
                        }
                        Spacer(Modifier.height(14.dp))

                        MjaButton(
                            text = "توقف سرور",
                            style = MjaButtonStyle.Destructive,
                            icon = Icons_Close,
                            onClick = { stopConfirm = true },
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

        // Report sheet
        if (reportOpen) {
            MjaBottomSheet(onDismiss = { reportOpen = false }, title = "گزارش فعالیت") {
                reportLines.forEach { line ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons_Schedule, contentDescription = null, tint = MjaTextDisabled, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(line, style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        // Connection limit sheet
        if (limitOpen) {
            MjaBottomSheet(onDismiss = { limitOpen = false }, title = "محدودیت اتصال") {
                listOf("5", "10", "20", "نامحدود").forEach { option ->
                    SettingsRow(
                        title = if (option == "نامحدود") "نامحدود" else "$option کاربر",
                        icon = Icons_People,
                        onClick = { connectionLimit = option; limitOpen = false; toast = "محدودیت اعمال شد" },
                        trailing = {
                            if (connectionLimit == option) Icon(Icons_Check, contentDescription = "انتخاب‌شده", tint = MjaBrand)
                        },
                    )
                }
            }
        }

        // Sessions sheet
        if (sessionsOpen) {
            MjaBottomSheet(onDismiss = { sessionsOpen = false }, title = "مدیریت نشست‌ها") {
                val sessionUsers = peers.take(4)
                if (sessionUsers.isEmpty()) {
                    Text(
                        "نشست فعالی وجود ندارد.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MjaTextDisabled,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                } else {
                    sessionUsers.forEach { p ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Avatar(p.name, size = 40.dp, colorIndex = p.id.hashCode().let { (it % 8 + 8) % 8 })
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(p.name, style = MaterialTheme.typography.titleSmall, color = MjaTextPrimary)
                                Spacer(Modifier.height(2.dp))
                                LtrText("${p.username} · ${p.ip}", style = MaterialTheme.typography.labelSmall, color = MjaTextDisabled)
                            }
                            TextButton(onClick = { toast = "نشست پایان یافت" }) {
                                Text("پایان نشست", style = MaterialTheme.typography.labelLarge, color = MjaError)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // Stop confirm
        if (stopConfirm) {
            MjaDialog(
                title = "توقف سرور؟",
                message = "اتصال کاربران متصل قطع می‌شود و پذیرش اتصال جدید متوقف خواهد شد.",
                destructive = true,
                icon = Icons_Close,
                confirmText = "توقف سرور",
                onDismiss = { stopConfirm = false },
                onConfirm = {
                    LocalNet.stopHotspot()
                    stopConfirm = false
                    toast = "سرور محلی متوقف شد"
                },
            )
        }
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    value: String,
    sub: String,
    ok: Boolean,
    modifier: Modifier = Modifier,
    ltr: Boolean = false,
) {
    MjaCard(modifier = modifier) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.labelSmall, color = MjaTextDisabled)
                Spacer(Modifier.weight(1f))
                OnlineDot(size = 8.dp, online = ok)
            }
            Spacer(Modifier.height(8.dp))
            if (ltr) {
                LtrText(value, style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary, fontWeight = FontWeight.SemiBold)
            } else {
                Text(value, style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 1)
            }
            Spacer(Modifier.height(4.dp))
            Text(sub, style = MaterialTheme.typography.labelSmall, color = MjaTextSecondary, maxLines = 1)
        }
    }
}
