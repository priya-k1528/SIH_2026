package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Language
import com.example.data.model.LessonItem
import com.example.data.repository.VernacularRepository
import com.example.services.AiContentService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LearnViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VernacularRepository(AppDatabase.getDatabase(application))
    private val aiService = AiContentService()

    val savedLessons: StateFlow<List<LessonItem>> = repository.getAllLessons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedGrade = MutableStateFlow("Grade 1")
    val selectedGrade: StateFlow<String> = _selectedGrade.asStateFlow()

    private val _selectedSubject = MutableStateFlow("Language")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    private val _topic = MutableStateFlow("Counting 1 to 5 with Animals")
    val topic: StateFlow<String> = _topic.asStateFlow()

    private val _difficulty = MutableStateFlow("Easy")
    val difficulty: StateFlow<String> = _difficulty.asStateFlow()

    private val _targetLanguage = MutableStateFlow(Language.HINDI)
    val targetLanguage: StateFlow<Language> = _targetLanguage.asStateFlow()

    private val _customInstruction = MutableStateFlow("")
    val customInstruction: StateFlow<String> = _customInstruction.asStateFlow()

    private val _generatedLesson = MutableStateFlow<LessonItem?>(null)
    val generatedLesson: StateFlow<LessonItem?> = _generatedLesson.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun setGrade(g: String) { _selectedGrade.value = g }
    fun setSubject(s: String) { _selectedSubject.value = s }
    fun setTopic(t: String) { _topic.value = t }
    fun setDifficulty(d: String) { _difficulty.value = d }
    fun setTargetLanguage(l: Language) { _targetLanguage.value = l }
    fun setCustomInstruction(ci: String) { _customInstruction.value = ci }

    fun generateLesson(generateAllLanguages: Boolean) {
        if (_topic.value.isBlank()) {
            _statusMessage.value = "Please enter a topic to generate a lesson."
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _statusMessage.value = null
            try {
                val lesson = aiService.generateLesson(
                    grade = _selectedGrade.value,
                    subject = _selectedSubject.value,
                    topic = _topic.value,
                    difficulty = _difficulty.value,
                    language = _targetLanguage.value,
                    customInstruction = _customInstruction.value,
                    generateAllLanguages = generateAllLanguages
                )
                _generatedLesson.value = lesson
                _statusMessage.value = "Lesson generated successfully!"
            } catch (e: Exception) {
                _statusMessage.value = "Failed to generate lesson. Please try again."
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun saveLesson() {
        val lesson = _generatedLesson.value ?: return
        viewModelScope.launch {
            repository.saveLesson(lesson)
            _statusMessage.value = "Lesson saved to Materials"
        }
    }

    fun deleteLesson(lesson: LessonItem) {
        viewModelScope.launch {
            repository.deleteLesson(lesson)
            _statusMessage.value = "Lesson removed"
        }
    }

    fun selectSavedLesson(lesson: LessonItem) {
        _generatedLesson.value = lesson
    }
}
