package com.phcodesage.kwentaro.data.auth

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** A local, offline account. Each one owns its own store database and settings file. */
@Entity(tableName = "accounts", indices = [Index(value = ["username"], unique = true)])
data class Account(
    @PrimaryKey val id: String,
    val username: String,
    val displayName: String,
    val passwordHash: ByteArray,
    val passwordSalt: ByteArray,
    val passwordIterations: Int,
    val recoveryHash: ByteArray,
    val recoverySalt: ByteArray,
    val recoveryIterations: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null,
) {
    // ByteArray fields make the generated equals() identity-based; compare by id instead.
    override fun equals(other: Any?) = other is Account && other.id == id && other.lastLoginAt == lastLoginAt &&
        other.displayName == displayName && other.passwordHash.contentEquals(passwordHash)
    override fun hashCode() = id.hashCode()
}

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY lastLoginAt IS NULL, lastLoginAt DESC, displayName COLLATE NOCASE")
    fun observeAll(): Flow<List<Account>>

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun count(): Int

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun get(id: String): Account?

    @Query("SELECT * FROM accounts WHERE username = :username")
    suspend fun byUsername(username: String): Account?

    @Insert
    suspend fun insert(account: Account)

    @Update
    suspend fun update(account: Account)

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun delete(id: String)
}

@Database(entities = [Account::class], version = 1)
abstract class AccountsDatabase : RoomDatabase() {
    abstract fun accounts(): AccountDao

    companion object {
        fun build(context: Context): AccountsDatabase =
            Room.databaseBuilder(context, AccountsDatabase::class.java, "accounts.db").build()
    }
}
