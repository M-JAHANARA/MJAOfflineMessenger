MJA Offline Messenger

<p align="center">
  <img src="assets/banner.png" alt="MJA Offline Messenger Banner" width="100%">
</p><p align="center">
  <strong>Connect Locally. Communicate Freely.</strong>
  <br>
  A modern offline messaging platform designed for local networks.
</p><p align="center">
  <img src="https://img.shields.io/badge/Project-MJA Messenger-DC2626?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Status-In Development-orange?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Internet-Not Required-16A34A?style=for-the-badge" />
</p><p align="center">
  <img src="https://img.shields.io/github/last-commit/OWNER/REPO?style=flat-square" />
  <img src="https://img.shields.io/github/issues/OWNER/REPO?style=flat-square" />
  <img src="https://img.shields.io/github/stars/OWNER/REPO?style=flat-square" />
  <img src="https://img.shields.io/github/license/OWNER/REPO?style=flat-square" />
</p>---

Overview

MJA Offline Messenger is a local-network-based messaging project designed to enable communication between devices without requiring an internet connection.

The goal is to create a secure, modern, and reliable communication environment where connected users can exchange messages and files through a shared local network, such as Wi-Fi or a mobile hotspot.

No cloud messaging infrastructure is required for the core local communication concept.

Core Concept

       Local Wi-Fi / Hotspot
                |
       +--------+--------+
       |        |        |
     Device A Device B Device C
       |        |        |
       +--------+--------+
                |
       Local Communication

Devices discover and communicate with compatible clients on the same local network.

Planned Features

Feature| Status
Private Messaging| Planned
Group Conversations| Planned
Nearby User Discovery| Planned
File Sharing| Planned
Voice Messages| Planned
Voice Calls| Planned
Video Calls| Planned
Offline Message Queue| Planned
Local Network Monitoring| Planned
Host & Device Management| Planned
Cross-Platform Support| Planned

«Feature availability will depend on implementation progress and platform capabilities.»

Platform Support

Platform| Target
Windows| Desktop Client
Android| Mobile Client
Local Network| Communication Infrastructure

Design Philosophy

MJA Offline Messenger is being designed around five principles:

- Privacy First: Local communication without depending on public messaging servers.
- Offline Native: Communication designed around local network connectivity.
- Modern UI: Minimal, professional, dark interface.
- Cross-Platform: A consistent experience across desktop and mobile.
- Reliability: Clear connection states, message delivery feedback, and network management.

Interface Preview

<p align="center">
  <img src="assets/windows-preview.png" alt="MJA Windows Interface" width="90%">
</p><p align="center">
  <sub>Windows UI Concept — Development Preview</sub>
</p><p align="center">
  <img src="assets/android-preview.png" alt="MJA Android Interface" width="45%">
</p><p align="center">
  <sub>Android UI Concept — Development Preview</sub>
</p>Project Structure

MJA-Offline-Messenger/
│
├── assets/
│   ├── banner.png
│   ├── windows-preview.png
│   └── android-preview.png
│
├── desktop/
│   └── Windows Client
│
├── mobile/
│   └── Android Client
│
├── core/
│   └── Shared Communication Logic
│
├── docs/
│   └── Documentation
│
└── README.md

The directory structure is a proposed organization and may change during development.

Development Roadmap


Project architecture

Local network communication

Device discovery

User identity and profiles

Private messaging

Group messaging

File transfer

Windows client

Android client

Voice communication

Video communication

Security and reliability testing

First stable release
Network & Privacy

MJA Offline Messenger is intended to operate within a local network.

A few important technical considerations:

- Devices must have a valid local communication path.
- Network isolatio

n and firewall rules can affect connectivity.
- Offline message delivery requires an online recipient or a reachable local server capable of storing and forwarding messages.
- Voice and video communication require additional real-time networking components.
- Local networking does not automatically guarantee end-to-end encryption.

Security and privacy mechanisms will be documented as they are implemented.

Contributing

Contributions, ideas, bug reports, and suggestions are welcome.

If you want to contribute:

1. Fork the repository.
2. Create a feature branch.
3. Make your changes.
4. Submit a Pull Request.

Please keep contributions clean, documented, and consistent with the project architecture.

License

The license will be announced with the project's first public release.

---

<p align="center">
  <strong>MJA Offline Messenger</strong>
  <br>
  Think Locally. Connect Freely.
  <br><br>
  Developed under the <strong>M.JAHANARA</strong> brand.
</p>
