package com.fghilmany.nufitai.presentation.parq.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fghilmany.nufitai.presentation.components.PrimaryButton
import com.fghilmany.nufitai.ui.theme.PrimaryDark
import com.fghilmany.nufitai.ui.theme.SurfaceWhite
import com.fghilmany.nufitai.ui.theme.TextDark
import com.fghilmany.nufitai.ui.theme.WarningRed

@Composable
fun ParqBlockedScreen(
    reason: String,
    onBackToQuestions: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Warning icon
        Text(
            text = "⚠️",
            fontSize = 64.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Konsultasi Dokter Diperlukan",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = WarningRed,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = reason,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = TextDark,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Kombinasi nyeri dada dan pingsan merupakan indikator risiko tinggi untuk aktivitas fisik intensitas menengah hingga tinggi.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = TextDark,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Kembali ke Pertanyaan",
            onClick = onBackToQuestions,
            enabled = true,
        )
    }
}
