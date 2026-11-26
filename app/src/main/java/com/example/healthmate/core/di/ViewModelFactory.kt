package com.example.healthmate.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.healthmate.feature.app.AppViewModel
import com.example.healthmate.feature.auth.LoginViewModel
import com.example.healthmate.feature.auth.RegisterViewModel
import com.example.healthmate.feature.dashboard.DashboardViewModel
import com.example.healthmate.feature.editor.RecordEditorViewModel
import com.example.healthmate.feature.history.HistoryViewModel
import com.example.healthmate.feature.lock.LockViewModel
import com.example.healthmate.feature.profile.ProfileViewModel

fun appViewModelFactory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        AppViewModel(
            authRepository = container.authRepository,
            settingsRepository = container.settingsRepository,
            appLockState = container.appLockState,
            seedSampleData = container.seedSampleDataUseCase,
        )
    }
    initializer { LoginViewModel(container.loginUseCase) }
    initializer { RegisterViewModel(container.registerUserUseCase) }
    initializer { LockViewModel(container.settingsRepository, container.appLockState) }
    initializer { DashboardViewModel(container.observeDashboardUseCase, container.clock) }
    initializer { HistoryViewModel(container.healthRecordRepository, container.clock) }
    initializer {
        RecordEditorViewModel(
            savedStateHandle = createSavedStateHandle(),
            repository = container.healthRecordRepository,
            saveHealthRecord = container.saveHealthRecordUseCase,
            clock = container.clock,
        )
    }
    initializer {
        ProfileViewModel(
            authRepository = container.authRepository,
            settings = container.settingsRepository,
            resetSampleData = container.resetSampleDataUseCase,
            targetValidator = container.targetValidator,
        )
    }
}