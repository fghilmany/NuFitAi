package com.fghilmany.nufitai.data.onboarding.repository

import com.fghilmany.nufitai.core.error.AppResult
import com.fghilmany.nufitai.core.error.Failure
import com.fghilmany.nufitai.data.db.NuFitDatabase
import com.fghilmany.nufitai.domain.onboarding.entity.BodyMeasurements
import com.fghilmany.nufitai.domain.onboarding.entity.FitnessGoal
import com.fghilmany.nufitai.domain.onboarding.entity.GymExperience
import com.fghilmany.nufitai.domain.onboarding.entity.HealthFlag
import com.fghilmany.nufitai.domain.onboarding.entity.OnboardingDraft
import com.fghilmany.nufitai.domain.onboarding.entity.UserProfile
import com.fghilmany.nufitai.domain.onboarding.entity.WorkoutFrequency
import com.fghilmany.nufitai.domain.onboarding.entity.WorkoutSplit
import com.fghilmany.nufitai.domain.onboarding.repository.OnboardingRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

private fun Boolean.toDbLong(): Long = if (this) 1L else 0L
private fun Long.toDbBoolean(): Boolean = this == 1L

class SqlDelightOnboardingRepository(
    private val database: NuFitDatabase,
) : OnboardingRepository {

    override suspend fun saveDraft(draft: OnboardingDraft): AppResult<Unit> {
        return try {
            database.onboardingDraftQueries.insertOrReplaceDraft(
                id = draft.id,
                parq_answers = json.encodeToString(draft.parqAnswers),
                parq_completed = draft.parqCompleted.toDbLong(),
                assessment_step = draft.assessmentStep?.toLong(),
                assessment_answers = json.encodeToString(draft.assessmentAnswers),
                body_height_cm = draft.bodyHeightCm,
                body_weight_kg = draft.bodyWeightKg,
                started_at = draft.startedAt,
                completed_at = draft.completedAt,
            )
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(Failure.Database("Failed to save draft: ${e.message}"))
        }
    }

    override suspend fun getDraft(): AppResult<OnboardingDraft?> {
        return try {
            val draft = database.onboardingDraftQueries.getAnyDraft().executeAsOneOrNull()
            AppResult.Success(draft?.toEntity())
        } catch (e: Exception) {
            AppResult.Error(Failure.Database("Failed to read draft: ${e.message}"))
        }
    }

    override suspend fun deleteDraft(): AppResult<Unit> {
        return try {
            database.onboardingDraftQueries.deleteAllDrafts()
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(Failure.Database("Failed to delete draft: ${e.message}"))
        }
    }

    override suspend fun saveProfile(profile: UserProfile): AppResult<Unit> {
        return try {
            database.userProfileQueries.insertOrReplaceProfile(
                id = profile.id,
                parq_completed = profile.parqCompleted.toDbLong(),
                health_flags = json.encodeToString(profile.healthFlags),
                experience = profile.experience?.name,
                goal = profile.goal?.name,
                frequency = profile.frequency?.name,
                split = profile.split?.name,
                split_auto_selected = profile.splitAutoSelected.toDbLong(),
                split_reason = profile.splitReason,
                height_cm = profile.bodyMeasurements?.heightCm,
                weight_kg = profile.bodyMeasurements?.weightKg,
                bmi = profile.bodyMeasurements?.bmi,
                assessment_completed = profile.assessmentCompleted.toDbLong(),
                created_at = profile.createdAt,
                updated_at = profile.updatedAt,
            )
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(Failure.Database("Failed to save profile: ${e.message}"))
        }
    }

    override suspend fun getProfile(): AppResult<UserProfile?> {
        return try {
            val profile = database.userProfileQueries.getAnyProfile().executeAsOneOrNull()
            AppResult.Success(profile?.toEntity())
        } catch (e: Exception) {
            AppResult.Error(Failure.Database("Failed to read profile: ${e.message}"))
        }
    }

    override suspend fun hasProfile(): AppResult<Boolean> {
        return try {
            val exists = database.userProfileQueries.checkProfileExists().executeAsOne()
            AppResult.Success(exists)
        } catch (e: Exception) {
            AppResult.Error(Failure.Database("Failed to check profile: ${e.message}"))
        }
    }

    override suspend fun isParqCompleted(): AppResult<Boolean> {
        return try {
            val profile = database.userProfileQueries.getAnyProfile().executeAsOneOrNull()
            AppResult.Success(profile?.parq_completed?.toDbBoolean() == true)
        } catch (e: Exception) {
            AppResult.Error(Failure.Database("Failed to check PAR-Q status: ${e.message}"))
        }
    }
}

// --- Mappers ---

private fun com.fghilmany.nufitai.data.db.OnboardingDraft.toEntity(): OnboardingDraft {
    return OnboardingDraft(
        id = id,
        parqAnswers = try {
            json.decodeFromString(parq_answers)
        } catch (e: Exception) {
            emptyMap()
        },
        parqCompleted = parq_completed.toDbBoolean(),
        assessmentStep = assessment_step?.toInt(),
        assessmentAnswers = try {
            json.decodeFromString(assessment_answers)
        } catch (e: Exception) {
            emptyMap()
        },
        bodyHeightCm = body_height_cm,
        bodyWeightKg = body_weight_kg,
        startedAt = started_at,
        completedAt = completed_at,
    )
}

private fun com.fghilmany.nufitai.data.db.UserProfile.toEntity(): UserProfile {
    return UserProfile(
        id = id,
        parqCompleted = parq_completed.toDbBoolean(),
        healthFlags = try {
            json.decodeFromString(health_flags)
        } catch (e: Exception) {
            emptySet()
        },
        experience = experience?.let {
            try { GymExperience.valueOf(it) } catch (e: Exception) { null }
        },
        goal = goal?.let {
            try { FitnessGoal.valueOf(it) } catch (e: Exception) { null }
        },
        frequency = frequency?.let {
            try { WorkoutFrequency.valueOf(it) } catch (e: Exception) { null }
        },
        split = split?.let {
            try { WorkoutSplit.valueOf(it) } catch (e: Exception) { null }
        },
        splitAutoSelected = split_auto_selected.toDbBoolean(),
        splitReason = split_reason,
        bodyMeasurements = if (height_cm != null || weight_kg != null) {
            BodyMeasurements(
                heightCm = height_cm,
                weightKg = weight_kg,
                bmi = bmi,
            )
        } else {
            null
        },
        assessmentCompleted = assessment_completed.toDbBoolean(),
        createdAt = created_at,
        updatedAt = updated_at,
    )
}
