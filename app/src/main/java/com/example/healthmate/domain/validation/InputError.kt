package com.example.healthmate.domain.validation


sealed interface InputError {
    data object Required : InputError
    data object NotANumber : InputError
    data class OutOfRange(val min: Int, val max: Int) :
        InputError
    data object FutureDate : InputError
}