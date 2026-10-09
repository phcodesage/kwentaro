package com.phcodesage.kwentaro.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

/** All money is stored as minor units (centavos) to keep totals exact. */
@Entity(tableName = "products", indices = [Index("barcode"), Index("category")])
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val barcode: String? = null,
    val category: String = "General",
    val priceCents: Long,
    val costCents: Long = 0,
    val stock: Int = 0,
    val lowStockThreshold: Int = 5,
    val imagePath: String? = null,
    val templateKey: String? = null,
    val archived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
) {
    val isLowStock: Boolean get() = stock <= lowStockThreshold
}

enum class PaymentMethod(val label: String) { CASH("Cash"), CARD("Card"), EWALLET("E-wallet") }

enum class SaleStatus { COMPLETED, REFUNDED }

@Entity(tableName = "sales", indices = [Index("createdAt")])
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val subtotalCents: Long,
    val discountCents: Long,
    val taxCents: Long,
    val totalCents: Long,
    val paymentMethod: PaymentMethod,
    val tenderedCents: Long,
    val changeCents: Long,
    val status: SaleStatus = SaleStatus.COMPLETED,
    val customerName: String? = null,
)

@Entity(
    tableName = "sale_items",
    foreignKeys = [ForeignKey(Sale::class, ["id"], ["saleId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("saleId"), Index("productId")],
)
data class SaleItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val saleId: Long = 0,
    // Nullable so history survives a product being deleted later.
    val productId: Long?,
    val name: String,
    val unitPriceCents: Long,
    val unitCostCents: Long,
    val quantity: Int,
) {
    val lineTotalCents: Long get() = unitPriceCents * quantity
}

data class SaleWithItems(
    @Embedded val sale: Sale,
    @Relation(parentColumn = "id", entityColumn = "saleId") val items: List<SaleItem>,
)

data class TopProduct(val name: String, val qty: Int, val revenueCents: Long)

data class DailyTotal(val day: String, val totalCents: Long, val count: Int)
