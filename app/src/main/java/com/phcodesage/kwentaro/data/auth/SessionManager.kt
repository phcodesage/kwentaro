package com.phcodesage.kwentaro.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.phcodesage.kwentaro.data.AppDatabase
import com.phcodesage.kwentaro.data.PosRepository
import com.phcodesage.kwentaro.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Everything that belongs to the signed-in account. Screens only ever see this account's data. */
class AccountSession(
    val account: Account,
    private val database: AppDatabase,
    val repository: PosRepository,
    val settings: SettingsRepository,
) {
    internal fun close() = database.close()
}

class SessionManager(private val context: Context, private val scope: CoroutineScope) {
    private val _session = MutableStateFlow<AccountSession?>(null)
    val session = _session.asStateFlow()

    // DataStore allows only one instance per file per process, so keep them for the app's lifetime.
    private val dataStores = mutableMapOf<String, DataStore<Preferences>>()

    @Synchronized
    private fun dataStore(accountId: String) = dataStores.getOrPut(accountId) {
        PreferenceDataStoreFactory.create(scope = scope) {
            context.preferencesDataStoreFile(StoreFiles.settingsFileName(accountId).removeSuffix(".preferences_pb"))
        }
    }

    fun open(account: Account): AccountSession {
        _session.value?.let { if (it.account.id == account.id) return it }
        close()
        val db = AppDatabase.build(context, StoreFiles.databaseName(account.id))
        return AccountSession(account, db, PosRepository(db), SettingsRepository(dataStore(account.id)))
            .also { _session.value = it }
    }

    fun close() {
        _session.value?.close()
        _session.value = null
    }
}
