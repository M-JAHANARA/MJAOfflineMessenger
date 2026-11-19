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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.Repo
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Register — spec 03
 */
@Composable
fun RegisterScreen(onBack: () -> Unit, onDone: () -> Unit) {
    var display by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var submitting by remember { mutableStateOf(false) }
    var avatarPick by remember { mutableStateOf(false) }

    LaunchedEffect(submitting) {
        if (submitting) {
            delay(500)
            val err = Repo.register(display, username, pass, pass2)
            submitting = false
            if (err == null) onDone() else error = err
        }
    }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            ScreenHeader(title = "ساخت حساب جدید", onBack = onBack)

            Column(Modifier.padding(horizontal = 24.dp)) {
                Spacer(Modifier.height(8.dp))

                // Avatar with camera overlay
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Avatar(display.ifBlank { "کاربر MJA" }, size = 96.dp)
                    Box(
                        modifier = Modifier
                            .offset(y = 10.dp)
                            .size(32.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MjaBrand)
                            .clickable { avatarPick = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons_Camera, contentDescription = "انتخاب تصویر", tint = MjaBackground, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(Modifier.height(26.dp))

                MjaTextField(
                    value = display,
                    onValueChange = { display = it; error = null },
                    label = "نام نمایشی",
                    placeholder = "مثلاً: محمد احمدی",
                    leadingIcon = Icons_Person,
                    imeAction = ImeAction.Next,
                    isError = error != null && display.isBlank(),
                )
                Spacer(Modifier.height(12.dp))
                MjaTextField(
                    value = username,
                    onValueChange = { username = it; error = null },
                    label = "نام کاربری",
                    placeholder = "نام کاربری منحصربه‌فرد",
                    leadingIcon = Icons_Qr,
                    imeAction = ImeAction.Next,
                )
                Spacer(Modifier.height(12.dp))
                MjaTextField(
                    value = pass,
                    onValueChange = { pass = it; error = null },
                    label = "رمز عبور",
                    placeholder = "حداقل ۴ نویسه",
                    leadingIcon = Icons_Key,
                    isPassword = !showPass,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                    supportingText = "رمز عبور باید حداقل ۴ نویسه باشد",
                    trailingIcon = {
                        MjaIconButton(
                            icon = if (showPass) Icons_VisibilityOff else Icons_Visibility,
                            onClick = { showPass = !showPass },
                            contentDescription = "نمایش رمز",
                            size = 32.dp,
                            iconSize = 18.dp,
                        )
                    },
                )
                Spacer(Modifier.height(12.dp))
                MjaTextField(
                    value = pass2,
                    onValueChange = { pass2 = it; error = null },
                    label = "تکرار رمز عبور",
                    placeholder = "دوباره رمز را وارد کنید",
                    leadingIcon = Icons_Key,
                    isPassword = !showPass,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    onImeAction = { if (!submitting) submitting = true },
                    isError = error != null,
                    supportingText = error,
                )

                Spacer(Modifier.height(24.dp))
                MjaButton(
                    text = if (submitting) "در حال ثبت..." else "ثبت‌نام",
                    onClick = { submitting = true },
                    loading = submitting,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                MjaButton(
                    text = "بازگشت به ورود",
                    style = MjaButtonStyle.Text,
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(32.dp))
            }
        }

        if (avatarPick) {
            MjaBottomSheet(onDismiss = { avatarPick = false }, title = "انتخاب تصویر پروفایل") {
                SettingsRow(title = "گالری", icon = Icons_Gallery, onClick = { avatarPick = false })
                SettingsRow(title = "دوربین", icon = Icons_Camera, onClick = { avatarPick = false })
            }
        }
    }
}
