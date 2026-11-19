# MJA Offline Messenger — UI Development Contract

Project: `C:\Users\Persia system\Desktop\massenger\MJAOfflineMessenger`
Package: `com.mja.messenger` · Language: Kotlin · UI: Jetpack Compose + Material 3
Reference screen: **393 x 852 dp** · Direction: **RTL** (forced by theme) · Language: **Persian**

## 1. Hard rules

1. Write ONLY the files assigned to you. Never edit shared files
   (`ui/theme/*`, `ui/components/*`, `data/*`, `net/*`, `navigation/*`, `MainActivity.kt`).
   Need a local helper? Define it as a `private` composable/function inside your own file.
2. Do NOT run Gradle / any build command. The integrator compiles everything.
3. No internet images, no new dependencies. Icons come from `ui/components/Icons.kt` aliases
   (`Icons_Search`, `Icons_Back`, `Icons_More`, `Icons_Call`, ...) or any
   `androidx.compose.material.icons.*` icon.
4. Every string literal in the UI is **Persian** (except: IPs, usernames, IDs, file names,
   English product words like "MJA", "Offline Messenger", "Chat Without Internet.").
   Latin/IP/ID text must use `LtrText(...)`.
5. Colors: import `com.mja.messenger.ui.theme.*` and use ONLY those tokens
   (`MjaBackground`, `MjaBackground2`, `MjaCard`, `MjaElevated`, `MjaBorder`,
   `MjaTextPrimary`, `MjaTextSecondary`, `MjaTextDisabled`, `MjaBrand`, `MjaAccent`,
   `MjaSuccess`, `MjaWarning`, `MjaError`, `MjaOnline`, `MjaBubbleOut`, `MjaBubbleIn`).
   Never hardcode other hex colors (white/black scrim exceptions allowed).
6. Typography: `MaterialTheme.typography.*` — headlineSmall=22 titleLarge=17 titleMedium=15
   bodyLarge=15 bodyMedium=14 bodySmall=12 labelSmall=11.
7. Spacing: horizontal screen margin **20.dp**, card gap **12.dp**, card padding 14–18.dp,
   card radius **16.dp**, button radius **14.dp**, primary button height **52.dp**.
8. Insets: detail screens → root modifier `.mjaInsets()` (from `ui.components.mjaInsets`).
   Tab screens (home/contacts/groups/notifications/settings) are hosted by `MainScaffold`
   which already applies insets — use `MainScaffold` and only consume its `PaddingValues`.
9. Every screen MUST contain: normal state, **empty state** (`EmptyState`), **error state**
   (`ErrorState` or an inline banner) and **loading state** (`LoadingState` / `SkeletonList`),
   reachable through real state (e.g. `loading` flag with `LaunchedEffect { delay(...) }`).
10. Minimum touch target 40–48.dp, text contrast readable, no color-only status
    (always pair color + icon/text).
11. Animations: implicit Compose animations are fine (150–250ms feel). No heavy effects.

## 2. Read these first (API reference)

- `ui/components/Controls.kt`, `Surfaces.kt`, `Chat.kt`, `Icons.kt`
- `data/Models.kt`, `data/Repo.kt`
- `ui/theme/Color.kt`, `Type.kt`
- `screens/HomeScreen.kt` (style exemplar), `screens/SplashScreen.kt`
- `navigation/AppNav.kt` (route names; you never edit it)

## 3. Component API (import `com.mja.messenger.ui.components.*`)

```kotlin
MjaButton(text, onClick, modifier=, style=MjaButtonStyle.Primary|Secondary|Text|Destructive|Ghost,
          enabled=true, loading=false, icon: ImageVector?=null)
MjaIconButton(icon, onClick, contentDescription, modifier=, tint=, background=, size=40.dp,
              iconSize=21.dp, bordered=false, enabled=true)
MjaTextField(value, onValueChange, modifier=, label=, placeholder=, leadingIcon=, trailingIcon=,
             isPassword=, isError=, supportingText=, imeAction=, onImeAction=, keyboardType=,
             singleLine=, enabled=, minHeight=52.dp)
SearchField(value, onValueChange, modifier=, placeholder=, onImeAction=)
MjaTabs(tabs: List<String>, selectedIndex, onSelect, modifier=)
MjaChip(text, onClick, modifier=, selected=false, icon=)
SettingsRow(title, onClick, modifier=, icon=, subtitle=, value=, danger=false, enabled=true, trailing=)
SwitchRow(title, checked, onCheckedChange, modifier=, description=, icon=, enabled=true)
StatusPill(text, modifier=, color=MjaOnline, filled=false)
LtrText(text, modifier=, style=, color=, fontWeight=, textAlign=)

MjaCard(modifier=, onClick=null, elevated=false, contentPadding=PaddingValues(16.dp)) { ColumnScope }
ScreenHeader(title, modifier=, onBack=null, subtitle=, showLogo=false, titleCentered=false, actions=RowScope)
SectionHeader(title, modifier=, actionText=, onAction=)
MjaLogo(size, modifier) · MjaWordmark(text, modifier, style)
Avatar(name, modifier=, size=48.dp, colorIndex=0, isGroup=false, online=null, ringColor=null)
OnlineDot(modifier=, size=10.dp, online=true)
CountBadge(count, modifier=, color=MjaError, max=99)
EmptyState(icon, title, description, modifier=, actionText=null, onAction=null)
ErrorState(title, description, modifier=, icon=Icons_ErrorOutline, actionText="تلاش مجدد", onAction=null)
LoadingState(label="در حال بارگذاری...", modifier=)
SkeletonList(modifier=, count=6, showAvatar=true)
ProgressBar(progress, modifier=, color=, track=, height=7.dp)
StatCard(title, value, modifier=, icon=null, accent=MjaBrand)
InfoRow(label, value, modifier=, valueLtr=false, valueColor=MjaTextPrimary)
ToastMessage(text, modifier=)
MjaDialog(title, message, onDismiss, onConfirm, modifier=, confirmText="تأیید", cancelText="انصراف",
          destructive=false, icon=null, content=null)
MjaBottomSheet(onDismiss, modifier=, title=null) { ColumnScope }
NetworkStatusCard(status: NetStatus, modifier=, usersText=null, onClick=null)
MjaBottomBar(currentIndex, onSelect, modifier=, unreadCount=0)   // inside MainScaffold only
mjaInsets(): Modifier                                            // Modifier.mjaInsets()

// Chat.kt
DateDivider(label, modifier) · DeliveryTicks(status, modifier) · Waveform(amplitudes, modifier=,
  height=26.dp, active=false, barWidth=2.dp, gap=2.dp)
MessageBubble(message, isSelf, modifier=, senderName=null, senderAvatar=null, showTime=true,
              onClick=null, reactions=emptyList(), voicePlaying=false, onPlayVoice=null,
              onOpenMedia=null)
TypingBubble(label, modifier) · ReplyBar(title, preview, onCancel, modifier=, tone=MjaAccent)
MessageComposer(value, onValueChange, onSend, modifier=, onMic=, onAttach=, onEmoji=,
                placeholder="پیام بنویسید...", reply=null, replyName=null, onCancelReply=)
formatDuration(ms: Long): String
```

## 4. Data API (`com.mja.messenger.data.Repo` object)

```kotlin
Repo.users / Repo.chats / Repo.messages / Repo.notifications / Repo.files / Repo.queue /
Repo.settings / Repo.session / Repo.sync / Repo.replyDraft / Repo.forwardDraft   // StateFlow
Repo.chat(id): Chat? · Repo.user(id): User? · Repo.currentUser(): User
Repo.messagesFor(chatId): List<Message> · Repo.timeLabel/dayLabel/fullDateTime/humanSize helpers
Repo.sendText(chatId, text) · Repo.sendMedia(chatId, kind, text=, fileName=, fileSize=, uri=, durationMs=)
Repo.markRead(chatId) · Repo.deleteMessage(id) · Repo.togglePin(id) · Repo.addReaction(id, emoji)
Repo.setReply(m) / Repo.setForward(m) / Repo.forwardTo(chatId)
Repo.openChatWith(userId): String · Repo.createGroup(name, desc, memberIds): String
Repo.addMembers(chatId, ids) · Repo.removeMember(chatId, userId) · Repo.leaveGroup(chatId)
Repo.toggleChatFlag(chatId, ChatFlag.PINNED|MUTED|SAVED) · Repo.clearChat(chatId)
Repo.retryQueueItem(id) / Repo.retryAllQueue() / Repo.removeQueueItem(id) / Repo.runSync()
Repo.updateSettings { it.copy(...) } · Repo.storageStats() · Repo.clearTemp() · Repo.clearAllMedia()
Repo.updateProfile(display, username, bio, status) · Repo.logout() · Repo.clearError()
Repo.addUserIfMissing(name, username, ip)
```

Enums: `MsgKind { TEXT, IMAGE, VIDEO, AUDIO, VOICE, FILE }`
`MsgStatus { PENDING, SENDING, SENT, DELIVERED, READ, FAILED }`
`FileKind { DOC, IMAGE, VIDEO, AUDIO }` · `TransferState { PENDING, TRANSFERRING, DONE, FAILED }`
`NoticeType { MESSAGE, GROUP, FILE, CALL, SYSTEM }`

Collect flows in composable: `val chats by Repo.chats.collectAsState()`

## 5. Network API (`com.mja.messenger.net.LocalNet`)

```kotlin
LocalNet.status: StateFlow<NetStatus>   // wifiEnabled, connected, ssid, ip, signal, isHotspot,
                                        // hostActive, serverIp, users, uptime, scanning
LocalNet.peers: StateFlow<List<Peer>>   // real discovered peers (id,name,username,ip,lastSeen)
LocalNet.startHotspot(onResult: (Boolean, String?) -> Unit)
LocalNet.stopHotspot() · LocalNet.hotspotActive(): Boolean · LocalNet.hotspotSupported(): Boolean
LocalNet.isConnected(): Boolean · LocalNet.hasRealPeer(): Boolean
LocalNet.connectedDevices(): List<Peer>
```

## 6. Tab screens pattern (home/contacts/groups/notifications/settings)

```kotlin
@Composable
fun XxxScreen(..., onBack: (() -> Unit)? = null, tab: (Int) -> Unit) {
    MainScaffold(current = N, onTab = tab, unreadCount = <sum of Repo.chats.unread>) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            ScreenHeader(title = "...")   // no back button on tabs
            ...
        }
    }
}
```
Tab index: 0=home 1=contacts 2=groups 3=notifications 4=settings.

## 7. Detail screen skeleton

```kotlin
@Composable
fun XxxScreen(onBack: () -> Unit, ...) {
    var loading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(500); loading = false }
    Column(Modifier.fillMaxSize().mjaInsets().background(MjaBackground)) {
        ScreenHeader(title = "...", onBack = onBack)
        // content, EmptyState / ErrorState / SkeletonList per state
    }
}
```

Use `collectAsState()` + `remember { }` filters for lists. Prefer `LazyColumn` for lists.
Dialogs/sheets: `var open by remember { mutableStateOf(false) }` + `if (open) MjaDialog/...`.

## 8. Quality bar

The result must look like a premium messenger (Telegram/Signal-grade polish, MJA identity):
consistent headers, subtle borders (#2B3039), no clutter, real Persian copy, meaningful
statuses (ticks, online dots, progress), and every spec item of your assigned screens
(listed in your prompt) implemented — no placeholders, no "TODO", no missing states.
