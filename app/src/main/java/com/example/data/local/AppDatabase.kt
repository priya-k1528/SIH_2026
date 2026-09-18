package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dictionary.VernacularDictionary
import com.example.data.model.AppSettingEntity
import com.example.data.model.EducationalContext
import com.example.data.model.FlashcardItem
import com.example.data.model.LessonItem
import com.example.data.model.QuizItem
import com.example.data.model.TranslationItem
import com.example.data.model.WorksheetItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TranslationItem::class,
        LessonItem::class,
        WorksheetItem::class,
        QuizItem::class,
        FlashcardItem::class,
        EducationalContext::class,
        AppSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun translationDao(): TranslationDao
    abstract fun lessonDao(): LessonDao
    abstract fun worksheetDao(): WorksheetDao
    abstract fun quizDao(): QuizDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun educationalContextDao(): EducationalContextDao
    abstract fun appSettingDao(): AppSettingDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vernacular_pedagogy_db"
                )
                    .addCallback(
                        DatabaseCallback(context.applicationContext)
                    )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                INSTANCE?.let { database ->

                    CoroutineScope(Dispatchers.IO).launch {
                        importMundariDataset(database)
                        seedFlashcards(database)
                    }
                }
            }

            private suspend fun seedFlashcards(
                database: AppDatabase
            ) {

                try {

                    val flashcardDao =
                        database.flashcardDao()

                    val existingCount =
                        flashcardDao.getCount()

                    if (existingCount > 0) {
                        return
                    }

                    val seedCards =
                        VernacularDictionary.entries.map { entry ->
                            FlashcardItem(
                                category = entry.category,
                                english = entry.english,
                                hindi = entry.hindi,
                                santhaliOlChiki = entry.santhaliOlChiki,
                                santhaliRoman = entry.santhaliRoman,
                                mundariDevanagari = entry.mundariDevanagari,
                                mundariRoman = entry.mundariRoman,
                                pronunciationGuide = entry.pronunciationNote,
                                iconEmoji = entry.emoji,
                                isCustom = false
                            )
                        }

                    if (seedCards.isNotEmpty()) {
                        flashcardDao.insertAll(seedCards)
                    }

                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }

            private suspend fun importMundariDataset(
                database: AppDatabase
            ) {

                try {

                    val translationDao =
                        database.translationDao()

                    val existingCount =
                        translationDao.getCount()

                    if (existingCount > 0) {
                        return
                    }

                    val translations =
                        mutableListOf<TranslationItem>()

                    context.assets
                        .open("mundari_clean.csv")
                        .bufferedReader()
                        .useLines { lines ->

                            var firstLine = true

                            for (line in lines) {

                                if (firstLine) {
                                    firstLine = false
                                    continue
                                }

                                if (line.isBlank()) {
                                    continue
                                }

                                val columns =
                                    parseCsvLine(line)

                                if (columns.size < 2) {
                                    continue
                                }

                                val hindi =
                                    columns[0].trim()

                                val mundari =
                                    columns[1].trim()

                                if (
                                    hindi.isBlank() ||
                                    mundari.isBlank()
                                ) {
                                    continue
                                }

                                translations.add(
                                    TranslationItem(
                                        sourceText = hindi,
                                        sourceLanguage = "hi",
                                        targetLanguage = "mun",
                                        translatedText = mundari,
                                        englishText = "",
                                        hindiText = hindi,
                                        santhaliOlChiki = "",
                                        santhaliRoman = "",
                                        mundariDevanagari = mundari,
                                        mundariRoman = "",
                                        pronunciationGuide = "",
                                        translationSource =
                                            "Offline Hindi-Mundari Dataset",
                                        timestamp = 0L,
                                        contextTopic = "",
                                        isFavorite = false
                                    )
                                )
                            }
                        }

                    if (translations.isNotEmpty()) {

                        translationDao.insertAll(
                            translations
                        )
                    }

                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }

            private fun parseCsvLine(
                line: String
            ): List<String> {

                val result =
                    mutableListOf<String>()

                val current =
                    StringBuilder()

                var insideQuotes = false
                var i = 0

                while (i < line.length) {

                    val char = line[i]

                    when {

                        char == '"' -> {

                            if (
                                insideQuotes &&
                                i + 1 < line.length &&
                                line[i + 1] == '"'
                            ) {
                                current.append('"')
                                i++
                            } else {
                                insideQuotes =
                                    !insideQuotes
                            }
                        }

                        char == ',' &&
                            !insideQuotes -> {

                            result.add(
                                current.toString()
                            )

                            current.clear()
                        }

                        else -> {
                            current.append(char)
                        }
                    }

                    i++
                }

                result.add(
                    current.toString()
                )

                return result
            }
        }
    }
}