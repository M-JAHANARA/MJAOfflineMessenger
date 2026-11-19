package com.mja.messenger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.mja.messenger.data.Repo
import com.mja.messenger.navigation.Routes
import com.mja.messenger.ui.components.MjaLogo
import com.mja.messenger.ui.components.MjaWordmark
import com.mja.messenger.ui.components.mjaInsets
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 01 — Splash Screen
 * Pure black, silver MJA mark, wordmark, tagline, thin loader at the bottom.
 */
@Composable
fun SplashScreen(onNavigate: (String) -> Unit) {
    LaunchedEffect(Unit) {
        delay(1900)
        val session = Repo.session.value
        val target = when {
            !session.onboarded -> Routes.WELCOME
            !session.loggedIn -> Routes.LOGIN
            else -> Routes.HOME
        }
        onNavigate(target)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .mjaInsets()
            .background(MjaSplash),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-24).dp),
        ) {
            MjaLogo(size = 108.dp)
            Spacer(Modifier.height(22.dp))
            MjaWordmark(
                text = "MJA",
                style = MaterialTheme.typography.displaySmall,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Offline Messenger",
                style = MaterialTheme.typography.titleMedium,
                color = MjaTextSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Chat Without Internet.",
                style = MaterialTheme.typography.bodySmall,
                color = MjaTextDisabled,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Private. Local. Secure.",
                style = MaterialTheme.typography.labelSmall,
                color = MjaTextDisabled.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
            )
        }

        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 54.dp)
                .size(26.dp),
            color = MjaBrand,
            strokeWidth = 2.5.dp,
        )
    }
}
