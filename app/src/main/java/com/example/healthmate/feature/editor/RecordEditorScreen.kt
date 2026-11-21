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
import com.example.healthmate.R
import com.example.healthmate.core.ui.components.HealthMateDatePickerDialog
import com.example.healthmate.domain.model.MetricType
import com.example.healthmate.domain.validation.RecordField


@Composable
fun RecordEditorScreen(
    viewModel: RecordEditorViewModel,
    onClose: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // The ViewModel reports "finished" once the record is saved; navigate back exactly once.
    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onClose()
    }

    RecordEditorContent(
        state = state,
        onDateChange = viewModel::onDateChange,
        onStepsChange = viewModel::onStepsChange,
        onCaloriesChange = viewModel::onCaloriesChange,
        onWaterChange = viewModel::onWaterChange,
        onSaveClick = viewModel::onSaveClick,
        onBackClick = onClose,
    )
}

// new record / edit screen
@Composable
fun RecordEditorContent(
    state: RecordEditorUiState,
    onDateChange: (java.time.LocalDate) -> Unit,
    onStepsChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onWaterChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (state.isEditing) R.string.editor_title_edit else R.string.editor_title_new,
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { innerPadding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DateField(
                dateText = state.date.toFullDateString(),
                errorText = state.errors[RecordField.DATE]?.message(null),
                onClick = { showDatePicker = true },
            )
            MetricField(
                metric = MetricType.STEPS,
                value = state.steps,
                onValueChange = onStepsChange,
                errorText = state.errors[RecordField.STEPS]?.message(MetricType.STEPS),
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
            )
            MetricField(
                metric = MetricType.CALORIES,
                value = state.calories,
                onValueChange = onCaloriesChange,
                errorText = state.errors[RecordField.CALORIES]?.message(MetricType.CALORIES),
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
            )
            MetricField(
                metric = MetricType.WATER,
                value = state.water,
                onValueChange = onWaterChange,
                errorText = state.errors[RecordField.WATER]?.message(MetricType.WATER),
                imeAction = ImeAction.Done,
                onImeAction = {
                    focusManager.clearFocus()
                    onSaveClick()
                },
            )

            Button(
                onClick = onSaveClick,
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(top = 8.dp),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(stringResource(R.string.editor_save))
                }
            }
        }
    }

    if (showDatePicker) {
        HealthMateDatePickerDialog(
            initialDate = state.date,
            maxDate = state.today,
            onDateSelected = onDateChange,
            onDismiss = { showDatePicker = false },
        )
    }
}

// date picker read only
@Composable
private fun DateField(
    dateText: String,
    errorText: String?,
    onClick: () -> Unit,
) {
    // A read-only OutlinedTextField swallows clicks, so a pressed-state interaction source
    // is used to detect the tap and open the picker.
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    LaunchedEffect(pressed) {
        if (pressed) onClick()
    }

    OutlinedTextField(
        value = dateText,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.editor_date)) },
        leadingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = stringResource(R.string.editor_pick_date)) },
        isError = errorText != null,
        supportingText = errorText?.let { { Text(it) } },
        singleLine = true,
        interactionSource = interactionSource,
    )
}

@Composable
private fun MetricField(
    metric: MetricType,
    value: String,
    onValueChange: (String) -> Unit,
    errorText: String?,
    imeAction: ImeAction,
    onImeAction: () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(metric.labelRes)) },
        leadingIcon = { Icon(metric.icon, contentDescription = null, tint = metric.accentColor) },
        suffix = { Text(stringResource(metric.unitRes)) },
        isError = errorText != null,
        supportingText = errorText?.let { { Text(it) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = imeAction),
        keyboardActions = KeyboardActions(onAny = { onImeAction() }),
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RecordEditorScreenPreview() {
    MaterialTheme {
        RecordEditorScreen()
    }
}
