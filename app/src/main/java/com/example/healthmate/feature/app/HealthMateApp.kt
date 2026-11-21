package com.example.healthmate.feature.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmate.core.navigation.MainScaffold
import com.example.healthmate.core.ui.theme.HealthMateTheme
import com.example.healthmate.feature.auth.AuthHost
import com.example.healthmate.feature.lock.LockScreen

// root composable

@Composable
fun HealthMateApp(
    appViewModel: AppViewModel,
    viewModelFactory: ViewModelProvider.Factory,
) {
    HealthMateTheme {
        val state by appViewModel.uiState.collectAsStateWithLifecycle()
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when (state) {
                AppState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                AppState.Unauthenticated -> AuthHost(viewModelFactory)
                AppState.Locked -> LockScreen(viewModel(factory = viewModelFactory))
                AppState.Authenticated -> MainScaffold(viewModelFactory)
            }
        }
    }
}
