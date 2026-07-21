package com.fghilmany.nufitai.presentation.parq.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fghilmany.nufitai.domain.onboarding.entity.parqQuestions
import com.fghilmany.nufitai.presentation.components.PrimaryButton
import com.fghilmany.nufitai.presentation.components.ProgressBar
import com.fghilmany.nufitai.presentation.parq.component.ParqBlockedScreen
import com.fghilmany.nufitai.presentation.parq.component.ParqQuestionCard
import com.fghilmany.nufitai.presentation.parq.component.ParqWarningDialog
import com.fghilmany.nufitai.presentation.parq.viewmodel.ParqEvent
import com.fghilmany.nufitai.presentation.parq.viewmodel.ParqState
import com.fghilmany.nufitai.presentation.parq.viewmodel.ParqViewModel
import com.fghilmany.nufitai.ui.theme.AccentGreen30
import com.fghilmany.nufitai.ui.theme.AccentGreen50
import com.fghilmany.nufitai.ui.theme.ButtonDisabled
import com.fghilmany.nufitai.ui.theme.PrimaryDark
import com.fghilmany.nufitai.ui.theme.SurfaceLight
import com.fghilmany.nufitai.ui.theme.SurfaceWhite
import com.fghilmany.nufitai.ui.theme.TextDark
import com.fghilmany.nufitai.ui.theme.TextDisabled
import com.fghilmany.nufitai.ui.theme.TextMuted
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParqScreen(
    onCompleted: () -> Unit,
    viewModel: ParqViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is ParqState.Loading -> {
            // Loading state
        }

        is ParqState.Questions -> {
            ParqQuestionsContent(
                answers = currentState.answers,
                answeredCount = currentState.answeredCount,
                allAnswered = currentState.allAnswered,
                onAnswer = { index, answer ->
                    viewModel.onEvent(ParqEvent.AnswerQuestion(index, answer))
                },
                onContinue = {
                    viewModel.onEvent(ParqEvent.Continue)
                },
            )
        }

        is ParqState.Warning -> {
            ParqWarningDialog(
                isHighRisk = currentState.highRisk,
                onConfirm = {
                    viewModel.onEvent(ParqEvent.Continue)
                },
                onDismiss = {
                    viewModel.onEvent(ParqEvent.DismissWarning)
                },
            )
        }

        is ParqState.Blocked -> {
            ParqBlockedScreen(
                reason = currentState.reason,
                onBackToQuestions = {
                    viewModel.onEvent(ParqEvent.DismissWarning)
                },
            )
        }

        is ParqState.Completed -> {
            onCompleted()
        }
    }
}

@Composable
private fun ParqQuestionsContent(
    answers: Map<Int, Boolean>,
    answeredCount: Int,
    allAnswered: Boolean,
    onAnswer: (Int, Boolean) -> Unit,
    onContinue: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite),
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceLight)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Back button placeholder
            Text(
                text = "←",
                fontSize = 16.sp,
                color = PrimaryDark,
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(8.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Cek Kesehatan Dulu",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = PrimaryDark,
            )
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Progress indicator
            ProgressBar(
                currentStep = answeredCount,
                totalSteps = 7,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Intro card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AccentGreen30)
                    .padding(25.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Green circle icon
                Text(
                    text = "🏥",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryDark)
                        .padding(8.dp),
                )

                Text(
                    text = "Sebelum mulai, mari pastikan latihan Anda aman. Jawab pertanyaan berikut dengan jujur agar kami bisa menyesuaikan program yang tepat untuk Anda.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextDark,
                    lineHeight = 26.sp,
                    modifier = Modifier.weight(1f),
                )
            }

            // Question cards
            parqQuestions.forEach { question ->
                ParqQuestionCard(
                    questionNumber = question.index + 1,
                    questionText = question.text,
                    selectedAnswer = answers[question.index],
                    onAnswer = { answer -> onAnswer(question.index, answer) },
                )
            }

            // Safety note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "🔒",
                    fontSize = 14.sp,
                )

                Text(
                    text = "Data Anda dijaga kerahasiaannya dan hanya digunakan untuk optimasi algoritma latihan NuFit AI.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted.copy(alpha = 0.7f),
                    lineHeight = 16.sp,
                    modifier = Modifier.weight(1f),
                )
            }

            // Bottom spacing for fixed footer
            Spacer(modifier = Modifier.height(80.dp))
        }

        // Fixed footer
        PrimaryButton(
            text = "Lanjutkan →",
            onClick = onContinue,
            enabled = allAnswered,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        )
    }
}
