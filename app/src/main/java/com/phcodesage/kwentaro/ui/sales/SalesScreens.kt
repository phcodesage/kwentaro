package com.phcodesage.kwentaro.ui.sales

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.phcodesage.kwentaro.data.PaymentMethod
import com.phcodesage.kwentaro.data.PosRepository
import com.phcodesage.kwentaro.data.SaleStatus
import com.phcodesage.kwentaro.data.SaleWithItems
import com.phcodesage.kwentaro.data.SettingsRepository
import com.phcodesage.kwentaro.data.StoreSettings
import com.phcodesage.kwentaro.ui.components.EmptyState
import com.phcodesage.kwentaro.ui.components.appViewModel
import com.phcodesage.kwentaro.ui.theme.MoneyStyle
import com.phcodesage.kwentaro.util.formatDateTime
import com.phcodesage.kwentaro.util.formatMoney
import com.phcodesage.kwentaro.util.receiptNumber
import kotlinx.coroutines.launch

class SalesViewModel(private val repo: PosRepository, settingsRepo: SettingsRepository) : ViewModel() {
    val sales = repo.allSales
    val settings = settingsRepo.settings
    fun sale(id: Long) = repo.sale(id)
    fun refund(id: Long) = viewModelScope.launch { repo.refund(id) }
}

private fun PaymentMethod.icon() = when (this) {
    PaymentMethod.CASH -> Icons.Rounded.Payments
    PaymentMethod.CARD -> Icons.Rounded.CreditCard
    PaymentMethod.EWALLET -> Icons.Rounded.PhoneAndroid
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(onOpen: (Long) -> Unit) {
    val vm = appViewModel { SalesViewModel(it.repository, it.settings) }
    val sales by vm.sales.collectAsStateWithLifecycle(emptyList())
    val settings by vm.settings.collectAsStateWithLifecycle(StoreSettings())
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = { LargeTopAppBar(title = { Text("Sales") }, scrollBehavior = scroll) },
    ) { padding ->
        if (sales.isEmpty()) {
            EmptyState(Icons.Rounded.ReceiptLong, "No sales yet", "Completed sales and their receipts show up here.", Modifier.padding(padding))
            return@Scaffold
        }
        LazyColumn(contentPadding = PaddingValues(top = padding.calculateTopPadding(), bottom = 16.dp)) {
            items(sales, key = { it.id }) { s ->
                val refunded = s.status == SaleStatus.REFUNDED
                ListItem(
                    modifier = Modifier.clickable { onOpen(s.id) },
                    leadingContent = {
                        Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.secondaryContainer) {
                            Icon(s.paymentMethod.icon(), null, Modifier.padding(10.dp))
                        }
                    },
                    headlineContent = { Text(receiptNumber(s.id, s.createdAt)) },
                    supportingContent = { Text(listOfNotNull(s.createdAt.formatDateTime(), s.customerName).joinToString(" · ")) },
                    trailingContent = {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                s.totalCents.formatMoney(settings.currencySymbol), style = MoneyStyle,
                                textDecoration = if (refunded) TextDecoration.LineThrough else null,
                            )
                            if (refunded) Text("Refunded", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                        }
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(saleId: Long, onBack: () -> Unit) {
    val vm = appViewModel { SalesViewModel(it.repository, it.settings) }
    val sale by remember(saleId) { vm.sale(saleId) }.collectAsStateWithLifecycle(null)
    val settings by vm.settings.collectAsStateWithLifecycle(StoreSettings())
    val context = LocalContext.current
    var confirmRefund by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Receipt") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back") } },
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) { padding ->
        val s = sale ?: return@Scaffold
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ReceiptPaper(s, settings)
            Spacer(Modifier.height(20.dp))
            Row(Modifier.widthIn(max = 420.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Receipt ${receiptNumber(s.sale.id, s.sale.createdAt)}")
                            putExtra(Intent.EXTRA_TEXT, receiptText(s, settings))
                        }
                        context.startActivity(Intent.createChooser(send, "Share receipt"))
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                ) { Icon(Icons.Rounded.Share, null, Modifier.size(18.dp)); Spacer(Modifier.size(8.dp)); Text("Share") }
                if (s.sale.status == SaleStatus.COMPLETED) {
                    OutlinedButton(
                        onClick = { confirmRefund = true },
                        modifier = Modifier.weight(1f).height(52.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    ) { Icon(Icons.Rounded.Undo, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error); Spacer(Modifier.size(8.dp)); Text("Refund", color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }

    if (confirmRefund) {
        AlertDialog(
            onDismissRequest = { confirmRefund = false },
            title = { Text("Refund this sale?") },
            text = { Text("The sale is marked refunded and every item goes back into stock.") },
            confirmButton = { TextButton(onClick = { vm.refund(saleId); confirmRefund = false }) { Text("Refund") } },
            dismissButton = { TextButton(onClick = { confirmRefund = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun ReceiptPaper(s: SaleWithItems, settings: StoreSettings) {
    val cur = settings.currencySymbol
    Surface(
        Modifier.widthIn(max = 420.dp).fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 2.dp,
    ) {
        Column(Modifier.padding(24.dp)) {
            Text(settings.storeName, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Text(
                receiptNumber(s.sale.id, s.sale.createdAt) + "\n" + s.sale.createdAt.formatDateTime(),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            )
            if (s.sale.status == SaleStatus.REFUNDED) {
                Box(Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.Center) {
                    Surface(color = MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.small) {
                        Text("REFUNDED", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                }
            }
            Dashed()
            s.items.forEach { item ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(item.name, style = MaterialTheme.typography.bodyLarge)
                        Text("${item.quantity} × ${item.unitPriceCents.formatMoney(cur)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(item.lineTotalCents.formatMoney(cur), style = MoneyStyle)
                }
            }
            Dashed()
            Line("Subtotal", s.sale.subtotalCents.formatMoney(cur))
            if (s.sale.discountCents > 0) Line("Discount", "-" + s.sale.discountCents.formatMoney(cur))
            Line(if (settings.taxInclusive) "VAT (included)" else "Tax", s.sale.taxCents.formatMoney(cur))
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Text("TOTAL", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text(s.sale.totalCents.formatMoney(cur), style = MoneyStyle.merge(MaterialTheme.typography.titleLarge), color = MaterialTheme.colorScheme.primary)
            }
            Line("Paid · ${s.sale.paymentMethod.label}", s.sale.tenderedCents.formatMoney(cur))
            if (s.sale.paymentMethod == PaymentMethod.CASH) Line("Change", s.sale.changeCents.formatMoney(cur))
            s.sale.customerName?.let { Line("Customer", it) }
            Dashed()
            Text(settings.receiptFooter, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun Line(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, style = MoneyStyle)
    }
}

@Composable
private fun Dashed() {
    HorizontalDivider(Modifier.padding(vertical = 14.dp), color = MaterialTheme.colorScheme.outlineVariant)
}

private fun receiptText(s: SaleWithItems, st: StoreSettings): String = buildString {
    val cur = st.currencySymbol
    appendLine(st.storeName)
    appendLine(receiptNumber(s.sale.id, s.sale.createdAt))
    appendLine(s.sale.createdAt.formatDateTime())
    appendLine("--------------------------------")
    s.items.forEach { appendLine("${it.quantity} x ${it.name}  ${it.lineTotalCents.formatMoney(cur)}") }
    appendLine("--------------------------------")
    appendLine("Subtotal: ${s.sale.subtotalCents.formatMoney(cur)}")
    if (s.sale.discountCents > 0) appendLine("Discount: -${s.sale.discountCents.formatMoney(cur)}")
    appendLine((if (st.taxInclusive) "VAT (incl.): " else "Tax: ") + s.sale.taxCents.formatMoney(cur))
    appendLine("TOTAL: ${s.sale.totalCents.formatMoney(cur)}")
    appendLine("Paid (${s.sale.paymentMethod.label}): ${s.sale.tenderedCents.formatMoney(cur)}")
    if (s.sale.paymentMethod == PaymentMethod.CASH) appendLine("Change: ${s.sale.changeCents.formatMoney(cur)}")
    if (s.sale.status == SaleStatus.REFUNDED) appendLine("** REFUNDED **")
    appendLine()
    append(st.receiptFooter)
}
