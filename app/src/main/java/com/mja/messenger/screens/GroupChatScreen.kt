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
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

private sealed interface GroupRowItem {
    data class DateRow(val label: String) : GroupRowItem
    data class MsgRow(val message: Message) : GroupRowItem
}

/**
 * 11 — Group Chat
 */
@Composable
fun GroupChatScreen(
    chatId: String,
    onBack: () -> Unit,
    onOpenGroupInfo: () -> Unit,
    onOpenMedia: () -> Unit,
    onOpenMessageSearch: () -> Unit,
    onMessageDetails: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
    onRecordVoice: (String) -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var text by remember { mutableStateOf("") }
    var menuOpen by remember { mutableStateOf(false) }
    var attachOpen by remember { mutableStateOf(false) }
    var emojiOpen by remember { mutableStateOf(false) }
    var mentionOpen by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Message?>(null) }
    var deleteConfirm by remember { mutableStateOf<Message?>(null) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(450); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2000); toast = null } }

    val chats by Repo.chats.collectAsState()
    val allMessages by Repo.messages.collectAsState()
    val reply by Repo.replyDraft.collectAsState()
    val chat = chats.firstOrNull { it.id == chatId }
    val me = Repo.currentUser()

    val msgs = remember(allMessages, chatId) {
        allMessages.filter { it.chatId == chatId }.sortedBy { it.timestamp }
    }
    val rows = remember(msgs) {
        buildList<GroupRowItem> {
            var lastDate: String? = null
            msgs.forEach { m ->
                if (m.dateLabel != lastDate) {
                    add(GroupRowItem.DateRow(m.dateLabel))
                    lastDate = m.dateLabel
                }
                add(GroupRowItem.MsgRow(m))
            }
        }
    }
    val members = remember(chat) {
        (chat?.memberIds ?: emptyList()).mapNotNull { Repo.user(it) }
    }

    val listState = rememberLazyListState()
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(rows.size) {
        if (rows.isNotEmpty()) listState.animateScrollToItem(rows.size - 1)
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
                    name = chat?.title ?: "گروه",
                    isGroup = true,
                    size = 42.dp,
                    colorIndex = chat?.avatarColor ?: 0,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onOpenGroupInfo() },
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f).clickable { onOpenGroupInfo() }) {
                    Text(
                        chat?.title ?: "گروه",
                        style = MaterialTheme.typography.titleMedium,
                        color = MjaTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        "${chat?.memberCount ?: members.size} عضو · متصل به شبکه محلی",
                        style = MaterialTheme.typography.labelSmall,
                        color = MjaSuccess,
                        maxLines = 1,
                    )
                }
                MjaIconButton(icon = Icons_InfoOutlined, onClick = onOpenGroupInfo, contentDescription = "اطلاعات گروه")
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
                    Text(timeLabel(pinnedMsg.timestamp), style = MaterialTheme.typography.labelSmall, color = MjaTextDisabled)
                }
            }

            /* ---------------- Body ---------------- */
            when {
                loading -> SkeletonList(count = 5)

                rows.isEmpty() -> EmptyState(
                    icon = Icons_Chat,
                    title = "هنوز پیامی در این گروه نیست.",
                    description = "با ارسال اولین پیام، گفتگوی گروهی را آغاز کنید.",
                )

                else -> LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    items(rows, key = {
                        when (it) {
                            is GroupRowItem.DateRow -> "gd_${it.label}"
                            is GroupRowItem.MsgRow -> it.message.id
                        }
                    }) { row ->
                        when (row) {
                            is GroupRowItem.DateRow -> DateDivider(row.label)
                            is GroupRowItem.MsgRow -> {
                                val m = row.message
                                val sender = Repo.user(m.senderId)
                                MessageBubble(
                                    message = m,
                                    isSelf = m.senderId == me.id,
                                    senderName = sender?.name,
                                    senderAvatar = sender?.name,
                                    onClick = { selected = m },
                                    onOpenMedia = { onOpenMedia() },
                                )
                            }
                        }
                    }
                    if (chat?.typing == true) {
                        item { TypingBubble("فردی در حال نوشتن...") }
                    }
                }
            }

            /* ---------------- Mention chips ---------------- */
            if (mentionOpen && members.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    members.take(3).forEach { u ->
                        MjaChip(
                            text = "@${u.username}",
                            onClick = {
                                text = "$text @${u.username} "
                                mentionOpen = false
                            },
                        )
                    }
                    Spacer(Modifier.weight(1f))
                }
            }

            /* ---------------- Composer ---------------- */
            MessageComposer(
                value = text,
                onValueChange = { text = it; if (it.endsWith("@")) mentionOpen = true },
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

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 110.dp)) {
                ToastMessage(toast!!)
            }
        }

        /* ---------------- Header menu ---------------- */
        if (menuOpen) {
            MjaBottomSheet(onDismiss = { menuOpen = false }, title = "گزینه‌های گروه") {
                SettingsRow(title = "مدیریت اعضا", icon = Icons_People, onClick = { menuOpen = false; onOpenGroupInfo() })
                SettingsRow(title = "جست‌وجوی پیام", icon = Icons_Search, onClick = { menuOpen = false; onOpenMessageSearch() })
                SettingsRow(title = "رسانه‌های گروه", icon = Icons_Gallery, onClick = { menuOpen = false; onOpenMedia() })
                SettingsRow(title = "جست‌وجو در مخاطبین", icon = Icons_Person, onClick = { menuOpen = false; mentionOpen = true })
                SettingsRow(
                    title = "پاک کردن گفتگو",
                    icon = Icons_Trash,
                    danger = true,
                    onClick = { menuOpen = false; Repo.clearChat(chatId); toast = "گفتگو پاک شد" },
                )
            }
        }

        /* ---------------- Attach sheet ---------------- */
        if (attachOpen) {
            MjaBottomSheet(onDismiss = { attachOpen = false }, title = "پیوست") {
                SettingsRow(title = "تصویر", icon = Icons_Image, onClick = {
                    attachOpen = false
                    Repo.sendMedia(chatId, MsgKind.IMAGE, fileName = "IMG_3391.jpg", fileSize = 2_140_000)
                    toast = "در حال ارسال تصویر..."
                })
                SettingsRow(title = "ویدئو", icon = Icons_Video, onClick = {
                    attachOpen = false
                    Repo.sendMedia(chatId, MsgKind.VIDEO, fileName = "ویدئو_آموزشی.mp4", fileSize = 12_400_000)
                    toast = "در حال ارسال ویدئو..."
                })
                SettingsRow(title = "فایل", icon = Icons_FileDoc, onClick = {
                    attachOpen = false
                    Repo.sendMedia(chatId, MsgKind.FILE, fileName = "قرارداد.pdf", fileSize = 980_000)
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
                emojis.chunked(6).forEach { rowEmojis ->
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
                                    .clickable { text += e; emojiOpen = false },
                                contentAlignment = Alignment.Center,
                            ) { Text(e, style = MaterialTheme.typography.titleLarge) }
                        }
                        repeat(6 - rowEmojis.size) { Spacer(Modifier.size(48.dp)) }
                    }
                }
            }
        }

        /* ---------------- Message actions ---------------- */
        selected?.let { m ->
            MjaBottomSheet(onDismiss = { selected = null }, title = "گزینه‌های پیام") {
                SettingsRow(title = "پاسخ‌دادن", icon = Icons_Reply, onClick = { Repo.setReply(m); selected = null })
                SettingsRow(title = "ارسال به", icon = Icons_Forward, onClick = {
                    Repo.setForward(m); selected = null; toast = "پیام برای ارسال آماده شد"
                })
                SettingsRow(title = "کپی", icon = Icons_Copy, onClick = {
                    clipboard.setText(AnnotatedString(m.text.ifBlank { m.fileName ?: "" }))
                    selected = null; toast = "کپی شد"
                })
                SettingsRow(title = "سنجاق‌شدن", icon = Icons_Pin, onClick = { Repo.togglePin(m.id); selected = null })
                SettingsRow(title = "جزئیات پیام", icon = Icons_InfoOutlined, onClick = {
                    selected = null; onMessageDetails(m.id)
                })
                SettingsRow(title = "حذف پیام", icon = Icons_Trash, danger = true, onClick = {
                    deleteConfirm = m; selected = null
                })
                Spacer(Modifier.height(4.dp))
            }
        }

        deleteConfirm?.let { m ->
            MjaDialog(
                title = "حذف پیام؟",
                message = "این پیام از گروه حذف می‌شود.",
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
