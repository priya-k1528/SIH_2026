package com.example.services

import com.example.BuildConfig
import com.example.data.dictionary.VernacularDictionary
import com.example.data.dictionary.VernacularEntry
import com.example.data.model.FlashcardItem
import com.example.data.model.Language
import com.example.data.model.LessonItem
import com.example.data.model.PairItem
import com.example.data.model.QuizQuestion
import com.example.data.model.WorksheetQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

class AiContentService {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun generateLesson(
        grade: String,
        subject: String,
        topic: String,
        difficulty: String,
        language: Language,
        customInstruction: String,
        generateAllLanguages: Boolean
    ): LessonItem = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val geminiLesson = callGeminiLesson(grade, subject, topic, difficulty, language, customInstruction, generateAllLanguages, apiKey)
                if (geminiLesson != null) {
                    return@withContext geminiLesson
                }
            } catch (_: Exception) {
                // Graceful fallback to offline pedagogical lesson builder
            }
        }

        buildOfflinePedagogicalLesson(grade, subject, topic, difficulty, language, customInstruction, generateAllLanguages)
    }

    suspend fun generateWorksheetQuestions(
        grade: String,
        subject: String,
        topic: String,
        language: Language,
        questionCount: Int,
        difficulty: String,
        questionType: String,
        customInstruction: String
    ): List<WorksheetQuestion> = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val geminiQuestions = callGeminiWorksheet(grade, subject, topic, language, questionCount, difficulty, questionType, customInstruction, apiKey)
                if (geminiQuestions.isNotEmpty()) {
                    return@withContext geminiQuestions
                }
            } catch (_: Exception) {
                // Fallback to offline generator
            }
        }

        buildOfflineWorksheetQuestions(grade, subject, topic, language, questionCount, difficulty, questionType)
    }

    suspend fun generateQuizQuestions(
        grade: String,
        subject: String,
        topic: String,
        language: Language,
        questionCount: Int,
        difficulty: String,
        customInstruction: String
    ): List<QuizQuestion> = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val geminiQuiz = callGeminiQuiz(grade, subject, topic, language, questionCount, difficulty, customInstruction, apiKey)
                if (geminiQuiz.isNotEmpty()) {
                    return@withContext geminiQuiz
                }
            } catch (_: Exception) {
                // Fallback to offline generator
            }
        }

        buildOfflineQuizQuestions(grade, subject, topic, language, questionCount)
    }

    suspend fun generateFlashcards(
        topic: String,
        category: String,
        count: Int,
        customInstruction: String = ""
    ): List<FlashcardItem> = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val geminiCards = callGeminiFlashcards(topic, category, count, customInstruction, apiKey)
                if (geminiCards.isNotEmpty()) {
                    return@withContext geminiCards
                }
            } catch (_: Exception) {
                // Graceful fallback to offline vocabulary builder
            }
        }

        buildOfflineFlashcards(topic, category, count)
    }

    // === OFFLINE PEDAGOGICAL GENERATORS ===

    private fun buildOfflinePedagogicalLesson(
        grade: String,
        subject: String,
        topic: String,
        difficulty: String,
        language: Language,
        customInstruction: String,
        generateAllLanguages: Boolean
    ): LessonItem {
        val dictMatches = VernacularDictionary.searchMatches(topic).take(4)
        val vocabList = if (dictMatches.isNotEmpty()) dictMatches else VernacularDictionary.entries.take(4)

        val vocabMarkdown = buildString {
            vocabList.forEach { v ->
                appendLine("- **${v.english}** | ${v.hindi} | ${v.santhaliOlChiki} (${v.santhaliRoman}) | ${v.mundariDevanagari} (${v.mundariRoman})")
            }
        }

        val objectives = """
            1. Introduce core foundational concepts of '$topic' for $grade students.
            2. Build bilingual connection between mother tongue (Santhali/Mundari) and Hindi/English.
            3. Encourage active student participation through visual and oral repetition.
        """.trimIndent()

        val teacherScript = """
            Teacher: "बच्चों, आज हम '$topic' के बारे में सीखेंगे।"
            (Children, today we will learn about '$topic'.)
            
            Teacher (in Santhali): "ᱛᱮᱦᱮᱧ ᱫᱚ ᱟᱵᱚ '$topic' ᱵᱟᱵᱚᱛ ᱵᱚᱱ ᱪᱮᱫᱚᱜᱼᱟ᱾"
            (Tehenj do abo '$topic' babat bon chedoka.)
            
            Teacher (in Mundari): "तिसिंग आबु '$topic' बाबत सेचेदबुआ।"
            (Tising aabu '$topic' baabat sechedbua.)
        """.trimIndent()

        val studentActivities = """
            1. **Object Identification**: Point to classroom objects or flashcards and name them in Santhali or Mundari.
            2. **Call and Response**: Teacher calls out in Hindi, students repeat together in their mother tongue.
            3. **Drawing and Labeling**: Students draw a picture related to '$topic' in their notebooks and write words in both languages.
        """.trimIndent()

        val fullMarkdown = """
            # $topic ($grade - $subject)
            
            ### 🎯 Learning Objectives
            $objectives
            
            ### 🗣️ Bilingual Teacher Script
            $teacherScript
            
            ### 📖 Key Multilingual Vocabulary
            $vocabMarkdown
            
            ### 🎨 Interactive Classroom Activities
            $studentActivities
            
            ---
            *Created with Jharkhand Vernacular Pedagogy Assistant (SIH26042)*
        """.trimIndent()

        val englishContent = "Lesson on $topic for $grade. Students learn core terminology and interactive oral activities bridging tribal mother tongue and state curriculum."
        val hindiContent = "$grade के लिए '$topic' पर पाठ। छात्र आदिवासी मातृभाषा और राज्य पाठ्यक्रम को जोड़ते हुए शब्दावली और गतिविधियां सीखते हैं।"
        val santhaliContent = "$grade ᱨᱮᱱ ᱯᱟᱹᱴᱷᱩᱣᱟᱹ ᱠᱚ ᱞᱟᱹᱜᱤᱫ '$topic' ᱯᱟᱲᱦᱟᱣ᱾ ᱟᱭᱳ ᱟᱲᱟᱝ ᱥᱟᱶᱛᱮ ᱦᱤᱱᱫᱤ ᱟᱨ ᱤᱝᱨᱟᱹᱡᱤ ᱪᱮᱫᱚᱜ ᱨᱮᱱᱟᱜ ᱵᱮᱵᱚᱥᱛᱟ᱾"
        val mundariContent = "$grade कोआः लेकाते '$topic' पुथी सेचेद। एंगा आड़ांग आर हिन्दी-अंग्रेजी जोड़के सेचेद क्रिया।"

        return LessonItem(
            title = "$topic ($grade - $subject)",
            grade = grade,
            subject = subject,
            topic = topic,
            difficulty = difficulty,
            primaryLanguage = language.id,
            learningObjectives = objectives,
            teacherScript = teacherScript,
            studentActivities = studentActivities,
            vocabularyMarkdown = vocabMarkdown,
            fullContentMarkdown = fullMarkdown,
            englishContent = englishContent,
            hindiContent = hindiContent,
            santhaliContent = santhaliContent,
            mundariContent = mundariContent,
            isMultilingual = generateAllLanguages
        )
    }

    private fun buildOfflineWorksheetQuestions(
        grade: String,
        subject: String,
        topic: String,
        language: Language,
        count: Int,
        difficulty: String,
        questionType: String
    ): List<WorksheetQuestion> {
        val sampleEntries = VernacularDictionary.searchMatches(topic).take(count.coerceAtLeast(3))
        val entries = if (sampleEntries.size >= 3) sampleEntries else VernacularDictionary.entries.take(count.coerceAtLeast(3))

        val list = mutableListOf<WorksheetQuestion>()
        for (i in 0 until count) {
            val entry = entries[i % entries.size]
            val qType = if (questionType == "All" || questionType.isBlank()) {
                when (i % 4) {
                    0 -> "MCQ"
                    1 -> "FILL_IN_BLANKS"
                    2 -> "TRUE_FALSE"
                    else -> "TRANSLATION"
                }
            } else {
                questionType
            }

            val question = when (qType) {
                "FILL_IN_BLANKS" -> WorksheetQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = i + 1,
                    questionType = "FILL_IN_BLANKS",
                    prompt = "The Santhali word for '${entry.hindi}' is ______.",
                    expectedAnswer = entry.santhaliRoman,
                    points = 1
                )
                "TRUE_FALSE" -> WorksheetQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = i + 1,
                    questionType = "TRUE_FALSE",
                    prompt = "In Mundari, '${entry.hindi}' is called '${entry.mundariRoman}'. (True or False)",
                    options = listOf("True", "False"),
                    expectedAnswer = "True",
                    points = 1
                )
                "TRANSLATION" -> WorksheetQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = i + 1,
                    questionType = "TRANSLATION",
                    prompt = "Translate '${entry.hindi}' into Santhali (Ol Chiki or Roman) or Mundari.",
                    expectedAnswer = "${entry.santhaliOlChiki} / ${entry.mundariRoman}",
                    points = 2
                )
                else -> WorksheetQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = i + 1,
                    questionType = "MCQ",
                    prompt = "What is '${entry.hindi}' in Santhali?",
                    options = listOf(
                        "${entry.santhaliOlChiki} (${entry.santhaliRoman})",
                        "ᱫᱟᱨᱮ (Dare)",
                        "ᱦᱟᱠᱚ (Hako)",
                        "ᱥᱮᱛᱟ (Seta)"
                    ).distinct(),
                    expectedAnswer = "${entry.santhaliOlChiki} (${entry.santhaliRoman})",
                    points = 1
                )
            }
            list.add(question)
        }
        return list
    }

    private fun buildOfflineQuizQuestions(
        grade: String,
        subject: String,
        topic: String,
        language: Language,
        count: Int
    ): List<QuizQuestion> {
        val sampleEntries = VernacularDictionary.searchMatches(topic).take(count.coerceAtLeast(3))
        val entries = if (sampleEntries.size >= 3) sampleEntries else VernacularDictionary.entries.take(count.coerceAtLeast(3))

        val list = mutableListOf<QuizQuestion>()
        for (i in 0 until count) {
            val entry = entries[i % entries.size]
            val isFill = (i % 2 == 1)

            val question = if (isFill) {
                QuizQuestion(
                    id = UUID.randomUUID().toString(),
                    questionText = "The Hindi word '${entry.hindi}' in Santhali is ______.",
                    questionType = "FILL_IN_BLANKS",
                    options = emptyList(),
                    correctAnswer = entry.santhaliRoman,
                    explanation = "'${entry.hindi}' is written as '${entry.santhaliOlChiki}' and pronounced as '${entry.santhaliRoman}' in Santhali.",
                    marks = 1
                )
            } else {
                val correct = "${entry.santhaliOlChiki} (${entry.santhaliRoman})"
                QuizQuestion(
                    id = UUID.randomUUID().toString(),
                    questionText = "Choose the correct Santhali word for '${entry.english} / ${entry.hindi}':",
                    questionType = "MCQ",
                    options = listOf(
                        correct,
                        "ᱫᱟᱨᱮ (Dare)",
                        "ᱢᱤᱫ (Mit')",
                        "ᱦᱟᱠᱚ (Hako)"
                    ).distinct(),
                    correctAnswer = correct,
                    explanation = "'${entry.english}' is '${entry.hindi}' in Hindi and '$correct' in Santhali.",
                    marks = 1
                )
            }
            list.add(question)
        }
        return list
    }

    private fun buildOfflineFlashcards(
        topic: String,
        category: String,
        count: Int
    ): List<FlashcardItem> {
        val requested = count.coerceIn(1, 20)

        val topicMatches = VernacularDictionary.searchMatches(topic)
        val categoryMatches: List<VernacularEntry> = if (category.isNotBlank() && !category.equals("All", ignoreCase = true)) {
            VernacularDictionary.entries.filter { it.category.equals(category, ignoreCase = true) }
        } else {
            emptyList()
        }

        val pool: List<VernacularEntry> = (topicMatches + categoryMatches + VernacularDictionary.entries)
            .distinctBy { it.english }

        val chosen = pool.take(requested)

        return chosen.map { entry ->
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
    }

    // === GEMINI API CALLS ===

    private fun callGeminiLesson(
        grade: String,
        subject: String,
        topic: String,
        difficulty: String,
        language: Language,
        customInstruction: String,
        generateAllLanguages: Boolean,
        apiKey: String
    ): LessonItem? {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        val prompt = """
            Create a comprehensive primary school bilingual lesson plan for Jharkhand teachers (SIH26042).
            Grade: $grade
            Subject: $subject
            Topic: $topic
            Difficulty: $difficulty
            Language: ${language.displayName}
            Custom Instruction: $customInstruction
            Generate All Languages: $generateAllLanguages (English, Hindi, Santhali in Ol Chiki and Roman, Mundari in Devanagari and Roman)
            
            Return STRICTLY valid JSON without code blocks or markdown:
            {
              "title": "Title of lesson",
              "learningObjectives": "Numbered list of objectives",
              "teacherScript": "Teacher dialogue with tribal vernacular greetings and directives",
              "studentActivities": "Classroom participation activities",
              "vocabularyMarkdown": "Table or list of English, Hindi, Santhali, Mundari terms",
              "fullContentMarkdown": "Full structured lesson markdown",
              "englishContent": "English summary",
              "hindiContent": "Hindi summary",
              "santhaliContent": "Santhali summary in Ol Chiki and Roman",
              "mundariContent": "Mundari summary in Devanagari and Roman"
            }
        """.trimIndent()

        val json = postGemini(endpoint, prompt) ?: return null
        return LessonItem(
            title = json.optString("title", "$topic ($grade)"),
            grade = grade,
            subject = subject,
            topic = topic,
            difficulty = difficulty,
            primaryLanguage = language.id,
            learningObjectives = json.optString("learningObjectives", ""),
            teacherScript = json.optString("teacherScript", ""),
            studentActivities = json.optString("studentActivities", ""),
            vocabularyMarkdown = json.optString("vocabularyMarkdown", ""),
            fullContentMarkdown = json.optString("fullContentMarkdown", ""),
            englishContent = json.optString("englishContent", ""),
            hindiContent = json.optString("hindiContent", ""),
            santhaliContent = json.optString("santhaliContent", ""),
            mundariContent = json.optString("mundariContent", ""),
            isMultilingual = generateAllLanguages
        )
    }

    private fun callGeminiWorksheet(
        grade: String,
        subject: String,
        topic: String,
        language: Language,
        count: Int,
        difficulty: String,
        questionType: String,
        customInstruction: String,
        apiKey: String
    ): List<WorksheetQuestion> {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        val prompt = """
            Create $count primary school worksheet questions for Jharkhand primary students (SIH26042).
            Grade: $grade | Subject: $subject | Topic: $topic | Type: $questionType | Difficulty: $difficulty
            Custom Instruction: $customInstruction
            
            Return STRICTLY valid JSON with schema:
            {
              "questions": [
                {
                  "prompt": "Question text",
                  "questionType": "MCQ or FILL_IN_BLANKS or TRUE_FALSE or TRANSLATION",
                  "options": ["Option A", "Option B", "Option C", "Option D"],
                  "expectedAnswer": "Correct answer",
                  "points": 1
                }
              ]
            }
        """.trimIndent()

        val json = postGemini(endpoint, prompt) ?: return emptyList()
        val qArray = json.optJSONArray("questions") ?: return emptyList()
        val list = mutableListOf<WorksheetQuestion>()
        for (i in 0 until qArray.length()) {
            val qObj = qArray.optJSONObject(i) ?: continue
            val options = mutableListOf<String>()
            val optArray = qObj.optJSONArray("options")
            if (optArray != null) {
                for (j in 0 until optArray.length()) {
                    options.add(optArray.optString(j))
                }
            }
            list.add(
                WorksheetQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = i + 1,
                    questionType = qObj.optString("questionType", "MCQ"),
                    prompt = qObj.optString("prompt", "Question ${i + 1}"),
                    options = options,
                    expectedAnswer = qObj.optString("expectedAnswer", ""),
                    points = qObj.optInt("points", 1)
                )
            )
        }
        return list
    }

    private fun callGeminiQuiz(
        grade: String,
        subject: String,
        topic: String,
        language: Language,
        count: Int,
        difficulty: String,
        customInstruction: String,
        apiKey: String
    ): List<QuizQuestion> {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        val prompt = """
            Create $count primary school quiz questions for Jharkhand students (SIH26042).
            Include a mix of MCQ and fill-in-the-blank questions testing mother-tongue vernacular understanding (Hindi, Santhali, Mundari, English).
            Grade: $grade | Subject: $subject | Topic: $topic | Difficulty: $difficulty
            
            Return STRICTLY valid JSON with schema:
            {
              "questions": [
                {
                  "questionText": "Question text",
                  "questionType": "MCQ or FILL_IN_BLANKS",
                  "options": ["Option 1", "Option 2", "Option 3", "Option 4"],
                  "correctAnswer": "Exact correct answer string",
                  "explanation": "Why this is correct",
                  "marks": 1
                }
              ]
            }
        """.trimIndent()

        val json = postGemini(endpoint, prompt) ?: return emptyList()
        val qArray = json.optJSONArray("questions") ?: return emptyList()
        val list = mutableListOf<QuizQuestion>()
        for (i in 0 until qArray.length()) {
            val qObj = qArray.optJSONObject(i) ?: continue
            val options = mutableListOf<String>()
            val optArray = qObj.optJSONArray("options")
            if (optArray != null) {
                for (j in 0 until optArray.length()) {
                    options.add(optArray.optString(j))
                }
            }
            list.add(
                QuizQuestion(
                    id = UUID.randomUUID().toString(),
                    questionText = qObj.optString("questionText", "Question ${i + 1}"),
                    questionType = qObj.optString("questionType", "MCQ"),
                    options = options,
                    correctAnswer = qObj.optString("correctAnswer", ""),
                    explanation = qObj.optString("explanation", ""),
                    marks = qObj.optInt("marks", 1)
                )
            )
        }
        return list
    }

    private fun callGeminiFlashcards(
        topic: String,
        category: String,
        count: Int,
        customInstruction: String,
        apiKey: String
    ): List<FlashcardItem> {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        val prompt = """
            Create $count multilingual vocabulary flashcards for Jharkhand primary school students (SIH26042).
            Topic: $topic | Preferred Category: $category
            Custom Instruction: $customInstruction

            Each flashcard needs a short word or phrase rendered in English, Hindi, Santhali (Ol Chiki script AND Roman transliteration), and Mundari (Devanagari script AND Roman transliteration), plus one representative emoji.

            Return STRICTLY valid JSON without code blocks or markdown:
            {
              "flashcards": [
                {
                  "category": "Classroom, Numbers, Colours, Animals, Fruits, Shapes, Objects, or Custom",
                  "english": "English word",
                  "hindi": "Hindi word",
                  "santhaliOlChiki": "Ol Chiki script",
                  "santhaliRoman": "Roman transliteration",
                  "mundariDevanagari": "Devanagari script",
                  "mundariRoman": "Roman transliteration",
                  "emoji": "Single representative emoji"
                }
              ]
            }
        """.trimIndent()

        val json = postGemini(endpoint, prompt) ?: return emptyList()
        val cardsArray = json.optJSONArray("flashcards") ?: return emptyList()
        val list = mutableListOf<FlashcardItem>()
        for (i in 0 until cardsArray.length()) {
            val cardObj = cardsArray.optJSONObject(i) ?: continue
            val english = cardObj.optString("english", "")
            val hindi = cardObj.optString("hindi", "")
            if (english.isBlank() && hindi.isBlank()) continue
            list.add(
                FlashcardItem(
                    category = cardObj.optString("category", category).ifBlank { "Custom" },
                    english = english,
                    hindi = hindi,
                    santhaliOlChiki = cardObj.optString("santhaliOlChiki", ""),
                    santhaliRoman = cardObj.optString("santhaliRoman", ""),
                    mundariDevanagari = cardObj.optString("mundariDevanagari", ""),
                    mundariRoman = cardObj.optString("mundariRoman", ""),
                    iconEmoji = cardObj.optString("emoji", "📚").ifBlank { "📚" },
                    isCustom = false
                )
            )
        }
        return list
    }

    private fun postGemini(endpoint: String, prompt: String): JSONObject? {
        val bodyJson = JSONObject().apply {
            val parts = JSONArray().put(JSONObject().put("text", prompt))
            val contents = JSONArray().put(JSONObject().put("parts", parts))
            put("contents", contents)
            put("generationConfig", JSONObject().put("temperature", 0.3))
        }

        val request = Request.Builder()
            .url(endpoint)
            .post(bodyJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) return null
        val responseBody = response.body?.string() ?: return null

        val root = JSONObject(responseBody)
        val candidates = root.optJSONArray("candidates") ?: return null
        val firstCandidate = candidates.optJSONObject(0) ?: return null
        val content = firstCandidate.optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        val rawText = parts.optJSONObject(0)?.optString("text") ?: return null

        val cleaned = rawText.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        return JSONObject(cleaned)
    }
}
