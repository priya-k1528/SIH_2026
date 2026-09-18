package com.example.services

import com.example.data.model.QuestionEvaluation
import com.example.data.model.QuizEvaluationResult
import com.example.data.model.QuizQuestion
import com.example.data.model.StudentAnswer

class QuizGradingService {

    fun evaluateQuiz(
        questions: List<QuizQuestion>,
        studentAnswers: Map<String, StudentAnswer>
    ): QuizEvaluationResult {
        var correctCount = 0
        var wrongCount = 0
        var unansweredCount = 0
        var totalMarks = 0
        var earnedMarks = 0

        val evaluations = mutableListOf<QuestionEvaluation>()

        for (q in questions) {
            totalMarks += q.marks
            val studentAns = studentAnswers[q.id]?.answeredText?.trim() ?: ""

            if (studentAns.isBlank()) {
                unansweredCount++
                evaluations.add(
                    QuestionEvaluation(
                        questionId = q.id,
                        questionText = q.questionText,
                        studentAnswer = "Not Answered",
                        correctAnswer = q.correctAnswer,
                        isCorrect = false,
                        isUnanswered = true,
                        marksEarned = 0,
                        explanation = q.explanation
                    )
                )
            } else {
                val isCorrect = compareAnswers(studentAns, q.correctAnswer)
                if (isCorrect) {
                    correctCount++
                    earnedMarks += q.marks
                } else {
                    wrongCount++
                }

                evaluations.add(
                    QuestionEvaluation(
                        questionId = q.id,
                        questionText = q.questionText,
                        studentAnswer = studentAns,
                        correctAnswer = q.correctAnswer,
                        isCorrect = isCorrect,
                        isUnanswered = false,
                        marksEarned = if (isCorrect) q.marks else 0,
                        explanation = q.explanation
                    )
                )
            }
        }

        val percentage = if (totalMarks > 0) {
            (earnedMarks.toDouble() / totalMarks.toDouble()) * 100.0
        } else 0.0

        val passed = percentage >= 50.0

        return QuizEvaluationResult(
            totalQuestions = questions.size,
            correctCount = correctCount,
            wrongCount = wrongCount,
            unansweredCount = unansweredCount,
            totalMarks = totalMarks,
            earnedMarks = earnedMarks,
            percentage = percentage,
            passed = passed,
            questionResults = evaluations
        )
    }

    private fun compareAnswers(student: String, correct: String): Boolean {
        val s = normalize(student)
        val c = normalize(correct)

        if (s == c) return true

        // Check if student string contains correct token or vice versa
        if (s.contains(c) || c.contains(s)) return true

        // Normalize numerals (e.g. 1 vs one vs एक)
        val numeralMap = mapOf(
            "1" to listOf("one", "ek", "एक", "mit'", "miyad"),
            "2" to listOf("two", "do", "दो", "bar", "bariya"),
            "3" to listOf("three", "teen", "तीन", "pe", "apiya"),
            "4" to listOf("four", "chaar", "चार", "pun", "upuniya"),
            "5" to listOf("five", "paanch", "पांच", "more", "modeya")
        )

        for ((digit, aliases) in numeralMap) {
            if ((s == digit || aliases.contains(s)) && (c == digit || aliases.contains(c))) {
                return true
            }
        }

        return false
    }

    private fun normalize(text: String): String {
        return text.trim()
            .lowercase()
            .replace("[.,!?;:()'\"]".toRegex(), "")
            .replace("\\s+".toRegex(), " ")
    }
}
