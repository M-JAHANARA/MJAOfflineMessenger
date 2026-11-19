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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Welcome / Onboarding — spec 01
 */
@Composable
fun WelcomeScreen(onStart: () -> Unit, onAbout: () -> Unit) {
    var starting by remember { mutableStateOf(false) }

    LaunchedEffect(starting) {
        if (starting) { delay(600); onStart() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .mjaInsets()
            .background(MjaBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(64.dp))

        MjaLogo(size = 96.dp)
        Spacer(Modifier.height(16.dp))
        MjaWordmark("MJA", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(6.dp))
        Text(
            "Welcome to MJA",
            style = MaterialTheme.typography.titleMedium,
            color = MjaTextSecondary,
        )
        Spacer(Modifier.height(24.dp))

        Text(
            "به MJA خوش آمدید",
            style = MaterialTheme.typography.headlineSmall,
            color = MjaTextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "پیام‌رسان مستقل و بدون اینترنت؛ ارتباط شما از طریق شبکه محلی و به‌صورت مستقیم بین دستگاه‌ها برقرار می‌شود.",
            style = MaterialTheme.typography.bodyLarge,
            color = MjaTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
        )

        Spacer(Modifier.height(28.dp))

        // Feature cards
        FeatureCard(
            icon = Icons_WifiOff,
            title = "بدون نیاز به اینترنت",
            desc = "تمام امکانات بدون اتصال به اینترنت جهانی کار می‌کنند.",
        )
        Spacer(Modifier.height(12.dp))
        FeatureCard(
            icon = Icons_Wifi,
            title = "ارتباط از طریق شبکه محلی",
            desc = "پیام‌ها مستقیماً بین دستگاه‌های متصل به یک شبکه منتقل می‌شوند.",
        )
        Spacer(Modifier.height(12.dp))
        FeatureCard(
            icon = Icons_Security,
            title = "حفظ حریم خصوصی",
            desc = "داده‌های شما روی دستگاه خودتان می‌مانند و به سرور ارسال نمی‌شوند.",
        )

        Spacer(Modifier.height(36.dp))

        MjaButton(
            text = "شروع",
            onClick = { starting = true },
            loading = starting,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))
        MjaButton(
            text = "درباره برنامه",
            style = MjaButtonStyle.Text,
            onClick = onAbout,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun FeatureCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MjaCard)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(MjaElevated),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = MjaBrand, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary)
            Spacer(Modifier.height(3.dp))
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary, lineHeight = MaterialTheme.typography.bodySmall.lineHeight)
        }
    }
}
