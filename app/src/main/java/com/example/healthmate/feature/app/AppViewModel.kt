package com.example.healthmate.feature.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.core.session.AppLockState
import com.example.healthmate.domain.repository.AuthRepository
import com.example.healthmate.domain.repository.SettingsRepository
import com.example.healthmate.domain.usecase.SeedSampleDataUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// top level states
sealed interface AppState {
    data object Loading : AppState
    data object Unauthenticated : AppState
    data object Locked : AppState
    data object Authenticated : AppState
}

class AppViewModel(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val appLockState: AppLockState,
    seedSampleData: SeedSampleDataUseCase,
) : ViewModel() {

    val uiState: StateFlow<AppState> = combine(
        authRepository.currentUser,
        settingsRepository.isAppLockEnabled,
        appLockState.isUnlocked,
    ) { user, lockEnabled, unlocked ->
        when {
            user == null -> AppState.Unauthenticated
            lockEnabled && !unlocked -> AppState.Locked
            else -> AppState.Authenticated
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AppState.Loading)

    init {
        viewModelScope.launch { seedSampleData() }

        // no lock enabled -> unlock immediately
        // lock enabled -> unlock when logging out
        viewModelScope.launch {
            settingsRepository.isAppLockEnabled.filter { enabled -> !enabled }.collect { appLockState.unlock() }
        }
        // immediate login logout should not ask password
        viewModelScope.launch {
            authRepository.currentUser
                .map { user -> user == null }
                .distinctUntilChanged()
                .filter { loggedOut -> loggedOut }
                .collect { appLockState.unlock() }
        }
    }

    // lock when app is backgrounded and user is signed in
    fun onAppBackgrounded() {
        viewModelScope.launch {
            val lockEnabled = settingsRepository.isAppLockEnabled.first()
            val signedIn = authRepository.currentUser.first() != null
            if (lockEnabled && signedIn) appLockState.lock()
        }
    }
}