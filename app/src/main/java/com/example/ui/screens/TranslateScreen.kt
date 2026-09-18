package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.dictionary.VernacularDictionary
import com.example.data.model.Language
import com.example.ui.theme.EducationalBlue
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.TranslationViewModel

@Composable
fun TranslateScreen(
    viewModel: TranslationViewModel,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val fromLanguage by viewModel.fromLanguage.collectAsState()
    val toLanguage by viewModel.toLanguage.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val translationResult by viewModel.translationResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    snackbarMessage?.let { message ->
        onShowSnackbar(message)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // =====================================================
        // HEADER
        // =====================================================

        Text(
            text = "Text Translation",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            text = "Translate pedagogical sentences between Hindi, English, Santhali and Mundari",
            fontSize = 13.sp,
            color = TextMuted,
            modifier = Modifier.padding(
                top = 2.dp,
                bottom = 14.dp
            )
        )


        // =====================================================
        // LANGUAGE SELECTOR
        // =====================================================

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(
                1.dp,
                OutlineLight
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 1.dp
            ),
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    ),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                // FROM LANGUAGE

                LanguageDropdownButton(
                    selectedLang = fromLanguage,
                    onSelect = {
                        viewModel.setFromLanguage(it)
                    },
                    testTag = "translate_from_lang"
                )


                // SWAP

                IconButton(
                    onClick = {
                        viewModel.swapLanguages()
                    },
                    modifier = Modifier.testTag(
                        "translate_swap_btn"
                    )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.SwapHoriz,
                        contentDescription =
                            "Swap Languages",
                        tint = PrimaryEmerald
                    )
                }


                // TO LANGUAGE

                LanguageDropdownButton(
                    selectedLang = toLanguage,
                    onSelect = {
                        viewModel.setToLanguage(it)
                    },
                    testTag = "translate_to_lang"
                )
            }
        }


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        // =====================================================
        // INPUT TEXT
        // =====================================================

        OutlinedTextField(
            value = inputText,

            onValueChange = {
                viewModel.setInputText(it)
            },

            label = {
                Text(
                    "Enter text in ${fromLanguage.displayName}..."
                )
            },

            placeholder = {
                Text(
                    "e.g., अपनी किताब खोलो / Stand up"
                )
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .testTag(
                    "translate_input_field"
                ),

            shape = RoundedCornerShape(18.dp),

            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PrimaryEmerald,
                unfocusedBorderColor = OutlineLight
            ),

            trailingIcon = {

                if (inputText.isNotBlank()) {

                    IconButton(
                        onClick = {
                            viewModel.clear()
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Clear,
                            contentDescription =
                                "Clear"
                        )
                    }
                }
            }
        )


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // =====================================================
        // TRANSLATE BUTTON
        // =====================================================

        Button(
            onClick = {
                viewModel.translate()
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag(
                    "translate_action_btn"
                ),

            shape = RoundedCornerShape(16.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryEmerald
            ),

            enabled =
                inputText.isNotBlank() &&
                !isLoading
        ) {

            if (isLoading) {

                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(22.dp)
                )

            } else {

                Icon(
                    imageVector =
                        Icons.Default.Translate,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Translate Text",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // =====================================================
        // QUICK CLASSROOM LOOKUP
        // =====================================================

        Text(
            text = "QUICK CLASSROOM LOOKUP",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = TextMuted
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )


        LazyRow(
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            items(
                VernacularDictionary.entries.take(8)
            ) { entry ->

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(14.dp)
                        )
                        .background(Color.White)
                        .border(
                            1.dp,
                            OutlineLight,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {

                            viewModel.setInputText(
                                entry.hindi
                            )

                            viewModel.translate()
                        }
                        .padding(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        )
                ) {

                    Text(
                        text =
                            "${entry.emoji} ${entry.hindi}",
                        fontSize = 12.sp,
                        fontWeight =
                            FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
            }
        }


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // =====================================================
        // TRANSLATION RESULT
        // =====================================================

        translationResult?.let { result ->

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),

                shape =
                    RoundedCornerShape(24.dp),

                border =
                    BorderStroke(
                        1.dp,
                        OutlineLight
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),

                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(
                        "translate_result_card"
                    )
            ) {

                Column(
                    modifier =
                        Modifier.padding(18.dp)
                ) {


                    // -------------------------------------------------
                    // RESULT HEADER
                    // -------------------------------------------------

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.SpaceBetween,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                "TRANSLATION RESULT",

                            fontSize = 11.sp,

                            fontWeight =
                                FontWeight.Bold,

                            letterSpacing =
                                0.5.sp,

                            color =
                                PrimaryEmerald
                        )


                        Row {

                            // COPY

                            IconButton(
                                onClick = {

                                    val clipText =
                                        buildString {

                                            append(
                                                "Translation: "
                                            )

                                            append(
                                                result.primaryTranslation
                                            )

                                            append(
                                                "\nHindi: "
                                            )

                                            append(
                                                result.hindiText
                                            )

                                            append(
                                                "\nSanthali: "
                                            )

                                            append(
                                                result.santhaliOlChiki
                                            )

                                            append(
                                                "\nMundari: "
                                            )

                                            append(
                                                result.mundariDevanagari
                                            )
                                        }


                                    val clipboard =
                                        context.getSystemService(
                                            Context.CLIPBOARD_SERVICE
                                        ) as ClipboardManager


                                    clipboard.setPrimaryClip(
                                        ClipData.newPlainText(
                                            "Translation",
                                            clipText
                                        )
                                    )


                                    onShowSnackbar(
                                        "Copied to clipboard"
                                    )
                                },

                                modifier =
                                    Modifier.size(32.dp)
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.ContentCopy,

                                    contentDescription =
                                        "Copy",

                                    tint =
                                        PrimaryEmerald,

                                    modifier =
                                        Modifier.size(18.dp)
                                )
                            }


                            // SAVE

                            IconButton(
                                onClick = {
                                    viewModel.saveTranslation()
                                },

                                modifier =
                                    Modifier.size(32.dp),

                                enabled =
                                    !isSaved
                            ) {

                                Icon(
                                    imageVector =
                                        if (isSaved) {
                                            Icons.Default.Bookmark
                                        } else {
                                            Icons.Default.BookmarkBorder
                                        },

                                    contentDescription =
                                        "Save",

                                    tint =
                                        PrimaryEmerald,

                                    modifier =
                                        Modifier.size(20.dp)
                                )
                            }
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )


                    // -------------------------------------------------
                    // PRIMARY TRANSLATION
                    // -------------------------------------------------

                    Text(
                        text =
                            result.primaryTranslation.ifBlank {
                                "-"
                            },

                        fontSize = 18.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextPrimary
                    )


                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )


                    // -------------------------------------------------
                    // ALL LANGUAGES
                    // -------------------------------------------------

                    Text(
                        text =
                            "All Languages Breakdown:",

                        fontSize = 12.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextMuted
                    )


                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(16.dp)
                            )
                            .background(
                                SleekBackground
                            )
                            .border(
                                1.dp,
                                OutlineLight,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {


                        // ENGLISH

                        ResultLanguageRow(
                            langName = "English",
                            content = result.englishText,
                            accentColor = EducationalBlue,
                            onSpeak = {

                                if (
                                    result.englishText
                                        .isNotBlank()
                                ) {

                                    viewModel.playAudio(
                                        result.englishText,
                                        Language.ENGLISH
                                    )
                                }
                            }
                        )


                        // HINDI

                        ResultLanguageRow(
                            langName = "Hindi",
                            content = result.hindiText,
                            accentColor = PrimaryEmerald,
                            onSpeak = {

                                if (
                                    result.hindiText
                                        .isNotBlank()
                                ) {

                                    viewModel.playAudio(
                                        result.hindiText,
                                        Language.HINDI
                                    )
                                }
                            }
                        )


                        // SANTHALI

                        val santhaliDisplay =
                            buildDisplayText(
                                result.santhaliOlChiki,
                                result.santhaliRoman
                            )

                        ResultLanguageRow(
                            langName =
                                "Santhali (Ol Chiki)",

                            content =
                                santhaliDisplay,

                            accentColor =
                                PrimaryGreen,

                            onSpeak = {

                                if (
                                    result.santhaliOlChiki
                                        .isNotBlank()
                                ) {

                                    viewModel.playAudio(
                                        result.santhaliOlChiki,
                                        Language.SANTHALI
                                    )
                                }
                            }
                        )


                        // MUNDARI

                        val mundariDisplay =
                            buildDisplayText(
                                result.mundariDevanagari,
                                result.mundariRoman
                            )

                        ResultLanguageRow(
                            langName =
                                "Mundari",

                            content =
                                mundariDisplay,

                            accentColor =
                                SecondaryTeal,

                            onSpeak = {

                                /*
                                 * IMPORTANT:
                                 *
                                 * Your current device does not
                                 * have a Mundari TTS voice.
                                 *
                                 * Therefore TextToSpeechService
                                 * will show a friendly message
                                 * instead of crashing.
                                 */

                                val textForAudio =
                                    result.mundariRoman.ifBlank {
                                        result.mundariDevanagari
                                    }

                                if (
                                    textForAudio.isNotBlank()
                                ) {

                                    viewModel.playAudio(
                                        textForAudio,
                                        Language.MUNDARI
                                    )
                                }
                            }
                        )
                    }


                    // -------------------------------------------------
                    // PRONUNCIATION
                    // -------------------------------------------------

                    if (
                        result.pronunciationGuide
                            .isNotBlank()
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )

                        Text(
                            text =
                                "💡 ${result.pronunciationGuide}",

                            fontSize = 11.sp,

                            color =
                                TextSecondary
                        )
                    }
                }
            }
        }


        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}


// =============================================================
// LANGUAGE SELECTOR
// =============================================================

@Composable
private fun LanguageDropdownButton(
    selectedLang: Language,
    onSelect: (Language) -> Unit,
    testTag: String
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically,

        modifier =
            Modifier.testTag(testTag)
    ) {

        Language.entries.forEach { lang ->

            val isSelected =
                selectedLang == lang


            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(
                        if (isSelected) {
                            PrimaryEmerald
                        } else {
                            Color.Transparent
                        }
                    )
                    .clickable {
                        onSelect(lang)
                    }
                    .padding(
                        horizontal = 8.dp,
                        vertical = 5.dp
                    )
            ) {

                Text(
                    text =
                        lang.displayName
                            .take(3)
                            .uppercase(),

                    fontSize = 11.sp,

                    fontWeight =
                        if (isSelected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Medium
                        },

                    color =
                        if (isSelected) {
                            Color.White
                        } else {
                            TextMuted
                        }
                )
            }
        }
    }
}


// =============================================================
// RESULT LANGUAGE ROW
// =============================================================

@Composable
private fun ResultLanguageRow(
    langName: String,
    content: String,
    accentColor: Color,
    onSpeak: () -> Unit
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween,

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text = langName,

                fontSize = 11.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    accentColor
            )

            Text(
                text =
                    content.ifBlank {
                        "-"
                    },

                fontSize = 13.sp,

                fontWeight =
                    FontWeight.Medium,

                color =
                    TextPrimary
            )
        }


        IconButton(
            onClick = onSpeak,

            modifier =
                Modifier.size(28.dp)
        ) {

            Icon(
                imageVector =
                    Icons.AutoMirrored.Filled.VolumeUp,

                contentDescription =
                    "Speak",

                tint =
                    accentColor,

                modifier =
                    Modifier.size(16.dp)
            )
        }
    }
}


// =============================================================
// DISPLAY TEXT HELPER
// =============================================================

private fun buildDisplayText(
    primary: String,
    roman: String
): String {

    val cleanPrimary =
        primary.trim()

    val cleanRoman =
        roman.trim()


    return when {

        cleanPrimary.isNotBlank() &&
                cleanRoman.isNotBlank() -> {

            "$cleanPrimary ($cleanRoman)"
        }

        cleanPrimary.isNotBlank() -> {

            cleanPrimary
        }

        cleanRoman.isNotBlank() -> {

            cleanRoman
        }

        else -> {
            ""
        }
    }
}