package com.example.healthmate.domain.model

// metric progress of its daily target
class MetricProgress (
    val metric: MetricType,
    val current: Int,
    val target: Int,
){

   // used to draw the rings and never exceeded the circle
    val fraction: Float
        get() = if (target <= 0) 0f else (current.toFloat() / target).coerceIn(0f, 1f)

    // whole number percentage of progress
    val percent: Int
        get() = if (target <= 0) 0 else ((current.toLong() * 100) / target).toInt()
}