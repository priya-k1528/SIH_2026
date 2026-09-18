package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Language
import com.example.data.model.WorksheetItem
import com.example.data.model.WorksheetQuestion
import com.example.data.repository.VernacularRepository
import com.example.services.AiContentService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class WorksheetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VernacularRepository(AppDatabase.getDatabase(application))
    private val aiService = AiContentService()

    val savedWorksheets: StateFlow<List<WorksheetItem>> = repository.getAllWorksheets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _grade = MutableStateFlow("Grade 1")
    val grade: StateFlow<String> = _grade.asStateFlow()

    private val _subject = MutableStateFlow("Language")
    val subject: StateFlow<String> = _subject.asStateFlow()

    private val _topic = MutableStateFlow("Forest Animals and Birds")
    val topic: StateFlow<String> = _topic.asStateFlow()

    private val _language = MutableStateFlow(Language.HINDI)
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _questionCount = MutableStateFlow(5)
    val questionCount: StateFlow<Int> = _questionCount.asStateFlow()

    private val _difficulty = MutableStateFlow("Easy")
    val difficulty: StateFlow<String> = _difficulty.asStateFlow()

    private val _questionType = MutableStateFlow("MCQ")
    val questionType: StateFlow<String> = _questionType.asStateFlow()

    private val _customInstruction = MutableStateFlow("")
    val customInstruction: StateFlow<String> = _customInstruction.asStateFlow()

    private val _questions = MutableStateFlow<List<WorksheetQuestion>>(emptyList())
    val questions: StateFlow<List<WorksheetQuestion>> = _questions.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun setGrade(g: String) { _grade.value = g }
    fun setSubject(s: String) { _subject.value = s }
    fun setTopic(t: String) { _topic.value = t }
    fun setLanguage(l: Language) { _language.value = l }
    fun setQuestionCount(c: Int) { _questionCount.value = c }
    fun setDifficulty(d: String) { _difficulty.value = d }
    fun setQuestionType(qt: String) { _questionType.value = qt }
    fun setCustomInstruction(ci: String) { _customInstruction.value = ci }

    fun generateWorksheet() {
        if (_topic.value.isBlank()) {
            _statusMessage.value = "Please enter a topic for the worksheet."
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _statusMessage.value = null
            try {
                val qList = aiService.generateWorksheetQuestions(
                    grade = _grade.value,
                    subject = _subject.value,
                    topic = _topic.value,
                    language = _language.value,
                    questionCount = _questionCount.value,
                    difficulty = _difficulty.value,
                    questionType = _questionType.value,
                    customInstruction = _customInstruction.value
                )
                _questions.value = qList
                _statusMessage.value = "Generated ${qList.size} worksheet questions!"
            } catch (e: Exception) {
                _statusMessage.value = "Failed to generate worksheet. Please try again."
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun addCustomQuestion(prompt: String, expectedAnswer: String, type: String = "FILL_IN_BLANKS") {
        val newQ = WorksheetQuestion(
            id = UUID.randomUUID().toString(),
            questionNumber = _questions.value.size + 1,
            questionType = type,
            prompt = prompt,
            options = if (type == "MCQ") listOf(expectedAnswer, "Option B", "Option C", "Option D") else emptyList(),
            expectedAnswer = expectedAnswer,
            points = 1
        )
        _questions.value = _questions.value + newQ
    }

    fun deleteQuestion(questionId: String) {
        val updated = _questions.value.filter { it.id != questionId }
            .mapIndexed { index, q -> q.copy(questionNumber = index + 1) }
        _questions.value = updated
    }

    fun saveWorksheet() {
        if (_questions.value.isEmpty()) {
            _statusMessage.value = "Generate or add questions before saving."
            return
        }

        viewModelScope.launch {
            val serialized = serializeQuestions(_questions.value)
            val item = WorksheetItem(
                title = "${_topic.value} (${_grade.value} - ${_subject.value})",
                grade = _grade.value,
                subject = _subject.value,
                topic = _topic.value,
                language = _language.value.id,
                difficulty = _difficulty.value,
                questionsJson = serialized,
                questionCount = _questions.value.size
            )
            repository.saveWorksheet(item)
            _statusMessage.value = "Worksheet saved to Materials"
        }
    }

    fun deleteSavedWorksheet(item: WorksheetItem) {
        viewModelScope.launch {
            repository.deleteWorksheet(item)
            _statusMessage.value = "Worksheet removed"
        }
    }

    fun loadSavedWorksheet(item: WorksheetItem) {
        _grade.value = item.grade
        _subject.value = item.subject
        _topic.value = item.topic
        _language.value = Language.fromId(item.language)
        _difficulty.value = item.difficulty
        _questions.value = deserializeQuestions(item.questionsJson)
    }

    private fun serializeQuestions(list: List<WorksheetQuestion>): String {
        val arr = JSONArray()
        for (q in list) {
            val obj = JSONObject().apply {
                put("id", q.id)
                put("questionNumber", q.questionNumber)
                put("questionType", q.questionType)
                put("prompt", q.prompt)
                put("expectedAnswer", q.expectedAnswer)
                put("points", q.points)
                val optArr = JSONArray()
                q.options.forEach { optArr.put(it) }
                put("options", optArr)
            }
            arr.put(obj)
        }
        return arr.toString()
    }

    private fun deserializeQuestions(json: String): List<WorksheetQuestion> {
        if (json.isBlank()) return emptyList()
        val list = mutableListOf<WorksheetQuestion>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val optList = mutableListOf<String>()
                val optArr = obj.optJSONArray("options")
                if (optArr != null) {
                    for (j in 0 until optArr.length()) {
                        optList.add(optArr.getString(j))
                    }
                }
                list.add(
                    WorksheetQuestion(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        questionNumber = obj.optInt("questionNumber", i + 1),
                        questionType = obj.optString("questionType", "MCQ"),
                        prompt = obj.optString("prompt", ""),
                        options = optList,
                        expectedAnswer = obj.optString("expectedAnswer", ""),
                        points = obj.optInt("points", 1)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }
}
