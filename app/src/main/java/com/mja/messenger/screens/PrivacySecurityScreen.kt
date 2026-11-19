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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.Repo
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 34 — Privacy & Security
 */
@Composable
fun PrivacySecurityScreen(onBack: () -> Unit, onOpen: (String) -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var passwordSheet by remember { mutableStateOf(false) }
    var sessionsOpen by remember { mutableStateOf(false) }
    var keysOpen by remember { mutableStateOf(false) }
    var trustedOpen by remember { mutableStateOf(false) }
    var deleteHistory by remember { mutableStateOf(false) }
    var clearTemp by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    // Password sheet fields
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var passError by remember { mutableStateOf<String?>(null) }
    var passSaved by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { delay(400); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2200); toast = null } }
    LaunchedEffect(passSaved) {
        if (passSaved) {
            delay(900)
            passwordSheet = false
            passSaved = false
            oldPass = ""; newPass = ""; confirmPass = ""
            toast = "رمز عبور تغییر کرد"
        }
    }

    val settings by Repo.settings.collectAsState()
    val stats = remember { Repo.storageStats() }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        when {
            loading -> Column(Modifier.fillMaxSize()) {
                ScreenHeader(title = "حریم خصوصی و امنیت", onBack = onBack)
                SkeletonList(count = 7, showAvatar = false)
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                ScreenHeader(title = "حریم خصوصی و امنیت", onBack = onBack)

                Column(Modifier.padding(horizontal = 20.dp)) {
                    /* Account security */
                    SectionHeader("امنیت حساب")
                    Spacer(Modifier.height(6.dp))
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            SettingsRow(
                                title = "تغییر رمز",
                                subtitle = "رمز عبور فعلی را تغییر دهید",
                                icon = Icons_Key,
                                onClick = { passwordSheet = true },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SettingsRow(
                                title = "مدیریت نشست‌ها",
                                subtitle = "۲ دستگاه فعال",
                                icon = Icons_Device,
                                onClick = { sessionsOpen = true },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SwitchRow(
                                title = "قفل برنامه",
                                description = "با باز شدن اپ، رمز ورود بخواه",
                                icon = Icons_Security,
                                checked = settings.appLock,
                                onCheckedChange = { v -> Repo.updateSettings { it.copy(appLock = v) } },
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))

                    /* Message security */
                    SectionHeader("امنیت پیام")
                    Spacer(Modifier.height(6.dp))
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            SwitchRow(
                                title = "رمزنگاری پیام‌ها",
                                description = "پیام‌ها فقط در شبکه محلی منتقل می‌شوند",
                                icon = Icons_Lock,
                                checked = settings.encryption,
                                onCheckedChange = { v -> Repo.updateSettings { it.copy(encryption = v) } },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SettingsRow(
                                title = "مدیریت کلیدها",
                                subtitle = "کلید رمزنگاری این دستگاه",
                                icon = Icons_Key,
                                onClick = { keysOpen = true },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SettingsRow(
                                title = "دستگاه‌های مورد اعتماد",
                                value = "۲",
                                icon = Icons_Shield,
                                onClick = { trustedOpen = true },
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))

                    /* Privacy */
                    SectionHeader("حریم خصوصی")
                    Spacer(Modifier.height(6.dp))
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            SwitchRow(
                                title = "نمایش آنلاین بودن",
                                icon = Icons_Person,
                                checked = settings.showOnline,
                                onCheckedChange = { v -> Repo.updateSettings { it.copy(showOnline = v) } },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SwitchRow(
                                title = "نمایش آخرین حضور",
                                icon = Icons_Schedule,
                                checked = settings.showLastSeen,
                                onCheckedChange = { v -> Repo.updateSettings { it.copy(showLastSeen = v) } },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SwitchRow(
                                title = "درخواست ارتباط",
                                description = "اجازه درخواست گفتگو از کاربران جدید",
                                icon = Icons_AddPerson,
                                checked = settings.allowContactRequests,
                                onCheckedChange = { v -> Repo.updateSettings { it.copy(allowContactRequests = v) } },
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))

                    /* Data */
                    SectionHeader("داده‌ها")
                    Spacer(Modifier.height(6.dp))
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            SettingsRow(
                                title = "حذف تاریخچه",
                                subtitle = "همه پیام‌ها و رسانه‌ها پاک شوند",
                                icon = Icons_Trash,
                                danger = true,
                                onClick = { deleteHistory = true },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SettingsRow(
                                title = "پاک‌سازی موقت",
                                subtitle = "استفاده فعلی: ${com.mja.messenger.data.humanSize(stats.temp)}",
                                icon = Icons_Delete,
                                onClick = { clearTemp = true },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SwitchRow(
                                title = "پشتیبان‌گیری محلی",
                                description = "ذخیره نسخه پشتیبان روی حافظه دستگاه",
                                icon = Icons_Backup,
                                checked = settings.localBackup,
                                onCheckedChange = { v -> Repo.updateSettings { it.copy(localBackup = v) } },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SettingsRow(
                                title = "مسیر پشتیبان",
                                value = "/MJA/Backup",
                                icon = Icons_Folder,
                                onClick = { toast = "مسیر پشتیبان محلی است" },
                            )
                        }
                    }
                    Spacer(Modifier.height(28.dp))
                }
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }

        /* ----- Change password sheet ----- */
        if (passwordSheet) {
            MjaBottomSheet(onDismiss = { passwordSheet = false }, title = "تغییر رمز عبور") {
                MjaTextField(
                    value = oldPass,
                    onValueChange = { oldPass = it; passError = null },
                    label = "رمز فعلی",
                    isPassword = true,
                    leadingIcon = Icons_Key,
                    minHeight = 48.dp,
                )
                Spacer(Modifier.height(10.dp))
                MjaTextField(
                    value = newPass,
                    onValueChange = { newPass = it; passError = null },
                    label = "رمز جدید",
                    isPassword = true,
                    leadingIcon = Icons_Key,
                    isError = passError != null,
                    minHeight = 48.dp,
                )
                Spacer(Modifier.height(10.dp))
                MjaTextField(
                    value = confirmPass,
                    onValueChange = { confirmPass = it; passError = null },
                    label = "تکرار رمز جدید",
                    isPassword = true,
                    leadingIcon = Icons_Key,
                    isError = passError != null,
                    supportingText = passError,
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        when {
                            oldPass.isBlank() -> passError = "رمز فعلی را وارد کنید"
                            newPass.length < 4 -> passError = "رمز جدید حداقل ۴ نویسه باشد"
                            newPass != confirmPass -> passError = "رمزها یکسان نیستند"
                            else -> passSaved = true
                        }
                    },
                    minHeight = 48.dp,
                )
                Spacer(Modifier.height(14.dp))
                MjaButton(
                    text = if (passSaved) "ذخیره شد ✓" else "ذخیره رمز جدید",
                    onClick = {
                        when {
                            oldPass.isBlank() -> passError = "رمز فعلی را وارد کنید"
                            newPass.length < 4 -> passError = "رمز جدید حداقل ۴ نویسه باشد"
                            newPass != confirmPass -> passError = "رمزها یکسان نیستند"
                            else -> passSaved = true
                        }
                    },
                    enabled = !passSaved,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        /* ----- Sessions sheet ----- */
        if (sessionsOpen) {
            MjaBottomSheet(onDismiss = { sessionsOpen = false }, title = "مدیریت نشست‌ها") {
                listOf(
                    Triple("این دستگاه", "MJA Device · فعال", true),
                    Triple("Galaxy S23", "192.168.43.12 · 2 ساعت پیش", false),
                ).forEach { (name, sub, current) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(MjaElevated),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons_Device, contentDescription = null, tint = if (current) MjaBrand else MjaTextSecondary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(name, style = MaterialTheme.typography.titleSmall, color = MjaTextPrimary)
                            Spacer(Modifier.height(2.dp))
                            Text(sub, style = MaterialTheme.typography.labelSmall, color = MjaTextSecondary)
                        }
                        if (current) {
                            StatusPill("این دستگاه", color = MjaSuccess)
                        } else {
                            TextButton(onClick = { toast = "نشست پایان یافت" }) {
                                Text("پایان", style = MaterialTheme.typography.labelLarge, color = MjaError)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                MjaButton(
                    text = "پایان همه نشست‌ها",
                    style = MjaButtonStyle.Destructive,
                    onClick = { sessionsOpen = false; toast = "همه نشست‌ها پایان یافت" },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        /* ----- Keys sheet ----- */
        if (keysOpen) {
            MjaBottomSheet(onDismiss = { keysOpen = false }, title = "مدیریت کلیدها") {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MjaElevated).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons_Key, contentDescription = null, tint = MjaBrand, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("کلید فعال", style = MaterialTheme.typography.titleSmall, color = MjaTextPrimary)
                        Spacer(Modifier.height(3.dp))
                        LtrText("MJA-KEY-8F2A-91C4-77DE", style = MaterialTheme.typography.labelSmall, color = MjaTextDisabled)
                    }
                }
                Spacer(Modifier.height(12.dp))
                MjaButton(
                    text = "کلید جدید",
                    style = MjaButtonStyle.Secondary,
                    icon = Icons_Sync,
                    onClick = { keysOpen = false; toast = "کلید جدید تولید شد" },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        /* ----- Trusted devices sheet ----- */
        if (trustedOpen) {
            MjaBottomSheet(onDismiss = { trustedOpen = false }, title = "دستگاه‌های مورد اعتماد") {
                listOf("Galaxy S23", "Redmi Note 12").forEach { name ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons_CheckCircle, contentDescription = null, tint = MjaSuccess, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(name, style = MaterialTheme.typography.bodyMedium, color = MjaTextPrimary, modifier = Modifier.weight(1f))
                        TextButton(onClick = { toast = "دستگاه حذف شد" }) {
                            Text("حذف", style = MaterialTheme.typography.labelLarge, color = MjaError)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        if (deleteHistory) {
            MjaDialog(
                title = "حذف تاریخچه؟",
                message = "همه پیام‌ها، رسانه‌ها و فایل‌ها از دستگاه حذف می‌شوند.",
                destructive = true,
                icon = Icons_Trash,
                confirmText = "حذف کامل",
                onDismiss = { deleteHistory = false },
                onConfirm = {
                    val freed = Repo.clearAllMedia()
                    deleteHistory = false
                    toast = "${com.mja.messenger.data.humanSize(freed)} آزاد شد"
                },
            )
        }

        if (clearTemp) {
            MjaDialog(
                title = "پاک‌سازی موقت؟",
                message = "فایل‌های موقت حذف می‌شوند.",
                destructive = true,
                icon = Icons_Delete,
                onDismiss = { clearTemp = false },
                onConfirm = {
                    val freed = Repo.clearTemp()
                    clearTemp = false
                    toast = "${com.mja.messenger.data.humanSize(freed)} آزاد شد"
                },
            )
        }
    }
}
