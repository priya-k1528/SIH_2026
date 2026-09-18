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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.data.model.QuizEvaluationResult
import com.example.data.model.QuizQuestion
import com.example.data.model.StudentAnswer
import com.example.ui.theme.EducationalBlue
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
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val topic by viewModel.topic.collectAsState()
    val grade by viewModel.grade.collectAsState()
    val activeQuestions by viewModel.activeQuestions.collectAsState()
    val studentAnswers by viewModel.studentAnswers.collectAsState()
    val quizEvaluation by viewModel.quizEvaluation.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val activeListeningQuestionId by viewModel.activeListeningQuestionId.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    statusMessage?.let { onShowSnackbar(it) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Interactive Quiz & Evaluation",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Students can answer using Text or Real Voice Input (Hindi)",
            fontSize = 13.sp,
            color = TextMuted,
            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
        )

        // Creator & Generator Controls
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, OutlineLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                OutlinedTextField(
                    value = topic,
                    onValueChange = { viewModel.setTopic(it) },
                    label = { Text("Quiz Topic") },
                    placeholder = { Text("e.g., Counting 1 to 5 / Forest Animals") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = OutlineLight
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { viewModel.generateQuiz() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("gen_quiz_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isGenerating
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Quiz Questions", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quiz Evaluation Report Card if submitted
        quizEvaluation?.let { eval ->
            EvaluationReportCard(
                evaluation = eval,
                onReset = { viewModel.resetQuiz() },
                onSave = { viewModel.saveQuiz() }
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Active Questions List
        if (activeQuestions.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, OutlineLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_questions_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "STUDENT QUESTIONS (${activeQuestions.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = PrimaryEmerald
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    activeQuestions.forEachIndexed { index, q ->
                        val currentAnswer = studentAnswers[q.id]?.answeredText ?: ""
                        val isListeningThis = activeListeningQuestionId == q.id

                        QuestionItemRow(
                            index = index + 1,
                            question = q,
                            currentAnswer = currentAnswer,
                            isListening = isListeningThis,
                            onAnswerChanged = { ans -> viewModel.setStudentAnswer(q.id, ans) },
                            onStartVoice = { viewModel.startVoiceAnswering(q.id) },
                            onStopVoice = { viewModel.stopVoiceAnswering() }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.submitQuiz() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_quiz_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Submit Answers for Grading", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun QuestionItemRow(
    index: Int,
    question: QuizQuestion,
    currentAnswer: String,
    isListening: Boolean,
    onAnswerChanged: (String) -> Unit,
    onStartVoice: () -> Unit,
    onStopVoice: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SleekBackground)
            .border(1.dp, OutlineLight, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(
            text = "Q$index. ${question.questionText}",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (question.questionType == "MCQ" && question.options.isNotEmpty()) {
            // Radio Button Options
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                question.options.forEach { opt ->
                    val selected = currentAnswer == opt
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) PrimaryContainer else Color.Transparent)
                            .clickable { onAnswerChanged(opt) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        RadioButton(
                            selected = selected,
                            onClick = { onAnswerChanged(opt) },
                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryEmerald)
                        )
                        Text(
                            text = opt,
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = TextPrimary
                        )
                    }
                }
            }
        } else {
            // Fill in the blank / Short answer with dedicated Voice Input button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = currentAnswer,
                    onValueChange = onAnswerChanged,
                    label = { Text("Student Answer") },
                    placeholder = { Text("Type or use mic...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = OutlineLight
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Microphone Button beside answer field
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isListening) Color(0xFFDC2626) else PrimaryEmerald)
                ) {
                    IconButton(onClick = { if (isListening) onStopVoice() else onStartVoice() }) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Voice Answer",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EvaluationReportCard(
    evaluation: QuizEvaluationResult,
    onReset: () -> Unit,
    onSave: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, OutlineLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quiz_evaluation_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 QUIZ EVALUATION REPORT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = PrimaryEmerald
                )

                Row {
                    IconButton(onClick = onSave) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Save Result", tint = PrimaryEmerald)
                    }
                    IconButton(onClick = onReset) {
                        Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = PrimaryEmerald)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Score Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScoreMetric("Score", "${evaluation.earnedMarks}/${evaluation.totalMarks}", PrimaryEmerald)
                ScoreMetric("Percentage", "${evaluation.percentage.toInt()}%", if (evaluation.passed) StateOnline else StateOffline)
                ScoreMetric("Correct", "${evaluation.correctCount}", StateOnline)
                ScoreMetric("Wrong", "${evaluation.wrongCount}", Color(0xFFDC2626))
                ScoreMetric("Unanswered", "${evaluation.unansweredCount}", TextMuted)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Detailed Question Breakdown:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

            Spacer(modifier = Modifier.height(8.dp))

            evaluation.questionResults.forEachIndexed { i, r ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SleekBackground)
                        .border(1.dp, OutlineLight, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Q${i + 1}. ${r.questionText}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
                        Text(
                            text = if (r.isCorrect) "✓ Correct (+${r.marksEarned})" else if (r.isUnanswered) "○ Unanswered" else "✗ Wrong",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (r.isCorrect) StateOnline else Color(0xFFDC2626)
                        )
                    }
                    Text("Your Answer: ${r.studentAnswer}", fontSize = 11.sp, color = TextSecondary)
                    if (!r.isCorrect) {
                        Text("Correct Answer: ${r.correctAnswer}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PrimaryEmerald)
                    }
                    if (r.explanation.isNotBlank()) {
                        Text("Note: ${r.explanation}", fontSize = 10.sp, color = TextMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 10.sp, color = TextMuted)
    }
}

