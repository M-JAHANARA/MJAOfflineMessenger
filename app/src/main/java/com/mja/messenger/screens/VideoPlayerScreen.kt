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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private const val DURATION_S = 142

/**
 * 21 — Video Player (fullscreen)
 */
@Composable
fun VideoPlayerScreen(onBack: () -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var playing by remember { mutableStateOf(false) }
    var position by remember { mutableStateOf(0f) }
    var failed by remember { mutableStateOf(false) }
    var buffering by remember { mutableStateOf(false) }
    var volumeOn by remember { mutableStateOf(true) }
    var fullscreen by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(700); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(1800); toast = null } }

    // Playback ticker
    LaunchedEffect(playing) {
        if (playing) {
            while (playing && position < DURATION_S) {
                delay(250)
                position = (position + 0.25f).coerceAtMost(DURATION_S.toFloat())
                if (position >= DURATION_S * 0.6f && position < DURATION_S * 0.6f + 0.3f) {
                    buffering = true
                    delay(800)
                    buffering = false
                }
            }
            if (position >= DURATION_S) {
                playing = false
                failed = true
            }
        }
    }

    Box(Modifier.fillMaxSize().background(MjaSplash)) {
        Column(Modifier.fillMaxSize().mjaInsets()) {

            /* Header */
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MjaIconButton(icon = Icons_Back, onClick = onBack, contentDescription = "بازگشت", tint = MjaTextPrimary)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    LtrText("ویدئو_جلسه.mp4", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Medium)
                    Text("پارسا کریمی · دیروز 21:40", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                }
            }

            /* Video area */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Brush.linearGradient(listOf(MjaBackground2, MjaElevated))),
                contentAlignment = Alignment.Center,
            ) {
                if (loading) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MjaBrand, strokeWidth = 3.dp)
                        Spacer(Modifier.height(10.dp))
                        Text("در حال بارگذاری ویدئو...", style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary)
                    }
                } else if (failed) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons_ErrorOutline, contentDescription = null, tint = MjaError, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(10.dp))
                        Text("ویدئو قابل پخش نیست.", style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary)
                        Spacer(Modifier.height(12.dp))
                        MjaButton(
                            text = "تلاش مجدد",
                            style = MjaButtonStyle.Secondary,
                            onClick = { failed = false; playing = true; position = 0f },
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MjaBrand.copy(alpha = 0.92f))
                            .clickable { playing = !playing },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (playing) Icons_Pause else Icons_Play,
                            contentDescription = if (playing) "توقف" else "پخش",
                            tint = MjaBackground,
                            modifier = Modifier.size(34.dp),
                        )
                    }
                }

                // Buffering overlay
                if (buffering && !loading && !failed) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MjaSplash.copy(alpha = 0.7f))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(color = MjaBrand, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("در حال بافر...", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }
                }
            }

            /* Info + controls */
            Column(Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "ویدئوی جلسه تیم پروژه",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                    )
                    StatusPill(
                        if (playing) "در حال پخش" else if (failed) "خطا" else "متوقف",
                        color = if (playing) MjaSuccess else if (failed) MjaError else MjaTextSecondary,
                    )
                }
                Spacer(Modifier.height(14.dp))

                // Seek slider
                Slider(
                    value = position,
                    onValueChange = { if (!failed) position = it },
                    colors = SliderDefaults.colors(
                        thumbColor = MjaBrand,
                        activeTrackColor = MjaBrand,
                        inactiveTrackColor = MjaElevated,
                    ),
                    enabled = !loading && !failed,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    LtrText(formatDuration((position * 1000).toLong()), style = MaterialTheme.typography.labelSmall, color = MjaTextSecondary)
                    LtrText(formatDuration(DURATION_S * 1000L), style = MaterialTheme.typography.labelSmall, color = MjaTextDisabled)
                }

                Spacer(Modifier.height(12.dp))

                // Transport controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ControlButton(icon = Icons_History, label = "10 ثانیه عقب") {
                        position = (position - 10f).coerceAtLeast(0f)
                    }
                    ControlButton(
                        icon = if (playing) Icons_Pause else Icons_Play,
                        label = if (playing) "توقف" else "پخش",
                        primary = true,
                    ) { if (!failed) playing = !playing }
                    ControlButton(icon = Icons_Schedule, label = "10 ثانیه جلو") {
                        position = (position + 10f).coerceAtMost(DURATION_S.toFloat())
                    }
                    ControlButton(
                        icon = if (volumeOn) Icons_Volume else Icons_MicOff,
                        label = if (volumeOn) "صدا" else "بی‌صدا",
                        active = volumeOn,
                    ) { volumeOn = !volumeOn }
                    ControlButton(
                        icon = Icons_Touch,
                        label = "تمام‌صفحه",
                        active = fullscreen,
                    ) { fullscreen = !fullscreen; toast = if (fullscreen) "حالت تمام‌صفحه" else "حالت عادی" }
                }

                if (failed) {
                    Spacer(Modifier.height(16.dp))
                    ErrorState(
                        title = "ویدئو قابل پخش نیست.",
                        description = "فایل ممکن است خراب یا حذف شده باشد.",
                        onAction = { failed = false; position = 0f; playing = false },
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }
    }
}

@Composable
private fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    primary: Boolean = false,
    active: Boolean = false,
    onClick: () -> Unit,
) {    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(if (primary) MjaBrand else if (active) MjaElevated else MjaCard).padding(horizontal = 12.dp, vertical = 8.dp).clickable(onClick = onClick),
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (primary) MjaBackground else if (active) MjaBrand else MjaTextSecondary,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.height(3.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (primary) MjaBackground else if (active) MjaBrand else MjaTextSecondary,
        )
    }
}