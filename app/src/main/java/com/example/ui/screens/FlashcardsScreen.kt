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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flip
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.TertiaryAmber
import com.example.ui.theme.TertiaryContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FlashcardViewModel

@Composable
fun FlashcardsScreen(
    viewModel: FlashcardViewModel,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val isFlipped by viewModel.isFlipped.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    val topic by viewModel.topic.collectAsState()
    val generateCategory by viewModel.generateCategory.collectAsState()
    val cardCount by viewModel.cardCount.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generatedCards by viewModel.generatedCards.collectAsState()

    // Recomputed whenever the saved deck, the category filter, or a fresh
    // generation preview changes, so the canvas always reflects the latest state.
    val allFlashcards by viewModel.allFlashcards.collectAsState()
    val isPreviewing = generatedCards.isNotEmpty()
    val displayCards = if (isPreviewing) generatedCards else viewModel.getFilteredCards()
    val currentCard = if (displayCards.isNotEmpty() && currentIndex < displayCards.size) displayCards[currentIndex] else null

    var showAddDialog by remember { mutableStateOf(false) }
    var newEnglish by remember { mutableStateOf("") }
    var newHindi by remember { mutableStateOf("") }
    var newSanthaliOl by remember { mutableStateOf("") }
    var newSanthaliRom by remember { mutableStateOf("") }
    var newMundariDev by remember { mutableStateOf("") }
    var newMundariRom by remember { mutableStateOf("") }
    var newEmoji by remember { mutableStateOf("📚") }

    statusMessage?.let { onShowSnackbar(it) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Multilingual Flashcards",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "AI-Generated Mother Tongue Vocabulary Deck",
                    fontSize = 13.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Card", tint = PrimaryEmerald)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Generation Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, OutlineLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Generate Flashcards", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Enter a topic and get instant multilingual flashcards",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = topic,
                    onValueChange = { viewModel.setTopic(it) },
                    label = { Text("Flashcard Topic") },
                    placeholder = { Text("e.g., Fruits, Forest Animals, Classroom Objects") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("flashcard_topic_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = OutlineLight
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Category", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                    items(viewModel.generateCategories) { cat ->
                        val selected = generateCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) PrimaryEmerald else Color.White)
                                .border(1.dp, if (selected) PrimaryEmerald else OutlineLight, RoundedCornerShape(12.dp))
                                .clickable { viewModel.setGenerateCategory(cat) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text("Number of Cards", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                    items(listOf(4, 6, 8, 10, 12)) { count ->
                        val selected = cardCount == count
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) PrimaryEmerald else Color.White)
                                .border(1.dp, if (selected) PrimaryEmerald else OutlineLight, RoundedCornerShape(12.dp))
                                .clickable { viewModel.setCardCount(count) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "$count",
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.generateFlashcards() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("flashcard_generate_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isGenerating
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Flashcards", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preview banner (shown only for freshly generated, not-yet-saved cards)
        if (isPreviewing) {
            Card(
                colors = CardDefaults.cardColors(containerColor = TertiaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Preview: ${generatedCards.size} new cards (not saved yet)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TertiaryAmber,
                        modifier = Modifier.weight(1f)
                    )
                    Row {
                        IconButton(
                            onClick = { viewModel.saveGeneratedCards() },
                            modifier = Modifier.testTag("flashcard_save_generated_btn")
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = "Save to Deck", tint = PrimaryEmerald)
                        }
                        IconButton(
                            onClick = { viewModel.discardGeneratedCards() },
                            modifier = Modifier.testTag("flashcard_discard_generated_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Discard Preview", tint = TextMuted)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Categories Row (browsing the saved deck)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            items(viewModel.categories) { cat ->
                val isSelected = !isPreviewing && selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PrimaryEmerald else Color.White)
                        .border(1.dp, if (isSelected) PrimaryEmerald else OutlineLight, RoundedCornerShape(12.dp))
                        .clickable { viewModel.selectCategory(cat) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Big Interactive Card Canvas
        if (currentCard != null) {
            Text(
                text = if (isPreviewing)
                    "Preview Card ${currentIndex + 1} of ${displayCards.size} • Category: ${currentCard.category}"
                else
                    "Card ${currentIndex + 1} of ${displayCards.size} • Category: ${currentCard.category}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                onClick = { viewModel.flipCard() },
                colors = CardDefaults.cardColors(
                    containerColor = if (isFlipped) PrimaryContainer else Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, if (isFlipped) PrimaryEmerald.copy(alpha = 0.3f) else OutlineLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .testTag("flashcard_canvas")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Emoji / Visual Asset
                    Text(
                        text = currentCard.iconEmoji,
                        fontSize = 54.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isFlipped) {
                        // FRONT SIDE: Hindi & English
                        Text(
                            text = currentCard.hindi,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryEmerald,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = currentCard.english,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Flip, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tap card to reveal Santhali & Mundari", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = PrimaryEmerald)
                        }
                    } else {
                        // BACK SIDE: Santhali (Ol Chiki + Roman) & Mundari (Devanagari + Roman)
                        Text(
                            text = "ᱥᱟᱱᱛᱟᱲᱤ: ${currentCard.santhaliOlChiki}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryEmerald,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Santhali: ${currentCard.santhaliRoman}",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "मुंडारी: ${currentCard.mundariDevanagari}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryTeal,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Mundari: ${currentCard.mundariRoman}",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.speak(currentCard.hindi, Language.HINDI) },
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, OutlineLight),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Hindi", fontSize = 11.sp, color = TextPrimary)
                            }

                            OutlinedButton(
                                onClick = { viewModel.speak(currentCard.english, Language.ENGLISH) },
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, OutlineLight),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("English", fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Controls (Previous, Next)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { viewModel.previousCard() },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, OutlineLight),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    modifier = Modifier.testTag("flashcard_prev_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous", tint = TextPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Previous", color = TextPrimary)
                }

                Button(
                    onClick = { viewModel.flipCard() },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                ) {
                    Icon(Icons.Default.Flip, contentDescription = "Flip", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Flip Card", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { viewModel.nextCard() },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, OutlineLight),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    modifier = Modifier.testTag("flashcard_next_btn")
                ) {
                    Text("Next", color = TextPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = TextPrimary)
                }
            }
        } else {
            // Empty state: no cards in this category and nothing generated yet
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, OutlineLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("flashcard_empty_state")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🗂️", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No flashcards here yet",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Generate a set above, pick another category, or add your own card.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Add Custom Flashcard Dialog
        if (showAddDialog) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, OutlineLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Create Custom Vernacular Card", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newHindi,
                        onValueChange = { newHindi = it },
                        label = { Text("Hindi Word") },
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
                        value = newEnglish,
                        onValueChange = { newEnglish = it },
                        label = { Text("English Word") },
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
                        value = newSanthaliOl,
                        onValueChange = { newSanthaliOl = it },
                        label = { Text("Santhali (Ol Chiki)") },
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
                        value = newMundariDev,
                        onValueChange = { newMundariDev = it },
                        label = { Text("Mundari (Devanagari)") },
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
                            onClick = { showAddDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, OutlineLight)
                        ) { Text("Cancel", color = TextPrimary) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newHindi.isNotBlank() && newEnglish.isNotBlank()) {
                                    viewModel.addCustomFlashcard("Custom", newEnglish, newHindi, newSanthaliOl, newSanthaliRom, newMundariDev, newMundariRom, newEmoji)
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
