package com.example.healthmate.domain.model

import java.time.LocalDate

// dashboard target values
class DashboardSummary(
    val totals: DailyTotals,
    val targets: DailyTargets,
) {

    val date: LocalDate get() = totals.date

    val metrics: List<MetricProgress>
        get() = MetricType.entries.map { metric ->
            MetricProgress(
                metric = metric,
                current = totals.valueOf(metric),
                target = targets.targetOf(metric),
            )
        }

}