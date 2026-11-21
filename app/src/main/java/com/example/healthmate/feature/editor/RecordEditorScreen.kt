@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.healthmate.feature.editor

import android.os.Build
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp



import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun RecordEditorScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {}
) {
    var date by rememberSaveable {
        mutableStateOf(LocalDate.now().toString())
    }

    var steps by rememberSaveable {
        mutableStateOf("8000")
    }

    var calories by rememberSaveable {
        mutableStateOf("2000")
    }

    var water by rememberSaveable {
        mutableStateOf("2.5")
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showSaveMessage by remember {
        mutableStateOf(false)
    }

    RecordEditorContent(
        date = date,
        steps = steps,
        calories = calories,
        water = water,
        onDateClick = {
            showDatePicker = true
        },
        onStepsChange = {
            steps = it
        },
        onCaloriesChange = {
            calories = it
        },
        onWaterChange = {
            water = it
        },
        onSaveClick = {
            showSaveMessage = true
        },
        onBackClick = onClose,
        modifier = modifier
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.toDateMillis()
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            date = it.toLocalDate().toString()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    if (showSaveMessage) {
        AlertDialog(
            onDismissRequest = {
                showSaveMessage = false
            },
            title = {
                Text("Record saved")
            },
            text = {
                Text("This is a UI preview. No record was actually saved.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSaveMessage = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun RecordEditorContent(
    date: String,
    steps: String,
    calories: String,
    water: String,
    onDateClick: () -> Unit,
    onStepsChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onWaterChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("New Record")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DateField(
                date = date,
                onClick = onDateClick
            )

            MetricField(
                value = steps,
                onValueChange = onStepsChange,
                label = "Steps",
                unit = "steps",
                icon = {
                    Icon(
                        Icons.Default.DirectionsWalk,
                        contentDescription = null
                    )
                },
                imeAction = ImeAction.Next,
                onImeAction = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )

            MetricField(
                value = calories,
                onValueChange = onCaloriesChange,
                label = "Calories",
                unit = "kcal",
                icon = {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = null
                    )
                },
                imeAction = ImeAction.Next,
                onImeAction = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )

            MetricField(
                value = water,
                onValueChange = onWaterChange,
                label = "Water",
                unit = "L",
                icon = {
                    Icon(
                        Icons.Default.WaterDrop,
                        contentDescription = null
                    )
                },
                imeAction = ImeAction.Done,
                onImeAction = {
                    focusManager.clearFocus()
                    onSaveClick()
                }
            )

            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(top = 8.dp)
            ) {
                Text("Save Record")
            }
        }
    }
}

@Composable
private fun DateField(
    date: String,
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val pressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(pressed) {
        if (pressed) onClick()
    }

    val formattedDate = remember(date) {
        LocalDate.parse(date).format(
            DateTimeFormatter.ofPattern("dd MMMM yyyy")
        )
    }

    OutlinedTextField(
        value = formattedDate,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text("Date")
        },
        leadingIcon = {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = "Select date"
            )
        },
        singleLine = true,
        interactionSource = interactionSource
    )
}

@Composable
private fun MetricField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    unit: String,
    icon: @Composable () -> Unit,
    imeAction: ImeAction,
    onImeAction: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        leadingIcon = icon,
        suffix = {
            Text(unit)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onAny = {
                onImeAction()
            }
        )
    )
}

private fun String.toDateMillis(): Long {
    return LocalDate.parse(this)
        .atStartOfDay(java.time.ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()
}

private fun Long.toLocalDate(): LocalDate {
    return java.time.Instant.ofEpochMilli(this)
        .atZone(java.time.ZoneOffset.UTC)
        .toLocalDate()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RecordEditorScreenPreview() {
    MaterialTheme {
        RecordEditorScreen()
    }
}
