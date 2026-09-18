package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.EducationalContext
import com.example.data.model.Language
import com.example.data.repository.VernacularRepository
import com.example.services.SpeechRecognitionService
import com.example.services.TextToSpeechService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DeviceDiagnostics(
    val isSpeechRecognitionAvailable: Boolean,
    val isEnglishTtsSupported: Boolean,
    val isHindiTtsSupported: Boolean,
    val isSanthaliTtsSupported: Boolean,
    val isMundariTtsSupported: Boolean
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VernacularRepository(AppDatabase.getDatabase(application))
    private val speechService = SpeechRecognitionService(application)
    private val ttsService = TextToSpeechService(application)

    val educationalContexts: StateFlow<List<EducationalContext>> = repository.getAllEducationalContexts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val defaultContext: StateFlow<EducationalContext?> = repository.getDefaultEducationalContext()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _diagnostics = MutableStateFlow<DeviceDiagnostics?>(null)
    val diagnostics: StateFlow<DeviceDiagnostics?> = _diagnostics.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        runDiagnostics()
    }

    fun runDiagnostics() {
        val sttAvail = speechService.isDeviceSpeechAvailable()
        val enTts = ttsService.isLanguageSupportedOnDevice(Language.ENGLISH)
        val hiTts = ttsService.isLanguageSupportedOnDevice(Language.HINDI)
        val satTts = ttsService.isLanguageSupportedOnDevice(Language.SANTHALI)
        val munTts = ttsService.isLanguageSupportedOnDevice(Language.MUNDARI)

        _diagnostics.value = DeviceDiagnostics(
            isSpeechRecognitionAvailable = sttAvail,
            isEnglishTtsSupported = enTts,
            isHindiTtsSupported = hiTts,
            isSanthaliTtsSupported = satTts,
            isMundariTtsSupported = munTts
        )
    }

    fun addEducationalContext(
        title: String,
        grade: String,
        subject: String,
        topic: String,
        customInstructions: String,
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            val id = repository.saveEducationalContext(
                EducationalContext(
                    title = title,
                    grade = grade,
                    subject = subject,
                    topic = topic,
                    customInstructions = customInstructions,
                    isDefault = isDefault
                )
            )
            if (isDefault) {
                repository.setDefaultEducationalContext(id)
            }
            _statusMessage.value = "Educational context added"
        }
    }

    fun setDefaultContext(context: EducationalContext) {
        viewModelScope.launch {
            repository.setDefaultEducationalContext(context.id)
            _statusMessage.value = "Default context updated: ${context.title}"
        }
    }

    fun deleteContext(context: EducationalContext) {
        viewModelScope.launch {
            repository.deleteEducationalContext(context)
            _statusMessage.value = "Educational context deleted"
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechService.destroyRecognizer()
        ttsService.shutdown()
    }
}
