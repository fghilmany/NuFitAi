package com.fghilmany.nufitai.usecase.onboarding

import com.fghilmany.nufitai.core.error.AppResult
import com.fghilmany.nufitai.core.error.Failure
import com.fghilmany.nufitai.core.util.currentTimestamp
import com.fghilmany.nufitai.core.util.generateUUID
import com.fghilmany.nufitai.domain.onboarding.entity.BodyMeasurements
import com.fghilmany.nufitai.domain.onboarding.entity.DifficultyTier
import com.fghilmany.nufitai.domain.onboarding.entity.FitnessGoal
import com.fghilmany.nufitai.domain.onboarding.entity.GymExperience
import com.fghilmany.nufitai.domain.onboarding.entity.HealthFlag
import com.fghilmany.nufitai.domain.onboarding.entity.OnboardingDraft
import com.fghilmany.nufitai.domain.onboarding.entity.UserProfile
import com.fghilmany.nufitai.domain.onboarding.entity.WorkoutFrequency
import com.fghilmany.nufitai.domain.onboarding.entity.WorkoutSplit
import com.fghilmany.nufitai.domain.onboarding.entity.parqQuestions
import com.fghilmany.nufitai.domain.onboarding.repository.OnboardingRepository

// --- DetermineNextScreen ---
class DetermineNextScreenImpl(
    private val repo: OnboardingRepository,
) : DetermineNextScreen {
    override suspend operator fun invoke(): OnboardingDestination {
        val profile = when (val r = repo.getProfile()) { is AppResult.Success -> r.data; is AppResult.Error -> null }
        val draft = when (val r = repo.getDraft()) { is AppResult.Success -> r.data; is AppResult.Error -> null }

        if (profile == null || !profile.parqCompleted) return OnboardingDestination.Parq
        if (draft != null && !draft.parqCompleted) return OnboardingDestination.Parq
        if (!profile.assessmentCompleted) return OnboardingDestination.Assessment(draft?.assessmentStep ?: 0)
        if (profile.bodyMeasurements == null) return OnboardingDestination.Measurements
        return OnboardingDestination.Home
    }
}

// --- SaveParqAnswer ---
class SaveParqAnswerImpl(
    private val repo: OnboardingRepository,
) : SaveParqAnswer {
    override suspend operator fun invoke(questionIndex: Int, answer: Boolean): AppResult<OnboardingDraft> {
        val existing = when (val r = repo.getDraft()) { is AppResult.Success -> r.data; is AppResult.Error -> null }
        val draft = existing ?: OnboardingDraft(id = generateUUID(), startedAt = currentTimestamp())
        val updated = draft.copy(parqAnswers = draft.parqAnswers + (questionIndex to answer))
        return when (repo.saveDraft(updated)) {
            is AppResult.Success -> AppResult.Success(updated)
            is AppResult.Error -> AppResult.Error(Failure.Database("Gagal menyimpan jawaban PAR-Q"))
        }
    }
    override fun isHighRisk(answers: Map<Int, Boolean>): Boolean = answers[1] == true && answers[2] == true
}

// --- CompleteParq ---
class CompleteParqImpl(
    private val repo: OnboardingRepository,
) : CompleteParq {
    override suspend operator fun invoke(draft: OnboardingDraft): AppResult<UserProfile> {
        val flags = computeHealthFlags(draft.parqAnswers)
        val now = currentTimestamp()
        val existing = when (val r = repo.getProfile()) { is AppResult.Success -> r.data; is AppResult.Error -> null }
        val profile = existing?.copy(parqCompleted = true, healthFlags = flags, updatedAt = now)
            ?: UserProfile(id = generateUUID(), parqCompleted = true, healthFlags = flags, createdAt = now, updatedAt = now)
        return when (repo.saveProfile(profile)) {
            is AppResult.Success -> { repo.deleteDraft(); AppResult.Success(profile) }
            is AppResult.Error -> AppResult.Error(Failure.Database("Gagal menyimpan profil"))
        }
    }
    override fun computeHealthFlags(answers: Map<Int, Boolean>): Set<HealthFlag> =
        parqQuestions.filter { answers[it.index] == true && it.flag != null }.mapNotNull { it.flag }.toSet()
}

// --- SaveAssessmentAnswer ---
class SaveAssessmentAnswerImpl(
    private val repo: OnboardingRepository,
) : SaveAssessmentAnswer {
    override suspend operator fun invoke(stepIndex: Int, answer: String): AppResult<OnboardingDraft> {
        val existing = when (val r = repo.getDraft()) { is AppResult.Success -> r.data; is AppResult.Error -> null }
        val draft = existing ?: OnboardingDraft(id = generateUUID(), startedAt = currentTimestamp())
        val updated = draft.copy(assessmentAnswers = draft.assessmentAnswers + (stepIndex to answer), assessmentStep = stepIndex)
        return when (repo.saveDraft(updated)) {
            is AppResult.Success -> AppResult.Success(updated)
            is AppResult.Error -> AppResult.Error(Failure.Database("Gagal menyimpan jawaban assessment"))
        }
    }
}

// --- ResolveSplit ---
class ResolveSplitImpl : ResolveSplit {
    override operator fun invoke(selectedSplit: WorkoutSplit, experience: GymExperience, frequency: WorkoutFrequency): ResolvedSplit {
        val (resolved, auto, reason) = when (selectedSplit) {
            WorkoutSplit.TIDAK_TAHU -> {
                val isBeginnerOrLow = experience == GymExperience.BEGINNER || frequency == WorkoutFrequency.TWO_TO_THREE
                val split = if (isBeginnerOrLow) WorkoutSplit.FULL_BODY else WorkoutSplit.UPPER_LOWER
                val r = if (isBeginnerOrLow) "Kami pilihkan Full Body karena kamu masih pemula atau latihan 2-3x seminggu."
                    else "Kami pilihkan Upper/Lower karena kamu latihan 4-5x seminggu."
                Triple(split, true, r)
            }
            else -> Triple(selectedSplit, false, null)
        }
        val conflictTip = when {
            resolved == WorkoutSplit.UPPER_LOWER && frequency == WorkoutFrequency.TWO_TO_THREE ->
                "Upper/Lower biasanya lebih efektif dengan latihan 4-5x/minggu."
            resolved == WorkoutSplit.FULL_BODY && frequency == WorkoutFrequency.FOUR_TO_FIVE ->
                "Full Body bisa dilakukan 4-5x/minggu, pastikan intensitas tidak terlalu tinggi."
            else -> null
        }
        return ResolvedSplit(resolved, auto, reason, conflictTip)
    }
}

// --- GenerateMonthlyPlan ---
class GenerateMonthlyPlanImpl : GenerateMonthlyPlan {
    override operator fun invoke(profile: UserProfile): AppResult<String> {
        val tier = mapExperienceToTier(profile.experience)
            ?: return AppResult.Error(Failure.Validation("Lengkapi data pengalaman gym"))
        val goal = profile.goal ?: return AppResult.Error(Failure.Validation("Lengkapi tujuan fitness"))
        val split = profile.split ?: return AppResult.Error(Failure.Validation("Lengkapi preferensi split"))
        return AppResult.Success(buildTemplateId(tier, goal, split))
    }
    override fun mapExperienceToTier(experience: GymExperience?): DifficultyTier? = when (experience) {
        GymExperience.BEGINNER -> DifficultyTier.BEGINNER; GymExperience.INTERMEDIATE -> DifficultyTier.INTERMEDIATE
        GymExperience.ADVANCED -> DifficultyTier.ADVANCED; null -> null
    }
    override fun buildTemplateId(tier: DifficultyTier, goal: FitnessGoal, split: WorkoutSplit): String {
        val t = when (tier) { DifficultyTier.BEGINNER -> "B"; DifficultyTier.INTERMEDIATE -> "I"; DifficultyTier.ADVANCED -> "A" }
        val g = when (goal) { FitnessGoal.LOSE_WEIGHT -> "LW"; FitnessGoal.BUILD_MUSCLE -> "BM"; FitnessGoal.HEALTH_FIT -> "HF"; FitnessGoal.GET_STRONG -> "GS" }
        val s = when (split) { WorkoutSplit.UPPER_LOWER -> "UL"; WorkoutSplit.FULL_BODY -> "FB"; WorkoutSplit.TIDAK_TAHU -> "FB" }
        return "$t-$g-$s"
    }
}

// --- SaveBodyMeasurements ---
class SaveBodyMeasurementsImpl(
    private val repo: OnboardingRepository,
) : SaveBodyMeasurements {
    override suspend operator fun invoke(heightCm: Double?, weightKg: Double?): AppResult<UserProfile> {
        val existing = when (val r = repo.getProfile()) { is AppResult.Success -> r.data; is AppResult.Error -> null }
            ?: return AppResult.Error(Failure.Database("Profil belum tersedia"))
        val bmi = BodyMeasurements.computeBmi(heightCm, weightKg)
        val updated = existing.copy(bodyMeasurements = BodyMeasurements(heightCm, weightKg, bmi), updatedAt = currentTimestamp())
        return when (repo.saveProfile(updated)) {
            is AppResult.Success -> AppResult.Success(updated)
            is AppResult.Error -> AppResult.Error(Failure.Database("Gagal menyimpan data tubuh"))
        }
    }
}
