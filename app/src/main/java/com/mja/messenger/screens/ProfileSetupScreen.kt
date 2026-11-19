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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.Repo
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Profile setup after first register — spec 04
 */
@Composable
fun ProfileSetupScreen(onDone: () -> Unit) {
    val users by Repo.users.collectAsState()
    val me = remember(users) { users.firstOrNull { it.isSelf } ?: Repo.currentUser() }

    var display by remember { mutableStateOf(me.name) }
    var username by remember { mutableStateOf(me.username) }
    var bio by remember { mutableStateOf(me.bio) }
    var status by remember { mutableStateOf("آنلاین") }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var avatarPick by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(saving) {
        if (saving) {
            delay(600)
            if (display.isBlank()) {
                error = "نام نمایشی را وارد کنید"
                saving = false
            } else {
                Repo.updateProfile(display.trim(), username.trim(), bio.trim(), status)
                saving = false
                toast = "پروفایل ذخیره شد"
                delay(800)
                onDone()
            }
        }
    }
    LaunchedEffect(toast) { if (toast != null) { delay(1500); toast = null } }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(40.dp))

            Box(contentAlignment = Alignment.BottomEnd) {
                Avatar(display.ifBlank { "کاربر MJA" }, size = 110.dp)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MjaBrand)
                        .clickable { avatarPick = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons_Camera, contentDescription = "تغییر عکس", tint = MjaBackground, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            TextButton(onClick = { avatarPick = true }) {
                Text("تغییر عکس", color = MjaBrand, style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(8.dp))

            Text("تکمیل پروفایل", style = MaterialTheme.typography.headlineSmall, color = MjaTextPrimary)
            Spacer(Modifier.height(6.dp))
            Text(
                "اطلاعات خود را برای دیگر کاربران شبکه محلی تنظیم کنید.",
                style = MaterialTheme.typography.bodyMedium,
                color = MjaTextSecondary,
            )
            Spacer(Modifier.height(24.dp))

            Column(Modifier.padding(horizontal = 24.dp)) {
                MjaTextField(
                    value = display,
                    onValueChange = { display = it; error = null },
                    label = "نام نمایشی",
                    leadingIcon = Icons_Person,
                    imeAction = ImeAction.Next,
                    isError = error != null && display.isBlank(),
                )
                Spacer(Modifier.height(12.dp))
                MjaTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "نام کاربری",
                    leadingIcon = Icons_Qr,
                    imeAction = ImeAction.Next,
                )
                Spacer(Modifier.height(12.dp))
                MjaTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = "بیوگرافی",
                    placeholder = "چند کلمه درباره خودتان...",
                    singleLine = false,
                    minHeight = 90.dp,
                    leadingIcon = Icons_TextSnippet,
                )
                Spacer(Modifier.height(16.dp))

                Text("وضعیت حضور", style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("آنلاین", "آفلاین", "مشغول").forEach { s ->
                        MjaChip(
                            text = s,
                            selected = status == s,
                            onClick = { status = s },
                            icon = when (s) {
                                "آنلاین" -> Icons_CheckCircle
                                "آفلاین" -> Icons_CloudOff
                                else -> Icons_Schedule
                            },
                        )
                    }
                }
                Spacer(Modifier.height(28.dp))

                MjaButton(
                    text = if (saving) "در حال ذخیره..." else "ذخیره",
                    onClick = { saving = true },
                    loading = saving,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                MjaButton(
                    text = "بعداً تنظیم می‌کنم",
                    style = MjaButtonStyle.Text,
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(32.dp))
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }

        if (avatarPick) {
            MjaBottomSheet(onDismiss = { avatarPick = false }, title = "انتخاب تصویر پروفایل") {
                SettingsRow(title = "گالری", icon = Icons_Gallery, onClick = { avatarPick = false })
                SettingsRow(title = "دوربین", icon = Icons_Camera, onClick = { avatarPick = false })
            }
        }
    }
}
