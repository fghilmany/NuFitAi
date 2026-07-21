package com.fghilmany.nufitai.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fghilmany.nufitai.presentation.assessment.screen.AssessmentScreen
import com.fghilmany.nufitai.presentation.measurements.screen.MeasurementsScreen
import com.fghilmany.nufitai.presentation.parq.screen.ParqScreen
import com.fghilmany.nufitai.presentation.splash.screen.SplashScreen
import com.fghilmany.nufitai.ui.theme.PrimaryDark

@Composable
fun OnboardingNavGraph() {
    var currentScreen by remember { mutableStateOf<OnboardingScreen>(OnboardingScreen.Splash) }

    when (val screen = currentScreen) {
        is OnboardingScreen.Splash -> {
            SplashScreen(
                onSplashComplete = {
                    currentScreen = OnboardingScreen.Parq
                },
            )
        }

        is OnboardingScreen.Parq -> {
            ParqScreen(
                onCompleted = {
                    currentScreen = OnboardingScreen.Assessment
                },
            )
        }

        is OnboardingScreen.Assessment -> {
            AssessmentScreen(
                onCompleted = {
                    currentScreen = OnboardingScreen.Measurements
                },
            )
        }

        is OnboardingScreen.Measurements -> {
            MeasurementsScreen(
                onCompleted = {
                    currentScreen = OnboardingScreen.Home
                },
                onSkip = {
                    currentScreen = OnboardingScreen.Home
                },
            )
        }

        is OnboardingScreen.Home -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "🎉 Selamat Datang di NuFit AI!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDark,
                )
            }
        }
    }
}

sealed interface OnboardingScreen {
    data object Splash : OnboardingScreen
    data object Parq : OnboardingScreen
    data object Assessment : OnboardingScreen
    data object Measurements : OnboardingScreen
    data object Home : OnboardingScreen
}
