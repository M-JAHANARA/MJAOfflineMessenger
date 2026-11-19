package com.mja.messenger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.*
import com.mja.messenger.ui.MainScaffold
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

private enum class ContactFilter(val label: String) {
    ALL("همه"), ONLINE("آنلاین"), OFFLINE("آفلاین")
}

private fun Modifier.clickableRow(onClick: () -> Unit): Modifier = this.clickable { onClick() }

/**
 * 07 — Contacts (tab 1)
 */
@Composable
fun ContactsScreen(
    onBack: (() -> Unit)? = null,
    onOpenProfile: (String) -> Unit,
    onOpenChat: (String) -> Unit,
    onOpenNearby: () -> Unit,
    onAddContact: () -> Unit,
    tab: (Int) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(ContactFilter.ALL) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) { delay(600); loading = false }

    val users by Repo.users.collectAsState()
    val chats by Repo.chats.collectAsState()
    val unread = chats.sumOf { it.unread }

    val me = Repo.currentUser()
    val others = remember(users) { users.filter { !it.isSelf } }

    val filtered = remember(others, query, filter) {
        others
            .filter {
                query.isBlank() ||
                    it.name.contains(query, ignoreCase = true) ||
                    it.username.contains(query, ignoreCase = true)
            }
            .filter {
                when (filter) {
                    ContactFilter.ALL -> true
                    ContactFilter.ONLINE -> it.online
                    ContactFilter.OFFLINE -> !it.online
                }
            }
            .sortedWith(compareByDescending<User> { it.online }.thenBy { it.name })
    }

    MainScaffold(current = 1, onTab = tab, unreadCount = unread) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            ScreenHeader(title = "مخاطبین") {
                MjaIconButton(
                    icon = Icons_Nearby,
                    onClick = onOpenNearby,
                    contentDescription = "افراد نزدیک",
                )
                MjaIconButton(
                    icon = Icons_AddPerson,
                    onClick = onAddContact,
                    contentDescription = "افزودن مخاطب",
                )
            }

            Column(Modifier.padding(horizontal = 20.dp)) {
                SearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "جست‌وجو در مخاطبین",
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "${others.count { it.online }} کاربر آنلاین از ${others.size} کاربر در شبکه محلی",
                    style = MaterialTheme.typography.labelSmall,
                    color = MjaTextSecondary,
                )
                Spacer(Modifier.height(10.dp))
                MjaTabs(
                    tabs = ContactFilter.entries.map { it.label },
                    selectedIndex = filter.ordinal,
                    onSelect = { filter = ContactFilter.entries[it] },
                )
                Spacer(Modifier.height(6.dp))
            }

            when {
                loading -> SkeletonList(count = 7)

                filtered.isEmpty() && query.isBlank() -> EmptyState(
                    icon = Icons_People,
                    title = "کاربری در شبکه پیدا نشد.",
                    description = "افراد نزدیک را از طریق کشف خودکار شبکه محلی پیدا کنید.",
                )

                filtered.isEmpty() -> EmptyState(
                    icon = Icons_Search,
                    title = "نتیجه‌ای پیدا نشد.",
                    description = "برای «$query» مخاطبی وجود ندارد.",
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                ) {
                    items(filtered, key = { it.id }) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickableRow { onOpenProfile(user.id) }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Avatar(
                                name = user.name,
                                size = 48.dp,
                                colorIndex = user.avatarColor,
                                online = user.online,
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    user.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MjaTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    LtrText(
                                        "@${user.username}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MjaTextDisabled,
                                    )
                                    if (user.online) {
                                        Spacer(Modifier.width(8.dp))
                                        OnlineDot(size = 7.dp, online = true)
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "آنلاین",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MjaOnline,
                                        )
                                    } else {
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            user.lastSeen.ifBlank { "آفلاین" },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MjaTextSecondary,
                                        )
                                    }
                                }
                            }
                            MjaIconButton(
                                icon = Icons_Chat,
                                onClick = { onOpenChat(Repo.openChatWith(user.id)) },
                                contentDescription = "پیام",
                                background = if (user.online) MjaCard else MjaElevated,
                            )
                        }
                        HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                    }
                    item { Spacer(Modifier.height(12.dp)) }
                }
            }
        }
    }
}
