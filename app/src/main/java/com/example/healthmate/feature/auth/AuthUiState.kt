package com.example.healthmate.feature.auth

import com.example.healthmate.domain.validation.AuthError
import com.example.healthmate.domain.validation.AuthField

// common auth errors
enum class AuthFormError { INVALID_CREDENTIALS, EMAIL_TAKEN }


data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val fieldErrors: Map<AuthField, AuthError> = emptyMap(),
    val formError: AuthFormError? = null,
    val isLoading: Boolean = false,
)

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fieldErrors: Map<AuthField, AuthError> = emptyMap(),
    val formError: AuthFormError? = null,
    val isLoading: Boolean = false,
) {
    // realtime validation
    val passwordsMismatch: Boolean
        get() = confirmPassword.isNotEmpty() && confirmPassword != password
}