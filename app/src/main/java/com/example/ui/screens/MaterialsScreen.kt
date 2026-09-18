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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MaterialTab
import com.example.ui.viewmodel.MaterialsViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun MaterialsScreen(
    viewModel: MaterialsViewModel,
    onNavigate: (Screen) -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedTab by viewModel.selectedTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val lessons by viewModel.lessons.collectAsState()
    val worksheets by viewModel.worksheets.collectAsState()
    val quizzes by viewModel.quizzes.collectAsState()
    val flashcards by viewModel.flashcards.collectAsState()
    val translations by viewModel.translations.collectAsState()

    var pendingDeleteAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val tabs = listOf(
        MaterialTab.ALL to "All (${lessons.size + worksheets.size + quizzes.size + translations.size})",
        MaterialTab.LESSONS to "Lessons (${lessons.size})",
        MaterialTab.WORKSHEETS to "Worksheets (${worksheets.size})",
        MaterialTab.QUIZZES to "Quizzes (${quizzes.size})",
        MaterialTab.TRANSLATIONS to "Translations (${translations.size})",
        MaterialTab.FLASHCARDS to "Flashcards (${flashcards.size})"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Materials Repository",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Your saved lessons, worksheets, quizzes, and translations",
            fontSize = 13.sp,
            color = TextMuted,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search materials by title or topic...", fontSize = 13.sp, color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = PrimaryEmerald) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("materials_search_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PrimaryEmerald,
                unfocusedBorderColor = OutlineLight
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs Row
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            items(tabs) { (tab, label) ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PrimaryEmerald else Color.White)
                        .border(
                            1.dp,
                            if (isSelected) PrimaryEmerald else OutlineLight,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.selectTab(tab) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("material_tab_${tab.name}")
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Content List or Empty State
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // LESSONS
            if (selectedTab == MaterialTab.ALL || selectedTab == MaterialTab.LESSONS) {
                val filteredLessons = lessons.filter {
                    searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.topic.contains(searchQuery, ignoreCase = true)
                }
                items(filteredLessons) { lesson ->
                    MaterialItemCard(
                        typeLabel = "LESSON PLAN",
                        title = lesson.title,
                        subtitle = "Grade: ${lesson.grade} • Subject: ${lesson.subject}",
                        accentColor = PrimaryEmerald,
                        onShare = {
                            val clip = "${lesson.title}\n\nObjectives:\n${lesson.learningObjectives}\n\nTeacher Script:\n${lesson.teacherScript}"
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Lesson Plan", clip))
                            onShowSnackbar("Copied lesson plan to clipboard")
                        },
                        onDelete = {
                            pendingDeleteAction = { viewModel.deleteLesson(lesson) }
                            showDeleteConfirmDialog = true
                        }
                    )
                }
            }

            // WORKSHEETS
            if (selectedTab == MaterialTab.ALL || selectedTab == MaterialTab.WORKSHEETS) {
                val filteredWorksheets = worksheets.filter {
                    searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.topic.contains(searchQuery, ignoreCase = true)
                }
                items(filteredWorksheets) { ws ->
                    MaterialItemCard(
                        typeLabel = "WORKSHEET",
                        title = ws.title,
                        subtitle = "${ws.questionCount} Questions • Difficulty: ${ws.difficulty}",
                        accentColor = SecondaryTeal,
                        onShare = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Worksheet", "${ws.title}\nQuestions: ${ws.questionsJson}"))
                            onShowSnackbar("Copied worksheet details to clipboard")
                        },
                        onDelete = {
                            pendingDeleteAction = { viewModel.deleteWorksheet(ws) }
                            showDeleteConfirmDialog = true
                        }
                    )
                }
            }

            // QUIZZES
            if (selectedTab == MaterialTab.ALL || selectedTab == MaterialTab.QUIZZES) {
                val filteredQuizzes = quizzes.filter {
                    searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.topic.contains(searchQuery, ignoreCase = true)
                }
                items(filteredQuizzes) { q ->
                    MaterialItemCard(
                        typeLabel = "QUIZ",
                        title = q.title,
                        subtitle = "Total Marks: ${q.totalMarks} • Last Score: ${if (q.lastScore >= 0) "${q.lastScore}/${q.totalMarks}" else "Not Taken"}",
                        accentColor = Color(0xFF0284C7),
                        onShare = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Quiz", q.title))
                            onShowSnackbar("Copied quiz title")
                        },
                        onDelete = {
                            pendingDeleteAction = { viewModel.deleteQuiz(q) }
                            showDeleteConfirmDialog = true
                        }
                    )
                }
            }

            // TRANSLATIONS
            if (selectedTab == MaterialTab.ALL || selectedTab == MaterialTab.TRANSLATIONS) {
                val filteredTranslations = translations.filter {
                    searchQuery.isBlank() || it.sourceText.contains(searchQuery, ignoreCase = true) || it.translatedText.contains(searchQuery, ignoreCase = true)
                }
                items(filteredTranslations) { tr ->
                    MaterialItemCard(
                        typeLabel = "TRANSLATION",
                        title = tr.sourceText,
                        subtitle = "Santhali: ${tr.santhaliOlChiki} (${tr.santhaliRoman})\nMundari: ${tr.mundariDevanagari} (${tr.mundariRoman})",
                        accentColor = Color(0xFFD97706),
                        onShare = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Translation", "${tr.sourceText} -> Santhali: ${tr.santhaliOlChiki} | Mundari: ${tr.mundariDevanagari}"))
                            onShowSnackbar("Copied translation")
                        },
                        onDelete = {
                            pendingDeleteAction = { viewModel.deleteTranslation(tr) }
                            showDeleteConfirmDialog = true
                        }
                    )
                }
            }

            // Empty state if nothing matches
            item {
                if (lessons.isEmpty() && worksheets.isEmpty() && quizzes.isEmpty() && translations.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = TextMuted, modifier = Modifier.size(56.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No saved materials yet", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Generate lessons, worksheets, quizzes or translate speech to save them here.", fontSize = 12.sp, color = TextMuted, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { onNavigate(Screen.Learn) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Generate a Lesson", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Confirm Deletion", fontWeight = FontWeight.Bold, color = TextPrimary) },
                text = { Text("Are you sure you want to delete this item? This action cannot be undone.", color = TextSecondary) },
                confirmButton = {
                    Button(
                        onClick = {
                            pendingDeleteAction?.invoke()
                            pendingDeleteAction = null
                            showDeleteConfirmDialog = false
                            onShowSnackbar("Item deleted successfully")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDeleteConfirmDialog = false },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, OutlineLight)
                    ) {
                        Text("Cancel", color = TextPrimary)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White
            )
        }
    }
}

@Composable
private fun MaterialItemCard(
    typeLabel: String,
    title: String,
    subtitle: String,
    accentColor: Color,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, OutlineLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = typeLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = accentColor
                )
                Row {
                    IconButton(onClick = onShare, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = accentColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(vertical = 3.dp)
            )

            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
