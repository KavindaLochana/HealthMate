package com.example.healthmate.core.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.healthmate.core.security.PasswordHasher
import com.example.healthmate.core.security.Pbkdf2PasswordHasher
import com.example.healthmate.core.session.AppLockState
import com.example.healthmate.data.local.db.AppDatabase
import com.example.healthmate.data.local.prefs.AppPreferences
import com.example.healthmate.data.repository.AuthRepositoryImpl
import com.example.healthmate.data.repository.HealthRecordRepositoryImpl
import com.example.healthmate.data.repository.SettingsRepositoryImpl
import com.example.healthmate.domain.repository.AuthRepository
import com.example.healthmate.domain.repository.HealthRecordRepository
import com.example.healthmate.domain.repository.SettingsRepository
import com.example.healthmate.domain.usecase.LoginUseCase
import com.example.healthmate.domain.usecase.ObserveDashboardUseCase
import com.example.healthmate.domain.usecase.RegisterUserUseCase
import com.example.healthmate.domain.usecase.ResetSampleDataUseCase
import com.example.healthmate.domain.usecase.SaveHealthRecordUseCase
import com.example.healthmate.domain.usecase.SeedSampleDataUseCase
import com.example.healthmate.domain.validation.AuthValidator
import com.example.healthmate.domain.validation.RecordValidator
import com.example.healthmate.domain.validation.TargetValidator
import java.time.Clock

private val Context.healthMateDataStore by preferencesDataStore(name = "healthmate_preferences")

// Di- viewmodels only see usecases and interfaces

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val clock: Clock = Clock.systemDefaultZone()

    private val database: AppDatabase = AppDatabase.create(appContext)
    private val preferences = AppPreferences(appContext.healthMateDataStore)
    private val hasher: PasswordHasher = Pbkdf2PasswordHasher()

    val healthRecordRepository: HealthRecordRepository =
        HealthRecordRepositoryImpl(database.healthRecordDao())
    val authRepository: AuthRepository = AuthRepositoryImpl(database.userDao(), preferences, hasher)
    val settingsRepository: SettingsRepository = SettingsRepositoryImpl(preferences, hasher)

    val appLockState = AppLockState()

    val authValidator = AuthValidator()
    val targetValidator = TargetValidator()
    private val recordValidator = RecordValidator(clock)

    val loginUseCase = LoginUseCase(authRepository, authValidator)
    val registerUserUseCase = RegisterUserUseCase(authRepository, authValidator)
    val observeDashboardUseCase =
        ObserveDashboardUseCase(healthRecordRepository, settingsRepository)
    val saveHealthRecordUseCase = SaveHealthRecordUseCase(healthRecordRepository, recordValidator)
    val seedSampleDataUseCase =
        SeedSampleDataUseCase(healthRecordRepository, settingsRepository, clock)
    val resetSampleDataUseCase = ResetSampleDataUseCase(healthRecordRepository, clock)
}