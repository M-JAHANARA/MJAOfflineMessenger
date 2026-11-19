package com.mja.messenger.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 38 — About
 */
@Composable
fun AboutScreen(onBack: () -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var privacyOpen by remember { mutableStateOf(false) }
    var permissionsOpen by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { delay(400); loading = false }
    LaunchedEffect(toast) { if (toast != null) { delay(2000); toast = null } }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        when {
            loading -> Column(Modifier.fillMaxSize()) {
                ScreenHeader(title = "درباره MJA", onBack = onBack)
                SkeletonList(count = 5, showAvatar = false)
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                ScreenHeader(title = "درباره MJA", onBack = onBack)

                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(8.dp))
                    MjaLogo(size = 96.dp)
                    Spacer(Modifier.height(14.dp))
                    MjaWordmark("MJA", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(4.dp))
                    Text("Offline Messenger", style = MaterialTheme.typography.titleMedium, color = MjaTextSecondary)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Chat Without Internet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MjaAccent,
                    )
                    Text(
                        "Private. Local. Secure.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MjaTextDisabled,
                    )
                    Spacer(Modifier.height(20.dp))

                    // Intro card
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "MJA یک پیام‌رسان مستقل است که امکان ارتباط کاربران را از طریق شبکه‌های محلی، بدون نیاز به اینترنت، فراهم می‌کند.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MjaTextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    // Info rows
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            InfoRow("شماره نسخه", "1.1.0", valueLtr = true, valueColor = MjaBrand)
                            InfoRow("ساخته‌شده با", "Kotlin · Jetpack Compose", valueLtr = true)
                            InfoRow("پلتفرم", "Android")
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    // Links
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            SettingsRow(
                                title = "سیاست حفظ حریم خصوصی",
                                icon = Icons_Shield,
                                onClick = { privacyOpen = true },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SettingsRow(
                                title = "اطلاعات مجوزها",
                                icon = Icons_Security,
                                onClick = { permissionsOpen = true },
                            )
                            HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                            SettingsRow(
                                title = "تماس با سازنده",
                                icon = Icons_Email,
                                onClick = { toast = "mja.local@offline" },
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    // Principles
                    MjaCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text("اصول چهارگانه MJA", style = MaterialTheme.typography.labelSmall, color = MjaTextDisabled)
                            Spacer(Modifier.height(12.dp))
                            listOf(
                                "Simplicity." to "سادگی",
                                "Privacy." to "حریم خصوصی",
                                "Independence." to "استقلال",
                                "Reliability." to "قابلیت اطمینان",
                            ).forEach { (en, fa) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    LtrText(
                                        en,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MjaTextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text("— $fa", style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))

                    // Footer
                    Text(
                        "MJA OFFLINE MESSENGER",
                        style = MaterialTheme.typography.labelSmall,
                        color = MjaTextDisabled,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(4.dp))
                    LtrText(
                        "CHAT WITHOUT INTERNET.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MjaTextDisabled,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(32.dp))
                }
            }
        }

        if (toast != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) { ToastMessage(toast!!) }
        }

        if (privacyOpen) {
            MjaBottomSheet(onDismiss = { privacyOpen = false }, title = "سیاست حفظ حریم خصوصی") {
                Text(
                    "MJA هیچ داده‌ای را به اینترنت ارسال نمی‌کند. تمام پیام‌ها، رسانه‌ها و اطلاعات حساب شما روی دستگاه خودتان ذخیره می‌شوند و ارتباطات صرفاً از طریق شبکه محلی (Wi-Fi/Hotspot) بین دستگاه‌های حاضر در یک شبکه انجام می‌گیرد.\n\n" +
                        "• بدون سرور ابری\n• بدون تبلیغات و ردیابی\n• بدون جمع‌آوری داده‌های شخصی\n• امکان حذف کامل تمام داده‌ها در هر لحظه",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MjaTextSecondary,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                )
                Spacer(Modifier.height(12.dp))
                MjaButton(text = "متوجه شدم", onClick = { privacyOpen = false }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            }
        }

        if (permissionsOpen) {
            MjaBottomSheet(onDismiss = { permissionsOpen = false }, title = "مجوزهای برنامه") {
                listOf(
                    Triple(Icons_Camera, "دوربین", "برای گرفتن عکس و ویدئو و تماس تصویری"),
                    Triple(Icons_Mic, "میکروفون", "برای پیام صوتی و تماس صوتی"),
                    Triple(Icons_Wifi, "موقعیت مکانی", "برای کشف دستگاه‌های هم‌شبکه (الزام اندروید)"),
                    Triple(Icons_Notifications, "اعلان‌ها", "برای نمایش اعلان پیام‌ها"),
                    Triple(Icons_Storage, "حافظه", "برای ذخیره و دریافت فایل‌ها"),
                ).forEach { (icon, title, desc) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(MjaElevated),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(icon, contentDescription = null, tint = MjaBrand, modifier = Modifier.size(19.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(title, style = MaterialTheme.typography.titleSmall, color = MjaTextPrimary)
                            Spacer(Modifier.height(2.dp))
                            Text(desc, style = MaterialTheme.typography.bodySmall, color = MjaTextSecondary)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
