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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.Repo
import com.mja.messenger.navigation.Routes
import com.mja.messenger.ui.MainScaffold
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 33 — Settings (tab 4)
 */
@Composable
fun SettingsScreen(
    onBack: (() -> Unit)? = null,
    onOpen: (String) -> Unit,
    onLogout: () -> Unit,
    tab: (Int) -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var logoutConfirm by remember { mutableStateOf(false) }
    var clearConfirm by remember { mutableStateOf(false) }
    var languageOpen by remember { mutableStateOf(false) }
    var textSizeOpen by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(500); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2200); toast = null } }

    val settings by Repo.settings.collectAsState()
    val chats by Repo.chats.collectAsState()
    val unread = chats.sumOf { it.unread }
    val me = Repo.currentUser()

    MainScaffold(current = 4, onTab = tab, unreadCount = unread) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            if (loading) {
                Column(Modifier.fillMaxSize()) {
                    ScreenHeader(title = "تنظیمات")
                    SkeletonList(count = 7, showAvatar = false)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    ScreenHeader(title = "تنظیمات")

                    Column(Modifier.padding(horizontal = 20.dp)) {
                        // Profile card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MjaCard)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Avatar(me.name, size = 56.dp, colorIndex = me.avatarColor, online = true)
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(me.name, style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Spacer(Modifier.height(3.dp))
                                LtrText("@${me.username}", style = MaterialTheme.typography.bodySmall, color = MjaTextDisabled)
                            }
                            MjaButton(
                                text = "ویرایش",
                                style = MjaButtonStyle.Secondary,
                                onClick = { onOpen(Routes.EDIT_PROFILE) },
                            )
                        }
                        Spacer(Modifier.height(18.dp))

                        /* ---------- General ---------- */
                        SectionHeader("عمومی")
                        Spacer(Modifier.height(6.dp))
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                SettingsRow(title = "زبان", icon = Icons_Language, value = settings.language, onClick = { languageOpen = true })
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(title = "تم", icon = Icons_Palette, value = settings.theme, onClick = { onOpen(Routes.APPEARANCE) })
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(title = "اندازه متن", icon = Icons_TextSize, value = settings.textSize, onClick = { textSizeOpen = true })
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SwitchRow(
                                    title = "انیمیشن‌ها",
                                    description = "حرکت‌های نرم رابط کاربری",
                                    icon = Icons_Animations,
                                    checked = settings.animations,
                                    onCheckedChange = { v -> Repo.updateSettings { it.copy(animations = v) } },
                                )
                            }
                        }
                        Spacer(Modifier.height(18.dp))

                        /* ---------- Notifications ---------- */
                        SectionHeader("اعلان‌ها")
                        Spacer(Modifier.height(6.dp))
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                SettingsRow(
                                    title = "اعلان پیام خصوصی",
                                    icon = Icons_Notifications,
                                    value = if (settings.notifyPrivate) "فعال" else "غیرفعال",
                                    onClick = { onOpen(Routes.NOTIF_SETTINGS) },
                                )
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(
                                    title = "صدای اعلان",
                                    icon = Icons_Signal,
                                    value = if (settings.sound) "فعال" else "غیرفعال",
                                    onClick = { onOpen(Routes.NOTIF_SETTINGS) },
                                )
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(
                                    title = "لرزش",
                                    icon = Icons_Device,
                                    value = if (settings.vibration) "فعال" else "غیرفعال",
                                    onClick = { onOpen(Routes.NOTIF_SETTINGS) },
                                )
                            }
                        }
                        Spacer(Modifier.height(18.dp))

                        /* ---------- Network ---------- */
                        SectionHeader("شبکه")
                        Spacer(Modifier.height(6.dp))
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                SettingsRow(title = "تنظیمات شبکه", icon = Icons_Wifi, onClick = { onOpen(Routes.NETWORK) })
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(title = "مدیریت سرور", icon = Icons_Server, onClick = { onOpen(Routes.HOTSPOT) })
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(title = "وضعیت اتصال", icon = Icons_Signal, onClick = { onOpen(Routes.NETWORK) })
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(title = "صف پیام‌های آفلاین", icon = Icons_Queue, onClick = { onOpen(Routes.QUEUE) })
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(title = "همگام‌سازی", icon = Icons_Sync, onClick = { onOpen(Routes.SYNC) })
                            }
                        }
                        Spacer(Modifier.height(18.dp))

                        /* ---------- Security ---------- */
                        SectionHeader("امنیت و حریم خصوصی")
                        Spacer(Modifier.height(6.dp))
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                SettingsRow(title = "امنیت", icon = Icons_Security, onClick = { onOpen(Routes.PRIVACY) })
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(title = "حریم خصوصی", icon = Icons_Key, onClick = { onOpen(Routes.PRIVACY) })
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(title = "ظاهر برنامه", icon = Icons_Palette, onClick = { onOpen(Routes.APPEARANCE) })
                            }
                        }
                        Spacer(Modifier.height(18.dp))

                        /* ---------- Storage ---------- */
                        SectionHeader("حافظه")
                        Spacer(Modifier.height(6.dp))
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                SettingsRow(title = "مدیریت حافظه", icon = Icons_Storage, onClick = { onOpen(Routes.STORAGE) })
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(title = "فایل‌ها", icon = Icons_Folder, onClick = { onOpen(Routes.FILES) })
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(
                                    title = "پاک‌سازی",
                                    subtitle = "حذف داده‌های موقت",
                                    icon = Icons_Trash,
                                    onClick = { clearConfirm = true },
                                )
                            }
                        }
                        Spacer(Modifier.height(18.dp))

                        /* ---------- About ---------- */
                        SectionHeader("درباره")
                        Spacer(Modifier.height(6.dp))
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                SettingsRow(title = "درباره برنامه", icon = Icons_InfoOutlined, onClick = { onOpen(Routes.ABOUT) })
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(title = "نسخه", icon = Icons_Help, value = "1.1.0", onClick = { onOpen(Routes.ABOUT) })
                                HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                SettingsRow(title = "اطلاعات سازنده", icon = Icons_Person, onClick = { onOpen(Routes.ABOUT) })
                            }
                        }
                        Spacer(Modifier.height(24.dp))

                        // Logout
                        MjaButton(
                            text = "خروج از حساب",
                            style = MjaButtonStyle.Destructive,
                            icon = Icons_Logout,
                            onClick = { logoutConfirm = true },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "MJA OFFLINE MESSENGER · CHAT WITHOUT INTERNET.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MjaTextDisabled,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                        Spacer(Modifier.height(32.dp))
                    }
                }
            }

            if (toast != null) {
                Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
            }

            if (languageOpen) {
                MjaBottomSheet(onDismiss = { languageOpen = false }, title = "زبان برنامه") {
                    listOf("فارسی", "English").forEach { lang ->
                        SettingsRow(
                            title = lang,
                            icon = Icons_Language,
                            onClick = {
                                Repo.updateSettings { it.copy(language = lang) }
                                languageOpen = false
                                toast = if (lang == "فارسی") "زبان فارسی انتخاب شد" else "زبان انگلیسی به‌زودی"
                            },
                            trailing = {
                                if (settings.language == lang) Icon(Icons_Check, contentDescription = "انتخاب‌شده", tint = MjaBrand)
                            },
                        )
                    }
                }
            }

            if (textSizeOpen) {
                MjaBottomSheet(onDismiss = { textSizeOpen = false }, title = "اندازه متن") {
                    listOf("کوچک", "پیش‌فرض", "بزرگ").forEach { size ->
                        SettingsRow(
                            title = size,
                            icon = Icons_TextSize,
                            onClick = {
                                Repo.updateSettings { it.copy(textSize = size) }
                                textSizeOpen = false
                                toast = "اندازه متن $size شد"
                            },
                            trailing = {
                                if (settings.textSize == size) Icon(Icons_Check, contentDescription = "انتخاب‌شده", tint = MjaBrand)
                            },
                        )
                    }
                }
            }

            if (clearConfirm) {
                MjaDialog(
                    title = "پاک‌سازی داده‌های موقت؟",
                    message = "فایل‌های موقت حذف می‌شوند و فضای بیشتری آزاد می‌شود.",
                    destructive = true,
                    icon = Icons_Trash,
                    onDismiss = { clearConfirm = false },
                    onConfirm = {
                        val freed = Repo.clearTemp()
                        clearConfirm = false
                        toast = "${com.mja.messenger.data.humanSize(freed)} آزاد شد"
                    },
                )
            }

            if (logoutConfirm) {
                MjaDialog(
                    title = "خروج از حساب؟",
                    message = "برای ورود مجدد باید نام کاربری و رمز عبور را وارد کنید.",
                    destructive = true,
                    icon = Icons_Logout,
                    confirmText = "خروج",
                    onDismiss = { logoutConfirm = false },
                    onConfirm = {
                        logoutConfirm = false
                        Repo.logout()
                        onLogout()
                    },
                )
            }
        }
    }
}
