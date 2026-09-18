package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Language
import com.example.data.model.QuizEvaluationResult
import com.example.data.model.QuizItem
import com.example.data.model.QuizQuestion
import com.example.data.model.StudentAnswer
import com.example.data.repository.VernacularRepository
import com.example.services.AiContentService
import com.example.services.QuizGradingService
import com.example.services.SpeechRecognitionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VernacularRepository(AppDatabase.getDatabase(application))
    private val aiService = AiContentService()
    private val gradingService = QuizGradingService()
    val speechService = SpeechRecognitionService(application)

    val savedQuizzes: StateFlow<List<QuizItem>> = repository.getAllQuizzes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Generation / Builder State
    private val _grade = MutableStateFlow("Grade 1")
    val grade: StateFlow<String> = _grade.asStateFlow()

    private val _subject = MutableStateFlow("Mathematics")
    val subject: StateFlow<String> = _subject.asStateFlow()

    private val _topic = MutableStateFlow("Counting 1 to 5")
    val topic: StateFlow<String> = _topic.asStateFlow()

    private val _language = MutableStateFlow(Language.HINDI)
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _difficulty = MutableStateFlow("Medium")
    val difficulty: StateFlow<String> = _difficulty.asStateFlow()

    private val _questionCount = MutableStateFlow(5)
    val questionCount: StateFlow<Int> = _questionCount.asStateFlow()

    private val _customInstruction = MutableStateFlow("")
    val customInstruction: StateFlow<String> = _customInstruction.asStateFlow()

    // Active Quiz State
    private val _activeQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val activeQuestions: StateFlow<List<QuizQuestion>> = _activeQuestions.asStateFlow()

    private val _studentAnswers = MutableStateFlow<Map<String, StudentAnswer>>(emptyMap())
    val studentAnswers: StateFlow<Map<String, StudentAnswer>> = _studentAnswers.asStateFlow()

    private val _quizEvaluation = MutableStateFlow<QuizEvaluationResult?>(null)
    val quizEvaluation: StateFlow<QuizEvaluationResult?> = _quizEvaluation.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _activeListeningQuestionId = MutableStateFlow<String?>(null)
    val activeListeningQuestionId: StateFlow<String?> = _activeListeningQuestionId.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun setGrade(g: String) { _grade.value = g }
    fun setSubject(s: String) { _subject.value = s }
    fun setTopic(t: String) { _topic.value = t }
    fun setLanguage(l: Language) { _language.value = l }
    fun setDifficulty(d: String) { _difficulty.value = d }
    fun setQuestionCount(c: Int) { _questionCount.value = c }
    fun setCustomInstruction(ci: String) { _customInstruction.value = ci }

    fun generateQuiz() {
        if (_topic.value.isBlank()) {
            _statusMessage.value = "Please enter a quiz topic."
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _statusMessage.value = null
            _quizEvaluation.value = null
            _studentAnswers.value = emptyMap()
            try {
                val qList = aiService.generateQuizQuestions(
                    grade = _grade.value,
                    subject = _subject.value,
                    topic = _topic.value,
                    language = _language.value,
                    questionCount = _questionCount.value,
                    difficulty = _difficulty.value,
                    customInstruction = _customInstruction.value
                )
                _activeQuestions.value = qList
                _statusMessage.value = "Quiz created with ${qList.size} questions!"
            } catch (e: Exception) {
                _statusMessage.value = "Failed to create quiz. Please try again."
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun addTeacherQuestion(text: String, correctAnswer: String, type: String, options: List<String>) {
        val q = QuizQuestion(
            id = UUID.randomUUID().toString(),
            questionText = text,
            questionType = type,
            options = options,
            correctAnswer = correctAnswer,
            explanation = "Correct answer is $correctAnswer",
            marks = 1
        )
        _activeQuestions.value = _activeQuestions.value + q
    }

    fun setStudentAnswer(questionId: String, answerText: String, isVoice: Boolean = false) {
        val updated = _studentAnswers.value.toMutableMap()
        updated[questionId] = StudentAnswer(questionId, answerText, isVoice)
        _studentAnswers.value = updated
    }

    fun startVoiceAnswering(questionId: String) {
        _activeListeningQuestionId.value = questionId
        speechService.startListening(
            language = Language.HINDI,
            onResult = { recognized ->
                setStudentAnswer(questionId, recognized, isVoice = true)
                _activeListeningQuestionId.value = null
            },
            onError = { err ->
                _statusMessage.value = err
                _activeListeningQuestionId.value = null
            }
        )
    }

    fun stopVoiceAnswering() {
        speechService.stopListening()
        _activeListeningQuestionId.value = null
    }

    fun submitQuiz() {
        val questions = _activeQuestions.value
        if (questions.isEmpty()) {
            _statusMessage.value = "No active quiz to submit."
            return
        }

        val result = gradingService.evaluateQuiz(questions, _studentAnswers.value)
        _quizEvaluation.value = result
        _statusMessage.value = "Quiz graded: ${result.earnedMarks} / ${result.totalMarks} (${result.percentage.toInt()}%)"
    }

    fun saveQuiz() {
        if (_activeQuestions.value.isEmpty()) return
        viewModelScope.launch {
            val serialized = serializeQuestions(_activeQuestions.value)
            val item = QuizItem(
                title = "${_topic.value} Quiz (${_grade.value})",
                grade = _grade.value,
                subject = _subject.value,
                topic = _topic.value,
                language = _language.value.id,
                difficulty = _difficulty.value,
                questionsJson = serialized,
                totalMarks = _activeQuestions.value.sumOf { it.marks },
                lastScore = _quizEvaluation.value?.earnedMarks ?: -1,
                lastAttemptTimestamp = System.currentTimeMillis()
            )
            repository.saveQuiz(item)
            _statusMessage.value = "Quiz saved to Materials"
        }
    }

    fun loadSavedQuiz(quiz: QuizItem) {
        _grade.value = quiz.grade
        _subject.value = quiz.subject
        _topic.value = quiz.topic
        _language.value = Language.fromId(quiz.language)
        _difficulty.value = quiz.difficulty
        _activeQuestions.value = deserializeQuestions(quiz.questionsJson)
        _studentAnswers.value = emptyMap()
        _quizEvaluation.value = null
    }

    fun deleteSavedQuiz(quiz: QuizItem) {
        viewModelScope.launch {
            repository.deleteQuiz(quiz)
            _statusMessage.value = "Quiz removed"
        }
    }

    fun resetQuiz() {
        _studentAnswers.value = emptyMap()
        _quizEvaluation.value = null
    }

    private fun serializeQuestions(list: List<QuizQuestion>): String {
        val arr = JSONArray()
        for (q in list) {
            val obj = JSONObject().apply {
                put("id", q.id)
                put("questionText", q.questionText)
                put("questionType", q.questionType)
                put("correctAnswer", q.correctAnswer)
                put("explanation", q.explanation)
                put("marks", q.marks)
                val optArr = JSONArray()
                q.options.forEach { optArr.put(it) }
                put("options", optArr)
            }
            arr.put(obj)
        }
        return arr.toString()
    }

    private fun deserializeQuestions(json: String): List<QuizQuestion> {
        if (json.isBlank()) return emptyList()
        val list = mutableListOf<QuizQuestion>()
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
                    QuizQuestion(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        questionText = obj.optString("questionText", ""),
                        questionType = obj.optString("questionType", "MCQ"),
                        options = optList,
                        correctAnswer = obj.optString("correctAnswer", ""),
                        explanation = obj.optString("explanation", ""),
                        marks = obj.optInt("marks", 1)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    override fun onCleared() {
        super.onCleared()
        speechService.destroyRecognizer()
    }
}
