package com.example.healthmate.domain.usecase

import com.example.healthmate.domain.model.HealthRecord
import com.example.healthmate.domain.repository.HealthRecordRepository
import com.example.healthmate.domain.validation.InputError
import com.example.healthmate.domain.validation.RecordField
import com.example.healthmate.domain.validation.RecordInput
import com.example.healthmate.domain.validation.RecordValidationResult
import com.example.healthmate.domain.validation.RecordValidator

sealed interface SaveRecordResult {
    data object Saved : SaveRecordResult
    data class Invalid(val errors: Map<RecordField, InputError>) : SaveRecordResult
}

// form validation and record update
class SaveHealthRecordUseCase(
    private val repository: HealthRecordRepository,
    private val validator: RecordValidator,
) {
    suspend operator fun invoke(
        recordId: Long = HealthRecord.NEW_ID,
        input: RecordInput,
    ): SaveRecordResult = when (val result = validator.validate(input)) {
        is RecordValidationResult.Invalid -> SaveRecordResult.Invalid(result.errors)
        is RecordValidationResult.Valid -> {
            val record = HealthRecord(
                id = recordId,
                date = result.date,
                steps = result.steps,
                calories = result.calories,
                water = result.water,
            )
            if (record.isNew) repository.insert(record) else repository.update(record)
            SaveRecordResult.Saved
        }
    }
}