package com.fghilmany.nufitai.presentation.parq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fghilmany.nufitai.core.error.AppResult
import com.fghilmany.nufitai.domain.onboarding.entity.OnboardingDraft
import com.fghilmany.nufitai.domain.onboarding.repository.OnboardingRepository
import com.fghilmany.nufitai.usecase.onboarding.CompleteParq
import com.fghilmany.nufitai.usecase.onboarding.SaveParqAnswer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ParqViewModel(
    private val saveAnswer: SaveParqAnswer,
    private val completeParq: CompleteParq,
    private val repo: OnboardingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ParqState>(ParqState.Loading)
    val state: StateFlow<ParqState> = _state.asStateFlow()

    private var currentDraft: OnboardingDraft? = null

    init {
        loadDraft()
    }

    private fun loadDraft() {
        viewModelScope.launch {
            val draft = when (val result = repo.getDraft()) {
                is AppResult.Success -> result.data
                is AppResult.Error -> null
            }
            if (draft != null && draft.parqAnswers.isNotEmpty()) {
                currentDraft = draft
                emitQuestionsState(draft.parqAnswers)
            } else {
                _state.value = ParqState.Questions(
                    answers = emptyMap(),
                    answeredCount = 0,
                    allAnswered = false,
                )
            }
        }
    }

    fun onEvent(event: ParqEvent) {
        when (event) {
            is ParqEvent.AnswerQuestion -> handleAnswer(event.index, event.answer)
            is ParqEvent.ConfirmWarning -> handleConfirmWarning()
            is ParqEvent.DismissWarning -> handleDismissWarning()
            is ParqEvent.Continue -> handleContinue()
        }
    }

    private fun handleAnswer(index: Int, answer: Boolean) {
        viewModelScope.launch {
            val result = saveAnswer(index, answer)
            when (result) {
                is AppResult.Success -> {
                    currentDraft = result.data
                    emitQuestionsState(result.data.parqAnswers)
                }
                is AppResult.Error -> { /* log error */ }
            }
        }
    }

    private fun emitQuestionsState(answers: Map<Int, Boolean>) {
        val answeredCount = answers.size
        val allAnswered = answeredCount >= 7
        val isHighRisk = saveAnswer.isHighRisk(answers)

        _state.value = when {
            isHighRisk && allAnswered -> ParqState.Warning(
                answers = answers,
                highRisk = true,
            )
            else -> ParqState.Questions(
                answers = answers,
                answeredCount = answeredCount,
                allAnswered = allAnswered,
            )
        }
    }

    private fun handleConfirmWarning() {
        val currentState = _state.value
        if (currentState is ParqState.Warning && currentState.highRisk) {
            _state.value = ParqState.Blocked(
                answers = currentState.answers,
                reason = "Kombinasi nyeri dada dan pingsan menunjukkan risiko tinggi. " +
                    "Kami sangat menyarankan Anda untuk berkonsultasi dengan dokter sebelum memulai program latihan.",
            )
        }
    }

    private fun handleDismissWarning() {
        val currentState = _state.value
        if (currentState is ParqState.Warning) {
            emitQuestionsState(currentState.answers)
        }
    }

    private fun handleContinue() {
        val currentState = _state.value
        if (currentState is ParqState.Questions && currentState.allAnswered) {
            viewModelScope.launch {
                val draft = currentDraft ?: return@launch
                when (completeParq(draft)) {
                    is AppResult.Success -> _state.value = ParqState.Completed
                    is AppResult.Error -> { /* log error */ }
                }
            }
        }
    }
}

sealed interface ParqState {
    data object Loading : ParqState
    data class Questions(
        val answers: Map<Int, Boolean>,
        val answeredCount: Int,
        val allAnswered: Boolean,
    ) : ParqState
    data class Warning(
        val answers: Map<Int, Boolean>,
        val highRisk: Boolean,
    ) : ParqState
    data class Blocked(
        val answers: Map<Int, Boolean>,
        val reason: String,
    ) : ParqState
    data object Completed : ParqState
}

sealed interface ParqEvent {
    data class AnswerQuestion(val index: Int, val answer: Boolean) : ParqEvent
    data object ConfirmWarning : ParqEvent
    data object DismissWarning : ParqEvent
    data object Continue : ParqEvent
}
