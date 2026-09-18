package com.example.services
import android.content.Context

import com.example.BuildConfig
import com.example.data.dictionary.VernacularDictionary
import com.example.data.dictionary.VernacularEntry
import com.example.data.model.Language
import com.example.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class FullTranslationResult(
    val sourceText: String,
    val sourceLanguage: Language,
    val targetLanguage: Language,
    val primaryTranslation: String,
    val englishText: String,
    val hindiText: String,
    val santhaliOlChiki: String,
    val santhaliRoman: String,
    val mundariDevanagari: String,
    val mundariRoman: String,
    val pronunciationGuide: String,
    val sourceEngine: String // "Offline Vernacular Engine" or "Cloud Gemini AI"
)

class TranslationService(private val context: Context) {

    private val database by lazy {
        AppDatabase.getDatabase(context)
    }

    private val translationDao by lazy {
        database.translationDao()
    }

    private val englishToSanthali by lazy {
        loadEnglishSanthaliDataset()
    }

    private fun loadEnglishSanthaliDataset(): Map<String, String> {
        val map = mutableMapOf<String, String>()


        try {
            context.assets.open("english_santali_android.csv")
                .bufferedReader()
                .useLines { lines ->

                    lines.drop(1).forEach { line ->
                        val commaIndex = line.indexOf(",")

                        if (commaIndex > 0) {
                            val source = line.substring(0, commaIndex)
                                .trim()
                                .removeSurrounding("\"")

                            val target = line.substring(commaIndex + 1)
                                .trim()
                                .removeSurrounding("\"")

                            if (source.isNotBlank() && target.isNotBlank()) {
                                map[source.lowercase()] = target
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return map
    }
    private fun findEnglishToSanthali(text: String): String? {
        return englishToSanthali[text.trim().lowercase()]
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun translate(
        text: String,
        fromLanguage: Language,
        toLanguage: Language,
        contextTopic: String = ""
    ): FullTranslationResult = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return@withContext FullTranslationResult(
                sourceText = "",
                sourceLanguage = fromLanguage,
                targetLanguage = toLanguage,
                primaryTranslation = "",
                englishText = "",
                hindiText = "",
                santhaliOlChiki = "",
                santhaliRoman = "",
                mundariDevanagari = "",
                mundariRoman = "",
                pronunciationGuide = "",
                sourceEngine = "Offline Vernacular Engine"
            )
        }
        // Hindi → Mundari Room dataset lookup
if (
    fromLanguage == Language.HINDI &&
    toLanguage == Language.MUNDARI
) {

    val datasetTranslation =
        translationDao.findExactTranslation(
            text = trimmed,
            sourceLanguage = "hi",
            targetLanguage = "mun"
        )

    if (datasetTranslation != null) {

        return@withContext FullTranslationResult(
            sourceText = trimmed,
            sourceLanguage = fromLanguage,
            targetLanguage = toLanguage,
            primaryTranslation =
                datasetTranslation.translatedText,
            englishText = "",
            hindiText = trimmed,
            santhaliOlChiki = "",
            santhaliRoman = "",
            mundariDevanagari =
                datasetTranslation.translatedText,
            mundariRoman = "",
            pronunciationGuide = "",
            sourceEngine =
                "Offline Hindi-Mundari Dataset"
        )
    }
}
        // English → Santhali dataset lookup
        if (fromLanguage == Language.ENGLISH && toLanguage == Language.SANTHALI) {

            val santhaliTranslation = findEnglishToSanthali(trimmed)

            if (santhaliTranslation != null) {
                return@withContext FullTranslationResult(
                    sourceText = trimmed,
                    sourceLanguage = fromLanguage,
                    targetLanguage = toLanguage,
                    primaryTranslation = santhaliTranslation,
                    englishText = trimmed,
                    hindiText = "",
                    santhaliOlChiki = santhaliTranslation,
                    santhaliRoman = "",
                    mundariDevanagari = "",
                    mundariRoman = "",
                    pronunciationGuide = "",
                    sourceEngine = "English-Santhali Dataset"
                )
            }
        }

        // Try Online Gemini AI Translation if API key is present and configured
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val geminiResult = callGeminiTranslation(trimmed, fromLanguage, toLanguage, contextTopic, apiKey)
                if (geminiResult != null) {
                    return@withContext geminiResult
                }
            } catch (_: Exception) {
                // Fall back gracefully to offline vernacular engine
            }
        }

        // Use Offline Vernacular Primary Pedagogy Engine
        translateUsingOfflineEngine(trimmed, fromLanguage, toLanguage)
    }

    private fun translateUsingOfflineEngine(
        text: String,
        fromLanguage: Language,
        toLanguage: Language
    ): FullTranslationResult {
        // Direct dictionary match
        val directMatch = VernacularDictionary.findDirectMatch(text)
        if (directMatch != null) {
            val primary = when (toLanguage) {
                Language.ENGLISH -> directMatch.english
                Language.HINDI -> directMatch.hindi
                Language.SANTHALI -> "${directMatch.santhaliOlChiki} (${directMatch.santhaliRoman})"
                Language.MUNDARI -> "${directMatch.mundariDevanagari} (${directMatch.mundariRoman})"
            }

            return FullTranslationResult(
                sourceText = text,
                sourceLanguage = fromLanguage,
                targetLanguage = toLanguage,
                primaryTranslation = primary,
                englishText = directMatch.english,
                hindiText = directMatch.hindi,
                santhaliOlChiki = directMatch.santhaliOlChiki,
                santhaliRoman = directMatch.santhaliRoman,
                mundariDevanagari = directMatch.mundariDevanagari,
                mundariRoman = directMatch.mundariRoman,
                pronunciationGuide = directMatch.pronunciationNote.ifBlank {
                    "Santhali: ${directMatch.santhaliRoman} | Mundari: ${directMatch.mundariRoman}"
                },
                sourceEngine = "Offline Vernacular Engine"
            )
        }

        // Word-by-word tokenized pedagogical translation for phrases
        val words = text.split("\\s+".toRegex())
        val translatedEnglish = StringBuilder()
        val translatedHindi = StringBuilder()
        val translatedSanthaliOlChiki = StringBuilder()
        val translatedSanthaliRoman = StringBuilder()
        val translatedMundariDevanagari = StringBuilder()
        val translatedMundariRoman = StringBuilder()

        var matchedCount = 0

        for (word in words) {
            val cleanWord = word.replace("[.,!?;:]".toRegex(), "")
            val match = VernacularDictionary.findDirectMatch(cleanWord)

            if (match != null) {
                matchedCount++
                appendWord(translatedEnglish, match.english)
                appendWord(translatedHindi, match.hindi)
                appendWord(translatedSanthaliOlChiki, match.santhaliOlChiki)
                appendWord(translatedSanthaliRoman, match.santhaliRoman)
                appendWord(translatedMundariDevanagari, match.mundariDevanagari)
                appendWord(translatedMundariRoman, match.mundariRoman)
            } else {
    // Unknown word:
    // Keep it only in source-compatible fields.
    // Do NOT copy Hindi/English text into
    // Santhali or Mundari fields.
    appendWord(translatedEnglish, word)
    appendWord(translatedHindi, word)
}
        }

        val en = translatedEnglish.toString()
        val hi = translatedHindi.toString()
        val satOl = translatedSanthaliOlChiki.toString()
        val satRom = translatedSanthaliRoman.toString()
        val munDev = translatedMundariDevanagari.toString()
        val munRom = translatedMundariRoman.toString()

        val primary = when (toLanguage) {
            Language.ENGLISH -> en
            Language.HINDI -> hi
            Language.SANTHALI -> if (satOl != satRom) "$satOl ($satRom)" else satOl
            Language.MUNDARI -> if (munDev != munRom) "$munDev ($munRom)" else munDev
        }

        val note = if (matchedCount > 0) {
            "Translated via Offline Primary Vernacular Pedagogical Engine ($matchedCount words matched)."
        } else {
            "Vernacular translation generated via primary pattern heuristics."
        }

        return FullTranslationResult(
            sourceText = text,
            sourceLanguage = fromLanguage,
            targetLanguage = toLanguage,
            primaryTranslation = primary,
            englishText = en,
            hindiText = hi,
            santhaliOlChiki = satOl,
            santhaliRoman = satRom,
            mundariDevanagari = munDev,
            mundariRoman = munRom,
            pronunciationGuide = note,
            sourceEngine = "Offline Vernacular Engine"
        )
    }

    private fun appendWord(sb: StringBuilder, word: String) {
        if (sb.isNotEmpty()) sb.append(" ")
        sb.append(word)
    }

    private fun callGeminiTranslation(
        text: String,
        fromLanguage: Language,
        toLanguage: Language,
        contextTopic: String,
        apiKey: String
    ): FullTranslationResult? {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val prompt = """
            You are a pedagogical translation expert for Jharkhand primary school education (SIH26042).
            Translate the following educational classroom statement from ${fromLanguage.displayName} into English, Hindi, Santhali, and Mundari.
            
            Text: "$text"
            Educational Context / Topic: "$contextTopic"
            
            Return STRICTLY valid JSON without code blocks or markdown, with this exact schema:
            {
              "english": "English translation",
              "hindi": "Hindi translation in Devanagari",
              "santhaliOlChiki": "Santhali translation in Ol Chiki script",
              "santhaliRoman": "Santhali phonetic pronunciation in Roman script",
              "mundariDevanagari": "Mundari translation in Devanagari script",
              "mundariRoman": "Mundari phonetic pronunciation in Roman script",
              "pronunciationGuide": "Helpful pronunciation advice for a Hindi-medium teacher"
            }
        """.trimIndent()

        val bodyJson = JSONObject().apply {
            val parts = JSONArray().put(JSONObject().put("text", prompt))
            val contents = JSONArray().put(JSONObject().put("parts", parts))
            put("contents", contents)
            put("generationConfig", JSONObject().put("temperature", 0.2))
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

        // Parse JSON from generated text
        val cleanedJson = rawText.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val parsed = JSONObject(cleanedJson)

        val en = parsed.optString("english", text)
        val hi = parsed.optString("hindi", text)
        val satOl = parsed.optString("santhaliOlChiki", "")
        val satRom = parsed.optString("santhaliRoman", "")
        val munDev = parsed.optString("mundariDevanagari", "")
        val munRom = parsed.optString("mundariRoman", "")
        val guide = parsed.optString("pronunciationGuide", "")

        val primary = when (toLanguage) {
            Language.ENGLISH -> en
            Language.HINDI -> hi
            Language.SANTHALI -> "$satOl ($satRom)"
            Language.MUNDARI -> "$munDev ($munRom)"
        }

        return FullTranslationResult(
            sourceText = text,
            sourceLanguage = fromLanguage,
            targetLanguage = toLanguage,
            primaryTranslation = primary,
            englishText = en,
            hindiText = hi,
            santhaliOlChiki = satOl,
            santhaliRoman = satRom,
            mundariDevanagari = munDev,
            mundariRoman = munRom,
            pronunciationGuide = guide,
            sourceEngine = "Cloud Gemini AI"
        )
    }
}
