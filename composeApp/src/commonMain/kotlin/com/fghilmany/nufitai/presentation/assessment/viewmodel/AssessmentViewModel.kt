package com.fghilmany.nufitai.presentation.assessment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fghilmany.nufitai.core.error.Failure
import com.fghilmany.nufitai.domain.onboarding.entity.GymExperience
import com.fghilmany.nufitai.domain.onboarding.entity.FitnessGoal
import com.fghilmany.nufitai.domain.onboarding.entity.UserProfile
import com.fghilmany.nufitai.domain.onboarding.entity.WorkoutFrequency
import com.fghilmany.nufitai.domain.onboarding.entity.WorkoutSplit
import com.fghilmany.nufitai.domain.onboarding.repository.OnboardingRepository
import com.fghilmany.nufitai.usecase.onboarding.GenerateMonthlyPlan
import com.fghilmany.nufitai.usecase.onboarding.ResolveSplit
import com.fghilmany.nufitai.core.util.currentTimestamp
import com.fghilmany.nufitai.core.util.generateUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssessmentViewModel(
    private val repo: OnboardingRepository,
    private val resolveSplit: ResolveSplit,
    private val generatePlan: GenerateMonthlyPlan,
) : ViewModel() {

    private val _state = MutableStateFlow<AssessmentState>(
        AssessmentState.Step(
            currentStep = 0,
            answers = emptyMap(),
            canContinue = false,
        ),
    )
    val state: StateFlow<AssessmentState> = _state.asStateFlow()

    private val answers = mutableMapOf<Int, String>()

    fun onEvent(event: AssessmentEvent) {
        when (event) {
            is AssessmentEvent.SelectAnswer -> handleSelectAnswer(event.step, event.option)
            is AssessmentEvent.GoBack -> handleGoBack()
            is AssessmentEvent.Continue -> handleContinue()
            is AssessmentEvent.SkipBodyMeasurements -> handleSkipMeasurements()
        }
    }

    private fun handleSelectAnswer(step: Int, option: AssessmentOption) {
        answers[step] = option.name

        val currentState = _state.value as? AssessmentState.Step ?: return

        // If this is the split step (3), resolve the split
        if (step == 3) {
            val split = WorkoutSplit.valueOf(option.name)
            val experience = answers[0]?.let { GymExperience.valueOf(it) } ?: GymExperience.BEGINNER
            val frequency = answers[2]?.let { WorkoutFrequency.valueOf(it) } ?: WorkoutFrequency.TWO_TO_THREE

            val resolved = resolveSplit(split, experience, frequency)

            _state.value = currentState.copy(
                answers = answers.toMap(),
                resolvedSplit = resolved.split,
                splitReason = resolved.reason,
                conflictTip = resolved.conflictTip,
                canContinue = true,
            )
        } else {
            _state.value = currentState.copy(
                answers = answers.toMap(),
                canContinue = true,
            )
        }
    }

    private fun handleGoBack() {
        val currentState = _state.value as? AssessmentState.Step ?: return
        if (currentState.currentStep > 0) {
            val prevStep = currentState.currentStep - 1
            _state.value = currentState.copy(
                currentStep = prevStep,
                canContinue = answers.containsKey(prevStep),
            )
        }
    }

    private fun handleContinue() {
        val currentState = _state.value as? AssessmentState.Step ?: return

        if (currentState.currentStep < 3) {
            // Move to next step
            val nextStep = currentState.currentStep + 1
            _state.value = currentState.copy(
                currentStep = nextStep,
                canContinue = answers.containsKey(nextStep),
            )
        } else {
            // Complete assessment
            viewModelScope.launch {
                _state.value = AssessmentState.Generating

                when (val result = saveProfile()) {
                    is com.fghilmany.nufitai.core.error.AppResult.Error -> {
                        _state.value = AssessmentState.Error(result.failure)
                    }
                    is com.fghilmany.nufitai.core.error.AppResult.Success -> {
                        when (val planResult = generatePlan(result.data)) {
                            is com.fghilmany.nufitai.core.error.AppResult.Error -> {
                                _state.value = AssessmentState.Error(planResult.failure)
                            }
                            is com.fghilmany.nufitai.core.error.AppResult.Success -> {
                                // TODO: Store templateId with profile
                                _state.value = AssessmentState.Done(result.data)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleSkipMeasurements() {
        val currentState = _state.value as? AssessmentState.Done ?: return
        _state.value = AssessmentState.Done(
            profile = currentState.profile,
        )
    }

    private suspend fun saveProfile(): com.fghilmany.nufitai.core.error.AppResult<UserProfile> {
        val now = currentTimestamp()
        val experience = answers[0]?.let { GymExperience.valueOf(it) }
        val goal = answers[1]?.let { FitnessGoal.valueOf(it) }
        val frequency = answers[2]?.let { WorkoutFrequency.valueOf(it) }
        val split = (_state.value as? AssessmentState.Step)?.resolvedSplit

        val existingProfile = when (val result = repo.getProfile()) {
            is com.fghilmany.nufitai.core.error.AppResult.Success -> result.data
            is com.fghilmany.nufitai.core.error.AppResult.Error -> null
        }
        val profile = if (existingProfile != null) {
            existingProfile.copy(
                experience = experience,
                goal = goal,
                frequency = frequency,
                split = split,
                splitAutoSelected = (_state.value as? AssessmentState.Step)?.splitReason != null,
                splitReason = (_state.value as? AssessmentState.Step)?.splitReason,
                assessmentCompleted = true,
                updatedAt = now,
            )
        } else {
            UserProfile(
                id = generateUUID(),
                parqCompleted = true,
                experience = experience,
                goal = goal,
                frequency = frequency,
                split = split,
                assessmentCompleted = true,
                createdAt = now,
                updatedAt = now,
            )
        }

        return when (repo.saveProfile(profile)) {
            is com.fghilmany.nufitai.core.error.AppResult.Success -> {
                repo.deleteDraft()
                com.fghilmany.nufitai.core.error.AppResult.Success(profile)
            }
            is com.fghilmany.nufitai.core.error.AppResult.Error -> com.fghilmany.nufitai.core.error.AppResult.Error(
                Failure.Database("Gagal menyimpan profil"),
            )
        }
    }
}

sealed interface AssessmentState {
    data class Step(
        val currentStep: Int,
        val totalSteps: Int = 4,
        val answers: Map<Int, String>,
        val resolvedSplit: WorkoutSplit? = null,
        val splitReason: String? = null,
        val conflictTip: String? = null,
        val canContinue: Boolean = false,
    ) : AssessmentState

    data object Generating : AssessmentState

    data class Done(
        val profile: UserProfile,
        val templateId: String? = null,
    ) : AssessmentState

    data class Error(val failure: Failure) : AssessmentState
}

sealed interface AssessmentEvent {
    data class SelectAnswer(val step: Int, val option: AssessmentOption) : AssessmentEvent
    data object GoBack : AssessmentEvent
    data object Continue : AssessmentEvent
    data object SkipBodyMeasurements : AssessmentEvent
}

enum class AssessmentOption {
    // Step 0: Experience
    BEGINNER, INTERMEDIATE, ADVANCED,
    // Step 1: Goal
    LOSE_WEIGHT, BUILD_MUSCLE, HEALTH_FIT, GET_STRONG,
    // Step 2: Frequency
    TWO_TO_THREE, FOUR_TO_FIVE,
    // Step 3: Split
    UPPER_LOWER, FULL_BODY, TIDAK_TAHU,
}
