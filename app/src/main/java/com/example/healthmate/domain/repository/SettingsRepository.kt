package com.example.healthmate.domain.repository

import com.example.healthmate.domain.model.DailyTargets
import kotlinx.coroutines.flow.Flow

// user settings
interface SettingsRepository {
    val targets: Flow<DailyTargets>
    val isAppLockEnabled: Flow<Boolean>

    suspend fun updateTargets(targets: DailyTargets)

    suspend fun enableAppLock(passcode: String)

    // disable lock when current pw is correct
    suspend fun disableAppLock(currentPasscode: String): Boolean

    suspend fun changePasscode(current: String, new: String): Boolean

    suspend fun verifyPasscode(passcode: String): Boolean

    suspend fun isSampleDataSeeded(): Boolean
    suspend fun markSampleDataSeeded()
}