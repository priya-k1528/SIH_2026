package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.ui.components.AppBottomBar
import com.example.ui.screens.FlashcardsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.MaterialsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.TranslateScreen
import com.example.ui.screens.VoiceTranslationScreen
import com.example.ui.screens.WorksheetsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FlashcardViewModel
import com.example.ui.viewmodel.LearnViewModel
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.MaterialsViewModel
import com.example.ui.viewmodel.ProfileViewModel
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TranslationViewModel
import com.example.ui.viewmodel.VoiceTranslationViewModel
import com.example.ui.viewmodel.WorksheetViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val voiceViewModel: VoiceTranslationViewModel by viewModels()
    private val translationViewModel: TranslationViewModel by viewModels()
    private val learnViewModel: LearnViewModel by viewModels()
    private val worksheetViewModel: WorksheetViewModel by viewModels()
    private val quizViewModel: QuizViewModel by viewModels()
    private val flashcardViewModel: FlashcardViewModel by viewModels()
    private val materialsViewModel: MaterialsViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppContent(
                    mainViewModel = mainViewModel,
                    voiceViewModel = voiceViewModel,
                    translationViewModel = translationViewModel,
                    learnViewModel = learnViewModel,
                    worksheetViewModel = worksheetViewModel,
                    quizViewModel = quizViewModel,
                    flashcardViewModel = flashcardViewModel,
                    materialsViewModel = materialsViewModel,
                    profileViewModel = profileViewModel
                )
            }
        }
    }
}

@Composable
fun MainAppContent(
    mainViewModel: MainViewModel,
    voiceViewModel: VoiceTranslationViewModel,
    translationViewModel: TranslationViewModel,
    learnViewModel: LearnViewModel,
    worksheetViewModel: WorksheetViewModel,
    quizViewModel: QuizViewModel,
    flashcardViewModel: FlashcardViewModel,
    materialsViewModel: MaterialsViewModel,
    profileViewModel: ProfileViewModel
) {
    val currentScreen by mainViewModel.currentScreen.collectAsState()
    val appLanguage by mainViewModel.appLanguage.collectAsState()
    val teachingLanguage by mainViewModel.teachingLanguage.collectAsState()
    val isOfflineMode by mainViewModel.isOfflineMode.collectAsState()
    val defaultContext by mainViewModel.defaultContext.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        mainViewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Handle back button: if not on Home, navigate back to Home
    BackHandler(enabled = currentScreen != Screen.Home && currentScreen != Screen.Onboarding) {
        mainViewModel.navigateTo(Screen.Home)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = com.example.ui.theme.SleekBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentScreen != Screen.Onboarding) {
                AppBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> mainViewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Onboarding -> {
                    OnboardingScreen(
                        appLanguage = appLanguage,
                        onSelectLanguage = { mainViewModel.setAppLanguage(it) },
                        onComplete = { mainViewModel.completeOnboarding() }
                    )
                }
                Screen.Home -> {
                    HomeScreen(
                        appLanguage = appLanguage,
                        teachingLanguage = teachingLanguage,
                        isOfflineMode = isOfflineMode,
                        onToggleOffline = { mainViewModel.toggleOfflineMode() },
                        activeContext = defaultContext,
                        onNavigate = { screen -> mainViewModel.navigateTo(screen) },
                        onSpeakPhrase = { text, lang ->
                            voiceViewModel.playTts(text, lang)
                        }
                    )
                }
                Screen.Voice -> {
                    VoiceTranslationScreen(
                        viewModel = voiceViewModel,
                        activeContext = defaultContext,
                        onShowSnackbar = { msg -> coroutineScope.launch { snackbarHostState.showSnackbar(msg) } }
                    )
                }
                Screen.Translate -> {
                    TranslateScreen(
                        viewModel = translationViewModel,
                        onShowSnackbar = { msg -> coroutineScope.launch { snackbarHostState.showSnackbar(msg) } }
                    )
                }
                Screen.Learn -> {
                    LearnScreen(
                        viewModel = learnViewModel,
                        onShowSnackbar = { msg -> coroutineScope.launch { snackbarHostState.showSnackbar(msg) } }
                    )
                }
                Screen.Worksheets -> {
                    WorksheetsScreen(
                        viewModel = worksheetViewModel,
                        onShowSnackbar = { msg -> coroutineScope.launch { snackbarHostState.showSnackbar(msg) } }
                    )
                }
                Screen.Quiz -> {
                    QuizScreen(
                        viewModel = quizViewModel,
                        onShowSnackbar = { msg -> coroutineScope.launch { snackbarHostState.showSnackbar(msg) } }
                    )
                }
                Screen.Flashcards -> {
                    FlashcardsScreen(
                        viewModel = flashcardViewModel,
                        onShowSnackbar = { msg -> coroutineScope.launch { snackbarHostState.showSnackbar(msg) } }
                    )
                }
                Screen.Materials -> {
                    MaterialsScreen(
                        viewModel = materialsViewModel,
                        onNavigate = { screen -> mainViewModel.navigateTo(screen) },
                        onShowSnackbar = { msg -> coroutineScope.launch { snackbarHostState.showSnackbar(msg) } }
                    )
                }
                Screen.Profile -> {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        appLanguage = appLanguage,
                        teachingLanguage = teachingLanguage,
                        onSetAppLanguage = { mainViewModel.setAppLanguage(it) },
                        onSetTeachingLanguage = { mainViewModel.setTeachingLanguage(it) },
                        onShowSnackbar = { msg -> coroutineScope.launch { snackbarHostState.showSnackbar(msg) } }
                    )
                }
            }
        }
    }
}
