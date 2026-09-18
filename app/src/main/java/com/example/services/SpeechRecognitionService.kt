package com.example.services

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.data.model.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class SpeechState {
    IDLE,
    LISTENING,
    PROCESSING,
    TRANSLATING,
    SPEAKING,
    COMPLETED,
    ERROR
}

class SpeechRecognitionService(
    private val context: Context
) {

    companion object {
        private const val TAG = "SPEECH_TEST"
    }

    private var speechRecognizer: SpeechRecognizer? = null

    private val _speechState =
        MutableStateFlow(SpeechState.IDLE)

    val speechState: StateFlow<SpeechState> =
        _speechState.asStateFlow()

    private val _recognizedText =
        MutableStateFlow("")

    val recognizedText: StateFlow<String> =
        _recognizedText.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    private val _soundLevel =
        MutableStateFlow(0f)

    val soundLevel: StateFlow<Float> =
        _soundLevel.asStateFlow()

    // ============================================================
    // CHECK GENERAL SPEECH RECOGNITION
    // ============================================================

    fun isDeviceSpeechAvailable(): Boolean {

        return SpeechRecognizer.isRecognitionAvailable(
            context
        )
    }

    // ============================================================
    // CHECK LANGUAGE SUPPORT
    // ============================================================

    private fun isLanguageAvailable(
        language: Language
    ): Boolean {

        val recognizer =
            speechRecognizer
                ?: try {

                    SpeechRecognizer.createSpeechRecognizer(
                        context
                    )

                } catch (e: Exception) {

                    Log.e(
                        TAG,
                        "Unable to create speech recognizer",
                        e
                    )

                    return false
                }

        val intent =
            Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            ).apply {

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    language.speechCode
                )

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                    language.speechCode
                )
            }

        /*
         * Android does not provide a completely reliable
         * synchronous API to check whether a specific language
         * has an STT model.
         *
         * Therefore we try the requested language when
         * startListening() is called.
         */

        Log.d(
            TAG,
            "Requested speech language = ${language.speechCode}"
        )

        if (speechRecognizer == null) {
            try {
                recognizer.destroy()
            } catch (_: Exception) {
            }
        }

        return true
    }

    // ============================================================
    // START LISTENING
    // ============================================================

    fun startListening(
        language: Language,
        onResult: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {

        _errorMessage.value = null
        _soundLevel.value = 0f

        Log.d(
            TAG,
            "START LISTENING"
        )

        Log.d(
            TAG,
            "Selected language = ${language.displayName}"
        )

        Log.d(
            TAG,
            "Speech code = ${language.speechCode}"
        )

        // --------------------------------------------------------
        // General speech recognition availability
        // --------------------------------------------------------

        if (!isDeviceSpeechAvailable()) {

            val msg =
                "Speech recognition is not available on this device."

            Log.e(
                TAG,
                msg
            )

            _errorMessage.value = msg
            _speechState.value = SpeechState.ERROR

            onError(msg)

            return
        }

        // --------------------------------------------------------
        // Informational check
        // --------------------------------------------------------

        isLanguageAvailable(language)

        // --------------------------------------------------------
        // Destroy previous recognizer
        // --------------------------------------------------------

        destroyRecognizer()

        try {

            speechRecognizer =
                SpeechRecognizer
                    .createSpeechRecognizer(context)
                    .apply {

                        setRecognitionListener(
                            createRecognitionListener(
                                onResult = onResult,
                                onError = onError,
                                language = language
                            )
                        )
                    }

            // ----------------------------------------------------
            // Recognition intent
            // ----------------------------------------------------

            val intent =
                Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                ).apply {

                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )

                    /*
                     * This is the most important line.
                     *
                     * It asks Android's speech recognizer to
                     * recognize the selected language.
                     */

                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE,
                        language.speechCode
                    )

                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                        language.speechCode
                    )

                    putExtra(
                        RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                        true
                    )

                    putExtra(
                        RecognizerIntent.EXTRA_MAX_RESULTS,
                        1
                    )

                    putExtra(
                        RecognizerIntent.EXTRA_PROMPT,
                        "Speak in ${language.displayName}"
                    )
                }

            Log.d(
                TAG,
                "Starting recognizer with locale = ${language.speechCode}"
            )

            _speechState.value =
                SpeechState.LISTENING

            speechRecognizer?.startListening(
                intent
            )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Unable to start speech recognition",
                e
            )

            val msg =
                "Unable to start voice recording. Please check microphone permission."

            _errorMessage.value = msg
            _speechState.value = SpeechState.ERROR

            onError(msg)
        }
    }

    // ============================================================
    // RECOGNITION LISTENER
    // ============================================================

    private fun createRecognitionListener(
        onResult: (String) -> Unit,
        onError: (String) -> Unit,
        language: Language
    ): RecognitionListener {

        return object : RecognitionListener {

            override fun onReadyForSpeech(
                params: Bundle?
            ) {

                Log.d(
                    TAG,
                    "Ready for speech"
                )

                _speechState.value =
                    SpeechState.LISTENING
            }

            override fun onBeginningOfSpeech() {

                Log.d(
                    TAG,
                    "Beginning of speech"
                )

                _speechState.value =
                    SpeechState.LISTENING
            }

            override fun onRmsChanged(
                rmsdB: Float
            ) {

                val normalized =
                    ((rmsdB + 2f) / 12f)
                        .coerceIn(
                            0f,
                            1f
                        )

                _soundLevel.value =
                    normalized
            }

            override fun onBufferReceived(
                buffer: ByteArray?
            ) {
                // Not required
            }

            override fun onEndOfSpeech() {

                Log.d(
                    TAG,
                    "End of speech"
                )

                _speechState.value =
                    SpeechState.PROCESSING

                _soundLevel.value = 0f
            }

            override fun onError(
                error: Int
            ) {

                _soundLevel.value = 0f

                Log.e(
                    TAG,
                    "Speech recognition error = $error"
                )

                val friendlyMessage =
                    getFriendlyErrorMessage(
                        error = error,
                        language = language
                    )

                _errorMessage.value =
                    friendlyMessage

                _speechState.value =
                    SpeechState.ERROR

                onError(
                    friendlyMessage
                )
            }

            override fun onResults(
                results: Bundle?
            ) {

                _soundLevel.value = 0f

                val matches =
                    results?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )

                val spokenText =
                    matches
                        ?.firstOrNull()
                        ?.trim()
                        ?: ""

                Log.d(
                    TAG,
                    "Final recognized text = $spokenText"
                )

                if (
                    spokenText.isNotBlank()
                ) {

                    _recognizedText.value =
                        spokenText

                    _speechState.value =
                        SpeechState.COMPLETED

                    _errorMessage.value =
                        null

                    onResult(
                        spokenText
                    )

                } else {

                    val msg =
                        "No speech detected. Please speak again."

                    _errorMessage.value =
                        msg

                    _speechState.value =
                        SpeechState.ERROR

                    onError(msg)
                }
            }

            override fun onPartialResults(
                partialResults: Bundle?
            ) {

                val matches =
                    partialResults?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )

                val partial =
                    matches
                        ?.firstOrNull()
                        ?.trim()

                if (
                    !partial.isNullOrBlank()
                ) {

                    Log.d(
                        TAG,
                        "Partial result = $partial"
                    )

                    _recognizedText.value =
                        partial
                }
            }

            override fun onEvent(
                eventType: Int,
                params: Bundle?
            ) {
                // Not required
            }
        }
    }

    // ============================================================
    // STOP LISTENING
    // ============================================================

    fun stopListening() {

        Log.d(
            TAG,
            "Stopping speech recognition"
        )

        try {

            speechRecognizer?.stopListening()

            _speechState.value =
                SpeechState.PROCESSING

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Error while stopping speech recognition",
                e
            )
        }
    }

    // ============================================================
    // CANCEL
    // ============================================================

    fun cancel() {

        Log.d(
            TAG,
            "Cancelling speech recognition"
        )

        try {

            speechRecognizer?.cancel()

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Error while cancelling speech recognition",
                e
            )
        }

        _soundLevel.value = 0f

        _speechState.value =
            SpeechState.IDLE
    }

    // ============================================================
    // SET STATE
    // ============================================================

    fun setState(
        state: SpeechState
    ) {

        _speechState.value =
            state
    }

    // ============================================================
    // CLEAR
    // ============================================================

    fun clear() {

        _recognizedText.value = ""

        _errorMessage.value = null

        _speechState.value =
            SpeechState.IDLE

        _soundLevel.value = 0f
    }

    // ============================================================
    // DESTROY
    // ============================================================

    fun destroyRecognizer() {

        try {

            speechRecognizer?.destroy()

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Error destroying recognizer",
                e
            )
        }

        speechRecognizer = null
    }

    // ============================================================
    // FRIENDLY ERROR
    // ============================================================

    private fun getFriendlyErrorMessage(
        error: Int,
        language: Language
    ): String {

        return when (error) {

            SpeechRecognizer.ERROR_AUDIO -> {

                "Audio recording error. Please check your microphone."
            }

            SpeechRecognizer.ERROR_CLIENT -> {

                "Speech recognition client error. Please try again."
            }

            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {

                "Microphone permission is required for voice translation."
            }

            SpeechRecognizer.ERROR_NETWORK -> {

                /*
                 * For Santhali/Mundari this may mean the installed
                 * recognizer requires network or does not have the
                 * offline model.
                 */

                if (
                    language == Language.MUNDARI
                ) {

                    "Mundari voice input is not available offline on this device."

                } else if (
                    language == Language.SANTHALI
                ) {

                    "Santhali voice input is not available offline on this device."

                } else {

                    "Network issue encountered during speech recognition."
                }
            }

            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {

                "Speech recognition timed out. Please try again."
            }

            SpeechRecognizer.ERROR_NO_MATCH -> {

                if (
                    language == Language.MUNDARI
                ) {

                    "Mundari speech could not be recognized. The installed speech engine may not support Mundari."

                } else if (
                    language == Language.SANTHALI
                ) {

                    "Santhali speech could not be recognized. The installed speech engine may not support Santhali."

                } else {

                    "Could not understand speech clearly. Please speak again."
                }
            }

            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {

                "Speech recognizer is busy. Please wait a moment."
            }

            SpeechRecognizer.ERROR_SERVER -> {

                "Speech recognition server error. Please try again."
            }

            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {

                "No speech detected. Please tap the microphone and speak again."
            }

            else -> {

                "Speech recognition error for ${language.displayName}. Please try again."
            }
        }
    }
}