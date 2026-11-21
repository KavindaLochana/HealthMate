package com.example.healthmate.domain.validation

import com.example.healthmate.domain.model.MetricType

// target limits
object TargetLimits {
    fun rangeOf(metric: MetricType): IntRange = when (metric) {
        MetricType.STEPS -> 1_000..100_000
        MetricType.CALORIES -> 50..10_000
        MetricType.WATER -> 500..10_000
    }
}

sealed interface TargetValidationResult {
    data class Valid(val value: Int) : TargetValidationResult
    data class Invalid(val error: InputError) : TargetValidationResult
}

class TargetValidator {
    fun validate(metric: MetricType, raw: String): TargetValidationResult =
        when (val result = IntParser.parse(raw, TargetLimits.rangeOf(metric))) {
            is ParseResult.Value -> TargetValidationResult.Valid(result.value)
            is ParseResult.Failure -> TargetValidationResult.Invalid(result.error)
        }
}