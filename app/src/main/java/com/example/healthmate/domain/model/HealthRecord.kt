package com.example.healthmate.domain.model

import java.time.LocalDate

// A single health entry
class HealthRecord (
    val id: Long = NEW_ID,
    val date: LocalDate,
    val steps: Int,
    val calories: Int,
    val water: Int,
) {

    val isNew: Boolean get() = id == NEW_ID

    companion object {
        // temp id for not saved one, room will generate the real id
        const val NEW_ID = 0L
    }
}