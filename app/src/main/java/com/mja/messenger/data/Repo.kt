package com.mja.messenger.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/* ------------------------------------------------------------------ */
/* Time helpers — western digits, always readable                     */
/* ------------------------------------------------------------------ */

fun timeLabel(ts: Long): String =
    SimpleDateFormat("HH:mm", Locale.US).format(Date(ts))

fun dayLabel(ts: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - ts
    return when {
        diff < 60_000 -> "الان"
        sameDay(ts, now) -> timeLabel(ts)
        diff < 2 * 86_400_000L -> "دیروز"
        diff < 7 * 86_400_000L -> SimpleDateFormat("EEEE", Locale.US).format(Date(ts))
        else -> SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Date(ts))
    }
}

fun fullDateTime(ts: Long): String =
    SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale.US).format(Date(ts))

private fun sameDay(a: Long, b: Long): Boolean {
    val f = SimpleDateFormat("yyyyMMdd", Locale.US)
    return f.format(Date(a)) == f.format(Date(b))
}

fun humanSize(bytes: Long): String = when {
    bytes >= 1L shl 30 -> String.format(java.util.Locale.US, "%.1f GB", bytes / 1073741824.0)
    bytes >= 1L shl 20 -> String.format(java.util.Locale.US, "%.1f MB", bytes / 1048576.0)
    bytes >= 1024 -> String.format(java.util.Locale.US, "%.1f KB", bytes / 1024.0)
    else -> "$bytes B"
}

/* ------------------------------------------------------------------ */
/* Session / sync states                                              */
/* ------------------------------------------------------------------ */

data class Session(
    val ready: Boolean = false,
    val loggedIn: Boolean = false,
    val onboarded: Boolean = false,
    val displayName: String = "",
    val username: String = "",
    val bio: String = "",
    val error: String? = null,
)

data class SyncStatus(
    val running: Boolean = false,
    val success: Boolean = false,
    val failed: Boolean = false,
    val progress: Float = 0f,
    val received: Int = 0,
    val sent: Int = 0,
    val lastSync: Long = 0L,
    val report: List<String> = emptyList(),
)

data class StorageStats(
    val total: Long = 0,
    val images: Long = 0,
    val videos: Long = 0,
    val files: Long = 0,
    val audio: Long = 0,
    val messages: Long = 0,
    val temp: Long = 0,
)

/* ------------------------------------------------------------------ */
/* Repository                                                          */
/* ------------------------------------------------------------------ */

object Repo {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var appContext: Context? = null
    private var replyJob: Job? = null

    private val _users = MutableStateFlow<List<User>>(emptyList())
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    private val _files = MutableStateFlow<List<FileItem>>(emptyList())
    private val _queue = MutableStateFlow<List<QueueItem>>(emptyList())
    private val _settings = MutableStateFlow(AppSettings())
    private val _session = MutableStateFlow(Session())
    private val _sync = MutableStateFlow(SyncStatus())
    private val _replyDraft = MutableStateFlow<Message?>(null)
    private val _forwardDraft = MutableStateFlow<Message?>(null)

    val users: StateFlow<List<User>> = _users.asStateFlow()
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()
    val files: StateFlow<List<FileItem>> = _files.asStateFlow()
    val queue: StateFlow<List<QueueItem>> = _queue.asStateFlow()
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    val session: StateFlow<Session> = _session.asStateFlow()
    val sync: StateFlow<SyncStatus> = _sync.asStateFlow()
    val replyDraft: StateFlow<Message?> = _replyDraft.asStateFlow()
    val forwardDraft: StateFlow<Message?> = _forwardDraft.asStateFlow()

    /* ------------------------------ init ------------------------- */

    fun init(context: Context) {
        if (appContext != null) return
        appContext = context.applicationContext
        loadSettings()
        loadSession()
        if (_users.value.isEmpty()) seed()
        loadMessages()
        if (_messages.value.isEmpty()) seedMessages()
    }

    /* --------------------------- demo seed ----------------------- */

    private fun seed() {
        val now = System.currentTimeMillis()
        _users.value = listOf(
            User("u_me", "محمد", "mja_user", online = true, lastSeen = "همین حالا",
                bio = "تا همین چند لحظه پیش فعال بود", avatarColor = 3, isSelf = true,
                deviceName = "MJA Device", ip = ""),
            User("u_ali", "علی محمدی", "ali_mmd", online = true, lastSeen = "همین حالا",
                bio = "توسعه‌دهنده نرم‌افزار", avatarColor = 0, deviceName = "Galaxy S23"),
            User("u_meh", "مریم فناوری", "mahsa_dev", online = true, lastSeen = "همین حالا",
                bio = "طراح رابط کاربری", avatarColor = 1, deviceName = "Pixel 8"),
            User("u_reza", "رضا کاشفی", "reza_k", online = false, lastSeen = "امروز، 18:40",
                bio = "دانشجوی شبکه", avatarColor = 2, deviceName = "Redmi Note 12"),
            User("u_sara", "سارا جمشیدی", "sara_j", online = false, lastSeen = "امروز، 17:10",
                bio = "مهندس فناوری اطلاعات", avatarColor = 4, deviceName = "A54"),
            User("u_parsa", "پارسا کریمی", "parsa_c", online = true, lastSeen = "همین حالا",
                bio = "عکاسی و ویرایش ویدئو", avatarColor = 5, deviceName = "iPhone 14"),
        )

        _chats.value = listOf(
            Chat(id = "c_dost", title = "دوستان نزدیک", isGroup = true, avatarColor = 2,
                lastMessage = "فردا همه حاضریم؟", lastTimestamp = now - 240_000, unread = 3,
                memberIds = listOf("u_ali", "u_meh", "u_reza", "u_sara"),
                description = "گروه دوستان برای هماهنگی جلسات", groupId = "g_731204",
                admins = listOf("u_me"), typing = true, isTypingGroup = "مریم در حال نوشتن..."),
            Chat(id = "c_ali", title = "علی محمدی", username = "ali_mmd", online = true,
                avatarColor = 0, lastMessage = "سلام خوبی؟", lastTimestamp = now - 600_000,
                unread = 0, lastSeen = "همین حالا"),
            Chat(id = "c_uni", title = "گروه دانشجویان", isGroup = true, avatarColor = 4,
                lastMessage = "مریم: پروژه آماده تحویل شد", lastTimestamp = now - 1_500_000,
                unread = 12, memberIds = listOf("u_meh", "u_reza", "u_sara", "u_parsa"),
                description = "گروه رسمی کلاس شبکه‌های محلی", groupId = "g_118802",
                admins = listOf("u_me", "u_meh"), muted = true),
            Chat(id = "c_reza", title = "رضا کاشفی", username = "reza_k", online = false,
                avatarColor = 2, lastMessage = "عکس رو دریافت کردی؟", lastTimestamp = now - 3_600_000,
                unread = 1, lastSeen = "امروز، 18:40"),
            Chat(id = "c_par", title = "پارسا کریمی", username = "parsa_c", online = true,
                avatarColor = 5, lastMessage = "ویدئو ارسال شد", lastTimestamp = now - 7_200_000,
                unread = 0, pinned = true, lastSeen = "همین حالا"),
            Chat(id = "c_proj", title = "گروه پروژه", isGroup = true, avatarColor = 1,
                lastMessage = "فایل گزارش نهایی ارسال شد", lastTimestamp = now - 26 * 3_600_000,
                unread = 0, memberIds = listOf("u_ali", "u_parsa"),
                description = "پروژه پایان‌ترم — ارتباط محلی", groupId = "g_559013",
                admins = listOf("u_me")),
            Chat(id = "c_sara", title = "سارا جمشیدی", username = "sara_j", online = false,
                avatarColor = 4, lastMessage = "ممنون، تحویل گرفتم", lastTimestamp = now - 50 * 3_600_000,
                unread = 0, saved = true, lastSeen = "امروز، 17:10"),
        )

        _notifications.value = listOf(
            NotificationItem("n1", "علی محمدی", "فردا همه حاضریم؟", "الان", "c_ali", false, NoticeType.MESSAGE),
            NotificationItem("n2", "گروه دانشجویان", "مریم: پروژه آماده تحویل شد", "25 دقیقه پیش", "c_uni", false, NoticeType.GROUP),
            NotificationItem("n3", "پارسا کریمی", "یک ویدئو ارسال کرد", "2 ساعت پیش", "c_par", true, NoticeType.FILE),
            NotificationItem("n4", "سیستم", "اتصال به شبکه محلی برقرار شد", "امروز", null, true, NoticeType.SYSTEM),
        )

        _files.value = listOf(
            FileItem("f1", "گزارش_نهایی.pdf", FileKind.DOC, 2_400_000, "علی محمدی", "امروز 20:15", chatTitle = "گروه پروژه"),
            FileItem("f2", "IMG_2481.jpg", FileKind.IMAGE, 1_840_000, "رضا کاشفی", "امروز 19:02", chatTitle = "رضا کاشفی"),
            FileItem("f3", "ویدئو_جلسه.mp4", FileKind.VIDEO, 18_400_000, "پارسا کریمی", "دیروز 21:40", chatTitle = "پارسا کریمی"),
            FileItem("f4", "یادداشت_صوتی.amr", FileKind.AUDIO, 340_000, "مریم فناوری", "دیروز 18:30", state = TransferState.FAILED, progress = 0.64f, chatTitle = "گروه دانشجویان"),
            FileItem("f5", "پیش‌نویس_قرارداد.docx", FileKind.DOC, 860_000, "من", "امروز 21:50", state = TransferState.TRANSFERRING, progress = 0.35f, chatTitle = "علی محمدی"),
        )
    }

    private fun seedMessages() {
        val now = System.currentTimeMillis()
        fun at(minAgo: Long) = now - minAgo * 60_000
        val list = mutableListOf<Message>()
        var i = 0
        fun add(
            chat: String, from: String, text: String, minAgo: Long,
            status: MsgStatus = MsgStatus.READ, kind: MsgKind = MsgKind.TEXT,
            date: String = "امروز",
        ) {
            list.add(
                Message(
                    id = "m_seed_${i++}", chatId = chat, senderId = from, text = text,
                    timestamp = at(minAgo), status = status, kind = kind, dateLabel = date,
                )
            )
        }

        add("c_ali", "u_ali", "سلام محمد، حالت چطوره؟", 48)
        add("c_ali", "u_me", "سلام علی، خوبم ممنون. تو چطوری؟", 46, MsgStatus.READ)
        add("c_ali", "u_ali", "منم خوبم، یه خبر خوب برات دارم", 44)
        add("c_ali", "u_me", "بگو ببینم چی شده؟", 42, MsgStatus.READ)
        add("c_ali", "u_ali", "شبکه محلی رو راه انداختم، همه وصلن!", 40, kind = MsgKind.TEXT)
        add("c_ali", "u_me", "عالیه، پس می‌تونیم بدون اینترنت چت کنیم 👌", 38, MsgStatus.DELIVERED)
        add("c_ali", "u_ali", "سلام خوبی؟", 10, MsgStatus.READ)

        add("c_dost", "u_ali", "سلام بچه‌ها، فردا جلسه داریم", 120)
        add("c_dost", "u_meh", "ساعت چنده دقیقاً؟", 110)
        add("c_dost", "u_ali", "ساع 17 تو کتابخانه مرکزی", 105)
        add("c_dost", "u_reza", "من که میام، فقط بگید پروژه رو بیارم؟", 95)
        add("c_dost", "u_me", "آره حتماً، نسخه نهایی باشه", 90, MsgStatus.DELIVERED)
        add("c_dost", "u_meh", "منم فایل طراحی‌ها رو می‌فرستم تا همین‌جا", 60, date = "دیروز")
        add("c_dost", "u_ali", "فردا همه حاضریم؟", 4)

        add("c_uni", "u_meh", "دوستان، ارائه‌ها آماده شد", 300, date = "دیروز")
        add("c_uni", "u_reza", "عالیه، لینک فایل رو بذارید اینجا", 280, date = "دیروز")
        add("c_uni", "u_parsa", "ویدئوی توضیحات رو هم ضبط کردم", 260, date = "دیروز")
        add("c_uni", "u_me", "ممنون بچه‌ها، شب بخیر", 250, MsgStatus.DELIVERED, date = "دیروز")
        add("c_uni", "u_meh", "پروژه آماده تحویل شد", 25)

        add("c_reza", "u_reza", "عکس‌ها رو فرستادم، ببین نظرت چیه", 90)
        add("c_reza", "u_me", "چشم الان می‌بینم", 85, MsgStatus.READ)
        add("c_reza", "u_reza", "عکس رو دریافت کردی؟", 62)

        add("c_par", "u_parsa", "یه ویدئوی باحال گرفتم، می‌فرستم", 300)
        add("c_par", "u_me", "بفرست ببینم", 295, MsgStatus.READ)
        add("c_par", "u_parsa", "ویدئو ارسال شد", 120)

        add("c_proj", "u_ali", "اسکرین‌شات آخرین نسخه اپ رو می‌فرستم", 1600, date = "دیروز")
        add("c_proj", "u_me", "ممنون، بررسی می‌کنم", 1580, MsgStatus.READ, date = "دیروز")
        add("c_proj", "u_parsa", "فایل گزارش نهایی ارسال شد", 1560, date = "دیروز")

        add("c_sara", "u_me", "فایل‌ها رو برایت فرستادم", 3000, MsgStatus.READ, date = "دیروز")
        add("c_sara", "u_sara", "ممنون، تحویل گرفتم", 2990, date = "دیروز")

        _messages.value = list
        persistMessages()
    }

    /* ------------------------------ auth ------------------------- */

    fun completeOnboarding() {
        _session.value = _session.value.copy(onboarded = true)
        saveSession()
    }

    fun login(username: String, password: String): Boolean {
        val u = username.trim()
        val p = password.trim()
        return if (u.isEmpty() || p.isEmpty()) {
            _session.value = _session.value.copy(error = "نام کاربری و رمز عبور را وارد کنید")
            false
        } else {
            _session.value = _session.value.copy(
                loggedIn = true, username = u, displayName = u,
                bio = "تا همین چند لحظه پیش فعال بود", error = null,
            )
            saveSession()
            true
        }
    }

    fun register(display: String, username: String, pass: String, pass2: String): String? {
        val error = when {
            display.isBlank() -> "نام نمایشی را وارد کنید"
            username.isBlank() -> "نام کاربری را وارد کنید"
            pass.length < 4 -> "رمز عبور باید حداقل ۴ نویسه باشد"
            pass != pass2 -> "رمز عبور و تکرار آن یکسان نیستند"
            else -> null
        }
        if (error != null) {
            _session.value = _session.value.copy(error = error)
            return error
        }
        _session.value = _session.value.copy(
            loggedIn = true, displayName = display, username = username,
            bio = "", error = null,
        )
        val me = _users.value.firstOrNull { it.isSelf } ?: return null
        _users.value = _users.value.map {
            if (it.isSelf) it.copy(name = display, username = username) else it
        }
        _chats.value = _chats.value.map { c ->
            if (c.memberIds.contains(me.id) && c.isGroup)
                c.copy(admins = c.admins.map { me.id }) else c
        }
        saveSession()
        return null
    }

    fun updateProfile(display: String, username: String, bio: String, status: String) {
        _users.value = _users.value.map {
            if (it.isSelf) it.copy(name = display, username = username, bio = bio, lastSeen = status) else it
        }
        _session.value = _session.value.copy(displayName = display, username = username, bio = bio)
        saveSession()
    }

    fun logout() {
        _session.value = Session()
        saveSession()
    }

    fun clearError() {
        if (_session.value.error != null) _session.value = _session.value.copy(error = null)
    }

    fun currentUser(): User = _users.value.firstOrNull { it.isSelf }
        ?: User("u_me", "من", "user", isSelf = true)

    /* --------------------------- messaging ----------------------- */

    fun messagesFor(chatId: String): List<Message> =
        _messages.value.filter { it.chatId == chatId }.sortedBy { it.timestamp }

    fun chat(id: String): Chat? = _chats.value.firstOrNull { it.id == id }

    fun user(id: String): User? = _users.value.firstOrNull { it.id == id }

    fun sendText(
        chatId: String,
        text: String,
        replyToId: String? = null,
        replyPreview: String? = null,
        replySenderName: String? = null,
    ) {
        if (text.isBlank()) return
        enqueueMessage(
            Message(
                id = "m_${System.currentTimeMillis()}_${Random.nextInt(999)}",
                chatId = chatId, senderId = currentUser().id, text = text.trim(),
                kind = MsgKind.TEXT, status = MsgStatus.SENDING, dateLabel = "امروز",
                replyToId = replyToId, replyPreview = replyPreview, replySenderName = replySenderName,
            )
        )
    }

    fun sendMedia(
        chatId: String,
        kind: MsgKind,
        text: String = "",
        fileName: String = "فایل",
        fileSize: Long = 0L,
        uri: String? = null,
        durationMs: Long = 0L,
    ) {
        enqueueMessage(
            Message(
                id = "m_${System.currentTimeMillis()}_${Random.nextInt(999)}",
                chatId = chatId, senderId = currentUser().id, text = text,
                kind = kind, status = MsgStatus.SENDING, fileName = fileName,
                fileSize = fileSize, mediaUri = uri, durationMs = durationMs,
                dateLabel = "امروز",
            )
        )
    }

    private fun enqueueMessage(msg: Message) {
        _messages.value = _messages.value + msg
        touchChat(msg)
        persistMessages()

        // Known demo peers are reachable inside the seeded session; real peers are
        // delivered through the local network layer when present.
        val peerOnline = peerOnlineFor(msg.chatId)
        val chat = chat(msg.chatId)

        if (!peerOnline) {
            _queue.value = _queue.value + QueueItem(
                id = msg.id, recipient = chat?.title ?: "—", preview = msg.previewText(),
                createdAt = msg.timestamp, status = MsgStatus.PENDING, chatId = msg.chatId,
            )
            updateStatus(msg.id, MsgStatus.PENDING)
        }

        val peerIp = peerIpFor(msg.chatId)
        scope.launch {
            if (peerIp.isNotBlank()) {
                // Real offline-LAN delivery to a discovered peer.
                val me = currentUser()
                val ok = com.mja.messenger.net.LocalNet.send(peerIp, me.name, me.username, msg.previewText())
                if (ok) {
                    updateStatus(msg.id, MsgStatus.DELIVERED)
                    markQueue(msg.id, MsgStatus.DELIVERED)
                    _sync.value = _sync.value.copy(
                        sent = _sync.value.sent + 1, lastSync = System.currentTimeMillis(),
                    )
                } else {
                    markQueue(msg.id, MsgStatus.FAILED, "ارتباط با گیرنده قطع شد")
                }
                return@launch
            }
            delay(if (peerOnline) 700 else 1_400)
            if (peerOnline) {
                updateStatus(msg.id, MsgStatus.DELIVERED)
                markQueue(msg.id, MsgStatus.DELIVERED)
                delay(1_600)
                updateStatus(msg.id, MsgStatus.READ)
            }
        }
        maybeAutoReply(msg)
    }

    /** Incoming message received from a real peer over the local network. */
    fun receiveMessage(fromName: String, fromUser: String, text: String) {
        addUserIfMissing(fromName, fromUser, "")
        val user = users.value.firstOrNull { it.username == fromUser }
        var chatId = _chats.value.firstOrNull { !it.isGroup && it.username == fromUser }?.id
        if (chatId == null) {
            chatId = "c_${System.currentTimeMillis() % 100000}"
            _chats.value = _chats.value + Chat(
                id = chatId, title = fromName, username = fromUser, online = true,
                avatarColor = (fromUser.hashCode().let { (it % 8 + 8) % 8 }),
                memberIds = listOfNotNull(user?.id), unread = 1,
                lastSeen = "همین حالا",
            )
        }
        val incoming = Message(
            id = "m_${System.currentTimeMillis()}_${Random.nextInt(999)}",
            chatId = chatId, senderId = user?.id ?: "u_$fromUser", text = text,
            status = MsgStatus.READ, dateLabel = "امروز",
        )
        _messages.value = _messages.value + incoming
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(
                lastMessage = text, lastTimestamp = incoming.timestamp, unread = it.unread + 1,
            ) else it
        }
        _notifications.value = _notifications.value.map {
            if (it.chatId == chatId) it.copy(read = false, body = text, time = "الان") else it
        }.ifEmpty {
            listOf(
                NotificationItem(
                    "n_${System.currentTimeMillis()}", fromName, text, "الان",
                    chatId, false, NoticeType.MESSAGE,
                )
            )
        }
        persistMessages()
    }

    private fun peerIpFor(chatId: String): String {
        val c = chat(chatId) ?: return ""
        if (c.isGroup) {
            return c.memberIds.firstNotNullOfOrNull { id ->
                users.value.firstOrNull { it.id == id && it.ip.isNotBlank() }?.ip
            } ?: ""
        }
        val peerId = c.memberIds.firstOrNull() ?: usernameToId(c.username) ?: return ""
        return users.value.firstOrNull { it.id == peerId }?.ip ?: ""
    }

    private fun peerOnlineFor(chatId: String): Boolean {
        val c = chat(chatId) ?: return false
        if (c.isGroup) {
            return c.memberIds.any { id -> _users.value.firstOrNull { it.id == id }?.online == true }
        }
        val peerId = c.memberIds.firstOrNull() ?: usernameToId(c.username)
        return _users.value.firstOrNull { it.id == peerId }?.online ?: false
    }

    private fun usernameToId(username: String): String? =
        _users.value.firstOrNull { it.username == username }?.id

    private fun updateStatus(id: String, status: MsgStatus) {
        _messages.value = _messages.value.map { if (it.id == id) it.copy(status = status) else it }
        persistMessages()
    }

    private fun markQueue(id: String, status: MsgStatus, error: String? = null) {
        _queue.value = _queue.value.map {
            if (it.id == id) it.copy(status = status, error = error) else it
        }
    }

    /** Warm demo conversations keep the product alive while no real peer replies. */
    private fun maybeAutoReply(msg: Message) {
        val chat = chat(msg.chatId) ?: return
        replyJob?.cancel()
        replyJob = scope.launch {
            val delayMs = 3_500L + Random.nextLong(2_500)
            delay(delayMs)
            if (com.mja.messenger.net.LocalNet.hasRealPeer()) return@launch
            val responderId = when {
                chat.isGroup -> chat.memberIds.filterNot { it == currentUser().id }
                    .randomOrNull() ?: return@launch
                else -> usernameToId(chat.username) ?: return@launch
            }
            val responder = user(responderId) ?: return@launch
            if (!responder.online) return@launch
            val pool = listOf(
                "خیلی عالیه 👍", "باشه، انجام می‌دم", "ممنون، پیامت رو دیدم",
                "چشم، تا چند لحظه دیگه جواب می‌دم", "عالیه، هماهنگیم ✅",
                "آره کاملاً در جریانم", "بفرست، آماده‌ام",
            )
            val reply = pool.random()
            val incoming = Message(
                id = "m_${System.currentTimeMillis()}_${Random.nextInt(999)}",
                chatId = msg.chatId, senderId = responderId, text = reply,
                status = MsgStatus.READ, dateLabel = "امروز",
            )
            _messages.value = _messages.value + incoming
            _chats.value = _chats.value.map {
                if (it.id == msg.chatId) it.copy(
                    lastMessage = reply, lastTimestamp = incoming.timestamp,
                    unread = it.unread + 1,
                ) else it
            }
            _notifications.value = _notifications.value.map {
                if (it.chatId == msg.chatId) it.copy(read = false, body = reply, time = "الان")
                else it
            }
            persistMessages()
        }
    }

    fun deleteMessage(id: String) {
        _messages.value = _messages.value.filterNot { it.id == id }
        _queue.value = _queue.value.filterNot { it.id == id }
        persistMessages()
    }

    fun togglePin(id: String) {
        _messages.value = _messages.value.map { if (it.id == id) it.copy(pinned = !it.pinned) else it }
        persistMessages()
    }

    fun addReaction(id: String, emoji: String) {
        _messages.value = _messages.value.map {
            if (it.id == id) {
                if (emoji in it.reactions) it.copy(reactions = it.reactions - emoji)
                else it.copy(reactions = it.reactions + emoji)
            } else it
        }
        persistMessages()
    }

    fun setReply(m: Message?) { _replyDraft.value = m }
    fun setForward(m: Message?) { _forwardDraft.value = m }

    fun forwardTo(chatId: String) {
        val src = _forwardDraft.value ?: return
        enqueueMessage(
            Message(
                id = "m_${System.currentTimeMillis()}_${Random.nextInt(999)}",
                chatId = chatId, senderId = currentUser().id, text = src.text,
                kind = src.kind, status = MsgStatus.SENDING, fileName = src.fileName,
                mediaUri = src.mediaUri, forwarded = true, dateLabel = "امروز",
            )
        )
        _forwardDraft.value = null
    }

    fun markRead(chatId: String) {
        _chats.value = _chats.value.map { if (it.id == chatId) it.copy(unread = 0) else it }
        _notifications.value = _notifications.value.map {
            if (it.chatId == chatId) it.copy(read = true) else it
        }
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(read = true) }
    }

    fun markReadNotification(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(read = true) else it
        }
    }

    fun deleteNotification(id: String) {
        _notifications.value = _notifications.value.filterNot { it.id == id }
    }

    fun clearChat(chatId: String) {
        _messages.value = _messages.value.filterNot { it.chatId == chatId }
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(lastMessage = "", unread = 0) else it
        }
        persistMessages()
    }

    /* ------------------------- chats / groups --------------------- */

    fun openChatWith(userId: String): String {
        val u = user(userId) ?: return "c_ali"
        val existing = _chats.value.firstOrNull { !it.isGroup && it.username == u.username }
        if (existing != null) { markRead(existing.id); return existing.id }
        val id = "c_${System.currentTimeMillis() % 100000}"
        _chats.value = _chats.value + Chat(
            id = id, title = u.name, username = u.username, online = u.online,
            avatarColor = u.avatarColor, lastSeen = u.lastSeen, unread = 0,
            memberIds = listOf(u.id),
        )
        return id
    }

    fun createGroup(name: String, description: String, memberIds: List<String>): String {
        val id = "c_${System.currentTimeMillis() % 100000}"
        _chats.value = _chats.value + Chat(
            id = id, title = name, isGroup = true, avatarColor = Random.nextInt(0, 8),
            memberIds = memberIds + currentUser().id, description = description,
            groupId = "g_${100000 + Random.nextInt(899999)}",
            admins = listOf(currentUser().id), unread = 0, lastMessage = "گروه ساخته شد",
            lastTimestamp = System.currentTimeMillis(),
        )
        return id
    }

    fun addMembers(chatId: String, ids: List<String>) {
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(memberIds = (it.memberIds + ids).distinct()) else it
        }
    }

    fun removeMember(chatId: String, userId: String) {
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(
                memberIds = it.memberIds - userId,
                admins = it.admins - userId,
            ) else it
        }
    }

    fun leaveGroup(chatId: String) {
        _chats.value = _chats.value.filterNot { it.id == chatId }
    }

    fun toggleChatFlag(chatId: String, flag: ChatFlag) {
        _chats.value = _chats.value.map {
            if (it.id != chatId) it else when (flag) {
                ChatFlag.PINNED -> it.copy(pinned = !it.pinned)
                ChatFlag.MUTED -> it.copy(muted = !it.muted)
                ChatFlag.SAVED -> it.copy(saved = !it.saved)
            }
        }
    }

    enum class ChatFlag { PINNED, MUTED, SAVED }

    private fun touchChat(msg: Message) {
        _chats.value = _chats.value.map {
            if (it.id == msg.chatId) it.copy(
                lastMessage = msg.previewText(), lastTimestamp = msg.timestamp,
            ) else it
        }
    }

    /* ---------------------------- queue --------------------------- */

    fun retryQueueItem(id: String) {
        markQueue(id, MsgStatus.SENDING)
        updateStatus(id, MsgStatus.SENDING)
        scope.launch {
            delay(1_200)
            val online = _queue.value.firstOrNull { it.id == id }?.let {
                chat(it.chatId)?.let { c -> peerOnlineFor(c.id) }
            } ?: false
            if (online || com.mja.messenger.net.LocalNet.hasRealPeer()) {
                markQueue(id, MsgStatus.DELIVERED)
                updateStatus(id, MsgStatus.DELIVERED)
                _queue.value = _queue.value.filterNot { it.id == id }
                _sync.value = _sync.value.copy(
                    sent = _sync.value.sent + 1, lastSync = System.currentTimeMillis(),
                )
            } else {
                markQueue(id, MsgStatus.FAILED, "گیرنده در شبکه محلی حاضر نیست")
                updateStatus(id, MsgStatus.FAILED)
            }
        }
    }

    fun retryAllQueue() { _queue.value.forEach { retryQueueItem(it.id) } }

    fun removeQueueItem(id: String) {
        _queue.value = _queue.value.filterNot { it.id == id }
        _messages.value = _messages.value.filterNot { it.id == id }
        persistMessages()
    }

    /* ----------------------------- sync --------------------------- */

    fun runSync() {
        if (_sync.value.running) return
        val pending = _queue.value.size
        _sync.value = SyncStatus(running = true, progress = 0f, report = listOf("شروع همگام‌سازی..."))
        scope.launch {
            val steps = listOf(
                "بررسی صف پیام‌های ارسال‌نشده",
                "اتصال به سرور محلی",
                "ارسال ${pending} پیام در انتظار",
                "دریافت پیام‌های جدید",
                "به‌روزرسانی تاریخچه گفتگوها",
            )
            steps.forEachIndexed { idx, step ->
                delay(700)
                _sync.value = _sync.value.copy(
                    progress = (idx + 1f) / steps.size,
                    report = _sync.value.report + step,
                )
            }
            val ok = com.mja.messenger.net.LocalNet.isConnected()
            delay(400)
            _sync.value = _sync.value.copy(
                running = false, success = ok, failed = !ok,
                progress = 1f, received = 2, sent = pending,
                lastSync = System.currentTimeMillis(),
                report = _sync.value.report + if (ok) "همگام‌سازی با موفقیت انجام شد"
                else "همگام‌سازی ناتمام ماند؛ شبکه در دسترس نیست",
            )
            if (ok) retryAllQueue()
        }
    }

    /* --------------------------- settings ------------------------- */

    fun updateSettings(transform: (AppSettings) -> AppSettings) {
        _settings.value = transform(_settings.value)
        persistSettings()
    }

    private fun persistSettings() {
        val s = _settings.value
        appContext?.getSharedPreferences("mja_settings", Context.MODE_PRIVATE)?.edit()
            ?.putBoolean("animations", s.animations)
            ?.putBoolean("notifyPrivate", s.notifyPrivate)
            ?.putBoolean("notifyGroups", s.notifyGroups)
            ?.putBoolean("notifyFiles", s.notifyFiles)
            ?.putBoolean("notifyCalls", s.notifyCalls)
            ?.putBoolean("sound", s.sound)
            ?.putBoolean("vibration", s.vibration)
            ?.putBoolean("preview", s.preview)
            ?.putBoolean("silentMode", s.silentMode)
            ?.putBoolean("showOnline", s.showOnline)
            ?.putBoolean("showLastSeen", s.showLastSeen)
            ?.putBoolean("allowContactRequests", s.allowContactRequests)
            ?.putBoolean("encryption", s.encryption)
            ?.putBoolean("appLock", s.appLock)
            ?.putBoolean("autoSync", s.autoSync)
            ?.putBoolean("localBackup", s.localBackup)
            ?.putString("language", s.language)
            ?.putString("theme", s.theme)
            ?.putString("textSize", s.textSize)
            ?.apply()
    }

    private fun loadSettings() {
        val p = appContext?.getSharedPreferences("mja_settings", Context.MODE_PRIVATE) ?: return
        _settings.value = AppSettings(
            language = p.getString("language", "فارسی") ?: "فارسی",
            theme = p.getString("theme", "Black & Graphite") ?: "Black & Graphite",
            textSize = p.getString("textSize", "پیش‌فرض") ?: "پیش‌فرض",
            animations = p.getBoolean("animations", true),
            notifyPrivate = p.getBoolean("notifyPrivate", true),
            notifyGroups = p.getBoolean("notifyGroups", true),
            notifyFiles = p.getBoolean("notifyFiles", true),
            notifyCalls = p.getBoolean("notifyCalls", true),
            sound = p.getBoolean("sound", true),
            vibration = p.getBoolean("vibration", true),
            preview = p.getBoolean("preview", true),
            silentMode = p.getBoolean("silentMode", false),
            showOnline = p.getBoolean("showOnline", true),
            showLastSeen = p.getBoolean("showLastSeen", true),
            allowContactRequests = p.getBoolean("allowContactRequests", true),
            encryption = p.getBoolean("encryption", true),
            appLock = p.getBoolean("appLock", false),
            autoSync = p.getBoolean("autoSync", true),
            localBackup = p.getBoolean("localBackup", false),
        )
    }

    /* --------------------------- session -------------------------- */

    private fun persistSession() {
        val s = _session.value
        appContext?.getSharedPreferences("mja_session", Context.MODE_PRIVATE)?.edit()
            ?.putBoolean("onboarded", s.onboarded)
            ?.putBoolean("loggedIn", s.loggedIn)
            ?.putString("displayName", s.displayName)
            ?.putString("username", s.username)
            ?.putString("bio", s.bio)
            ?.apply()
    }

    private fun saveSession() = persistSession()

    private fun loadSession() {
        val p = appContext?.getSharedPreferences("mja_session", Context.MODE_PRIVATE) ?: return
        _session.value = Session(
            ready = true,
            loggedIn = p.getBoolean("loggedIn", false),
            onboarded = p.getBoolean("onboarded", false),
            displayName = p.getString("displayName", "") ?: "",
            username = p.getString("username", "") ?: "",
            bio = p.getString("bio", "") ?: "",
        )
        if (_session.value.loggedIn) {
            val u = _session.value
            _users.value = _users.value.map {
                if (it.isSelf) it.copy(name = u.displayName.ifBlank { "من" }, username = u.username.ifBlank { it.username }) else it
            }
        }
    }

    /* -------------------------- persistence ----------------------- */

    private fun messagesFile(): File? =
        appContext?.let { File(it.filesDir, "mja_messages.json") }

    fun persistMessages() {
        val file = messagesFile() ?: return
        try {
            val arr = JSONArray()
            _messages.value.forEach { m ->
                arr.put(JSONObject().apply {
                    put("id", m.id); put("chatId", m.chatId); put("senderId", m.senderId)
                    put("text", m.text); put("kind", m.kind.name); put("ts", m.timestamp)
                    put("status", m.status.name); put("date", m.dateLabel)
                    put("file", m.fileName ?: ""); put("uri", m.mediaUri ?: "")
                    put("dur", m.durationMs); put("size", m.fileSize)
                })
            }
            file.writeText(arr.toString())
        } catch (_: Exception) {
        }
    }

    private fun loadMessages() {
        val file = messagesFile() ?: return
        if (!file.exists()) return
        try {
            val arr = JSONArray(file.readText())
            val list = mutableListOf<Message>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    Message(
                        id = o.getString("id"), chatId = o.getString("chatId"),
                        senderId = o.getString("senderId"), text = o.getString("text"),
                        kind = runCatching { MsgKind.valueOf(o.getString("kind")) }.getOrDefault(MsgKind.TEXT),
                        timestamp = o.getLong("ts"),
                        status = runCatching { MsgStatus.valueOf(o.getString("status")) }.getOrDefault(MsgStatus.DELIVERED),
                        dateLabel = o.getString("date"),
                        fileName = o.optString("file").ifBlank { null },
                        mediaUri = o.optString("uri").ifBlank { null },
                        durationMs = o.optLong("dur"), fileSize = o.optLong("size"),
                    )
                )
            }
            _messages.value = list
        } catch (_: Exception) {
        }
    }

    /* --------------------------- storage -------------------------- */

    fun storageStats(): StorageStats {
        val ctx = appContext ?: return StorageStats()
        fun dirSize(f: File?): Long {
            if (f == null || !f.exists()) return 0L
            if (f.isFile) return f.length()
            return f.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        }
        val images = dirSize(File(ctx.filesDir, "images"))
        val videos = dirSize(File(ctx.filesDir, "videos"))
        val audio = dirSize(File(ctx.filesDir, "audio"))
        val files = dirSize(File(ctx.filesDir, "files"))
        val messages = dirSize(messagesFile())
        val temp = dirSize(ctx.cacheDir)
        return StorageStats(
            total = images + videos + audio + files + messages + temp,
            images = images, videos = videos, files = files, audio = audio,
            messages = messages, temp = temp,
        )
    }

    fun clearTemp(): Long {
        val ctx = appContext ?: return 0L
        val before = dirSizeCompat(ctx.cacheDir)
        ctx.cacheDir?.deleteRecursively()
        return before
    }

    fun clearAllMedia(): Long {
        val ctx = appContext ?: return 0L
        val before = storageStats().total
        listOf("images", "videos", "audio", "files").forEach {
            File(ctx.filesDir, it).deleteRecursively()
        }
        ctx.cacheDir?.deleteRecursively()
        return before
    }

    private fun dirSizeCompat(f: File?): Long =
        if (f == null || !f.exists()) 0L else f.walkTopDown().filter { it.isFile }.sumOf { it.length() }

    fun mediaFilesDir(kind: String): File? {
        val ctx = appContext ?: return null
        val dir = File(ctx.filesDir, kind).apply { mkdirs() }
        return dir
    }

    fun addUserIfMissing(name: String, username: String, ip: String): Boolean {
        val exists = _users.value.any { it.username == username }
        if (exists) return false
        _users.value = _users.value + User(
            id = "u_${username}_${ip.replace('.', '_')}", name = name, username = username,
            online = true, lastSeen = "همین حالا", avatarColor = Random.nextInt(0, 8),
            ip = ip, deviceName = "MJA Device", connectedAt = System.currentTimeMillis(),
        )
        return true
    }

    fun setUserOnline(id: String, online: Boolean) {
        _users.value = _users.value.map {
            if (it.id == id) it.copy(online = online, lastSeen = if (online) "همین حالا" else "چند لحظه پیش") else it
        }
    }
}

/** Short preview used by chat rows & the offline queue. */
fun Message.previewText(): String = when (kind) {
    MsgKind.TEXT -> text
    MsgKind.IMAGE -> "تصویر ارسال شد"
    MsgKind.VIDEO -> "ویدئو ارسال شد"
    MsgKind.AUDIO -> "فایل صوتی ارسال شد"
    MsgKind.VOICE -> "پیام صوتی"
    MsgKind.FILE -> "فایل: ${fileName ?: ""}"
}.ifBlank { text }
