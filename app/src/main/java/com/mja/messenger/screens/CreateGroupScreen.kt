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
 * 15 — Create Group
 */
@Composable
fun CreateGroupScreen(onBack: () -> Unit, onCreated: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(setOf<String>()) }
    var creating by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var avatarPick by remember { mutableStateOf(false) }

    val users by Repo.users.collectAsState()
    val others = remember(users) { users.filter { !it.isSelf } }
    val filtered = remember(others, query) {
        others.filter { query.isBlank() || it.name.contains(query, true) || it.username.contains(query, true) }
    }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(title = "ساخت گروه", onBack = onBack)

            Column(Modifier.padding(horizontal = 20.dp)) {
                // Avatar
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 6.dp),
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    Avatar(
                        name = name.ifBlank { "گروه جدید" },
                        isGroup = true,
                        size = 92.dp,
                    )
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MjaBrand)
                            .clickable { avatarPick = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons_Camera, contentDescription = "انتخاب تصویر", tint = MjaBackground, modifier = Modifier.size(15.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))

                MjaTextField(
                    value = name,
                    onValueChange = { name = it; nameError = null },
                    label = "نام گروه",
                    placeholder = "مثلاً: دوستان مدرسه",
                    isError = nameError != null,
                    supportingText = nameError,
                    leadingIcon = Icons_TextSize,
                )
                Spacer(Modifier.height(12.dp))
                MjaTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = "توضیحات",
                    placeholder = "توضیح کوتاه درباره گروه...",
                    singleLine = false,
                    minHeight = 80.dp,
                    leadingIcon = Icons_InfoOutlined,
                )
                Spacer(Modifier.height(16.dp))

                SearchField(value = query, onValueChange = { query = it }, placeholder = "جست‌وجوی اعضا")
                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SectionHeader("انتخاب اعضا", modifier = Modifier.weight(1f))
                    Text(
                        "${selected.size} انتخاب‌شده",
                        style = MaterialTheme.typography.labelSmall,
                        color = MjaBrand,
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            ) {
                items(filtered, key = { it.id }) { user ->
                    val checked = user.id in selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selected = if (checked) selected - user.id else selected + user.id
                            }
                            .padding(vertical = 9.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                selected = if (checked) selected - user.id else selected + user.id
                            },
                            colors = CheckboxDefaults.colors(checkedColor = MjaBrand),
                        )
                        Spacer(Modifier.width(6.dp))
                        Avatar(name = user.name, size = 42.dp, colorIndex = user.avatarColor, online = user.online)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(user.name, style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.height(2.dp))
                            LtrText("@${user.username}", style = MaterialTheme.typography.bodySmall, color = MjaTextDisabled)
                        }
                    }
                }
                item { Spacer(Modifier.height(90.dp)) }
            }

            // Sticky bottom button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MjaBackground)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                MjaButton(
                    text = if (creating) "در حال ساخت..." else "ساخت گروه",
                    onClick = {
                        if (name.isBlank()) {
                            nameError = "نام گروه را وارد کنید"
                        } else if (selected.isEmpty()) {
                            nameError = "حداقل ۱ عضو انتخاب کنید"
                        } else {
                            creating = true
                            val id = Repo.createGroup(name.trim(), desc.trim(), selected.toList())
                            onCreated(id)
                        }
                    },
                    enabled = !creating,
                    loading = creating,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (avatarPick) {
            MjaBottomSheet(onDismiss = { avatarPick = false }, title = "تصویر گروه") {
                SettingsRow(title = "گالری", icon = Icons_Gallery, onClick = { avatarPick = false })
                SettingsRow(title = "دوربین", icon = Icons_Camera, onClick = { avatarPick = false })
            }
        }
    }
}
