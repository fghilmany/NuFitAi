package com.fghilmany.nufitai.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fghilmany.nufitai.ui.theme.PrimaryDark
import com.fghilmany.nufitai.ui.theme.PrimaryGreen
import com.fghilmany.nufitai.ui.theme.SurfaceGray
import com.fghilmany.nufitai.ui.theme.TextDark
import com.fghilmany.nufitai.ui.theme.TextMuted

@Composable
fun ProgressBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    height: Int = 6,
) {
    val progress = (currentStep.toFloat() / totalSteps).coerceIn(0f, 1f)

    Column(modifier = modifier) {
        // Step label row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "LANGKAH $currentStep DARI $totalSteps",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted,
                letterSpacing = 0.6.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryDark,
            )
        }

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp)
                .clip(RoundedCornerShape(9999.dp))
                .background(SurfaceGray),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(height.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(PrimaryGreen),
            )
        }
    }
}

@Composable
fun StepProgressBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    val progress = (currentStep.toFloat() / totalSteps).coerceIn(0f, 1f)
    val percentage = ((currentStep.toFloat() / totalSteps) * 100).toInt()

    Column(modifier = modifier) {
        // Step label row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Langkah $currentStep dari $totalSteps",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "$percentage%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
            )
        }

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(9999.dp))
                .background(SurfaceGray),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(12.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(PrimaryGreen),
            )
        }
    }
}
