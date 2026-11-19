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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.*
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 16 — Group Info
 */
@Composable
fun GroupInfoScreen(
    chatId: String,
    onBack: () -> Unit,
    onOpenMember: (String) -> Unit,
    onOpenMedia: () -> Unit,
    onLeave: () -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var addMembersOpen by remember { mutableStateOf(false) }
    var leaveConfirm by remember { mutableStateOf(false) }
    var removeTarget by remember { mutableStateOf<User?>(null) }
    var notifOn by remember { mutableStateOf(true) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(500); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2000); toast = null } }

    val chats by Repo.chats.collectAsState()
    val users by Repo.users.collectAsState()
    val chat = chats.firstOrNull { it.id == chatId }
    val me = Repo.currentUser()
    val members = remember(chat, users) { (chat?.memberIds ?: emptyList()).mapNotNull { id -> users.firstOrNull { it.id == id } } }
    val admins = remember(chat) { chat?.admins ?: emptyList() }
    val notInGroup = remember(members, users) { users.filter { !it.isSelf && members.none { m -> m.id == it.id } } }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        when {
            loading -> Column(Modifier.fillMaxSize()) {
                ScreenHeader(title = "اطلاعات گروه", onBack = onBack)
                SkeletonList(count = 6, showAvatar = false)
            }

            chat == null -> Column(Modifier.fillMaxSize()) {
                ScreenHeader(title = "اطلاعات گروه", onBack = onBack)
                ErrorState(title = "گروه پیدا نشد.", description = "این گروه حذف یا ترک شده است.", onAction = onBack)
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                item {
                    ScreenHeader(title = "اطلاعات گروه", onBack = onBack)
                }

                // Hero
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Avatar(chat.title, isGroup = true, size = 96.dp, colorIndex = chat.avatarColor)
                        Spacer(Modifier.height(12.dp))
                        Text(chat.title, style = MaterialTheme.typography.headlineSmall, color = MjaTextPrimary)
                        if (chat.description.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                chat.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MjaTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatusPill("${members.size} عضو", color = MjaBrand)
                            if (chat.groupId.isNotBlank()) {
                                StatusPill("شناسه: ${chat.groupId}", color = MjaTextSecondary)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }

                // Media
                item {
                    Column(Modifier.padding(horizontal = 20.dp)) {
                        SectionHeader("رسانه‌های مشترک", actionText = "مشاهده همه", onAction = onOpenMedia)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            repeat(3) { i ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(74.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MjaElevated)
                                        .clickable { onOpenMedia() },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        if (i == 1) Icons_Video else Icons_Image,
                                        contentDescription = null,
                                        tint = MjaTextDisabled,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                    }
                }

                // Admins
                item {
                    Column(Modifier.padding(horizontal = 20.dp)) {
                        SectionHeader("مدیران")
                        Spacer(Modifier.height(4.dp))
                    }
                }
                items(members.filter { it.id in admins }, key = { "admin_${it.id}" }) { u ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Avatar(u.name, size = 44.dp, colorIndex = u.avatarColor, online = u.online)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(u.name, style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary)
                            Spacer(Modifier.height(2.dp))
                            LtrText("@${u.username}", style = MaterialTheme.typography.bodySmall, color = MjaTextDisabled)
                        }
                        StatusPill("مدیر", color = MjaWarning)
                    }
                }

                // Members
                item {
                    Column(Modifier.padding(horizontal = 20.dp)) {
                        Spacer(Modifier.height(12.dp))
                        SectionHeader("اعضا", actionText = "افزودن", onAction = { addMembersOpen = true })
                        Spacer(Modifier.height(4.dp))
                    }
                }
                items(members.filter { it.id !in admins || it.id != me.id }, key = { it.id }) { u ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Avatar(u.name, size = 44.dp, colorIndex = u.avatarColor, online = u.online)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (u.isSelf) "${u.name} (شما)" else u.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MjaTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(Modifier.height(2.dp))
                            LtrText("@${u.username}", style = MaterialTheme.typography.bodySmall, color = MjaTextDisabled)
                        }
                        if (!u.isSelf && u.id in admins) {
                            StatusPill("مدیر", color = MjaWarning)
                            Spacer(Modifier.width(6.dp))
                        }
                        if (!u.isSelf) {
                            TextButton(onClick = { removeTarget = u }) {
                                Text("حذف", style = MaterialTheme.typography.labelLarge, color = MjaError)
                            }
                        }
                    }
                }

                // Settings
                item {
                    Column(Modifier.padding(horizontal = 20.dp)) {
                        Spacer(Modifier.height(12.dp))
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            SwitchRow(
                                title = "اعلان‌های گروه",
                                checked = notifOn,
                                onCheckedChange = { notifOn = it },
                                icon = Icons_Notifications,
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        MjaCard(modifier = Modifier.fillMaxWidth()) {
                            SettingsRow(
                                title = "خروج از گروه",
                                icon = Icons_Exit,
                                danger = true,
                                onClick = { leaveConfirm = true },
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }

        // Add members sheet
        if (addMembersOpen) {
            MjaBottomSheet(onDismiss = { addMembersOpen = false }, title = "افزودن عضو") {
                if (notInGroup.isEmpty()) {
                    EmptyState(
                        icon = Icons_People,
                        title = "همه کاربران عضو هستند.",
                        description = "کاربر دیگری در شبکه محلی شناسایی نشده است.",
                    )
                } else {
                    var picked by remember { mutableStateOf(setOf<String>()) }
                    Column {
                        notInGroup.forEach { u ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Checkbox(
                                    checked = u.id in picked,
                                    onCheckedChange = {
                                        picked = if (u.id in picked) picked - u.id else picked + u.id
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = MjaBrand),
                                )
                                Spacer(Modifier.width(4.dp))
                                Avatar(u.name, size = 40.dp, colorIndex = u.avatarColor, online = u.online)
                                Spacer(Modifier.width(10.dp))
                                Text(u.name, style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        MjaButton(
                            text = "افزودن ${picked.size} عضو",
                            onClick = {
                                if (picked.isNotEmpty()) {
                                    Repo.addMembers(chatId, picked.toList())
                                    addMembersOpen = false
                                    toast = "اعضا اضافه شدند"
                                }
                            },
                            enabled = picked.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }

        // Remove confirm
        removeTarget?.let { u ->
            MjaDialog(
                title = "حذف ${u.name}؟",
                message = "این عضو از گروه حذف می‌شود.",
                destructive = true,
                icon = Icons_RemoveUser,
                onDismiss = { removeTarget = null },
                onConfirm = {
                    Repo.removeMember(chatId, u.id)
                    removeTarget = null
                    toast = "عضو حذف شد"
                },
            )
        }

        // Leave confirm
        if (leaveConfirm) {
            MjaDialog(
                title = "خروج از گروه؟",
                message = "دیگر پیام‌های این گروه را دریافت نمی‌کنید.",
                destructive = true,
                icon = Icons_Exit,
                confirmText = "خروج",
                onDismiss = { leaveConfirm = false },
                onConfirm = {
                    leaveConfirm = false
                    Repo.leaveGroup(chatId)
                    onLeave()
                },
            )
        }
    }
}
