package com.fghilmany.nufitai.usecase.onboarding

import com.fghilmany.nufitai.core.error.AppResult
import com.fghilmany.nufitai.core.error.Failure
import com.fghilmany.nufitai.domain.onboarding.entity.FitnessGoal
import com.fghilmany.nufitai.domain.onboarding.entity.GymExperience
import com.fghilmany.nufitai.domain.onboarding.entity.UserProfile
import com.fghilmany.nufitai.domain.onboarding.entity.WorkoutSplit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GenerateMonthlyPlanTest {

    private val generatePlan = GenerateMonthlyPlanImpl()

    @Test
    fun `given BEGINNER LOSE_WEIGHT FULL_BODY when generate then B-LW-FB`() {
        val profile = createProfile(GymExperience.BEGINNER, FitnessGoal.LOSE_WEIGHT, WorkoutSplit.FULL_BODY)
        when (val result = generatePlan(profile)) {
            is AppResult.Success -> assertEquals("B-LW-FB", result.data)
            is AppResult.Error -> assertTrue(false, "Should not fail")
        }
    }

    @Test
    fun `given INTERMEDIATE BUILD_MUSCLE UPPER_LOWER when generate then I-BM-UL`() {
        val profile = createProfile(GymExperience.INTERMEDIATE, FitnessGoal.BUILD_MUSCLE, WorkoutSplit.UPPER_LOWER)
        when (val result = generatePlan(profile)) {
            is AppResult.Success -> assertEquals("I-BM-UL", result.data)
            is AppResult.Error -> assertTrue(false, "Should not fail")
        }
    }

    @Test
    fun `given ADVANCED GET_STRONG UPPER_LOWER when generate then A-GS-UL`() {
        val profile = createProfile(GymExperience.ADVANCED, FitnessGoal.GET_STRONG, WorkoutSplit.UPPER_LOWER)
        when (val result = generatePlan(profile)) {
            is AppResult.Success -> assertEquals("A-GS-UL", result.data)
            is AppResult.Error -> assertTrue(false, "Should not fail")
        }
    }

    @Test
    fun `given HEALTH_FIT FULL_BODY when generate then B-HF-FB`() {
        val profile = createProfile(GymExperience.BEGINNER, FitnessGoal.HEALTH_FIT, WorkoutSplit.FULL_BODY)
        when (val result = generatePlan(profile)) {
            is AppResult.Success -> assertEquals("B-HF-FB", result.data)
            is AppResult.Error -> assertTrue(false, "Should not fail")
        }
    }

    @Test
    fun `given null experience when generate then Validation error`() {
        val profile = UserProfile(
            id = "test", parqCompleted = true, experience = null,
            goal = FitnessGoal.LOSE_WEIGHT, split = WorkoutSplit.FULL_BODY,
            assessmentCompleted = true, createdAt = "2026-01-01T00:00:00Z", updatedAt = "2026-01-01T00:00:00Z",
        )
        when (val result = generatePlan(profile)) {
            is AppResult.Error -> assertTrue(result.failure is Failure.Validation)
            is AppResult.Success -> assertTrue(false, "Should fail with null experience")
        }
    }

    @Test
    fun `given null goal when generate then Validation error`() {
        val profile = UserProfile(
            id = "test", parqCompleted = true, experience = GymExperience.BEGINNER,
            goal = null, split = WorkoutSplit.FULL_BODY,
            assessmentCompleted = true, createdAt = "2026-01-01T00:00:00Z", updatedAt = "2026-01-01T00:00:00Z",
        )
        when (val result = generatePlan(profile)) {
            is AppResult.Error -> assertTrue(result.failure is Failure.Validation)
            is AppResult.Success -> assertTrue(false, "Should fail with null goal")
        }
    }

    @Test
    fun `given null split when generate then Validation error`() {
        val profile = UserProfile(
            id = "test", parqCompleted = true, experience = GymExperience.BEGINNER,
            goal = FitnessGoal.LOSE_WEIGHT, split = null,
            assessmentCompleted = true, createdAt = "2026-01-01T00:00:00Z", updatedAt = "2026-01-01T00:00:00Z",
        )
        when (val result = generatePlan(profile)) {
            is AppResult.Error -> assertTrue(result.failure is Failure.Validation)
            is AppResult.Success -> assertTrue(false, "Should fail with null split")
        }
    }

    @Test
    fun `given all 12 combinations when generate then all succeed`() {
        for (exp in GymExperience.entries) {
            for (goal in FitnessGoal.entries) {
                for (split in listOf(WorkoutSplit.UPPER_LOWER, WorkoutSplit.FULL_BODY)) {
                    val profile = createProfile(exp, goal, split)
                    when (val result = generatePlan(profile)) {
                        is AppResult.Success -> assertTrue(result.data.contains("-"))
                        is AppResult.Error -> assertTrue(false, "Failed for $exp/$goal/$split: ${result.failure.message}")
                    }
                }
            }
        }
    }

    private fun createProfile(exp: GymExperience, goal: FitnessGoal, split: WorkoutSplit) = UserProfile(
        id = "test", parqCompleted = true, experience = exp, goal = goal, split = split,
        assessmentCompleted = true, createdAt = "2026-01-01T00:00:00Z", updatedAt = "2026-01-01T00:00:00Z",
    )
}
