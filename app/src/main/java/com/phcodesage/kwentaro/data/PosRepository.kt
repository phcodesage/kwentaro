package com.phcodesage.kwentaro.data

import androidx.room.withTransaction
import kotlin.math.roundToLong

data class CartLine(val product: Product, val quantity: Int) {
    val totalCents: Long get() = product.priceCents * quantity
}

data class Totals(
    val subtotalCents: Long,
    val discountCents: Long,
    val taxCents: Long,
    val totalCents: Long,
) {
    companion object {
        /**
         * Discount applies before tax. With inclusive tax the shelf price already contains
         * VAT, so the tax is backed out of the total rather than added on top.
         */
        fun of(lines: List<CartLine>, discountPercent: Int, settings: StoreSettings): Totals {
            val subtotal = lines.sumOf { it.totalCents }
            val discount = (subtotal * discountPercent / 100.0).roundToLong()
            val taxable = subtotal - discount
            val rate = settings.taxRatePercent / 100.0
            return if (settings.taxInclusive) {
                val tax = (taxable - taxable / (1 + rate)).roundToLong()
                Totals(subtotal, discount, tax, taxable)
            } else {
                val tax = (taxable * rate).roundToLong()
                Totals(subtotal, discount, tax, taxable + tax)
            }
        }
    }
}

class PosRepository(private val db: AppDatabase) {
    private val products = db.productDao()
    private val sales = db.saleDao()

    val allProducts = products.observeAll()
    val categories = products.observeCategories()
    val lowStock = products.observeLowStock()
    val allSales = sales.observeAll()

    suspend fun product(id: Long) = products.get(id)
    suspend fun findByBarcode(code: String) = products.findByBarcode(code)
    suspend fun saveProduct(p: Product) = products.upsert(p)
    suspend fun archiveProduct(id: Long) = products.archive(id)
    suspend fun adjustStock(id: Long, delta: Int) = products.adjustStock(id, delta)

    fun sale(id: Long) = sales.observeWithItems(id)
    fun dailyTotals(from: Long) = sales.observeDailyTotals(from)
    fun topProducts(from: Long, limit: Int = 5) = sales.observeTopProducts(from, limit)
    fun costSince(from: Long) = sales.observeCostSince(from)

    /** Records the sale and decrements stock atomically; returns the new sale id. */
    suspend fun checkout(
        lines: List<CartLine>,
        totals: Totals,
        method: PaymentMethod,
        tenderedCents: Long,
        customerName: String?,
    ): Long = db.withTransaction {
        val saleId = sales.insertSale(
            Sale(
                subtotalCents = totals.subtotalCents,
                discountCents = totals.discountCents,
                taxCents = totals.taxCents,
                totalCents = totals.totalCents,
                paymentMethod = method,
                tenderedCents = tenderedCents,
                changeCents = (tenderedCents - totals.totalCents).coerceAtLeast(0),
                customerName = customerName?.takeIf { it.isNotBlank() },
            )
        )
        sales.insertItems(lines.map {
            SaleItem(
                saleId = saleId,
                productId = it.product.id,
                name = it.product.name,
                unitPriceCents = it.product.priceCents,
                unitCostCents = it.product.costCents,
                quantity = it.quantity,
            )
        })
        lines.forEach { products.adjustStock(it.product.id, -it.quantity) }
        saleId
    }

    /** Marks a sale refunded and puts its items back on the shelf. */
    suspend fun refund(saleId: Long) = db.withTransaction {
        val sale = sales.getWithItems(saleId) ?: return@withTransaction
        if (sale.sale.status == SaleStatus.REFUNDED) return@withTransaction
        sales.setStatus(saleId, SaleStatus.REFUNDED)
        sale.items.forEach { item -> item.productId?.let { products.adjustStock(it, item.quantity) } }
    }

    suspend fun seedIfEmpty() {
        if (products.count() > 0) return
        products.insertAll(SampleData.products)
    }
}
