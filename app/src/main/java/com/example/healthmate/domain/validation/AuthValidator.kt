package com.example.healthmate.domain.validation

enum class AuthField { NAME, EMAIL, PASSWORD, CONFIRM_PASSWORD }

enum class AuthError {
    REQUIRED,
    NAME_TOO_SHORT,
    INVALID_EMAIL,
    PASSWORD_TOO_SHORT,
    PASSWORDS_DO_NOT_MATCH,
}


class AuthValidator {
}