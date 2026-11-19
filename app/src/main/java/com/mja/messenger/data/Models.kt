package com.mja.messenger.data

import androidx.compose.runtime.Immutable

/** Kind of message content supported by MJA. */
enum class MsgKind { TEXT, IMAGE, VIDEO, AUDIO, VOICE, FILE }

/** Delivery lifecycle — also drives the offline queue. */
enum class MsgStatus { PENDING, SENDING, SENT, DELIVERED, READ, FAILED }

@Immutable
data class User(
    val id: String,
    val name: String,
    val username: String,
    val online: Boolean = false,
    val lastSeen: String = "همین حالا",
    val bio: String = "",
    val avatarColor: Int = 0,
    val isSelf: Boolean = false,
    val deviceName: String = "",
    val ip: String = "",
    val connectedAt: Long? = null,
)

@Immutable
data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val text: String = "",
    val kind: MsgKind = MsgKind.TEXT,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MsgStatus = MsgStatus.DELIVERED,
    val replyToId: String? = null,
    val replyPreview: String? = null,
    val replySenderName: String? = null,
    val fileName: String? = null,
    val fileSize: Long = 0L,
    val durationMs: Long = 0L,
    val mediaUri: String? = null,
    val reactions: List<String> = emptyList(),
    val pinned: Boolean = false,
    val forwarded: Boolean = false,
    val dateLabel: String = "امروز",
)

@Immutable
data class Chat(
    val id: String,
    val title: String,
    val isGroup: Boolean = false,
    val username: String = "",
    val online: Boolean = false,
    val lastSeen: String = "",
    val avatarColor: Int = 0,
    val lastMessage: String = "",
    val lastTimestamp: Long = 0L,
    val unread: Int = 0,
    val pinned: Boolean = false,
    val muted: Boolean = false,
    val saved: Boolean = false,
    val memberIds: List<String> = emptyList(),
    val description: String = "",
    val groupId: String = "",
    val admins: List<String> = emptyList(),
    val typing: Boolean = false,
    val isTypingGroup: String = "",
)

@Immutable
data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val time: String,
    val chatId: String? = null,
    val read: Boolean = false,
    val type: NoticeType = NoticeType.MESSAGE,
)

enum class NoticeType { MESSAGE, GROUP, FILE, CALL, SYSTEM }

@Immutable
data class FileItem(
    val id: String,
    val name: String,
    val kind: FileKind,
    val size: Long,
    val sender: String,
    val date: String,
    val progress: Float = 1f,
    val state: TransferState = TransferState.DONE,
    val chatTitle: String = "",
    val uri: String? = null,
)

enum class FileKind { DOC, IMAGE, VIDEO, AUDIO }
enum class TransferState { PENDING, TRANSFERRING, DONE, FAILED }

@Immutable
data class QueueItem(
    val id: String,
    val recipient: String,
    val preview: String,
    val createdAt: Long,
    val status: MsgStatus,
    val error: String? = null,
    val chatId: String,
)

@Immutable
data class CallSession(
    val peerName: String,
    val video: Boolean = false,
    val connected: Boolean = false,
    val startedAt: Long = 0L,
    val failed: Boolean = false,
)

/** Persisted user preferences (Settings → every switch in one place). */
data class AppSettings(
    val language: String = "فارسی",
    val theme: String = "Black & Graphite",
    val textSize: String = "پیش‌فرض",
    val animations: Boolean = true,
    val notifyPrivate: Boolean = true,
    val notifyGroups: Boolean = true,
    val notifyFiles: Boolean = true,
    val notifyCalls: Boolean = true,
    val sound: Boolean = true,
    val vibration: Boolean = true,
    val preview: Boolean = true,
    val silentMode: Boolean = false,
    val showOnline: Boolean = true,
    val showLastSeen: Boolean = true,
    val allowContactRequests: Boolean = true,
    val encryption: Boolean = true,
    val appLock: Boolean = false,
    val autoSync: Boolean = true,
    val localBackup: Boolean = false,
)

/** Snapshot of the local network, produced by [com.mja.messenger.net.LocalNet]. */
data class NetStatus(
    val wifiEnabled: Boolean = false,
    val connected: Boolean = false,
    val ssid: String = "—",
    val ip: String = "0.0.0.0",
    val signal: Int = 0,
    val isHotspot: Boolean = false,
    val hostActive: Boolean = false,
    val serverIp: String = "0.0.0.0",
    val users: Int = 0,
    val uptime: Long = 0L,
    val scanning: Boolean = true,
)

data class Peer(
    val id: String,
    val name: String,
    val username: String,
    val ip: String,
    val lastSeen: Long = System.currentTimeMillis(),
)
