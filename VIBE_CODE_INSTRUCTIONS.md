# Vibe Coding Instructions: Noizs App

## 1. Core Concept

A mobile application for Android that provides users with a selection of ambient sounds (white noise, brown noise, grey noise) to aid with sleep and relaxation.

## 2. Key Features

*   **Sound Library:** A simple, intuitive interface to browse and select different soundscapes.
*   **Audio Playback:** High-quality, looping audio playback.
*   **Background Audio:** The app must continue playing audio when it is in the background or when the device's screen is off. This is a critical feature.
*   **No Login/Authentication:** The app will be free and require no user accounts or login.
*   **simple media controls in notification to play/pause:** When the user has selected a sound file to play, provide a simple notification with play/pause control and some basic information like the name of the sound file playing
*   **Widget support to allow quick play/pause of last selected sound file:** If no last sound selected, clicking the widget opens the app as normal. The widget should display only the "moon off" image from assets\AppIcons when the app is not playing sound, and should show the "moon on" image when sound is playing. 

## 3. Target Audience

Anyone looking for a simple, free app to help them sleep, focus, or relax.

## 4. Technology Stack

*   **Framework:** Android native with Kotlin and Compose UI

## 5. Implementation Notes
* For Android, the app is called "Noizs" and the app ID will be "com.lusion.noizs"
* All of the android assets for building the application like the icons are in the AppIcons folder in assets/AppIcons
* The sound files for playback are in assets/sound
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

## 8. Technical Implementation Details

### App Widget and Service Communication

**Problem:** The application was crashing when the `NoizsAppWidget` (an `AppWidgetProvider`, which is a `BroadcastReceiver`) attempted to interact with the `SoundService`.

**Root Cause:** The crash was caused by a `ReceiverCallNotAllowedException`. `BroadcastReceiver` components are not permitted to bind to services directly, which is what the `MediaController.Builder` was attempting to do.

**Solution:** The communication between the app widget and the `SoundService` was re-architected to use intents instead of a direct binding.

*   **`NoizsAppWidget.kt`:**
    *   The widget now sends an intent with the action `SoundService.ACTION_TOGGLE_PLAYBACK` to the `SoundService` when the widget button is clicked.
    *   It no longer directly binds to the service or uses a `MediaController`.
    *   A static `updateWidget` method was added to allow the `SoundService` to push UI updates (the play/pause icon) to the widget.

*   **`SoundService.kt`:**
    *   The service now handles incoming intents with specific actions (`ACTION_TOGGLE_PLAYBACK`, `ACTION_PLAY_SOUND`).
    *   It manages the `ExoPlayer` instance and its state.
    *   Crucially, it listens for `onIsPlayingChanged` events from the player and, in response, calls the static `NoizsAppWidget.updateWidget` method to ensure the widget's UI is always synchronized with the actual playback state.
    *   It also now runs as a foreground service with a notification, which is a requirement for background media playback.

*   **`MainActivity.kt`:**
    *   The activity's interaction with the `SoundService` remains unchanged. It continues to use a `MediaController` to manage playback, which is the correct and recommended approach for an `Activity`. This allows for a more responsive UI within the app itself.

This decoupled approach ensures that the app widget and the service communicate in a way that respects Android's component lifecycle rules, preventing the `ReceiverCallNotAllowedException` and creating a more robust and stable application.

### Widget Preview Image

*   **Preview Image Configuration:** The image displayed in the widget picker (preview image) is configured in the `appwidget-provider` XML file (e.g., `app/src/main/res/xml/noizs_app_widget_info.xml`) using the `android:previewImage` attribute. Ensure this attribute points to a valid drawable resource (e.g., `@drawable/moon_off`) for the preview to display correctly.
