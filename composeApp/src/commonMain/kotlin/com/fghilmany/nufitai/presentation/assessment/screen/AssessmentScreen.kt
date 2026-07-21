package com.fghilmany.nufitai.presentation.assessment.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.fghilmany.nufitai.presentation.assessment.component.AssessmentOptionCard
import com.fghilmany.nufitai.presentation.assessment.component.ConflictTipSnackbar
import com.fghilmany.nufitai.presentation.assessment.viewmodel.AssessmentEvent
import com.fghilmany.nufitai.presentation.assessment.viewmodel.AssessmentOption
import com.fghilmany.nufitai.presentation.assessment.viewmodel.AssessmentState
import com.fghilmany.nufitai.presentation.assessment.viewmodel.AssessmentViewModel
import com.fghilmany.nufitai.presentation.components.AiTipCard
import com.fghilmany.nufitai.presentation.components.PrimaryButton
import com.fghilmany.nufitai.presentation.components.StepProgressBar
import com.fghilmany.nufitai.ui.theme.PrimaryDark
import com.fghilmany.nufitai.ui.theme.SurfaceWhite
import com.fghilmany.nufitai.ui.theme.TextDark
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AssessmentScreen(
    onCompleted: () -> Unit,
    viewModel: AssessmentViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is AssessmentState.Step -> {
            AssessmentStepContent(
                currentStep = currentState.currentStep,
                totalSteps = currentState.totalSteps,
                selectedOption = currentState.answers[currentState.currentStep],
                conflictTip = currentState.conflictTip,
                splitReason = currentState.splitReason,
                canContinue = currentState.canContinue,
                onSelectOption = { option ->
                    viewModel.onEvent(AssessmentEvent.SelectAnswer(currentState.currentStep, option))
                },
                onGoBack = {
                    viewModel.onEvent(AssessmentEvent.GoBack)
                },
                onContinue = {
                    viewModel.onEvent(AssessmentEvent.Continue)
                },
            )
        }

        is AssessmentState.Generating -> {
            // Loading state for plan generation
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceWhite)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "⏳",
                    fontSize = 64.sp,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Membuat rencana latihan...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryDark,
                )
            }
        }

        is AssessmentState.Done -> {
            onCompleted()
        }

        is AssessmentState.Error -> {
            // Error state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceWhite)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "❌",
                    fontSize = 64.sp,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = currentState.failure.message,
                    fontSize = 16.sp,
                    color = PrimaryDark,
                )
            }
        }
    }
}

@Composable
private fun AssessmentStepContent(
    currentStep: Int,
    totalSteps: Int,
    selectedOption: String?,
    conflictTip: String?,
    splitReason: String?,
    canContinue: Boolean,
    onSelectOption: (AssessmentOption) -> Unit,
    onGoBack: () -> Unit,
    onContinue: () -> Unit,
) {
    val steps = listOf(
        StepConfig(
            question = "Seberapa pengalaman kamu di gym?",
            subtitle = "Pilih yang paling menggambarkan pengalamanmu saat ini.",
            options = listOf(
                AssessmentOption.BEGINNER to OptionConfig("🌱", "Belum pernah", "Baru memulai perjalanan fitness"),
                AssessmentOption.INTERMEDIATE to OptionConfig("💪", "< 1 tahun", "Sudah rutin tapi masih baru"),
                AssessmentOption.ADVANCED to OptionConfig("🏆", "Rutin bertahun-tahun", "Sudah berpengalaman di gym"),
            ),
        ),
        StepConfig(
            question = "Apa tujuan utamamu?",
            subtitle = "Pilih satu agar NuFit AI dapat merancang rencana yang tepat untukmu.",
            options = listOf(
                AssessmentOption.LOSE_WEIGHT to OptionConfig("🔥", "Turun berat badan", "Bakar lemak secara efisien"),
                AssessmentOption.BUILD_MUSCLE to OptionConfig("💪", "Bentuk otot", "Tingkatkan massa dan definisi"),
                AssessmentOption.HEALTH_FIT to OptionConfig("❤️", "Sehat & bugar", "Stamina untuk kegiatan harian"),
                AssessmentOption.GET_STRONG to OptionConfig("⚡", "Jadi lebih kuat", "Fokus pada kekuatan fungsional"),
            ),
        ),
        StepConfig(
            question = "Seberapa sering kamu mau latihan?",
            subtitle = "Pilih frekuensi yang realistis untuk jadwalmu.",
            options = listOf(
                AssessmentOption.TWO_TO_THREE to OptionConfig("📅", "2-3x per minggu", "Cocok untuk pemula atau sibuk"),
                AssessmentOption.FOUR_TO_FIVE to OptionConfig("🔥", "4-5x per minggu", "Kompleks lebih tinggi, hasil lebih cepat"),
            ),
        ),
        StepConfig(
            question = "Mau pakai split latihan apa?",
            subtitle = "Pilih preferensimu atau biarkan kami yang memilihkan.",
            options = listOf(
                AssessmentOption.UPPER_LOWER to OptionConfig("🔀", "Upper/Lower", "Fokus per bagian tubuh"),
                AssessmentOption.FULL_BODY to OptionConfig("🏋️", "Full Body", "Latihan seluruh tubuh setiap sesi"),
                AssessmentOption.TIDAK_TAHU to OptionConfig("🤔", "Tidak tahu", "Bantu pilihkan yang terbaik"),
            ),
        ),
    )

    val step = steps[currentStep]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite),
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (currentStep > 0) {
                Text(
                    text = "←",
                    fontSize = 16.sp,
                    color = PrimaryDark,
                    modifier = Modifier
                        .clip(CircleShape)
                        .padding(8.dp)
                        .then(
                            Modifier.run {
                                this@run
                            },
                        ),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Quick Assessment",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
            )
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Progress bar
            StepProgressBar(
                currentStep = currentStep + 1,
                totalSteps = totalSteps,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Question
            Text(
                text = step.question,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
                lineHeight = 36.sp,
            )

            Text(
                text = step.subtitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = TextDark,
                lineHeight = 24.sp,
            )

            // Options
            step.options.forEach { (option, config) ->
                AssessmentOptionCard(
                    icon = config.icon,
                    title = config.title,
                    subtitle = config.subtitle,
                    isSelected = selectedOption == option.name,
                    onClick = { onSelectOption(option) },
                )
            }

            // Conflict tip
            ConflictTipSnackbar(message = conflictTip)

            // Split reason (if auto-selected)
            if (splitReason != null) {
                AiTipCard(
                    message = splitReason,
                )
            }

            // AI Tip card (static per Figma)
            if (currentStep == 1) {
                AiTipCard(
                    message = "Jangan khawatir, kamu bisa mengubah tujuan ini kapan saja nanti. Kita mulai dari yang paling penting bagimu saat ini!",
                )
            }

            // Bottom spacing for fixed footer
            Spacer(modifier = Modifier.height(80.dp))
        }

        // Fixed footer
        PrimaryButton(
            text = "Lanjutkan",
            onClick = onContinue,
            enabled = canContinue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        )
    }
}

private data class StepConfig(
    val question: String,
    val subtitle: String,
    val options: List<Pair<AssessmentOption, OptionConfig>>,
)

private data class OptionConfig(
    val icon: String,
    val title: String,
    val subtitle: String,
)
