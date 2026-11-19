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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.*
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 23 — File Transfer details
 */
@Composable
fun FileTransferScreen(fileId: String, onBack: () -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0f) }
    var localState by remember { mutableStateOf<TransferState?>(null) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(450); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2000); toast = null } }

    val files by Repo.files.collectAsState()
    val file = files.firstOrNull { it.id == fileId }
    val state = localState ?: file?.state ?: TransferState.PENDING

    // Simulated transfer progression
    LaunchedEffect(fileId, state) {
        if (state == TransferState.TRANSFERRING) {
            progress = file?.progress ?: 0.35f
            while (progress < 1f) {
                delay(400)
                progress = (progress + 0.06f).coerceAtMost(1f)
            }
            delay(300)
            localState = TransferState.DONE
        }
    }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            ScreenHeader(title = "جزئیات انتقال فایل", onBack = onBack)

            when {
                loading -> Column(Modifier.padding(20.dp)) { SkeletonList(count = 5, showAvatar = false) }

                file == null -> ErrorState(
                    title = "فایل پیدا نشد.",
                    description = "این فایل حذف شده است.",
                    onAction = onBack,
                )

                else -> Column(Modifier.padding(horizontal = 20.dp)) {
                    // Icon card + name
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier.size(84.dp).clip(RoundedCornerShape(22.dp)).background(MjaElevated),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                when (file.kind) {
                                    FileKind.DOC -> Icons_FileDoc
                                    FileKind.IMAGE -> Icons_FileImage
                                    FileKind.VIDEO -> Icons_FileVideo
                                    FileKind.AUDIO -> Icons_FileAudio
                                },
                                contentDescription = null,
                                tint = MjaBrand,
                                modifier = Modifier.size(38.dp),
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        LtrText(file.name, style = MaterialTheme.typography.titleLarge, color = MjaTextPrimary, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(10.dp))
                        StatusPill(
                            when (state) {
                                TransferState.PENDING -> "در انتظار"
                                TransferState.TRANSFERRING -> "در حال انتقال"
                                TransferState.DONE -> "دریافت کامل شد"
                                TransferState.FAILED -> "ناموفق"
                            },
                            color = when (state) {
                                TransferState.PENDING -> MjaWarning
                                TransferState.TRANSFERRING -> MjaBrand
                                TransferState.DONE -> MjaSuccess
                                TransferState.FAILED -> MjaError
                            },
                            filled = state == TransferState.DONE || state == TransferState.FAILED,
                        )
                    }
                    Spacer(Modifier.height(18.dp))

                    // Info card
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            InfoRow("حجم", humanSize(file.size))
                            InfoRow("فرستنده", file.sender)
                            InfoRow("گیرنده", file.chatTitle.ifBlank { "من" })
                            InfoRow("زمان", file.date)
                            InfoRow("نوع", when (file.kind) {
                                FileKind.DOC -> "سند"
                                FileKind.IMAGE -> "تصویر"
                                FileKind.VIDEO -> "ویدئو"
                                FileKind.AUDIO -> "صوت"
                            })
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    // Progress
                    if (state == TransferState.TRANSFERRING) {
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("پیشرفت انتقال", style = MaterialTheme.typography.bodyMedium, color = MjaTextSecondary)
                                    Spacer(Modifier.weight(1f))
                                    LtrText(
                                        "${(progress * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MjaBrand,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                ProgressBar(progress = progress, color = MjaBrand)
                                Spacer(Modifier.height(8.dp))
                                LtrText(
                                    "سرعت انتقال: 1.4 MB/s",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MjaTextDisabled,
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    // Success / failure cards
                    if (state == TransferState.DONE) {
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons_CheckCircle, contentDescription = null, tint = MjaSuccess, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("دریافت کامل شد", style = MaterialTheme.typography.titleMedium, color = MjaSuccess)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    if (state == TransferState.FAILED) {
                        ErrorState(
                            title = "انتقال ناموفق بود.",
                            description = "گیرنده در شبکه محلی حاضر نیست یا ارتباط قطع شده است.",
                            actionText = "تلاش مجدد",
                            onAction = {
                                localState = TransferState.TRANSFERRING
                                progress = 0.35f
                            },
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    // Actions
                    when (state) {
                        TransferState.TRANSFERRING -> {
                            MjaButton(
                                text = "لغو انتقال",
                                style = MjaButtonStyle.Destructive,
                                icon = Icons_Close,
                                onClick = { localState = TransferState.FAILED; toast = "انتقال لغو شد" },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        TransferState.FAILED -> {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MjaButton(
                                    text = "تلاش مجدد",
                                    onClick = {
                                        localState = TransferState.TRANSFERRING
                                        progress = 0.35f
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                                MjaButton(
                                    text = "لغو",
                                    style = MjaButtonStyle.Ghost,
                                    onClick = onBack,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                        TransferState.DONE -> {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MjaButton(
                                    text = "باز کردن فایل",
                                    icon = Icons_FileDoc,
                                    onClick = { toast = "در حال باز کردن..." },
                                    modifier = Modifier.weight(1f),
                                )
                                MjaButton(
                                    text = "اشتراک",
                                    style = MjaButtonStyle.Secondary,
                                    icon = Icons_Share,
                                    onClick = { toast = "اشتراک‌گذاری..." },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                        TransferState.PENDING -> {
                            MjaButton(
                                text = "شروع انتقال",
                                onClick = { localState = TransferState.TRANSFERRING; progress = 0.35f },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    Spacer(Modifier.height(28.dp))
                }
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }
    }
}
