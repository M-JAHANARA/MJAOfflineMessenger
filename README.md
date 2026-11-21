MJA Offline Messenger

<p align="center">
  <img src="assets/banner.png" width="100%" alt="MJA Offline Messenger"/>
</p><h3 align="center">Connect Locally. Communicate Freely.</h3><p align="center">
  A modern offline messaging platform built for local network communication.
</p><p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
  <img src="https://img.shields.io/badge/UI-Jetpack Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/>
  <img src="https://img.shields.io/badge/Status-Development-orange?style=for-the-badge"/>
</p><p align="center">
  <a href="https://github.com/M-JAHANARA/MJAOfflineMessenger">
    <img src="https://img.shields.io/github/stars/M-JAHANARA/MJAOfflineMessenger?style=flat-square&color=DC2626&label=Stars"/>
  </a>
  <a href="https://github.com/M-JAHANARA/MJAOfflineMessenger/network/members">
    <img src="https://img.shields.io/github/forks/M-JAHANARA/MJAOfflineMessenger?style=flat-square&color=555555&label=Forks"/>
  </a>
  <a href="https://github.com/M-JAHANARA/MJAOfflineMessenger/issues">
    <img src="https://img.shields.io/github/issues/M-JAHANARA/MJAOfflineMessenger?style=flat-square&color=E4B45B&label=Issues"/>
  </a>
  <a href="https://github.com/M-JAHANARA/MJAOfflineMessenger/commits">
    <img src="https://img.shields.io/github/last-commit/M-JAHANARA/MJAOfflineMessenger?style=flat-square&color=37C98B&label=Last Commit"/>
  </a>
</p>---

Overview

MJA Offline Messenger is an offline-first messaging project designed to connect devices through local networks without requiring an internet connection for local communication.

Using Wi-Fi and mobile hotspots, the project aims to create a private communication environment where nearby devices can discover each other, exchange messages and share files.

The long-term vision is to build a cross-platform communication ecosystem with Android and Windows clients.

Live Repository Statistics

<p align="center">
  <a href="https://github.com/M-JAHANARA/MJAOfflineMessenger">
    <img src="https://github-readme-stats.vercel.app/api/pin/?username=M-JAHANARA&repo=MJAOfflineMessenger&theme=transparent&hide_border=true&show_owner=true" width="100%" alt="Repository Statistics"/>
  </a>
</p><p align="center">
  <img src="https://komarev.com/ghpvc/?username=M-JAHANARA&repo=MJAOfflineMessenger&color=DC2626&style=for-the-badge&label=REPOSITORY+VIEWS" alt="Repository Views"/>
</p>Project Vision

A local communication ecosystem designed around:

- Private and group conversations
- Nearby device discovery
- Local file sharing
- Voice messaging
- Real-time audio and video communication
- Network monitoring and device management
- Reliable message delivery and synchronization

No internet required for communication within a reachable local network.

Feature Tracker

Feature| Status
Android Application| In Development
Modern UI| In Development
Private Messaging| Planned
Group Messaging| Planned
Nearby Device Discovery| Planned
Local Network Communication| Planned
File Sharing| Planned
Voice Messages| Planned
Voice Calls| Planned
Video Calls| Planned
Message Synchronization| Planned
Windows Desktop Client| Design Stage

«This tracker describes project goals. Feature status should be updated as implementation progresses.»

Technology Stack

<p align="center">
  <img src="https://skillicons.dev/icons?i=kotlin,androidstudio,gradle,git,github" alt="Technology Stack"/>
</p>Technology| Role
Kotlin| Main programming language
Jetpack Compose| Modern declarative UI
Material 3| Design system
Android SDK| Application platform
Gradle Kotlin DSL| Build system

Language Distribution

<p align="center">
  <img src="https://github-readme-stats.vercel.app/api/top-langs/?username=M-JAHANARA&layout=donut&theme=transparent&hide_border=true&langs_count=8" alt="GitHub Language Statistics"/>
</p>Architecture

The project is intended to follow a modular architecture with se

parated responsibilities.

MJAOfflineMessenger/
│
├── app/
│   └── Android Application
│
├── gradle/
│   └── wrapper/
│
├── CONTRACT.md
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
│
└── README.md

The architecture is expected to evolve as the communication infrastructure develops.

<details>
<summary><strong>Network Architecture</strong></summary>             Local Wi-Fi / Hotspot
                       |
           +-----------+-----------+
           |           |           |
        Device A    Device B    Device C
           |           |           |
           +-----------+-----------+
                       |
             Local Communication

Communication depends on local network reachability, device availability and the implemented networking services.

</details><details>
<summary><strong>Security & Privacy</strong></summary>The project follows a privacy-focused development philosophy:

- Minimize unnecessary external dependencies.
- Keep local communication within the intended network.
- Design appropriate authentication mechanisms.
- Protect messages and transferred files.
- Provide clear connection and delivery states.

Local connectivity does not automatically guarantee end-to-end encryption. Security mechanisms must be implemented and verified separately.

</details>Development Roadmap


Initial Android project setup

Kotlin foundation

Jetpack Compose foundation

Material 3 integration

Complete messaging interface

Local network communication

Device discovery

Private messaging

Group messaging

File transfer

Message synchronization

Voice messaging

Audio and video calls

Security testing

First stable release

Windows desktop client
Development Activity

<p align="center">
  <img src="https://github-readme-activity-graph.vercel.app/graph?username=M-JAHANARA&bg_color=00000000&color=9CA3AF&line=DC2626&point=FFFFFF&area=true&hide_border=true" width="100%" alt="Development Activity Graph"/>
</p>Contributors

<p align="center">
  <a href="https://github.com/M-JAHANARA/MJAOfflineMessenger/graphs/contributors">
    <img src="https://contrib.rocks/image?repo=M-JAHANARA/MJAOfflineMessenger" alt="Project Contributors"/>
  </a>
</p>Getting Started

Requirements

- Android Studio
- JDK 17
- Android SDK compatible with the project configuration
- Android device or emulator

Clone the Repository

git clone https://github.com/M-JAHANARA/MJAOfflineMessenger.git

Build the Application

Windows:

.\gradlew.bat :app:assembleDebug

Linux / macOS:

./gradlew :app:assembleDebug

Install

adb install -r app/build/outputs/apk/debug/app-debug.apk

Windows Client

A professional Windows desktop client is part of the project's long-term vision.

The desktop experience is currently in the design stage. Its planned capabilities include:

- Multi-panel messaging interface
- Conversation and contact management
- Local network monitoring
- File transfer management
- Voice and video communication

Contributing

Contributions, ideas and feedback are welcome.

1. Fork the repository.
2. Create a feature branch.
3. Implement your changes.
4. Submit a Pull Request.

Please follow the project structure and development guidelines.

License

No public license has been declared yet. All rights reserved by the project owner.

---

<p align="center">
  <img src="https://img.shields.io/badge/CREATED BY-M.JAHANARA-DC2626?style=for-the-badge"/>
</p><h3 align="center">MJA Offline Messenger</h3><p align="center">
  Think Locally. Connect Freely.
  <br/>
  <a href="https://github.com/M-JAHANARA">GitHub Profile</a>
  ·
  <a href="https://github.com/M-JAHANARA/MJAOfflineMessenger">Source Code</a>
  ·
  <a href="https://github.com/M-JAHANARA/MJAOfflineMessenger/issues">Report an Issue</a>
</p>
