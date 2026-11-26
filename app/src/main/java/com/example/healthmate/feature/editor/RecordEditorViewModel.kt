package com.example.healthmate.feature.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.domain.model.HealthRecord
import com.example.healthmate.domain.repository.HealthRecordRepository
import com.example.healthmate.domain.usecase.SaveHealthRecordUseCase
import com.example.healthmate.domain.usecase.SaveRecordResult
import com.example.healthmate.domain.validation.InputError
import com.example.healthmate.domain.validation.RecordField
import com.example.healthmate.domain.validation.RecordInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate

object EditorArgs {
    const val RECORD_ID = "recordId"
    const val DATE = "date"
    const val NO_RECORD = -1L
}

data class RecordEditorUiState(
    val today: LocalDate,
    val date: LocalDate,
    val steps: String = "",
    val calories: String = "",
    val water: String = "",
    val errors: Map<RecordField, InputError> = emptyMap(),
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isFinished: Boolean = false,
)

class RecordEditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: HealthRecordRepository,
    private val saveHealthRecord: SaveHealthRecordUseCase,
    clock: Clock,
) : ViewModel() {

    private val recordId: Long =
        savedStateHandle.get<Long>(EditorArgs.RECORD_ID)
            ?.takeIf { it != EditorArgs.NO_RECORD && it > 0 }
            ?: HealthRecord.NEW_ID

    private val isEditing = recordId != HealthRecord.NEW_ID

    private val _uiState: MutableStateFlow<RecordEditorUiState>
    val uiState: StateFlow<RecordEditorUiState>

    init {
        val today = LocalDate.now(clock)
        val requestedDate = savedStateHandle.get<String>(EditorArgs.DATE)
            ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        val initialDate = requestedDate?.takeIf { !it.isAfter(today) } ?: today

        _uiState = MutableStateFlow(
            RecordEditorUiState(
                today = today,
                date = initialDate,
                isEditing = isEditing,
                isLoading = isEditing,
            ),
        )
        uiState = _uiState.asStateFlow()

        if (isEditing) loadRecord()
    }

    fun onDateChange(date: LocalDate) = _uiState.update {
        it.copy(date = date, errors = it.errors - RecordField.DATE)
    }

    fun onStepsChange(value: String) = _uiState.update {
        it.copy(steps = value.sanitized(), errors = it.errors - RecordField.STEPS)
    }

    fun onCaloriesChange(value: String) = _uiState.update {
        it.copy(calories = value.sanitized(), errors = it.errors - RecordField.CALORIES)
    }

    fun onWaterChange(value: String) = _uiState.update {
        it.copy(water = value.sanitized(), errors = it.errors - RecordField.WATER)
    }

    fun onSaveClick() {
        val current = _uiState.value
        if (current.isSaving || current.isLoading) return
        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val input = RecordInput(
                date = current.date,
                steps = current.steps,
                calories = current.calories,
                water = current.water,
            )
            when (val result = saveHealthRecord(recordId, input)) {
                SaveRecordResult.Saved -> _uiState.update {
                    it.copy(
                        isSaving = false,
                        isFinished = true
                    )
                }

                is SaveRecordResult.Invalid -> _uiState.update {
                    it.copy(
                        isSaving = false,
                        errors = result.errors
                    )
                }
            }
        }
    }

    private fun loadRecord() {
        viewModelScope.launch {
            val record = repository.getById(recordId)
            _uiState.update { state ->
                if (record == null) {
                    // The record was deleted in the meantime; nothing left to edit.
                    state.copy(isLoading = false, isFinished = true)
                } else {
                    state.copy(
                        isLoading = false,
                        date = record.date,
                        steps = record.steps.toString(),
                        calories = record.calories.toString(),
                        water = record.water.toString(),
                    )
                }
            }
        }
    }

    private fun String.sanitized(): String = filter(Char::isDigit).take(MAX_DIGITS)

    private companion object {
        const val MAX_DIGITS = 6
    }
}

