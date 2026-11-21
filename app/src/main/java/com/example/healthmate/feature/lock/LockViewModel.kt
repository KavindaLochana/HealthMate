package com.example.healthmate.feature.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.core.session.AppLockState
import com.example.healthmate.domain.repository.SettingsRepository
import com.example.healthmate.domain.validation.PasscodeRules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class LockUiState(
    val enteredDigits: Int = 0,
    val hasError: Boolean = false,
    val isVerifying: Boolean = false,
)

// passcode keypad
class LockViewModel(
    private val settings: SettingsRepository,
    private val appLockState: AppLockState,
) : ViewModel() {

    private var passcode = ""

    private val _uiState = MutableStateFlow(LockUiState())
    val uiState: StateFlow<LockUiState> = _uiState.asStateFlow()

    fun onDigit(digit: Char) {
        if (_uiState.value.isVerifying || !digit.isDigit() || passcode.length >= PasscodeRules.LENGTH) return
        passcode += digit
        _uiState.update { it.copy(enteredDigits = passcode.length, hasError = false) }
        if (passcode.length == PasscodeRules.LENGTH) verify(passcode)
    }

    fun onBackspace() {
        if (_uiState.value.isVerifying || passcode.isEmpty()) return
        passcode = passcode.dropLast(1)
        _uiState.update { it.copy(enteredDigits = passcode.length, hasError = false) }
    }

    private fun verify(code: String) {
        _uiState.update { it.copy(isVerifying = true) }
        viewModelScope.launch {
            val correct = settings.verifyPasscode(code)
            passcode = ""
            if (correct) {
                appLockState.unlock()
                _uiState.value = LockUiState()
            } else {
                _uiState.value = LockUiState(hasError = true)
            }
        }
    }
}
