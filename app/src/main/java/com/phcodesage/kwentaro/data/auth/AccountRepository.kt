package com.phcodesage.kwentaro.data.auth

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

sealed interface AuthResult {
    data class Success(val account: Account, val recoveryCode: String? = null) : AuthResult
    data class Error(val message: String) : AuthResult
}

/**
 * Offline sign-up and sign-in. Nothing leaves the device: credentials are salted PBKDF2 hashes in
 * accounts.db, and each account's store data lives in its own files (see [StoreFiles]).
 */
class AccountRepository(private val context: Context) {
    private val dao = AccountsDatabase.build(context).accounts()
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private var failedAttempts = 0
    private var lockedUntil = 0L

    val accounts = dao.observeAll()

    /** Account to reopen on launch when the user chose to stay signed in. */
    var rememberedAccountId: String?
        get() = prefs.getString("remembered", null)
        set(value) = prefs.edit { if (value == null) remove("remembered") else putString("remembered", value) }

    suspend fun get(id: String) = dao.get(id)

    /** True when this phone has pre-accounts store data that the first sign-up will inherit. */
    suspend fun hasLegacyData(): Boolean = dao.count() == 0 && StoreFiles.hasLegacy(context)

    suspend fun signUp(name: String, username: String, password: String, confirm: String): AuthResult =
        withContext(Dispatchers.Default) {
            AccountRules.validateSignup(name, username, password, confirm)?.let { return@withContext AuthResult.Error(it) }
            val handle = AccountRules.normalizeUsername(username)
            if (dao.byUsername(handle) != null) return@withContext AuthResult.Error("@$handle is already taken on this phone")
            val isFirst = dao.count() == 0
            val code = PasswordHasher.newRecoveryCode()
            val pw = PasswordHasher.hash(password)
            val rc = PasswordHasher.hash(PasswordHasher.normalizeRecoveryCode(code))
            val account = Account(
                id = UUID.randomUUID().toString(), username = handle, displayName = name.trim(),
                passwordHash = pw.hash, passwordSalt = pw.salt, passwordIterations = pw.iterations,
                recoveryHash = rc.hash, recoverySalt = rc.salt, recoveryIterations = rc.iterations,
                lastLoginAt = System.currentTimeMillis(),
            )
            // Hand data created before accounts existed to the first account, before anything opens it.
            if (isFirst) StoreFiles.adoptLegacy(context, account.id)
            dao.insert(account)
            AuthResult.Success(account, code)
        }

    suspend fun signIn(accountId: String, password: String): AuthResult = withContext(Dispatchers.Default) {
        val now = System.currentTimeMillis()
        if (now < lockedUntil) return@withContext AuthResult.Error("Too many tries. Wait ${(lockedUntil - now) / 1000 + 1}s")
        val account = dao.get(accountId) ?: return@withContext AuthResult.Error("Account not found")
        if (!PasswordHasher.verify(password, account.passwordHash, account.passwordSalt, account.passwordIterations)) {
            failedAttempts++
            if (failedAttempts >= 5) { lockedUntil = now + 30_000; failedAttempts = 0 }
            return@withContext AuthResult.Error("Wrong password")
        }
        failedAttempts = 0
        val updated = account.copy(lastLoginAt = now)
        dao.update(updated)
        AuthResult.Success(updated)
    }

    suspend fun changePassword(accountId: String, current: String, new: String, confirm: String): String? =
        withContext(Dispatchers.Default) {
            val account = dao.get(accountId) ?: return@withContext "Account not found"
            if (!PasswordHasher.verify(current, account.passwordHash, account.passwordSalt, account.passwordIterations)) {
                return@withContext "Current password is wrong"
            }
            setPassword(account, new, confirm)
        }

    /** Offline "forgot password": the recovery code from sign-up proves ownership. */
    suspend fun resetWithRecoveryCode(username: String, code: String, new: String, confirm: String): AuthResult =
        withContext(Dispatchers.Default) {
            val account = dao.byUsername(AccountRules.normalizeUsername(username))
                ?: return@withContext AuthResult.Error("No @${username.trim()} on this phone")
            val ok = PasswordHasher.verify(
                PasswordHasher.normalizeRecoveryCode(code), account.recoveryHash, account.recoverySalt, account.recoveryIterations,
            )
            if (!ok) return@withContext AuthResult.Error("Recovery code doesn't match")
            setPassword(account, new, confirm)?.let { return@withContext AuthResult.Error(it) }
            AuthResult.Success(dao.get(account.id)!!)
        }

    private suspend fun setPassword(account: Account, new: String, confirm: String): String? {
        if (new.length < AccountRules.MIN_PASSWORD) return "Password needs at least ${AccountRules.MIN_PASSWORD} characters"
        if (new != confirm) return "Passwords don't match"
        val pw = PasswordHasher.hash(new)
        dao.update(account.copy(passwordHash = pw.hash, passwordSalt = pw.salt, passwordIterations = pw.iterations))
        return null
    }

    /** Deletes the account and all of its store data. The caller must have closed its session. */
    suspend fun delete(accountId: String, password: String): String? = withContext(Dispatchers.IO) {
        val account = dao.get(accountId) ?: return@withContext "Account not found"
        if (!PasswordHasher.verify(password, account.passwordHash, account.passwordSalt, account.passwordIterations)) {
            return@withContext "Wrong password"
        }
        dao.delete(accountId)
        if (rememberedAccountId == accountId) rememberedAccountId = null
        StoreFiles.delete(context, accountId)
        null
    }
}

/** Where each account's data lives. Keeping files separate is what guarantees records never mix. */
object StoreFiles {
    private const val LEGACY_DB = "kwentaro.db"
    private const val LEGACY_SETTINGS = "settings.preferences_pb"

    fun databaseName(accountId: String) = "kwentaro-$accountId.db"
    fun settingsFileName(accountId: String) = "settings-$accountId.preferences_pb"

    private fun dbFiles(context: Context, name: String) =
        listOf("", "-wal", "-shm", "-journal").map { File(context.getDatabasePath(name).path + it) }

    private fun settingsFile(context: Context, name: String) = File(context.filesDir, "datastore/$name")

    fun hasLegacy(context: Context) = context.getDatabasePath(LEGACY_DB).exists() || settingsFile(context, LEGACY_SETTINGS).exists()

    fun adoptLegacy(context: Context, accountId: String) {
        dbFiles(context, LEGACY_DB).zip(dbFiles(context, databaseName(accountId))).forEach { (from, to) ->
            if (from.exists()) from.renameTo(to)
        }
        val settings = settingsFile(context, LEGACY_SETTINGS)
        if (settings.exists()) settings.renameTo(settingsFile(context, settingsFileName(accountId)))
    }

    fun delete(context: Context, accountId: String) {
        dbFiles(context, databaseName(accountId)).forEach { it.delete() }
        settingsFile(context, settingsFileName(accountId)).delete()
    }
}
