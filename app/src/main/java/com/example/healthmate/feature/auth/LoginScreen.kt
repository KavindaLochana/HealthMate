package com.example.healthmate.feature.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.text.get
import com.example.healthmate.R
import com.example.healthmate.core.ui.messageRes
import com.example.healthmate.domain.validation.AuthField

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onCreateAccountClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LoginContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSignInClick = viewModel::onSignInClick,
        onCreateAccountClick = onCreateAccountClick,
    )
}

/** Stateless sign-in form (easy to preview and to test). */
@Composable
fun LoginContent(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = stringResource(R.string.auth_sign_in_title),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
        )

        Column(
            modifier = Modifier.widthIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
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
                imeAction = ImeAction.Done,
                onImeAction = onSignInClick,
            )

            if (state.formError == AuthFormError.INVALID_CREDENTIALS) {
                Text(
                    text = stringResource(R.string.auth_error_invalid_credentials),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Button(
                onClick = onSignInClick,
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
                    Text(stringResource(R.string.auth_sign_in))
                }
            }
            TextButton(
                onClick = onCreateAccountClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.auth_create_account))
            }
        }

        Text(
            text = stringResource(R.string.auth_sign_in_footer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp),
        )
    }
}


//
//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    MaterialTheme {
//        LoginScreen()
//    }
//}