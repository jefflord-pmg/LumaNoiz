# Vibe Coding Instructions: Noizs App

## 1. Core Concept

A mobile application for Android that provides users with a selection of ambient sounds (white noise, brown noise, grey noise) to aid with sleep and relaxation.

## 2. Key Features

*   **Sound Library:** A simple, intuitive interface to browse and select different soundscapes.
*   **Audio Playback:** High-quality, looping audio playback.
*   **Background Audio:** The app must continue playing audio when it is in the background or when the device's screen is off. This is a critical feature.
*   **No Login/Authentication:** The app will be free and require no user accounts or login.
*   **simple media controls in notification to play/pause:** When the user has selected a sound file to play, provide a simple notification with play/pause control and some basic information like the name of the sound file playing
*   **Widget support to allow quick play/pause of last selected sound file:** If no last sound selected, clicking the widget opens the app as normal

## 3. Target Audience

Anyone looking for a simple, free app to help them sleep, focus, or relax.

## 4. Technology Stack

*   **Framework:** Android native with Kotlin and Compose UI

## 5. Implementation Notes
* For Android, the app is called "Noizs" and the app ID will be "com.lusion.noizs"
* The UI should rely on Compose for rendering and avoid legacy XML layout
* **Audio Playback Implementation:** Audio playback and background audio are intended to be implemented using AndroidX Media3 (ExoPlayer and MediaSession). Most of the code for this migration has been implemented in `SoundService.kt`, `MainActivity.kt`, and `NoizsAppWidget.kt`.
* **Persistent Compilation Issue:** A persistent `No value passed for parameter 'p1'` compilation error in `SoundService.kt` (specifically within the `onCustomCommand` method of `MediaSession.Callback`) is preventing a successful build. This error is unusual as the method signature appears correct according to Media3 documentation. It may indicate a deeper environmental issue (e.g., Gradle cache, Kotlin plugin version incompatibility, or Android Studio caches).
* **Raw Audio File Naming:** When adding raw audio files (e.g., MP3s) to `app/src/main/res/raw/`, ensure their filenames contain only lowercase letters (a-z), numbers (0-9), and underscores (`_`). Hyphens (`-`) are not allowed and will cause compilation errors. For example, `white_noise.mp3` is valid, but `white-noise.mp3` is not.
* **Avoid Legacy Media APIs:** Do not use `android.media.MediaPlayer` or `android.support.v4.media` APIs for media playback or session management, as they are deprecated or less robust compared to AndroidX Media3.

## 6. User Interface (UI) & User Experience (UX)

*   **Simplicity:** The UI should be minimal and easy to navigate.
*   **Dark Theme:** A dark theme is recommended to be easy on the eyes, especially for nighttime use.
*   **Sound Selection:** A clear grid or list of available sounds with simple icons representing each choice
*   **Playback Controls:** Simple and clear controls (play/pause).

## 7. Monetization

The app will be completely free. No ads, no in-app purchases.
