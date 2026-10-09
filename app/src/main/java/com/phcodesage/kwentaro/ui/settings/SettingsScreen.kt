package com.phcodesage.kwentaro.ui.settings

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.phcodesage.kwentaro.BuildConfig
import com.phcodesage.kwentaro.data.SettingsRepository
import com.phcodesage.kwentaro.data.StoreSettings
import com.phcodesage.kwentaro.ui.components.appViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(private val repo: SettingsRepository) : ViewModel() {
    suspend fun load() = repo.settings.first()
    fun save(s: StoreSettings) = viewModelScope.launch { repo.update(s) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val vm = appViewModel { SettingsViewModel(it.settings) }
    var s by remember { mutableStateOf<StoreSettings?>(null) }
    var taxText by remember { mutableStateOf("") }
    LaunchedEffect(Unit) { vm.load().let { s = it; taxText = it.taxRatePercent.toString().removeSuffix(".0") } }
    val scroll = TopAppBarDefaults.pinnedScrollBehavior()

    // Every edit persists immediately; there is no save button to forget.
    fun update(next: StoreSettings) { s = next; vm.save(next) }

    Scaffold(
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = { TopAppBar(title = { Text("Settings") }, scrollBehavior = scroll) },
    ) { padding ->
        val cur = s ?: return@Scaffold
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Section("Store") {
                OutlinedTextField(cur.storeName, { update(cur.copy(storeName = it)) }, label = { Text("Store name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(cur.currencySymbol, { update(cur.copy(currencySymbol = it.take(4))) }, label = { Text("Currency symbol") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(cur.receiptFooter, { update(cur.copy(receiptFooter = it)) }, label = { Text("Receipt footer") }, modifier = Modifier.fillMaxWidth())
            }
            Section("Tax") {
                OutlinedTextField(
                    taxText,
                    { v -> taxText = v.filter { c -> c.isDigit() || c == '.' }; taxText.toFloatOrNull()?.let { update(cur.copy(taxRatePercent = it.coerceIn(0f, 100f))) } },
                    label = { Text("Tax rate") }, suffix = { Text("%") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(),
                )
                ToggleRow("Prices include tax", "VAT is already part of the shelf price", cur.taxInclusive) { update(cur.copy(taxInclusive = it)) }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Section("Appearance") {
                    ToggleRow("Match wallpaper colors", "Use Material You dynamic color", cur.dynamicColor) { update(cur.copy(dynamicColor = it)) }
                }
            }
            Section("About") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Version", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    Text(
                        "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                        style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Build", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    Text(BuildConfig.GIT_SHA, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(
                "Kwentaro · offline-first point of sale. Your data stays on this device.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            content()
        }
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked, onChange)
    }
}
