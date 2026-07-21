package com.fghilmany.nufitai.presentation.parq.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.fghilmany.nufitai.presentation.components.PrimaryButton
import com.fghilmany.nufitai.ui.theme.PrimaryDark
import com.fghilmany.nufitai.ui.theme.SurfaceWhite
import com.fghilmany.nufitai.ui.theme.TextDark

@Composable
fun ParqWarningDialog(
    isHighRisk: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    var checkboxChecked by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceWhite)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = if (isHighRisk) "Peringatan Risiko Tinggi" else "Peringatan Kesehatan",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
            )

            Text(
                text = if (isHighRisk) {
                    "Kombinasi nyeri dada dan pingsan menunjukkan risiko tinggi untuk aktivitas fisik. " +
                        "Kami sangat menyarankan Anda untuk berkonsultasi dengan dokter sebelum memulai program latihan."
                } else {
                    "Anda menjawab 'Ya' pada salah satu pertanyaan kesehatan. " +
                        "Kami sarankan untuk berkonsultasi dengan dokter terlebih dahulu untuk memastikan latihan aman bagi Anda."
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = TextDark,
                lineHeight = 24.sp,
            )

            if (!isHighRisk) {
                // Checkbox for non-high-risk
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { checkboxChecked = !checkboxChecked }
                        .padding(8.dp),
                ) {
                    Checkbox(
                        checked = checkboxChecked,
                        onCheckedChange = { checkboxChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryDark,
                        ),
                    )
                    Text(
                        text = "Saya sudah/akan konsultasi dan memahami risikonya",
                        fontSize = 14.sp,
                        color = TextDark,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                PrimaryButton(
                    text = "Lanjutkan",
                    onClick = onConfirm,
                    enabled = checkboxChecked,
                )
            } else {
                // High-risk: no proceed, just educational content
                Text(
                    text = "Untuk keamanan Anda, kami tidak dapat melanjutkan program latihan tanpa konfirmasi dokter. " +
                        "Silakan konsultasi dengan dokter Anda terlebih dahulu.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark,
                )

                PrimaryButton(
                    text = "Kembali ke Pertanyaan",
                    onClick = onDismiss,
                    enabled = true,
                )
            }
        }
    }
}
