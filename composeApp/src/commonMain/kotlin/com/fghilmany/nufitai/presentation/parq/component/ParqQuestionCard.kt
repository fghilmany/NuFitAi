package com.fghilmany.nufitai.presentation.parq.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fghilmany.nufitai.ui.theme.ButtonBorder
import com.fghilmany.nufitai.ui.theme.PrimaryDark
import com.fghilmany.nufitai.ui.theme.SurfaceGray
import com.fghilmany.nufitai.ui.theme.SurfaceWhite
import com.fghilmany.nufitai.ui.theme.TextPrimary

@Composable
fun ParqQuestionCard(
    questionNumber: Int,
    questionText: String,
    selectedAnswer: Boolean?,
    onAnswer: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x0A000000))
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceWhite)
            .padding(25.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Question text
        Text(
            text = "$questionNumber. $questionText",
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = TextPrimary,
            lineHeight = 27.sp,
        )

        // Ya/Tidak buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Ya button
            val yaSelected = selectedAnswer == true
            val yaBg = if (yaSelected) PrimaryDark else SurfaceWhite
            val yaTextColor = if (yaSelected) SurfaceWhite else TextPrimary
            val yaBorder = if (yaSelected) PrimaryDark else ButtonBorder

            Text(
                text = "Ya",
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(yaBg)
                    .border(1.dp, yaBorder, RoundedCornerShape(12.dp))
                    .clickable { onAnswer(true) }
                    .padding(vertical = 13.dp, horizontal = 17.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = yaTextColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            // Tidak button
            val tidakSelected = selectedAnswer == false
            val tidakBg = if (tidakSelected) SurfaceGray else SurfaceWhite
            val tidakTextColor = if (tidakSelected) TextPrimary else TextPrimary
            val tidakBorder = if (tidakSelected) SurfaceGray else ButtonBorder

            Text(
                text = "Tidak",
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tidakBg)
                    .border(1.dp, tidakBorder, RoundedCornerShape(12.dp))
                    .clickable { onAnswer(false) }
                    .padding(vertical = 13.dp, horizontal = 17.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = tidakTextColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}
