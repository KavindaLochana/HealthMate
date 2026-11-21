package com.example.healthmate.domain.validation

import java.time.Clock
import java.time.LocalDate


enum class RecordField { DATE, STEPS, CALORIES, WATER }

// validation for each record
object RecordLimits {
    val STEPS = 0..100_000
    val CALORIES = 0..10_000
    val WATER = 0..10_000
}

// record input form
data class RecordInput(
    val date: LocalDate?,
    val steps: String,
    val calories: String,
    val water: String,
)

sealed interface RecordValidationResult {
    data class Valid(
        val date: LocalDate,
        val steps: Int,
        val calories: Int,
        val water: Int,
    ) : RecordValidationResult

    data class Invalid(val errors: Map<RecordField, InputError>) : RecordValidationResult
}

// validate new/edit record
class RecordValidator(private val clock: Clock) {

    fun validate(input: RecordInput): RecordValidationResult {
        val errors = linkedMapOf<RecordField, InputError>()

        val date = input.date
        when {
            date == null -> errors[RecordField.DATE] = InputError.Required
            date.isAfter(LocalDate.now(clock)) -> errors[RecordField.DATE] = InputError.FutureDate
        }

        val steps = parseField(RecordField.STEPS, input.steps, RecordLimits.STEPS, errors)
        val calories =
            parseField(RecordField.CALORIES, input.calories, RecordLimits.CALORIES, errors)
        val water = parseField(RecordField.WATER, input.water, RecordLimits.WATER, errors)

        if (errors.isNotEmpty() || date == null || steps == null || calories == null || water == null) {
            return RecordValidationResult.Invalid(errors)
        }
        return RecordValidationResult.Valid(date, steps, calories, water)
    }

    private fun parseField(
        field: RecordField,
        raw: String,
        range: IntRange,
        errors: MutableMap<RecordField, InputError>,
    ): Int? = when (val result = IntParser.parse(raw, range)) {
        is ParseResult.Value -> result.value
        is ParseResult.Failure -> {
            errors[field] = result.error
            null
        }
    }
}