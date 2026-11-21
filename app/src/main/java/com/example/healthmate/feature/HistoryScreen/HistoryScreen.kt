@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.healthmate.feature.HistoryScreen


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.healthmate.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// -----------------------------------------------------------------------------
// UI-only data
// -----------------------------------------------------------------------------

private data class HistoryRecord(
    val id: Int,
    val date: LocalDate,
    val steps: Int,
    val calories: Int,
    val water: Int
)

private data class CalendarDay(
    val date: LocalDate,
    val isToday: Boolean,
    val isSelected: Boolean,
    val hasRecords: Boolean
)

// -----------------------------------------------------------------------------
// Main screen
// -----------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier
) {
    val today = remember { LocalDate.now() }

    var visibleMonth by rememberSaveable {
        mutableStateOf(YearMonth.from(today))
    }

    var selectedDate by rememberSaveable {
        mutableStateOf<LocalDate?>(today)
    }

    var showDatePicker by rememberSaveable {
        mutableStateOf(false)
    }

    var pendingDelete by remember {
        mutableStateOf<HistoryRecord?>(null)
    }

    var editingRecord by remember {
        mutableStateOf<HistoryRecord?>(null)
    }

    var nextRecordId by remember {
        mutableIntStateOf(4)
    }

    var records by remember {
        mutableStateOf(
            listOf(
                HistoryRecord(
                    id = 1,
                    date = today,
                    steps = 8240,
                    calories = 2150,
                    water = 2200
                ),
                HistoryRecord(
                    id = 2,
                    date = today.minusDays(1),
                    steps = 6520,
                    calories = 1980,
                    water = 1800
                ),
                HistoryRecord(
                    id = 3,
                    date = today.minusDays(3),
                    steps = 10240,
                    calories = 2300,
                    water = 2500
                )
            )
        )
    }

    val filteredRecords = remember(records, selectedDate) {
        records
            .filter { selectedDate == null || it.date == selectedDate }
            .sortedByDescending { it.date }
    }

    val calendarWeeks = remember(visibleMonth, selectedDate, records) {
        generateCalendarWeeks(
            month = visibleMonth,
            selectedDate = selectedDate,
            records = records
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "title") {
                Text(
                    text = "stringResource(R.string.history_title)",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item(key = "calendar") {
                CalendarCard(
                    visibleMonth = visibleMonth,
                    weeks = calendarWeeks,
                    onPreviousMonth = {
                        visibleMonth = visibleMonth.minusMonths(1)
                    },
                    onNextMonth = {
                        visibleMonth = visibleMonth.plusMonths(1)
                    },
                    onDateSelected = { date ->
                        selectedDate = date
                    },
                    onSearchByDate = {
                        showDatePicker = true
                    }
                )
            }

            item(key = "filter") {
                FilterRow(
                    selectedDate = selectedDate,
                    onShowAll = {
                        selectedDate = null
                    }
                )
            }

            if (filteredRecords.isEmpty()) {
                item(key = "empty") {
                    EmptyRecords(
                        isFiltered = selectedDate != null,
                        onAddRecord = {
                            val date = selectedDate ?: today

                            records = records + HistoryRecord(
                                id = nextRecordId++,
                                date = date,
                                steps = 8000,
                                calories = 2000,
                                water = 2000
                            )
                        }
                    )
                }
            } else {
                items(
                    items = filteredRecords,
                    key = { it.id }
                ) { record ->
                    RecordCard(
                        record = record,
                        showDate = selectedDate == null,
                        onEdit = {
                            editingRecord = record
                        },
                        onDelete = {
                            pendingDelete = record
                        }
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = {
                val date = selectedDate ?: today

                records = records + HistoryRecord(
                    id = nextRecordId++,
                    date = date,
                    steps = 8000,
                    calories = 2000,
                    water = 2000
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            },
            text = {
                Text("stringResource(R.string.editor_title_new)")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }

    // Date picker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = (
                    selectedDate ?: today
                    ).toEpochMillis()
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = millis.toLocalDate()

                            selectedDate = date
                            visibleMonth = YearMonth.from(date)
                        }

                        showDatePicker = false
                    }
                ) {
                    Text("stringResource(R.string.action_confirm)")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("stringResource(R.string.action_cancel)")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Delete confirmation
    pendingDelete?.let { record ->
        AlertDialog(
            onDismissRequest = {
                pendingDelete = null
            },
            title = {
                Text("stringResource(R.string.history_delete_title)")
            },
            text = {
                Text("date"
//                    stringResource(
//                        R.string.history_delete_message,
//                        record.date.format(
//                            DateTimeFormatter.ofPattern("dd MMM yyyy")
//                        )
//                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        records = records.filterNot {
                            it.id == record.id
                        }
                        pendingDelete = null
                    }
                ) {
                    Text(
                       " stringResource(R.string.action_delete)",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingDelete = null
                    }
                ) {
                    Text("stringResource(R.string.action_cancel)")
                }
            }
        )
    }

    // Temporary edit dialog
    editingRecord?.let { record ->
        AlertDialog(
            onDismissRequest = {
                editingRecord = null
            },
            title = {
                Text("Edit health record")
            },
            text = {
                Text(
                    "Editing is currently a UI preview. " +
                            "The selected record contains ${record.steps} steps."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        editingRecord = null
                    }
                ) {
                    Text("stringResource(R.string.action_confirm)")
                }
            }
        )
    }
}

// -----------------------------------------------------------------------------
// Calendar
// -----------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarCard(
    visibleMonth: YearMonth,
    weeks: List<List<CalendarDay?>>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onSearchByDate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.55f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "stringResource(R.string.history_previous_month)"
                    )
                }

                Text(
                    text = visibleMonth.format(
                        DateTimeFormatter.ofPattern("MMMM yyyy")
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onNextMonth) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "stringResource(R.string.history_next_month)"
                    )
                }

                IconButton(onClick = onSearchByDate) {
                    Icon(
                        Icons.Default.EditCalendar,
                        contentDescription = "stringResource(R.string.history_search_date)"
                    )
                }
            }

            WeekdayHeader()

            weeks.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    week.forEach { day ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (day != null) {
                                DayCell(
                                    day = day,
                                    onClick = {
                                        onDateSelected(day.date)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeekdayHeader() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        DayOfWeek.entries.forEach { weekday ->
            Text(
                text = weekday.getDisplayName(
                    TextStyle.NARROW,
                    Locale.getDefault()
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DayCell(
    day: CalendarDay,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    val description = if (day.hasRecords) {
        "Records available on ${day.date}"
    } else {
        day.date.toString()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 2.dp)
            .clip(CircleShape)
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .semantics {
                contentDescription = description
            }
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .then(
                    when {
                        day.isSelected -> Modifier.background(primary)
                        day.isToday -> Modifier.background(
                            primary.copy(alpha = 0.14f)
                        )
                        else -> Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (day.isToday || day.isSelected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
                color = if (day.isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(5.dp)
                .clip(CircleShape)
                .background(
                    if (day.hasRecords) {
                        primary
                    } else {
                        Color.Transparent
                    }
                )
        )
    }
}

// -----------------------------------------------------------------------------
// Filter and empty state
// -----------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun FilterRow(
    selectedDate: LocalDate?,
    onShowAll: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = selectedDate?.format(
                DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")
            ) ?: "stringResource(R.string.history_showing_all)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        FilterChip(
            selected = selectedDate == null,
            onClick = onShowAll,
            label = {
                Text("stringResource(R.string.history_show_all)")
            }
        )
    }
}

@Composable
private fun EmptyRecords(
    isFiltered: Boolean,
    onAddRecord: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text ="day emp",
//                stringResource(
//                if (isFiltered) {
//                    R.string.history_empty_day
//                } else {
//                    R.string.history_empty_all
//                }
//            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedButton(onClick = onAddRecord) {
            Text("stringResource(R.string.editor_title_new)")
        }
    }
}

// -----------------------------------------------------------------------------
// Record card
// -----------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun RecordCard(
    record: HistoryRecord,
    showDate: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showDate) {
                Text(
                    text = record.date.format(
                        DateTimeFormatter.ofPattern("dd MMMM yyyy")
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            RecordLine(
                icon = Icons.Default.DirectionsWalk,
                label = "Steps",
                value = "%,d steps".format(record.steps),
                tint = MaterialTheme.colorScheme.primary
            )

            RecordLine(
                icon = Icons.Default.LocalFireDepartment,
                label = "Calories",
                value = "%,d kcal".format(record.calories),
                tint = MaterialTheme.colorScheme.tertiary
            )

            RecordLine(
                icon = Icons.Default.WaterDrop,
                label = "Water",
                value = "%,d ml".format(record.water),
                tint = MaterialTheme.colorScheme.secondary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onEdit) {
                    Text("stringResource(R.string.action_edit)")
                }

                TextButton(onClick = onDelete) {
                    Text(
                        "stringResource(R.string.action_delete)",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// -----------------------------------------------------------------------------
// Calendar generation
// -----------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
private fun generateCalendarWeeks(
    month: YearMonth,
    selectedDate: LocalDate?,
    records: List<HistoryRecord>
): List<List<CalendarDay?>> {
    val firstDay = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()

    // Monday is the first day of the week.
    val leadingEmptyDays = firstDay.dayOfWeek.value - 1

    val allDays = mutableListOf<CalendarDay?>()

    repeat(leadingEmptyDays) {
        allDays.add(null)
    }

    for (dayNumber in 1..daysInMonth) {
        val date = month.atDay(dayNumber)

        allDays.add(
            CalendarDay(
                date = date,
                isToday = date == LocalDate.now(),
                isSelected = date == selectedDate,
                hasRecords = records.any { it.date == date }
            )
        )
    }

    while (allDays.size % 7 != 0) {
        allDays.add(null)
    }

    return allDays.chunked(7)
}

// -----------------------------------------------------------------------------
// Date conversion helpers
// -----------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
private fun LocalDate.toEpochMillis(): Long {
    return atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Long.toLocalDate(): LocalDate {
    return java.time.Instant
        .ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
}

// -----------------------------------------------------------------------------
// Preview
// -----------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    MaterialTheme {
        HistoryScreen()
    }
}
