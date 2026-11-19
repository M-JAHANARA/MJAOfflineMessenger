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
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.Repo
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 37 — Notification Settings
 */
@Composable
fun NotificationSettingsScreen(onBack: () -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var quietOpen by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(400); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2000); toast = null } }

    val settings by Repo.settings.collectAsState()
    var quietHours by remember { mutableStateOf("22:00 - 07:00") }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        when {
            loading -> Column(Modifier.fillMaxSize()) {
                ScreenHeader(title = "تنظیمات اعلان‌ها", onBack = onBack)
                SkeletonList(count = 7, showAvatar = false)
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                ScreenHeader(title = "تنظیمات اعلان‌ها", onBack = onBack)

                Column(Modifier.padding(horizontal = 20.dp)) {
                    /* Sample notification preview */
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            MjaLogo(size = 34.dp)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("MJA", style = MaterialTheme.typography.labelSmall, color = MjaTextDisabled)
                                    Spacer(Modifier.weight(1f))
                                    Text("الان", style = MaterialTheme.typography.labelSmall, color = MjaTextDisabled)
                                }
                                Spacer(Modifier.height(3.dp))
                                Text("علی محمدی", style = MaterialTheme.typography.titleSmall, color = MjaTextPrimary)
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    if (settings.preview) "سلام، خوبی؟" else "پیام جدید",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (settings.preview) MjaTextSecondary else MjaTextDisabled,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(18.dp))

                    /* Notifications */
                    SectionHeader("اعلان‌ها")
                    Spacer(Modifier.height(6.dp))
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            SwitchRow(
                                title = "اعلان پیام خصوصی",
                                description = "اعلان برای پیام‌های خصوصی",
                                icon = Icons_Chat,
                                checked = settings.notifyPrivate,
                                onCheckedChange = { v -> Repo.updateSettings { it.copy(notifyPrivate = v) } },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SwitchRow(
                                title = "اعلان گروه‌ها",
                                description = "اعلان برای پیام‌های گروهی",
                                icon = Icons_People,
                                checked = settings.notifyGroups,
                                onCheckedChange = { v -> Repo.updateSettings { it.copy(notifyGroups = v) } },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SwitchRow(
                                title = "اعلان فایل",
                                description = "اعلان هنگام دریافت فایل",
                                icon = Icons_FileDoc,
                                checked = settings.notifyFiles,
                                onCheckedChange = { v -> Repo.updateSettings { it.copy(notifyFiles = v) } },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SwitchRow(
                                title = "اعلان تماس",
                                description = "اعلان هنگام تماس صوتی/تصویری",
                                icon = Icons_Call,
                                checked = settings.notifyCalls,
                                onCheckedChange = { v -> Repo.updateSettings { it.copy(notifyCalls = v) } },
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))

                    /* Sound & vibration */
                    SectionHeader("صدا و لرزش")
                    Spacer(Modifier.height(6.dp))
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            SwitchRow(
                                title = "صدای اعلان",
                                icon = Icons_Signal,
                                checked = settings.sound,
                                onCheckedChange = { v -> Repo.updateSettings { it.copy(sound = v) } },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SwitchRow(
                                title = "لرزش",
                                icon = Icons_Device,
                                checked = settings.vibration,
                                onCheckedChange = { v -> Repo.updateSettings { it.copy(vibration = v) } },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SwitchRow(
                                title = "حالت بی‌صدا",
                                description = "اعلان‌ها بدون صدا نمایش داده می‌شوند",
                                icon = Icons_MicOff,
                                checked = settings.silentMode,
                                onCheckedChange = { v -> Repo.updateSettings { it.copy(silentMode = v) } },
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))

                    /* Content */
                    SectionHeader("محتوا")
                    Spacer(Modifier.height(6.dp))
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            SwitchRow(
                                title = "پیش‌نمایش پیام",
                                description = "نمایش متن پیام در اعلان‌ها",
                                icon = Icons_TextSnippet,
                                checked = settings.preview,
                                onCheckedChange = { v -> Repo.updateSettings { it.copy(preview = v) } },
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))

                    /* Schedule */
                    SectionHeader("زمان‌بندی")
                    Spacer(Modifier.height(6.dp))
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        SettingsRow(
                            title = "ساعت سکوت",
                            subtitle = "اعلان‌ها در این بازه نمایش داده نمی‌شوند",
                            icon = Icons_Schedule,
                            value = quietHours,
                            onClick = { quietOpen = true },
                        )
                    }
                    Spacer(Modifier.height(28.dp))
                }
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }

        if (quietOpen) {
            MjaBottomSheet(onDismiss = { quietOpen = false }, title = "ساعت سکوت") {
                listOf("22:00 - 07:00", "23:00 - 06:00", "00:00 - 08:00", "غیرفعال").forEach { option ->
                    SettingsRow(
                        title = option,
                        icon = Icons_Schedule,
                        onClick = {
                            quietHours = option
                            quietOpen = false
                            toast = if (option == "غیرفعال") "ساعت سکوت غیرفعال شد" else "ساعت سکوت تنظیم شد"
                        },
                        trailing = {
                            if (quietHours == option) Icon(Icons_Check, contentDescription = "انتخاب‌شده", tint = MjaBrand)
                        },
                    )
                }
            }
        }
    }
}
