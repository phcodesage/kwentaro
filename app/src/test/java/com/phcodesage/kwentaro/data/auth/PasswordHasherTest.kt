package com.phcodesage.kwentaro.data.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {
    @Test
    fun verifiesCorrectPasswordAndRejectsWrongOne() {
        val h = PasswordHasher.hash("salamat123", iterations = 1_000)
        assertTrue(PasswordHasher.verify("salamat123", h.hash, h.salt, h.iterations))
        assertFalse(PasswordHasher.verify("Salamat123", h.hash, h.salt, h.iterations))
        assertFalse(PasswordHasher.verify("", h.hash, h.salt, h.iterations))
    }

    @Test
    fun samePasswordGetsDifferentSaltAndHash() {
        val a = PasswordHasher.hash("same-password", iterations = 1_000)
        val b = PasswordHasher.hash("same-password", iterations = 1_000)
        assertFalse(a.salt.contentEquals(b.salt))
        assertFalse(a.hash.contentEquals(b.hash))
    }

    @Test
    fun recoveryCodeIsReadableAndNormalizes() {
        val code = PasswordHasher.newRecoveryCode()
        assertTrue(Regex("^[A-HJ-NP-Z2-9]{4}-[A-HJ-NP-Z2-9]{4}-[A-HJ-NP-Z2-9]{4}$").matches(code))
        val h = PasswordHasher.hash(PasswordHasher.normalizeRecoveryCode(code), iterations = 1_000)
        val typedSloppily = " " + code.lowercase().replace("-", " ") + " "
        assertTrue(PasswordHasher.verify(PasswordHasher.normalizeRecoveryCode(typedSloppily), h.hash, h.salt, h.iterations))
    }

    @Test
    fun signupValidation() {
        assertNull(AccountRules.validateSignup("Aling Nena", "Nena_01", "secret1", "secret1"))
        assertEquals("nena_01", AccountRules.normalizeUsername("  Nena_01 "))
        assertNotNull(AccountRules.validateSignup("", "nena", "secret1", "secret1"))
        assertNotNull(AccountRules.validateSignup("Nena", "ne", "secret1", "secret1"))
        assertNotNull(AccountRules.validateSignup("Nena", "nena store", "secret1", "secret1"))
        assertNotNull(AccountRules.validateSignup("Nena", "nena", "12345", "12345"))
        assertNotNull(AccountRules.validateSignup("Nena", "nena", "secret1", "secret2"))
    }

    @Test
    fun storeFilesAreSeparatePerAccount() {
        assertFalse(StoreFiles.databaseName("a") == StoreFiles.databaseName("b"))
        assertFalse(StoreFiles.settingsFileName("a") == StoreFiles.settingsFileName("b"))
    }
}
