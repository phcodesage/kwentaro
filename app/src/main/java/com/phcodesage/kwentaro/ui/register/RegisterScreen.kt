package com.phcodesage.kwentaro.ui.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingBasket
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phcodesage.kwentaro.data.CartLine
import com.phcodesage.kwentaro.data.PaymentMethod
import com.phcodesage.kwentaro.data.Product
import com.phcodesage.kwentaro.ui.camera.BarcodeScannerDialog
import com.phcodesage.kwentaro.ui.components.EmptyState
import com.phcodesage.kwentaro.ui.components.ProductThumb
import com.phcodesage.kwentaro.ui.components.appViewModel
import com.phcodesage.kwentaro.ui.theme.MoneyStyle
import com.phcodesage.kwentaro.util.formatMoney
import com.phcodesage.kwentaro.util.parseMoneyToCents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(wide: Boolean, onCheckedOut: (Long) -> Unit) {
    val vm = appViewModel { RegisterViewModel(it.repository, it.settings) }
    val state by vm.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var scanning by remember { mutableStateOf(false) }
    var showCart by remember { mutableStateOf(false) }
    var showCheckout by remember { mutableStateOf(false) }
    val cur = state.settings.currencySymbol

    LaunchedEffect(Unit) { vm.events.collect { snackbar.showSnackbar(it) } }

    Box(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxSize()) {
            Column(Modifier.weight(1.6f).fillMaxHeight()) {
                CatalogHeader(state, vm, onScan = { scanning = true })
                ProductGrid(
                    state = state,
                    onTap = vm::add,
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = if (wide) 16.dp else 96.dp),
                )
            }
            if (wide) {
                Surface(
                    Modifier.weight(1f).fillMaxHeight(),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    CartPanel(state, vm, onCharge = { showCheckout = true }, Modifier.windowInsetsPadding(WindowInsets.statusBars))
                }
            }
        }

        if (!wide) {
            AnimatedVisibility(
                visible = state.lines.isNotEmpty(),
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                CartBar(state.itemCount, state.totals.totalCents.formatMoney(cur), onClick = { showCart = true })
            }
        }
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).padding(bottom = if (wide) 0.dp else 80.dp))
    }

    if (showCart && !wide) {
        ModalBottomSheet(onDismissRequest = { showCart = false }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
            CartPanel(state, vm, onCharge = { showCart = false; showCheckout = true })
        }
    }
    if (showCheckout) {
        CheckoutSheet(
            totalCents = state.totals.totalCents,
            currency = cur,
            onDismiss = { showCheckout = false },
            onConfirm = { method, tendered, customer ->
                showCheckout = false
                vm.checkout(method, tendered, customer, onCheckedOut)
            },
        )
    }
    if (scanning) {
        BarcodeScannerDialog(onScanned = vm::onBarcode, onDismiss = { scanning = false }, continuous = true, hint = "Scan items to add to the cart")
    }
}

@Composable
private fun CatalogHeader(state: RegisterState, vm: RegisterViewModel, onScan: () -> Unit) {
    Column(Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(top = 12.dp)) {
        Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Register", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(state.settings.storeName, style = MaterialTheme.typography.headlineSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            FilledTonalIconButton(onClick = onScan, modifier = Modifier.size(52.dp)) {
                Icon(Icons.Rounded.QrCodeScanner, "Scan barcode")
            }
        }
        OutlinedTextField(
            value = state.query,
            onValueChange = vm::setQuery,
            placeholder = { Text("Search name or barcode") },
            leadingIcon = { Icon(Icons.Rounded.Search, null) },
            trailingIcon = {
                if (state.query.isNotEmpty()) IconButton(onClick = { vm.setQuery("") }) { Icon(Icons.Rounded.Close, "Clear search") }
            },
            singleLine = true,
            shape = CircleShape,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        )
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item { FilterChip(selected = state.selectedCategory == null, onClick = { vm.selectCategory(null) }, label = { Text("All") }) }
            items(state.categories) { c ->
                FilterChip(selected = state.selectedCategory == c, onClick = { vm.selectCategory(if (state.selectedCategory == c) null else c) }, label = { Text(c) })
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ProductGrid(state: RegisterState, onTap: (Product) -> Unit, contentPadding: PaddingValues) {
    if (state.products.isEmpty()) {
        EmptyState(Icons.Rounded.Search, "Nothing here", if (state.query.isBlank()) "Add products in the Products tab." else "No product matches \"${state.query}\".")
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(state.products, key = { it.id }) { p ->
            ProductCard(p, state.qtyOf(p.id), state.settings.currencySymbol, onClick = { onTap(p) })
        }
    }
}

@Composable
private fun ProductCard(p: Product, inCart: Int, currency: String, onClick: () -> Unit) {
    val out = p.stock <= 0
    Card(
        onClick = onClick,
        enabled = !out,
        colors = CardDefaults.cardColors(
            containerColor = if (inCart > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (inCart > 0) 0.dp else 1.dp),
    ) {
        Box {
            ProductThumb(p, Modifier.fillMaxWidth().aspectRatio(1.35f).padding(8.dp))
            if (inCart > 0) {
                Badge(Modifier.align(Alignment.TopEnd).padding(14.dp), containerColor = MaterialTheme.colorScheme.tertiary) {
                    Text("×$inCart", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                }
            }
        }
        Column(Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
            Text(p.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(p.priceCents.formatMoney(currency), style = MoneyStyle, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                Text(
                    if (out) "Out" else "${p.stock} left",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (p.isLowStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CartBar(count: Int, total: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
    ) {
        Row(Modifier.padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.ShoppingBasket, null)
            Spacer(Modifier.width(12.dp))
            Text("$count item${if (count == 1) "" else "s"}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text(total, style = MoneyStyle.merge(MaterialTheme.typography.titleMedium))
        }
    }
}

@Composable
private fun CartPanel(state: RegisterState, vm: RegisterViewModel, onCharge: () -> Unit, modifier: Modifier = Modifier) {
    val cur = state.settings.currencySymbol
    Column(modifier.fillMaxWidth().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Current sale", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            if (state.lines.isNotEmpty()) TextButton(onClick = vm::clear) {
                Icon(Icons.Rounded.DeleteSweep, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Clear")
            }
        }
        if (state.lines.isEmpty()) {
            Box(Modifier.weight(1f, fill = false).height(240.dp)) {
                EmptyState(Icons.Rounded.ShoppingBasket, "Cart is empty", "Tap a product or scan a barcode to start a sale.")
            }
        } else {
            LazyColumn(Modifier.weight(1f, fill = false), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(state.lines, key = { it.product.id }) { CartRow(it, cur, vm) }
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 12.dp))
        Text("Discount", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 6.dp)) {
            listOf(0, 5, 10, 20).forEach { d ->
                FilterChip(selected = state.discountPercent == d, onClick = { vm.setDiscount(d) }, label = { Text(if (d == 0) "None" else "$d%") })
            }
        }
        TotalRow("Subtotal", state.totals.subtotalCents.formatMoney(cur))
        if (state.totals.discountCents > 0) TotalRow("Discount", "-" + state.totals.discountCents.formatMoney(cur))
        TotalRow(
            if (state.settings.taxInclusive) "VAT ${state.settings.taxRatePercent.clean()}% (incl.)" else "Tax ${state.settings.taxRatePercent.clean()}%",
            state.totals.taxCents.formatMoney(cur),
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Total", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            Text(state.totals.totalCents.formatMoney(cur), style = MoneyStyle.merge(MaterialTheme.typography.headlineSmall), color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onCharge,
            enabled = state.lines.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.medium,
        ) { Text("Charge ${state.totals.totalCents.formatMoney(cur)}", style = MaterialTheme.typography.titleMedium) }
    }
}

private fun Float.clean() = if (this % 1f == 0f) toInt().toString() else toString()

@Composable
private fun TotalRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, style = MoneyStyle)
    }
}

@Composable
private fun CartRow(line: CartLine, cur: String, vm: RegisterViewModel) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        ProductThumb(line.product, Modifier.size(48.dp), MaterialTheme.shapes.small)
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(line.product.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(line.totalCents.formatMoney(cur), style = MoneyStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        FilledTonalIconButton(onClick = { vm.decrement(line.product) }, modifier = Modifier.size(34.dp)) { Icon(Icons.Rounded.Remove, "Less") }
        Text("${line.quantity}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        FilledTonalIconButton(onClick = { vm.add(line.product) }, modifier = Modifier.size(34.dp)) { Icon(Icons.Rounded.Add, "More") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckoutSheet(totalCents: Long, currency: String, onDismiss: () -> Unit, onConfirm: (PaymentMethod, Long, String) -> Unit) {
    var method by rememberSaveable { mutableStateOf(PaymentMethod.CASH) }
    var tenderedText by rememberSaveable { mutableStateOf("") }
    var customer by rememberSaveable { mutableStateOf("") }
    val tendered = tenderedText.parseMoneyToCents() ?: 0L
    val enough = method != PaymentMethod.CASH || tendered >= totalCents

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Column {
                Text("Amount due", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(totalCents.formatMoney(currency), style = MoneyStyle.merge(MaterialTheme.typography.displaySmall), color = MaterialTheme.colorScheme.primary)
            }
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                PaymentMethod.entries.forEachIndexed { i, m ->
                    SegmentedButton(
                        selected = method == m,
                        onClick = { method = m },
                        shape = SegmentedButtonDefaults.itemShape(i, PaymentMethod.entries.size),
                    ) { Text(m.label) }
                }
            }
            if (method == PaymentMethod.CASH) {
                OutlinedTextField(
                    value = tenderedText,
                    onValueChange = { tenderedText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Cash received") },
                    prefix = { Text(currency) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(quickCash(totalCents)) { c ->
                        FilterChip(
                            selected = tendered == c,
                            onClick = { tenderedText = (c / 100).toString() + if (c % 100 != 0L) ".%02d".format(c % 100) else "" },
                            label = { Text(if (c == totalCents) "Exact" else c.formatMoney(currency)) },
                        )
                    }
                }
                Surface(color = MaterialTheme.colorScheme.tertiaryContainer, shape = MaterialTheme.shapes.medium) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Change", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Text(
                            if (enough) (tendered - totalCents).formatMoney(currency) else "—",
                            style = MoneyStyle.merge(MaterialTheme.typography.headlineSmall),
                        )
                    }
                }
            }
            OutlinedTextField(
                value = customer, onValueChange = { customer = it },
                label = { Text("Customer name (optional)") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { onConfirm(method, tendered, customer) },
                enabled = enough,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium,
            ) { Text("Complete sale", style = MaterialTheme.typography.titleMedium) }
        }
    }
}

/** Exact amount plus the next common bill denominations above it. */
private fun quickCash(total: Long): List<Long> {
    val bills = listOf(20, 50, 100, 200, 500, 1000).map { it * 100L }
    val rounded = bills.mapNotNull { b -> ((total + b - 1) / b * b).takeIf { it > total } }
    return (listOf(total) + rounded).distinct().sorted().take(5)
}
