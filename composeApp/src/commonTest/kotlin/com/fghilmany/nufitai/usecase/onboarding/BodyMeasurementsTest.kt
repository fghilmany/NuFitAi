package com.fghilmany.nufitai.usecase.onboarding

import com.fghilmany.nufitai.domain.onboarding.entity.BodyMeasurements
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BodyMeasurementsTest {

    @Test
    fun `given valid height and weight when computeBmi then correct result`() {
        val bmi = BodyMeasurements.computeBmi(heightCm = 170.0, weightKg = 70.0)
        // BMI = 70 / (1.7)^2 = 70 / 2.89 ≈ 24.22
        assertEquals(24.22, bmi!!, 0.01)
    }

    @Test
    fun `given null height when computeBmi then null`() {
        assertNull(BodyMeasurements.computeBmi(heightCm = null, weightKg = 70.0))
    }

    @Test
    fun `given null weight when computeBmi then null`() {
        assertNull(BodyMeasurements.computeBmi(heightCm = 170.0, weightKg = null))
    }

    @Test
    fun `given both null when computeBmi then null`() {
        assertNull(BodyMeasurements.computeBmi(heightCm = null, weightKg = null))
    }

    @Test
    fun `given zero height when computeBmi then null`() {
        assertNull(BodyMeasurements.computeBmi(heightCm = 0.0, weightKg = 70.0))
    }

    @Test
    fun `given zero weight when computeBmi then null`() {
        assertNull(BodyMeasurements.computeBmi(heightCm = 170.0, weightKg = 0.0))
    }

    @Test
    fun `given negative height when computeBmi then null`() {
        assertNull(BodyMeasurements.computeBmi(heightCm = -170.0, weightKg = 70.0))
    }

    @Test
    fun `given underweight bmi when category then Kurus`() {
        assertEquals("Kurus", BodyMeasurements.bmiCategory(17.0))
    }

    @Test
    fun `given normal bmi when category then Normal`() {
        assertEquals("Normal", BodyMeasurements.bmiCategory(22.0))
    }

    @Test
    fun `given overweight bmi when category then Gemuk`() {
        assertEquals("Gemuk", BodyMeasurements.bmiCategory(27.0))
    }

    @Test
    fun `given obese bmi when category then Obesitas`() {
        assertEquals("Obesitas", BodyMeasurements.bmiCategory(35.0))
    }

    @Test
    fun `given boundary bmi 18_5 when category then Normal`() {
        assertEquals("Normal", BodyMeasurements.bmiCategory(18.5))
    }

    @Test
    fun `given boundary bmi 25 when category then Gemuk`() {
        assertEquals("Gemuk", BodyMeasurements.bmiCategory(25.0))
    }

    @Test
    fun `given boundary bmi 30 when category then Obesitas`() {
        assertEquals("Obesitas", BodyMeasurements.bmiCategory(30.0))
    }
}
