package com.example.healthmate.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthHost(viewModelFactory: ViewModelProvider.Factory) {
    var showRegister by rememberSaveable { mutableStateOf(false) }

    if (showRegister) {
        val viewModel: RegisterViewModel = viewModel(factory = viewModelFactory)
        RegisterScreen(viewModel = viewModel, onBackToSignIn = { showRegister = false })
    } else {
        val viewModel: LoginViewModel = viewModel(factory = viewModelFactory)
        LoginScreen(viewModel = viewModel, onCreateAccountClick = { showRegister = true })
    }
}
