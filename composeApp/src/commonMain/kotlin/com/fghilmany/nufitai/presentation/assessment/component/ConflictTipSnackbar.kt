package com.fghilmany.nufitai.presentation.assessment.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fghilmany.nufitai.ui.theme.AccentGreen10
import com.fghilmany.nufitai.ui.theme.AccentGreen20
import com.fghilmany.nufitai.ui.theme.TextDark

@Composable
fun ConflictTipSnackbar(
    message: String?,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        if (message != null) {
            Text(
                text = "💡 $message",
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentGreen10)
                    .padding(16.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark,
                lineHeight = 20.sp,
            )
        }
    }
}
