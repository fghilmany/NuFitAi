package com.fghilmany.nufitai.usecase.onboarding

import com.fghilmany.nufitai.domain.onboarding.entity.GymExperience
import com.fghilmany.nufitai.domain.onboarding.entity.WorkoutFrequency
import com.fghilmany.nufitai.domain.onboarding.entity.WorkoutSplit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResolveSplitTest {

    private val resolveSplit = ResolveSplitImpl()

    @Test
    fun `given BEGINNER and TIDAK_TAHU when resolve then FULL_BODY`() {
        val result = resolveSplit(
            selectedSplit = WorkoutSplit.TIDAK_TAHU,
            experience = GymExperience.BEGINNER,
            frequency = WorkoutFrequency.TWO_TO_THREE,
        )

        assertEquals(WorkoutSplit.FULL_BODY, result.split)
        assertTrue(result.autoSelected)
        assertNotNull(result.reason)
    }

    @Test
    fun `given INTERMEDIATE and TWO_TO_THREE and TIDAK_TAHU when resolve then FULL_BODY`() {
        val result = resolveSplit(
            selectedSplit = WorkoutSplit.TIDAK_TAHU,
            experience = GymExperience.INTERMEDIATE,
            frequency = WorkoutFrequency.TWO_TO_THREE,
        )

        assertEquals(WorkoutSplit.FULL_BODY, result.split)
        assertTrue(result.autoSelected)
    }

    @Test
    fun `given ADVANCED and FOUR_TO_FIVE and TIDAK_TAHU when resolve then UPPER_LOWER`() {
        val result = resolveSplit(
            selectedSplit = WorkoutSplit.TIDAK_TAHU,
            experience = GymExperience.ADVANCED,
            frequency = WorkoutFrequency.FOUR_TO_FIVE,
        )

        assertEquals(WorkoutSplit.UPPER_LOWER, result.split)
        assertTrue(result.autoSelected)
        assertNotNull(result.reason)
    }

    @Test
    fun `given UPPER_LOWER selected directly when resolve then UPPER_LOWER not auto`() {
        val result = resolveSplit(
            selectedSplit = WorkoutSplit.UPPER_LOWER,
            experience = GymExperience.BEGINNER,
            frequency = WorkoutFrequency.TWO_TO_THREE,
        )

        assertEquals(WorkoutSplit.UPPER_LOWER, result.split)
        assertFalse(result.autoSelected)
        assertNull(result.reason)
    }

    @Test
    fun `given FULL_BODY selected directly when resolve then FULL_BODY not auto`() {
        val result = resolveSplit(
            selectedSplit = WorkoutSplit.FULL_BODY,
            experience = GymExperience.ADVANCED,
            frequency = WorkoutFrequency.FOUR_TO_FIVE,
        )

        assertEquals(WorkoutSplit.FULL_BODY, result.split)
        assertFalse(result.autoSelected)
    }

    @Test
    fun `given UPPER_LOWER and TWO_TO_THREE when resolve then conflict detected`() {
        val result = resolveSplit(
            selectedSplit = WorkoutSplit.UPPER_LOWER,
            experience = GymExperience.INTERMEDIATE,
            frequency = WorkoutFrequency.TWO_TO_THREE,
        )

        assertNotNull(result.conflictTip)
        assertTrue(result.conflictTip!!.contains("Upper/Lower"))
    }

    @Test
    fun `given FULL_BODY and FOUR_TO_FIVE when resolve then conflict detected`() {
        val result = resolveSplit(
            selectedSplit = WorkoutSplit.FULL_BODY,
            experience = GymExperience.ADVANCED,
            frequency = WorkoutFrequency.FOUR_TO_FIVE,
        )

        assertNotNull(result.conflictTip)
        assertTrue(result.conflictTip!!.contains("Full Body"))
    }

    @Test
    fun `given UPPER_LOWER and FOUR_TO_FIVE when resolve then no conflict`() {
        val result = resolveSplit(
            selectedSplit = WorkoutSplit.UPPER_LOWER,
            experience = GymExperience.INTERMEDIATE,
            frequency = WorkoutFrequency.FOUR_TO_FIVE,
        )

        assertNull(result.conflictTip)
    }

    @Test
    fun `given FULL_BODY and TWO_TO_THREE when resolve then no conflict`() {
        val result = resolveSplit(
            selectedSplit = WorkoutSplit.FULL_BODY,
            experience = GymExperience.BEGINNER,
            frequency = WorkoutFrequency.TWO_TO_THREE,
        )

        assertNull(result.conflictTip)
    }
}
