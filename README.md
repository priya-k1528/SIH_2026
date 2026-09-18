# 🎓 Vernacular Pedagogy (SIH 2026)
### AI-Powered Vernacular Pedagogy and Real-Time Speech Translation Tool for Mother Tongue-Based Primary Education

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://www.android.com/)
[![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![UI Toolkit](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![AI Engine](https://img.shields.io/badge/AI-Google%20Gemini-8E75FF.svg?style=flat&logo=googlegemini)](https://ai.google.dev/)
[![Hackathon](https://img.shields.io/badge/SIH-2026-FF9933.svg?style=flat)](#smart-india-hackathon-2026)
[![Problem ID](https://img.shields.io/badge/Problem%20ID-SIH26042-138808.svg?style=flat)](#problem-statement)

---

## 📌 Problem Statement & Vision (SIH26042)

In tribal and remote regions (such as **Jharkhand**), primary school children predominantly speak native indigenous mother tongues like **Santhali**, **Mundari**, and other tribal dialects, while school instruction and textbooks are delivered primarily in **Hindi** or **English**. This severe linguistic disconnect leads to early learning hurdles, lower retention, high classroom anxiety, and elevated dropout rates.

**Vernacular Pedagogy** bridges this gap. Designed for **Smart India Hackathon (SIH 2026)**, this modern Android application empowers teachers and young tribal learners with **real-time bidirectional speech translation**, **interactive bilingual flashcards**, **Gemini-powered dynamic worksheet and curriculum generation**, and an **offline-ready vernacular dictionary** supporting indigenous scripts such as **Ol Chiki** (for Santhali) alongside Devanagari and Roman transliterations.

---

## ✨ Key Features

### 🎙️ 1. Real-Time Speech & Voice Translation
- **Two-Way Audio Translation:** Instant speech-to-text and text-to-speech translation between Hindi/English and tribal languages.
- **Classroom Dialogue Mode:** Allows non-native teachers to communicate smoothly with vernacular-speaking students during classroom instructions.
- **Pronunciation Guides:** Audio playback assist students in learning correct pronunciation and phonetic nuances.

### 🗂️ 2. Interactive Bilingual Flashcards
- **Multi-Script Cards:** Displays words with corresponding illustrations, English, Hindi, and tribal scripts (e.g., Santhali in Ol Chiki ᱚᱞ ᱪᱤᱠᱤ and Mundari in Devanagari/Roman).
- **Gamified Learning:** Smooth 3D card flip animations, category filtering (Animals, Colors, Classroom, Nature, Numbers), and interactive audio buttons.

### 🤖 3. AI-Powered Vernacular Content & Worksheets
- **Generative Pedagogy via Gemini AI:** Generates context-aware vernacular lesson plans, illustrated storybooks, and printable bilingual practice worksheets.
- **Localized Cultural Narratives:** Creates educational stories rooted in regional folklore, traditions, and everyday rural experiences.

### 🧠 4. Vernacular Quizzes & AI Assessment
- **Interactive Quizzes:** Picture identification, audio listening comprehension, and vernacular vocabulary challenges.
- **Instant AI Feedback:** Automated grading and constructive encouragement in the student's mother tongue.

### 📖 5. Offline Vernacular Dictionary
- **Zero-Connectivity Ready:** High-priority classroom instructions, daily vocabulary, numbers, and basic conversation phrases preloaded locally.
- **Dual Transliteration:** Provides Ol Chiki / Devanagari alongside phonetic Roman text for seamless reading by both teachers and students.

### 👥 6. Role-Based Dual Experience
- **Teacher Mode:** Class management, worksheet generation, teaching aids, and custom flashcard creation.
- **Student Mode:** Distraction-free, vibrant, gamified interface tailored for early childhood education (ECE) and foundational literacy.

---

## 🛠️ Technology Stack & Architecture

- **Platform:** Native Android (Min SDK: 24 | Target SDK: 36)
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material Design 3
- **Architecture:** Clean Architecture + MVVM (Model-View-ViewModel) with StateFlow & Coroutines
- **AI & LLM Integration:** Google Gemini API (`generativeai` Android SDK)
- **Audio & Speech Engine:** Android `SpeechRecognizer` (STT) + `TextToSpeech` (TTS)
- **Data & Storage:** Kotlin Data Classes, Room / DataStore, Offline Dictionary Cache
- **Build System:** Gradle (Kotlin DSL - `build.gradle.kts`) with Version Catalog (`libs.versions.toml`)

---

## 📂 Repository Structure

```
SIH_2026/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── assets/
│   │   ├── java/com/example/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── dictionary/         # Preloaded Vernacular Dictionary (Santhali, Mundari, etc.)
│   │   │   │   ├── local/              # Local preferences & persistence
│   │   │   │   ├── model/              # Data models (Flashcards, Quizzes, Lessons)
│   │   │   │   └── repository/         # Repository pattern abstraction
│   │   │   ├── services/
│   │   │   │   ├── AiContentService.kt # Gemini AI generation service
│   │   │   │   ├── QuizGradingService.kt
│   │   │   │   ├── SpeechRecognitionService.kt
│   │   │   │   ├── TextToSpeechService.kt
│   │   │   │   └── TranslationService.kt
│   │   │   └── ui/
│   │   │       ├── components/         # Reusable Jetpack Compose widgets
│   │   │       ├── screens/            # Home, Flashcards, VoiceTranslation, Quiz, Worksheets, etc.
│   │   │       ├── theme/              # Typography, Colors, Theme definitions
│   │   │       └── viewmodel/          # ViewModel state holders
│   │   └── res/                        # Drawables, strings, mipmaps, fonts
│   └── build.gradle.kts
├── gradle/                             # Gradle wrapper & version catalogs
├── .env.example                        # Template for API Keys
├── .gitignore                          # Git ignore rules
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🚀 Getting Started & Local Setup

### Prerequisites
1. **Android Studio** (Ladybug / Iguana or later recommended).
2. **JDK 17 or JDK 21**.
3. **Android Device or Emulator** running API 24 (Android 7.0) or higher.
4. **Google Gemini API Key** (Obtain from [Google AI Studio](https://aistudio.google.com/)).

### Installation Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/priya-k1528/SIH_2026.git
   cd SIH_2026
   ```

2. **Configure Gemini API Key:**
   - Copy `.env.example` to `.env` in the root folder:
     ```bash
     cp .env.example .env
     ```
   - Open `.env` and configure your API key:
     ```env
     GEMINI_API_KEY=your_actual_gemini_api_key_here
     ```

3. **Open Project in Android Studio:**
   - Launch Android Studio.
   - Select **Open** and choose the `SIH_2026` / `vernacular` project root directory.
   - Let Gradle sync and download required dependencies.

4. **Build and Run:**
   - Select your target device or emulator.
   - Click the **Run ▶** button or execute via Gradle:
     ```bash
     ./gradlew assembleDebug
     ```

---

## 🗣️ Supported Vernacular Languages & Dialects

| Language | Script / Writing System | Region | Status |
| :--- | :--- | :--- | :--- |
| **Santhali (ᱥᱟᱱᱛᱟᱲᱤ)** | Ol Chiki (ᱚᱞ ᱪᱤᱠᱤ) & Roman | Jharkhand, West Bengal, Odisha | ✅ Supported |
| **Mundari (मुंडारी)** | Devanagari (देवनागरी) & Roman | Jharkhand, Chota Nagpur Plateau | ✅ Supported |
| **Hindi (हिन्दी)** | Devanagari | Inter-state & national instruction | ✅ Supported |
| **English** | Latin / Roman | Curricular bridge language | ✅ Supported |

---

## 🏆 Smart India Hackathon 2026 Details

- **Hackathon:** Smart India Hackathon (SIH 2026)
- **Problem Statement ID:** SIH26042
- **Domain:** Smart Education / Tribal Welfare / Vernacular Pedagogy
- **Organization / Ministry:** State Government of Jharkhand / Ministry of Tribal Affairs

---

## 📄 License

This project is developed for the Smart India Hackathon 2026. Distributed under the MIT License. See `LICENSE` for more information.

---

<div align="center">
  <sub>Built with ❤️ for inclusive, mother-tongue-based education in tribal India.</sub>
</div>
#   T r i b a l L e a r n  
 