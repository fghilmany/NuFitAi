package com.fghilmany.nufitai.presentation.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fghilmany.nufitai.usecase.onboarding.DetermineNextScreen
import com.fghilmany.nufitai.usecase.onboarding.OnboardingDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val determineNextScreen: DetermineNextScreen,
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // Minimum 2s splash display
            val checkJob = launch {
                val destination = determineNextScreen()
                _state.value = SplashState.Ready(destination)
            }

            delay(SPLASH_MIN_DURATION_MS)
            checkJob.join()
        }
    }

    companion object {
        private const val SPLASH_MIN_DURATION_MS = 2000L
    }
}

sealed interface SplashState {
    data object Loading : SplashState
    data class Ready(val destination: OnboardingDestination) : SplashState
}
