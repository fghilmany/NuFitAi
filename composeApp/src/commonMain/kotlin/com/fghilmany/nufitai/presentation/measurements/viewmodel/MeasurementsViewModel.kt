package com.fghilmany.nufitai.presentation.measurements.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fghilmany.nufitai.core.error.Failure
import com.fghilmany.nufitai.domain.onboarding.entity.BodyMeasurements
import com.fghilmany.nufitai.usecase.onboarding.SaveBodyMeasurements
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MeasurementsViewModel(
    private val saveMeasurements: SaveBodyMeasurements,
) : ViewModel() {

    private val _state = MutableStateFlow<MeasurementsState>(
        MeasurementsState.Ready(
            height = "",
            weight = "",
            bmi = null,
            bmiCategory = null,
            canSave = false,
        ),
    )
    val state: StateFlow<MeasurementsState> = _state.asStateFlow()

    fun onEvent(event: MeasurementsEvent) {
        when (event) {
            is MeasurementsEvent.UpdateHeight -> handleUpdateHeight(event.value)
            is MeasurementsEvent.UpdateWeight -> handleUpdateWeight(event.value)
            is MeasurementsEvent.Save -> handleSave()
            is MeasurementsEvent.Skip -> handleSkip()
        }
    }

    private fun handleUpdateHeight(value: String) {
        val currentState = _state.value as? MeasurementsState.Ready ?: return
        val height = value.toDoubleOrNull()
        val weight = currentState.weight.toDoubleOrNull()
        val bmi = BodyMeasurements.computeBmi(height, weight)

        _state.value = currentState.copy(
            height = value,
            bmi = bmi,
            bmiCategory = bmi?.let { BodyMeasurements.bmiCategory(it) },
            canSave = value.isNotEmpty() || currentState.weight.isNotEmpty(),
        )
    }

    private fun handleUpdateWeight(value: String) {
        val currentState = _state.value as? MeasurementsState.Ready ?: return
        val height = currentState.height.toDoubleOrNull()
        val weight = value.toDoubleOrNull()
        val bmi = BodyMeasurements.computeBmi(height, weight)

        _state.value = currentState.copy(
            weight = value,
            bmi = bmi,
            bmiCategory = bmi?.let { BodyMeasurements.bmiCategory(it) },
            canSave = currentState.height.isNotEmpty() || value.isNotEmpty(),
        )
    }

    private fun handleSave() {
        val currentState = _state.value as? MeasurementsState.Ready ?: return
        val height = currentState.height.toDoubleOrNull()
        val weight = currentState.weight.toDoubleOrNull()

        viewModelScope.launch {
            _state.value = MeasurementsState.Saving
            when (val result = saveMeasurements(height, weight)) {
                is com.fghilmany.nufitai.core.error.AppResult.Error -> {
                    _state.value = MeasurementsState.Ready(
                        height = currentState.height,
                        weight = currentState.weight,
                        bmi = currentState.bmi,
                        bmiCategory = currentState.bmiCategory,
                        canSave = currentState.canSave,
                    )
                }
                is com.fghilmany.nufitai.core.error.AppResult.Success -> {
                    _state.value = MeasurementsState.Done
                }
            }
        }
    }

    private fun handleSkip() {
        _state.value = MeasurementsState.Done
    }
}

sealed interface MeasurementsState {
    data class Ready(
        val height: String,
        val weight: String,
        val bmi: Double?,
        val bmiCategory: String?,
        val canSave: Boolean,
    ) : MeasurementsState

    data object Saving : MeasurementsState
    data object Done : MeasurementsState
}

sealed interface MeasurementsEvent {
    data class UpdateHeight(val value: String) : MeasurementsEvent
    data class UpdateWeight(val value: String) : MeasurementsEvent
    data object Save : MeasurementsEvent
    data object Skip : MeasurementsEvent
}
