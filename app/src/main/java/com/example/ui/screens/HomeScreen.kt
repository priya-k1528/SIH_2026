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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.dictionary.VernacularDictionary
import com.example.data.model.EducationalContext
import com.example.data.model.Language
import com.example.ui.components.NetworkStatusBadge
import com.example.ui.theme.BorderEmerald
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.PrimaryEmeraldDark
import com.example.ui.theme.PrimaryEmeraldDeep
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.TertiaryAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.ToolBlueBg
import com.example.ui.theme.ToolBlueFg
import com.example.ui.theme.ToolOrangeBg
import com.example.ui.theme.ToolOrangeFg
import com.example.ui.theme.ToolPurpleBg
import com.example.ui.theme.ToolPurpleFg
import com.example.ui.theme.ToolRoseBg
import com.example.ui.theme.ToolRoseFg
import com.example.ui.theme.ToolSlateBg
import com.example.ui.theme.ToolSlateFg
import com.example.ui.theme.ToolTealBg
import com.example.ui.theme.ToolTealFg
import com.example.ui.viewmodel.Screen

@Composable
fun HomeScreen(
    appLanguage: Language,
    teachingLanguage: Language,
    isOfflineMode: Boolean,
    onToggleOffline: () -> Unit,
    activeContext: EducationalContext?,
    onNavigate: (Screen) -> Unit,
    onSpeakPhrase: (String, Language) -> Unit,
    modifier: Modifier = Modifier
) {
    val dailyPhrase = VernacularDictionary.entries.first()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
    ) {
        // Sleek White Header with Avatar & Online Status Badge
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Teacher Avatar & Profile
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(PrimaryEmerald)
                        ) {
                            Text(
                                text = "A",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column {
                            Text(
                                text = "SUPRABHAT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "Anjali Kumari",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    // Online Status Badge
                    NetworkStatusBadge(
                        isOfflineMode = isOfflineMode,
                        onToggle = onToggleOffline
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Language & Grade Filter Chips Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryEmerald)
                                .clickable { onNavigate(Screen.Translate) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${teachingLanguage.displayName} → Santhali",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, OutlineLight, RoundedCornerShape(12.dp))
                                .clickable { onNavigate(Screen.Learn) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = activeContext?.grade ?: "Grade 3",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, OutlineLight, RoundedCornerShape(12.dp))
                                .clickable { onNavigate(Screen.Materials) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Classroom Bridge",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Main Scrollable Dashboard Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Sleek Hero Banner: Real-Time Voice Translation
            item {
                Card(
                    onClick = { onNavigate(Screen.Voice) },
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_hero_voice_btn")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(PrimaryEmeraldDark, PrimaryEmeraldDeep)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        // Subtle Watermark Mic Icon in background
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.10f),
                            modifier = Modifier
                                .size(96.dp)
                                .align(Alignment.TopEnd)
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Ready to Speak?",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Real-time voice translation active. Speak in Hindi or English.",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Row {
                                Button(
                                    onClick = { onNavigate(Screen.Voice) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFF065F46)
                                    ),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = Color(0xFF065F46),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Start Voice",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Teacher Tools Section Header
            item {
                Text(
                    text = "TEACHER TOOLS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(start = 2.dp, end = 2.dp, top = 2.dp)
                )
            }

            // Row 1: Worksheets & Interactive Quiz
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SleekToolCard(
                        title = "Worksheets",
                        caption = "Generate for Class",
                        icon = Icons.Default.Description,
                        iconContainerColor = ToolOrangeBg,
                        iconTintColor = ToolOrangeFg,
                        onClick = { onNavigate(Screen.Worksheets) },
                        modifier = Modifier.weight(1f),
                        testTag = "home_mod_worksheets"
                    )

                    SleekToolCard(
                        title = "Interactive Quiz",
                        caption = "Voice Assessment",
                        icon = Icons.Default.Quiz,
                        iconContainerColor = ToolBlueBg,
                        iconTintColor = ToolBlueFg,
                        onClick = { onNavigate(Screen.Quiz) },
                        modifier = Modifier.weight(1f),
                        testTag = "home_mod_quiz"
                    )
                }
            }

            // Row 2: Flashcards & Curriculum Lessons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SleekToolCard(
                        title = "Flashcards",
                        caption = "Vernacular Images",
                        icon = Icons.Default.Style,
                        iconContainerColor = ToolPurpleBg,
                        iconTintColor = ToolPurpleFg,
                        onClick = { onNavigate(Screen.Flashcards) },
                        modifier = Modifier.weight(1f),
                        testTag = "home_mod_flashcards"
                    )

                    SleekToolCard(
                        title = "Lessons",
                        caption = "Generate Curriculum",
                        icon = Icons.Default.School,
                        iconContainerColor = ToolRoseBg,
                        iconTintColor = ToolRoseFg,
                        onClick = { onNavigate(Screen.Learn) },
                        modifier = Modifier.weight(1f),
                        testTag = "home_mod_learn"
                    )
                }
            }

            // Row 3: Text Translate & Materials Library
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SleekToolCard(
                        title = "Text Translate",
                        caption = "Dictionary & AI",
                        icon = Icons.Default.Translate,
                        iconContainerColor = ToolTealBg,
                        iconTintColor = ToolTealFg,
                        onClick = { onNavigate(Screen.Translate) },
                        modifier = Modifier.weight(1f),
                        testTag = "home_mod_translate"
                    )

                    SleekToolCard(
                        title = "Materials",
                        caption = "Saved Curriculum",
                        icon = Icons.Default.Folder,
                        iconContainerColor = ToolSlateBg,
                        iconTintColor = ToolSlateFg,
                        onClick = { onNavigate(Screen.Materials) },
                        modifier = Modifier.weight(1f),
                        testTag = "home_mod_materials"
                    )
                }
            }

            // Current Topic Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, OutlineLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_context_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CURRENT TOPIC",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSubtle,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = activeContext?.topic ?: "Plants & Ecosystems",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryContainer)
                                .clickable { onNavigate(Screen.Learn) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Change",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryEmerald
                            )
                        }
                    }
                }
            }

            // Daily Classroom Phrase Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, OutlineLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CLASSROOM PHRASE OF THE DAY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = TertiaryAmber
                            )
                            IconButton(
                                onClick = { onSpeakPhrase(dailyPhrase.hindi, Language.HINDI) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Listen",
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${dailyPhrase.emoji} \"${dailyPhrase.hindi}\" (${dailyPhrase.english})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SleekBackground)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "ᱥᱟᱱᱛᱟᱲᱤ (Santhali)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = dailyPhrase.santhaliOlChiki,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = dailyPhrase.santhaliRoman,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SleekBackground)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "मुंडारी (Mundari)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryTeal
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = dailyPhrase.mundariDevanagari,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = dailyPhrase.mundariRoman,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun SleekToolCard(
    title: String,
    caption: String,
    icon: ImageVector,
    iconContainerColor: Color,
    iconTintColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, OutlineLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconContainerColor)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTintColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = caption,
                fontSize = 11.sp,
                color = TextSubtle
            )
        }
    }
}

