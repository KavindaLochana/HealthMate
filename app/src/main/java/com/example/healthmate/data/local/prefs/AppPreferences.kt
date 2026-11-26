package com.example.healthmate.data.local.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.healthmate.core.security.HashedSecret
import com.example.healthmate.domain.model.DailyTargets
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class AppPreferences(private val dataStore: DataStore<Preferences>) {

    private val data: Flow<Preferences> = dataStore.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }

    val sessionUserId: Flow<Long?> = data.map { it[Keys.SESSION_USER_ID] }.distinctUntilChanged()


    val targets: Flow<DailyTargets> = data.map { prefs ->
        DailyTargets(
            steps = prefs[Keys.TARGET_STEPS]
                ?: DailyTargets.DEFAULT_STEPS,
            calories = prefs[Keys.TARGET_CALORIES]
                ?: DailyTargets.DEFAULT_CALORIES,
            water = prefs[Keys.TARGET_WATER]
                ?: DailyTargets.DEFAULT_WATER,
        )
    }.distinctUntilChanged()

    val passcode: Flow<HashedSecret?> = data.map { prefs ->
        val hash = prefs[Keys.PASSCODE_HASH]
        val salt = prefs[Keys.PASSCODE_SALT]
        if (hash != null && salt != null) HashedSecret(hash, salt) else null
    }.distinctUntilChanged()

    suspend fun setSessionUserId(id: Long?) {
        dataStore.edit { prefs ->
            if (id == null) prefs.remove(Keys.SESSION_USER_ID) else prefs[Keys.SESSION_USER_ID] = id
        }
    }

    suspend fun setTargets(targets: DailyTargets) {
        dataStore.edit { prefs ->
            prefs[Keys.TARGET_STEPS] = targets.steps
            prefs[Keys.TARGET_CALORIES] = targets.calories
            prefs[Keys.TARGET_WATER] = targets.water
        }
    }

    suspend fun setPasscode(secret: HashedSecret?) {
        dataStore.edit { prefs ->
            if (secret == null) {
                prefs.remove(Keys.PASSCODE_HASH)
                prefs.remove(Keys.PASSCODE_SALT)
            } else {
                prefs[Keys.PASSCODE_HASH] = secret.hash
                prefs[Keys.PASSCODE_SALT] = secret.salt
            }
        }
    }

    suspend fun isSampleDataSeeded(): Boolean = data.first()[Keys.SAMPLE_SEEDED] ?: false

    suspend fun markSampleDataSeeded() {
        dataStore.edit { it[Keys.SAMPLE_SEEDED] = true }
    }


    private object Keys {
        val SESSION_USER_ID = longPreferencesKey("session_user_id")
        val TARGET_STEPS = intPreferencesKey("target_steps")
        val TARGET_CALORIES = intPreferencesKey("target_calories")
        val TARGET_WATER = intPreferencesKey("target_water")
        val PASSCODE_HASH = stringPreferencesKey("passcode_hash")
        val PASSCODE_SALT = stringPreferencesKey("passcode_salt")
        val SAMPLE_SEEDED = booleanPreferencesKey("sample_data_seeded")
    }


}