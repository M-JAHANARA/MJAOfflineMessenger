package com.mja.messenger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.MsgKind
import com.mja.messenger.data.Repo
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val MAX_RECORD_S = 120

/**
 * 26 — Voice Recording (fullscreen)
 */
@Composable
fun VoiceRecordingScreen(chatId: String, onBack: () -> Unit) {
    // idle | recording | ready
    var phase by remember { mutableStateOf("idle") }
    var seconds by remember { mutableStateOf(0) }
    var playing by remember { mutableStateOf(false) }
    var playPos by remember { mutableStateOf(0f) }
    var micDenied by remember { mutableStateOf(false) }
    var amplitudes by remember { mutableStateOf(List(40) { 0.15f }) }

    // Live waveform while recording
    LaunchedEffect(phase) {
        if (phase == "recording") {
            while (phase == "recording" && seconds < MAX_RECORD_S) {
                delay(120)
                amplitudes = List(40) {
                    (0.15f + Random.nextFloat() * 0.8f).coerceAtMost(1f)
                }
            }
        }
    }

    // Timer
    LaunchedEffect(phase) {
        if (phase == "recording") {
            while (phase == "recording" && seconds < MAX_RECORD_S) {
                delay(1000)
                seconds++
            }
            if (seconds >= MAX_RECORD_S) phase = "ready"
        }
    }

    // Preview playback
    LaunchedEffect(playing) {
        if (playing) {
            while (playing && playPos < seconds) {
                delay(200)
                playPos = (playPos + 0.2f).coerceAtMost(seconds.toFloat())
            }
            playing = false
            if (playPos >= seconds) playPos = 0f
        }
    }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaSplash)) {
        Column(Modifier.fillMaxSize()) {

            /* Header */
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MjaIconButton(
                    icon = Icons_Close,
                    onClick = onBack,
                    contentDescription = "بستن",
                    tint = MjaTextPrimary,
                )
                Spacer(Modifier.width(8.dp))
                Text("پیام صوتی", style = MaterialTheme.typography.titleLarge, color = MjaTextPrimary)
                Spacer(Modifier.weight(1f))
                if (phase == "recording") {
                    StatusPill("در حال ضبط...", color = MjaError, filled = true)
                } else if (phase == "ready") {
                    StatusPill("آماده ارسال", color = MjaSuccess, filled = true)
                }
            }

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(28.dp))

                if (micDenied) {
                    ErrorState(
                        title = "دسترسی به میکروفون موجود نیست.",
                        description = "برای ضبط پیام صوتی، مجوز میکروفون را فعال کنید.",
                        actionText = "اصلاح مجوز",
                        onAction = { micDenied = false },
                    )
                    Spacer(Modifier.height(16.dp))
                } else {
                    // Waveform
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MjaCard),
                        contentAlignment = Alignment.Center,
                    ) {
                        Waveform(
                            amplitudes = amplitudes,
                            active = phase == "recording",
                            height = 90.dp,
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                    }
                    Spacer(Modifier.height(18.dp))

                    // Timer
                    Text(
                        String.format(java.util.Locale.US, "%02d:%02d", seconds / 60, seconds % 60),
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (phase == "recording") MjaError else MjaTextPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        when (phase) {
                            "idle" -> "برای شروع ضبط، دکمه را لمس کنید."
                            "recording" -> "در حال ضبط... حداکثر ۲ دقیقه"
                            else -> "پیش‌نمایش آماده است."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MjaTextSecondary,
                    )

                    // Preview playback
                    if (phase == "ready") {
                        Spacer(Modifier.height(20.dp))
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
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(MjaBrand)
                                    .clickable { playing = !playing },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    if (playing) Icons_Pause else Icons_Play,
                                    contentDescription = if (playing) "توقف" else "پخش",
                                    tint = MjaBackground,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    if (playing) "در حال پخش..." else "پیش‌نمایش",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MjaTextPrimary,
                                )
                                Spacer(Modifier.height(8.dp))
                                ProgressBar(
                                    progress = if (seconds > 0) playPos / seconds else 0f,
                                    height = 5.dp,
                                )
                            }
                        }
                    }
                }
            }

            /* Controls */
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (phase) {
                    "idle" -> {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MjaBrand)
                                .clickable {
                                    try {
                                        phase = "recording"
                                        seconds = 0
                                    } catch (_: SecurityException) {
                                        micDenied = true
                                    }
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons_Mic, contentDescription = "شروع ضبط", tint = MjaBackground, modifier = Modifier.size(34.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Text("شروع ضبط", style = MaterialTheme.typography.bodyMedium, color = MjaTextSecondary)
                    }

                    "recording" -> {
                        // Stop
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MjaError)
                                .clickable { phase = "ready"; amplitudes = amplitudes.map { it * 0.6f } },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons_Pause, contentDescription = "توقف ضبط", tint = Color_White, modifier = Modifier.size(32.dp))
                        }
                        Spacer(Modifier.width(24.dp))
                        // Cancel
                        Column(
                            modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(MjaCard).padding(horizontal = 18.dp, vertical = 12.dp).clickable {
                                phase = "idle"
                                seconds = 0
                                amplitudes = List(40) { 0.15f }
                            },
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(Icons_Trash, contentDescription = "لغو", tint = MjaTextSecondary, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.height(4.dp))
                            Text("لغو", style = MaterialTheme.typography.labelSmall, color = MjaTextSecondary)
                        }
                    }

                    "ready" -> {
                        // Delete
                        Column(
                            modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(MjaCard).padding(horizontal = 18.dp, vertical = 12.dp).clickable {
                                phase = "idle"
                                seconds = 0
                                playPos = 0f
                                amplitudes = List(40) { 0.15f }
                            },
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(Icons_Trash, contentDescription = "حذف", tint = MjaError, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.height(4.dp))
                            Text("حذف", style = MaterialTheme.typography.labelSmall, color = MjaError)
                        }
                        Spacer(Modifier.width(20.dp))
                        // Send
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MjaBrand)
                                .clickable {
                                    Repo.sendMedia(
                                        chatId = chatId,
                                        kind = MsgKind.VOICE,
                                        fileName = "پیام صوتی",
                                        durationMs = seconds * 1000L,
                                    )
                                    onBack()
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons_Send, contentDescription = "ارسال", tint = MjaBackground, modifier = Modifier.size(32.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Text("ارسال", style = MaterialTheme.typography.bodyMedium, color = MjaTextSecondary)
                    }
                }
            }
        }
    }
}

private val Color_White = androidx.compose.ui.graphics.Color.White
