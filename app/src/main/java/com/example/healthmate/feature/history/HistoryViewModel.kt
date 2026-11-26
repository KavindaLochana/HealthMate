package com.example.healthmate.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.domain.calendar.CalendarGenerator
import com.example.healthmate.domain.model.HealthRecord
import com.example.healthmate.domain.repository.HealthRecordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.time.YearMonth

// month grid cell
data class CalendarDay(
    val date: LocalDate,
    val hasRecords: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
)

data class HistoryUiState(
    val visibleMonth: YearMonth,
    val selectedDate: LocalDate?, // null means show all
    val weeks: List<List<CalendarDay?>> = emptyList(),
    val records: List<HealthRecord> = emptyList(),
    val pendingDelete: HealthRecord? = null,
    val isLoading: Boolean = true,
)

class HistoryViewModel(
    private val repository: HealthRecordRepository,
    private val clock: Clock,
) : ViewModel() {

    private val visibleMonth = MutableStateFlow(YearMonth.now(clock))
    private val selectedDate = MutableStateFlow<LocalDate?>(LocalDate.now(clock))
    private val pendingDelete = MutableStateFlow<HealthRecord?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HistoryUiState> = combine(
        visibleMonth,
        selectedDate,
        pendingDelete,
        visibleMonth.flatMapLatest { month -> repository.observeDatesWithRecords(month) },
        selectedDate.flatMapLatest { date ->
            if (date == null) repository.observeAll() else repository.observeByDate(date)
        },
    ) { month, selected, pending, datesWithRecords, records ->
        HistoryUiState(
            visibleMonth = month,
            selectedDate = selected,
            weeks = buildWeeks(month, selected, datesWithRecords),
            records = records,
            pendingDelete = pending,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = HistoryUiState(
            visibleMonth = visibleMonth.value,
            selectedDate = selectedDate.value,
        ),
    )

    fun onPreviousMonth() = visibleMonth.update { it.minusMonths(1) }

    fun onNextMonth() = visibleMonth.update { it.plusMonths(1) }

    // taping a day filter data, again tap clears it
    fun onDateSelected(date: LocalDate) =
        selectedDate.update { current -> if (current == date) null else date }

    fun onShowAllRecords() {
        selectedDate.value = null
    }

    fun onJumpToDate(date: LocalDate) {
        visibleMonth.value = YearMonth.from(date)
        selectedDate.value = date
    }

    fun onDeleteRequested(record: HealthRecord) {
        pendingDelete.value = record
    }

    fun onDeleteDismissed() {
        pendingDelete.value = null
    }

    fun onDeleteConfirmed() {
        val record = pendingDelete.value ?: return
        pendingDelete.value = null
        viewModelScope.launch { repository.delete(record.id) }
    }

    private fun buildWeeks(
        month: YearMonth,
        selected: LocalDate?,
        datesWithRecords: Set<LocalDate>,
    ): List<List<CalendarDay?>> {
        val today = LocalDate.now(clock)
        return CalendarGenerator.monthGrid(month).map { week ->
            week.map { date ->
                date?.let {
                    CalendarDay(
                        date = it,
                        hasRecords = it in datesWithRecords,
                        isToday = it == today,
                        isSelected = it == selected,
                    )
                }
            }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}