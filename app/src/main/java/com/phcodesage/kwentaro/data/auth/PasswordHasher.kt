package com.phcodesage.kwentaro.data.auth

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Salted PBKDF2-HMAC-SHA256. Iterations are stored with each hash so they can be raised later
 * without invalidating existing accounts.
 */
object PasswordHasher {
    const val DEFAULT_ITERATIONS = 60_000
    private const val KEY_BITS = 256
    private val random = SecureRandom()

    data class Hash(val hash: ByteArray, val salt: ByteArray, val iterations: Int)

    fun hash(secret: String, iterations: Int = DEFAULT_ITERATIONS): Hash {
        val salt = ByteArray(16).also(random::nextBytes)
        return Hash(derive(secret, salt, iterations), salt, iterations)
    }

    fun verify(secret: String, expected: ByteArray, salt: ByteArray, iterations: Int): Boolean =
        MessageDigest.isEqual(derive(secret, salt, iterations), expected)

    private fun derive(secret: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(secret.toCharArray(), salt, iterations, KEY_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    /** A human-friendly one-time code (no 0/O/1/I) shown once at signup for offline password resets. */
    fun newRecoveryCode(): String {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..12).map { alphabet[random.nextInt(alphabet.length)] }
            .chunked(4).joinToString("-") { it.joinToString("") }
    }

    fun normalizeRecoveryCode(code: String) = code.uppercase().filter { it.isLetterOrDigit() }
}

object AccountRules {
    val usernamePattern = Regex("^[a-z0-9_.]{3,20}$")
    const val MIN_PASSWORD = 6

    fun normalizeUsername(raw: String) = raw.trim().lowercase()

    /** Returns an error message, or null when valid. */
    fun validateSignup(name: String, username: String, password: String, confirm: String): String? = when {
        name.isBlank() -> "Enter your name"
        !usernamePattern.matches(normalizeUsername(username)) -> "Username: 3–20 letters, numbers, _ or ."
        password.length < MIN_PASSWORD -> "Password needs at least $MIN_PASSWORD characters"
        password != confirm -> "Passwords don't match"
        else -> null
    }
}
