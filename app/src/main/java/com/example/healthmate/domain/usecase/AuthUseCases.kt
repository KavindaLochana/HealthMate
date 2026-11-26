package com.example.healthmate.domain.usecase

import com.example.healthmate.domain.model.AuthResult
import com.example.healthmate.domain.repository.AuthRepository
import com.example.healthmate.domain.validation.AuthValidator

class LoginUseCase(
    private val repository: AuthRepository,
    private val validator: AuthValidator,
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        val errors = validator.validateLogin(email, password)
        if (errors.isNotEmpty()) return AuthResult.InvalidInput(errors)
        return repository.login(email, password)
    }
}

class RegisterUserUseCase(
    private val repository: AuthRepository,
    private val validator: AuthValidator,
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): AuthResult {
        val errors = validator.validateRegistration(name, email, password, confirmPassword)
        if (errors.isNotEmpty()) return AuthResult.InvalidInput(errors)
        return repository.register(name, email, password)
    }
}