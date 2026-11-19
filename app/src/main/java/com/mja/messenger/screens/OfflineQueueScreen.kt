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
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

private enum class QueueTab(val label: String) {
    ALL("همه"), PENDING("در انتظار"), DELIVERED("تحویل‌شده"), FAILED("ناموفق")
}

/**
 * 27 — Offline Queue (flagship)
 */
@Composable
fun OfflineQueueScreen(onBack: () -> Unit, onOpenChat: (String) -> Unit) {
    var tab by remember { mutableStateOf(QueueTab.ALL) }
    var loading by remember { mutableStateOf(true) }
    var menuFor by remember { mutableStateOf<QueueItem?>(null) }
    var detailFor by remember { mutableStateOf<QueueItem?>(null) }
    var deleteFor by remember { mutableStateOf<QueueItem?>(null) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(600); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2000); toast = null } }

    val queue by Repo.queue.collectAsState()

    val pendingCount = queue.count { it.status == MsgStatus.PENDING || it.status == MsgStatus.SENDING }
    val deliveredCount = queue.count { it.status == MsgStatus.DELIVERED || it.status == MsgStatus.READ }
    val failedCount = queue.count { it.status == MsgStatus.FAILED }

    val filtered = remember(queue, tab) {
        when (tab) {
            QueueTab.ALL -> queue
            QueueTab.PENDING -> queue.filter { it.status == MsgStatus.PENDING || it.status == MsgStatus.SENDING }
            QueueTab.DELIVERED -> queue.filter { it.status == MsgStatus.DELIVERED || it.status == MsgStatus.READ }
            QueueTab.FAILED -> queue.filter { it.status == MsgStatus.FAILED }
        }
    }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(title = "صف پیام‌های آفلاین", onBack = onBack) {
                MjaIconButton(
                    icon = Icons_Refresh,
                    onClick = { Repo.retryAllQueue(); toast = "تلاش مجدد برای همه پیام‌ها" },
                    contentDescription = "تلاش مجدد همه",
                )
            }

            Column(Modifier.padding(horizontal = 20.dp)) {
                // Intro card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MjaElevated)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons_InfoOutlined, contentDescription = null, tint = MjaAccent, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "پیام‌های شما تا زمان اتصال مجدد گیرنده در سرور محلی نگهداری می‌شوند.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MjaTextSecondary,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
                    )
                }
                Spacer(Modifier.height(12.dp))

                // Stats
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        title = "در انتظار",
                        value = "$pendingCount",
                        icon = Icons_Schedule,
                        accent = MjaWarning,
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        title = "تحویل‌شده",
                        value = "$deliveredCount",
                        icon = Icons_CheckCircle,
                        accent = MjaSuccess,
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        title = "ناموفق",
                        value = "$failedCount",
                        icon = Icons_ErrorOutline,
                        accent = MjaError,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(12.dp))

                MjaTabs(
                    tabs = QueueTab.entries.map { it.label },
                    selectedIndex = tab.ordinal,
                    onSelect = { tab = QueueTab.entries[it] },
                )
                Spacer(Modifier.height(8.dp))
            }

            when {
                loading -> SkeletonList(count = 5, showAvatar = false)

                filtered.isEmpty() -> EmptyState(
                    icon = Icons_Queue,
                    title = "هیچ پیامی در صف انتظار نیست.",
                    description = "پیام‌های ارسال‌نشده هنگام قطع اتصال اینجا جمع می‌شوند.",
                    actionText = "ارسال پیام",
                    onAction = onBack,
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                ) {
                    items(filtered, key = { it.id }) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MjaCard)
                                .clickable { onOpenChat(item.chatId) }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    item.recipient,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MjaTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    item.preview,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MjaTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    StatusPill(
                                        when (item.status) {
                                            MsgStatus.PENDING -> "در انتظار"
                                            MsgStatus.SENDING -> "در حال ارسال"
                                            MsgStatus.SENT -> "ارسال شد"
                                            MsgStatus.DELIVERED -> "تحویل شد"
                                            MsgStatus.READ -> "خوانده شد"
                                            MsgStatus.FAILED -> "ناموفق"
                                        },
                                        color = when (item.status) {
                                            MsgStatus.PENDING, MsgStatus.SENDING -> MjaWarning
                                            MsgStatus.FAILED -> MjaError
                                            MsgStatus.DELIVERED, MsgStatus.READ -> MjaSuccess
                                            else -> MjaTextSecondary
                                        },
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        dayLabel(item.createdAt),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MjaTextDisabled,
                                    )
                                }
                                if (item.status == MsgStatus.FAILED && item.error != null) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        item.error,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MjaError,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            MjaIconButton(
                                icon = Icons_More,
                                onClick = { menuFor = item },
                                contentDescription = "گزینه‌ها",
                                size = 34.dp,
                                iconSize = 18.dp,
                                background = MjaElevated,
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }

        // Row menu
        menuFor?.let { item ->
            MjaBottomSheet(onDismiss = { menuFor = null }, title = item.recipient) {
                SettingsRow(title = "تلاش مجدد", icon = Icons_Refresh, onClick = {
                    Repo.retryQueueItem(item.id)
                    menuFor = null
                    toast = "در حال تلاش مجدد..."
                })
                SettingsRow(title = "مشاهده جزئیات", icon = Icons_InfoOutlined, onClick = {
                    detailFor = item
                    menuFor = null
                })
                SettingsRow(title = "حذف پیام", icon = Icons_Trash, danger = true, onClick = {
                    deleteFor = item
                    menuFor = null
                })
            }
        }

        // Detail dialog
        detailFor?.let { item ->
            MjaDialog(
                title = "جزئیات پیام",
                message = buildString {
                    append("گیرنده: ${item.recipient}\n")
                    append("متن: ${item.preview}\n")
                    append("زمان: ${dayLabel(item.createdAt)} - ${timeLabel(item.createdAt)}\n")
                    append("وضعیت: ")
                    append(
                        when (item.status) {
                            MsgStatus.PENDING -> "در انتظار"
                            MsgStatus.SENDING -> "در حال ارسال"
                            MsgStatus.SENT -> "ارسال شد"
                            MsgStatus.DELIVERED -> "تحویل شد"
                            MsgStatus.READ -> "خوانده شد"
                            MsgStatus.FAILED -> "ناموفق"
                        },
                    )
                    if (item.status == MsgStatus.FAILED) {
                        append("\n\nعلت خطا: گیرنده در شبکه محلی حاضر نیست")
                    }
                },
                confirmText = "تلاش مجدد",
                cancelText = "بستن",
                onDismiss = { detailFor = null },
                onConfirm = {
                    Repo.retryQueueItem(item.id)
                    detailFor = null
                    toast = "در حال تلاش مجدد..."
                },
            )
        }

        // Delete confirm
        deleteFor?.let { item ->
            MjaDialog(
                title = "حذف پیام؟",
                message = "این پیام از صف حذف می‌شود.",
                destructive = true,
                icon = Icons_Trash,
                onDismiss = { deleteFor = null },
                onConfirm = {
                    Repo.removeQueueItem(item.id)
                    deleteFor = null
                    toast = "پیام حذف شد"
                },
            )
        }
    }
}
