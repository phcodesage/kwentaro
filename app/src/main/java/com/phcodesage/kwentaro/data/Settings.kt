package com.phcodesage.kwentaro.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class StoreSettings(
    val storeName: String = "My Sari-Sari Store",
    val currencySymbol: String = "₱",
    val taxRatePercent: Float = 12f,
    val taxInclusive: Boolean = true,
    val receiptFooter: String = "Salamat po! Come again.",
    val dynamicColor: Boolean = false,
    val onboardingDone: Boolean = false,
)

/** Settings for one account; the DataStore file is per account (see SessionManager). */
class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    private object Keys {
        val storeName = stringPreferencesKey("store_name")
        val currency = stringPreferencesKey("currency")
        val taxRate = floatPreferencesKey("tax_rate")
        val taxInclusive = booleanPreferencesKey("tax_inclusive")
        val footer = stringPreferencesKey("receipt_footer")
        val dynamicColor = booleanPreferencesKey("dynamic_color")
        val onboardingDone = booleanPreferencesKey("onboarding_done")
    }

    val settings: Flow<StoreSettings> = dataStore.data.map { p ->
        val d = StoreSettings()
        StoreSettings(
            storeName = p[Keys.storeName] ?: d.storeName,
            currencySymbol = p[Keys.currency] ?: d.currencySymbol,
            taxRatePercent = p[Keys.taxRate] ?: d.taxRatePercent,
            taxInclusive = p[Keys.taxInclusive] ?: d.taxInclusive,
            receiptFooter = p[Keys.footer] ?: d.receiptFooter,
            dynamicColor = p[Keys.dynamicColor] ?: d.dynamicColor,
            onboardingDone = p[Keys.onboardingDone] ?: d.onboardingDone,
        )
    }

    suspend fun update(s: StoreSettings) {
        dataStore.edit { p ->
            p[Keys.storeName] = s.storeName
            p[Keys.currency] = s.currencySymbol
            p[Keys.taxRate] = s.taxRatePercent
            p[Keys.taxInclusive] = s.taxInclusive
            p[Keys.footer] = s.receiptFooter
            p[Keys.dynamicColor] = s.dynamicColor
            p[Keys.onboardingDone] = s.onboardingDone
        }
    }

    suspend fun setOnboardingDone(done: Boolean) {
        dataStore.edit { it[Keys.onboardingDone] = done }
    }

    /** Commit setup and completion together, preserving tax, appearance and receipt preferences. */
    suspend fun completeOnboarding(storeName: String, currencySymbol: String) {
        dataStore.edit {
            it[Keys.storeName] = storeName.trim()
            it[Keys.currency] = currencySymbol.trim()
            it[Keys.onboardingDone] = true
        }
    }
}
