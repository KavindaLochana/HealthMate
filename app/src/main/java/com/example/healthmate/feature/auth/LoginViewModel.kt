package com.example.healthmate.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.domain.model.AuthResult
import com.example.healthmate.domain.usecase.LoginUseCase
import com.example.healthmate.domain.validation.AuthField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val login: LoginUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

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

    fun onSignInClick() {
        val current = _uiState.value
        if (current.isLoading) return
        _uiState.update { it.copy(isLoading = true, formError = null) }

        viewModelScope.launch {
            when (val result = login(current.email, current.password)) {
                AuthResult.Success -> _uiState.value = LoginUiState()
                is AuthResult.InvalidInput ->
                    _uiState.update { it.copy(isLoading = false, fieldErrors = result.errors) }

                AuthResult.InvalidCredentials ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            formError = AuthFormError.INVALID_CREDENTIALS
                        )
                    }

                AuthResult.EmailAlreadyRegistered ->
                    _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}