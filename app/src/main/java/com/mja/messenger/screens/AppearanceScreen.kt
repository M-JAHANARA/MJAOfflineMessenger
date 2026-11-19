package com.mja.messenger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.Repo
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 35 — Appearance
 */
@Composable
fun AppearanceScreen(onBack: () -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(400); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2000); toast = null } }

    val settings by Repo.settings.collectAsState()

    // Local preference states
    var textSize by remember(settings.textSize) { mutableStateOf(settings.textSize) }
    var bubbleStyle by remember { mutableStateOf("گوشه گرد") }
    var listDensity by remember { mutableStateOf("استاندارد") }
    var autoDownload by remember { mutableStateOf(true) }
    var showPreview by remember { mutableStateOf(true) }
    var imageQuality by remember { mutableStateOf("همیشه با کیفیت") }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        when {
            loading -> Column(Modifier.fillMaxSize()) {
                ScreenHeader(title = "ظاهر برنامه", onBack = onBack)
                SkeletonList(count = 6, showAvatar = false)
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                ScreenHeader(title = "ظاهر برنامه", onBack = onBack)

                Column(Modifier.padding(horizontal = 20.dp)) {
                    /* Preview */
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text("پیش‌نمایش", style = MaterialTheme.typography.labelSmall, color = MjaTextDisabled)
                            Spacer(Modifier.height(12.dp))
                            // Mini mock
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MjaBackground)
                                    .padding(14.dp),
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(50)).background(MjaElevated))
                                        Spacer(Modifier.width(8.dp))
                                        Text("علی محمدی", style = MaterialTheme.typography.labelLarge, color = MjaTextPrimary)
                                        Spacer(Modifier.weight(1f))
                                        OnlineDot(size = 7.dp, online = true)
                                    }
                                    Spacer(Modifier.height(14.dp))
                                    // Incoming bubble
                                    Box(
                                        modifier = Modifier
                                            .widthIn(max = 190.dp)
                                            .clip(RoundedCornerShape(if (bubbleStyle == "گوشه گرد") 14.dp else 4.dp))
                                            .background(MjaBubbleIn)
                                            .padding(horizontal = 10.dp, vertical = 7.dp),
                                    ) {
                                        Text("سلام، خبری از پروژه هست؟", style = MaterialTheme.typography.bodySmall, color = MjaTextPrimary)
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    // Outgoing bubble (aligned end)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        Box(
                                            modifier = Modifier
                                                .widthIn(max = 190.dp)
                                                .clip(RoundedCornerShape(if (bubbleStyle == "گوشه گرد") 14.dp else 4.dp))
                                                .background(MjaBubbleOut)
                                                .padding(horizontal = 10.dp, vertical = 7.dp),
                                        ) {
                                            Text("بله، آماده تحویل است ✅", style = MaterialTheme.typography.bodySmall, color = MjaTextPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(18.dp))

                    /* Theme */
                    SectionHeader("تم برنامه")
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ThemeCard(
                            name = "Black & Graphite",
                            selected = settings.theme == "Black & Graphite",
                            modifier = Modifier.weight(1f),
                        ) {
                            Repo.updateSettings { it.copy(theme = "Black & Graphite") }
                            toast = "تم Black & Graphite انتخاب شد"
                        }
                        ThemeCard(
                            name = "مشکی مات",
                            selected = settings.theme == "مشکی مات",
                            modifier = Modifier.weight(1f),
                        ) {
                            Repo.updateSettings { it.copy(theme = "مشکی مات") }
                            toast = "تم مشکی مات انتخاب شد"
                        }
                        ThemeCard(
                            name = "خاکستری تیره",
                            selected = settings.theme == "خاکستری تیره",
                            modifier = Modifier.weight(1f),
                        ) {
                            Repo.updateSettings { it.copy(theme = "خاکستری تیره") }
                            toast = "تم خاکستری تیره انتخاب شد"
                        }
                    }
                    Spacer(Modifier.height(18.dp))

                    /* Font size */
                    SectionHeader("اندازه فونت")
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("کوچک", "پیش‌فرض", "بزرگ").forEach { s ->
                            MjaChip(
                                text = s,
                                selected = textSize == s,
                                onClick = {
                                    textSize = s
                                    Repo.updateSettings { it.copy(textSize = s) }
                                },
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))

                    /* Bubble style + density */
                    SectionHeader("سبک حباب پیام")
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("گوشه گرد", "گوشه تیز").forEach { s ->
                            MjaChip(text = s, selected = bubbleStyle == s, onClick = { bubbleStyle = s })
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    SectionHeader("تراکم فهرست گفتگو")
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("فرود", "استاندارد", "متراکم").forEach { s ->
                            MjaChip(text = s, selected = listDensity == s, onClick = { listDensity = s })
                        }
                    }
                    Spacer(Modifier.height(18.dp))

                    /* Images */
                    SectionHeader("تنظیمات تصاویر")
                    Spacer(Modifier.height(6.dp))
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            SwitchRow(
                                title = "دانلود خودکار تصاویر",
                                description = "در شبکه محلی، بدون تأیید",
                                icon = Icons_Image,
                                checked = autoDownload,
                                onCheckedChange = { autoDownload = it },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SwitchRow(
                                title = "نمایش پیش‌نمایش تصویر",
                                description = "بندانگشتی در فهرست چت",
                                icon = Icons_Grid,
                                checked = showPreview,
                                onCheckedChange = { showPreview = it },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp)) {
                                Text("کیفیت تصویر", style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary)
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("همیشه با کیفیت", "صرفه‌جویی").forEach { q ->
                                        MjaChip(
                                            text = q,
                                            selected = imageQuality == q,
                                            onClick = { imageQuality = q },
                                        )
                                    }
                                }
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
    }
}

@Composable
private fun ThemeCard(
    name: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MjaCard)
            .border(
                1.5.dp,
                if (selected) MjaBrand else MjaBorder,
                RoundedCornerShape(14.dp),
            )
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Mini swatch
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MjaBackground),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(5.dp)
                    .width(34.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MjaBubbleOut),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(5.dp)
                    .width(34.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MjaBubbleIn),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            name,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MjaTextPrimary else MjaTextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (selected) {
            Spacer(Modifier.height(4.dp))
            Icon(Icons_Check, contentDescription = "انتخاب‌شده", tint = MjaBrand, modifier = Modifier.size(14.dp))
        }
    }
}
