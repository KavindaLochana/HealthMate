package com.example.healthmate.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.text.get
import com.example.healthmate.R
import com.example.healthmate.core.ui.messageRes
import com.example.healthmate.domain.validation.AuthError
import com.example.healthmate.domain.validation.AuthField


@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onBackToSignIn: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    RegisterContent(
        state = state,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onCreateAccountClick = viewModel::onCreateAccountClick,
        onBackToSignIn = onBackToSignIn,
    )
}

/** Stateless create-account form. */
@Composable
fun RegisterContent(
    state: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onCreateAccountClick: () -> Unit,
    onBackToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The confirm field reports a mismatch live; submit-time errors take priority.
    val confirmError: String? = when {
        state.fieldErrors[AuthField.CONFIRM_PASSWORD] != null ->
            stringResource(state.fieldErrors.getValue(AuthField.CONFIRM_PASSWORD).messageRes)

        state.passwordsMismatch -> stringResource(AuthError.PASSWORDS_DO_NOT_MATCH.messageRes)
        else -> null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AuthLogo()
        Text(
            text = stringResource(R.string.auth_create_account),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
        )

        Column(
            modifier = Modifier.widthIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            AuthTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = stringResource(R.string.auth_name),
                modifier = Modifier.fillMaxWidth(),
                errorText = state.fieldErrors[AuthField.NAME]?.let { stringResource(it.messageRes) },
            )
            AuthTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.auth_email),
                modifier = Modifier.fillMaxWidth(),
                errorText = state.fieldErrors[AuthField.EMAIL]?.let { stringResource(it.messageRes) },
                keyboardType = KeyboardType.Email,
            )
            PasswordTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.auth_password),
                modifier = Modifier.fillMaxWidth(),
                errorText = state.fieldErrors[AuthField.PASSWORD]?.let { stringResource(it.messageRes) },
            )
            PasswordTextField(
                value = state.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = stringResource(R.string.auth_confirm_password),
                modifier = Modifier.fillMaxWidth(),
                errorText = confirmError,
                imeAction = ImeAction.Done,
                onImeAction = onCreateAccountClick,
            )

            if (state.formError == AuthFormError.EMAIL_TAKEN) {
                Text(
                    text = stringResource(R.string.auth_error_email_taken),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Button(
                onClick = onCreateAccountClick,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(top = 8.dp),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(stringResource(R.string.auth_create_account))
                }
            }
            TextButton(onClick = onBackToSignIn, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.auth_back_to_sign_in))
            }
        }

        Text(
            text = stringResource(R.string.auth_register_footer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp),
        )
    }
}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun RegisterScreenPreview() {
//    MaterialTheme {
//        RegisterContent(
//            state = RegisterUiState(
//                name = "Jane Doe",
//                email = "jane.doe@example.com",
//                password = "password123",
//                confirmPassword = "password123",
//                isLoading = false
//            ),
//            onNameChange = {},
//            onEmailChange = {},
//            onPasswordChange = {},
//            onConfirmPasswordChange = {},
//            onCreateAccountClick = {},
//            onBackToSignIn = {}
//        )
//    }
//}