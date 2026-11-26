package com.example.healthmate.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.domain.model.DashboardSummary
import com.example.healthmate.domain.usecase.ObserveDashboardUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.LocalDate

data class DashboardUiState(val summary: DashboardSummary? = null)

class DashboardViewModel(
    observeDashboard: ObserveDashboardUseCase,
    private val clock: Clock,
) : ViewModel() {

    private val today = MutableStateFlow(LocalDate.now(clock))

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = today
        .flatMapLatest { date -> observeDashboard(date) }
        .map { summary -> DashboardUiState(summary) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            DashboardUiState()
        )

    fun onResume() {
        today.value = LocalDate.now(clock)
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
