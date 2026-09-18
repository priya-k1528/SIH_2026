package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EducationalContext
import com.example.data.model.Language
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.StateOffline
import com.example.ui.theme.StateOnline
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    appLanguage: Language,
    teachingLanguage: Language,
    onSetAppLanguage: (Language) -> Unit,
    onSetTeachingLanguage: (Language) -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val educationalContexts by viewModel.educationalContexts.collectAsState()
    val defaultContext by viewModel.defaultContext.collectAsState()
    val diagnostics by viewModel.diagnostics.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var showAddContextDialog by remember { mutableStateOf(false) }
    var newContextTitle by remember { mutableStateOf("") }
    var newContextGrade by remember { mutableStateOf("Grade 1") }
    var newContextSubject by remember { mutableStateOf("Language") }
    var newContextTopic by remember { mutableStateOf("") }
    var newContextInstructions by remember { mutableStateOf("") }

    statusMessage?.let { onShowSnackbar(it) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Teacher Profile Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 18.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(PrimaryContainer)
                    .border(1.dp, OutlineLight, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = PrimaryEmerald,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Teacher Profile & Settings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Jharkhand Primary School Vernacular Pedagogy",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        // Section 1: Independent Language Settings
        Text(
            text = "Language Settings",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, OutlineLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // App Interface Language
                Text(
                    text = "App Interface Language",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryEmerald
                )
                Text(
                    text = "Controls UI menus, buttons, and system headers",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Language.entries.forEach { lang ->
                        val isSelected = appLanguage == lang
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PrimaryEmerald else Color.White)
                                .border(1.dp, if (isSelected) PrimaryEmerald else OutlineLight, RoundedCornerShape(12.dp))
                                .clickable { onSetAppLanguage(lang) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lang.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Teaching / Translation Language
                Text(
                    text = "Teaching / Translation Language",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryTeal
                )
                Text(
                    text = "Default source language spoken by teacher for classroom speech",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Language.entries.forEach { lang ->
                        val isSelected = teachingLanguage == lang
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SecondaryTeal else Color.White)
                                .border(1.dp, if (isSelected) SecondaryTeal else OutlineLight, RoundedCornerShape(12.dp))
                                .clickable { onSetTeachingLanguage(lang) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lang.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 2: Educational Contexts Manager
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Classroom Contexts",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            IconButton(onClick = { showAddContextDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Context", tint = PrimaryEmerald)
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, OutlineLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                educationalContexts.forEach { ctx ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (ctx.isDefault) PrimaryContainer.copy(alpha = 0.6f) else SleekBackground)
                            .border(1.dp, if (ctx.isDefault) PrimaryEmerald.copy(alpha = 0.3f) else OutlineLight, RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(ctx.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                if (ctx.isDefault) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("DEFAULT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PrimaryEmerald)
                                }
                            }
                            Text("${ctx.grade} • ${ctx.subject} • ${ctx.topic}", fontSize = 11.sp, color = TextMuted)
                        }

                        Row {
                            if (!ctx.isDefault) {
                                IconButton(onClick = { viewModel.setDefaultContext(ctx) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Check, contentDescription = "Set Default", tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                                }
                            }
                            IconButton(onClick = { viewModel.deleteContext(ctx) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 3: Device Diagnostics & Capability Verification
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Device Speech & TTS Diagnostics",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            IconButton(onClick = { viewModel.runDiagnostics() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = PrimaryEmerald)
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, OutlineLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                diagnostics?.let { diag ->
                    DiagRow("Device Speech Recognizer", diag.isSpeechRecognitionAvailable)
                    DiagRow("English TTS Voice", diag.isEnglishTtsSupported)
                    DiagRow("Hindi TTS Voice", diag.isHindiTtsSupported)
                    DiagRow("Santhali TTS Voice (device native)", diag.isSanthaliTtsSupported, fallbackNote = "Phonetic & Roman guides enabled")
                    DiagRow("Mundari TTS Voice (device native)", diag.isMundariTtsSupported, fallbackNote = "Phonetic & Roman guides enabled")
                }
            }
        }

        // Add Context Dialog
        if (showAddContextDialog) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, OutlineLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Add Educational Context", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newContextTitle,
                        onValueChange = { newContextTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = OutlineLight
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newContextGrade,
                        onValueChange = { newContextGrade = it },
                        label = { Text("Grade") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = OutlineLight
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newContextSubject,
                        onValueChange = { newContextSubject = it },
                        label = { Text("Subject") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = OutlineLight
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newContextTopic,
                        onValueChange = { newContextTopic = it },
                        label = { Text("Topic") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = OutlineLight
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { showAddContextDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, OutlineLight)
                        ) { Text("Cancel", color = TextPrimary) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newContextTitle.isNotBlank()) {
                                    viewModel.addEducationalContext(newContextTitle, newContextGrade, newContextSubject, newContextTopic, newContextInstructions, false)
                                    showAddContextDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Add", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DiagRow(title: String, isAvailable: Boolean, fallbackNote: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            if (!isAvailable && fallbackNote != null) {
                Text(fallbackNote, fontSize = 10.sp, color = TextMuted)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isAvailable) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = null,
                tint = if (isAvailable) StateOnline else StateOffline,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isAvailable) "Available" else "Not on Device",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAvailable) StateOnline else StateOffline
            )
        }
    }
}

