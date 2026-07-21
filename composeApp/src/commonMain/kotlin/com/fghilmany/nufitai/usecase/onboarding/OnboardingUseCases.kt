package com.fghilmany.nufitai.usecase.onboarding

import com.fghilmany.nufitai.core.error.AppResult
import com.fghilmany.nufitai.domain.onboarding.entity.DifficultyTier
import com.fghilmany.nufitai.domain.onboarding.entity.FitnessGoal
import com.fghilmany.nufitai.domain.onboarding.entity.GymExperience
import com.fghilmany.nufitai.domain.onboarding.entity.HealthFlag
import com.fghilmany.nufitai.domain.onboarding.entity.OnboardingDraft
import com.fghilmany.nufitai.domain.onboarding.entity.UserProfile
import com.fghilmany.nufitai.domain.onboarding.entity.WorkoutFrequency
import com.fghilmany.nufitai.domain.onboarding.entity.WorkoutSplit

// --- Routing ---
sealed interface OnboardingDestination {
    data object Parq : OnboardingDestination
    data class Assessment(val step: Int = 0) : OnboardingDestination
    data object Measurements : OnboardingDestination
    data object Home : OnboardingDestination
}

// --- Split resolution result ---
data class ResolvedSplit(
    val split: WorkoutSplit,
    val autoSelected: Boolean,
    val reason: String?,
    val conflictTip: String?,
)

// --- Use case interfaces ---
interface SaveParqAnswer {
    suspend operator fun invoke(questionIndex: Int, answer: Boolean): AppResult<OnboardingDraft>
    fun isHighRisk(answers: Map<Int, Boolean>): Boolean
}

interface CompleteParq {
    suspend operator fun invoke(draft: OnboardingDraft): AppResult<UserProfile>
    fun computeHealthFlags(answers: Map<Int, Boolean>): Set<HealthFlag>
}

interface SaveAssessmentAnswer {
    suspend operator fun invoke(stepIndex: Int, answer: String): AppResult<OnboardingDraft>
}

interface ResolveSplit {
    operator fun invoke(selectedSplit: WorkoutSplit, experience: GymExperience, frequency: WorkoutFrequency): ResolvedSplit
}

interface GenerateMonthlyPlan {
    operator fun invoke(profile: UserProfile): AppResult<String>
    fun mapExperienceToTier(experience: GymExperience?): DifficultyTier?
    fun buildTemplateId(tier: DifficultyTier, goal: FitnessGoal, split: WorkoutSplit): String
}

interface SaveBodyMeasurements {
    suspend operator fun invoke(heightCm: Double?, weightKg: Double?): AppResult<UserProfile>
}

interface DetermineNextScreen {
    suspend operator fun invoke(): OnboardingDestination
}
