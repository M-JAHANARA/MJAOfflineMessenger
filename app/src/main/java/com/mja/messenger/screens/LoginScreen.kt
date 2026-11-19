package com.mja.messenger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.Repo
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Login — spec 02
 */
@Composable
fun LoginScreen(onSuccess: () -> Unit, onRegister: () -> Unit, onForgot: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var forgotOpen by remember { mutableStateOf(false) }

    val session by Repo.session.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .mjaInsets()
            .background(MjaBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(56.dp))
        MjaLogo(size = 72.dp)
        Spacer(Modifier.height(14.dp))
        Text("ورود به حساب کاربری", style = MaterialTheme.typography.headlineSmall, color = MjaTextPrimary)
        Spacer(Modifier.height(6.dp))
        Text(
            "خوش آمدید، برای ادامه وارد شوید.",
            style = MaterialTheme.typography.bodyMedium,
            color = MjaTextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))

        MjaTextField(
            value = username,
            onValueChange = { username = it; error = null },
            label = "نام کاربری",
            placeholder = "نام کاربری خود را وارد کنید",
            leadingIcon = Icons_Person,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            isError = error != null,
        )
        Spacer(Modifier.height(14.dp))
        MjaTextField(
            value = password,
            onValueChange = { password = it; error = null },
            label = "رمز عبور",
            placeholder = "رمز عبور",
            leadingIcon = Icons_Key,
            isPassword = !showPass,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            onImeAction = {
                if (!loading) {
                    loading = true
                    error = null
                }
            },
            trailingIcon = {
                MjaIconButton(
                    icon = if (showPass) Icons_VisibilityOff else Icons_Visibility,
                    onClick = { showPass = !showPass },
                    contentDescription = "نمایش رمز",
                    size = 32.dp,
                    iconSize = 18.dp,
                )
            },
            isError = error != null,
            supportingText = error,
        )

        Spacer(Modifier.height(8.dp))
        Text(
            "فراموشی رمز عبور؟",
            style = MaterialTheme.typography.bodySmall,
            color = MjaAccent,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { forgotOpen = true },
        )

        Spacer(Modifier.height(20.dp))
        MjaButton(
            text = if (loading) "در حال ورود..." else "ورود",
            onClick = {
                loading = true
                error = null
            },
            loading = loading,
            modifier = Modifier.fillMaxWidth(),
        )

        // Validation after loading starts
        LaunchedEffect(loading) {
            if (loading) {
                delay(500)
                val ok = Repo.login(username, password)
                loading = false
                if (ok) onSuccess() else error = session.error ?: "ورود ناموفق بود"
            }
        }

        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(color = MjaBorder, modifier = Modifier.weight(1f))
            Text(
                "یا",
                style = MaterialTheme.typography.labelSmall,
                color = MjaTextDisabled,
                modifier = Modifier.padding(horizontal = 12.dp),
            )
            HorizontalDivider(color = MjaBorder, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(18.dp))

        MjaButton(
            text = "ساخت حساب جدید",
            style = MjaButtonStyle.Secondary,
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(32.dp))
    }

    if (forgotOpen) {
        MjaBottomSheet(onDismiss = { forgotOpen = false }, title = "فراموشی رمز عبور") {
            Text(
                "در نسخه آفلاین MJA بازیابی رمز از طریق کد بازیابی محلی انجام می‌شود. کد بازیابی خود را در تنظیمات > امنیت ذخیره کرده‌اید؟",
                style = MaterialTheme.typography.bodyMedium,
                color = MjaTextSecondary,
                modifier = Modifier.padding(bottom = 14.dp),
            )
            MjaButton(
                text = "بازیابی با کد محلی",
                onClick = { forgotOpen = false },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            MjaButton(
                text = "انصراف",
                style = MjaButtonStyle.Text,
                onClick = { forgotOpen = false },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun Modifier.clickableLike(onClick: () -> Unit): Modifier = this.clickable { onClick() }
