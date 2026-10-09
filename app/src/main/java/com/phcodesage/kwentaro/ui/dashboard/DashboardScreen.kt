package com.phcodesage.kwentaro.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phcodesage.kwentaro.data.DailyTotal
import com.phcodesage.kwentaro.data.PosRepository
import com.phcodesage.kwentaro.data.Product
import com.phcodesage.kwentaro.data.SettingsRepository
import com.phcodesage.kwentaro.data.StoreSettings
import com.phcodesage.kwentaro.data.TopProduct
import com.phcodesage.kwentaro.ui.components.appViewModel
import com.phcodesage.kwentaro.ui.theme.MoneyStyle
import com.phcodesage.kwentaro.util.formatMoney
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DashboardViewModel(repo: PosRepository, settingsRepo: SettingsRepository) : ViewModel() {
    private val zone = ZoneId.systemDefault()
    val today: LocalDate = LocalDate.now()
    private val startOfToday = today.atStartOfDay(zone).toInstant().toEpochMilli()
    private val weekStart = today.minusDays(6).atStartOfDay(zone).toInstant().toEpochMilli()

    val daily = repo.dailyTotals(weekStart)
    val top = repo.topProducts(weekStart)
    val costToday = repo.costSince(startOfToday)
    val lowStock = repo.lowStock
    val settings = settingsRepo.settings
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val vm = appViewModel { DashboardViewModel(it.repository, it.settings) }
    val daily by vm.daily.collectAsStateWithLifecycle(emptyList())
    val top by vm.top.collectAsStateWithLifecycle(emptyList())
    val costToday by vm.costToday.collectAsStateWithLifecycle(0L)
    val lowStock by vm.lowStock.collectAsStateWithLifecycle(emptyList())
    val settings by vm.settings.collectAsStateWithLifecycle(StoreSettings())
    val cur = settings.currencySymbol
    val scroll = TopAppBarDefaults.pinnedScrollBehavior()

    val todayKey = vm.today.toString()
    val todayTotal = daily.firstOrNull { it.day == todayKey }
    val revenue = todayTotal?.totalCents ?: 0L
    val count = todayTotal?.count ?: 0
    // Revenue is tax-inclusive when VAT is inclusive; profit here is a quick gross figure.
    val profit = revenue - costToday

    Scaffold(
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = { TopAppBar(title = { Text("Insights") }, scrollBehavior = scroll) },
    ) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Today", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp), maxItemsInEachRow = 4) {
                val tile = Modifier.weight(1f).widthIn(min = 150.dp)
                StatTile(Icons.Rounded.Payments, "Revenue", revenue.formatMoney(cur), tile, highlight = true)
                StatTile(Icons.Rounded.ReceiptLong, "Sales", count.toString(), tile)
                StatTile(Icons.Rounded.TrendingUp, "Avg. ticket", (if (count > 0) revenue / count else 0L).formatMoney(cur), tile)
                StatTile(Icons.Rounded.Savings, "Gross profit", profit.formatMoney(cur), tile)
            }
            WeekChart(daily, vm.today, cur)
            TopProductsCard(top, cur)
            LowStockCard(lowStock)
        }
    }
}

@Composable
private fun StatTile(icon: ImageVector, label: String, value: String, modifier: Modifier, highlight: Boolean = false) {
    val colors = if (highlight) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
    else CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest, contentColor = MaterialTheme.colorScheme.onSurface)
    Card(modifier, colors = colors) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, Modifier.size(22.dp), tint = if (highlight) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.merge(MoneyStyle), maxLines = 1)
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = if (highlight) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun WeekChart(daily: List<DailyTotal>, today: LocalDate, cur: String) {
    val days = remember(today) { (6 downTo 0).map { today.minusDays(it.toLong()) } }
    val byDay = daily.associateBy { it.day }
    val values = days.map { byDay[it.toString()]?.totalCents ?: 0L }
    val max = (values.maxOrNull() ?: 0L).coerceAtLeast(1L)
    val bar = MaterialTheme.colorScheme.primary
    val todayBar = MaterialTheme.colorScheme.tertiary
    val track = MaterialTheme.colorScheme.surfaceContainerHigh
    val fmt = DateTimeFormatter.ofPattern("EEE")

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
        Column(Modifier.padding(16.dp)) {
            Text("Last 7 days", style = MaterialTheme.typography.titleMedium)
            Text(values.sum().formatMoney(cur), style = MaterialTheme.typography.headlineSmall.merge(MoneyStyle), color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Canvas(Modifier.fillMaxWidth().height(140.dp)) {
                val gap = 12.dp.toPx()
                val w = (size.width - gap * (values.size - 1)) / values.size
                values.forEachIndexed { i, v ->
                    val x = i * (w + gap)
                    val r = CornerRadius(8.dp.toPx())
                    drawRoundRect(track, Offset(x, 0f), Size(w, size.height), r)
                    val h = size.height * (v.toFloat() / max)
                    if (h > 0f) drawRoundRect(if (i == values.lastIndex) todayBar else bar, Offset(x, size.height - h), Size(w, h), r)
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 6.dp)) {
                days.forEach { d ->
                    Text(d.format(fmt), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun TopProductsCard(top: List<TopProduct>, cur: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Best sellers this week", style = MaterialTheme.typography.titleMedium)
            if (top.isEmpty()) Text("Ring up a few sales to see your best sellers.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            val maxQty = top.maxOfOrNull { it.qty }?.coerceAtLeast(1) ?: 1
            top.forEach { t ->
                Column {
                    Row {
                        Text(t.name, modifier = Modifier.weight(1f))
                        Text("${t.qty} sold · ${t.revenueCents.formatMoney(cur)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    LinearProgressIndicator(
                        progress = { t.qty.toFloat() / maxQty },
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    )
                }
            }
        }
    }
}

@Composable
private fun LowStockCard(items: List<Product>) {
    Card(colors = CardDefaults.cardColors(containerColor = if (items.isEmpty()) MaterialTheme.colorScheme.surfaceContainerLowest else MaterialTheme.colorScheme.errorContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(if (items.isEmpty()) Icons.Rounded.Inventory else Icons.Rounded.WarningAmber, null)
                Spacer(Modifier.size(8.dp))
                Text(if (items.isEmpty()) "Stock looks healthy" else "Restock soon", style = MaterialTheme.typography.titleMedium)
            }
            items.forEach { p ->
                Row {
                    Text(p.name, modifier = Modifier.weight(1f))
                    Text(if (p.stock <= 0) "Out of stock" else "${p.stock} left", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
