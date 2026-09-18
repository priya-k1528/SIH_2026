package com.example.data.model

data class WorksheetQuestion(
    val id: String,
    val questionNumber: Int,
    val questionType: String, // MCQ, FILL_IN_BLANKS, MATCH, PICTURE_ID, COUNTING, TRANSLATION, TRUE_FALSE, SHORT_ANSWER
    val prompt: String,
    val options: List<String> = emptyList(),
    val matchPairs: List<PairItem> = emptyList(),
    val imageOrIconHint: String = "",
    val expectedAnswer: String = "",
    val points: Int = 1
)

data class PairItem(
    val left: String,
    val right: String
)
