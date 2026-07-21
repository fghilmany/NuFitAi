package com.fghilmany.nufitai.usecase.onboarding

import com.fghilmany.nufitai.core.error.AppResult
import com.fghilmany.nufitai.data.onboarding.repository.InMemoryOnboardingRepository
import com.fghilmany.nufitai.domain.onboarding.entity.BodyMeasurements
import com.fghilmany.nufitai.domain.onboarding.entity.FitnessGoal
import com.fghilmany.nufitai.domain.onboarding.entity.GymExperience
import com.fghilmany.nufitai.domain.onboarding.entity.UserProfile
import com.fghilmany.nufitai.domain.onboarding.entity.WorkoutFrequency
import com.fghilmany.nufitai.domain.onboarding.entity.WorkoutSplit
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class DetermineNextScreenTest {

    private val repository = InMemoryOnboardingRepository()
    private val determineNext = DetermineNextScreenImpl(repository)

    @Test
    fun `given no profile when determine then Parq`() = runTest {
        val result = determineNext()
        assertTrue(result is OnboardingDestination.Parq)
    }

    @Test
    fun `given profile with parq not completed when determine then Parq`() = runTest {
        repository.saveProfile(
            UserProfile(
                id = "test",
                parqCompleted = false,
                createdAt = "2026-01-01T00:00:00Z",
                updatedAt = "2026-01-01T00:00:00Z",
            ),
        )
        val result = determineNext()
        assertTrue(result is OnboardingDestination.Parq)
    }

    @Test
    fun `given profile with parq completed but assessment not when determine then Assessment`() = runTest {
        repository.saveProfile(
            UserProfile(
                id = "test",
                parqCompleted = true,
                assessmentCompleted = false,
                createdAt = "2026-01-01T00:00:00Z",
                updatedAt = "2026-01-01T00:00:00Z",
            ),
        )
        val result = determineNext()
        assertTrue(result is OnboardingDestination.Assessment)
    }

    @Test
    fun `given profile with assessment completed but no measurements when determine then Measurements`() = runTest {
        repository.saveProfile(
            UserProfile(
                id = "test",
                parqCompleted = true,
                experience = GymExperience.BEGINNER,
                goal = FitnessGoal.LOSE_WEIGHT,
                frequency = WorkoutFrequency.TWO_TO_THREE,
                split = WorkoutSplit.FULL_BODY,
                assessmentCompleted = true,
                bodyMeasurements = null,
                createdAt = "2026-01-01T00:00:00Z",
                updatedAt = "2026-01-01T00:00:00Z",
            ),
        )
        val result = determineNext()
        assertTrue(result is OnboardingDestination.Measurements)
    }

    @Test
    fun `given profile fully completed when determine then Home`() = runTest {
        repository.saveProfile(
            UserProfile(
                id = "test",
                parqCompleted = true,
                experience = GymExperience.BEGINNER,
                goal = FitnessGoal.LOSE_WEIGHT,
                frequency = WorkoutFrequency.TWO_TO_THREE,
                split = WorkoutSplit.FULL_BODY,
                assessmentCompleted = true,
                bodyMeasurements = BodyMeasurements(heightCm = 170.0, weightKg = 70.0, bmi = 24.22),
                createdAt = "2026-01-01T00:00:00Z",
                updatedAt = "2026-01-01T00:00:00Z",
            ),
        )
        val result = determineNext()
        assertTrue(result is OnboardingDestination.Home)
    }
}
