package com.example.data.model

data class QuizQuestion(
    val id: String,
    val questionText: String,
    val questionType: String = "MCQ", // MCQ, FILL_IN_BLANKS, TRUE_FALSE, SHORT_ANSWER
    val options: List<String> = emptyList(),
    val correctAnswer: String,
    val explanation: String = "",
    val marks: Int = 1
)

data class StudentAnswer(
    val questionId: String,
    val answeredText: String,
    val isVoiceInput: Boolean = false
)

data class QuizEvaluationResult(
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val totalMarks: Int,
    val earnedMarks: Int,
    val percentage: Double,
    val passed: Boolean,
    val questionResults: List<QuestionEvaluation>
)

data class QuestionEvaluation(
    val questionId: String,
    val questionText: String,
    val studentAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
    val isUnanswered: Boolean,
    val marksEarned: Int,
    val explanation: String
)
