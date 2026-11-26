package com.example.healthmate.domain.usecase

import com.example.healthmate.domain.model.DashboardSummary
import com.example.healthmate.domain.repository.HealthRecordRepository
import com.example.healthmate.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

// combines date and target
class ObserveDashboardUseCase(
    private val records: HealthRecordRepository,
    private val settings: SettingsRepository,
) {
    operator fun invoke(date: LocalDate): Flow<DashboardSummary> =
        combine(records.observeTotals(date), settings.targets) { totals, targets ->
            DashboardSummary(totals = totals, targets = targets)
        }
}
