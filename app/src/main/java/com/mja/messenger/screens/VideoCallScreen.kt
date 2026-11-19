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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.Repo
import com.mja.messenger.net.LocalNet
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 25 — Video Call (fullscreen)
 */
@Composable
fun VideoCallScreen(userId: String, onBack: () -> Unit) {
    var attempt by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf("connecting") } // connecting | connected | failed | networkLost
    var seconds by remember { mutableStateOf(0) }
    var muted by remember { mutableStateOf(false) }
    var speakerOn by remember { mutableStateOf(false) }
    var frontCamera by remember { mutableStateOf(true) }

    val user = Repo.user(userId)
    val netStatus by LocalNet.status.collectAsState()
    val peerReachable = user?.online == true || LocalNet.hasRealPeer()

    // Connection state machine
    LaunchedEffect(attempt) {
        phase = "connecting"
        seconds = 0
        delay(3000)
        phase = if (peerReachable) "connected" else "failed"
    }

    // Timer + mid-call network monitoring
    LaunchedEffect(phase) {
        if (phase == "connected") {
            while (phase == "connected") {
                delay(1000)
                seconds++
                if (!netStatus.connected && !LocalNet.hasRealPeer()) {
                    phase = "networkLost"
                }
            }
        }
    }

    Box(Modifier.fillMaxSize().background(MjaSplash)) {
        Column(Modifier.fillMaxSize()) {

            /* Remote video area (simulated) */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Brush.linearGradient(listOf(MjaBackground2, MjaElevated))),
                contentAlignment = Alignment.Center,
            ) {
                // Watermark avatar
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Avatar(
                        user?.name ?: "تماس",
                        size = 96.dp,
                        colorIndex = user?.avatarColor ?: 0,
                        online = if (phase == "connected") true else null,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        user?.name ?: "تماس تصویری",
                        style = MaterialTheme.typography.titleLarge,
                        color = MjaTextPrimary,
                    )
                }

                // Self preview card (top-start)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .size(width = 110.dp, height = 150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MjaCard),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons_Person,
                            contentDescription = null,
                            tint = MjaTextDisabled,
                            modifier = Modifier.size(36.dp),
                        )
                        Spacer(Modifier.height(6.dp))
                        StatusPill("شما", color = MjaTextSecondary)
                    }
                }

                // Header overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MjaIconButton(icon = Icons_Back, onClick = onBack, contentDescription = "بازگشت", tint = MjaTextPrimary)
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            user?.name ?: "تماس تصویری",
                            style = MaterialTheme.typography.titleMedium,
                            color = MjaTextPrimary,
                        )
                        when (phase) {
                            "connecting" -> Text("در حال اتصال...", style = MaterialTheme.typography.labelSmall, color = MjaTextSecondary)
                            "connected" -> LtrText(
                                String.format(java.util.Locale.US, "%02d:%02d", seconds / 60, seconds % 60),
                                style = MaterialTheme.typography.labelLarge,
                                color = MjaSuccess,
                                fontWeight = FontWeight.SemiBold,
                            )
                            "failed" -> Text("اتصال ویدئو برقرار نشد", style = MaterialTheme.typography.labelSmall, color = MjaError)
                            else -> Text("اتصال شبکه محلی قطع شد", style = MaterialTheme.typography.labelSmall, color = MjaWarning)
                        }
                    }
                }

                // Failure / network overlays
                if (phase == "failed" || phase == "networkLost") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MjaCard.copy(alpha = 0.96f))
                            .padding(18.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                if (phase == "failed") Icons_VideoOff else Icons_WifiOff,
                                contentDescription = null,
                                tint = if (phase == "failed") MjaError else MjaWarning,
                                modifier = Modifier.size(32.dp),
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                if (phase == "failed") "اتصال ویدئو برقرار نشد" else "اتصال شبکه محلی قطع شد",
                                style = MaterialTheme.typography.titleMedium,
                                color = MjaTextPrimary,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "گیرنده در دسترس نیست؛ اتصال را دوباره برقرار کنید.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MjaTextSecondary,
                            )
                            Spacer(Modifier.height(14.dp))
                            MjaButton(
                                text = "تلاش مجدد",
                                onClick = { attempt++ },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }

                // Connecting spinner
                if (phase == "connecting") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MjaSplash.copy(alpha = 0.6f))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(color = MjaBrand, strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("در حال تماس تصویری...", style = MaterialTheme.typography.labelSmall, color = MjaTextPrimary)
                        }
                    }
                }
            }

            /* Bottom controls */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MjaBackground)
                    .padding(vertical = 26.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                VCallControl(
                    icon = Icons_SwitchCam,
                    label = "تعویض دوربین",
                    enabled = phase == "connected",
                ) { frontCamera = !frontCamera }

                VCallControl(
                    icon = if (muted) Icons_MicOff else Icons_Mic,
                    label = if (muted) "قطع صدا" else "میکروفون",
                    active = muted,
                    enabled = phase == "connected",
                ) { muted = !muted }

                // End
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(MjaError)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons_CallEnd, contentDescription = "پایان تماس", tint = MjaTextPrimary, modifier = Modifier.size(30.dp))
                }

                VCallControl(
                    icon = if (speakerOn) Icons_Volume else Icons_MicOff,
                    label = if (speakerOn) "بلندگو" else "بدون بلندگو",
                    active = speakerOn,
                    enabled = phase == "connected",
                ) { speakerOn = !speakerOn }
            }
        }
    }
}

@Composable
private fun VCallControl(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) MjaElevated else MjaCard)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 14.dp, vertical = 11.dp),
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (!enabled) MjaTextDisabled else if (active) MjaBrand else MjaTextSecondary,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.height(5.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (!enabled) MjaTextDisabled else if (active) MjaBrand else MjaTextSecondary,
        )
    }
}
