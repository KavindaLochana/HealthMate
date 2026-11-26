package com.example.healthmate.feature.profile

import androidx.compose.runtime.Composable
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.healthmate.domain.model.MetricType
import com.example.healthmate.R
import com.example.healthmate.core.ui.accentColor
import com.example.healthmate.core.ui.icon
import com.example.healthmate.core.ui.labelRes
import com.example.healthmate.core.ui.message
import com.example.healthmate.core.ui.valueFormatRes
import com.example.healthmate.core.util.formatNumber
import com.example.healthmate.domain.model.DailyTargets
import com.example.healthmate.domain.model.User


// all user actions on profile
data class ProfileActions(
    val onTargetClick: (MetricType) -> Unit = {},
    val onTargetInputChange: (String) -> Unit = {},
    val onTargetConfirm: () -> Unit = {},
    val onAppLockToggle: (Boolean) -> Unit = {},
    val onChangePasscodeClick: () -> Unit = {},
    val onCurrentPasscodeChange: (String) -> Unit = {},
    val onNewPasscodeChange: (String) -> Unit = {},
    val onConfirmPasscodeChange: (String) -> Unit = {},
    val onPasscodeConfirm: () -> Unit = {},
    val onResetSampleDataClick: () -> Unit = {},
    val onResetSampleDataConfirm: () -> Unit = {},
    val onLogoutClick: () -> Unit = {},
    val onLogoutConfirm: () -> Unit = {},
    val onDialogDismiss: () -> Unit = {},
    val onMessageShown: () -> Unit = {},
)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileContent(
        state = state,
        actions = ProfileActions(
            onTargetClick = viewModel::onTargetClick,
            onTargetInputChange = viewModel::onTargetInputChange,
            onTargetConfirm = viewModel::onTargetConfirm,
            onAppLockToggle = viewModel::onAppLockToggle,
            onChangePasscodeClick = viewModel::onChangePasscodeClick,
            onCurrentPasscodeChange = viewModel::onCurrentPasscodeChange,
            onNewPasscodeChange = viewModel::onNewPasscodeChange,
            onConfirmPasscodeChange = viewModel::onConfirmPasscodeChange,
            onPasscodeConfirm = viewModel::onPasscodeConfirm,
            onResetSampleDataClick = viewModel::onResetSampleDataClick,
            onResetSampleDataConfirm = viewModel::onResetSampleDataConfirm,
            onLogoutClick = viewModel::onLogoutClick,
            onLogoutConfirm = viewModel::onLogoutConfirm,
            onDialogDismiss = viewModel::onDialogDismiss,
            onMessageShown = viewModel::onMessageShown,
        ),
        modifier = modifier,
    )
}

@Composable
fun ProfileContent(
    state: ProfileUiState,
    actions: ProfileActions,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val messageText = state.message?.let { stringResource(it.textRes) }
    LaunchedEffect(state.message) {
        if (messageText != null) {
            snackbarHostState.showSnackbar(messageText)
            actions.onMessageShown()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
        ) {
            Text(
                text = stringResource(R.string.profile_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 16.dp),
            )

            AccountHeader(state.user)

            SectionHeader(R.string.profile_targets, caption = R.string.profile_targets_caption)
            MetricType.entries.forEach { metric ->
                TargetRow(
                    metric = metric,
                    targets = state.targets,
                    onClick = { actions.onTargetClick(metric) })
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            SectionHeader(R.string.profile_security)
            ListItem(
                headlineContent = { Text(stringResource(R.string.profile_app_lock)) },
                supportingContent = { Text(stringResource(R.string.profile_app_lock_summary)) },
                leadingContent = { Icon(Icons.Filled.Lock, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = state.isAppLockEnabled,
                        onCheckedChange = actions.onAppLockToggle
                    )
                },
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.profile_change_passcode)) },
                leadingContent = { Icon(Icons.Filled.LockReset, contentDescription = null) },
                modifier = if (state.isAppLockEnabled) {
                    Modifier.clickable(onClick = actions.onChangePasscodeClick)
                } else {
                    Modifier
                },
                colors = androidx.compose.material3.ListItemDefaults.colors(
                    headlineColor = if (state.isAppLockEnabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    },
                ),
            )

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            SectionHeader(R.string.profile_database)
            ListItem(
                headlineContent = { Text(stringResource(R.string.profile_reset_sample)) },
                supportingContent = { Text(stringResource(R.string.profile_reset_sample_summary)) },
                leadingContent = { Icon(Icons.Filled.RestartAlt, contentDescription = null) },
                modifier = Modifier.clickable(onClick = actions.onResetSampleDataClick),
            )

            OutlinedButton(
                onClick = actions.onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    stringResource(R.string.profile_logout),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        )
    }

    ProfileDialogs(dialog = state.dialog, actions = actions)
}

@get:StringRes
private val ProfileMessage.textRes: Int
    get() = when (this) {
        ProfileMessage.TARGETS_SAVED -> R.string.message_targets_saved
        ProfileMessage.SAMPLE_DATA_RESET -> R.string.message_sample_reset
        ProfileMessage.APP_LOCK_ENABLED -> R.string.message_lock_enabled
        ProfileMessage.APP_LOCK_DISABLED -> R.string.message_lock_disabled
        ProfileMessage.PASSCODE_CHANGED -> R.string.message_passcode_changed
    }

@Composable
private fun AccountHeader(user: User?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = user?.initial.orEmpty(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Text(
            text = user?.name.orEmpty(),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = user?.email.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SectionHeader(@StringRes title: Int, @StringRes caption: Int? = null) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        if (caption != null) {
            Text(
                text = stringResource(caption),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TargetRow(metric: MetricType, targets: DailyTargets, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(stringResource(metric.labelRes)) },
        leadingContent = {
            Icon(
                metric.icon,
                contentDescription = null,
                tint = metric.accentColor
            )
        },
        trailingContent = {
            Text(
                text = stringResource(
                    metric.valueFormatRes,
                    formatNumber(targets.targetOf(metric))
                ),
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        modifier = Modifier.clickable(onClick = onClick),
    )
}

//dialogs

@Composable
private fun ProfileDialogs(dialog: ProfileDialog?, actions: ProfileActions) {
    when (dialog) {
        null -> Unit
        is ProfileDialog.EditTarget -> TargetDialog(dialog, actions)
        is ProfileDialog.Passcode -> PasscodeDialog(dialog, actions)
        ProfileDialog.ConfirmReset -> ConfirmDialog(
            title = R.string.reset_title,
            message = R.string.reset_message,
            confirmLabel = R.string.action_reset,
            onConfirm = actions.onResetSampleDataConfirm,
            onDismiss = actions.onDialogDismiss,
        )

        ProfileDialog.ConfirmLogout -> ConfirmDialog(
            title = R.string.logout_title,
            message = R.string.logout_message,
            confirmLabel = R.string.profile_logout,
            onConfirm = actions.onLogoutConfirm,
            onDismiss = actions.onDialogDismiss,
        )
    }
}

@Composable
private fun TargetDialog(
    dialog: ProfileDialog.EditTarget,
    actions: ProfileActions
) {
    AlertDialog(
        onDismissRequest = actions.onDialogDismiss,
        title = {
            Text(
                stringResource(
                    R.string.profile_edit_target_title,
                    stringResource(dialog.metric.labelRes)
                )
            )
        },
        text = {
            OutlinedTextField(
                value = dialog.input,
                onValueChange = actions.onTargetInputChange,
                singleLine = true,
                isError = dialog.error != null,
                supportingText = dialog.error?.let { { Text(it.message(dialog.metric)) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        },
        confirmButton = {
            TextButton(onClick = actions.onTargetConfirm) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = actions.onDialogDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}

@Composable
private fun PasscodeDialog(
    dialog: ProfileDialog.Passcode,
    actions: ProfileActions
) {
    val titleRes = when (dialog.mode) {
        PasscodeMode.ENABLE -> R.string.passcode_enable_title
        PasscodeMode.CHANGE -> R.string.passcode_change_title
        PasscodeMode.DISABLE -> R.string.passcode_disable_title
    }
    val errorRes = when (dialog.error) {
        PasscodeError.INVALID_FORMAT -> R.string.passcode_error_format
        PasscodeError.MISMATCH -> R.string.passcode_error_mismatch
        PasscodeError.WRONG_CURRENT -> R.string.passcode_error_wrong
        null -> null
    }
    val needsCurrent = dialog.mode != PasscodeMode.ENABLE
    val needsNew = dialog.mode != PasscodeMode.DISABLE

    AlertDialog(
        onDismissRequest = actions.onDialogDismiss,
        title = { Text(stringResource(titleRes)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (needsCurrent) {
                    PasscodeField(
                        value = dialog.current,
                        label = R.string.passcode_current,
                        onValueChange = actions.onCurrentPasscodeChange,
                        isError = dialog.error == PasscodeError.WRONG_CURRENT,
                    )
                }
                if (needsNew) {
                    PasscodeField(
                        value = dialog.new,
                        label = R.string.passcode_new,
                        onValueChange = actions.onNewPasscodeChange,
                        isError = dialog.error == PasscodeError.INVALID_FORMAT,
                    )
                    PasscodeField(
                        value = dialog.confirm,
                        label = R.string.passcode_confirm,
                        onValueChange = actions.onConfirmPasscodeChange,
                        isError = dialog.error == PasscodeError.MISMATCH,
                    )
                    Text(
                        text = stringResource(R.string.passcode_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (errorRes != null) {
                    Text(
                        text = stringResource(errorRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = actions.onPasscodeConfirm) { Text(stringResource(R.string.action_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = actions.onDialogDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}

@Composable
private fun PasscodeField(
    value: String,
    @StringRes label: Int,
    onValueChange: (String) -> Unit,
    isError: Boolean,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label)) },
        singleLine = true,
        isError = isError,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
    )
}

@Composable
private fun ConfirmDialog(
    @StringRes title: Int,
    @StringRes message: Int,
    @StringRes confirmLabel: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(title)) },
        text = { Text(stringResource(message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(confirmLabel), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun ProfileScreenPreview() {
//    MaterialTheme {
//        ProfileScreen()
//    }
//}