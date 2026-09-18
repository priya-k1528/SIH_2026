package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Language
import com.example.data.model.TranslationItem
import com.example.data.repository.VernacularRepository
import com.example.services.FullTranslationResult
import com.example.services.TextToSpeechService
import com.example.services.TranslationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TranslationViewModel(application: Application) : AndroidViewModel(application) {

    // ---------------------------------------------------------
    // SERVICES
    // ---------------------------------------------------------

    private val repository =
        VernacularRepository(
            AppDatabase.getDatabase(application)
        )

    private val translationService =
        TranslationService(application)

    val ttsService =
        TextToSpeechService(application)


    // ---------------------------------------------------------
    // LANGUAGE SELECTION
    // ---------------------------------------------------------

    private val _fromLanguage =
        MutableStateFlow(Language.HINDI)

    val fromLanguage: StateFlow<Language> =
        _fromLanguage.asStateFlow()


    private val _toLanguage =
        MutableStateFlow(Language.SANTHALI)

    val toLanguage: StateFlow<Language> =
        _toLanguage.asStateFlow()


    // ---------------------------------------------------------
    // INPUT TEXT
    // ---------------------------------------------------------

    private val _inputText =
        MutableStateFlow("")

    val inputText: StateFlow<String> =
        _inputText.asStateFlow()


    // ---------------------------------------------------------
    // TRANSLATION RESULT
    // ---------------------------------------------------------

    private val _translationResult =
        MutableStateFlow<FullTranslationResult?>(null)

    val translationResult: StateFlow<FullTranslationResult?> =
        _translationResult.asStateFlow()


    // ---------------------------------------------------------
    // LOADING
    // ---------------------------------------------------------

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()


    // ---------------------------------------------------------
    // SAVE STATUS
    // ---------------------------------------------------------

    private val _isSaved =
        MutableStateFlow(false)

    val isSaved: StateFlow<Boolean> =
        _isSaved.asStateFlow()


    // ---------------------------------------------------------
    // MESSAGE
    // ---------------------------------------------------------

    private val _snackbarMessage =
        MutableStateFlow<String?>(null)

    val snackbarMessage: StateFlow<String?> =
        _snackbarMessage.asStateFlow()


    // =========================================================
    // LANGUAGE FUNCTIONS
    // =========================================================

    fun setFromLanguage(language: Language) {

        _fromLanguage.value = language

        // Old translation is no longer valid
        _translationResult.value = null
        _isSaved.value = false
    }


    fun setToLanguage(language: Language) {

        _toLanguage.value = language

        // Old translation is no longer valid
        _translationResult.value = null
        _isSaved.value = false
    }


    // =========================================================
    // INPUT TEXT
    // =========================================================

    fun setInputText(text: String) {

        _inputText.value = text

        _isSaved.value = false
    }


    // =========================================================
    // SWAP LANGUAGES
    // =========================================================

    fun swapLanguages() {

        val oldFrom = _fromLanguage.value
        val oldTo = _toLanguage.value

        _fromLanguage.value = oldTo
        _toLanguage.value = oldFrom

        val currentResult = _translationResult.value

        if (currentResult != null) {

            _inputText.value =
                currentResult.primaryTranslation

            _translationResult.value = null
        }

        _isSaved.value = false
    }


    // =========================================================
    // TRANSLATE
    // =========================================================

    fun translate() {

        val query =
            _inputText.value.trim()

        if (query.isBlank()) {

            _snackbarMessage.value =
                "Please enter some text."

            return
        }


        viewModelScope.launch {

            _isLoading.value = true
            _isSaved.value = false
            _snackbarMessage.value = null

            try {

                val result =
                    translationService.translate(
                        text = query,
                        fromLanguage = _fromLanguage.value,
                        toLanguage = _toLanguage.value
                    )

                _translationResult.value = result

            } catch (e: Exception) {

                _translationResult.value = null

                _snackbarMessage.value =
                    "Translation error. Please try again."

            } finally {

                _isLoading.value = false
            }
        }
    }


    // =========================================================
    // PLAY AUDIO
    // =========================================================

    fun playAudio(
        text: String,
        language: Language
    ) {

        val cleanText =
            text.trim()

        if (cleanText.isBlank()) {

            _snackbarMessage.value =
                "No text available for audio."

            return
        }

        ttsService.speak(
            cleanText,
            language
        ) { message ->

            _snackbarMessage.value = message
        }
    }


    // =========================================================
    // SAVE TRANSLATION
    // =========================================================

    fun saveTranslation() {

        val result =
            _translationResult.value
                ?: return

        viewModelScope.launch {

            try {

                val item =
                    TranslationItem(

                        sourceText =
                            result.sourceText,

                        sourceLanguage =
                            result.sourceLanguage.id,

                        targetLanguage =
                            result.targetLanguage.id,

                        translatedText =
                            result.primaryTranslation,

                        englishText =
                            result.englishText,

                        hindiText =
                            result.hindiText,

                        santhaliOlChiki =
                            result.santhaliOlChiki,

                        santhaliRoman =
                            result.santhaliRoman,

                        mundariDevanagari =
                            result.mundariDevanagari,

                        mundariRoman =
                            result.mundariRoman,

                        pronunciationGuide =
                            result.pronunciationGuide,

                        translationSource =
                            result.sourceEngine
                    )


                repository.saveTranslation(item)

                _isSaved.value = true

                _snackbarMessage.value =
                    "Saved to Materials"

            } catch (e: Exception) {

                _snackbarMessage.value =
                    "Could not save translation."
            }
        }
    }


    // =========================================================
    // CLEAR
    // =========================================================

    fun clear() {

        _inputText.value = ""

        _translationResult.value = null

        _isSaved.value = false

        _snackbarMessage.value = null
    }


    // =========================================================
    // VIEWMODEL CLEANUP
    // =========================================================

    override fun onCleared() {

        ttsService.shutdown()

        super.onCleared()
    }
}