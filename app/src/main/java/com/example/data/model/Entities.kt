package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translations")
data class TranslationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val translatedText: String,
    val englishText: String = "",
    val hindiText: String = "",
    val santhaliOlChiki: String = "",
    val santhaliRoman: String = "",
    val mundariDevanagari: String = "",
    val mundariRoman: String = "",
    val pronunciationGuide: String = "",
    val translationSource: String = "Offline Vernacular Engine", // or "Gemini AI"
    val timestamp: Long = System.currentTimeMillis(),
    val contextTopic: String = "",
    val isFavorite: Boolean = false
)

@Entity(tableName = "lessons")
data class LessonItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val grade: String,
    val subject: String,
    val topic: String,
    val difficulty: String = "Medium",
    val primaryLanguage: String = "hi",
    val learningObjectives: String = "",
    val teacherScript: String = "",
    val studentActivities: String = "",
    val vocabularyMarkdown: String = "",
    val fullContentMarkdown: String = "",
    val englishContent: String = "",
    val hindiContent: String = "",
    val santhaliContent: String = "",
    val mundariContent: String = "",
    val isMultilingual: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "worksheets")
data class WorksheetItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val grade: String,
    val subject: String,
    val topic: String,
    val language: String = "hi",
    val difficulty: String = "Easy",
    val questionsJson: String = "", // serialized list of WorksheetQuestion
    val instructions: String = "",
    val questionCount: Int = 5,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quizzes")
data class QuizItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val grade: String,
    val subject: String,
    val topic: String,
    val language: String = "hi",
    val difficulty: String = "Medium",
    val questionsJson: String = "", // serialized list of QuizQuestion
    val totalMarks: Int = 10,
    val isAiGenerated: Boolean = false,
    val lastScore: Int = -1,
    val lastAttemptTimestamp: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "flashcards")
data class FlashcardItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // Numbers, Colours, Animals, Fruits, Shapes, Classroom, Objects
    val english: String,
    val hindi: String,
    val santhaliOlChiki: String,
    val santhaliRoman: String,
    val mundariDevanagari: String,
    val mundariRoman: String,
    val pronunciationGuide: String = "",
    val iconEmoji: String = "📚",
    val isCustom: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "educational_contexts")
data class EducationalContext(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val grade: String,
    val subject: String,
    val topic: String,
    val customInstructions: String = "",
    val isDefault: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingEntity(
    @PrimaryKey val key: String,
    val value: String
)
