package com.example.healthmate.feature

import androidx.lifecycle.ViewModel

sealed interface AppState {
    data object Loading : AppState
    data object Authenticated : AppState
    data object Locked: AppState
    data object Unauthenticated : AppState
}

class AppViewModel (
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val appLockState: AppLockState,
    seedSampleData: SeedSampleDataUseCase
) : ViewModel() {



}