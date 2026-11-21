package com.example.healthmate.feature.lock


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.healthmate.R

private const val PASSCODE_LENGTH = 4
private val KEY_SIZE = 72.dp

// -----------------------------------------------------------------------------
// Lock screen
// -----------------------------------------------------------------------------

@Composable
fun LockScreen(
    modifier: Modifier = Modifier
) {
    var enteredDigits by rememberSaveable {
        mutableStateOf("")
    }

    var hasError by rememberSaveable {
        mutableStateOf(false)
    }

    LockContent(
        enteredDigits = enteredDigits,
        hasError = hasError,
        onDigit = { digit ->
            if (enteredDigits.length < PASSCODE_LENGTH) {
                enteredDigits += digit
                hasError = false
            }
        },
        onBackspace = {
            if (enteredDigits.isNotEmpty()) {
                enteredDigits = enteredDigits.dropLast(1)
                hasError = false
            }
        },
        modifier = modifier
    )
}

// -----------------------------------------------------------------------------
// Lock content
// -----------------------------------------------------------------------------

@Composable
private fun LockContent(
    enteredDigits: String,
    hasError: Boolean,
    onDigit: (Char) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Lock icon
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )

        // Title
        Text(
            text = stringResource(R.string.lock_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Passcode indicators
        Row(
            modifier = Modifier.padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(PASSCODE_LENGTH) { index ->
                val filled = index < enteredDigits.length

                val color = if (hasError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }

                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .then(
                            if (filled) {
                                Modifier.background(color)
                            } else {
                                Modifier.border(
                                    width = 2.dp,
                                    color = color,
                                    shape = CircleShape
                                )
                            }
                        )
                )
            }
        }

        // Error message
        Text(
            text = if (hasError) {
                stringResource(R.string.lock_wrong)
            } else {
                ""
            },
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.height(24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Numeric keypad
        Keypad(
            onDigit = onDigit,
            onBackspace = onBackspace
        )
    }
}

// -----------------------------------------------------------------------------
// Numeric keypad
// -----------------------------------------------------------------------------

@Composable
private fun Keypad(
    onDigit: (Char) -> Unit,
    onBackspace: () -> Unit
) {
    val rows = listOf(
        "123",
        "456",
        "789"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { digit ->
                    DigitKey(
                        digit = digit,
                        onDigit = onDigit
                    )
                }
            }
        }

        // Bottom row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(KEY_SIZE))

            DigitKey(
                digit = '0',
                onDigit = onDigit
            )

            Box(
                modifier = Modifier
                    .size(KEY_SIZE),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = stringResource(R.string.lock_delete),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onBackspace)
                        .padding(12.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Individual digit key
// -----------------------------------------------------------------------------

@Composable
private fun DigitKey(
    digit: Char,
    onDigit: (Char) -> Unit
) {
    FilledTonalButton(
        onClick = {
            onDigit(digit)
        },
        modifier = Modifier.size(KEY_SIZE),
        shape = CircleShape,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Text(
            text = digit.toString(),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

// -----------------------------------------------------------------------------
// Preview
// -----------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
private fun LockScreenPreview() {
    MaterialTheme {
        LockScreen()
    }
}
