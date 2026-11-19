package com.mja.messenger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.Repo
import com.mja.messenger.net.LocalNet
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 24 — Voice Call (fullscreen)
 */
@Composable
fun VoiceCallScreen(userId: String, onBack: () -> Unit) {
    var attempt by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf("connecting") } // connecting | connected | failed
    var seconds by remember { mutableStateOf(0) }
    var speakerOn by remember { mutableStateOf(false) }
    var muted by remember { mutableStateOf(false) }

    val user = Repo.user(userId)
    val peerReachable = user?.online == true || LocalNet.hasRealPeer()

    // Connection state machine
    LaunchedEffect(attempt) {
        phase = "connecting"
        seconds = 0
        delay(3000)
        phase = if (peerReachable) "connected" else "failed"
    }

    // Call timer
    LaunchedEffect(phase) {
        if (phase == "connected") {
            while (phase == "connected") {
                delay(1000)
                seconds++
            }
        }
    }

    Box(Modifier.fillMaxSize().background(MjaSplash)) {
        Column(
            modifier = Modifier.fillMaxSize().mjaInsets().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(36.dp))

            // Avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                Avatar(
                    user?.name ?: "تماس",
                    size = 130.dp,
                    colorIndex = user?.avatarColor ?: 0,
                    online = if (phase == "connected") true else null,
                )
                if (phase == "connected") {
                    OnlineDot(size = 18.dp, online = true)
                }
            }
            Spacer(Modifier.height(18.dp))

            Text(
                user?.name ?: "تماس صوتی",
                style = MaterialTheme.typography.headlineSmall,
                color = MjaTextPrimary,
            )
            Spacer(Modifier.height(6.dp))

            // Status
            when (phase) {
                "connecting" -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = MjaBrand, strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("در حال تماس...", style = MaterialTheme.typography.bodyMedium, color = MjaTextSecondary)
                    }
                }
                "connected" -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OnlineDot(size = 8.dp, online = true)
                        Spacer(Modifier.width(8.dp))
                        Text("متصل", style = MaterialTheme.typography.bodyMedium, color = MjaSuccess)
                        Spacer(Modifier.width(10.dp))
                        LtrText(
                            String.format(java.util.Locale.US, "%02d:%02d", seconds / 60, seconds % 60),
                            style = MaterialTheme.typography.titleMedium,
                            color = MjaTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                "failed" -> {
                    Text("تماس برقرار نشد", style = MaterialTheme.typography.bodyMedium, color = MjaError)
                }
            }

            Spacer(Modifier.height(28.dp))

            // Failure card
            if (phase == "failed") {
                MjaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(Icons_WifiOff, contentDescription = null, tint = MjaWarning, modifier = Modifier.size(34.dp))
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "گیرنده در شبکه محلی در دسترس نیست.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MjaTextPrimary,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "مطمئن شوید هر دو دستگاه به یک شبکه Hotspot متصل هستند.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MjaTextSecondary,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(14.dp))
                        MjaButton(
                            text = "تلاش مجدد",
                            onClick = { attempt++ },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.weight(1f))

            // Permission banner
            if (phase == "connected" && muted) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MjaCard)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons_MicOff, contentDescription = null, tint = MjaWarning, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "میکروفون قطع است؛ طرف مقابل صدای شما را نمی‌شنود.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MjaTextSecondary,
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CallControl(
                    icon = Icons_Volume,
                    label = "بلندگو",
                    active = speakerOn,
                    enabled = phase == "connected",
                ) { speakerOn = !speakerOn }

                // End call (always active)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MjaError)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons_CallEnd, contentDescription = "پایان تماس", tint = MjaTextPrimary, modifier = Modifier.size(32.dp))
                }

                CallControl(
                    icon = if (muted) Icons_MicOff else Icons_Mic,
                    label = if (muted) "قطع میکروفون" else "میکروفون",
                    active = muted,
                    enabled = phase == "connected",
                ) { muted = !muted }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CallControl(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (active) MjaElevated else MjaCard)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (!enabled) MjaTextDisabled else if (active) MjaBrand else MjaTextSecondary,
            modifier = Modifier.size(26.dp),
        )
        Spacer(Modifier.height(5.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (!enabled) MjaTextDisabled else if (active) MjaBrand else MjaTextSecondary,
        )
    }
}
