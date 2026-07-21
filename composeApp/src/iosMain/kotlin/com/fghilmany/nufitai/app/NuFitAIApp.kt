package com.fghilmany.nufitai.app

import androidx.compose.runtime.Composable
import com.fghilmany.nufitai.di.appModule
import com.fghilmany.nufitai.di.onboardingModule
import com.fghilmany.nufitai.di.platformModule
import com.fghilmany.nufitai.navigation.OnboardingNavGraph
import com.fghilmany.nufitai.ui.theme.NuFitAITheme
import org.koin.compose.KoinApplication

@Composable
actual fun NuFitAIApp() {
    KoinApplication(
        application = {
            modules(platformModule, appModule, onboardingModule)
        },
    ) {
        NuFitAITheme {
            OnboardingNavGraph()
        }
    }
}
