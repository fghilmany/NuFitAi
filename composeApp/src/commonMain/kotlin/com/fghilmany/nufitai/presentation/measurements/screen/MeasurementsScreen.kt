package com.fghilmany.nufitai.presentation.measurements.screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fghilmany.nufitai.presentation.components.PrimaryButton
import com.fghilmany.nufitai.presentation.components.SecondaryButton
import com.fghilmany.nufitai.presentation.components.StepProgressBar
import com.fghilmany.nufitai.presentation.measurements.viewmodel.MeasurementsEvent
import com.fghilmany.nufitai.presentation.measurements.viewmodel.MeasurementsState
import com.fghilmany.nufitai.presentation.measurements.viewmodel.MeasurementsViewModel
import com.fghilmany.nufitai.ui.theme.PrimaryDark
import com.fghilmany.nufitai.ui.theme.PrimaryGreen
import com.fghilmany.nufitai.ui.theme.SurfaceWhite
import com.fghilmany.nufitai.ui.theme.TextDark
import com.fghilmany.nufitai.ui.theme.TextMuted
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MeasurementsScreen(
    onCompleted: () -> Unit,
    onSkip: () -> Unit,
    viewModel: MeasurementsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is MeasurementsState.Ready -> {
            MeasurementsContent(
                height = currentState.height,
                weight = currentState.weight,
                bmi = currentState.bmi,
                bmiCategory = currentState.bmiCategory,
                canSave = currentState.canSave,
                onUpdateHeight = { viewModel.onEvent(MeasurementsEvent.UpdateHeight(it)) },
                onUpdateWeight = { viewModel.onEvent(MeasurementsEvent.UpdateWeight(it)) },
                onSave = { viewModel.onEvent(MeasurementsEvent.Save) },
                onSkip = onSkip,
            )
        }

        is MeasurementsState.Saving -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceWhite)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "💾",
                    fontSize = 64.sp,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Menyimpan data...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryDark,
                )
            }
        }

        is MeasurementsState.Done -> {
            onCompleted()
        }
    }
}

@Composable
private fun MeasurementsContent(
    height: String,
    weight: String,
    bmi: Double?,
    bmiCategory: String?,
    canSave: Boolean,
    onUpdateHeight: (String) -> Unit,
    onUpdateWeight: (String) -> Unit,
    onSave: () -> Unit,
    onSkip: () -> Unit,
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "←",
                fontSize = 16.sp,
                color = PrimaryDark,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Data Tubuh",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
            )
        }

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Progress bar
            StepProgressBar(
                currentStep = 5,
                totalSteps = 5,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Data tubuh (opsional)",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
                lineHeight = 36.sp,
            )

            Text(
                text = "Isi data ini untuk perhitungan BMI yang lebih akurat. Kamu bisa melewatkannya.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = TextDark,
                lineHeight = 24.sp,
            )

            // Height input
            OutlinedTextField(
                value = height,
                onValueChange = onUpdateHeight,
                label = { Text("Tinggi badan (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = TextMuted,
                ),
            )

            // Weight input
            OutlinedTextField(
                value = weight,
                onValueChange = onUpdateWeight,
                label = { Text("Berat badan (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = TextMuted,
                ),
            )

            // BMI display
            if (bmi != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (bmiCategory == "Normal") PrimaryGreen.copy(alpha = 0.1f)
                            else PrimaryDark.copy(alpha = 0.1f),
                            RoundedCornerShape(16.dp),
                        )
                        .padding(16.dp),
                ) {
                    val bmiRounded = kotlin.math.round(bmi * 10) / 10
                    Text(
                        text = "BMI: $bmiRounded",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDark,
                    )
                    Text(
                        text = "Kategori: $bmiCategory",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark,
                    )
                }
            }
        }

        // Footer buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PrimaryButton(
                text = "Simpan",
                onClick = onSave,
                enabled = canSave,
            )

            SecondaryButton(
                text = "Lewati",
                onClick = onSkip,
                enabled = true,
            )
        }
    }
}
