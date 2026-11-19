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
 * 12 — Message Details
 */
@Composable
fun MessageDetailsScreen(messageId: String, onBack: () -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var toast by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) { delay(400); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2000); toast = null } }

    val messages by Repo.messages.collectAsState()
    val message = messages.firstOrNull { it.id == messageId }
    var deleteConfirm by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            ScreenHeader(title = "جزئیات پیام", onBack = onBack)

            when {
                loading -> SkeletonList(count = 6, showAvatar = false)

                message == null -> ErrorState(
                    title = "پیام پیدا نشد.",
                    description = "این پیام حذف شده است.",
                    onAction = onBack,
                )

                else -> {
                    val chat = Repo.chat(message.chatId)
                    val sender = Repo.user(message.senderId)
                    val delivered = message.status == MsgStatus.DELIVERED ||
                        message.status == MsgStatus.READ
                    val read = message.status == MsgStatus.READ

                    Column(Modifier.padding(horizontal = 20.dp)) {

                        /* Preview card */
                        MjaCard(
                            modifier = Modifier.fillMaxWidth(),
                            elevated = true,
                            contentPadding = PaddingValues(16.dp),
                        ) {
                            MessageBubble(
                                message = message,
                                isSelf = message.senderId == Repo.currentUser().id,
                                senderName = sender?.name,
                                showTime = false,
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        InfoRow("فرستنده", sender?.name ?: "نامشخص")
                        InfoRow("گفتگو", chat?.title ?: "—")
                        InfoRow("زمان ارسال", dayLabel(message.timestamp) + " - " + timeLabel(message.timestamp))
                        InfoRow("زمان تحویل", if (delivered) timeLabel(message.timestamp + 2_000) else "—")
                        InfoRow("زمان خواندن", if (read) timeLabel(message.timestamp + 90_000) else "—")
                        InfoRow("نوع پیام", when (message.kind) {
                            MsgKind.TEXT -> "متن"
                            MsgKind.IMAGE -> "تصویر"
                            MsgKind.VIDEO -> "ویدئو"
                            MsgKind.AUDIO -> "صوت"
                            MsgKind.VOICE -> "پیام صوتی"
                            MsgKind.FILE -> "فایل"
                        })
                        if (message.fileSize > 0) {
                            InfoRow("حجم", humanSize(message.fileSize))
                        }
                        if (message.fileName != null) {
                            InfoRow("نام فایل", message.fileName!!, valueLtr = true)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("وضعیت انتقال", style = MaterialTheme.typography.bodyMedium, color = MjaTextSecondary)
                            Spacer(Modifier.weight(1f))
                            val (label, color) = when (message.status) {
                                MsgStatus.PENDING -> "در انتظار" to MjaWarning
                                MsgStatus.SENDING -> "در حال ارسال" to MjaAccent
                                MsgStatus.SENT -> "ارسال شد" to MjaTextPrimary
                                MsgStatus.DELIVERED -> "تحویل شد" to MjaSuccess
                                MsgStatus.READ -> "خوانده شد" to MjaOnline
                                MsgStatus.FAILED -> "ناموفق" to MjaError
                            }
                            StatusPill(label, color = color, filled = message.status == MsgStatus.READ || message.status == MsgStatus.FAILED)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("شناسه پیام", style = MaterialTheme.typography.bodyMedium, color = MjaTextSecondary)
                            Spacer(Modifier.weight(1f))
                            LtrText(message.id, style = MaterialTheme.typography.bodySmall, color = MjaTextDisabled)
                        }

                        Spacer(Modifier.height(20.dp))

                        MjaButton(
                            text = "کپی شناسه",
                            style = MjaButtonStyle.Secondary,
                            icon = Icons_Copy,
                            onClick = { toast = "شناسه کپی شد" },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(12.dp))
                        MjaButton(
                            text = "حذف پیام",
                            style = MjaButtonStyle.Destructive,
                            icon = Icons_Trash,
                            onClick = { deleteConfirm = true },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }

        if (deleteConfirm && message != null) {
            MjaDialog(
                title = "حذف پیام؟",
                message = "این پیام برای شما حذف می‌شود.",
                destructive = true,
                icon = Icons_Trash,
                onDismiss = { deleteConfirm = false },
                onConfirm = {
                    Repo.deleteMessage(message.id)
                    deleteConfirm = false
                    onBack()
                },
            )
        }
    }
}
