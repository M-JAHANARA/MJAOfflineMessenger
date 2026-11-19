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

private enum class FileTab(val label: String) {
    ALL("همه"), DOCS("اسناد"), IMAGES("تصاویر"), VIDEOS("ویدئوها"), AUDIO("صوتی")
}

/**
 * 22 — File Manager
 */
@Composable
fun FileManagerScreen(
    onBack: () -> Unit,
    onOpenTransfer: (String) -> Unit,
    onSendFile: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var tab by remember { mutableStateOf(FileTab.ALL) }
    var loading by remember { mutableStateOf(true) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(600); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2000); toast = null } }

    val files by Repo.files.collectAsState()

    val filtered = remember(files, tab, query) {
        files
            .filter {
                when (tab) {
                    FileTab.ALL -> true
                    FileTab.DOCS -> it.kind == FileKind.DOC
                    FileTab.IMAGES -> it.kind == FileKind.IMAGE
                    FileTab.VIDEOS -> it.kind == FileKind.VIDEO
                    FileTab.AUDIO -> it.kind == FileKind.AUDIO
                }
            }
            .filter { query.isBlank() || it.name.contains(query, true) || it.sender.contains(query, true) }
    }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(title = "فایل‌ها", onBack = onBack) {
                MjaIconButton(icon = Icons_Upload, onClick = onSendFile, contentDescription = "ارسال فایل")
            }

            Column(Modifier.padding(horizontal = 20.dp)) {
                SearchField(value = query, onValueChange = { query = it }, placeholder = "جست‌وجو در فایل‌ها")
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${files.size} فایل · ${humanSize(files.sumOf { it.size })}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MjaTextSecondary,
                        modifier = Modifier.weight(1f),
                    )
                    StatusPill("${filtered.size} نتیجه", color = MjaTextSecondary)
                }
                Spacer(Modifier.height(10.dp))
                MjaTabs(
                    tabs = FileTab.entries.map { it.label },
                    selectedIndex = tab.ordinal,
                    onSelect = { tab = FileTab.entries[it] },
                )
                Spacer(Modifier.height(8.dp))
            }

            when {
                loading -> SkeletonList(count = 6, showAvatar = false)

                filtered.isEmpty() -> EmptyState(
                    icon = Icons_Folder,
                    title = "هنوز فایلی دریافت نکرده‌اید.",
                    description = "فایل‌های ارسالی و دریافتی از طریق شبکه محلی اینجا ذخیره می‌شوند.",
                    actionText = "ارسال فایل",
                    onAction = onSendFile,
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                ) {
                    items(filtered, key = { it.id }) { f ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onOpenTransfer(f.id) }.padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier.size(46.dp).clip(RoundedCornerShape(13.dp)).background(MjaElevated),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    when (f.kind) {
                                        FileKind.DOC -> Icons_FileDoc
                                        FileKind.IMAGE -> Icons_FileImage
                                        FileKind.VIDEO -> Icons_FileVideo
                                        FileKind.AUDIO -> Icons_FileAudio
                                    },
                                    contentDescription = null,
                                    tint = MjaTextSecondary,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                LtrText(
                                    f.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MjaTextPrimary,
                                    fontWeight = FontWeight.Medium,
                                )
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    "${humanSize(f.size)} · ${f.date} · ${f.sender}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MjaTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                if (f.state == TransferState.TRANSFERRING) {
                                    Spacer(Modifier.height(6.dp))
                                    ProgressBar(progress = f.progress)
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            when (f.state) {
                                TransferState.DONE -> {
                                    Icon(Icons_CheckCircle, contentDescription = "دریافت شد", tint = MjaSuccess, modifier = Modifier.size(20.dp))
                                }
                                TransferState.TRANSFERRING -> {
                                    CircularProgressIndicator(
                                        color = MjaBrand,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                                TransferState.FAILED -> {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("خطا", style = MaterialTheme.typography.labelSmall, color = MjaError)
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons_Refresh, contentDescription = "تلاش مجدد", tint = MjaError, modifier = Modifier.size(18.dp))
                                    }
                                }
                                TransferState.PENDING -> {
                                    StatusPill("در انتظار", color = MjaWarning)
                                }
                            }
                        }
                        HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }
    }
}
