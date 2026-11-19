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
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.*
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 18 — Edit Profile
 */
@Composable
fun EditProfileScreen(onBack: () -> Unit, onSaved: () -> Unit) {
    val users by Repo.users.collectAsState()
    val me = remember(users) { users.firstOrNull { it.isSelf } ?: Repo.currentUser() }

    var display by remember { mutableStateOf(me.name) }
    var username by remember { mutableStateOf(me.username) }
    var bio by remember { mutableStateOf(me.bio) }
    var status by remember { mutableStateOf(me.lastSeen) }
    var saving by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var avatarPick by remember { mutableStateOf(false) }
    var discard by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    val dirty = display != me.name || username != me.username || bio != me.bio || status != me.lastSeen

    LaunchedEffect(saving) {
        if (saving) {
            delay(600)
            Repo.updateProfile(display.trim(), username.trim(), bio.trim(), status)
            saving = false
            toast = "تغییرات ذخیره شد"
            delay(900)
            onSaved()
        }
    }
    LaunchedEffect(toast) { if (toast != null) { delay(1500); toast = null } }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            ScreenHeader(title = "ویرایش پروفایل", onBack = {
                if (dirty) discard = true else onBack()
            })

            Column(Modifier.padding(horizontal = 20.dp)) {
                // Avatar
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp),
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    Avatar(display.ifBlank { "من" }, size = 100.dp, colorIndex = me.avatarColor)
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MjaBrand)
                            .clickable { avatarPick = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons_Camera, contentDescription = "تغییر عکس", tint = MjaBackground, modifier = Modifier.size(15.dp))
                    }
                }
                Spacer(Modifier.height(6.dp))
                TextButton(
                    onClick = { avatarPick = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text("تغییر عکس", color = MjaBrand, style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.height(12.dp))

                MjaTextField(
                    value = display,
                    onValueChange = { display = it; nameError = null },
                    label = "نام نمایشی",
                    leadingIcon = Icons_Person,
                    isError = nameError != null,
                    supportingText = nameError,
                )
                Spacer(Modifier.height(12.dp))
                MjaTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "نام کاربری",
                    leadingIcon = Icons_Qr,
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

                SectionHeader("وضعیت حضور")
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("آنلاین", "آفلاین", "مشغول").forEach { s ->
                        MjaChip(
                            text = s,
                            selected = status == s || (s == "آنلاین" && status == "همین حالا"),
                            onClick = { status = if (s == "آنلاین") "همین حالا" else s },
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))

                MjaButton(
                    text = if (saving) "در حال ذخیره..." else "ذخیره تغییرات",
                    onClick = {
                        when {
                            display.isBlank() -> nameError = "نام نمایشی را وارد کنید"
                            username.isBlank() -> nameError = "نام کاربری را وارد کنید"
                            else -> saving = true
                        }
                    },
                    enabled = !saving,
                    loading = saving,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(28.dp))
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }

        if (avatarPick) {
            MjaBottomSheet(onDismiss = { avatarPick = false }, title = "تغییر عکس پروفایل") {
                SettingsRow(title = "گالری", icon = Icons_Gallery, onClick = { avatarPick = false })
                SettingsRow(title = "دوربین", icon = Icons_Camera, onClick = { avatarPick = false })
                SettingsRow(title = "حذف عکس", icon = Icons_Trash, danger = true, onClick = { avatarPick = false })
            }
        }

        if (discard) {
            MjaDialog(
                title = "تغییرات ذخیره نشده است",
                message = "اگر خارج شوید، تغییرات از بین می‌رود.",
                destructive = true,
                confirmText = "خروج بدون ذخیره",
                cancelText = "ادامه ویرایش",
                onDismiss = { discard = false },
                onConfirm = { discard = false; onBack() },
            )
        }
    }
}
