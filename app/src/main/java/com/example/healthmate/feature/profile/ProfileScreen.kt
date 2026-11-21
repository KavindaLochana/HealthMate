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

@Composable
fun ProfileScreen (
    modifier: Modifier = Modifier
) {

    var isAppLockEnabled by remember { mutableStateOf(true) }

    var calories by remember { mutableStateOf("2000 kcal") }
    var water by remember { mutableStateOf("2.5 L") }
    var steps by remember { mutableStateOf("8000 steps") }
    var sleep by remember { mutableStateOf("8 hours") }

    var selectedTarget by remember { mutableStateOf("") }
    var targetInput by remember { mutableStateOf("") }

    var showPasscodeDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    var currentPasscode by remember { mutableStateOf("") }
    var newPasscode by remember { mutableStateOf("") }
    var confirmPasscode by remember { mutableStateOf("") }


    fun openTargetDialog(title: String, value: String) {
        selectedTarget = title
        targetInput = value.substringBefore(" ")
    }

    fun saveTarget() {
        when (selectedTarget) {
            "Calories" -> calories = "$targetInput kcal"
            "Water" -> water = "$targetInput L"
            "Steps" -> steps = "$targetInput steps"
            "Sleep" -> sleep = "$targetInput hours"
        }
        selectedTarget = ""
    }

    ProfileContent(
        modifier = modifier,
        isAppLockEnabled = isAppLockEnabled,
        calories = calories,
        water = water,
        steps = steps,
        sleep = sleep,
        onAppLockToggle = {
            isAppLockEnabled = it
        },
        onTargetClick = ::openTargetDialog,
        onChangePasscodeClick = {
            showPasscodeDialog = true
        },
        onResetClick = {
            showResetDialog = true
        },
        onLogoutClick = {
            showLogoutDialog = true
        }
    )

    if (selectedTarget.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { selectedTarget = "" },
            title = {
                Text("Edit $selectedTarget Target")
            },
            text = {
                OutlinedTextField(
                    value = targetInput,
                    onValueChange = { targetInput = it },
                    label = { Text("Target value") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = ::saveTarget) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTarget = "" }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showPasscodeDialog) {
        AlertDialog(
            onDismissRequest = { showPasscodeDialog = false },
            title = {
                Text("Change Passcode")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PasscodeInput(
                        value = currentPasscode,
                        onValueChange = { currentPasscode = it },
                        label = "Current Passcode"
                    )

                    PasscodeInput(
                        value = newPasscode,
                        onValueChange = { newPasscode = it },
                        label = "New Passcode"
                    )

                    PasscodeInput(
                        value = confirmPasscode,
                        onValueChange = { confirmPasscode = it },
                        label = "Confirm Passcode"
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showPasscodeDialog = false
                    currentPasscode = ""
                    newPasscode = ""
                    confirmPasscode = ""
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPasscodeDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showResetDialog) {
        ConfirmationDialog(
            title = "Reset Sample Data",
            message = "Are you sure you want to reset the sample data?",
            confirmLabel = "Reset",
            onConfirm = {
                showResetDialog = false
            },
            onDismiss = {
                showResetDialog = false
            }
        )
    }

    if (showLogoutDialog) {
        ConfirmationDialog(
            title = "Log Out",
            message = "Are you sure you want to log out of your account?",
            confirmLabel = "Log Out",
            onConfirm = {
                showLogoutDialog = false
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }
}

@Composable
private fun ProfileContent(
    isAppLockEnabled: Boolean,
    calories: String,
    water: String,
    steps: String,
    sleep: String,
    onAppLockToggle: (Boolean) -> Unit,
    onTargetClick: (String, String) -> Unit,
    onChangePasscodeClick: () -> Unit,
    onResetClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(
                start = 20.dp,
                top = 16.dp,
                bottom = 16.dp
            )
        )

        AccountHeader()

        SectionHeader(
            title = "Daily Targets",
            caption = "Manage your daily health goals"
        )

        TargetRow("Calories", calories) {
            onTargetClick("Calories", calories)
        }

        TargetRow("Water", water) {
            onTargetClick("Water", water)
        }

        TargetRow("Steps", steps) {
            onTargetClick("Steps", steps)
        }

        TargetRow("Sleep", sleep) {
            onTargetClick("Sleep", sleep)
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp)
        )

        SectionHeader("Security")

        ListItem(
            headlineContent = {
                Text("App Lock")
            },
            supportingContent = {
                Text("Require a passcode to access the app")
            },
            leadingContent = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null
                )
            },
            trailingContent = {
                Switch(
                    checked = isAppLockEnabled,
                    onCheckedChange = onAppLockToggle
                )
            }
        )

        ListItem(
            headlineContent = {
                Text("Change Passcode")
            },
            leadingContent = {
                Icon(
                    Icons.Default.LockReset,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .clickable(
                    enabled = isAppLockEnabled,
                    onClick = onChangePasscodeClick
                ),
            colors = ListItemDefaults.colors(
                headlineColor = if (isAppLockEnabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp)
        )

        SectionHeader("Database")

        ListItem(
            headlineContent = {
                Text("Reset Sample Data")
            },
            supportingContent = {
                Text("Restore the application sample data")
            },
            leadingContent = {
                Icon(
                    Icons.Default.RestartAlt,
                    contentDescription = null
                )
            },
            modifier = Modifier.clickable(
                onClick = onResetClick
            )
        )

        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Text(
                "Log Out",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun AccountHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "K",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Text(
            text = "Kavinda",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "kavinda@example.com",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    caption: String? = null
) {
    Column(
        modifier = Modifier.padding(
            horizontal = 20.dp,
            vertical = 8.dp
        )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        if (caption != null) {
            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TargetRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(title)
        },
        trailingContent = {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun PasscodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ConfirmationDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {
            Text(message)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    confirmLabel,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}




@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen()
    }
}