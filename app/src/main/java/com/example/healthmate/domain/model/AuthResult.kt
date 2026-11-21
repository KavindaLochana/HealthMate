package com.example.healthmate.domain.model

import com.example.healthmate.domain.validation.AuthError
import com.example.healthmate.domain.validation.AuthField

// login or registration attempt outcome
sealed interface AuthResult {
    data object Success: AuthResult
    data class InvalidInput(val errors: Map<AuthField, AuthError>): AuthResult
    data object InvalidCredentials : AuthResult
    data object EmailAlreadyRegistered : AuthResult
}