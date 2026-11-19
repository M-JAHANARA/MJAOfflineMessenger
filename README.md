◈ MJA OFFLINE MESSENGER

<div align="center"><a href="https://github.com/M-JAHANARA/MJAOfflineMessenger">
  <img src="assets/banner.png" width="100%" alt="MJA Offline Messenger">
</a><br><img src="https://readme-typing-svg.demolab.com?font=JetBrains+Mono&weight=700&size=25&duration=3000&pause=1000&color=37C98B&center=true&vCenter=true&repeat=true&width=650&height=55&lines=CHAT+WITHOUT+INTERNET.;PRIVATE.+LOCAL.+SECURE.;COMMUNICATION+WITHOUT+BORDERS." alt="Typing SVG"><br>A decentralized offline messaging experience.

Built for local communication. Designed for privacy. Powered by Kotlin.

<br><a href="https://github.com/M-JAHANARA/MJAOfflineMessenger">
<img src="https://img.shields.io/badge/MJA-Offline%20Messenger-090A0C?style=for-the-badge&logo=android&logoColor=37C98B" alt="MJA">
</a>
<a href="https://github.com/M-JAHANARA/MJAOfflineMessenger/releases">
<img src="https://img.shields.io/github/v/release/M-JAHANARA/MJAOfflineMessenger?style=for-the-badge&color=37C98B&label=RELEASE" alt="Release">
</a>
<a href="https://github.com/M-JAHANARA/MJAOfflineMessenger">
<img src="https://img.shields.io/github/stars/M-JAHANARA/MJAOfflineMessenger?style=for-the-badge&color=E5A94D&label=STARS" alt="Stars">
</a><br><a href="#-live-project-dashboard">DASHBOARD</a> ·
<a href="#-features">FEATURES</a> ·
<a href="#-architecture">ARCHITECTURE</a> ·
<a href="#-installation">INSTALLATION</a> ·
<a href="#-developer">DEVELOPER</a>

<br><img src="https://img.shields.io/badge/PLATFORM-ANDROID-37C98B?style=flat-square">
<img src="https://img.shields.io/badge/LANGUAGE-KOTLIN-7F52FF?style=flat-square">
<img src="https://img.shields.io/badge/UI-JETPACK%20COMPOSE-4285F4?style=flat-square">
<img src="https://img.shields.io/badge/NETWORK-UDP%20%2B%20TCP-37C98B?style=flat-square">
<img src="https://img.shields.io/badge/INTERNET-NOT%20REQUIRED-37C98B?style=flat-square"></div>---

◈ 01. LIVE PROJECT DASHBOARD

<div align="center">PROJECT STATUS

<img src="https://img.shields.io/github/last-commit/M-JAHANARA/MJAOfflineMessenger?style=for-the-badge&color=37C98B&label=LAST%20COMMIT"><img src="https://img.shields.io/github/commit-activity/m/M-JAHANARA/MJAOfflineMessenger?style=for-the-badge&color=37C98B&label=MONTHLY%20ACTIVITY"><img src="https://img.shields.io/github/issues/M-JAHANARA/MJAOfflineMessenger?style=for-the-badge&color=E35F68&label=OPEN%20ISSUES"><img src="https://img.shields.io/github/issues-pr/M-JAHANARA/MJAOfflineMessenger?style=for-the-badge&color=4285F4&label=PULL%20REQUESTS"><img src="https://img.shields.io/github/repo-size/M-JAHANARA/MJAOfflineMessenger?style=for-the-badge&color=37C98B&label=REPOSITORY%20SIZE"><img src="https://img.shields.io/github/languages/top/M-JAHANARA/MJAOfflineMessenger?style=for-the-badge&color=7F52FF&label=PRIMARY%20LANGUAGE"><br>LATEST RELEASE

<a href="https://github.com/M-JAHANARA/MJAOfflineMessenger/releases/latest">
<img src="https://img.shields.io/github/v/release/M-JAHANARA/MJAOfflineMessenger?display_name=tag&style=for-the-badge&color=37C98B&logo=github&label=LATEST%20VERSION">
</a><a href="https://github.com/M-JAHANARA/MJAOfflineMessenger/releases">
<img src="https://img.shields.io/github/downloads/M-JAHANARA/MJAOfflineMessenger/total?style=for-the-badge&color=E5A94D&label=TOTAL%20DOWNLOADS">
</a><br>BUILD STATUS

<a href="https://github.com/M-JAHANARA/MJAOfflineMessenger/actions">
<img src="https://img.shields.io/github/actions/workflow/status/M-JAHANARA/MJAOfflineMessenger/android.yml?branch=main&style=for-the-badge&label=ANDROID%20BUILD">
</a></div>---

◈ 02. ABOUT MJA

MJA Offline Messenger is an Android application designed to enable direct communication between nearby devices without requiring an internet connection.

Traditional messaging platforms depend on centralized servers and internet connectivity.

MJA takes a different approach.

It uses local network communication technologies to discover nearby devices, establish connections and exchange messages directly over a LAN or Wi-Fi hotspot.

CORE PHILOSOPHY

NO INTERNET.
NO CLOUD SERVERS.
NO CENTRALIZED MESSAGING INFRASTRUCTURE.

JUST LOCAL COMMUNICATION.

PROJECT INFORMATION

Specification| Value
Project Name| MJA Offline Messenger
Developer| M.JAHANARA
Current Version| 1.1.0
Platform| Android
Language| Kotlin
UI Framework| Jetpack Compose
Architecture| Single Activity + StateFlow
Minimum SDK| 24
Target SDK| 35
Compile SDK| 36
Network| UDP / TCP
Storage| SharedPreferences + JSON
UI Language| Persian / RTL
License| Personal & Educational

---

◈ 03. FEATURES

<details open>
<summary><b>01 — OFFLINE COMMUNICATION</b></summary>- Automatic peer discovery.
- UDP broadcast discovery.
- Direct TCP communication.
- ACK-based delivery confirmation.
- Offline message queue.
- Automatic reconnection handling.
- Local network monitoring.
- Connected devices management.

</details><details>
<summary><b>02 — MESSAGING SYSTEM</b></summary>- Private conversations.
- Group conversations.
- Message history.
- Message search.
- Message forwarding.
- Message details.
- Message deletion.
- Voice message interface.
- Delivery status.

</details><details>
<summary><b>03 — NETWORK MANAGEMENT</b></summary>- Local network discovery.
- Hotspot management interface.
- Connected devices list.
- Network diagnostics.
- Synchronization engine.
- Host administration.
- Connection monitoring.

</details><details>
<summary><b>04 — USER EXPERIENCE</b></summary>- Minimalist dark interface.
- Full Persian RTL.
- Vazirmatn typography.
- Jetpack Compose UI.
- Loading skeletons.
- Empty states.
- Error handling.
- Dynamic dialogs.
- Bottom sheets.

</details><details>
<summary><b>05 — MEDIA & FILES</b></summary>- Media gallery.
- Image viewer.
- Video player.
- File manager.
- File transfer interface.
- Voice recording interface.

</details>---

◈ 04. ARCHITECTURE

MJA follows a decentralized local-network communication architecture.

<div align="center">NETWORK TOPOLOGY

</div>flowchart TB

    subgraph LAN["LOCAL NETWORK / WI-FI HOTSPOT"]
        A["DEVICE A"]
        B["DEVICE B"]
        C["DEVICE C"]
    end

    D["UDP DISCOVERY<br/>PORT 47777"]

    A <-->|"TCP + ACK<br/>47778"| B
    B <-->|"TCP + ACK<br/>47778"| C
    A <-->|"TCP + ACK<br/>47778"| C

    A -.-> D
    B -.-> D
    C -.-> D

    style LAN fill:#090A0C,stroke:#37C98B,color:#FFFFFF
    style D fill:#171A20,stroke:#E5A94D,color:#FFFFFF

COMMUNICATION PROTOCOLS

Protocol| Port| Purpose
UDP| 47777| Peer discovery
TCP| 47778| Message transport
ACK| TCP| Delivery acknowledgment

MESSAGE LIFECYCLE

flowchart TD

    A["CREATE MESSAGE"] --> B["LOCAL STORAGE"]
    B --> C{"PEER AVAILABLE?"}

    C -->|YES| D["TCP TRANSMISSION"]
    C -->|NO| E["OFFLINE QUEUE"]

    E --> F["PEER RECONNECTED"]
    F --> D

    D --> G{"ACK RECEIVED?"}

    G -->|YES| H["DELIVERED"]
    G -->|NO| I["PENDING"]

    I --> C

    style H fill:#174D3A,stroke:#37C98B,color:#FFFFFF
    style E fill:#332B19,stroke:#E5A94D,color:#FFFFFF

---

◈ 05. APPLICATION SCREENS

38+ Screens · 9 Functional Categories

Module| Screens
Authentication| Welcome, Login, Register, Profile Setup
Main| Home, Contacts, Groups, Notifications, Settings
Messaging| Private Chat, Group Chat, Search, Details
Profiles| User Profile, Edit Profile, Nearby Users
Groups| Create Group, Group Information, Management
Media| Gallery, Image Viewer, Video Player, File Manager
Calls| Voice Call, Video Call
Network| Offline Queue, Sync, Hotspot, Connected Devices
System| Privacy, Appearance, Storage, Notifications, About

SCREENSHOTS

<div align="center">HOME| PRIVATE CHAT
<img src="assets/screenshots/home.png" width="240">| <img src="assets/screenshots/chat.png" width="240">

CONTACTS| GROUPS
<img src="assets/screenshots/contacts.png" width="240">| <img src="assets/screenshots/groups.png" width="240">

NETWORK| SETTINGS
<img src="assets/screenshots/network.png" width="240">| <img src="assets/screenshots/settings.png" width="240">

</div>---

◈ 06. MJA DESIGN SYSTEM

<div align="center">DARK. MINIMAL. PROFESSIONAL.

</div>COLOR PALETTE

Color| HEX| Usage
Background| "#090A0C"| Main Background
Cards| "#171A20"| Components
Outgoing| "#252C38"| Outgoing Messages
Incoming| "#191C22"| Incoming Messages
Emerald| "#37C98B"| Primary Accent
Error| "#E35F68"| Error States
Gold| "#E5A94D"| Secondary Accent

TYPOGRAPHY

Vazirmatn

Regular / Medium / SemiBold / Bold

DESIGN PRINCIPLES

- Dark-first experience.
- Full RTL support.
- Consistent spacing.
- High contrast.
- Minimal visual noise.
- Reusable components.
- Clear interaction feedback.

---

◈ 07. TECHNOLOGY STACK

<div align="center"><img src="https://skillicons.dev/icons?i=kotlin,androidstudio,gradle,java,git,github&theme=dark" alt="Technology Stack"></div>Layer| Technology
Programming| Kotlin
UI| Jetpack Compose
Design| Material 3
Navigation| Navigation Compose
State| StateFlow
Storage| SharedPreferences
Serialization| org.json
Networking| Java UDP / TCP Sockets
Media| AndroidX Media3
Images| Coil
Build| Gradle
Android Plugin| AGP

---

◈ 08. LIVE DEVELOPMENT ACTIVITY

<div align="center">CONTRIBUTION GRAPH

<img src="https://github-readme-activity-graph.vercel.app/graph?username=M-JAHANARA&theme=github-compact&hide_border=true&bg_color=090A0C&color=37C98B&line=37C98B&point=E5A94D" width="100%" alt="GitHub Activity Graph">GITHUB STATISTICS

<img src="https://github-readme-stats.vercel.app/api?username=M-JAHANARA&show_icons=true&theme=transparent&hide_border=true&bg_color=090A0C&title_color=37C98B&icon_color=E5A94D&text_color=C9D1D9" width="49%" alt="GitHub Statistics"><img src="https://github-readme-stats.vercel.app/api/top-langs/?username=M-JAHANARA&layout=compact&theme=transparent&hide_border=true&bg_color=090A0C&title_color=37C98B&text_color=C9D1D9" width="49%" alt="Top Languages"></div>---

◈ 09. PROJECT STRUCTURE

MJAOfflineMessenger/
│
├── app/
│   └── src/main/
│       ├── java/com/mja/messenger/
│       │
│       ├── MainActivity.kt
│       ├── MjaApp.kt
│       │
│       ├── data/
│       │   ├── models/
│       │   ├── repository/
│       │   └── storage/
│       │
│       ├── net/
│       │   ├── discovery/
│       │   ├── transport/
│       │   └── hotspot/
│       │
│       ├── navigation/
│       │   └── AppNav.kt
│       │
│       ├── ui/
│       │   ├── components/
│       │   └── theme/
│       │
│       └── screens/
│
├── assets/
│   ├── banner.png
│   ├── logo.png
│   └── screenshots/
│
├── keystore/
│   └── mja_release.jks
│
├── README.md
└── build.gradle.kts

---

◈ 10. INSTALLATION

<div align="center"><a href="https://github.com/M-JAHANARA/MJAOfflineMessenger/releases/latest">
<img src="https://img.shields.io/badge/DOWNLOAD-ANDROID%20APK-37C98B?style=for-the-badge&logo=android&logoColor=white" alt="Download APK">
</a></div>CURRENT RELEASE

Property| Value
Version| 1.1.0
Version Code| 1
Minimum Android| 7.0
Architecture| Universal
File| MJA_realese_1.1.0.apk

INSTALLATION STEPS

1. Download the APK from Releases.
2. Transfer it to your Android device.
3. Open the APK.
4. Allow installation from the selected source if prompted.
5. Complete installation.
6. Connect devices to the same compatible local network.

---

◈ 11. BUILD FROM SOURCE

REQUIREMENTS

- JDK 21
- Android SDK
- Platform 36
- Build Tools 36
- Gradle Wrapper

WINDOWS

git clone https://github.com/M-JAHANARA/MJAOfflineMessenger.git

cd MJAOfflineMessenger

.\gradlew.bat --no-daemon assembleRelease

LINUX / MACOS

git clone https://github.com/M-JAHANARA/MJAOfflineMessenger.git

cd MJAOfflineMessenger

chmod +x gradlew

./gradlew --no-daemon assembleRelease

OUTPUT

app/build/outputs/apk/release/app-release.apk

SIGNING

Release signing uses a project-specific keystore.

Never upload private keystore files or signing credentials to a public GitHub repository.

---

◈ 12. PRIVACY & SECURITY

MJA is designed for local communication.

PRIVACY PRINCIPLES

- No centralized messaging infrastructure.
- No cloud messaging dependency.
- No analytics or tracking by design.
- Local message storage.
- Direct local-network communication.

IMPORTANT SECURITY NOTES

Local-network communication does not automatically guarantee end-to-end encryption.

TCP provides reliable transport, and ACK confirms delivery. Neither provides cryptographic protection against malicious devices on the same network.

Future security improvements should include:

- End-to-end encryption.
- Cryptographic peer authentication.
- Message integrity verification.
- Secure key management.
- Protection against replay attacks.

---

◈ 13. ROADMAP

Feature| Status
UDP Peer Discovery| Implemented
TCP Message Transport| Implemented
ACK Confirmation| Implemented
Offline Message Queue| Implemented
Network Synchronization| Implemented
Hotspot Management UI| Implemented
Persian RTL Interface| Implemented
End-to-End Encryption| Planned
Cryptographic Authentication| Planned
Secure File Transfer| Planned
Advanced Network Diagnostics| Planned
Automated Integration Tests| Planned

---

◈ 14. RELEASE HISTORY

v1.1.0 — CURRENT

- Completed 38+ application screens.
- Implemented local UDP discovery.
- Implemented TCP transport.
- Added ACK confirmation.
- Added offline message queue.
- Added synchronization engine.
- Added connected devices management.
- Added hotspot management interface.
- Added host administration panel.
- Added Persian RTL design system.
- Integrated Vazirmatn typography.

<a href="https://github.com/M-JAHANARA/MJAOfflineMessenger/releases">
<img src="https://img.shields.io/badge/VIEW-ALL%20RELEASES-37C98B?style=for-the-badge&logo=github" alt="All Releases">
</a>---

◈ 15. DEVELOPER

<div align="center"><img src="https://avatars.githubusercontent.com/M-JAHANARA" width="120" style="border-radius:50%" alt="MJA Developer">M.JAHANARA

Developer · Software Engineering Enthusiast · AI Security

Building technology with a focus on privacy, innovation and local-first systems.

<a href="https://github.com/M-JAHANARA">
<img src="https://img.shields.io/badge/GITHUB-M.JAHANARA-181717?style=for-the-badge&logo=github" alt="GitHub">
</a></div>---

◈ 16. LICENSE

This project is provided for personal and educational use.

Redistribution, modification and commercial use are subject to the project owner's permission.

---

<div align="center"><img src="assets/logo.png" width="90" alt="MJA Logo">MJA OFFLINE MESSENGER

CHAT WITHOUT INTERNET.

PRIVATE. LOCAL. SECURE.

<br><img src="https://img.shields.io/badge/ENGINEERED%20BY-M.JAHANARA-37C98B?style=for-the-badge"><br><br>

Think. Build. Protect.

© M.JAHANARA

</div>
