package com.fghilmany.nufitai.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fghilmany.nufitai.ui.theme.AccentGreen10
import com.fghilmany.nufitai.ui.theme.AccentGreen20
import com.fghilmany.nufitai.ui.theme.PrimaryDark
import com.fghilmany.nufitai.ui.theme.TextDark

@Composable
fun AiTipCard(
    message: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AccentGreen10)
            .border(1.dp, AccentGreen20, RoundedCornerShape(12.dp))
            .padding(17.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
    ) {
        // Avatar circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PrimaryDark),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "🤖",
                fontSize = 18.sp,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = "NuFit AI Mentor",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryDark,
                letterSpacing = 0.14.sp,
            )

            Text(
                text = "\"$message\"",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = TextDark,
                lineHeight = 24.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
