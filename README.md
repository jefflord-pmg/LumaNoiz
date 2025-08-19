# 🌙 LumaNoiz

**A simple, free Android app for ambient sounds to aid sleep and relaxation**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-orange.svg)](https://developer.android.com/jetpack/compose)

## 📖 Table of Contents

- [About](#about)
- [Features](#features)
- [Screenshots](#screenshots)
- [Installation](#installation)
- [Usage](#usage)
- [Technical Details](#technical-details)
- [Project Structure](#project-structure)
- [Development](#development)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## 🎯 About

An offshoot from https://github.com/Rowdster/noizs.

LumaNoiz is a minimalist Android application designed to help users achieve better sleep and relaxation through high-quality ambient soundscapes and visual therapy techniques. The app provides a carefully curated selection of noise types including white noise, brown noise, grey noise, and pink noise, all optimized for continuous background playback, plus customizable ball animation and brainwave frequency-based strobe therapy features for focus and visual relaxation.

### Why LumaNoiz?

- **Sleep Enhancement**: Ambient sounds help mask disruptive environmental noise
- **Focus Improvement**: Background noise and visual therapy can enhance concentration during work or study
- **Relaxation Aid**: Gentle soundscapes and calming animations promote stress reduction and mental calm
- **Visual Therapy**: Customizable ball animation for focus training and visual relaxation
- **Brainwave Therapy**: Scientifically-informed strobe frequencies targeting specific mental states
- **Completely Free**: No ads, no in-app purchases, no user tracking
- **Simple Design**: Intuitive interface designed for ease of use, especially at night

### Target Audience

Anyone looking for a simple, free app to help them sleep, focus, or relax, including:
- Light sleepers who need noise masking
- Students and professionals seeking focus enhancement
- Meditation practitioners interested in brainwave frequency therapy
- Parents helping children sleep
- Anyone dealing with tinnitus or anxiety
- Individuals using visual therapy techniques for focus and relaxation
- People with ADHD or attention difficulties who benefit from controlled visual stimulation
- Those interested in exploring therapeutic brainwave frequencies (Delta, Theta, Alpha, Beta)

## ✨ Features

### 🎵 Audio Features
- **High-Quality Sound Library**: Premium ambient sounds including:
  - White Noise
  - Brown Noise  
  - Grey Noise
  - Pink Noise
  - Relaxing Smoothed Brown Noise
  - Soft Brown Noise
- **Continuous Playback**: Seamless looping audio without interruptions
- **Background Audio**: Continues playing when app is minimized or screen is off
- **Professional Audio Engine**: Built with AndroidX Media3 (ExoPlayer) for optimal performance

### 📱 User Interface
- **Dark Theme**: Eye-friendly design optimized for nighttime use
- **Jetpack Compose UI**: Modern, responsive interface
- **Simple Navigation**: Intuitive sound selection grid
- **Clear Controls**: Easy-to-use play/pause functionality
- **Adaptive Layout**: Long-press main screen to toggle UI anchoring to bottom
- **Visual Therapy Mode**: Full-screen ball animation with customizable settings

### 🔧 System Integration
- **Notification Controls**: Media controls in notification panel for quick access
- **Home Screen Widget**: One-tap play/pause directly from your home screen
- **No Login Required**: Start using immediately without accounts or setup
- **Minimal Permissions**: Only requests necessary permissions for audio playback
- **Persistent Preferences**: Settings saved automatically using DataStore

### 🎯 Visual Therapy Features
- **"Show Lights" Animation**: Relaxing ball animation for focus and visual therapy
  - Customizable ball size, speed (100-5000ms), and session duration (1-10 minutes)
  - Adaptive display that adjusts to screen orientation and size
  - Interactive controls with long-press to pause/resume and access settings
  - Session management with timer-based sessions and automatic completion
  - Text-to-Speech feedback with voice announcement of session results
  - Progress tracking with real-time display of remaining time and ball movement count
- **"Strobe Lights" Mode**: Brainwave frequency-based strobe lighting therapy
  - **Delta Waves** (0.5-4 Hz): Deep sleep and healing frequencies
  - **Theta Waves** (4-8 Hz): Meditation and creative states
  - **Alpha Waves** (8-13 Hz): Relaxed awareness and focus (default)
  - **Beta Waves** (13-30 Hz): Active concentration and alertness
  - Dynamic frequency variations within selected brainwave ranges
  - Customizable strobe ball size and real-time frequency display
  - Automatic frequency changes every 2 seconds for optimal therapeutic effect

## 📱 Screenshots

*Main App Interface:*
- Sound selection grid with six ambient sound options
- "Show Lights" button for accessing ball animation visual therapy mode
- "Strobe Lights" button for accessing brainwave frequency strobe therapy

*Widget States:*
- Moon off icon: When no sound is playing
- Moon on icon: When sound is actively playing

*Visual Therapy Modes:*
- **Ball Animation Mode**: Full-screen ball animation with customizable settings and pause menu for ball size, speed, and duration controls
- **Strobe Lights Mode**: Brainwave frequency-based strobe therapy with Delta, Theta, Alpha, and Beta wave options

*Note: Screenshots and app previews can be found in the repository's image files.*

## 🚀 Installation

### Prerequisites

For users wanting to install the app:
- Android device running Android 10 (API level 29) or higher
- Approximately 50MB of storage space

### For Developers

To build and modify the app, you'll need:
- **Android Studio**: Arctic Fox (2020.3.1) or newer
- **Android SDK**: API level 36 (compileSdk)
- **Kotlin**: Version 2.2.0 or compatible
- **Gradle**: Version 8.13 or newer
- **Java**: Version 11 or higher

### Building from Source

1. **Clone the Repository**
   ```bash
   git clone https://github.com/jefflord-pmg/LumaNoiz.git
   cd LumaNoiz
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory and open it

3. **Sync Project**
   - Android Studio will automatically prompt to sync Gradle
   - Wait for sync to complete and dependencies to download

4. **Build the App**
   ```bash
   ./gradlew assembleDebug
   ```

5. **Run on Device/Emulator**
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

## 📖 Usage

### Basic Operation

1. **Launch the App**: Tap the LumaNoiz icon to open the application
2. **Select a Sound**: Browse the grid of available ambient sounds
3. **Start Playback**: Tap your preferred sound to begin playing
4. **Control Playback**: Use the play/pause controls within the app
5. **Background Use**: Minimize the app - audio continues playing
5. **Access Visual Therapy**: Tap "Show Lights" for ball animation mode or "Strobe Lights" for brainwave frequency therapy

### Visual Therapy Mode

1. **Enter Mode**: Tap the "Show Lights" button on main screen
2. **Start Session**: Animation begins automatically with default settings
3. **Pause/Settings**: Long-press anywhere on screen to pause and access settings
4. **Customize Settings**: 
   - Adjust ball size with slider
   - Set minimum and maximum ball speed (100-5000ms)
   - Choose session duration (1-10 minutes)
5. **Resume/Restart**: Use "Resume" to continue or "Restart" for new session
6. **Session Completion**: Audio announcement when timer expires

### Strobe Lights Mode

1. **Enter Mode**: Tap the "Strobe Lights" button on main screen
2. **Select Brainwave Frequency**: Choose from four therapeutic frequency ranges:
   - **Delta** (0.5-4 Hz): For deep relaxation and sleep preparation
   - **Theta** (4-8 Hz): For meditation and creative visualization  
   - **Alpha** (8-13 Hz): For relaxed focus and stress reduction (default)
   - **Beta** (13-30 Hz): For active concentration and mental alertness
3. **Automatic Variations**: Frequency changes dynamically every 2 seconds within selected range
4. **Customize Ball Size**: Long-press screen to access menu and adjust strobe ball size
5. **Monitor Frequency**: Current frequency displayed in top-right corner (Hz)
6. **Exit**: Tap outside settings menu or use back gesture to return to main screen

### Notification Controls

When audio is playing, you'll see a notification with:
- Sound name currently playing
- Play/pause toggle button
- Quick access to return to the app

### Widget Usage

1. **Add Widget**: Long-press on home screen → Widgets → LumaNoiz
2. **Quick Control**: Tap widget to play/pause last selected sound
3. **Visual Indicator**: 
   - Moon off: No sound playing
   - Moon on: Sound currently playing
4. **First Use**: If no previous sound selected, tapping opens the main app

### Tips for Best Experience

- **Volume**: Adjust system media volume to comfortable level
- **Sleep Mode**: Enable Do Not Disturb to prevent other notifications
- **Battery**: App is optimized for minimal battery usage during playback
- **Storage**: All sounds are stored locally - no internet required after install
- **Layout Preference**: Long-press main screen to anchor controls to bottom
- **Visual Therapy**: Use in dark environment for best ball animation visibility
- **Focus Sessions**: Start with shorter durations (1-3 minutes) and gradually increase
- **Strobe Therapy**: Begin with Alpha waves (8-13 Hz) for general relaxation, experiment with other frequencies based on desired mental state
- **Brainwave Guidelines**: Delta for sleep preparation, Theta for meditation, Alpha for focus, Beta for concentration

## 🔧 Technical Details

### Architecture

- **Platform**: Android Native
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Audio Engine**: AndroidX Media3 (ExoPlayer and MediaSession)
- **Minimum SDK**: Android 10 (API 29)
- **Target SDK**: Android 14 (API 36)
- **App ID**: `com.lusion.LumaNoiz`

### Key Components

- **MainActivity.kt**: Primary app interface using Compose UI
- **LightsActivity.kt**: Ball animation visual therapy mode implementation
- **StrobeLightsActivity.kt**: Brainwave frequency-based strobe therapy mode
- **SoundService.kt**: Background service managing audio playback
- **LumaNoizAppWidget.kt**: Home screen widget implementation
- **UserPreferencesRepository.kt**: Preference management using DataStore
- **Media3 Integration**: Professional-grade audio handling

### Audio Implementation

The app uses AndroidX Media3 for robust audio playback:
- **ExoPlayer**: High-performance media playback engine
- **MediaSession**: System integration for notification controls
- **Foreground Service**: Ensures uninterrupted background playback
- **Wake Lock**: Maintains playback when screen is off

### Visual Therapy Implementation

The animation system provides therapeutic visual experiences:
- **Jetpack Compose Animations**: Smooth, hardware-accelerated ball movement
- **Coroutine-based Timing**: Precise control over animation duration and speed
- **Text-to-Speech**: Android TTS API for session feedback
- **DataStore Preferences**: Persistent storage of user customizations
- **Orientation Adaptation**: Dynamic scaling based on screen dimensions

### Strobe Therapy Implementation

The brainwave frequency system provides scientifically-informed therapeutic strobing:
- **Frequency Ranges**: Four therapeutic brainwave frequency bands (Delta, Theta, Alpha, Beta)
- **Dynamic Frequency Control**: Automatic variations within selected ranges every 2 seconds
- **Real-time Updates**: Live frequency display and smooth transitions
- **Customizable Strobe Ball**: Adjustable size and positioning for optimal visual impact
- **DataStore Persistence**: Saves user frequency and ball size preferences
- **Therapeutic Targeting**: Specific frequencies designed for different mental states

### Widget Architecture

- **Service Communication**: Intent-based messaging between widget and service
- **State Synchronization**: Real-time UI updates reflecting playback status
- **Broadcast Receiver**: Efficient widget update mechanism

## 📁 Project Structure

```
LumaNoiz/
├── app/                          # Main application module
│   ├── src/main/
│   │   ├── java/com/lusion/LumaNoiz/    # Kotlin source files
│   │   │   ├── MainActivity.kt       # Main sound selection interface
│   │   │   ├── LightsActivity.kt     # Ball animation visual therapy
│   │   │   ├── StrobeLightsActivity.kt # Brainwave frequency strobe therapy
│   │   │   ├── SoundService.kt       # Background audio service
│   │   │   ├── LumaNoizAppWidget.kt     # Home screen widget
│   │   │   └── UserPreferencesRepository.kt # Settings management
│   │   └── res/                      # Android resources
│   └── build.gradle.kts             # App-level build configuration
├── assets/                       # Project assets
│   ├── AppIcons/                 # Application icons and UI assets
│   └── sound/                    # Audio files (MP3 format)
├── gradle/                       # Gradle wrapper and dependencies
├── build.gradle.kts             # Project-level build configuration
├── settings.gradle.kts          # Gradle settings
├── README.md                    # This file
├── LICENSE                      # MIT License
└── VIBE_CODE_INSTRUCTIONS.md    # Detailed development documentation
```

### Important Files

- **VIBE_CODE_INSTRUCTIONS.md**: Comprehensive development guidelines and technical specifications
- **UserPreferencesRepository.kt**: User settings and preferences management
- **LightsActivity.kt**: Ball animation visual therapy mode implementation
- **StrobeLightsActivity.kt**: Brainwave frequency strobe therapy mode implementation
- **assets/sound/**: Six high-quality ambient sound files
- **LICENSE**: MIT License for open-source usage
- **gradle.properties**: Build configuration properties

## 🛠 Development

### Building and Testing

```bash
# Build debug version
./gradlew assembleDebug

# Build release version  
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

### Code Style

- **Kotlin**: Follow official Kotlin coding conventions
- **Compose**: Use declarative UI patterns
- **Architecture**: MVVM pattern with repository pattern for data layer

### Known Issues

- **Build Dependencies**: Project uses alpha versions of some Android Gradle Plugin dependencies
- **Raw Audio Naming**: Audio files must use lowercase letters, numbers, and underscores only (no hyphens)

### Adding New Sounds

1. Convert audio to MP3 format
2. Ensure filename uses only lowercase letters, numbers, and underscores
3. Place in `app/src/main/res/raw/` directory
4. Update UI to reference the new sound resource

## 🤝 Contributing

We welcome contributions to improve LumaNoiz! Here's how you can help:

### Ways to Contribute

- **Bug Reports**: File issues for any bugs you discover
- **Feature Requests**: Suggest new features or improvements  
- **Code Contributions**: Submit pull requests for fixes or enhancements
- **Documentation**: Help improve documentation and guides
- **Sound Assets**: Contribute new high-quality ambient sounds

### Development Guidelines

1. **Fork the Repository**: Create your own fork of the project
2. **Create Feature Branch**: `git checkout -b feature/your-feature-name`
3. **Follow Code Style**: Maintain consistent Kotlin and Compose patterns
4. **Test Thoroughly**: Ensure changes don't break existing functionality
5. **Update Documentation**: Include relevant documentation updates
6. **Submit Pull Request**: Provide clear description of changes

### Code of Conduct

- Be respectful and inclusive in all interactions
- Focus on constructive feedback and collaboration
- Help maintain a welcoming environment for all contributors

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025 RogueCoder

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 📞 Contact

- **Repository**: [https://github.com/jefflord-pmg/LumaNoiz](https://github.com/jefflord-pmg/LumaNoiz)
- **Issues**: [Report bugs or request features](https://github.com/jefflord-pmg/LumaNoiz/issues)
- **Author**: RogueCoder

### Support

For support with the app:
1. Check existing [GitHub Issues](https://github.com/jefflord-pmg/LumaNoiz/issues)
2. Search through [documentation](VIBE_CODE_INSTRUCTIONS.md)
3. Create a new issue with detailed description

---

**Made with ❤️ for better sleep and relaxation**

*Download, use, modify, and share freely under the MIT License.*
