package com.phcodesage.kwentaro.ui.products

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phcodesage.kwentaro.data.Product
import com.phcodesage.kwentaro.data.StoreSettings
import com.phcodesage.kwentaro.ui.camera.BarcodeScannerDialog
import com.phcodesage.kwentaro.ui.camera.PhotoCaptureDialog
import com.phcodesage.kwentaro.ui.components.ProductThumb
import com.phcodesage.kwentaro.ui.components.appViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditorScreen(productId: Long, onDone: () -> Unit) {
    val vm = appViewModel(key = "product-$productId") { ProductEditorViewModel(it.repository, it.settings, productId) }
    val form by vm.form.collectAsStateWithLifecycle()
    val categories by vm.categories.collectAsStateWithLifecycle(emptyList())
    val settings by vm.settings.collectAsStateWithLifecycle(StoreSettings())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    var scanning by remember { mutableStateOf(false) }
    var capturing by remember { mutableStateOf(false) }
    var choosingImage by rememberSaveable { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { vm.importPhoto(context, it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (vm.isNew) "New product" else "Edit product") },
                navigationIcon = { IconButton(onClick = onDone) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back") } },
                actions = {
                    if (!vm.isNew) IconButton(onClick = { confirmDelete = true }) { Icon(Icons.Rounded.DeleteOutline, "Delete") }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            Button(
                onClick = {
                    showErrors = true
                    scope.launch { vm.save()?.let { snackbar.showSnackbar(it) } ?: onDone() }
                },
                modifier = Modifier.fillMaxWidth().navigationBarsPadding().imePadding().padding(16.dp).height(56.dp),
                shape = MaterialTheme.shapes.medium,
            ) { Text("Save product", style = MaterialTheme.typography.titleMedium) }
        },
    ) { padding ->
        if (!form.loaded) return@Scaffold
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Photo
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProductThumb(
                    Product(name = form.name.ifBlank { "?" }, priceCents = 0, imagePath = form.imagePath, templateKey = form.templateKey),
                    Modifier.size(112.dp),
                    MaterialTheme.shapes.large,
                )
                Spacer(Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(onClick = { capturing = true }) {
                        Icon(Icons.Rounded.CameraAlt, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Take photo")
                    }
                    TextButton(onClick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                        Icon(Icons.Rounded.PhotoLibrary, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("From gallery")
                    }
                    TextButton(onClick = { choosingImage = true }) {
                        Icon(Icons.Rounded.Image, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Choose image")
                    }
                }
            }
            if (form.imagePath != null || form.templateKey != null) {
                TextButton(onClick = vm::removeImage) {
                    Icon(Icons.Rounded.DeleteOutline, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Remove image")
                }
            }
            OutlinedTextField(
                value = form.name, onValueChange = vm::setName,
                label = { Text("Product name") }, singleLine = true,
                isError = showErrors && form.nameError, modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = form.barcode, onValueChange = { v -> vm.edit { it.copy(barcode = v) } },
                label = { Text("Barcode / SKU") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                trailingIcon = { IconButton(onClick = { scanning = true }) { Icon(Icons.Rounded.QrCodeScanner, "Scan barcode") } },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = form.price, onValueChange = { v -> vm.edit { it.copy(price = v.filter { c -> c.isDigit() || c == '.' }) } },
                    label = { Text("Price") }, prefix = { Text(settings.currencySymbol) }, singleLine = true,
                    isError = showErrors && form.priceError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = form.cost, onValueChange = { v -> vm.edit { it.copy(cost = v.filter { c -> c.isDigit() || c == '.' }) } },
                    label = { Text("Cost") }, prefix = { Text(settings.currencySymbol) }, singleLine = true,
                    supportingText = { Text("For profit reports") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f),
                )
            }
            OutlinedTextField(
                value = form.category, onValueChange = { v -> vm.edit { it.copy(category = v) } },
                label = { Text("Category") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            )
            if (categories.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { c -> FilterChip(selected = form.category == c, onClick = { vm.edit { it.copy(category = c) } }, label = { Text(c) }) }
                }
            }
            Text("Inventory", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalIconButton(onClick = { vm.edit { it.copy(stock = ((it.stock.toIntOrNull() ?: 0) - 1).coerceAtLeast(0).toString()) } }) { Icon(Icons.Rounded.Remove, "Less stock") }
                OutlinedTextField(
                    value = form.stock, onValueChange = { v -> vm.edit { it.copy(stock = v.filter(Char::isDigit)) } },
                    label = { Text("In stock") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f),
                )
                FilledTonalIconButton(onClick = { vm.edit { it.copy(stock = ((it.stock.toIntOrNull() ?: 0) + 1).toString()) } }) { Icon(Icons.Rounded.Add, "More stock") }
                OutlinedTextField(
                    value = form.lowStock, onValueChange = { v -> vm.edit { it.copy(lowStock = v.filter(Char::isDigit)) } },
                    label = { Text("Alert at") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(0.8f),
                )
            }
            Box(Modifier.height(24.dp))
        }
    }

    if (scanning) BarcodeScannerDialog(onScanned = { code -> vm.edit { it.copy(barcode = code) } }, onDismiss = { scanning = false })
    if (capturing) PhotoCaptureDialog(onCaptured = vm::setPhoto, onDismiss = { capturing = false })
    if (choosingImage) {
        ProductTemplatePicker(
            productName = form.name,
            selectedKey = form.templateKey,
            onSelected = { template -> vm.chooseTemplate(template.key); choosingImage = false },
            onDismiss = { choosingImage = false },
        )
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            icon = { Icon(Icons.Rounded.DeleteOutline, null) },
            title = { Text("Delete ${form.name}?") },
            text = { Text("It disappears from the register. Past sales keep their records.") },
            confirmButton = { TextButton(onClick = { scope.launch { vm.delete(); onDone() } }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } },
        )
    }
}
