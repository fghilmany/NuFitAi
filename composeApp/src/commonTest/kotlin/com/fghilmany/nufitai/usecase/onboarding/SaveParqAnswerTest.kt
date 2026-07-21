package com.fghilmany.nufitai.usecase.onboarding

import com.fghilmany.nufitai.core.error.AppResult
import com.fghilmany.nufitai.data.onboarding.repository.InMemoryOnboardingRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SaveParqAnswerTest {

    private val repository = InMemoryOnboardingRepository()
    private val saveParqAnswer = SaveParqAnswerImpl(repository)

    @Test
    fun `given no existing draft when save answer then new draft created`() = runTest {
        val result = saveParqAnswer(questionIndex = 0, answer = true)
        when (result) {
            is AppResult.Success -> {
                assertNotNull(result.data)
                assertEquals(true, result.data.parqAnswers[0])
                assertEquals(1, result.data.parqAnswers.size)
            }
            is AppResult.Error -> assertTrue(false, "Should not fail")
        }
    }

    @Test
    fun `given existing draft when save another answer then both answers preserved`() = runTest {
        saveParqAnswer(questionIndex = 0, answer = true)
        val result = saveParqAnswer(questionIndex = 1, answer = false)
        when (result) {
            is AppResult.Success -> {
                assertEquals(true, result.data.parqAnswers[0])
                assertEquals(false, result.data.parqAnswers[1])
                assertEquals(2, result.data.parqAnswers.size)
            }
            is AppResult.Error -> assertTrue(false, "Should not fail")
        }
    }

    @Test
    fun `given chest pain and fainting both true when isHighRisk then true`() {
        assertTrue(saveParqAnswer.isHighRisk(mapOf(1 to true, 2 to true)))
    }

    @Test
    fun `given only chest pain true when isHighRisk then false`() {
        assertFalse(saveParqAnswer.isHighRisk(mapOf(1 to true, 2 to false)))
    }

    @Test
    fun `given only fainting true when isHighRisk then false`() {
        assertFalse(saveParqAnswer.isHighRisk(mapOf(1 to false, 2 to true)))
    }

    @Test
    fun `given neither when isHighRisk then false`() {
        assertFalse(saveParqAnswer.isHighRisk(mapOf(1 to false, 2 to false)))
    }

    @Test
    fun `given empty answers when isHighRisk then false`() {
        assertFalse(saveParqAnswer.isHighRisk(emptyMap()))
    }
}
