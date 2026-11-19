package com.mja.messenger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.*
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

private enum class MediaTab(val label: String) {
    ALL("همه"), IMAGES("تصاویر"), VIDEOS("ویدئوها"), FILES("فایل‌ها")
}

/**
 * 19 — Media Gallery
 */
@Composable
fun MediaGalleryScreen(
    chatId: String?,
    onBack: () -> Unit,
    onOpenImage: () -> Unit,
    onOpenVideo: () -> Unit,
) {
    var tab by remember { mutableStateOf(MediaTab.ALL) }
    var loading by remember { mutableStateOf(true) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(600); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2000); toast = null } }

    val chat = remember(chatId) { chatId?.let { Repo.chat(it) } }

    // Placeholder media tiles
    val tiles = remember {
        List(15) { i ->
            MediaTile(
                type = when {
                    i % 5 == 3 -> TileType.VIDEO
                    i % 7 == 6 -> TileType.DOC
                    else -> TileType.IMAGE
                },
                duration = if (i % 5 == 3) listOf("02:14", "01:05", "00:47", "03:22", "00:59", "05:11", "01:42", "02:38", "04:00", "00:33", "02:57", "01:19", "06:04", "00:52", "03:41")[i] else null,
            )
        }
    }

    val filtered = remember(tiles, tab) {
        when (tab) {
            MediaTab.ALL -> tiles
            MediaTab.IMAGES -> tiles.filter { it.type == TileType.IMAGE }
            MediaTab.VIDEOS -> tiles.filter { it.type == TileType.VIDEO }
            MediaTab.FILES -> tiles.filter { it.type == TileType.DOC }
        }
    }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(
                title = "رسانه‌ها",
                onBack = onBack,
                subtitle = chat?.title ?: "همه رسانه‌ها",
            )

            Column(Modifier.padding(horizontal = 20.dp)) {
                MjaTabs(
                    tabs = MediaTab.entries.map { it.label },
                    selectedIndex = tab.ordinal,
                    onSelect = { tab = MediaTab.entries[it] },
                )
                Spacer(Modifier.height(10.dp))
            }

            when {
                loading -> Column(Modifier.padding(horizontal = 20.dp)) { SkeletonList(count = 4, showAvatar = false) }

                filtered.isEmpty() -> EmptyState(
                    icon = Icons_Gallery,
                    title = "هنوز فایلی دریافت نکرده‌اید.",
                    description = "رسانه‌های ارسالی و دریافتی اینجا نمایش داده می‌شوند.",
                    actionText = "ارسال فایل",
                    onAction = { toast = "از صفحه گفتگو فایل بفرستید" },
                )

                tab == MediaTab.FILES -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                ) {
                    items(filtered.filter { it.type == TileType.DOC }, key = { it.hashCode() }) { tile ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onOpenImage() }.padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(MjaElevated),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons_FileDoc, contentDescription = null, tint = MjaTextSecondary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                LtrText("سند_${filtered.indexOf(tile)}.pdf", style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                                Spacer(Modifier.height(3.dp))
                                Text("1.2 MB · امروز", style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary)
                            }
                            Icon(Icons_Chevron, contentDescription = null, tint = MjaTextDisabled, modifier = Modifier.size(18.dp))
                        }
                        HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }

                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(filtered.filter { it.type != TileType.DOC }.take(if (tab == MediaTab.ALL) 15 else filtered.size), key = { it.hashCode() }) { tile ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            when (tile.type) {
                                                TileType.IMAGE -> MjaElevated
                                                TileType.VIDEO -> MjaBubbleOut
                                                TileType.DOC -> MjaCard
                                            },
                                            MjaCard,
                                        ),
                                    ),
                                )
                                .clickable {
                                    if (tile.type == TileType.VIDEO) onOpenVideo() else onOpenImage()
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                if (tile.type == TileType.VIDEO) Icons_Play else Icons_Image,
                                contentDescription = null,
                                tint = MjaTextDisabled,
                                modifier = Modifier.size(28.dp),
                            )
                            // Duration chip for videos
                            if (tile.type == TileType.VIDEO && tile.duration != null) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Black.copy(alpha = 0.55f))
                                        .padding(horizontal = 5.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        tile.duration,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(12.dp)) }
                }
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }
    }
}

private enum class TileType { IMAGE, VIDEO, DOC }
private data class MediaTile(val type: TileType, val duration: String? = null)
