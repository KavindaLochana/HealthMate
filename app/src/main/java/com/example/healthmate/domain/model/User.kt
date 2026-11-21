package com.example.healthmate.domain.model

// registered account owner
data class User(
    val id: Long,
    val name: String,
    val email: String
) {
    // first letter of the name shown in the profile picture
    val initial: String
    get() = name.trim().firstOrNull()?.uppercase() ?: "?"
}