package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.FlashcardItem
import com.example.data.model.Language
import com.example.data.repository.VernacularRepository
import com.example.services.AiContentService
import com.example.services.TextToSpeechService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlashcardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VernacularRepository(AppDatabase.getDatabase(application))
    private val aiService = AiContentService()
    val ttsService = TextToSpeechService(application)

    val allFlashcards: StateFlow<List<FlashcardItem>> = repository.getAllFlashcards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isFlipped = MutableStateFlow(false)
    val isFlipped: StateFlow<Boolean> = _isFlipped.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    val categories = listOf("All", "Classroom", "Numbers", "Colours", "Animals", "Fruits", "Shapes", "Objects")

    // === AI Flashcard Generation ===

    val generateCategories = listOf("Classroom", "Numbers", "Colours", "Animals", "Fruits", "Shapes", "Objects", "Custom")

    private val _topic = MutableStateFlow("")
    val topic: StateFlow<String> = _topic.asStateFlow()

    private val _generateCategory = MutableStateFlow("Classroom")
    val generateCategory: StateFlow<String> = _generateCategory.asStateFlow()

    private val _cardCount = MutableStateFlow(6)
    val cardCount: StateFlow<Int> = _cardCount.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generatedCards = MutableStateFlow<List<FlashcardItem>>(emptyList())
    val generatedCards: StateFlow<List<FlashcardItem>> = _generatedCards.asStateFlow()

    fun setTopic(t: String) {
        _topic.value = t
    }

    fun setGenerateCategory(cat: String) {
        _generateCategory.value = cat
    }

    fun setCardCount(count: Int) {
        _cardCount.value = count.coerceIn(1, 12)
    }

    fun generateFlashcards() {
        if (_topic.value.isBlank()) {
            _statusMessage.value = "Please enter a topic to generate flashcards."
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _statusMessage.value = null
            try {
                val cards = aiService.generateFlashcards(
                    topic = _topic.value,
                    category = _generateCategory.value,
                    count = _cardCount.value
                )
                if (cards.isEmpty()) {
                    _statusMessage.value = "Couldn't generate flashcards for that topic. Try a different word or category."
                } else {
                    _generatedCards.value = cards
                    _currentIndex.value = 0
                    _isFlipped.value = false
                    _statusMessage.value = "Generated ${cards.size} flashcards! Review them, then save to your deck."
                }
            } catch (e: Exception) {
                _statusMessage.value = "Failed to generate flashcards. Please try again."
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun saveGeneratedCards() {
        val cards = _generatedCards.value
        if (cards.isEmpty()) {
            _statusMessage.value = "Generate flashcards before saving."
            return
        }

        viewModelScope.launch {
            repository.saveFlashcards(cards)
            _statusMessage.value = "Saved ${cards.size} flashcards to your deck!"
            _generatedCards.value = emptyList()
            _currentIndex.value = 0
            _isFlipped.value = false
        }
    }

    fun discardGeneratedCards() {
        _generatedCards.value = emptyList()
        _currentIndex.value = 0
        _isFlipped.value = false
    }

    // === Deck Browsing ===

    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
        _currentIndex.value = 0
        _isFlipped.value = false
        _generatedCards.value = emptyList()
    }

    fun getFilteredCards(): List<FlashcardItem> {
        val all = allFlashcards.value
        return if (_selectedCategory.value == "All") all else all.filter { it.category.equals(_selectedCategory.value, ignoreCase = true) }
    }

    /**
     * The cards currently shown in the big card canvas: a freshly generated,
     * not-yet-saved preview takes priority over the saved deck so the user
     * can review AI results before committing them.
     */
    fun getDisplayCards(): List<FlashcardItem> {
        val preview = _generatedCards.value
        return if (preview.isNotEmpty()) preview else getFilteredCards()
    }

    fun isPreviewingGenerated(): Boolean = _generatedCards.value.isNotEmpty()

    fun nextCard() {
        val list = getDisplayCards()
        if (list.isNotEmpty()) {
            _currentIndex.value = (_currentIndex.value + 1) % list.size
            _isFlipped.value = false
        }
    }

    fun previousCard() {
        val list = getDisplayCards()
        if (list.isNotEmpty()) {
            _currentIndex.value = if (_currentIndex.value - 1 < 0) list.size - 1 else _currentIndex.value - 1
            _isFlipped.value = false
        }
    }

    fun flipCard() {
        _isFlipped.value = !_isFlipped.value
    }

    fun speak(text: String, language: Language) {
        ttsService.speak(text, language) { msg ->
            _statusMessage.value = msg
        }
    }

    fun addCustomFlashcard(
        category: String,
        english: String,
        hindi: String,
        santhaliOlChiki: String,
        santhaliRoman: String,
        mundariDevanagari: String,
        mundariRoman: String,
        emoji: String
    ) {
        viewModelScope.launch {
            val item = FlashcardItem(
                category = category,
                english = english,
                hindi = hindi,
                santhaliOlChiki = santhaliOlChiki,
                santhaliRoman = santhaliRoman,
                mundariDevanagari = mundariDevanagari,
                mundariRoman = mundariRoman,
                iconEmoji = emoji.ifBlank { "📚" },
                isCustom = true
            )
            repository.saveFlashcard(item)
            _statusMessage.value = "Added custom flashcard!"
        }
    }

    fun deleteCard(card: FlashcardItem) {
        viewModelScope.launch {
            repository.deleteFlashcard(card)
            _statusMessage.value = "Flashcard deleted."
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsService.shutdown()
    }
}
