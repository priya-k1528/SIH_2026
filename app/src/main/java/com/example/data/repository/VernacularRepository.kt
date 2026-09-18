package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.AppSettingEntity
import com.example.data.model.EducationalContext
import com.example.data.model.FlashcardItem
import com.example.data.model.LessonItem
import com.example.data.model.QuizItem
import com.example.data.model.TranslationItem
import com.example.data.model.WorksheetItem
import kotlinx.coroutines.flow.Flow

class VernacularRepository(private val database: AppDatabase) {

    // Translations
    fun getAllTranslations(): Flow<List<TranslationItem>> = database.translationDao().getAll()
    fun getFavoriteTranslations(): Flow<List<TranslationItem>> = database.translationDao().getFavorites()
    suspend fun saveTranslation(item: TranslationItem): Long = database.translationDao().insert(item)
    suspend fun deleteTranslation(item: TranslationItem) = database.translationDao().delete(item)
    suspend fun deleteTranslationById(id: Long) = database.translationDao().deleteById(id)
    suspend fun clearAllTranslations() = database.translationDao().clearAll()

    // Lessons
    fun getAllLessons(): Flow<List<LessonItem>> = database.lessonDao().getAll()
    suspend fun getLessonById(id: Long): LessonItem? = database.lessonDao().getById(id)
    suspend fun saveLesson(lesson: LessonItem): Long = database.lessonDao().insert(lesson)
    suspend fun updateLesson(lesson: LessonItem) = database.lessonDao().update(lesson)
    suspend fun deleteLesson(lesson: LessonItem) = database.lessonDao().delete(lesson)
    suspend fun deleteLessonById(id: Long) = database.lessonDao().deleteById(id)

    // Worksheets
    fun getAllWorksheets(): Flow<List<WorksheetItem>> = database.worksheetDao().getAll()
    suspend fun getWorksheetById(id: Long): WorksheetItem? = database.worksheetDao().getById(id)
    suspend fun saveWorksheet(item: WorksheetItem): Long = database.worksheetDao().insert(item)
    suspend fun updateWorksheet(item: WorksheetItem) = database.worksheetDao().update(item)
    suspend fun deleteWorksheet(item: WorksheetItem) = database.worksheetDao().delete(item)
    suspend fun deleteWorksheetById(id: Long) = database.worksheetDao().deleteById(id)

    // Quizzes
    fun getAllQuizzes(): Flow<List<QuizItem>> = database.quizDao().getAll()
    suspend fun getQuizById(id: Long): QuizItem? = database.quizDao().getById(id)
    suspend fun saveQuiz(item: QuizItem): Long = database.quizDao().insert(item)
    suspend fun updateQuiz(item: QuizItem) = database.quizDao().update(item)
    suspend fun deleteQuiz(item: QuizItem) = database.quizDao().delete(item)
    suspend fun deleteQuizById(id: Long) = database.quizDao().deleteById(id)

    // Flashcards
    fun getAllFlashcards(): Flow<List<FlashcardItem>> = database.flashcardDao().getAll()
    fun getFlashcardsByCategory(category: String): Flow<List<FlashcardItem>> = database.flashcardDao().getByCategory(category)
    suspend fun saveFlashcard(item: FlashcardItem): Long = database.flashcardDao().insert(item)
    suspend fun saveFlashcards(items: List<FlashcardItem>) = database.flashcardDao().insertAll(items)
    suspend fun updateFlashcard(item: FlashcardItem) = database.flashcardDao().update(item)
    suspend fun deleteFlashcard(item: FlashcardItem) = database.flashcardDao().delete(item)
    suspend fun deleteFlashcardById(id: Long) = database.flashcardDao().deleteById(id)

    // Educational Contexts
    fun getAllEducationalContexts(): Flow<List<EducationalContext>> = database.educationalContextDao().getAll()
    fun getDefaultEducationalContext(): Flow<EducationalContext?> = database.educationalContextDao().getDefaultContext()
    suspend fun saveEducationalContext(context: EducationalContext): Long = database.educationalContextDao().insert(context)
    suspend fun updateEducationalContext(context: EducationalContext) = database.educationalContextDao().update(context)
    suspend fun deleteEducationalContext(context: EducationalContext) = database.educationalContextDao().delete(context)
    suspend fun deleteEducationalContextById(id: Long) = database.educationalContextDao().deleteById(id)
    suspend fun setDefaultEducationalContext(id: Long) {
        database.educationalContextDao().clearDefault()
        database.educationalContextDao().setDefault(id)
    }

    // App Settings
    suspend fun getSetting(key: String): String? = database.appSettingDao().getValue(key)
    suspend fun setSetting(key: String, value: String) = database.appSettingDao().setValue(AppSettingEntity(key, value))
}
