# MJA Offline Messenger

> **Chat Without Internet.** · **Private. Local. Secure.**

A fully offline messenger for local networks (LAN/Hotspot) — no internet, no cloud servers, no tracking.
Persian/RTL minimalist dark UI, built with Kotlin and Jetpack Compose.

---

## 📦 Final Deliverable

| Item | Value |
|---|---|
| Final release file | **`MJA_realese_1.1.0.apk`** |
| versionName | **1.1.0** |
| versionCode | 1 |
| minSdk | 24 (Android 7.0) |
| targetSdk | 35 |
| compileSdk | 36 |
| Signing | Release (project-specific keystore) |
| APK architectures | universal (arm64-v8a / armeabi-v7a / x86 / x86_64) |

**Install:** copy the APK to your Android device and tap **Install**
(enable **Install from unknown sources** if prompted).

---

## ✨ Key Features

### Real offline communication (no internet required)
- **Automatic peer discovery** via UDP broadcast on port `47777` over the local network
- **Send/receive messages** over TCP on port `47778` with delivery **ACK** confirmation
- **Built-in hotspot management** (Android LocalOnlyHotspot), respecting OS limitations
- **Offline message queue**: messages are stored locally until the receiver appears on the LAN,
  then delivered automatically upon reconnection
- **Sync engine** with operation report, status states and progress percentage
- **Connected devices list** from the ARP table plus discovered peers
- **Demo mode**: sample auto-replies are sent only when no real peer is present on the network

### User interface (39 routes / 38+ screens)
| Category | Screens |
|---|---|
| Auth & account | Welcome, Login, Register, Profile setup |
| Main tabs | Home (conversation timeline), Contacts, Groups, Notifications, Settings |
| Chat | Private chat, Group chat, Message details, Message search, Voice recording |
| Profiles | User profile, Edit profile, Nearby users, Global search |
| Groups | Create group, Group info (members / shared media / management) |
| Media & files | Media gallery, Image viewer, Video player, File manager, File transfer details |
| Calls | Voice call, Video call |
| System | Offline queue, Sync, Network status, Hotspot management, Connected devices, Host admin |
| Settings | Settings, Privacy & security, Appearance, Storage, Notification settings, About |

- **Bottom navigation**: Home · Contacts · Groups · Notifications · Settings
  (hidden automatically on fullscreen pages)
- Every screen implements all required states: normal, **empty**, **error** and
  **loading** (skeleton loaders)
- Rich dialogs and bottom sheets: message actions, attachments, emoji, delete confirmation,
  forwarding, and more

### MJA Design System
- **Key colors:**
  - Background: `#090A0C`
  - Cards: `#171A20`
  - Outgoing bubble: `#252C38`
  - Incoming bubble: `#191C22`
  - Success: `#37C98B`
  - Error: `#E35F68`
  - Brand/accents: emerald green `#37C98B` and gold/orange accent
- **Typography scale:** 22 / 17 / 15 / 14 / 12 / 11 sp
- **Bundled Vazirmatn font** (Regular / Medium / SemiBold / Bold)
- **Full forced RTL** Persian layout everywhere, with an `LtrText` component for IPs, IDs
  and usernames
- Radii: cards 16dp, buttons 14dp, primary button height 52dp, screen margin 20dp
- Dark neutral palette with high contrast; no color-only status indicators
  (color is always paired with text/icon)

---

## 🛠 Technology Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.4.21 |
| UI | Jetpack Compose + Material 3 (Compose BOM 2026.09.00) |
| Navigation | Navigation Compose 2.7.5 (39 routes) |
| Architecture | Single-Activity · StateFlow-based |
| Persistence | SharedPreferences + JSON files (org.json) |
| Networking | Native Java UDP/TCP sockets (zero internet dependency) |
| Media | Media3 1.4.1 · Coil 2.5.0 |
| Build | Gradle 9.6.0 · AGP 9.4.1 |

### Project Structure
```
MJAOfflineMessenger/
├── app/src/main/java/com/mja/messenger/
│   ├── MainActivity.kt            # App entry, forced Persian locale
│   ├── MjaApp.kt                  # Repo + LocalNet bootstrap
│   ├── data/                      # Models · Repo (state, queue, sync)
│   ├── net/                       # LocalNet (discovery, transport, hotspot)
│   ├── navigation/AppNav.kt       # 39 navigation routes
│   ├── ui/
│   │   ├── MainScaffold.kt        # Tabbed-screen scaffold
│   │   ├── components/            # Shared components (buttons, fields, bubbles, ...)
│   │   └── theme/                 # Color · Type · Theme (Vazirmatn)
│   └── screens/                   # 38+ UI screens
├── keystore/mja_release.jks       # Release signing key
└── README.md
```

---

## 🔨 Building

Prerequisites: JDK 21 · Android SDK (platform 36 + build-tools 36) · Gradle via wrapper

```powershell
# Windows
Set-Location MJAOfflineMessenger
.\gradlew.bat --no-daemon assembleRelease
```

```bash
# Linux / macOS
cd MJAOfflineMessenger
./gradlew --no-daemon assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`
Rename to the deliverable file:

```powershell
Copy-Item app\build\outputs\apk\release\app-release.apk MJA_realese_1.1.0.apk
```

Release signing is already configured in `app/build.gradle.kts`
using `keystore/mja_release.jks` (alias: `mja`).

---

## 🔐 Permissions

| Permission | Purpose |
|---|---|
| `ACCESS_WIFI_STATE` / `CHANGE_WIFI_STATE` | Peer discovery and hotspot management |
| `ACCESS_FINE_LOCATION` | OS requirement for LocalOnlyHotspot and Wi-Fi scanning |
| `NEARBY_WIFI_DEVICES` (Android 13+) | Location alternative for Wi-Fi operations |
| `INTERNET` | Local LAN sockets only (no external traffic) |
| `RECORD_AUDIO` | Voice messages and voice calls |
| `CAMERA` | Video calls |
| `POST_NOTIFICATIONS` (Android 13+) | Message and group notifications |
| `READ_MEDIA_*` | Picking and displaying images/videos/files |

> ⚠️ **Honest note about hotspot control:** some Android versions do not allow third-party
> apps to create/stop the system hotspot automatically. In that case MJA redirects the user
> to system settings. Automatic hotspot creation cannot be guaranteed on every device —
> this is an OS limitation, not an app bug.

---

## 📋 Version History

### 1.1.0 — current release
- Completed all 38+ screens from the design spec (normal / empty / error / loading states)
- Real LAN transport: UDP discovery + TCP with ACK + offline queue
- Full hotspot management, connected devices list and host admin panel
- Minimalist dark Persian theme with bundled Vazirmatn font and full RTL
- Signed final output: `MJA_realese_1.1.0.apk`

---

## 📄 License

Provided for personal and educational use.
MJA OFFLINE MESSENGER — CHAT WITHOUT INTERNET.