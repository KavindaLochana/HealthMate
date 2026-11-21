package com.example.healthmate.data.repository

import com.example.healthmate.core.security.PasswordHasher
import com.example.healthmate.data.local.prefs.AppPreferences
import com.example.healthmate.domain.model.DailyTargets
import com.example.healthmate.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SettingsRepositoryImpl(
    private val preferences: AppPreferences,
    private val hasher: PasswordHasher,
    private val hashingDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SettingsRepository {

    override val targets: Flow<DailyTargets> = preferences.targets

    override val isAppLockEnabled: Flow<Boolean> =
        preferences.passcode.map { it != null }.distinctUntilChanged()

    override suspend fun updateTargets(targets: DailyTargets) = preferences.setTargets(targets)

    override suspend fun enableAppLock(passcode: String) {
        val hashed = withContext(hashingDispatcher) { hasher.hash(passcode) }
        preferences.setPasscode(hashed)
    }

    override suspend fun disableAppLock(currentPasscode: String): Boolean {
        if (!verifyPasscode(currentPasscode)) return false
        preferences.setPasscode(null)
        return true
    }

    override suspend fun changePasscode(current: String, new: String): Boolean {
        if (!verifyPasscode(current)) return false
        enableAppLock(new)
        return true
    }

    override suspend fun verifyPasscode(passcode: String): Boolean {
        val stored = preferences.passcode.first() ?: return false
        return withContext(hashingDispatcher) { hasher.verify(passcode, stored) }
    }

    override suspend fun isSampleDataSeeded(): Boolean = preferences.isSampleDataSeeded()

    override suspend fun markSampleDataSeeded() = preferences.markSampleDataSeeded()
}