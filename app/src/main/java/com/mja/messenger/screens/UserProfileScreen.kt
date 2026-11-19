package com.mja.messenger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.*
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 17 — User Profile
 */
@Composable
fun UserProfileScreen(
    userId: String,
    onBack: () -> Unit,
    onMessage: (String) -> Unit,
    onVoiceCall: (String) -> Unit,
    onVideoCall: (String) -> Unit,
    onEditProfile: () -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var menuOpen by remember { mutableStateOf(false) }
    var blockDialog by remember { mutableStateOf(false) }
    var reportDialog by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(500); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2000); toast = null } }

    val users by Repo.users.collectAsState()
    val user = users.firstOrNull { it.id == userId }
    val isSelf = user?.isSelf == true || userId == Repo.currentUser().id
    val clipboard = LocalClipboardManager.current

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            ScreenHeader(
                title = if (isSelf) "پروفایل من" else "پروفایل کاربر",
                onBack = onBack,
            ) {
                if (!isSelf) {
                    MjaIconButton(icon = Icons_More, onClick = { menuOpen = true }, contentDescription = "بیشتر")
                }
            }

            when {
                loading -> Column(Modifier.padding(20.dp)) { SkeletonList(count = 5, showAvatar = false) }

                user == null -> ErrorState(
                    title = "کاربر پیدا نشد.",
                    description = "این کاربر دیگر در شبکه محلی فعال نیست.",
                    onAction = onBack,
                )

                else -> Column(Modifier.padding(horizontal = 20.dp)) {
                    // Avatar + identity
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Avatar(
                            user.name,
                            size = 110.dp,
                            colorIndex = user.avatarColor,
                            online = user.online,
                        )
                        Spacer(Modifier.height(14.dp))
                        Text(user.name, style = MaterialTheme.typography.headlineSmall, color = MjaTextPrimary)
                        Spacer(Modifier.height(4.dp))
                        LtrText(
                            "@${user.username}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MjaTextSecondary,
                        )
                        Spacer(Modifier.height(10.dp))
                        StatusPill(
                            if (user.online) "آنلاین" else user.lastSeen.ifBlank { "آفلاین" },
                            color = if (user.online) MjaOnline else MjaTextDisabled,
                        )
                        if (user.bio.isNotBlank()) {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                user.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MjaTextSecondary,
                                textAlign = TextAlign.Center,
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    // Info card
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            InfoRow("آخرین حضور", if (user.online) "همین حالا" else user.lastSeen.ifBlank { "نامشخص" })
                            if (user.deviceName.isNotBlank()) {
                                InfoRow("دستگاه", user.deviceName, valueLtr = true)
                            }
                            if (user.ip.isNotBlank()) {
                                InfoRow("آدرس IP", user.ip, valueLtr = true)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    // Actions
                    if (isSelf) {
                        MjaButton(
                            text = "ویرایش پروفایل",
                            style = MjaButtonStyle.Secondary,
                            icon = Icons_Edit,
                            onClick = onEditProfile,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                MjaButton(
                                    text = "پیام",
                                    icon = Icons_Chat,
                                    onClick = { onMessage(userId) },
                                    modifier = Modifier.weight(1f),
                                )
                                MjaButton(
                                    text = "تماس",
                                    style = MjaButtonStyle.Secondary,
                                    icon = Icons_Call,
                                    onClick = { onVoiceCall(userId) },
                                    modifier = Modifier.weight(1f),
                                )
                                MjaButton(
                                    text = "ویدئو",
                                    style = MjaButtonStyle.Secondary,
                                    icon = Icons_Video,
                                    onClick = { onVideoCall(userId) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))

                    // Shared media
                    SectionHeader("رسانه‌های مشترک", actionText = "مشاهده همه")
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        repeat(5) { i ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(58.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (i % 2 == 0) MjaElevated else MjaCard),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    when (i % 3) {
                                        0 -> Icons_Image
                                        1 -> Icons_Video
                                        else -> Icons_FileDoc
                                    },
                                    contentDescription = null,
                                    tint = MjaTextDisabled,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(28.dp))
                }
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }

        // Overflow menu
        if (menuOpen && !isSelf) {
            MjaBottomSheet(onDismiss = { menuOpen = false }, title = user?.name ?: "گزینه‌ها") {
                SettingsRow(title = "کپی نام کاربری", icon = Icons_Copy, onClick = {
                    clipboard.setText(AnnotatedString(user?.username ?: ""))
                    menuOpen = false
                    toast = "نام کاربری کپی شد"
                })
                SettingsRow(title = "مسدودسازی", icon = Icons_Block, danger = true, onClick = {
                    menuOpen = false
                    blockDialog = true
                })
                SettingsRow(title = "گزارش", icon = Icons_Report, danger = true, onClick = {
                    menuOpen = false
                    reportDialog = true
                })
            }
        }

        if (blockDialog) {
            MjaDialog(
                title = "مسدودسازی کاربر؟",
                message = "دیگر پیام‌ها و تماس‌های این کاربر را دریافت نمی‌کنید.",
                destructive = true,
                icon = Icons_Block,
                confirmText = "مسدودسازی",
                onDismiss = { blockDialog = false },
                onConfirm = { blockDialog = false; toast = "کاربر مسدود شد" },
            )
        }

        if (reportDialog) {
            MjaDialog(
                title = "گزارش کاربر؟",
                message = "گزارش شما برای بررسی ثبت می‌شود.",
                destructive = true,
                icon = Icons_Report,
                confirmText = "گزارش",
                onDismiss = { reportDialog = false },
                onConfirm = { reportDialog = false; toast = "گزارش ثبت شد" },
            )
        }
    }
}
