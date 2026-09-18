package com.example.ui.screens

import android.Manifest
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EducationalContext
import com.example.data.model.Language
import com.example.services.SpeechState
import com.example.ui.components.MicPulseButton
import com.example.ui.theme.BorderEmerald
import com.example.ui.theme.EducationalBlue
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VoiceTranslationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VoiceTranslationScreen(
    viewModel: VoiceTranslationViewModel,
    activeContext: EducationalContext?,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val speechState by viewModel.speechState.collectAsState()
    val recognizedText by viewModel.recognizedText.collectAsState()
    val soundLevel by viewModel.soundLevel.collectAsState()
    val translationResult by viewModel.translationResult.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var speechLanguage by remember {
        mutableStateOf(Language.HINDI)
    }

    val micPermissionState =
        rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ---------------------------------------------------------
        // EDUCATIONAL CONTEXT
        // ---------------------------------------------------------

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(PrimaryContainer)
                .border(
                    1.dp,
                    BorderEmerald,
                    RoundedCornerShape(50)
                )
                .padding(
                    horizontal = 14.dp,
                    vertical = 6.dp
                )
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = PrimaryEmerald,
                modifier = Modifier.size(15.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Context: ${
                    activeContext?.topic
                        ?: "Daily Classroom Interaction"
                }",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryEmerald
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------------------------------------------------------
        // TITLE
        // ---------------------------------------------------------

        Text(
            text = "Voice Translation",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            text = "Speak teacher commands in Hindi or English to bridge with Santhali & Mundari",
            fontSize = 13.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 4.dp
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ---------------------------------------------------------
        // LANGUAGE SELECTOR
        // ---------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            Language.entries.forEach { lang ->

                val isSelected =
                    speechLanguage == lang

                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) {
                                PrimaryEmerald
                            } else {
                                Color.White
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) {
                                PrimaryEmerald
                            } else {
                                OutlineLight
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {

                            speechLanguage = lang

                            if (!lang.isDeviceSttDefaultSupported) {
                                onShowSnackbar(
                                    "Speech recognition is not available for " +
                                            "${lang.displayName} on this device. " +
                                            "Primary input is Hindi/English."
                                )
                            }
                        }
                        .padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        )
                        .testTag(
                            "speech_lang_${lang.id}"
                        )
                ) {

                    Text(
                        text = lang.displayName,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Medium
                        },
                        color = if (isSelected) {
                            Color.White
                        } else {
                            TextSecondary
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------------------------------------------------
        // MICROPHONE
        // ---------------------------------------------------------

        MicPulseButton(
            isListening =
                speechState == SpeechState.LISTENING,

            soundLevel = soundLevel,

            onClick = {

                if (!micPermissionState.status.isGranted) {

                    micPermissionState.launchPermissionRequest()

                } else {

                    if (speechState == SpeechState.LISTENING) {

                        viewModel.stopVoiceRecording()

                    } else {

                        viewModel.startVoiceRecording(
                            speechLanguage
                        )
                    }
                }
            },

            testTag = "voice_screen_mic_btn"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ---------------------------------------------------------
        // SPEECH STATE
        // ---------------------------------------------------------

        val stateText = when (speechState) {

            SpeechState.IDLE ->
                "Tap microphone to speak"

            SpeechState.LISTENING ->
                "Listening... Speak clearly now"

            SpeechState.PROCESSING ->
                "Recognizing speech..."

            SpeechState.TRANSLATING ->
                "Translating into Santhali & Mundari..."

            SpeechState.SPEAKING ->
                "Playing audio..."

            SpeechState.COMPLETED ->
                "Translation completed"

            SpeechState.ERROR ->
                "Ready to try again"
        }

        Text(
            text = stateText,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (
                speechState == SpeechState.LISTENING
            ) {
                Color(0xFFDC2626)
            } else {
                PrimaryEmerald
            },
            modifier = Modifier.testTag(
                "speech_state_label"
            )
        )

        // ---------------------------------------------------------
        // STATUS MESSAGE
        // ---------------------------------------------------------

        statusMessage?.let { message ->

            Card(
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {

                Text(
                    text = message,
                    fontSize = 12.sp,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------------------------------------------------------
        // RECOGNIZED SPEECH
        // ---------------------------------------------------------

        if (recognizedText.isNotBlank()) {

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
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(
                        "recognized_text_card"
                    )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                "RECOGNIZED SPEECH (${speechLanguage.displayName})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = PrimaryEmerald
                        )

                        IconButton(
                            onClick = {
                                viewModel.playTts(
                                    recognizedText,
                                    speechLanguage
                                )
                            },
                            modifier = Modifier.size(32.dp)
                        ) {

                            Icon(
                                imageVector =
                                    Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription =
                                    "Play Input",
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = recognizedText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(
                            vertical = 4.dp
                        )
                    )
                }
            }
        }

        // ---------------------------------------------------------
        // LOADING
        // ---------------------------------------------------------

        if (
            speechState == SpeechState.PROCESSING ||
            speechState == SpeechState.TRANSLATING
        ) {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            CircularProgressIndicator(
                color = PrimaryEmerald,
                modifier = Modifier.size(36.dp)
            )
        }

        // ---------------------------------------------------------
        // TRANSLATION RESULT
        // ---------------------------------------------------------

        translationResult?.let { result ->

            Spacer(
                modifier = Modifier.height(16.dp)
            )

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
                    defaultElevation = 2.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(
                        "translation_output_card"
                    )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    // -------------------------------------------------
                    // HEADER
                    // -------------------------------------------------

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                "PEDAGOGICAL TRANSLATIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = PrimaryEmerald
                        )

                        Row {

                            // COPY
                            IconButton(
                                onClick = {

                                    val clip =
                                        "${result.sourceText}\n" +
                                                "Santhali: ${result.santhaliOlChiki} " +
                                                "(${result.santhaliRoman})\n" +
                                                "Mundari: ${result.mundariDevanagari} " +
                                                "(${result.mundariRoman})"

                                    val clipboard =
                                        context.getSystemService(
                                            Context.CLIPBOARD_SERVICE
                                        ) as ClipboardManager

                                    clipboard.setPrimaryClip(
                                        ClipData.newPlainText(
                                            "Translation",
                                            clip
                                        )
                                    )

                                    onShowSnackbar(
                                        "Copied translations to clipboard"
                                    )
                                },
                                modifier = Modifier.size(32.dp)
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.ContentCopy,
                                    contentDescription =
                                        "Copy",
                                    tint = PrimaryEmerald,
                                    modifier =
                                        Modifier.size(18.dp)
                                )
                            }

                            // SAVE
                            IconButton(
                                onClick = {

                                    viewModel.saveCurrentTranslation(
                                        activeContext?.topic ?: ""
                                    )

                                    onShowSnackbar(
                                        "Saved to Materials repository"
                                    )
                                },
                                modifier = Modifier.size(32.dp),
                                enabled = !isSaved
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
                                        if (isSaved) {
                                            PrimaryGreen
                                        } else {
                                            PrimaryEmerald
                                        },
                                    modifier =
                                        Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    // -------------------------------------------------
                    // 1. SANTHALI
                    // -------------------------------------------------

                    TranslationLangBlock(
                        title =
                            "ᱥᱟᱱᱛᱟᱲᱤ (Santhali)",

                        primaryScript =
                            result.santhaliOlChiki,

                        scriptLabel =
                            "Ol Chiki Script",

                        phoneticRoman =
                            result.santhaliRoman,

                        accentColor =
                            PrimaryEmerald,

                        onPlay = {

                            // IMPORTANT:
                            // Santhali TTS gets Ol Chiki text
                            viewModel.playTts(
                                result.santhaliOlChiki,
                                Language.SANTHALI
                            )
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    // -------------------------------------------------
                    // 2. MUNDARI
                    // -------------------------------------------------

                    TranslationLangBlock(
                        title =
                            "मुंडारी (Mundari)",

                        primaryScript =
                            result.mundariDevanagari,

                        scriptLabel =
                            "Devanagari Script",

                        phoneticRoman =
                            result.mundariRoman,

                        accentColor =
                            SecondaryTeal,

                        onPlay = {

                            viewModel.playTts(
                                result.mundariRoman,
                                Language.MUNDARI
                            )
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    // -------------------------------------------------
                    // 3. ENGLISH
                    // -------------------------------------------------

                    TranslationLangBlock(
                        title =
                            "English (Curriculum Bridge)",

                        primaryScript =
                            result.englishText,

                        scriptLabel =
                            "Latin Script",

                        phoneticRoman = "",

                        accentColor =
                            EducationalBlue,

                        onPlay = {

                            viewModel.playTts(
                                result.englishText,
                                Language.ENGLISH
                            )
                        }
                    )

                    // -------------------------------------------------
                    // PRONUNCIATION / ENGINE NOTE
                    // -------------------------------------------------

                    if (
                        result.pronunciationGuide.isNotBlank()
                    ) {

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(
                                    RoundedCornerShape(12.dp)
                                )
                                .background(
                                    PrimaryContainer
                                )
                                .border(
                                    1.dp,
                                    BorderEmerald,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(10.dp)
                        ) {

                            Text(
                                text =
                                    "🗣️ Note: " +
                                            "${result.pronunciationGuide}\n" +
                                            "Engine: " +
                                            result.sourceEngine,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ---------------------------------------------------------
        // CLEAR BUTTON
        // ---------------------------------------------------------

        if (
            recognizedText.isNotBlank() ||
            translationResult != null
        ) {

            OutlinedButton(
                onClick = {
                    viewModel.clear()
                },
                modifier = Modifier.testTag(
                    "voice_clear_btn"
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    OutlineLight
                ),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = TextPrimary
                    )
            ) {

                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    "Clear and Start New Recording"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}


// =====================================================================
// TRANSLATION LANGUAGE BLOCK
// =====================================================================

@Composable
private fun TranslationLangBlock(
    title: String,
    primaryScript: String,
    scriptLabel: String,
    phoneticRoman: String,
    accentColor: Color,
    onPlay: () -> Unit
) {

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
            .padding(14.dp)
    ) {

        // -------------------------------------------------------------
        // HEADER
        // -------------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "• $scriptLabel",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            IconButton(
                onClick = onPlay,
                modifier = Modifier.size(28.dp)
            ) {

                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription =
                        "Speak",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // -------------------------------------------------------------
        // MAIN TRANSLATION TEXT
        // -------------------------------------------------------------

        Text(
            text =
                primaryScript.ifBlank {
                    "Translation in progress..."
                },
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(
                vertical = 3.dp
            )
        )

        // -------------------------------------------------------------
        // PRONUNCIATION
        // -------------------------------------------------------------

        if (phoneticRoman.isNotBlank()) {

            Text(
                text =
                    "Pronunciation: $phoneticRoman",
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}