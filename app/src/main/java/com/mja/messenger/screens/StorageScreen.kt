package com.mja.messenger.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.Repo
import com.mja.messenger.data.StorageStats
import com.mja.messenger.data.humanSize
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 36 — Storage Management
 */
@Composable
fun StorageScreen(onBack: () -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var clearTempConfirm by remember { mutableStateOf(false) }
    var clearAllConfirm by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(500); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2500); toast = null } }

    var stats by remember { mutableStateOf(StorageStats()) }
    LaunchedEffect(loading) {
        if (!loading) stats = Repo.storageStats()
    }

    val usedTotal = stats.total
    val capBytes = 64L * 1024 * 1024 * 1024 // 64GB reference cap
    val overall = if (capBytes > 0) (usedTotal.toFloat() / capBytes).coerceIn(0f, 1f) else 0f
    val overLimit = usedTotal > 500L * 1024 * 1024

    val rows = listOf(
        StorageRowData("تصاویر", stats.images, Icons_Image, MjaBrand),
        StorageRowData("ویدئوها", stats.videos, Icons_Video, MjaAccent),
        StorageRowData("فایل‌ها", stats.files + stats.audio, Icons_FileDoc, MjaSuccess),
        StorageRowData("پیام‌ها", stats.messages, Icons_Chat, MjaWarning),
        StorageRowData("داده‌های موقت", stats.temp, Icons_Trash, MjaTextSecondary),
    )

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            ScreenHeader(title = "مدیریت حافظه", onBack = onBack)

            Column(Modifier.padding(horizontal = 20.dp)) {
                when {
                    loading -> SkeletonList(count = 6, showAvatar = false)

                    usedTotal == 0L -> EmptyState(
                        icon = Icons_Storage,
                        title = "فضای اشغال‌شده‌ای وجود ندارد.",
                        description = "هنوز فایل یا رسانه‌ای ذخیره نکرده‌اید.",
                    )

                    else -> {
                        // Total card
                        MjaCard(modifier = Modifier.fillMaxWidth(), elevated = true) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(46.dp).clip(RoundedCornerShape(13.dp)).background(MjaElevated),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(Icons_Storage, contentDescription = null, tint = MjaBrand, modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            humanSize(usedTotal),
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = MjaTextPrimary,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                        Text("کل فضای اشغال‌شده", style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary)
                                    }
                                    StatusPill("${(overall * 100).toInt()}٪", color = MjaBrand)
                                }
                                Spacer(Modifier.height(14.dp))
                                ProgressBar(progress = overall, height = 9.dp)
                                Spacer(Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "از ۶۴ گیگابایت مرجع",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MjaTextDisabled,
                                    )
                                    Spacer(Modifier.weight(1f))
                                    LtrText(
                                        "${humanSize(usedTotal)} / 64 GB",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MjaTextDisabled,
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))

                        // Warning
                        if (overLimit) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MjaWarning.copy(alpha = 0.12f))
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons_ErrorOutline, contentDescription = null, tint = MjaWarning, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text("فضای ذخیره‌سازی بالا است", style = MaterialTheme.typography.bodyMedium, color = MjaWarning)
                                    Text(
                                        "برای آزادسازی فضا، داده‌های موقت را پاک کنید.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MjaTextSecondary,
                                    )
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                        }

                        // Usage rows
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                rows.forEachIndexed { index, row ->
                                    if (index > 0) HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 11.dp, horizontal = 14.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(row.icon, contentDescription = null, tint = row.accent, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(10.dp))
                                            Text(row.title, style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary, modifier = Modifier.weight(1f))
                                            LtrText(
                                                humanSize(row.bytes),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MjaTextSecondary,
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        val fraction = if (usedTotal > 0) (row.bytes.toFloat() / usedTotal).coerceIn(0f, 1f) else 0f
                                        ProgressBar(progress = fraction, color = row.accent, height = 5.dp)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        // Actions
                        MjaButton(
                            text = "پاک‌سازی داده‌های موقت",
                            style = MjaButtonStyle.Secondary,
                            icon = Icons_Trash,
                            onClick = { clearTempConfirm = true },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(12.dp))
                        MjaButton(
                            text = "پاک‌سازی کامل",
                            style = MjaButtonStyle.Destructive,
                            icon = Icons_DeleteSolid,
                            onClick = { clearAllConfirm = true },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(12.dp))
                        MjaButton(
                            text = "مشاهده فایل‌ها",
                            style = MjaButtonStyle.Ghost,
                            icon = Icons_Folder,
                            onClick = { toast = "از صفحه فایل‌ها استفاده کنید" },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(28.dp))
                    }
                }
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }

        if (clearTempConfirm) {
            MjaDialog(
                title = "پاک‌سازی داده‌های موقت؟",
                message = "فایل‌های موقت حذف می‌شوند؛ پیام‌ها و رسانه‌ها دست‌نخورده می‌مانند.",
                destructive = true,
                icon = Icons_Trash,
                onDismiss = { clearTempConfirm = false },
                onConfirm = {
                    val freed = Repo.clearTemp()
                    stats = Repo.storageStats()
                    clearTempConfirm = false
                    toast = "${humanSize(freed)} آزاد شد"
                },
            )
        }

        if (clearAllConfirm) {
            MjaDialog(
                title = "همه رسانه‌ها حذف شوند؟",
                message = "تصاویر، ویدئوها، فایل‌ها و داده‌های موقت برای همیشه حذف می‌شوند.",
                destructive = true,
                icon = Icons_DeleteSolid,
                confirmText = "حذف کامل",
                onDismiss = { clearAllConfirm = false },
                onConfirm = {
                    val freed = Repo.clearAllMedia()
                    stats = Repo.storageStats()
                    clearAllConfirm = false
                    toast = "${humanSize(freed)} آزاد شد"
                },
            )
        }
    }
}

private data class StorageRowData(
    val title: String,
    val bytes: Long,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accent: Color,
)
