package com.mja.messenger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.*
import com.mja.messenger.ui.MainScaffold
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

private enum class NotifTab(val label: String) {
    ALL("همه"), UNREAD("خوانده‌نشده"), GROUPS("گروه‌ها"), FILES("فایل‌ها")
}

/**
 * Notifications tab (spec extra — tab 3)
 */
@Composable
fun NotificationsScreen(
    onBack: (() -> Unit)? = null,
    onOpenChat: (String) -> Unit,
    tab: (Int) -> Unit,
) {
    var selected by remember { mutableStateOf(NotifTab.ALL) }
    var loading by remember { mutableStateOf(true) }
    var toast by remember { mutableStateOf<String?>(null) }
    var deleteTarget by remember { mutableStateOf<NotificationItem?>(null) }

    LaunchedEffect(Unit) { delay(600); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2000); toast = null } }

    val notifications by Repo.notifications.collectAsState()
    val chats by Repo.chats.collectAsState()
    val unread = chats.sumOf { it.unread }

    val filtered = remember(notifications, selected) {
        when (selected) {
            NotifTab.ALL -> notifications
            NotifTab.UNREAD -> notifications.filter { !it.read }
            NotifTab.GROUPS -> notifications.filter { it.type == NoticeType.GROUP }
            NotifTab.FILES -> notifications.filter { it.type == NoticeType.FILE }
        }
    }

    MainScaffold(current = 3, onTab = tab, unreadCount = unread) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            Column(Modifier.fillMaxSize()) {
                ScreenHeader(title = "اعلان‌ها") {
                    MjaButton(
                        text = "خواندن همه",
                        style = MjaButtonStyle.Text,
                        onClick = {
                            Repo.markAllNotificationsRead()
                            toast = "همه اعلان‌ها خوانده شد"
                        },
                    )
                }

                Column(Modifier.padding(horizontal = 20.dp)) {
                    MjaTabs(
                        tabs = NotifTab.entries.map { it.label },
                        selectedIndex = selected.ordinal,
                        onSelect = { selected = NotifTab.entries[it] },
                    )
                    Spacer(Modifier.height(8.dp))
                }

                when {
                    loading -> SkeletonList(count = 6)

                    filtered.isEmpty() && selected == NotifTab.ALL -> EmptyState(
                        icon = Icons_Bell,
                        title = "اعلان جدیدی ندارید.",
                        description = "پیام‌ها، درخواست‌ها و رویدادهای مهم اینجا نمایش داده می‌شوند.",
                    )

                    filtered.isEmpty() -> EmptyState(
                        icon = Icons_Search,
                        title = "اعلانی در این دسته نیست.",
                        description = "در این دسته‌بندی اعلانی ثبت نشده است.",
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    ) {
                        items(filtered, key = { it.id }) { n ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (n.read) MjaBackground else MjaCard)
                                    .clickable {
                                        Repo.markReadNotification(n.id)
                                        n.chatId?.let(onOpenChat)
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            when (n.type) {
                                                NoticeType.MESSAGE -> MjaBubbleIn
                                                NoticeType.GROUP -> MjaBubbleOut
                                                NoticeType.FILE -> MjaElevated
                                                NoticeType.CALL -> MjaCard
                                                NoticeType.SYSTEM -> MjaBubbleIn
                                            },
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        when (n.type) {
                                            NoticeType.MESSAGE -> Icons_Chat
                                            NoticeType.GROUP -> Icons_People
                                            NoticeType.FILE -> Icons_FileDoc
                                            NoticeType.CALL -> Icons_Call
                                            NoticeType.SYSTEM -> Icons_InfoOutlined
                                        },
                                        contentDescription = null,
                                        tint = when (n.type) {
                                            NoticeType.MESSAGE -> MjaBrand
                                            NoticeType.GROUP -> MjaAccent
                                            NoticeType.FILE -> MjaTextSecondary
                                            NoticeType.CALL -> MjaSuccess
                                            NoticeType.SYSTEM -> MjaWarning
                                        },
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        n.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (n.read) MjaTextSecondary else MjaTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Spacer(Modifier.height(3.dp))
                                    Text(
                                        n.body,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MjaTextSecondary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Spacer(Modifier.height(5.dp))
                                    Text(
                                        timeLabel(n.timestamp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MjaTextDisabled,
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    if (!n.read) OnlineDot(size = 8.dp, online = true)
                                    Spacer(Modifier.height(6.dp))
                                    MjaIconButton(
                                        icon = Icons_More,
                                        onClick = { deleteTarget = n },
                                        contentDescription = "بیشتر",
                                        size = 32.dp,
                                        iconSize = 16.dp,
                                        background = MjaBackground,
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        item { Spacer(Modifier.height(12.dp)) }
                    }
                }
            }

            if (toast != null) {
                Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)) { ToastMessage(toast!!) }
            }

            deleteTarget?.let { n ->
                MjaBottomSheet(onDismiss = { deleteTarget = null }, title = n.title) {
                    SettingsRow(title = "باز کردن گفتگو", icon = Icons_Chat, onClick = {
                        deleteTarget = null
                        Repo.markReadNotification(n.id)
                        n.chatId?.let(onOpenChat)
                    })
                    SettingsRow(title = "علامت‌گذاری به‌عنوان خوانده‌شده", icon = Icons_CheckCircle, onClick = {
                        Repo.markReadNotification(n.id)
                        deleteTarget = null
                        toast = "اعلان خوانده شد"
                    })
                    SettingsRow(title = "حذف اعلان", icon = Icons_Trash, danger = true, onClick = {
                        Repo.deleteNotification(n.id)
                        deleteTarget = null
                        toast = "اعلان حذف شد"
                    })
                }
            }
        }
    }
}
