package com.example.services

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import com.example.data.model.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TextToSpeechService(
    private val context: Context
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val pendingRequests = mutableListOf<PendingSpeech>()

    private data class PendingSpeech(
        val text: String,
        val language: Language,
        val callback: (String) -> Unit
    )

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> =
        _isSpeaking.asStateFlow()

    private val _ttsMessage = MutableStateFlow<String?>(null)
    val ttsMessage: StateFlow<String?> =
        _ttsMessage.asStateFlow()

    init {
        initializeTts()
    }

    // =========================================================
    // INITIALIZE TTS
    // =========================================================

    private fun initializeTts() {

        try {

            tts = TextToSpeech(
                context.applicationContext,
                this
            )

        } catch (e: Exception) {

            isInitialized = false

            _ttsMessage.value =
                "Could not initialize text-to-speech."

            android.util.Log.e(
                "TTS_TEST",
                "TTS initialization error",
                e
            )
        }
    }

    // =========================================================
    // TTS INIT CALLBACK
    // =========================================================

    override fun onInit(status: Int) {

        if (status != TextToSpeech.SUCCESS) {

            isInitialized = false

            _ttsMessage.value =
                "Text-to-speech engine failed to initialize."

            android.util.Log.e(
                "TTS_TEST",
                "TTS initialization failed. status=$status"
            )

            return
        }

        isInitialized = true

        android.util.Log.d(
            "TTS_TEST",
            "TTS initialized successfully"
        )

        tts?.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {

                override fun onStart(
                    utteranceId: String?
                ) {

                    _isSpeaking.value = true

                    android.util.Log.d(
                        "TTS_TEST",
                        "Utterance started: $utteranceId"
                    )
                }

                override fun onDone(
                    utteranceId: String?
                ) {

                    _isSpeaking.value = false

                    android.util.Log.d(
                        "TTS_TEST",
                        "Utterance completed: $utteranceId"
                    )
                }

                override fun onError(
                    utteranceId: String?
                ) {

                    _isSpeaking.value = false

                    android.util.Log.e(
                        "TTS_TEST",
                        "Utterance error: $utteranceId"
                    )
                }

                override fun onError(
                    utteranceId: String?,
                    errorCode: Int
                ) {

                    _isSpeaking.value = false

                    android.util.Log.e(
                        "TTS_TEST",
                        "Utterance error: id=$utteranceId code=$errorCode"
                    )
                }
            }
        )

        // -----------------------------------------------------
        // Process anything requested before initialization
        // -----------------------------------------------------

        val requests = pendingRequests.toList()
        pendingRequests.clear()

        requests.forEach { request ->

            speakInternal(
                text = request.text,
                language = request.language,
                onStatusMessage = request.callback
            )
        }
    }

    // =========================================================
    // LANGUAGE LOCALE
    // =========================================================

    private fun getLocale(
        language: Language
    ): Locale {

        return when (language) {

            Language.ENGLISH ->
                Locale("en", "IN")

            Language.HINDI ->
                Locale("hi", "IN")

            Language.SANTHALI ->
                Locale("sat", "IN")

            Language.MUNDARI ->
                Locale("unr", "IN")
        }
    }

    // =========================================================
    // FIND INSTALLED VOICE
    // =========================================================

    private fun findVoice(
        language: Language
    ): Voice? {

        val engine =
            tts ?: return null

        val voices =
            engine.voices ?: return null

        val targetLocale =
            getLocale(language)

        // -----------------------------------------------------
        // Exact language + country
        // -----------------------------------------------------

        val exactVoice =
            voices.firstOrNull { voice ->

                voice.locale.language.equals(
                    targetLocale.language,
                    ignoreCase = true
                ) &&
                voice.locale.country.equals(
                    targetLocale.country,
                    ignoreCase = true
                )
            }

        if (exactVoice != null) {
            return exactVoice
        }

        // -----------------------------------------------------
        // Language only
        // -----------------------------------------------------

        val languageVoice =
            voices.firstOrNull { voice ->

                voice.locale.language.equals(
                    targetLocale.language,
                    ignoreCase = true
                )
            }

        return languageVoice
    }

    // =========================================================
    // CHECK LANGUAGE SUPPORT
    // =========================================================

    fun isLanguageSupportedOnDevice(
        language: Language
    ): Boolean {

        if (!isInitialized || tts == null) {
            return false
        }

        if (
            language == Language.SANTHALI ||
            language == Language.MUNDARI
        ) {

            return findVoice(language) != null
        }

        val locale =
            getLocale(language)

        val result =
            tts?.isLanguageAvailable(locale)
                ?: TextToSpeech.LANG_NOT_SUPPORTED

        return result >= TextToSpeech.LANG_AVAILABLE
    }

    // =========================================================
    // PUBLIC SPEAK
    // =========================================================

    fun speak(
        text: String,
        language: Language,
        onStatusMessage: (String) -> Unit = {}
    ) {

        android.util.Log.d(
            "TTS_TEST",
            "SPEAK CALLED"
        )

        android.util.Log.d(
            "TTS_TEST",
            "Text = $text"
        )

        android.util.Log.d(
            "TTS_TEST",
            "Language = ${language.displayName}"
        )

        // -----------------------------------------------------
        // Empty text
        // -----------------------------------------------------

        if (text.isBlank()) {

            val message =
                "No text available for audio."

            _ttsMessage.value = message

            onStatusMessage(message)

            return
        }

        // -----------------------------------------------------
        // If TTS is still initializing
        // -----------------------------------------------------

        if (!isInitialized || tts == null) {

            android.util.Log.d(
                "TTS_TEST",
                "TTS not ready. Request queued."
            )

            pendingRequests.add(
                PendingSpeech(
                    text = text,
                    language = language,
                    callback = onStatusMessage
                )
            )

            _ttsMessage.value =
                "Preparing audio..."

            return
        }

        // -----------------------------------------------------
        // TTS ready
        // -----------------------------------------------------

        speakInternal(
            text = text,
            language = language,
            onStatusMessage = onStatusMessage
        )
    }

    // =========================================================
    // INTERNAL SPEAK
    // =========================================================

    private fun speakInternal(
        text: String,
        language: Language,
        onStatusMessage: (String) -> Unit
    ) {

        val engine =
            tts ?: return

        try {

            val locale =
                getLocale(language)

            // -------------------------------------------------
            // Santhali / Mundari
            // -------------------------------------------------

            if (
                language == Language.SANTHALI ||
                language == Language.MUNDARI
            ) {

                val voice =
                    findVoice(language)

                if (voice == null) {

                    val message =
                        "Voice output is not available for ${language.displayName} on this device."

                    _ttsMessage.value = message

                    onStatusMessage(message)

                    android.util.Log.e(
                        "TTS_TEST",
                        "No voice found for ${language.displayName}"
                    )

                    return
                }

                android.util.Log.d(
                    "TTS_TEST",
                    "Selected voice = ${voice.name}"
                )

                android.util.Log.d(
                    "TTS_TEST",
                    "Voice locale = ${voice.locale}"
                )

                android.util.Log.d(
                    "TTS_TEST",
                    "Network required = ${voice.isNetworkConnectionRequired}"
                )

                engine.voice = voice

                val languageResult =
                    engine.setLanguage(voice.locale)

                if (
                    languageResult ==
                    TextToSpeech.LANG_NOT_SUPPORTED
                ) {

                    val message =
                        "Selected voice does not support ${language.displayName}."

                    _ttsMessage.value = message

                    onStatusMessage(message)

                    return
                }
            }

            // -------------------------------------------------
            // English / Hindi
            // -------------------------------------------------

            else {

                val languageResult =
                    engine.setLanguage(locale)

                if (
                    languageResult ==
                    TextToSpeech.LANG_NOT_SUPPORTED ||
                    languageResult ==
                    TextToSpeech.LANG_MISSING_DATA
                ) {

                    val message =
                        "Voice output is not available for ${language.displayName} on this device."

                    _ttsMessage.value = message

                    onStatusMessage(message)

                    return
                }
            }

            // -------------------------------------------------
            // Utterance ID
            // -------------------------------------------------

            val utteranceId =
                "TRIBAL_LEARN_${System.currentTimeMillis()}"

            // -------------------------------------------------
            // Speak
            // -------------------------------------------------

            val result =
                engine.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    utteranceId
                )

            if (result == TextToSpeech.ERROR) {

                val message =
                    "Unable to play audio."

                _ttsMessage.value = message

                onStatusMessage(message)

                android.util.Log.e(
                    "TTS_TEST",
                    "TextToSpeech.speak() returned ERROR"
                )

                return
            }

            _isSpeaking.value = true
            _ttsMessage.value = null

            android.util.Log.d(
                "TTS_TEST",
                "Speech successfully queued"
            )

        } catch (e: Exception) {

            android.util.Log.e(
                "TTS_TEST",
                "Exception while speaking",
                e
            )

            val message =
                "Error playing audio."

            _ttsMessage.value = message

            onStatusMessage(message)
        }
    }

    // =========================================================
    // STOP
    // =========================================================

    fun stop() {

        try {

            tts?.stop()

            pendingRequests.clear()

            _isSpeaking.value = false

        } catch (e: Exception) {

            android.util.Log.e(
                "TTS_TEST",
                "Error stopping TTS",
                e
            )
        }
    }

    // =========================================================
    // SHUTDOWN
    // =========================================================

    fun shutdown() {

        try {

            pendingRequests.clear()

            tts?.stop()

            tts?.shutdown()

            tts = null

            isInitialized = false

            _isSpeaking.value = false

        } catch (e: Exception) {

            android.util.Log.e(
                "TTS_TEST",
                "Error shutting down TTS",
                e
            )
        }
    }
}