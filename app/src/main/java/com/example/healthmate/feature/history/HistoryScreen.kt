package com.example.healthmate.feature.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.healthmate.domain.model.HealthRecord
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import com.example.healthmate.R
import com.example.healthmate.core.ui.accentColor
import com.example.healthmate.core.ui.components.HealthMateDatePickerDialog
import com.example.healthmate.core.ui.icon
import com.example.healthmate.core.ui.labelRes
import com.example.healthmate.core.ui.valueFormatRes
import com.example.healthmate.core.util.formatNumber
import com.example.healthmate.core.util.toFullDateString
import com.example.healthmate.core.util.toShortDateString
import com.example.healthmate.core.util.toTitleString
import com.example.healthmate.domain.calendar.CalendarGenerator
import com.example.healthmate.domain.model.MetricType

data class HistoryActions(
    val onPreviousMonth: () -> Unit = {},
    val onNextMonth: () -> Unit = {},
    val onDateSelected: (LocalDate) -> Unit = {},
    val onShowAllRecords: () -> Unit = {},
    val onJumpToDate: (LocalDate) -> Unit = {},
    val onAddRecord: () -> Unit = {},
    val onEditRecord: (HealthRecord) -> Unit = {},
    val onDeleteRequested: (HealthRecord) -> Unit = {},
    val onDeleteConfirmed: () -> Unit = {},
    val onDeleteDismissed: () -> Unit = {},
)

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onAddRecord: (LocalDate?) -> Unit,
    onEditRecord: (HealthRecord) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HistoryContent(
        state = state,
        actions = HistoryActions(
            onPreviousMonth = viewModel::onPreviousMonth,
            onNextMonth = viewModel::onNextMonth,
            onDateSelected = viewModel::onDateSelected,
            onShowAllRecords = viewModel::onShowAllRecords,
            onJumpToDate = viewModel::onJumpToDate,
            onAddRecord = { onAddRecord(state.selectedDate) },
            onEditRecord = onEditRecord,
            onDeleteRequested = viewModel::onDeleteRequested,
            onDeleteConfirmed = viewModel::onDeleteConfirmed,
            onDeleteDismissed = viewModel::onDeleteDismissed,
        ),
        modifier = modifier,
    )
}

@Composable
fun HistoryContent(
    state: HistoryUiState,
    actions: HistoryActions,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "title") {
                Text(
                    text = stringResource(R.string.history_title),
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            item(key = "calendar") {
                CalendarCard(
                    state = state,
                    onPreviousMonth = actions.onPreviousMonth,
                    onNextMonth = actions.onNextMonth,
                    onDateSelected = actions.onDateSelected,
                    onSearchByDate = { showDatePicker = true },
                )
            }
            item(key = "filter") {
                FilterRow(
                    selectedDate = state.selectedDate,
                    onShowAll = actions.onShowAllRecords,
                )
            }

            if (state.records.isEmpty() && !state.isLoading) {
                item(key = "empty") {
                    EmptyRecords(
                        isFiltered = state.selectedDate != null,
                        onAddRecord = actions.onAddRecord,
                    )
                }
            } else {
                items(items = state.records, key = { it.id }) { record ->
                    RecordCard(
                        record = record,
                        showDate = state.selectedDate == null,
                        onEdit = { actions.onEditRecord(record) },
                        onDelete = { actions.onDeleteRequested(record) },
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = actions.onAddRecord,
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text(stringResource(R.string.editor_title_new)) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        )
    }

    if (showDatePicker) {
        HealthMateDatePickerDialog(
            initialDate = state.selectedDate ?: LocalDate.now(),
            onDateSelected = actions.onJumpToDate,
            onDismiss = { showDatePicker = false },
        )
    }

    state.pendingDelete?.let { record ->
        AlertDialog(
            onDismissRequest = actions.onDeleteDismissed,
            title = { Text(stringResource(R.string.history_delete_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.history_delete_message,
                        record.date.toShortDateString()
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = actions.onDeleteConfirmed) {
                    Text(
                        stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = actions.onDeleteDismissed) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}


@Composable
private fun CalendarCard(
    state: HistoryUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onSearchByDate: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.history_previous_month),
                    )
                }
                Text(
                    text = state.visibleMonth.toTitleString(),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onNextMonth) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.history_next_month),
                    )
                }
                IconButton(onClick = onSearchByDate) {
                    Icon(
                        Icons.Filled.EditCalendar,
                        contentDescription = stringResource(R.string.history_search_date),
                    )
                }
            }

            WeekdayHeader()
            state.weeks.forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            if (day != null) DayCell(
                                day = day,
                                onClick = { onDateSelected(day.date) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekdayHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        CalendarGenerator.weekdays().forEach { weekday: DayOfWeek ->
            Text(
                text = weekday.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun DayCell(day: CalendarDay, onClick: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val description = if (day.hasRecords) {
        stringResource(R.string.history_day_has_records, day.date.toFullDateString())
    } else {
        day.date.toFullDateString()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 2.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .then(
                    when {
                        day.isSelected -> Modifier.background(primary)
                        day.isToday -> Modifier.background(primary.copy(alpha = 0.14f))
                        else -> Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (day.isToday || day.isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (day.isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            )
        }
        // Dot marks days that have records ("highlighted calendar").
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(5.dp)
                .clip(CircleShape)
                .background(if (day.hasRecords) primary else androidx.compose.ui.graphics.Color.Transparent),
        )
    }
}

@Composable
private fun FilterRow(selectedDate: LocalDate?, onShowAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = selectedDate?.toFullDateString() ?: stringResource(R.string.history_showing_all),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
        )
        FilterChip(
            selected = selectedDate == null,
            onClick = onShowAll,
            label = { Text(stringResource(R.string.history_show_all)) },
        )
    }
}

@Composable
private fun EmptyRecords(isFiltered: Boolean, onAddRecord: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(if (isFiltered) R.string.history_empty_day else R.string.history_empty_all),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedButton(onClick = onAddRecord) {
            Text(stringResource(R.string.editor_title_new))
        }
    }
}

@Composable
fun RecordCard(
    record: HealthRecord,
    showDate: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showDate) {
                Text(
                    text = record.date.toFullDateString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            RecordLine(MetricType.STEPS, record.steps)
            RecordLine(MetricType.CALORIES, record.calories)
            RecordLine(MetricType.WATER, record.water)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onEdit) { Text(stringResource(R.string.action_edit)) }
                TextButton(onClick = onDelete) {
                    Text(
                        stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordLine(metric: MetricType, value: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = metric.icon,
            contentDescription = null,
            tint = metric.accentColor,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = stringResource(metric.labelRes),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(metric.valueFormatRes, formatNumber(value)),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
