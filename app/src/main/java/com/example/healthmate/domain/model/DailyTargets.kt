package com.example.healthmate.domain.model

// this runs the dashboard rings

data class DailyTargets(
    val steps: Int = DEFAULT_STEPS,
    val calories: Int = DEFAULT_CALORIES,
    val water: Int = DEFAULT_WATER,
) {
    fun targetOf(metric: MetricType): Int = when (metric) {
        MetricType.STEPS -> steps
        MetricType.CALORIES -> calories
        MetricType.WATER -> water
    }

    fun withTarget(metric: MetricType, value: Int): DailyTargets = when (metric) {
        MetricType.STEPS -> copy(steps = value)
        MetricType.CALORIES -> copy(calories = value)
        MetricType.WATER -> copy(water = value)
    }

    companion object {
        const val DEFAULT_STEPS = 10_000
        const val DEFAULT_CALORIES = 500
        const val DEFAULT_WATER = 2_500
    }
}
