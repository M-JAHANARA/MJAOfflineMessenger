package com.mja.messenger.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.*
import com.mja.messenger.ui.MainScaffold
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 14 — My Groups (tab 2)
 */
@Composable
fun GroupsScreen(
    onBack: (() -> Unit)? = null,
    onOpenGroup: (String) -> Unit,
    onCreate: () -> Unit,
    tab: (Int) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) { delay(600); loading = false }

    val chats by Repo.chats.collectAsState()
    val unread = chats.sumOf { it.unread }

    val groups = remember(chats) { chats.filter { it.isGroup } }
    val filtered = remember(groups, query) {
        groups.filter { query.isBlank() || it.title.contains(query, ignoreCase = true) }
    }

    MainScaffold(current = 2, onTab = tab, unreadCount = unread) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            ScreenHeader(title = "گروه‌ها") {
                MjaIconButton(icon = Icons_Add, onClick = onCreate, contentDescription = "ساخت گروه")
            }

            Column(Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "${groups.size} گروه · ${groups.sumOf { it.memberCount }} عضویت",
                    style = MaterialTheme.typography.labelSmall,
                    color = MjaTextSecondary,
                )
                Spacer(Modifier.height(8.dp))
                SearchField(value = query, onValueChange = { query = it }, placeholder = "جست‌وجوی گروه‌ها")
                Spacer(Modifier.height(6.dp))
            }

            when {
                loading -> SkeletonList(count = 5)

                filtered.isEmpty() && query.isBlank() -> EmptyState(
                    icon = Icons_People,
                    title = "هنوز عضو هیچ گروهی نیستید.",
                    description = "گروه بسازید و اعضای متصل به شبکه محلی را اضافه کنید.",
                    actionText = "ساخت گروه",
                    onAction = onCreate,
                )

                filtered.isEmpty() -> EmptyState(
                    icon = Icons_Search,
                    title = "نتیجه‌ای پیدا نشد.",
                    description = "گروهی با نام «$query» وجود ندارد.",
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                ) {
                    items(filtered, key = { it.id }) { chat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenGroup(chat.id) }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Avatar(
                                name = chat.title,
                                isGroup = true,
                                size = 50.dp,
                                colorIndex = chat.avatarColor,
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        chat.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MjaTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false),
                                    )
                                    if (chat.muted) {
                                        Spacer(Modifier.width(6.dp))
                                        Icon(
                                            Icons_NotificationsOff,
                                            contentDescription = "بی‌صدا",
                                            tint = MjaTextDisabled,
                                            modifier = Modifier.size(14.dp),
                                        )
                                    }
                                }
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    chat.lastMessage.ifBlank { chat.description ?: "گفتگوی خالی" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MjaTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons_People,
                                        contentDescription = null,
                                        tint = MjaTextDisabled,
                                        modifier = Modifier.size(13.dp),
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "${chat.memberCount} عضو",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MjaTextDisabled,
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        timeLabel(chat.lastTimestamp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MjaTextDisabled,
                                    )
                                }
                            }
                            if (chat.unread > 0) {
                                Spacer(Modifier.width(8.dp))
                                CountBadge(chat.unread)
                            }
                        }
                        HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                    }
                    item { Spacer(Modifier.height(12.dp)) }
                }
            }
        }
    }
}
