package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppSettingEntity
import com.example.data.model.EducationalContext
import com.example.data.model.FlashcardItem
import com.example.data.model.LessonItem
import com.example.data.model.QuizItem
import com.example.data.model.TranslationItem
import com.example.data.model.WorksheetItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDao {

    @Query("SELECT * FROM translations ORDER BY timestamp DESC")
    fun getAll(): Flow<List<TranslationItem>>

    @Query("SELECT * FROM translations WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<TranslationItem>>

    @Query("""
        SELECT * FROM translations
        WHERE sourceText = :text
        AND sourceLanguage = :sourceLanguage
        AND targetLanguage = :targetLanguage
        LIMIT 1
    """)
    suspend fun findExactTranslation(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): TranslationItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TranslationItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TranslationItem>)

    @Delete
    suspend fun delete(item: TranslationItem)

    @Query("DELETE FROM translations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM translations")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM translations")
    suspend fun getCount(): Int
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY timestamp DESC")
    fun getAll(): Flow<List<LessonItem>>

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getById(id: Long): LessonItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lesson: LessonItem): Long

    @Update
    suspend fun update(lesson: LessonItem)

    @Delete
    suspend fun delete(lesson: LessonItem)

    @Query("DELETE FROM lessons WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface WorksheetDao {
    @Query("SELECT * FROM worksheets ORDER BY timestamp DESC")
    fun getAll(): Flow<List<WorksheetItem>>

    @Query("SELECT * FROM worksheets WHERE id = :id")
    suspend fun getById(id: Long): WorksheetItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WorksheetItem): Long

    @Update
    suspend fun update(item: WorksheetItem)

    @Delete
    suspend fun delete(item: WorksheetItem)

    @Query("DELETE FROM worksheets WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes ORDER BY timestamp DESC")
    fun getAll(): Flow<List<QuizItem>>

    @Query("SELECT * FROM quizzes WHERE id = :id")
    suspend fun getById(id: Long): QuizItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: QuizItem): Long

    @Update
    suspend fun update(item: QuizItem)

    @Delete
    suspend fun delete(item: QuizItem)

    @Query("DELETE FROM quizzes WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards ORDER BY category, id")
    fun getAll(): Flow<List<FlashcardItem>>

    @Query("SELECT * FROM flashcards WHERE category = :category ORDER BY id")
    fun getByCategory(category: String): Flow<List<FlashcardItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FlashcardItem): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<FlashcardItem>)

    @Update
    suspend fun update(item: FlashcardItem)

    @Delete
    suspend fun delete(item: FlashcardItem)

    @Query("DELETE FROM flashcards WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM flashcards")
    suspend fun getCount(): Int
}

@Dao
interface EducationalContextDao {
    @Query("SELECT * FROM educational_contexts ORDER BY isDefault DESC, timestamp DESC")
    fun getAll(): Flow<List<EducationalContext>>

    @Query("SELECT * FROM educational_contexts WHERE isDefault = 1 LIMIT 1")
    fun getDefaultContext(): Flow<EducationalContext?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(context: EducationalContext): Long

    @Update
    suspend fun update(context: EducationalContext)

    @Delete
    suspend fun delete(context: EducationalContext)

    @Query("DELETE FROM educational_contexts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE educational_contexts SET isDefault = 0")
    suspend fun clearDefault()

    @Query("UPDATE educational_contexts SET isDefault = 1 WHERE id = :id")
    suspend fun setDefault(id: Long)

    @Query("SELECT COUNT(*) FROM educational_contexts")
    suspend fun getCount(): Int
}

@Dao
interface AppSettingDao {
    @Query("SELECT value FROM app_settings WHERE `key` = :key")
    suspend fun getValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setValue(setting: AppSettingEntity)
}
