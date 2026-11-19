package com.mja.messenger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 20 — Image Viewer (fullscreen)
 */
@Composable
fun ImageViewerScreen(onBack: () -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var failed by remember { mutableStateOf(false) }
    var infoOpen by remember { mutableStateOf(false) }
    var deleteConfirm by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(600); loading = false }
    LaunchedEffect(loading) { if (loading) { delay(600); loading = false } }
    LaunchedEffect(toast) { if (toast != null) { delay(2000); toast = null } }

    Box(Modifier.fillMaxSize().background(MjaSplash)) {
        Column(Modifier.fillMaxSize().mjaInsets()) {

            /* Top overlay bar */
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MjaIconButton(icon = Icons_Back, onClick = onBack, contentDescription = "بازگشت", tint = MjaTextPrimary)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("علی محمدی", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text("امروز - 21:50", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.65f))
                }
            }

            /* Center image placeholder */
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.linearGradient(listOf(MjaElevated, MjaCard))),
                    contentAlignment = Alignment.Center,
                ) {
                    if (loading) {
                        CircularProgressIndicator(color = MjaBrand, strokeWidth = 3.dp)
                    } else if (failed) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons_ErrorOutline, contentDescription = null, tint = MjaError, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(10.dp))
                            Text("تصویر قابل نمایش نیست.", style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary)
                            Spacer(Modifier.height(12.dp))
                            MjaButton(text = "تلاش مجدد", onClick = { failed = false; loading = true }, style = MjaButtonStyle.Secondary)
                        }
                    } else {
                        Icon(
                            Icons_Image,
                            contentDescription = null,
                            tint = MjaTextDisabled.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp),
                        )
                    }
                }
            }

            /* Bottom action bar */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ViewerAction(icon = Icons_Backup, label = "ذخیره") { toast = "تصویر ذخیره شد" }
                ViewerAction(icon = Icons_Share, label = "ارسال") { toast = "ارسال به..." }
                ViewerAction(icon = Icons_Info, label = "اطلاعات") { infoOpen = true }
                ViewerAction(icon = Icons_Trash, label = "حذف", danger = true) { deleteConfirm = true }
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 90.dp)) { ToastMessage(toast!!) }
        }

        if (infoOpen) {
            MjaBottomSheet(onDismiss = { infoOpen = false }, title = "اطلاعات فایل") {
                InfoRow("فرستنده", "علی محمدی")
                InfoRow("تاریخ", "امروز - 21:50")
                InfoRow("اندازه", "1.8 MB")
                InfoRow("نوع", "تصویر")
                InfoRow("نام فایل", "IMG_2481.jpg", valueLtr = true)
                Spacer(Modifier.height(8.dp))
            }
        }

        if (deleteConfirm) {
            MjaDialog(
                title = "حذف تصویر؟",
                message = "این تصویر از دستگاه شما حذف می‌شود.",
                destructive = true,
                icon = Icons_Trash,
                onDismiss = { deleteConfirm = false },
                onConfirm = { deleteConfirm = false; onBack() },
            )
        }
    }
}

@Composable
private fun ViewerAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    danger: Boolean = false,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.06f)).padding(horizontal = 14.dp, vertical = 8.dp).clickableRow(onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = label, tint = if (danger) MjaError else Color.White, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = if (danger) MjaError else Color.White)
    }
}

private fun Modifier.clickableRow(onClick: () -> Unit): Modifier = this.clickable { onClick() }
