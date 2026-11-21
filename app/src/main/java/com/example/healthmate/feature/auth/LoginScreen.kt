package com.example.healthmate.feature.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onCreateAccountClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LoginContent (
    email = email,
    password = password,
    onEmailChange = { email = it },
    onPasswordChange = { password = it },
    onSignInClick = {},
    onCreateAccountClick = onCreateAccountClick,
    modifier = modifier

    )
}

@Composable
private fun LoginContent(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthLogo()

        Text(
            text = "HealthMate",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        Column(
            modifier = Modifier.widthIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Email
            )

            PasswordTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Done,
                onImeAction = onSignInClick
            )
            Button(
                onClick = onSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Sign In")
            }

            TextButton(
                onClick = onCreateAccountClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Account")
            }

            Text(
                text = "Your health journey starts here.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 24.dp)
            )

        }
    }
}



@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen()
    }
}