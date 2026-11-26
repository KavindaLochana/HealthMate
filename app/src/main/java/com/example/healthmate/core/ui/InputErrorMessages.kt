package com.example.healthmate.core.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.healthmate.domain.model.MetricType
import com.example.healthmate.domain.validation.InputError
import com.example.healthmate.R
import com.example.healthmate.core.util.formatNumber
import com.example.healthmate.domain.validation.AuthError

// readable errors

@Composable
fun InputError.message(metric: MetricType?): String = when (this) {
    InputError.Required -> stringResource(R.string.error_required)
    InputError.NotANumber -> stringResource(R.string.error_not_number)
    InputError.FutureDate -> stringResource(R.string.error_future_date)
    is InputError.OutOfRange ->
        if (metric == null) {
            stringResource(R.string.error_out_of_range_generic, formatNumber(min), formatNumber(max))
        } else {
            stringResource(
                R.string.error_out_of_range,
                stringResource(metric.fieldNameRes),
                formatNumber(min),
                formatNumber(max),
                stringResource(metric.unitRes),
            )
        }
}

@get:StringRes
val AuthError.messageRes: Int
    get() = when (this) {
        AuthError.REQUIRED -> R.string.error_required
        AuthError.NAME_TOO_SHORT -> R.string.auth_error_name_short
        AuthError.INVALID_EMAIL -> R.string.auth_error_invalid_email
        AuthError.PASSWORD_TOO_SHORT -> R.string.auth_error_password_short
        AuthError.PASSWORDS_DO_NOT_MATCH -> R.string.auth_error_passwords_mismatch
    }
