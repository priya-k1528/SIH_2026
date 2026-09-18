package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Language
import com.example.data.model.TranslationItem
import com.example.data.repository.VernacularRepository
import com.example.services.FullTranslationResult
import com.example.services.SpeechRecognitionService
import com.example.services.SpeechState
import com.example.services.TextToSpeechService
import com.example.services.TranslationService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VoiceTranslationViewModel(
    application: Application
) : AndroidViewModel(application) {

    // ---------------------------------------------------------
    // SERVICES
    // ---------------------------------------------------------

    private val repository =
        VernacularRepository(
            AppDatabase.getDatabase(application)
        )

    val speechService =
        SpeechRecognitionService(application)

    val ttsService =
        TextToSpeechService(application)

    val translationService =
        TranslationService(application)


    // ---------------------------------------------------------
    // SPEECH STATE
    // ---------------------------------------------------------

    val speechState: StateFlow<SpeechState> =
        speechService.speechState

    val recognizedText: StateFlow<String> =
        speechService.recognizedText

    val soundLevel: StateFlow<Float> =
        speechService.soundLevel

    val errorMessage: StateFlow<String?> =
        speechService.errorMessage


    // ---------------------------------------------------------
    // TRANSLATION RESULT
    // ---------------------------------------------------------

    private val _translationResult =
        MutableStateFlow<FullTranslationResult?>(null)

    val translationResult: StateFlow<FullTranslationResult?> =
        _translationResult.asStateFlow()


    // ---------------------------------------------------------
    // SAVED STATE
    // ---------------------------------------------------------

    private val _isSaved =
        MutableStateFlow(false)

    val isSaved: StateFlow<Boolean> =
        _isSaved.asStateFlow()


    // ---------------------------------------------------------
    // STATUS MESSAGE
    // ---------------------------------------------------------

    private val _statusMessage =
        MutableStateFlow<String?>(null)

    val statusMessage: StateFlow<String?> =
        _statusMessage.asStateFlow()


    // =========================================================
    // START VOICE RECORDING
    // =========================================================

    fun startVoiceRecording(language: Language) {

        _isSaved.value = false
        _translationResult.value = null
        _statusMessage.value = null

        speechService.startListening(

            language = language,

            onResult = { spokenText ->

                processAndTranslate(
                    text = spokenText,
                    sourceLang = language
                )
            },

            onError = { error ->

                _statusMessage.value = error

                speechService.setState(
                    SpeechState.ERROR
                )
            }
        )
    }


    // =========================================================
    // STOP VOICE RECORDING
    // =========================================================

    fun stopVoiceRecording() {

        speechService.stopListening()
    }


    // =========================================================
    // VOICE TEXT
    //        ↓
    // TRANSLATION
    //        ↓
    // SANTHALI AUDIO
    // =========================================================

    fun processAndTranslate(
        text: String,
        sourceLang: Language
    ) {

        viewModelScope.launch {

            speechService.setState(
                SpeechState.TRANSLATING
            )

            try {

                // -------------------------------------------------
                // 1. TRANSLATE VOICE TEXT
                // -------------------------------------------------

                val result =
                    translationService.translate(
                        text = text,
                        fromLanguage = sourceLang,
                        toLanguage = Language.SANTHALI
                    )


                // -------------------------------------------------
                // 2. STORE TRANSLATION RESULT
                // -------------------------------------------------

                _translationResult.value = result


                // -------------------------------------------------
                // 3. GET SANTHALI OL CHIKI TEXT
                // -------------------------------------------------

                val santhaliText =
                    result.santhaliOlChiki.trim()


                // -------------------------------------------------
                // 4. PLAY SANTHALI AUDIO
                // -------------------------------------------------

                if (santhaliText.isNotEmpty()) {

                    /*
                     * Small delay so that the translation result
                     * is completely updated before TTS starts.
                     */
                    delay(300)

                    playTts(
                        text = santhaliText,
                        language = Language.SANTHALI
                    )

                } else {

                    speechService.setState(
                        SpeechState.COMPLETED
                    )

                    _statusMessage.value =
                        "Santhali translation is empty."
                }

            } catch (e: Exception) {

                speechService.setState(
                    SpeechState.ERROR
                )

                _statusMessage.value =
                    "Translation error. Please try again."
            }
        }
    }


    // =========================================================
    // PLAY TEXT-TO-SPEECH
    // =========================================================

    fun playTts(
        text: String,
        language: Language
    ) {

        if (text.isBlank()) {

            _statusMessage.value =
                "No text available for audio."

            speechService.setState(
                SpeechState.COMPLETED
            )

            return
        }


        // -------------------------------------------------------
        // SET SPEAKING STATE
        // -------------------------------------------------------

        speechService.setState(
            SpeechState.SPEAKING
        )


        // -------------------------------------------------------
        // START TTS
        // -------------------------------------------------------

        ttsService.speak(
            text = text,
            language = language
        ) { message ->

            _statusMessage.value = message

            speechService.setState(
                SpeechState.COMPLETED
            )
        }
    }


    // =========================================================
    // STOP TTS
    // =========================================================

    fun stopTts() {

        ttsService.stop()

        speechService.setState(
            SpeechState.COMPLETED
        )
    }


    // =========================================================
    // SAVE CURRENT TRANSLATION
    // =========================================================

    fun saveCurrentTranslation(
        contextTopic: String = ""
    ) {

        val current =
            _translationResult.value
                ?: return


        viewModelScope.launch {

            val item =
                TranslationItem(

                    sourceText =
                        current.sourceText,

                    sourceLanguage =
                        current.sourceLanguage.id,

                    targetLanguage =
                        current.targetLanguage.id,

                    translatedText =
                        current.primaryTranslation,

                    englishText =
                        current.englishText,

                    hindiText =
                        current.hindiText,

                    santhaliOlChiki =
                        current.santhaliOlChiki,

                    santhaliRoman =
                        current.santhaliRoman,

                    mundariDevanagari =
                        current.mundariDevanagari,

                    mundariRoman =
                        current.mundariRoman,

                    pronunciationGuide =
                        current.pronunciationGuide,

                    translationSource =
                        current.sourceEngine,

                    contextTopic =
                        contextTopic
                )


            repository.saveTranslation(item)

            _isSaved.value = true

            _statusMessage.value =
                "Saved to Materials successfully"
        }
    }


    // =========================================================
    // CLEAR
    // =========================================================

    fun clear() {

        speechService.clear()

        ttsService.stop()

        _translationResult.value = null

        _isSaved.value = false

        _statusMessage.value = null
    }


    // =========================================================
    // CLEANUP
    // =========================================================

    override fun onCleared() {

        super.onCleared()

        speechService.destroyRecognizer()

        ttsService.shutdown()
    }
}