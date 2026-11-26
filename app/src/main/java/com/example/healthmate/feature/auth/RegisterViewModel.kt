package com.example.healthmate.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.domain.model.AuthResult
import com.example.healthmate.domain.usecase.RegisterUserUseCase
import com.example.healthmate.domain.validation.AuthField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val register: RegisterUserUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = _uiState.update {
        it.copy(name = value, fieldErrors = it.fieldErrors - AuthField.NAME, formError = null)
    }

    fun onEmailChange(value: String) = _uiState.update {
        it.copy(email = value, fieldErrors = it.fieldErrors - AuthField.EMAIL, formError = null)
    }

    fun onPasswordChange(value: String) = _uiState.update {
        it.copy(
            password = value,
            fieldErrors = it.fieldErrors - AuthField.PASSWORD,
            formError = null
        )
    }

    fun onConfirmPasswordChange(value: String) = _uiState.update {
        it.copy(
            confirmPassword = value,
            fieldErrors = it.fieldErrors - AuthField.CONFIRM_PASSWORD,
            formError = null
        )
    }

    fun onCreateAccountClick() {
        val current = _uiState.value
        if (current.isLoading) return
        _uiState.update { it.copy(isLoading = true, formError = null) }

        viewModelScope.launch {
            val result =
                register(current.name, current.email, current.password, current.confirmPassword)
            when (result) {
                AuthResult.Success -> _uiState.value = RegisterUiState()
                is AuthResult.InvalidInput ->
                    _uiState.update { it.copy(isLoading = false, fieldErrors = result.errors) }

                AuthResult.EmailAlreadyRegistered ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            formError = AuthFormError.EMAIL_TAKEN
                        )
                    }

                AuthResult.InvalidCredentials ->
                    _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}