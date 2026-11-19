package com.mja.messenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.Message
import com.mja.messenger.data.MsgKind
import com.mja.messenger.data.MsgStatus
import com.mja.messenger.data.timeLabel
import com.mja.messenger.ui.theme.*

/* ------------------------------------------------------------------ */
/* Date separator                                                      */
/* ------------------------------------------------------------------ */

@Composable
fun DateDivider(label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.weight(1f).height(1.dp).background(MjaBorder.copy(alpha = 0.6f)))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MjaTextSecondary,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(MjaCard)
                .padding(horizontal = 10.dp, vertical = 5.dp),
        )
        Box(Modifier.weight(1f).height(1.dp).background(MjaBorder.copy(alpha = 0.6f)))
    }
}

/* ------------------------------------------------------------------ */
/* Delivery status ticks                                               */
/* ------------------------------------------------------------------ */

@Composable
fun DeliveryTicks(status: MsgStatus, modifier: Modifier = Modifier) {
    val tint = when (status) {
        MsgStatus.PENDING -> MjaTextDisabled
        MsgStatus.SENDING -> MjaTextDisabled
        MsgStatus.FAILED -> MjaError
        MsgStatus.SENT -> MjaTextSecondary
        MsgStatus.DELIVERED -> MjaAccent
        MsgStatus.READ -> MjaSuccess
    }
    val icon = when (status) {
        MsgStatus.PENDING, MsgStatus.SENDING -> Icons_Schedule
        MsgStatus.FAILED -> Icons_ErrorOutline
        MsgStatus.SENT -> Icons_Check
        MsgStatus.DELIVERED, MsgStatus.READ -> Icons_Done
    }
    Icon(icon, contentDescription = status.name, tint = tint, modifier = modifier.size(13.dp))
}

/* ------------------------------------------------------------------ */
/* Waveform (voice notes)                                              */
/* ------------------------------------------------------------------ */

@Composable
fun Waveform(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier,
    height: Dp = 26.dp,
    active: Boolean = false,
    barWidth: Dp = 2.dp,
    gap: Dp = 2.dp,
) {
    Row(
        modifier = modifier.height(height),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        val list = if (amplitudes.isEmpty()) List(28) { 0.3f } else amplitudes
        list.forEachIndexed { i, amp ->
            val h = (height.value * (0.18f + amp.coerceIn(0f, 1f) * 0.82f)).dp
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(h)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (active && i < list.size / 2) MjaBrand
                        else if (active) MjaTextDisabled
                        else MjaTextSecondary,
                    ),
            )
        }
    }
}

/* ------------------------------------------------------------------ */
/* Reply preview strip inside a bubble                                 */
/* ------------------------------------------------------------------ */

@Composable
fun ReplyStrip(name: String, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black.copy(alpha = 0.25f))
            .padding(8.dp),
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(34.dp)
                .clip(RoundedCornerShape(50))
                .background(MjaAccent),
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(name, style = MaterialTheme.typography.labelSmall, color = MjaAccent, fontWeight = FontWeight.SemiBold)
            Text(
                text,
                style = MaterialTheme.typography.bodySmall,
                color = MjaTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/* ------------------------------------------------------------------ */
/* Message bubble                                                      */
/* ------------------------------------------------------------------ */

/**
 * The single message bubble of MJA.
 * Out: #252C38 (left in RTL), In: #191C22 (right in RTL).
 */
@Composable
fun MessageBubble(
    message: Message,
    isSelf: Boolean,
    modifier: Modifier = Modifier,
    senderName: String? = null,
    senderAvatar: (@Composable () -> Unit)? = null,
    showTime: Boolean = true,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    reactions: List<String> = emptyList(),
    voicePlaying: Boolean = false,
    onPlayVoice: (() -> Unit)? = null,
    onOpenMedia: (() -> Unit)? = null,
) {
    val bg = if (isSelf) MjaBubbleOut else MjaBubbleIn
    val alignment = if (isSelf) Alignment.Start else Alignment.End

    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 3.dp, horizontal = 12.dp),
        horizontalAlignment = alignment,
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (!isSelf && senderAvatar != null) {
                senderAvatar()
                Spacer(Modifier.width(7.dp))
            }
            Column(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(bg)
                    .border(1.dp, MjaBorder.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
                    .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
                    .padding(10.dp),
            ) {
                if (!isSelf && senderName != null) {
                    Text(
                        senderName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MjaAccent,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                if (message.replyPreview != null && message.replySenderName != null) {
                    ReplyStrip(name = message.replySenderName, text = message.replyPreview)
                    Spacer(Modifier.height(6.dp))
                }
                when (message.kind) {
                    MsgKind.TEXT -> Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MjaTextPrimary,
                    )

                    MsgKind.IMAGE -> MediaPlaceholder(
                        icon = Icons_Image,
                        label = message.fileName ?: "تصویر",
                        meta = if (message.fileSize > 0) com.mja.messenger.data.humanSize(message.fileSize) else "",
                        onClick = onOpenMedia,
                    )

                    MsgKind.VIDEO -> MediaPlaceholder(
                        icon = Icons_Video,
                        label = message.fileName ?: "ویدئو",
                        meta = message.fileName ?: "",
                        onClick = onOpenMedia,
                    )

                    MsgKind.FILE, MsgKind.AUDIO -> MediaPlaceholder(
                        icon = if (message.kind == MsgKind.AUDIO) Icons_FileAudio else Icons_FileDoc,
                        label = message.fileName ?: "فایل",
                        meta = com.mja.messenger.data.humanSize(message.fileSize),
                        onClick = onOpenMedia,
                    )

                    MsgKind.VOICE -> Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        MjaIconButton(
                            icon = if (voicePlaying) Icons_Pause else Icons_Play,
                            onClick = { onPlayVoice?.invoke() },
                            contentDescription = "پخش",
                            background = MjaBrand,
                            tint = MjaBackground,
                            size = 38.dp,
                            iconSize = 18.dp,
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Waveform(
                                amplitudes = List(22) { i ->
                                    (((message.id.hashCode().let { h -> (h ushr (i % 16)) }) and 0xFF) / 255f)
                                        .coerceIn(0.15f, 1f)
                                },
                                active = voicePlaying,
                                height = 22.dp,
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = formatDuration(message.durationMs),
                                style = MaterialTheme.typography.labelSmall,
                                color = MjaTextSecondary,
                            )
                        }
                    }
                }

                if (message.forwarded) {
                    Text(
                        text = "↪ ارسال‌شده",
                        style = MaterialTheme.typography.labelSmall,
                        color = MjaAccent,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }

                Spacer(Modifier.height(5.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (showTime) {
                        Text(
                            text = timeLabel(message.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MjaTextDisabled,
                        )
                    }
                    if (isSelf) {
                        Spacer(Modifier.width(5.dp))
                        DeliveryTicks(message.status)
                    }
                }
            }
        }

        if (reactions.isNotEmpty() || message.reactions.isNotEmpty()) {
            val all = (reactions + message.reactions).distinct()
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MjaElevated)
                    .border(1.dp, MjaBorder, RoundedCornerShape(50))
                    .padding(horizontal = 9.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(all.joinToString(" "), style = MaterialTheme.typography.labelSmall, color = MjaTextPrimary)
            }
        }
    }
}

@Composable
private fun MediaPlaceholder(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    meta: String,
    onClick: (() -> Unit)?,
) {
    Row(
        modifier = Modifier
            .width(230.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MjaElevated)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(MjaBackground2),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = MjaAccent, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = MjaTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (meta.isNotBlank()) {
                Text(
                    meta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MjaTextSecondary,
                    maxLines = 1,
                )
            }
        }
    }
}

fun formatDuration(ms: Long): String {
    val totalSec = ms / 1000
    val m = totalSec / 60
    val s = totalSec % 60
    return String.format(java.util.Locale.US, "%02d:%02d", m, s)
}

/* ------------------------------------------------------------------ */
/* Typing indicator                                                    */
/* ------------------------------------------------------------------ */

@Composable
fun TypingBubble(label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(MjaBubbleIn)
                .border(1.dp, MjaBorder.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary)
        }
    }
}

/* ------------------------------------------------------------------ */
/* Reply / forward context bar                                         */
/* ------------------------------------------------------------------ */

@Composable
fun ReplyBar(
    title: String,
    preview: String,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    tone: Color = MjaAccent,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            .background(MjaElevated)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(34.dp)
                .clip(RoundedCornerShape(50))
                .background(tone),
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = tone, fontWeight = FontWeight.SemiBold)
            Text(
                preview,
                style = MaterialTheme.typography.bodySmall,
                color = MjaTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        MjaIconButton(icon = Icons_Close, onClick = onCancel, contentDescription = "بستن", size = 32.dp, iconSize = 17.dp)
    }
}

/* ------------------------------------------------------------------ */
/* Message composer                                                    */
/* ------------------------------------------------------------------ */

/**
 * Input bar: emoji / attachment / microphone / send.
 * The microphone turns into the send button as soon as text is typed.
 */
@Composable
fun MessageComposer(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    onMic: () -> Unit = {},
    onAttach: () -> Unit = {},
    onEmoji: () -> Unit = {},
    placeholder: String = "پیام بنویسید...",
    reply: Message? = null,
    replyName: String? = null,
    onCancelReply: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MjaBackground2)
            .border(1.dp, MjaBorder.copy(alpha = 0.7f), RoundedCornerShape(0.dp)),
    ) {
        if (reply != null) {
            ReplyBar(
                title = "پاسخ به ${replyName ?: "پیام"}",
                preview = reply.text.ifBlank { reply.previewTextForBar() },
                onCancel = onCancelReply,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MjaIconButton(icon = Icons_Emoji, onClick = onEmoji, contentDescription = "ایموجی")
            MjaIconButton(icon = Icons_Attach, onClick = onAttach, contentDescription = "پیوست")
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(23.dp))
                    .background(MjaCard)
                    .border(1.dp, MjaBorder, RoundedCornerShape(23.dp))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicComposerField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = placeholder,
                )
            }
            Spacer(Modifier.width(8.dp))
            if (value.isBlank()) {
                MjaIconButton(
                    icon = Icons_Mic,
                    onClick = onMic,
                    contentDescription = "پیام صوتی",
                    background = MjaElevated,
                    size = 46.dp,
                    iconSize = 21.dp,
                )
            } else {
                MjaIconButton(
                    icon = Icons_Send,
                    onClick = onSend,
                    contentDescription = "ارسال",
                    background = MjaBrand,
                    tint = MjaBackground,
                    size = 46.dp,
                    iconSize = 21.dp,
                )
            }
        }
    }
}

@Composable
private fun BasicComposerField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.CenterStart) {
        if (value.isEmpty()) {
            Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = MjaTextDisabled)
        }
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = false,
            maxLines = 4,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MjaTextPrimary),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(MjaBrand),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun Message.previewTextForBar(): String =
    text.ifBlank { fileName ?: "فایل" }
