# Quickly-Use-Jetpack-Compose

This is a personal Jetpack Compose practical project template based on **commercial-grade development standards**. I will continuously maintain and sync the latest practical insights. The project deeply integrates modern engineering standards such as **multi-module plugin-based management**, **custom design systems**, **offline-ready data repositories**, and **startup performance optimization**. It also covers practical solutions like **deep system capability encapsulation**, **third-party SDK integration examples**, and **interaction experience optimization**. It aims to provide developers with a standardized, reusable engineering base, skipping infrastructure setup and directly entering high-quality business development. [![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/Cheng-Kun-Liu/Quickly-Use-Jetpack-Compose)

[![GitHub License](https://img.shields.io/badge/license-Apache--2.0-blue.svg?style=flat)](https://github.com/Cheng-Kun-Liu/Quickly-Use-Jetpack-Compose/blob/main/LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![AGP](https://img.shields.io/badge/AGP-9.x-blue.svg?style=flat&logo=android)](https://developer.android.com/studio/releases/gradle-plugin)
[![Compose](https://img.shields.io/badge/Compose-Latest-orange.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)

[中文版 (Chinese Version)](README_CN.md)

# 📬 Contact Me

If you need help developing an app, feel free to email me anytime. I'm from China and offer great cost-effectiveness.

- **Email**: [zzjcode@gmail.com](mailto:zzjcode@gmail.com)

# 📜 Protocol

[View Privacy Policy](PRIVACY.md)

# 🏗️ Architecture

The architecture of Quickly-Use-Jetpack-Compose refers to the Android official best practice project [Now in Android App](https://github.com/android/nowinandroid).

## Architectural Components

+ **Modularization**: Code organized by app, core, feature, flavor, res, etc., to reduce coupling.
+ **Dependency Injection**: Uses Hilt to manage global and local dependencies.
+ **Data Layer**: Adopts the Repository pattern, integrating **Room** database and **Ktor (OkHttp)** for network requests.
+ **UI Driven**: Single Activity architecture, using Navigation to manage page jumps, combined with Compose + ViewModel + Flow for reactive UI.

## Experience Optimization

+ **Task Stack Scheduling**: Built-in `SchemeActivity` intermediate layer to solve task stack disorder when jumping back from external sources (e.g., payment callbacks, OAuth login), ensuring the app seamlessly restores to the state before leaving.
+ **Recent Task Cleanup**: Encapsulated `finishAndCleanupTask` extension function for independent task stacks (e.g., WebView), providing an automatic destruction mechanism to ensure the Activity is cleared from the "Recent Tasks List" after exit.
+ **Video Play-while-Saving**: LRU caching mechanism based on Media3 for real-time local synchronization and persistence of video data during playback, achieving instant second-time opening and significant traffic savings.
+ **Image Disk Cache**: Deep configuration of Coil disk caching strategy to ensure network images remain visible offline and significantly improve image reloading speed and scrolling smoothness.

# 🎨 Design System

The project includes a custom Compose design system, WeChat-style, not directly using Material 3 visual styles.

+ **WeTheme**: Replaces MaterialTheme, adapted for 375dp design width in portrait mode.
+ **WeColorScheme**: Defines the color system, supporting system, dynamic, light, dark, and blue themes.
+ **WeTypography**: Defines the font size system.
+ **WeIndication**: Defines touch, hover, and focus feedback.
+ **WeDimen**: Defines dimension standards.
+ **WeIcons**: Uses ImageVector to draw icons.
+ **WeWidget**: Common components like TopBar, BottomBar, Button, Toast, ActionSheet, Radio, Checkbox, Switch, etc.
+ **View**: Common Compose components like Clickable富文本 (ClickableAnnotatedText), Drag-and-Sort (DragList), Error Page (ErrorView), Loading, Screenshot prevention (SecureComposeView), etc.

# 📦 Module Directory Overview

+ **app**: Application entry, aggregates features and handles Navigation.
+ **build-logic**: Custom Gradle Convention plugin center, responsible for build logic reuse and modular management.
+ **core-logic**:
    - `repository` / `network` / `database`: Core data links, providing offline-first data repositories and network interceptors.
    - `authenticate` / `notification` / `location` / `language`: Core domain capability encapsulation, including biometrics, FCM notifications, location, and multi-language switching.
+ **core-ui** & **core-launcher**:
    - `core-ui`: Core implementation of WeDesign design system.
    - `core-launcher`: System interaction encapsulation, providing one-line calls for camera, gallery, and permission requests.
+ **feature**:
    - `main`: Main app frame, including Home Pager container and "Functions" page logic.
    - `chat` / `video` / `webview`: Vertical business modules, deeply integrating AI chat, Media3 video architecture, and universal WebView. These modules are referenced by the `explore` module as third-party integration examples.
    - `explore` / `system`: Third-party SDK integration examples (HTTP, Firebase, Google Login) and system native capability encapsulation (location, biometrics).
    - `ui-demo`: UI interaction examples, including custom calendar, drawing board, nested scrolling, etc.
    - `settings`: App preference settings (multi-language, theme, font).
+ **flavor**: Provides examples of channel-differentiated implementations for `gp` (Google Play) and `sam` (Samsung).
+ **res**: Unified management of resources like strings, images, and multi-language files.
+ **baseline-profile**: Configures startup performance optimization.

# 🚀 Development and Running

It is recommended to use the latest version of Android Studio. Switch to the `app` configuration before starting.

## Keys and Signing

Key files are stored in the `keystore` directory at the root. Signing configurations are in `AndroidApplicationConventionPlugin.kt`.

> [!IMPORTANT]
> **Firebase Configuration**: The `app/google-services.json` file in the project is for structure reference only and **cannot be used directly**. Before running the project, please replace it with the `google-services.json` file generated in your own Firebase console for your project; otherwise, Firebase-related functions (e.g., AI chat, crash analytics, etc.) will not work properly.

## Build Variants

The project pre-sets two productFlavors, `gp` and `sam`, corresponding to different ApplicationIds and signing configurations, which can be switched in the Build Variants panel in Android Studio.

# 📱 Running Effects

### Core Business Capabilities

| Example | Screenshot/GIF | Highlights |
| --- | --- | --- |
| **AI Chat (Gemini)** | <img src="docs/images/AiChat.gif" width="320"/> | Integrates Google AI Gemini, showcasing streaming responses and notification integration |
| **Media3 Video Playback** | <img src="docs/images/Media3.jpg" width="320"/> | Encapsulates Media3, supports play-while-saving and offline caching, handles Lifecycle and full-screen switching |

---

### System Capability Integration

| Example | Screenshot/GIF | Highlights |
| --- | --- | --- |
| **Dynamic Icon Switch** | <img src="docs/images/SwitchAppLogo.gif" width="320"/> | Change App desktop icon dynamically without re-releasing |
| **Network Exception Handling** | <img src="docs/images/HttpScreen.gif" width="320"/> | Unified error code interception, Loading, and Retry mechanism |
| **System Capabilities** | <img src="docs/images/LocationPhotoContacts.gif" width="320"/> | Encapsulated permission requests, location, gallery, and contact selection |
---

### Internationalization and UI Interaction

| Example | Screenshot/GIF |
| --- | --- |
| **Multi-language and Themes** | <img src="docs/images/LanguageSwitch.gif" width="320"/> |
| **Custom Calendar** | <img src="docs/images/CustomizeCalendar.gif" width="320"/> |
| **Drawing Board** | <img src="docs/images/PainterScreen.png" width="320"/> |
| **Lazy List Sorting** | <img src="docs/images/LazySort.gif" width="320"/> |
