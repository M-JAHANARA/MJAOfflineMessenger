package com.mja.messenger.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.Repo
import com.mja.messenger.data.dayLabel
import com.mja.messenger.data.timeLabel
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 28 — Sync
 */
@Composable
fun SyncScreen(onBack: () -> Unit) {
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) { delay(500); loading = false }

    val sync by Repo.sync.collectAsState()
    val queue by Repo.queue.collectAsState()

    val rotation by rememberInfiniteTransition(label = "sync").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "rot",
    )

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            ScreenHeader(title = "همگام‌سازی پیام‌ها", onBack = onBack)

            Column(Modifier.padding(horizontal = 20.dp)) {
                // Status card
                MjaCard(modifier = Modifier.fillMaxWidth(), elevated = true) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier.size(76.dp).clip(RoundedCornerShape(50)).background(MjaElevated),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons_Sync,
                                contentDescription = null,
                                tint = when {
                                    sync.running -> MjaBrand
                                    sync.failed -> MjaError
                                    sync.success -> MjaSuccess
                                    else -> MjaTextSecondary
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .rotate(if (sync.running) rotation else 0f),
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(
                            when {
                                sync.running -> "در حال همگام‌سازی..."
                                sync.failed -> "همگام‌سازی ناموفق"
                                sync.success -> "همگام‌سازی موفق"
                                else -> "در انتظار اتصال"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = when {
                                sync.running -> MjaTextPrimary
                                sync.failed -> MjaError
                                sync.success -> MjaSuccess
                                else -> MjaTextSecondary
                            },
                        )
                        Spacer(Modifier.height(14.dp))
                        ProgressBar(progress = sync.progress)
                        Spacer(Modifier.height(6.dp))
                        LtrText(
                            "${(sync.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MjaTextDisabled,
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Stats
                MjaCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        InfoRow("پیام‌های دریافت‌نشده", "${sync.received}")
                        InfoRow("پیام‌های ارسال‌نشده", "${queue.size}")
                        InfoRow(
                            "زمان آخرین همگام‌سازی",
                            if (sync.lastSync > 0) "${dayLabel(sync.lastSync)} - ${timeLabel(sync.lastSync)}" else "هرگز",
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Failure state
                if (sync.failed) {
                    ErrorState(
                        title = "اتصال به سرور محلی برقرار نشد.",
                        description = "مطمئن شوید به شبکه Hotspot متصل هستید و سرور محلی فعال است.",
                        actionText = "تلاش مجدد",
                        onAction = { Repo.runSync() },
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // Success banner
                if (sync.success && !sync.running) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MjaSuccess.copy(alpha = 0.12f))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons_CheckCircle, contentDescription = null, tint = MjaSuccess, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("همه پیام‌ها همگام شدند.", style = MaterialTheme.typography.bodyMedium, color = MjaSuccess)
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Action
                MjaButton(
                    text = if (sync.running) "در حال همگام‌سازی..." else "شروع همگام‌سازی",
                    icon = Icons_Sync,
                    onClick = { Repo.runSync() },
                    enabled = !sync.running,
                    loading = sync.running,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(20.dp))

                // Report
                SectionHeader("گزارش عملیات")
                Spacer(Modifier.height(8.dp))
                if (sync.report.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MjaCard)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons_Terminal, contentDescription = null, tint = MjaTextDisabled, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("گزارشی ثبت نشده است.", style = MaterialTheme.typography.bodySmall, color = MjaTextDisabled)
                    }
                } else {
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            sync.report.forEach { line ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.Top,
                                ) {
                                    Box(
                                        modifier = Modifier.size(6.dp).clip(RoundedCornerShape(50)).background(MjaAccent).padding(top = 4.dp),
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        line,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MjaTextSecondary,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}
