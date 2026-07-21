package com.fghilmany.nufitai.app

import androidx.compose.runtime.Composable
import com.fghilmany.nufitai.navigation.OnboardingNavGraph
import com.fghilmany.nufitai.ui.theme.NuFitAITheme
import org.koin.compose.KoinContext

@Composable
actual fun NuFitAIApp() {
    KoinContext {
        NuFitAITheme {
            OnboardingNavGraph()
        }
    }
}
