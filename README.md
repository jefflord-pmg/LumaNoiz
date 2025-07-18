# 🌙 Noizs

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

Noizs is a minimalist Android application designed to help users achieve better sleep and relaxation through high-quality ambient soundscapes. The app provides a carefully curated selection of noise types including white noise, brown noise, grey noise, and pink noise, all optimized for continuous background playback.

### Why Noizs?

- **Sleep Enhancement**: Ambient sounds help mask disruptive environmental noise
- **Focus Improvement**: Background noise can enhance concentration during work or study
- **Relaxation Aid**: Gentle soundscapes promote stress reduction and mental calm
- **Completely Free**: No ads, no in-app purchases, no user tracking
- **Simple Design**: Intuitive interface designed for ease of use, especially at night

### Target Audience

Anyone looking for a simple, free app to help them sleep, focus, or relax, including:
- Light sleepers who need noise masking
- Students and professionals seeking focus enhancement
- Meditation practitioners
- Parents helping children sleep
- Anyone dealing with tinnitus or anxiety

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

### 🔧 System Integration
- **Notification Controls**: Media controls in notification panel for quick access
- **Home Screen Widget**: One-tap play/pause directly from your home screen
- **No Login Required**: Start using immediately without accounts or setup
- **Minimal Permissions**: Only requests necessary permissions for audio playback

## 📱 Screenshots

*Widget States:*
- Moon off icon: When no sound is playing
- Moon on icon: When sound is actively playing

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
   git clone https://github.com/jefflord-pmg/noizs.git
   cd noizs
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

1. **Launch the App**: Tap the Noizs icon to open the application
2. **Select a Sound**: Browse the grid of available ambient sounds
3. **Start Playback**: Tap your preferred sound to begin playing
4. **Control Playback**: Use the play/pause controls within the app
5. **Background Use**: Minimize the app - audio continues playing

### Notification Controls

When audio is playing, you'll see a notification with:
- Sound name currently playing
- Play/pause toggle button
- Quick access to return to the app

### Widget Usage

1. **Add Widget**: Long-press on home screen → Widgets → Noizs
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

## 🔧 Technical Details

### Architecture

- **Platform**: Android Native
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Audio Engine**: AndroidX Media3 (ExoPlayer and MediaSession)
- **Minimum SDK**: Android 10 (API 29)
- **Target SDK**: Android 14 (API 36)
- **App ID**: `com.lusion.noizs`

### Key Components

- **MainActivity.kt**: Primary app interface using Compose UI
- **SoundService.kt**: Background service managing audio playback
- **NoizsAppWidget.kt**: Home screen widget implementation
- **Media3 Integration**: Professional-grade audio handling

### Audio Implementation

The app uses AndroidX Media3 for robust audio playback:
- **ExoPlayer**: High-performance media playback engine
- **MediaSession**: System integration for notification controls
- **Foreground Service**: Ensures uninterrupted background playback
- **Wake Lock**: Maintains playback when screen is off

### Widget Architecture

- **Service Communication**: Intent-based messaging between widget and service
- **State Synchronization**: Real-time UI updates reflecting playback status
- **Broadcast Receiver**: Efficient widget update mechanism

## 📁 Project Structure

```
noizs/
├── app/                          # Main application module
│   ├── src/main/
│   │   ├── java/com/lusion/noizs/    # Kotlin source files
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

We welcome contributions to improve Noizs! Here's how you can help:

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

- **Repository**: [https://github.com/jefflord-pmg/noizs](https://github.com/jefflord-pmg/noizs)
- **Issues**: [Report bugs or request features](https://github.com/jefflord-pmg/noizs/issues)
- **Author**: RogueCoder

### Support

For support with the app:
1. Check existing [GitHub Issues](https://github.com/jefflord-pmg/noizs/issues)
2. Search through [documentation](VIBE_CODE_INSTRUCTIONS.md)
3. Create a new issue with detailed description

---

**Made with ❤️ for better sleep and relaxation**

*Download, use, modify, and share freely under the MIT License.*
