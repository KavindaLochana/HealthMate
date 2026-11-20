package com.example.healthmate.domain.model

import java.time.LocalDate

// summary of every record
 class DailyTotals (
     val date: LocalDate,
     val steps: Int,
     val calories: Int,
     val water: Int,
 ) {
    fun valueOf(metric: MetricType): Int = when (metric) {
        MetricType.STEPS -> steps
        MetricType.CALORIES -> calories
        MetricType.WATER -> water
    }

    companion object {
        fun empty(date: LocalDate) = DailyTotals(date = date, steps = 0, calories = 0, water = 0)
    }
}