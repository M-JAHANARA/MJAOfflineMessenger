package com.mja.messenger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mja.messenger.data.*
import com.mja.messenger.net.LocalNet
import com.mja.messenger.ui.components.*
import com.mja.messenger.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun NearbyUsersScreen(
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(refreshKey) { delay(700); loading = false }

    val peers = LocalNet.peers
    val status = LocalNet.status
    val users by Repo.users.collectAsState()
    val me = Repo.currentUser()

    // Merge real peers with demo online users not in peers
    val merged = remember(peers, users) {
        val real = peers.value
        val known = users.filter { it.online && !it.isSelf && !real.any { p -> p.id == it.id || p.username == it.username } }
        val extra = known.map { u -> Peer(u.id, u.name, u.username, u.ip.ifBlank { "192.168.43.1" }, System.currentTimeMillis()) }
        real + extra
    }

    Box(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(title = "افراد نزدیک", onBack = onBack)

            Column(Modifier.padding(horizontal = 20.dp)) {
                NetworkStatusCard(status = status.value)
                Spacer(Modifier.height(8.dp))
                Text(
                    "${merged.size} کاربر شناسایی‌شده",
                    style = MaterialTheme.typography.bodySmall,
                    color = MjaTextSecondary,
                )
                Spacer(Modifier.height(8.dp))
            }

            when {
                loading -> SkeletonList(count = 5)

                merged.isEmpty() -> EmptyState(
                    icon = Icons_WifiOff,
                    title = "کاربری در شبکه پیدا نشد.",
                    description = "به شبکه محلی Hotspot وصل شوید تا کاربران دیگر شناسایی شوند.",
                    actionText = "بررسی وضعیت شبکه",
                    onAction = { refreshKey++ },
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                ) {
                    items(merged, key = { it.id }) { peer ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickableRow { onOpenProfile(peer.id) }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Avatar(
                                name = peer.name,
                                size = 48.dp,
                                colorIndex = peer.id.hashCode().let { (it % 8 + 8) % 8 },
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    peer.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MjaTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    LtrText(
                                        "@${peer.username}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MjaTextDisabled,
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    StatusPill("متصل به شبکه محلی", color = MjaOnline)
                                    Spacer(Modifier.width(8.dp))
                                    LtrText(
                                        peer.ip,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MjaTextDisabled,
                                    )
                                }
                            }
                            MjaButton(
                                text = "شروع گفتگو",
                                style = MjaButtonStyle.Secondary,
                                onClick = { onOpenChat(Repo.openChatWith(peer.id)) },
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                        HorizontalDivider(color = MjaBorder, thickness = 0.5.dp)
                    }
                    item { Spacer(Modifier.height(12.dp)) }
                }
            }
        }
    }
}

private fun Modifier.clickableRow(onClick: () -> Unit): Modifier = this.clickable { onClick() }