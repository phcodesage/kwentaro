package com.phcodesage.kwentaro.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phcodesage.kwentaro.data.CartLine
import com.phcodesage.kwentaro.data.PaymentMethod
import com.phcodesage.kwentaro.data.PosRepository
import com.phcodesage.kwentaro.data.Product
import com.phcodesage.kwentaro.data.SettingsRepository
import com.phcodesage.kwentaro.data.StoreSettings
import com.phcodesage.kwentaro.data.Totals
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterState(
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val query: String = "",
    val lines: List<CartLine> = emptyList(),
    val discountPercent: Int = 0,
    val totals: Totals = Totals(0, 0, 0, 0),
    val settings: StoreSettings = StoreSettings(),
) {
    val itemCount: Int get() = lines.sumOf { it.quantity }
    fun qtyOf(id: Long) = lines.firstOrNull { it.product.id == id }?.quantity ?: 0
}

private data class Filters(val query: String, val category: String?, val cart: Map<Long, Int>, val discount: Int)

class RegisterViewModel(private val repo: PosRepository, settingsRepo: SettingsRepository) : ViewModel() {
    private val filters = MutableStateFlow(Filters("", null, linkedMapOf(), 0))
    private val messages = Channel<String>(Channel.BUFFERED)
    val events = messages.receiveAsFlow()

    val state = combine(repo.allProducts, repo.categories, settingsRepo.settings, filters) { all, cats, settings, f ->
        val byId = all.associateBy { it.id }
        val lines = f.cart.mapNotNull { (id, qty) -> byId[id]?.let { CartLine(it, qty) } }
        val q = f.query.trim()
        RegisterState(
            products = all.filter { p ->
                (f.category == null || p.category == f.category) &&
                    (q.isEmpty() || p.name.contains(q, ignoreCase = true) || p.barcode?.contains(q) == true)
            },
            categories = cats,
            selectedCategory = f.category,
            query = f.query,
            lines = lines,
            discountPercent = f.discount,
            totals = Totals.of(lines, f.discount, settings),
            settings = settings,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RegisterState())

    fun setQuery(q: String) = filters.update { it.copy(query = q) }
    fun selectCategory(c: String?) = filters.update { it.copy(category = c) }
    fun setDiscount(p: Int) = filters.update { it.copy(discount = p.coerceIn(0, 100)) }

    fun add(product: Product) = setQty(product, state.value.qtyOf(product.id) + 1)
    fun decrement(product: Product) = setQty(product, state.value.qtyOf(product.id) - 1)
    fun remove(product: Product) = setQty(product, 0)
    fun clear() = filters.update { it.copy(cart = linkedMapOf(), discount = 0) }

    private fun setQty(product: Product, qty: Int) {
        if (qty > product.stock) {
            messages.trySend(if (product.stock <= 0) "${product.name} is out of stock" else "Only ${product.stock} ${product.name} left")
            return
        }
        filters.update { f ->
            val cart = LinkedHashMap(f.cart)
            if (qty <= 0) cart.remove(product.id) else cart[product.id] = qty
            f.copy(cart = cart)
        }
    }

    fun onBarcode(code: String) = viewModelScope.launch {
        val product = repo.findByBarcode(code)
        if (product == null) messages.send("No product with barcode $code") else add(product)
    }

    fun checkout(method: PaymentMethod, tenderedCents: Long, customer: String, onDone: (Long) -> Unit) = viewModelScope.launch {
        val s = state.value
        if (s.lines.isEmpty()) return@launch
        val tendered = if (method == PaymentMethod.CASH) tenderedCents else s.totals.totalCents
        val id = repo.checkout(s.lines, s.totals, method, tendered, customer)
        clear()
        onDone(id)
    }
}
