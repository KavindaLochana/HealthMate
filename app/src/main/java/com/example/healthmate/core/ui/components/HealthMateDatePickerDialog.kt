package com.example.healthmate.core.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.healthmate.core.util.toUtcMillis
import com.example.healthmate.core.util.utcMillisToLocalDate
import java.time.LocalDate
import com.example.healthmate.R

@Composable
fun HealthMateDatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    maxDate: LocalDate? = null,
) {
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toUtcMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                maxDate == null || !utcTimeMillis.utcMillisToLocalDate().isAfter(maxDate)
        },
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    pickerState.selectedDateMillis?.let { onDateSelected(it.utcMillisToLocalDate()) }
                    onDismiss()
                },
            ) { Text(stringResource(R.string.action_ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    ) {
        DatePicker(state = pickerState)
    }
}