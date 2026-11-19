package com.mja.messenger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.*
import com.mja.messenger.navigation.Routes
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.launch

private sealed interface RowItem {
    data class DateRow(val label: String) : RowItem
    data class MsgRow(val message: Message) : RowItem
}

private fun Modifier.clickableSimple(onClick: () -> Unit): Modifier = this.clickable { onClick() }

/**
 * 10 — Private Chat
 */
@Composable
fun PrivateChatScreen(
    chatId: String,
    onBack: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenGroupInfo: () -> Unit,
    onOpenMedia: () -> Unit,
    onOpenMessageSearch: () -> Unit,
    onMessageDetails: (String) -> Unit,
    onVoiceCall: (String) -> Unit,
    onVideoCall: (String) -> Unit,
    onRecordVoice: (String) -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var text by remember { mutableStateOf("") }
    var menuOpen by remember { mutableStateOf(false) }
    var attachOpen by remember { mutableStateOf(false) }
    var emojiOpen by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Message?>(null) }
    var deleteConfirm by remember { mutableStateOf<Message?>(null) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { kotlinx.coroutines.delay(450); loading = false }

    val chats by Repo.chats.collectAsState()
    val allMessages by Repo.messages.collectAsState()
    val reply by Repo.replyDraft.collectAsState()
    val chat = chats.firstOrNull { it.id == chatId }
    val me = Repo.currentUser()

    val peerId = chat?.memberIds?.firstOrNull { it != me.id }
        ?: Repo.users.value.firstOrNull { it.username == chat?.username }?.id
        ?: "u_ali"
    val peer = Repo.user(peerId)

    val msgs = remember(allMessages, chatId) {
        allMessages.filter { it.chatId == chatId }.sortedBy { it.timestamp }
    }

    val rows = remember(msgs) {
        buildList<RowItem> {
            var lastDate: String? = null
            msgs.forEach { m ->
                if (m.dateLabel != lastDate) {
                    add(RowItem.DateRow(m.dateLabel))
                    lastDate = m.dateLabel
                }
                add(RowItem.MsgRow(m))
            }
        }
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(rows.size) {
        if (rows.isNotEmpty()) listState.animateScrollToItem(rows.size - 1)
    }

    LaunchedEffect(toast) {
        if (toast != null) { kotlinx.coroutines.delay(2000); toast = null }
    }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(Modifier.fillMaxSize()) {

            /* ---------------- Header ---------------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MjaIconButton(icon = Icons_Back, onClick = onBack, contentDescription = "بازگشت")
                Spacer(Modifier.width(2.dp))
                Avatar(
                    name = chat?.title ?: "—",
                    size = 42.dp,
                    colorIndex = chat?.avatarColor ?: 0,
                    online = chat?.online,
                    modifier = Modifier.clip(RoundedCornerShape(14.dp)).clickableSimple { onOpenProfile(peerId) },
                )
                Spacer(Modifier.width(10.dp))
                Column(
                    Modifier.weight(1f).clickableSimple { onOpenProfile(peerId) },
                ) {
                    Text(
                        chat?.title ?: "گفتگو",
                        style = MaterialTheme.typography.titleMedium,
                        color = MjaTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val subtitle = when {
                        chat?.typing == true -> "در حال تایپ..."
                        chat?.online == true -> "آنلاین"
                        else -> (peer?.lastSeen ?: "").ifBlank { "آفلاین" }
                    }
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (chat?.typing == true || chat?.online == true) MjaSuccess else MjaTextSecondary,
                        maxLines = 1,
                    )
                }
                MjaIconButton(icon = Icons_Call, onClick = { onVoiceCall(peerId) }, contentDescription = "تماس صوتی")
                MjaIconButton(icon = Icons_Video, onClick = { onVideoCall(peerId) }, contentDescription = "تماس تصویری")
                MjaIconButton(icon = Icons_More, onClick = { menuOpen = true }, contentDescription = "بیشتر")
            }

            /* ---------------- Pinned banner ---------------- */
            val pinnedMsg = msgs.lastOrNull { it.pinned }
            if (pinnedMsg != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MjaCard)
                        .border(1.dp, MjaBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons_Pin, contentDescription = null, tint = MjaAccent, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        pinnedMsg.text.ifBlank { pinnedMsg.fileName ?: "پیام" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MjaTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        timeLabel(pinnedMsg.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MjaTextDisabled,
                    )
                }
            }

            /* ---------------- Body ---------------- */
            when {
                loading -> SkeletonList(count = 5, showAvatar = true)

                rows.isEmpty() -> EmptyState(
                    icon = Icons_Chat,
                    title = "هنوز پیامی در این گفتگو نیست.",
                    description = "اولین پیام را بفرستید تا گفتگو شروع شود.",
                )

                else -> LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    items(rows, key = {
                        when (it) {
                            is RowItem.DateRow -> "d_${it.label}"
                            is RowItem.MsgRow -> it.message.id
                        }
                    }) { row ->
                        when (row) {
                            is RowItem.DateRow -> DateDivider(row.label)
                            is RowItem.MsgRow -> {
                                val m = row.message
                                MessageBubble(
                                    message = m,
                                    isSelf = m.senderId == me.id,
                                    onClick = { selected = m },
                                    onOpenMedia = { onOpenMedia() },
                                )
                            }
                        }
                    }
                    if (chat?.typing == true) {
                        item { TypingBubble("در حال نوشتن...") }
                    }
                }
            }

            /* ---------------- Composer ---------------- */
            MessageComposer(
                value = text,
                onValueChange = { text = it },
                onSend = {
                    if (text.isNotBlank()) {
                        val r = reply
                        Repo.sendText(
                            chatId = chatId,
                            text = text,
                            replyToId = r?.id,
                            replyPreview = r?.text?.ifBlank { r.fileName ?: "فایل" },
                            replySenderName = r?.let { m -> Repo.user(m.senderId)?.name ?: "پیام" },
                        )
                        Repo.setReply(null)
                        text = ""
                    }
                },
                onMic = { onRecordVoice(chatId) },
                onAttach = { attachOpen = true },
                onEmoji = { emojiOpen = true },
                reply = reply,
                replyName = reply?.let { Repo.user(it.senderId)?.name ?: "پیام" },
                onCancelReply = { Repo.setReply(null) },
            )
        }

        /* ---------------- Toast ---------------- */
        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 110.dp)) {
                ToastMessage(toast!!)
            }
        }

        /* ---------------- Header menu ---------------- */
        if (menuOpen) {
            MjaBottomSheet(onDismiss = { menuOpen = false }, title = "گزینه‌های گفتگو") {
                SettingsRow(title = "نمایش پروفایل", icon = Icons_Person, onClick = { menuOpen = false; onOpenProfile(peerId) })
                SettingsRow(title = "جست‌وجوی پیام", icon = Icons_Search, onClick = { menuOpen = false; onOpenMessageSearch() })
                SettingsRow(title = "رسانه‌های گفتگو", icon = Icons_Gallery, onClick = { menuOpen = false; onOpenMedia() })
                SettingsRow(
                    title = "پاک کردن گفتگو",
                    icon = Icons_Trash,
                    danger = true,
                    onClick = { menuOpen = false; Repo.clearChat(chatId); toast = "گفتگو پاک شد" },
                )
                SettingsRow(title = "بی‌صدا کردن", icon = Icons_NotificationsOff, onClick = {
                    menuOpen = false
                    Repo.toggleChatFlag(chatId, Repo.ChatFlag.MUTED)
                })
                SettingsRow(title = "سنجاق‌شدن", icon = Icons_Pin, onClick = {
                    menuOpen = false
                    Repo.toggleChatFlag(chatId, Repo.ChatFlag.PINNED)
                })
            }
        }

        /* ---------------- Attach sheet ---------------- */
        if (attachOpen) {
            MjaBottomSheet(onDismiss = { attachOpen = false }, title = "پیوست") {
                SettingsRow(title = "تصویر", icon = Icons_Image, onClick = {
                    attachOpen = false
                    Repo.sendMedia(chatId, MsgKind.IMAGE, fileName = "IMG_2481.jpg", fileSize = 1_840_000)
                    toast = "در حال ارسال تصویر..."
                })
                SettingsRow(title = "ویدئو", icon = Icons_Video, onClick = {
                    attachOpen = false
                    Repo.sendMedia(chatId, MsgKind.VIDEO, fileName = "ویدئو_جلسه.mp4", fileSize = 8_400_000)
                    toast = "در حال ارسال ویدئو..."
                })
                SettingsRow(title = "فایل", icon = Icons_FileDoc, onClick = {
                    attachOpen = false
                    Repo.sendMedia(chatId, MsgKind.FILE, fileName = "گزارش_نهایی.pdf", fileSize = 2_400_000)
                    toast = "در حال ارسال فایل..."
                })
                SettingsRow(title = "پیام صوتی", icon = Icons_Mic, onClick = {
                    attachOpen = false
                    onRecordVoice(chatId)
                })
            }
        }

        /* ---------------- Emoji sheet ---------------- */
        if (emojiOpen) {
            MjaBottomSheet(onDismiss = { emojiOpen = false }, title = "ایموجی") {
                val emojis = listOf("😀", "😂", "😍", "👍", "🙏", "❤️", "🔥", "✅", "🎉", "😎", "🤔", "👋")
                val chunked = emojis.chunked(6)
                chunked.forEach { rowEmojis ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        rowEmojis.forEach { e ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MjaCard)
                                    .clickableSimple {
                                        text += e
                                        emojiOpen = false
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(e, style = MaterialTheme.typography.titleLarge)
                            }
                        }
                        repeat(6 - rowEmojis.size) { Spacer(Modifier.size(48.dp)) }
                    }
                }
            }
        }

        /* ---------------- Message actions sheet ---------------- */
        selected?.let { m ->
            MjaBottomSheet(onDismiss = { selected = null }, title = "گزینه‌های پیام") {
                SettingsRow(title = "پاسخ‌دادن", icon = Icons_Reply, onClick = {
                    Repo.setReply(m); selected = null
                })
                SettingsRow(title = "ارسال به", icon = Icons_Forward, onClick = {
                    Repo.setForward(m); selected = null; toast = "پیام برای ارسال آماده شد"
                })
                SettingsRow(title = "کپی", icon = Icons_Copy, onClick = {
                    clipboard.setText(AnnotatedString(m.text.ifBlank { m.fileName ?: "" }))
                    selected = null; toast = "کپی شد"
                })
                SettingsRow(title = "سنجاق‌شدن", icon = Icons_Pin, onClick = {
                    Repo.togglePin(m.id); selected = null
                })
                SettingsRow(title = "واکنش", icon = Icons_Star, onClick = {
                    val emojis = listOf("😀", "👍", "❤️", "🔥")
                    val pick = emojis.firstOrNull { it !in m.reactions } ?: "❤️"
                    Repo.addReaction(m.id, pick)
                    selected = null
                })
                SettingsRow(title = "جزئیات پیام", icon = Icons_InfoOutlined, onClick = {
                    selected = null; onMessageDetails(m.id)
                })
                SettingsRow(title = "حذف پیام", icon = Icons_Trash, danger = true, onClick = {
                    deleteConfirm = m; selected = null
                })
                Spacer(Modifier.height(4.dp))
            }
        }

        /* ---------------- Delete confirm ---------------- */
        deleteConfirm?.let { m ->
            MjaDialog(
                title = "حذف پیام؟",
                message = "این پیام برای شما حذف می‌شود.",
                destructive = true,
                icon = Icons_Trash,
                onDismiss = { deleteConfirm = null },
                onConfirm = {
                    Repo.deleteMessage(m.id)
                    deleteConfirm = null
                    toast = "پیام حذف شد"
                },
            )
        }
    }
}
