package com.phcodesage.kwentaro.ui.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phcodesage.kwentaro.data.StoreSettings
import com.phcodesage.kwentaro.ui.camera.BarcodeScannerDialog
import com.phcodesage.kwentaro.ui.components.EmptyState
import com.phcodesage.kwentaro.ui.components.ProductThumb
import com.phcodesage.kwentaro.ui.components.appViewModel
import com.phcodesage.kwentaro.ui.theme.MoneyStyle
import com.phcodesage.kwentaro.util.formatMoney
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(onEdit: (Long) -> Unit, onAdd: () -> Unit) {
    val vm = appViewModel { ProductsListViewModel(it.repository, it.settings) }
    val products by vm.products.collectAsStateWithLifecycle(emptyList())
    val settings by vm.settings.collectAsStateWithLifecycle(StoreSettings())
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var scanning by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Products") },
                actions = { IconButton(onClick = { scanning = true }) { Icon(Icons.Rounded.QrCodeScanner, "Find by barcode") } },
                scrollBehavior = scroll,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAdd, icon = { Icon(Icons.Rounded.Add, null) }, text = { Text("New product") })
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        if (products.isEmpty()) {
            EmptyState(Icons.Rounded.Inventory2, "No products yet", "Add your first product — snap a photo and scan its barcode.", Modifier.padding(padding))
        } else {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(top = padding.calculateTopPadding(), bottom = 96.dp)) {
                items(products, key = { it.id }) { p ->
                    ListItem(
                        modifier = Modifier.clickable { onEdit(p.id) }.padding(horizontal = 8.dp),
                        headlineContent = { Text(p.name) },
                        supportingContent = {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(listOfNotNull(p.category, p.barcode).joinToString(" · "), style = MaterialTheme.typography.bodySmall)
                            }
                        },
                        leadingContent = { ProductThumb(p, Modifier.size(56.dp), MaterialTheme.shapes.small) },
                        trailingContent = {
                            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                                Text(p.priceCents.formatMoney(settings.currencySymbol), style = MoneyStyle)
                                AssistChip(
                                    onClick = { onEdit(p.id) },
                                    label = { Text("${p.stock} in stock") },
                                    colors = if (p.isLowStock) AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        labelColor = MaterialTheme.colorScheme.onErrorContainer,
                                    ) else AssistChipDefaults.assistChipColors(),
                                )
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
                    )
                }
            }
        }
    }

    if (scanning) {
        BarcodeScannerDialog(
            onScanned = { code ->
                scope.launch {
                    val match = vm.find(code)
                    if (match != null) onEdit(match.id) else snackbar.showSnackbar("No product with barcode $code")
                }
            },
            onDismiss = { scanning = false },
        )
    }
}
