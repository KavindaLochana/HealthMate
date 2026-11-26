package com.example.healthmate.domain.validation

// the lock passcode rule

object PasscodeRules {
    const val LENGTH = 4

    fun isValid(passcode: String): Boolean =
        passcode.length == LENGTH && passcode.all(Char::isDigit)
}