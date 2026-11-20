package com.example.healthmate.feature.auth


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    onBackToSignIn: () -> Unit = {},
    modifier: Modifier = Modifier,
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    RegisterContent(
        name = name,
        email = email,
        password = password,
        confirmPassword = confirmPassword,
        onNameChange = { name = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onConfirmPasswordChange = { confirmPassword = it },
        onCreateAccountClick = {},
        onBackToSignIn = onBackToSignIn,
        modifier = modifier
    )

}

@Composable
private fun RegisterContent(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onCreateAccountClick: () -> Unit,
    onBackToSignIn: () -> Unit,
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
        verticalArrangement = Arrangement.Center
    ) {
        AuthLogo()

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
        )

        Column(
            modifier = Modifier.widthIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AuthTextField(
                value = name,
                onValueChange = onNameChange,
                label = "Full Name",
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Next
            )

            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )

            PasswordTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Next
            )

            PasswordTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Confirm Password",
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Done,
                onImeAction = onCreateAccountClick
            )

            Button(
                onClick = onCreateAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(top = 8.dp)
            ) {
                Text("Create Account")
            }

            TextButton(
                onClick = onBackToSignIn,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Already have an account? Sign In")
            }
        }

        Text(
            text = "Your health journey starts here.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen()
    }
}