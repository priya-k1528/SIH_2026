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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.Language
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LearnViewModel

@Composable
fun LearnScreen(
    viewModel: LearnViewModel,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val grade by viewModel.selectedGrade.collectAsState()
    val subject by viewModel.selectedSubject.collectAsState()
    val topic by viewModel.topic.collectAsState()
    val difficulty by viewModel.difficulty.collectAsState()
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val customInstruction by viewModel.customInstruction.collectAsState()
    val generatedLesson by viewModel.generatedLesson.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    statusMessage?.let { onShowSnackbar(it) }

    val grades = listOf("Pre-Primary", "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5")
    val subjects = listOf("Language", "Mathematics", "EVS", "Science", "Social Science")
    val difficulties = listOf("Easy", "Medium", "Hard")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Curriculum & Lesson Generator",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Generate structured primary lessons with tribal mother tongue integration",
            fontSize = 13.sp,
            color = TextMuted,
            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
        )

        // Generator Options Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, OutlineLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Grade Selector
                Text("Select Grade", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                    items(grades) { g ->
                        val selected = grade == g
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) PrimaryEmerald else Color.White)
                                .border(1.dp, if (selected) PrimaryEmerald else OutlineLight, RoundedCornerShape(12.dp))
                                .clickable { viewModel.setGrade(g) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                g,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Subject Selector
                Text("Select Subject", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                    items(subjects) { s ->
                        val selected = subject == s
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) SecondaryTeal else Color.White)
                                .border(1.dp, if (selected) SecondaryTeal else OutlineLight, RoundedCornerShape(12.dp))
                                .clickable { viewModel.setSubject(s) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                s,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Topic Field
                OutlinedTextField(
                    value = topic,
                    onValueChange = { viewModel.setTopic(it) },
                    label = { Text("Lesson Topic") },
                    placeholder = { Text("e.g., Counting 1 to 5 with Animals") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = OutlineLight
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Custom Teacher Instruction
                OutlinedTextField(
                    value = customInstruction,
                    onValueChange = { viewModel.setCustomInstruction(it) },
                    label = { Text("Custom Instructions (Optional)") },
                    placeholder = { Text("e.g., Include local Jharkhand village examples") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = OutlineLight
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.generateLesson(generateAllLanguages = false) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("learn_gen_single_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Generate Lesson", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.generateLesson(generateAllLanguages = true) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("learn_gen_all_btn"),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, OutlineLight),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        enabled = !isGenerating
                    ) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("All Languages", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Generated Lesson Preview Card
        generatedLesson?.let { lesson ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, OutlineLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lesson_preview_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LESSON PLAN PREVIEW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = PrimaryEmerald
                        )
                        Row {
                            IconButton(
                                onClick = {
                                    val clip = "${lesson.title}\n\nObjectives:\n${lesson.learningObjectives}\n\nTeacher Script:\n${lesson.teacherScript}\n\nVocabulary:\n${lesson.vocabularyMarkdown}"
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Lesson", clip))
                                    onShowSnackbar("Copied lesson to clipboard")
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { viewModel.saveLesson() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Bookmark, contentDescription = "Save", tint = PrimaryEmerald, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    Text(
                        text = lesson.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Objectives
                    LessonSection("🎯 Learning Objectives", lesson.learningObjectives)

                    // Teacher Script
                    LessonSection("🗣️ Bilingual Teacher Script", lesson.teacherScript)

                    // Multilingual Vocabulary
                    LessonSection("📖 Multilingual Vocabulary", lesson.vocabularyMarkdown)

                    // Activities
                    LessonSection("🎨 Classroom Activities", lesson.studentActivities)

                    // Multilingual Summaries if requested
                    if (lesson.isMultilingual) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Multilingual Content Summaries:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryEmerald)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("English: ${lesson.englishContent}", fontSize = 12.sp, color = TextPrimary)
                        Text("Hindi: ${lesson.hindiContent}", fontSize = 12.sp, color = TextPrimary)
                        Text("Santhali: ${lesson.santhaliContent}", fontSize = 12.sp, color = PrimaryEmerald)
                        Text("Mundari: ${lesson.mundariContent}", fontSize = 12.sp, color = SecondaryTeal)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LessonSection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryEmerald)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SleekBackground)
                .border(1.dp, OutlineLight, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Text(text = content.ifBlank { "N/A" }, fontSize = 12.sp, color = TextPrimary, lineHeight = 17.sp)
        }
    }
}

