package com.example.healthmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel

@Composable
fun HealthMateApp(viewModel: HealthViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectionAsState()
}
