package com.example.healthmate.core.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// both are Base64
data class HashedSecret(val hash: String, val salt: String)


interface PasswordHasher {
    fun hash(secret: String): HashedSecret
    fun verify(secret: String, hashed: HashedSecret): Boolean
}

//PBKDF2-HMAC-SHA256 with a random 16-byte salt per secret.

class Pbkdf2PasswordHasher(
    private val iterations: Int = DEFAULT_ITERATIONS,
    private val random: SecureRandom = SecureRandom(),
) : PasswordHasher {

    override fun hash(secret: String): HashedSecret {
        val salt = ByteArray(SALT_BYTES).also(random::nextBytes)
        return HashedSecret(
            hash = encode(derive(secret, salt)),
            salt = encode(salt),
        )
    }

    override fun verify(secret: String, hashed: HashedSecret): Boolean {
        val salt = decode(hashed.salt) ?: return false
        val expected = decode(hashed.hash) ?: return false
        // Constant-time comparison avoids leaking how many leading bytes matched.
        return MessageDigest.isEqual(derive(secret, salt), expected)
    }

    private fun derive(secret: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(secret.toCharArray(), salt, iterations, KEY_BITS)
        return try {
            SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun encode(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)

    private fun decode(text: String): ByteArray? =
        try {
            Base64.getDecoder().decode(text)
        } catch (_: IllegalArgumentException) {
            null
        }

    private companion object {
        const val ALGORITHM = "PBKDF2WithHmacSHA256"
        const val DEFAULT_ITERATIONS = 120_000
        const val KEY_BITS = 256
        const val SALT_BYTES = 16
    }
}