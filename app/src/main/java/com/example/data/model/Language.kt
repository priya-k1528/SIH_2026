package com.example.data.model

enum class Language(
    val id: String,
    val displayName: String,
    val nativeName: String,
    val scriptName: String,
    val speechCode: String,
    val isDeviceSttDefaultSupported: Boolean,
    val isDeviceTtsDefaultSupported: Boolean,
    val voiceAvailabilityNote: String
) {
    ENGLISH(
        id = "en",
        displayName = "English",
        nativeName = "English",
        scriptName = "Latin",
        speechCode = "en-IN",
        isDeviceSttDefaultSupported = true,
        isDeviceTtsDefaultSupported = true,
        voiceAvailabilityNote = "Supported natively on device"
    ),
    HINDI(
        id = "hi",
        displayName = "Hindi",
        nativeName = "हिंदी",
        scriptName = "Devanagari",
        speechCode = "hi-IN",
        isDeviceSttDefaultSupported = true,
        isDeviceTtsDefaultSupported = true,
        voiceAvailabilityNote = "Supported natively on device"
    ),
    SANTHALI(
        id = "sat",
        displayName = "Santhali",
        nativeName = "ᱥᱟᱱᱛᱟᱲᱤ",
        scriptName = "Ol Chiki & Roman",
        speechCode = "sat-IN",
        isDeviceSttDefaultSupported = false,
        isDeviceTtsDefaultSupported = false,
        voiceAvailabilityNote = "Speech recognition & TTS not natively installed on standard Android. Supported via phonetic guides and custom vernacular engine."
    ),
    MUNDARI(
        id = "mun",
        displayName = "Mundari",
        nativeName = "मुंडारी",
        scriptName = "Devanagari & Roman",
        speechCode = "unr-IN",
        isDeviceSttDefaultSupported = false,
        isDeviceTtsDefaultSupported = false,
        voiceAvailabilityNote = "Speech recognition & TTS not natively installed on standard Android. Supported via phonetic guides and custom vernacular engine."
    );

    companion object {
        fun fromId(id: String): Language {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: HINDI
        }

        fun fromSpeechCode(code: String): Language {
            return entries.find { code.startsWith(it.id, ignoreCase = true) } ?: HINDI
        }
    }
}
