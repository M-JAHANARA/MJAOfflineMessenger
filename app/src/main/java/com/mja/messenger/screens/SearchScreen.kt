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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.*
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 09 — Search (unified local search)
 */
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenGroup: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }

    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            searching = true
            delay(250)
            searching = false
        } else {
            searching = false
        }
    }

    val users by Repo.users.collectAsState()
    val chats by Repo.chats.collectAsState()
    val messages by Repo.messages.collectAsState()
    val files by Repo.files.collectAsState()

    val me = Repo.currentUser()
    val q = query.trim()

    val matchedUsers = remember(users, q) {
        if (q.isBlank()) emptyList() else users.filter {
            !it.isSelf && (it.name.contains(q, true) || it.username.contains(q, true))
        }
    }
    val matchedGroups = remember(chats, q) {
        if (q.isBlank()) emptyList() else chats.filter { it.isGroup && it.title.contains(q, true) }
    }
    val matchedChats = remember(chats, q) {
        if (q.isBlank()) emptyList() else chats.filter { !it.isGroup && it.title.contains(q, true) }
    }
    val matchedMessages = remember(messages, q) {
        if (q.isBlank()) emptyList() else messages.filter { it.text.contains(q, true) }.sortedByDescending { it.timestamp }.take(8)
    }
    val matchedFiles = remember(files, q) {
        if (q.isBlank()) emptyList() else files.filter { it.name.contains(q, true) }
    }

    val hasResults = matchedUsers.isNotEmpty() || matchedGroups.isNotEmpty() ||
        matchedChats.isNotEmpty() || matchedMessages.isNotEmpty() || matchedFiles.isNotEmpty()

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(Modifier.fillMaxSize()) {
            // Header with inline search field
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MjaIconButton(icon = Icons_Back, onClick = onBack, contentDescription = "بازگشت")
                Spacer(Modifier.width(4.dp))
                SearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "جست‌وجو در مخاطبین، گفتگوها، پیام‌ها...",
                    modifier = Modifier.weight(1f),
                )
            }

            when {
                query.isBlank() -> {
                    // Recent searches
                    Column(Modifier.padding(horizontal = 20.dp)) {
                        SectionHeader("جست‌وجوهای اخیر")
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("جلسه فردا", "گزارش", "پروژه").forEach { term ->
                                MjaChip(text = term, onClick = { query = term }, icon = Icons_History)
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "برای شروع جست‌وجو تایپ کنید. جست‌وجو کاملاً محلی است و به اینترنت نیاز ندارد.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MjaTextDisabled,
                        )
                    }
                }

                searching -> SkeletonList(count = 5)

                !hasResults -> EmptyState(
                    icon = Icons_Search,
                    title = "نتیجه‌ای پیدا نشد.",
                    description = "برای «$query» مخاطب، گفتگو یا پیامی وجود ندارد.",
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                ) {
                    if (matchedUsers.isNotEmpty()) {
                        item { SectionHeader("مخاطبین") }
                        items(matchedUsers, key = { "u_${it.id}" }) { u ->
                            ResultRow(
                                title = u.name,
                                subtitle = "@${u.username}",
                                ltr = true,
                                online = u.online,
                                onClick = { onOpenProfile(u.id) },
                            )
                        }
                    }
                    if (matchedGroups.isNotEmpty()) {
                        item { SectionHeader("گروه‌ها") }
                        items(matchedGroups, key = { "g_${it.id}" }) { c ->
                            ResultRow(
                                title = c.title,
                                subtitle = "${c.memberCount} عضو",
                                isGroup = true,
                                onClick = { onOpenGroup(c.id) },
                            )
                        }
                    }
                    if (matchedChats.isNotEmpty()) {
                        item { SectionHeader("گفتگوها") }
                        items(matchedChats, key = { "c_${it.id}" }) { c ->
                            ResultRow(
                                title = c.title,
                                subtitle = c.lastMessage,
                                online = c.online,
                                onClick = { onOpenChat(c.id) },
                            )
                        }
                    }
                    if (matchedMessages.isNotEmpty()) {
                        item { SectionHeader("پیام‌ها") }
                        items(matchedMessages, key = { "m_${it.id}" }) { m ->
                            ResultRow(
                                title = m.text.take(60),
                                subtitle = "${Repo.chat(m.chatId)?.title ?: "گفتگو"} · ${timeLabel(m.timestamp)}",
                                icon = Icons_Chat,
                                onClick = { onOpenChat(m.chatId) },
                            )
                        }
                    }
                    if (matchedFiles.isNotEmpty()) {
                        item { SectionHeader("فایل‌ها") }
                        items(matchedFiles, key = { "f_${it.id}" }) { f ->
                            ResultRow(
                                title = f.name,
                                subtitle = "${humanSize(f.size)} · ${f.sender}",
                                ltr = true,
                                icon = Icons_FileDoc,
                                onClick = { },
                            )
                        }
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ResultRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    ltr: Boolean = false,
    isGroup: Boolean = false,
    online: Boolean? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickableRow { onClick() }.padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier.size(44.dp).background(MjaElevated, androidx.compose.foundation.shape.RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = MjaTextSecondary, modifier = Modifier.size(20.dp))
            }
        } else {
            Avatar(title, size = 44.dp, isGroup = isGroup, online = online)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            if (ltr) {
                LtrText(title, style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
            } else {
                Text(title, style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(2.dp))
            if (ltr) {
                LtrText(subtitle, style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary)
            } else {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Icon(Icons_Chevron, contentDescription = null, tint = MjaTextDisabled, modifier = Modifier.size(18.dp))
    }
}

private fun Modifier.clickableRow(onClick: () -> Unit): Modifier = this.clickable { onClick() }
