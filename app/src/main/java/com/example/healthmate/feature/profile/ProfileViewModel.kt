package com.example.healthmate.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.domain.model.DailyTargets
import com.example.healthmate.domain.model.MetricType
import com.example.healthmate.domain.model.User
import com.example.healthmate.domain.repository.AuthRepository
import com.example.healthmate.domain.repository.SettingsRepository
import com.example.healthmate.domain.usecase.ResetSampleDataUseCase
import com.example.healthmate.domain.validation.InputError
import com.example.healthmate.domain.validation.PasscodeRules
import com.example.healthmate.domain.validation.TargetValidationResult
import com.example.healthmate.domain.validation.TargetValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


enum class PasscodeMode { ENABLE, CHANGE, DISABLE }

enum class PasscodeError { INVALID_FORMAT, MISMATCH, WRONG_CURRENT }

enum class ProfileMessage { TARGETS_SAVED, SAMPLE_DATA_RESET, APP_LOCK_ENABLED, APP_LOCK_DISABLED, PASSCODE_CHANGED }

sealed interface ProfileDialog {
    data class EditTarget(
        val metric: MetricType,
        val input: String,
        val error: InputError? = null,
    ) : ProfileDialog

    data class Passcode(
        val mode: PasscodeMode,
        val current: String = "",
        val new: String = "",
        val confirm: String = "",
        val error: PasscodeError? = null,
    ) : ProfileDialog

    data object ConfirmReset : ProfileDialog
    data object ConfirmLogout : ProfileDialog
}

data class ProfileUiState(
    val user: User? = null,
    val targets: DailyTargets = DailyTargets(),
    val isAppLockEnabled: Boolean = false,
    val dialog: ProfileDialog? = null,
    val message: ProfileMessage? = null,
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val settings: SettingsRepository,
    private val resetSampleData: ResetSampleDataUseCase,
    private val targetValidator: TargetValidator,
) : ViewModel() {

    private val dialog = MutableStateFlow<ProfileDialog?>(null)
    private val message = MutableStateFlow<ProfileMessage?>(null)

    val uiState: StateFlow<ProfileUiState> = combine(
        authRepository.currentUser,
        settings.targets,
        settings.isAppLockEnabled,
        dialog,
        message,
    ) { user, targets, lockEnabled, currentDialog, currentMessage ->
        ProfileUiState(
            user = user,
            targets = targets,
            isAppLockEnabled = lockEnabled,
            dialog = currentDialog,
            message = currentMessage,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), ProfileUiState())

    // daily targets
    fun onTargetClick(metric: MetricType) {
        viewModelScope.launch {
            val value = settings.targets.first().targetOf(metric)
            dialog.value = ProfileDialog.EditTarget(metric, value.toString())
        }
    }

    fun onTargetInputChange(value: String) = updateDialog<ProfileDialog.EditTarget> {
        it.copy(input = value.filter(Char::isDigit).take(MAX_TARGET_DIGITS), error = null)
    }

    fun onTargetConfirm() {
        val edit = dialog.value as? ProfileDialog.EditTarget ?: return
        when (val result = targetValidator.validate(edit.metric, edit.input)) {
            is TargetValidationResult.Invalid -> dialog.value = edit.copy(error = result.error)
            is TargetValidationResult.Valid -> viewModelScope.launch {
                val updated = settings.targets.first().withTarget(edit.metric, result.value)
                settings.updateTargets(updated)
                dialog.value = null
                message.value = ProfileMessage.TARGETS_SAVED
            }
        }
    }

    // app lock
    fun onAppLockToggle(enable: Boolean) {
        dialog.value =
            ProfileDialog.Passcode(if (enable) PasscodeMode.ENABLE else PasscodeMode.DISABLE)
    }

    fun onChangePasscodeClick() {
        dialog.value = ProfileDialog.Passcode(PasscodeMode.CHANGE)
    }

    fun onCurrentPasscodeChange(value: String) = updateDialog<ProfileDialog.Passcode> {
        it.copy(current = value.passcodeDigits(), error = null)
    }

    fun onNewPasscodeChange(value: String) = updateDialog<ProfileDialog.Passcode> {
        it.copy(new = value.passcodeDigits(), error = null)
    }

    fun onConfirmPasscodeChange(value: String) = updateDialog<ProfileDialog.Passcode> {
        it.copy(confirm = value.passcodeDigits(), error = null)
    }

    fun onPasscodeConfirm() {
        val form = dialog.value as? ProfileDialog.Passcode ?: return
        viewModelScope.launch {
            when (form.mode) {
                PasscodeMode.ENABLE -> {
                    val error = newPasscodeError(form)
                    if (error != null) {
                        dialog.value = form.copy(error = error)
                        return@launch
                    }
                    settings.enableAppLock(form.new)
                    finishDialog(ProfileMessage.APP_LOCK_ENABLED)
                }

                PasscodeMode.CHANGE -> {
                    val error = newPasscodeError(form)
                    if (error != null) {
                        dialog.value = form.copy(error = error)
                        return@launch
                    }
                    if (settings.changePasscode(form.current, form.new)) {
                        finishDialog(ProfileMessage.PASSCODE_CHANGED)
                    } else {
                        dialog.value = form.copy(error = PasscodeError.WRONG_CURRENT)
                    }
                }

                PasscodeMode.DISABLE -> {
                    if (settings.disableAppLock(form.current)) {
                        finishDialog(ProfileMessage.APP_LOCK_DISABLED)
                    } else {
                        dialog.value = form.copy(error = PasscodeError.WRONG_CURRENT)
                    }
                }
            }
        }
    }

    fun onResetSampleDataClick() {
        dialog.value = ProfileDialog.ConfirmReset
    }

    fun onResetSampleDataConfirm() {
        dialog.value = null
        viewModelScope.launch {
            resetSampleData()
            message.value = ProfileMessage.SAMPLE_DATA_RESET
        }
    }

    fun onLogoutClick() {
        dialog.value = ProfileDialog.ConfirmLogout
    }

    fun onLogoutConfirm() {
        dialog.value = null
        viewModelScope.launch { authRepository.logout() }
    }

    // common functions
    fun onDialogDismiss() {
        dialog.value = null
    }

    fun onMessageShown() {
        message.value = null
    }

    private fun newPasscodeError(form: ProfileDialog.Passcode): PasscodeError? = when {
        !PasscodeRules.isValid(form.new) -> PasscodeError.INVALID_FORMAT
        form.new != form.confirm -> PasscodeError.MISMATCH
        else -> null
    }

    private fun finishDialog(result: ProfileMessage) {
        dialog.value = null
        message.value = result
    }

    private inline fun <reified T : ProfileDialog> updateDialog(crossinline transform: (T) -> T) {
        dialog.update { current -> if (current is T) transform(current) else current }
    }

    private fun String.passcodeDigits(): String = filter(Char::isDigit).take(PasscodeRules.LENGTH)

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
        const val MAX_TARGET_DIGITS = 6
    }
}
