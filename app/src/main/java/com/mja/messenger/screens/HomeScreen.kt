package com.mja.messenger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.Chat
import com.mja.messenger.data.MsgStatus
import com.mja.messenger.data.Repo
import com.mja.messenger.data.dayLabel
import com.mja.messenger.net.LocalNet
import com.mja.messenger.navigation.Routes
import com.mja.messenger.ui.MainScaffold
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 06 — Home / Chat List  (the core screen of MJA)
 *
 * Header → network card → search → filters → chat list → FAB → bottom nav.
 */
@Composable
fun HomeScreen(
    onOpenChat: (String) -> Unit,
    onNewChat: () -> Unit,
    onOpen: (String) -> Unit,
    tab: (Int) -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var query by remember { mutableStateOf("") }
    var filterIndex by remember { mutableStateOf(0) }
    var menuOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { delay(650); loading = false }

    val chats by Repo.chats.collectAsState()
    val net by LocalNet.status.collectAsState()

    val unreadTotal = chats.sumOf { it.unread }

    val filtered = remember(chats, query, filterIndex) {
        val base = chats.sortedWith(compareByDescending<Chat> { it.pinned }.thenByDescending { it.lastTimestamp })
        val byFilter = when (filterIndex) {
            1 -> base.filter { !it.isGroup }
            2 -> base.filter { it.isGroup }
            3 -> base.filter { it.saved }
            else -> base
        }
        if (query.isBlank()) byFilter
        else byFilter.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.lastMessage.contains(query, ignoreCase = true)
        }
    }

    MainScaffold(current = 0, onTab = tab, unreadCount = unreadTotal) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            Column(Modifier.fillMaxSize()) {

                /* ---------- Header ---------- */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MjaLogo(size = 32.dp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "MJA",
                            style = MaterialTheme.typography.titleLarge,
                            color = MjaTextPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Offline Messenger",
                            style = MaterialTheme.typography.labelSmall,
                            color = MjaTextDisabled,
                        )
                    }
                    MjaIconButton(
                        icon = Icons_Search,
                        onClick = { onOpen(Routes.SEARCH) },
                        contentDescription = "جست‌وجو",
                        background = MjaCard,
                    )
                    Spacer(Modifier.width(6.dp))
                    MjaIconButton(
                        icon = Icons_More,
                        onClick = { menuOpen = true },
                        contentDescription = "بیشتر",
                        background = MjaCard,
                    )
                }

                /* ---------- Network status ---------- */
                NetworkStatusCard(
                    status = net,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    onClick = { onOpen(Routes.NETWORK) },
                )

                /* ---------- Search ---------- */
                SearchField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    placeholder = "جست‌وجو در گفتگوها",
                )

                /* ---------- Filter tabs ---------- */
                MjaTabs(
                    tabs = listOf("همه", "خصوصی", "گروه‌ها", "ذخیره‌شده‌ها"),
                    selectedIndex = filterIndex,
                    onSelect = { filterIndex = it },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                )

                Spacer(Modifier.height(6.dp))

                /* ---------- List / states ---------- */
                when {
                    loading -> SkeletonList(count = 6)

                    filtered.isEmpty() && query.isBlank() -> EmptyState(
                        icon = Icons_Chat,
                        title = "هنوز گفتگویی ندارید.",
                        description = "با کاربران حاضر در شبکه محلی گفتگو را شروع کنید.",
                        actionText = "گفتگوی جدید",
                        onAction = onNewChat,
                    )

                    filtered.isEmpty() -> EmptyState(
                        icon = Icons_Search,
                        title = "نتیجه‌ای پیدا نشد.",
                        description = "برای «$query» گفتگویی وجود ندارد.",
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 92.dp),
                    ) {
                        items(filtered, key = { it.id }) { chat ->
                            ChatRow(chat = chat, onClick = { onOpenChat(chat.id) })
                        }
                    }
                }
            }

            /* ---------- FAB ---------- */
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp)
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MjaBrand)
                    .clickable { onNewChat() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons_Edit,
                    contentDescription = "گفتگوی جدید",
                    tint = MjaBackground,
                    modifier = Modifier.size(23.dp),
                )
            }

            /* ---------- Header menu (bottom sheet) ---------- */
            if (menuOpen) {
                MjaBottomSheet(onDismiss = { menuOpen = false }, title = "منوی MJA") {
                    SettingsRow(
                        title = "افراد نزدیک",
                        subtitle = "کاربران قابل شناسایی در شبکه محلی",
                        icon = Icons_Nearby,
                        onClick = { menuOpen = false; onOpen(Routes.NEARBY) },
                    )
                    SettingsRow(
                        title = "صف پیام‌های آفلاین",
                        subtitle = "پیام‌های در انتظار ارسال",
                        icon = Icons_Queue,
                        onClick = { menuOpen = false; onOpen(Routes.QUEUE) },
                    )
                    SettingsRow(
                        title = "همگام‌سازی پیام‌ها",
                        icon = Icons_Sync,
                        onClick = { menuOpen = false; onOpen(Routes.SYNC) },
                    )
                    SettingsRow(
                        title = "فایل‌ها",
                        icon = Icons_Folder,
                        onClick = { menuOpen = false; onOpen(Routes.FILES) },
                    )
                    SettingsRow(
                        title = "جست‌وجوی پیام",
                        icon = Icons_Search,
                        onClick = { menuOpen = false; onOpen(Routes.MESSAGE_SEARCH) },
                    )
                    SettingsRow(
                        title = "مدیریت Hotspot",
                        subtitle = "میزبانی شبکه محلی",
                        icon = Icons_Wifi,
                        onClick = { menuOpen = false; onOpen(Routes.HOTSPOT) },
                    )
                    SettingsRow(
                        title = "درباره MJA",
                        icon = Icons_InfoOutlined,
                        onClick = { menuOpen = false; onOpen(Routes.ABOUT) },
                    )
                }
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/* Chat row                                                            */
/* ------------------------------------------------------------------ */

@Composable
private fun ChatRow(
    chat: Chat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lastStatus = remember(chat.id) {
        Repo.messagesFor(chat.id).lastOrNull()?.status
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(
            name = chat.title,
            size = 52.dp,
            colorIndex = chat.avatarColor,
            isGroup = chat.isGroup,
            online = if (chat.isGroup) null else chat.online,
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = chat.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MjaTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (chat.muted) {
                    Spacer(Modifier.width(5.dp))
                    Icon(
                        Icons_NotificationsOff,
                        contentDescription = "بی‌صدا",
                        tint = MjaTextDisabled,
                        modifier = Modifier.size(13.dp),
                    )
                }
                if (chat.pinned) {
                    Spacer(Modifier.width(5.dp))
                    Icon(
                        Icons_Pin,
                        contentDescription = "سنجاق‌شده",
                        tint = MjaTextDisabled,
                        modifier = Modifier.size(13.dp),
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (chat.typing) {
                    Text(
                        text = chat.isTypingGroup.ifBlank { "در حال نوشتن..." },
                        style = MaterialTheme.typography.bodySmall,
                        color = MjaSuccess,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                } else {
                    if (chat.lastMessage.isNotBlank() && lastStatus != null && lastStatus != MsgStatus.READ) {
                        DeliveryTicks(
                            status = lastStatus,
                            modifier = Modifier.padding(end = 4.dp),
                        )
                    }
                    Text(
                        text = chat.lastMessage.ifBlank { "پیامی وجود ندارد" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MjaTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Text(
                text = dayLabel(chat.lastTimestamp),
                style = MaterialTheme.typography.labelSmall,
                color = if (chat.unread > 0) MjaAccent else MjaTextDisabled,
            )
            if (chat.unread > 0) {
                CountBadge(chat.unread)
            }
        }
    }
}
