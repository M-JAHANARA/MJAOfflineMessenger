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
 * 13 — Message Search
 */
@Composable
fun MessageSearchScreen(onBack: () -> Unit, onOpenChat: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }
    var scopeOpen by remember { mutableStateOf(false) }
    var dateOpen by remember { mutableStateOf(false) }
    var scope by remember { mutableStateOf("همه مخاطبین") }
    var dateFilter by remember { mutableStateOf("هر زمان") }

    LaunchedEffect(query, scope, dateFilter) {
        if (query.isNotBlank()) {
            searching = true
            delay(250)
            searching = false
        } else searching = false
    }

    val messages by Repo.messages.collectAsState()
    val chats by Repo.chats.collectAsState()
    val q = query.trim()

    val results = remember(messages, q, scope, dateFilter) {
        if (q.isBlank()) emptyList() else messages
            .filter { m ->
                if (!m.text.contains(q, true)) return@filter false
                val chat = chats.firstOrNull { it.id == m.chatId }
                when (scope) {
                    "گروه‌ها" -> chat?.isGroup == true
                    "همه مخاطبین" -> true
                    else -> true
                } && when (dateFilter) {
                    "امروز" -> m.dateLabel == "امروز"
                    "دیروز" -> m.dateLabel == "دیروز"
                    else -> true
                }
            }
            .sortedByDescending { it.timestamp }
    }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(title = "جست‌وجوی پیام", onBack = onBack)

            Column(Modifier.padding(horizontal = 20.dp)) {
                SearchField(value = query, onValueChange = { query = it }, placeholder = "عبارت مورد نظر...")
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MjaChip(text = scope, selected = true, onClick = { scopeOpen = true }, icon = Icons_Filter)
                    MjaChip(text = dateFilter, selected = dateFilter != "هر زمان", onClick = { dateOpen = true }, icon = Icons_Schedule)
                    if (scope != "همه مخاطبین" || dateFilter != "هر زمان") {
                        MjaChip(text = "پاک کردن", onClick = { scope = "همه مخاطبین"; dateFilter = "هر زمان" }, icon = Icons_Close)
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            when {
                query.isBlank() -> EmptyState(
                    icon = Icons_Search,
                    title = "جست‌وجو در پیام‌ها",
                    description = "عبارتی که می‌جوهید تایپ کنید تا در همه گفتگوها جست‌وجو شود.",
                )

                searching -> SkeletonList(count = 6, showAvatar = false)

                results.isEmpty() -> EmptyState(
                    icon = Icons_Search,
                    title = "پیامی با این عبارت پیدا نشد.",
                    description = "عبارت دیگری را امتحان کنید.",
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                ) {
                    item {
                        Text(
                            "${results.size} نتیجه",
                            style = MaterialTheme.typography.labelSmall,
                            color = MjaTextSecondary,
                            modifier = Modifier.padding(vertical = 6.dp),
                        )
                    }
                    items(results, key = { it.id }) { m ->
                        val chat = chats.firstOrNull { it.id == m.chatId }
                        val sender = Repo.user(m.senderId)
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onOpenChat(m.chatId) }.padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Avatar(
                                chat?.title ?: sender?.name ?: "گفتگو",
                                size = 44.dp,
                                isGroup = chat?.isGroup == true,
                                colorIndex = chat?.avatarColor ?: 0,
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    chat?.title ?: "گفتگو",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MjaBrand,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    m.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MjaTextPrimary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${m.dateLabel} · ${timeLabel(m.timestamp)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MjaTextDisabled,
                                )
                            }
                            Icon(Icons_Chevron, contentDescription = null, tint = MjaTextDisabled, modifier = Modifier.size(18.dp))
                        }
                        HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }

        if (scopeOpen) {
            MjaBottomSheet(onDismiss = { scopeOpen = false }, title = "محدوده جست‌وجو") {
                listOf("همه مخاطبین", "گروه‌ها", "خصوصی").forEach { s ->
                    SettingsRow(
                        title = s,
                        icon = if (s == "گروه‌ها") Icons_People else Icons_Person,
                        onClick = { scope = s; scopeOpen = false },
                        trailing = {
                            if (scope == s) Icon(Icons_Check, contentDescription = "انتخاب‌شده", tint = MjaBrand)
                        },
                    )
                }
            }
        }

        if (dateOpen) {
            MjaBottomSheet(onDismiss = { dateOpen = false }, title = "بازه زمانی") {
                listOf("هر زمان", "امروز", "دیروز", "این هفته").forEach { d ->
                    SettingsRow(
                        title = d,
                        icon = Icons_Schedule,
                        onClick = { dateFilter = d; dateOpen = false },
                        trailing = {
                            if (dateFilter == d) Icon(Icons_Check, contentDescription = "انتخاب‌شده", tint = MjaBrand)
                        },
                    )
                }
            }
        }
    }
}
