package com.mja.messenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mja.messenger.data.NetStatus
import com.mja.messenger.ui.theme.*

/* ------------------------------------------------------------------ */
/* Card                                                                */
/* ------------------------------------------------------------------ */

@Composable
fun MjaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevated: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (elevated) MjaElevated else MjaCard)
            .border(1.dp, if (elevated) MjaBorder else MjaBorder.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(contentPadding),
        content = content,
    )
}

/* ------------------------------------------------------------------ */
/* Top app bar                                                         */
/* ------------------------------------------------------------------ */

/**
 * Standard MJA screen header. RTL: back arrow sits on the right side.
 */
@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    subtitle: String? = null,
    showLogo: Boolean = false,
    titleCentered: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            MjaIconButton(icon = Icons_Back, onClick = onBack, contentDescription = "بازگشت")
            Spacer(Modifier.width(6.dp))
        } else if (showLogo) {
            MjaLogo(size = 30.dp, modifier = Modifier.padding(end = 2.dp))
            Spacer(Modifier.width(9.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MjaTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = if (titleCentered) Modifier.fillMaxWidth() else Modifier,
                textAlign = if (titleCentered) TextAlign.Center else TextAlign.Start,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MjaTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), content = actions)
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MjaTextSecondary,
            modifier = Modifier.weight(1f),
        )
        if (actionText != null && onAction != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.bodySmall,
                color = MjaAccent,
                modifier = Modifier.clickable { onAction() }.padding(4.dp),
            )
        }
    }
}

/* ------------------------------------------------------------------ */
/* Logo                                                                */
/* ------------------------------------------------------------------ */

@Composable
fun MjaLogo(size: Dp = 48.dp, modifier: Modifier = Modifier) {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(com.mja.messenger.R.drawable.ic_mja_logo),
        contentDescription = "MJA",
        modifier = modifier.size(size),
    )
}

/** Silver metallic gradient text used for the MJA wordmark. */
@Composable
fun MjaWordmark(
    text: String = "MJA",
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.displaySmall,
) {
    val brush = MjaSilverGradient.first()
    Text(
        text = text,
        style = style,
        fontWeight = FontWeight.Bold,
        color = brush,
        modifier = modifier,
    )
}

/* ------------------------------------------------------------------ */
/* Avatar                                                              */
/* ------------------------------------------------------------------ */

/**
 * Generated avatar: initials on a graphite plate — no network images needed.
 */
@Composable
fun Avatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    colorIndex: Int = 0,
    isGroup: Boolean = false,
    online: Boolean? = null,
    ringColor: Color? = null,
) {
    val palette = MjaAvatarPalette
    val bg = palette[((colorIndex % palette.size) + palette.size) % palette.size]
    val initials = name.trim().take(1).ifBlank { "M" }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(size.value / 2.7f))
                .background(bg)
                .then(
                    if (ringColor != null) Modifier.border(1.5.dp, ringColor, RoundedCornerShape(size.value / 2.7f))
                    else Modifier,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (isGroup) {
                Icon(
                    Icons_Group,
                    contentDescription = null,
                    tint = MjaBrand,
                    modifier = Modifier.size(size * 0.46f),
                )
            } else {
                Text(
                    text = initials,
                    color = MjaTextPrimary,
                    style = if (size >= 48.dp) MaterialTheme.typography.titleLarge
                    else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        if (online != null) {
            OnlineDot(
                size = (size * 0.24f).coerceAtLeast(8.dp),
                modifier = Modifier.align(Alignment.BottomStart),
                online = online,
            )
        }
    }
}

@Composable
fun OnlineDot(
    modifier: Modifier = Modifier,
    size: Dp = 10.dp,
    online: Boolean = true,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(50))
            .background(if (online) MjaOnline else MjaTextDisabled)
            .border(2.dp, MjaBackground2, RoundedCornerShape(50)),
    )
}

/* ------------------------------------------------------------------ */
/* Badge / counters                                                    */
/* ------------------------------------------------------------------ */

@Composable
fun CountBadge(
    count: Int,
    modifier: Modifier = Modifier,
    color: Color = MjaError,
    max: Int = 99,
) {
    if (count <= 0) return
    Box(
        modifier = modifier
            .height(20.dp)
            .clip(RoundedCornerShape(50))
            .background(color)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (count > max) "$max+" else count.toString(),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/* ------------------------------------------------------------------ */
/* Feedback: empty / error / loading                                   */
/* ------------------------------------------------------------------ */

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 44.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(MjaCard)
                .border(1.dp, MjaBorder, RoundedCornerShape(26.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = MjaTextDisabled, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MjaTextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MjaTextSecondary,
            textAlign = TextAlign.Center,
        )
        if (actionText != null && onAction != null) {
            Spacer(Modifier.height(18.dp))
            MjaButton(text = actionText, onClick = onAction, style = MjaButtonStyle.Secondary)
        }
    }
}

@Composable
fun ErrorState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons_ErrorOutline,
    actionText: String = "تلاش مجدد",
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MjaError.copy(alpha = 0.10f))
                .border(1.dp, MjaError.copy(alpha = 0.35f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = MjaError, modifier = Modifier.size(30.dp))
        }
        Spacer(Modifier.height(15.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, color = MjaTextPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(7.dp))
        Text(description, style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary, textAlign = TextAlign.Center)
        if (onAction != null) {
            Spacer(Modifier.height(17.dp))
            MjaButton(text = actionText, onClick = onAction)
        }
    }
}

@Composable
fun LoadingState(
    label: String = "در حال بارگذاری...",
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(30.dp),
            color = MjaBrand,
            strokeWidth = 2.5.dp,
        )
        Spacer(Modifier.height(14.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary)
    }
}

/** Skeleton placeholder rows — used while chats/contacts load. */
@Composable
fun SkeletonList(
    modifier: Modifier = Modifier,
    count: Int = 6,
    showAvatar: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        repeat(count) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showAvatar) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MjaCard),
                    )
                    Spacer(Modifier.width(12.dp))
                }
                Column(Modifier.weight(1f)) {
                    SkeletonBar(width = 0.45f)
                    Spacer(Modifier.height(8.dp))
                    SkeletonBar(width = 0.75f, height = 10.dp, alpha = 0.5f)
                }
                Spacer(Modifier.width(10.dp))
                SkeletonBar(width = 0.22f, height = 10.dp, alpha = 0.5f)
            }
        }
    }
}

@Composable
private fun SkeletonBar(width: Float, height: Dp = 13.dp, alpha: Float = 1f) {
    Box(
        modifier = Modifier
            .fillMaxWidth(width)
            .height(height)
            .clip(RoundedCornerShape(6.dp))
            .background(MjaCard)
            .alpha(alpha),
    )
}

/* ------------------------------------------------------------------ */
/* Progress                                                            */
/* ------------------------------------------------------------------ */

@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MjaBrand,
    track: Color = MjaElevated,
    height: Dp = 7.dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(track),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(color),
        )
    }
}

/* ------------------------------------------------------------------ */
/* Stat card (offline queue / network overview)                        */
/* ------------------------------------------------------------------ */

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accent: Color = MjaBrand,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MjaCard)
            .border(1.dp, MjaBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(10.dp))
        }
        Text(value, style = MaterialTheme.typography.headlineSmall, color = MjaTextPrimary)
        Spacer(Modifier.height(3.dp))
        Text(title, style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary, maxLines = 1)
    }
}

/* ------------------------------------------------------------------ */
/* Info rows                                                           */
/* ------------------------------------------------------------------ */

@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueLtr: Boolean = false,
    valueColor: Color = MjaTextPrimary,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary, modifier = Modifier.weight(1f))
        if (valueLtr) {
            LtrText(value, style = MaterialTheme.typography.bodyMedium, color = valueColor, fontWeight = FontWeight.Medium)
        } else {
            Text(value, style = MaterialTheme.typography.bodyMedium, color = valueColor, fontWeight = FontWeight.Medium)
        }
    }
}

/* ------------------------------------------------------------------ */
/* Toast / capsule message                                             */
/* ------------------------------------------------------------------ */

@Composable
fun ToastMessage(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .background(MjaElevated)
            .border(1.dp, MjaBorder, RoundedCornerShape(13.dp))
            .padding(horizontal = 16.dp, vertical = 11.dp),
    ) {
        Text(text, style = MaterialTheme.typography.bodySmall, color = MjaTextPrimary)
    }
}

/* ------------------------------------------------------------------ */
/* Dialog                                                              */
/* ------------------------------------------------------------------ */

/**
 * Confirmation / warning / information dialog built on the MJA design system.
 */
@Composable
fun MjaDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "تأیید",
    cancelText: String = "انصراف",
    destructive: Boolean = false,
    icon: ImageVector? = null,
    content: (@Composable () -> Unit)? = null,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MjaCard)
                .border(1.dp, MjaBorder, RoundedCornerShape(20.dp))
                .padding(20.dp),
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (destructive) MjaError.copy(alpha = 0.14f) else MjaElevated),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        icon, contentDescription = null,
                        tint = if (destructive) MjaError else MjaBrand,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.height(14.dp))
            }
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                color = if (destructive) MjaError else MjaTextPrimary,
            )
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MjaTextSecondary)
            if (content != null) {
                Spacer(Modifier.height(14.dp))
                content()
            }
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MjaButton(
                    text = cancelText,
                    onClick = onDismiss,
                    style = MjaButtonStyle.Secondary,
                    modifier = Modifier.weight(1f),
                )
                MjaButton(
                    text = confirmText,
                    onClick = onConfirm,
                    style = if (destructive) MjaButtonStyle.Destructive else MjaButtonStyle.Primary,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/* Bottom sheet                                                        */
/* ------------------------------------------------------------------ */

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MjaBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
        containerColor = MjaBackground2,
        contentColor = MjaTextPrimary,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 42.dp, height = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MjaBorder),
            )
        },
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            if (title != null) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MjaTextPrimary,
                    modifier = Modifier.padding(bottom = 10.dp, top = 4.dp),
                )
            }
            content()
            Spacer(Modifier.height(28.dp))
        }
    }
}

/* ------------------------------------------------------------------ */
/* Bottom navigation                                                   */
/* ------------------------------------------------------------------ */

data class BottomTab(val label: String, val icon: ImageVector)

/**
 * Bottom navigation — خانه / مخاطبین / گروه‌ها / اعلان‌ها / تنظیمات
 * Background #101216, height ~70dp, active silver, unread badge.
 */
@Composable
fun MjaBottomBar(
    currentIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    unreadCount: Int = 0,
) {
    val tabs = remember {
        listOf(
            BottomTab("خانه", Icons_Home),
            BottomTab("مخاطبین", Icons_People),
            BottomTab("گروه‌ها", Icons_Groups),
            BottomTab("اعلان‌ها", Icons_Notifications),
            BottomTab("تنظیمات", Icons_Settings),
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MjaBackground2)
            .border(1.dp, MjaBorder.copy(alpha = 0.7f), RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp))
            .height(70.dp)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = index == currentIndex
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelect(index) }
                    .padding(vertical = 9.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                BadgedBox(
                    badge = {
                        if (index == 3 && unreadCount > 0) CountBadge(unreadCount)
                    },
                ) {
                    Icon(
                        tab.icon,
                        contentDescription = tab.label,
                        tint = if (selected) MjaTextPrimary else MjaTextDisabled,
                        modifier = Modifier.size(23.dp),
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    tab.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) MjaTextPrimary else MjaTextDisabled,
                )
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/* Network status card (Home / Nearby / Network screens)              */
/* ------------------------------------------------------------------ */

@Composable
fun NetworkStatusCard(
    status: NetStatus,
    modifier: Modifier = Modifier,
    usersText: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MjaCard)
            .border(1.dp, MjaBorder, RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (status.connected) MjaSuccess.copy(alpha = 0.14f) else MjaWarning.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (status.connected) Icons_Wifi else Icons_WifiOff,
                contentDescription = null,
                tint = if (status.connected) MjaSuccess else MjaWarning,
                modifier = Modifier.size(21.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = if (status.connected) "متصل به شبکه محلی" else "اتصال برقرار نیست",
                style = MaterialTheme.typography.titleMedium,
                color = MjaTextPrimary,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = usersText ?: "${status.users} کاربر متصل",
                style = MaterialTheme.typography.bodySmall,
                color = MjaTextSecondary,
            )
        }
        StatusPill(
            text = if (status.connected) "فعال" else "غیرفعال",
            color = if (status.connected) MjaOnline else MjaWarning,
        )
    }
}
