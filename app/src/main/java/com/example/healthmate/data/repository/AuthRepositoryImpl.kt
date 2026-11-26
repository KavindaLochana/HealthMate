package com.example.healthmate.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.healthmate.core.security.HashedSecret
import com.example.healthmate.core.security.PasswordHasher
import com.example.healthmate.data.local.db.UserDao
import com.example.healthmate.data.local.db.UserEntity
import com.example.healthmate.data.local.prefs.AppPreferences
import com.example.healthmate.data.mapper.toDomain
import com.example.healthmate.domain.model.AuthResult
import com.example.healthmate.domain.model.User
import com.example.healthmate.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val preferences: AppPreferences,
    private val hasher: PasswordHasher,
    private val hashingDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : AuthRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUser: Flow<User?> = preferences.sessionUserId.flatMapLatest { id ->
        if (id == null) flowOf(null) else userDao.observeById(id).map { it?.toDomain() }
    }

    override suspend fun register(name: String, email: String, password: String): AuthResult {
        val normalizedEmail = normalize(email)
        if (userDao.findByEmail(normalizedEmail) != null) return AuthResult.EmailAlreadyRegistered

        val hashed = withContext(hashingDispatcher) { hasher.hash(password) }
        val userId = try {
            userDao.insert(
                UserEntity(
                    name = name.trim(),
                    email = normalizedEmail,
                    passwordHash = hashed.hash,
                    passwordSalt = hashed.salt,
                ),
            )
        } catch (_: SQLiteConstraintException) {
            // Another insert won the race on the unique email index.
            return AuthResult.EmailAlreadyRegistered
        }
        preferences.setSessionUserId(userId)
        return AuthResult.Success
    }

    override suspend fun login(email: String, password: String): AuthResult {
        val user = userDao.findByEmail(normalize(email)) ?: return AuthResult.InvalidCredentials
        val matches = withContext(hashingDispatcher) {
            hasher.verify(password, HashedSecret(user.passwordHash, user.passwordSalt))
        }
        if (!matches) return AuthResult.InvalidCredentials

        preferences.setSessionUserId(user.id)
        return AuthResult.Success
    }

    override suspend fun logout() = preferences.setSessionUserId(null)

    private fun normalize(email: String) = email.trim().lowercase()
}
