package com.example.healthmate.domain.repository

import com.example.healthmate.domain.model.AuthResult
import com.example.healthmate.domain.model.User
import kotlinx.coroutines.flow.Flow

// local account management and session tracking
interface AuthRepository {
    // signed-in user, null when logout
    val currentUser: Flow<User?>

    // create account and signing, return error on duplicates.
    suspend fun register(name: String, email: String, password: String): AuthResult

    // return success or give invalid credentials
    suspend fun login(email: String, password: String): AuthResult

    suspend fun logout()
}