package com.mja.messenger.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mja.messenger.screens.*

/** Central route registry of MJA. */
object Routes {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE_SETUP = "profile_setup"

    const val HOME = "home"
    const val CONTACTS = "contacts"
    const val GROUPS = "groups"
    const val NOTIFICATIONS = "notifications"
    const val SETTINGS = "settings"

    const val NEARBY = "nearby"
    const val SEARCH = "search"
    const val CHAT = "chat/{chatId}"
    const val GROUP_CHAT = "gchat/{chatId}"
    const val MESSAGE_DETAILS = "msg_details/{msgId}"
    const val MESSAGE_SEARCH = "msg_search"
    const val CREATE_GROUP = "create_group"
    const val GROUP_INFO = "group_info/{chatId}"
    const val USER_PROFILE = "user_profile/{userId}"
    const val EDIT_PROFILE = "edit_profile"

    const val MEDIA = "media/{chatId}"
    const val IMAGE_VIEWER = "image_viewer"
    const val VIDEO_PLAYER = "video_player"
    const val FILES = "files"
    const val TRANSFER = "transfer/{fileId}"

    const val VOICE_CALL = "voice_call/{userId}"
    const val VIDEO_CALL = "video_call/{userId}"
    const val VOICE_RECORDING = "voice_rec/{chatId}"

    const val QUEUE = "queue"
    const val SYNC = "sync"

    const val NETWORK = "network"
    const val HOTSPOT = "hotspot"
    const val DEVICES = "devices"
    const val HOST = "host"

    const val PRIVACY = "privacy"
    const val APPEARANCE = "appearance"
    const val STORAGE = "storage"
    const val NOTIF_SETTINGS = "notif_settings"
    const val ABOUT = "about"

    fun chat(id: String) = "chat/$id"
    fun groupChat(id: String) = "gchat/$id"
    fun messageDetails(id: String) = "msg_details/$id"
    fun groupInfo(id: String) = "group_info/$id"
    fun userProfile(id: String) = "user_profile/$id"
    fun media(id: String) = "media/$id"
    fun transfer(id: String) = "transfer/$id"
    fun voiceCall(id: String) = "voice_call/$id"
    fun videoCall(id: String) = "video_call/$id"
    fun voiceRecord(id: String) = "voice_rec/$id"
}

@Composable
fun MjaNavHost(navController: NavHostController = rememberNavController()) {

    fun go(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    fun tab(index: Int) {
        val route = when (index) {
            0 -> Routes.HOME
            1 -> Routes.CONTACTS
            2 -> Routes.GROUPS
            3 -> Routes.NOTIFICATIONS
            else -> Routes.SETTINGS
        }
        navController.navigate(route) {
            popUpTo(Routes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun back() { navController.popBackStack() }

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        /* ---------------- A. auth & onboarding ---------------- */
        composable(Routes.SPLASH) {
            SplashScreen(onNavigate = { go(it) })
        }
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onStart = { go(Routes.LOGIN) },
                onAbout = { go(Routes.ABOUT) },
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onRegister = { go(Routes.REGISTER) },
                onForgot = { go(Routes.ABOUT) },
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onBack = { back() },
                onDone = {
                    navController.navigate(Routes.PROFILE_SETUP) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.PROFILE_SETUP) {
            ProfileSetupScreen(
                onDone = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        /* ---------------- main tabs ---------------- */
        composable(Routes.HOME) {
            HomeScreen(
                onOpenChat = { go(Routes.chat(it)) },
                onNewChat = { go(Routes.CONTACTS) },
                onOpen = { go(it) },
                tab = { tab(it) },
            )
        }
        composable(Routes.CONTACTS) {
            ContactsScreen(
                onBack = null,
                onOpenProfile = { go(Routes.userProfile(it)) },
                onOpenChat = { go(Routes.chat(it)) },
                onOpenNearby = { go(Routes.NEARBY) },
                onAddContact = { go(Routes.PROFILE_SETUP) },
                tab = { tab(it) },
            )
        }
        composable(Routes.GROUPS) {
            GroupsScreen(
                onBack = null,
                onOpenGroup = { go(Routes.groupChat(it)) },
                onCreate = { go(Routes.CREATE_GROUP) },
                tab = { tab(it) },
            )
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(
                onBack = null,
                onOpenChat = { go(Routes.chat(it)) },
                tab = { tab(it) },
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = null,
                onOpen = { go(it) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                tab = { tab(it) },
            )
        }

        /* ---------------- contacts & profiles ---------------- */
        composable(Routes.NEARBY) {
            NearbyUsersScreen(
                onBack = { back() },
                onOpenChat = { go(Routes.chat(it)) },
                onOpenProfile = { go(Routes.userProfile(it)) },
            )
        }
        composable(
            route = Routes.SEARCH,
        ) {
            SearchScreen(
                onBack = { back() },
                onOpenChat = { go(Routes.chat(it)) },
                onOpenProfile = { go(Routes.userProfile(it)) },
                onOpenGroup = { go(Routes.groupChat(it)) },
            )
        }
        composable(
            route = Routes.USER_PROFILE,
            arguments = listOf(navArgument("userId") { type = NavType.StringType }),
        ) { entry ->
            val userId = entry.arguments?.getString("userId") ?: "u_me"
            UserProfileScreen(
                userId = userId,
                onBack = { back() },
                onMessage = { go(Routes.chat(com.mja.messenger.data.Repo.openChatWith(it))) },
                onVoiceCall = { go(Routes.voiceCall(it)) },
                onVideoCall = { go(Routes.videoCall(it)) },
                onEditProfile = { go(Routes.EDIT_PROFILE) },
            )
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(onBack = { back() }, onSaved = { back() })
        }

        /* ---------------- messaging ---------------- */
        composable(
            route = Routes.CHAT,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType }),
        ) { entry ->
            val chatId = entry.arguments?.getString("chatId") ?: ""
            PrivateChatScreen(
                chatId = chatId,
                onBack = { back() },
                onOpenProfile = { go(Routes.userProfile(it)) },
                onOpenGroupInfo = { go(Routes.groupInfo(chatId)) },
                onOpenMedia = { go(Routes.media(chatId)) },
                onOpenMessageSearch = { go(Routes.MESSAGE_SEARCH) },
                onMessageDetails = { go(Routes.messageDetails(it)) },
                onVoiceCall = { go(Routes.voiceCall(it)) },
                onVideoCall = { go(Routes.videoCall(it)) },
                onRecordVoice = { go(Routes.voiceRecord(chatId)) },
            )
        }
        composable(
            route = Routes.GROUP_CHAT,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType }),
        ) { entry ->
            val chatId = entry.arguments?.getString("chatId") ?: ""
            GroupChatScreen(
                chatId = chatId,
                onBack = { back() },
                onOpenGroupInfo = { go(Routes.groupInfo(chatId)) },
                onOpenMedia = { go(Routes.media(chatId)) },
                onOpenMessageSearch = { go(Routes.MESSAGE_SEARCH) },
                onMessageDetails = { go(Routes.messageDetails(it)) },
                onOpenProfile = { go(Routes.userProfile(it)) },
                onRecordVoice = { go(Routes.voiceRecord(chatId)) },
            )
        }
        composable(
            route = Routes.MESSAGE_DETAILS,
            arguments = listOf(navArgument("msgId") { type = NavType.StringType }),
        ) { entry ->
            MessageDetailsScreen(
                messageId = entry.arguments?.getString("msgId") ?: "",
                onBack = { back() },
            )
        }
        composable(Routes.MESSAGE_SEARCH) {
            MessageSearchScreen(
                onBack = { back() },
                onOpenChat = { navController.navigate(Routes.chat(it)) { popUpTo(Routes.MESSAGE_SEARCH) { inclusive = true } } },
            )
        }
        composable(Routes.CREATE_GROUP) {
            CreateGroupScreen(
                onBack = { back() },
                onCreated = { id ->
                    navController.navigate(Routes.groupChat(id)) {
                        popUpTo(Routes.GROUPS) { inclusive = false }
                    }
                },
            )
        }
        composable(
            route = Routes.GROUP_INFO,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType }),
        ) { entry ->
            val chatId = entry.arguments?.getString("chatId") ?: ""
            GroupInfoScreen(
                chatId = chatId,
                onBack = { back() },
                onOpenMember = { go(Routes.userProfile(it)) },
                onOpenMedia = { go(Routes.media(chatId)) },
                onLeave = {
                    navController.navigate(Routes.GROUPS) {
                        popUpTo(Routes.GROUPS) { inclusive = true }
                    }
                },
            )
        }

        /* ---------------- media & files ---------------- */
        composable(
            route = Routes.MEDIA,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType }),
        ) { entry ->
            MediaGalleryScreen(
                chatId = entry.arguments?.getString("chatId"),
                onBack = { back() },
                onOpenImage = { go(Routes.IMAGE_VIEWER) },
                onOpenVideo = { go(Routes.VIDEO_PLAYER) },
            )
        }
        composable(Routes.IMAGE_VIEWER) {
            ImageViewerScreen(onBack = { back() })
        }
        composable(Routes.VIDEO_PLAYER) {
            VideoPlayerScreen(onBack = { back() })
        }
        composable(Routes.FILES) {
            FileManagerScreen(
                onBack = { back() },
                onOpenTransfer = { go(Routes.transfer(it)) },
                onSendFile = { back() },
            )
        }
        composable(
            route = Routes.TRANSFER,
            arguments = listOf(navArgument("fileId") { type = NavType.StringType }),
        ) { entry ->
            FileTransferScreen(
                fileId = entry.arguments?.getString("fileId") ?: "",
                onBack = { back() },
            )
        }

        /* ---------------- calls ---------------- */
        composable(
            route = Routes.VOICE_CALL,
            arguments = listOf(navArgument("userId") { type = NavType.StringType }),
        ) { entry ->
            VoiceCallScreen(
                userId = entry.arguments?.getString("userId") ?: "u_ali",
                onBack = { back() },
            )
        }
        composable(
            route = Routes.VIDEO_CALL,
            arguments = listOf(navArgument("userId") { type = NavType.StringType }),
        ) { entry ->
            VideoCallScreen(
                userId = entry.arguments?.getString("userId") ?: "u_ali",
                onBack = { back() },
            )
        }
        composable(
            route = Routes.VOICE_RECORDING,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType }),
        ) { entry ->
            VoiceRecordingScreen(
                chatId = entry.arguments?.getString("chatId") ?: "",
                onBack = { back() },
            )
        }

        /* ---------------- offline queue & sync ---------------- */
        composable(Routes.QUEUE) {
            OfflineQueueScreen(onBack = { back() }, onOpenChat = { go(Routes.chat(it)) })
        }
        composable(Routes.SYNC) {
            SyncScreen(onBack = { back() })
        }

        /* ---------------- network & host ---------------- */
        composable(Routes.NETWORK) {
            NetworkStatusScreen(
                onBack = { back() },
                onOpenHotspot = { go(Routes.HOTSPOT) },
                onOpenDevices = { go(Routes.DEVICES) },
                onOpenHost = { go(Routes.HOST) },
            )
        }
        composable(Routes.HOTSPOT) {
            HotspotScreen(
                onBack = { back() },
                onOpenDevices = { go(Routes.DEVICES) },
                onOpenSettings = { go(Routes.NETWORK) },
            )
        }
        composable(Routes.DEVICES) {
            ConnectedDevicesScreen(
                onBack = { back() },
                onOpenProfile = { go(Routes.userProfile(it)) },
            )
        }
        composable(Routes.HOST) {
            HostAdminScreen(
                onBack = { back() },
                onOpenDevices = { go(Routes.DEVICES) },
                onOpenSettings = { go(Routes.SETTINGS) },
            )
        }

        /* ---------------- settings ---------------- */
        composable(Routes.PRIVACY) {
            PrivacySecurityScreen(onBack = { back() }, onOpen = { go(it) })
        }
        composable(Routes.APPEARANCE) {
            AppearanceScreen(onBack = { back() })
        }
        composable(Routes.STORAGE) {
            StorageScreen(onBack = { back() })
        }
        composable(Routes.NOTIF_SETTINGS) {
            NotificationSettingsScreen(onBack = { back() })
        }
        composable(Routes.ABOUT) {
            AboutScreen(onBack = { back() })
        }
    }
}
