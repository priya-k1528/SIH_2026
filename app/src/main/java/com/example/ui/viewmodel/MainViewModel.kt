package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.EducationalContext
import com.example.data.model.Language
import com.example.data.repository.VernacularRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String) {
    data object Onboarding : Screen("onboarding", "Welcome")
    data object Home : Screen("home", "Dashboard")
    data object Translate : Screen("translate", "Translate")
    data object Voice : Screen("voice", "Voice Translation")
    data object Learn : Screen("learn", "Learn & Curriculum")
    data object Worksheets : Screen("worksheets", "Worksheets")
    data object Quiz : Screen("quiz", "Quiz")
    data object Flashcards : Screen("flashcards", "Flashcards")
    data object Materials : Screen("materials", "Materials")
    data object Profile : Screen("profile", "Profile & Settings")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository: VernacularRepository = VernacularRepository(AppDatabase.getDatabase(application))

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _appLanguage = MutableStateFlow(Language.ENGLISH)
    val appLanguage: StateFlow<Language> = _appLanguage.asStateFlow()

    private val _teachingLanguage = MutableStateFlow(Language.HINDI)
    val teachingLanguage: StateFlow<Language> = _teachingLanguage.asStateFlow()

    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(false)
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    val defaultContext: StateFlow<EducationalContext?> = repository.getDefaultEducationalContext()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        loadPersistedSettings()
    }

    private fun loadPersistedSettings() {
        viewModelScope.launch {
            repository.getSetting("app_language")?.let { id ->
                _appLanguage.value = Language.fromId(id)
            }
            repository.getSetting("teaching_language")?.let { id ->
                _teachingLanguage.value = Language.fromId(id)
            }
            repository.getSetting("offline_mode")?.let { boolStr ->
                _isOfflineMode.value = boolStr.toBoolean()
            }
            val completed = repository.getSetting("onboarding_completed")?.toBoolean() ?: false
            _isOnboardingCompleted.value = completed
            if (!completed) {
                _currentScreen.value = Screen.Onboarding
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            _isOnboardingCompleted.value = true
            repository.setSetting("onboarding_completed", "true")
            _currentScreen.value = Screen.Home
        }
    }

    fun setAppLanguage(language: Language) {
        viewModelScope.launch {
            _appLanguage.value = language
            repository.setSetting("app_language", language.id)
            showSnackbar("App language set to ${language.displayName}")
        }
    }

    fun setTeachingLanguage(language: Language) {
        viewModelScope.launch {
            _teachingLanguage.value = language
            repository.setSetting("teaching_language", language.id)
            showSnackbar("Teaching language set to ${language.displayName}")
        }
    }

    fun toggleOfflineMode() {
        viewModelScope.launch {
            val newState = !_isOfflineMode.value
            _isOfflineMode.value = newState
            repository.setSetting("offline_mode", newState.toString())
            val status = if (newState) "Offline Mode (Using local vernacular database)" else "Online Mode"
            showSnackbar(status)
        }
    }

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            _snackbarEvent.emit(message)
        }
    }
}
