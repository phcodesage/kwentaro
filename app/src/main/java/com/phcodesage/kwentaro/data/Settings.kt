package com.phcodesage.kwentaro.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class StoreSettings(
    val storeName: String = "My Sari-Sari Store",
    val currencySymbol: String = "₱",
    val taxRatePercent: Float = 12f,
    val taxInclusive: Boolean = true,
    val receiptFooter: String = "Salamat po! Come again.",
    val dynamicColor: Boolean = false,
)

private val Context.dataStore by preferencesDataStore("settings")

class SettingsRepository(private val context: Context) {
    private object Keys {
        val storeName = stringPreferencesKey("store_name")
        val currency = stringPreferencesKey("currency")
        val taxRate = floatPreferencesKey("tax_rate")
        val taxInclusive = booleanPreferencesKey("tax_inclusive")
        val footer = stringPreferencesKey("receipt_footer")
        val dynamicColor = booleanPreferencesKey("dynamic_color")
    }

    val settings: Flow<StoreSettings> = context.dataStore.data.map { p ->
        val d = StoreSettings()
        StoreSettings(
            storeName = p[Keys.storeName] ?: d.storeName,
            currencySymbol = p[Keys.currency] ?: d.currencySymbol,
            taxRatePercent = p[Keys.taxRate] ?: d.taxRatePercent,
            taxInclusive = p[Keys.taxInclusive] ?: d.taxInclusive,
            receiptFooter = p[Keys.footer] ?: d.receiptFooter,
            dynamicColor = p[Keys.dynamicColor] ?: d.dynamicColor,
        )
    }

    suspend fun update(s: StoreSettings) {
        context.dataStore.edit { p ->
            p[Keys.storeName] = s.storeName
            p[Keys.currency] = s.currencySymbol
            p[Keys.taxRate] = s.taxRatePercent
            p[Keys.taxInclusive] = s.taxInclusive
            p[Keys.footer] = s.receiptFooter
            p[Keys.dynamicColor] = s.dynamicColor
        }
    }
}
