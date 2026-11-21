package com.example.healthmate.domain.validation

enum class AuthField { NAME, EMAIL, PASSWORD, CONFIRM_PASSWORD }

enum class AuthError {
    REQUIRED,
    NAME_TOO_SHORT,
    INVALID_EMAIL,
    PASSWORD_TOO_SHORT,
    PASSWORDS_DO_NOT_MATCH,
}

// validation for sign in and create account forms
class AuthValidator {

    fun validateLogin(email: String, password: String): Map<AuthField, AuthError> {
        val errors = linkedMapOf<AuthField, AuthError>()
        if (email.isBlank()) errors[AuthField.EMAIL] = AuthError.REQUIRED
        if (password.isEmpty()) errors[AuthField.PASSWORD] = AuthError.REQUIRED
        return errors
    }

    fun validateRegistration(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): Map<AuthField, AuthError> {
        val errors = linkedMapOf<AuthField, AuthError>()

        when {
            name.isBlank() -> errors[AuthField.NAME] = AuthError.REQUIRED
            name.trim().length < MIN_NAME_LENGTH -> errors[AuthField.NAME] =
                AuthError.NAME_TOO_SHORT
        }
        when {
            email.isBlank() -> errors[AuthField.EMAIL] = AuthError.REQUIRED
            !EMAIL_PATTERN.matches(email.trim()) -> errors[AuthField.EMAIL] =
                AuthError.INVALID_EMAIL
        }
        when {
            password.isEmpty() -> errors[AuthField.PASSWORD] = AuthError.REQUIRED
            password.length < MIN_PASSWORD_LENGTH -> errors[AuthField.PASSWORD] =
                AuthError.PASSWORD_TOO_SHORT
        }
        when {
            confirmPassword.isEmpty() -> errors[AuthField.CONFIRM_PASSWORD] = AuthError.REQUIRED
            confirmPassword != password -> errors[AuthField.CONFIRM_PASSWORD] =
                AuthError.PASSWORDS_DO_NOT_MATCH
        }
        return errors
    }

    companion object {
        const val MIN_NAME_LENGTH = 2
        const val MIN_PASSWORD_LENGTH = 8
        private val EMAIL_PATTERN = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
