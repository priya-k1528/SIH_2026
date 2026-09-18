package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.FlashcardItem
import com.example.data.model.LessonItem
import com.example.data.model.QuizItem
import com.example.data.model.TranslationItem
import com.example.data.model.WorksheetItem
import com.example.data.repository.VernacularRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MaterialTab {
    ALL,
    LESSONS,
    WORKSHEETS,
    QUIZZES,
    FLASHCARDS,
    TRANSLATIONS
}

class MaterialsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VernacularRepository(AppDatabase.getDatabase(application))

    val lessons: StateFlow<List<LessonItem>> = repository.getAllLessons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val worksheets: StateFlow<List<WorksheetItem>> = repository.getAllWorksheets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizzes: StateFlow<List<QuizItem>> = repository.getAllQuizzes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashcards: StateFlow<List<FlashcardItem>> = repository.getAllFlashcards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val translations: StateFlow<List<TranslationItem>> = repository.getAllTranslations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTab = MutableStateFlow(MaterialTab.ALL)
    val selectedTab: StateFlow<MaterialTab> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun selectTab(tab: MaterialTab) {
        _selectedTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun deleteLesson(lesson: LessonItem) {
        viewModelScope.launch {
            repository.deleteLesson(lesson)
            _statusMessage.value = "Lesson deleted"
        }
    }

    fun deleteWorksheet(worksheet: WorksheetItem) {
        viewModelScope.launch {
            repository.deleteWorksheet(worksheet)
            _statusMessage.value = "Worksheet deleted"
        }
    }

    fun deleteQuiz(quiz: QuizItem) {
        viewModelScope.launch {
            repository.deleteQuiz(quiz)
            _statusMessage.value = "Quiz deleted"
        }
    }

    fun deleteTranslation(translation: TranslationItem) {
        viewModelScope.launch {
            repository.deleteTranslation(translation)
            _statusMessage.value = "Translation deleted"
        }
    }

    fun deleteFlashcard(card: FlashcardItem) {
        viewModelScope.launch {
            repository.deleteFlashcard(card)
            _statusMessage.value = "Flashcard deleted"
        }
    }
}
