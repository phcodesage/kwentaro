package com.phcodesage.kwentaro.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE archived = 0 ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<Product>>

    @Query("SELECT DISTINCT category FROM products WHERE archived = 0 ORDER BY category COLLATE NOCASE")
    fun observeCategories(): Flow<List<String>>

    @Query("SELECT * FROM products WHERE archived = 0 AND stock <= lowStockThreshold ORDER BY stock")
    fun observeLowStock(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun get(id: Long): Product?

    @Query("SELECT * FROM products WHERE barcode = :barcode AND archived = 0 LIMIT 1")
    suspend fun findByBarcode(barcode: String): Product?

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int

    @Upsert
    suspend fun upsert(product: Product): Long

    @Insert
    suspend fun insertAll(products: List<Product>)

    @Query("UPDATE products SET archived = 1 WHERE id = :id")
    suspend fun archive(id: Long)

    @Query("UPDATE products SET stock = stock + :delta WHERE id = :id")
    suspend fun adjustStock(id: Long, delta: Int)
}

@Dao
interface SaleDao {
    @Insert
    suspend fun insertSale(sale: Sale): Long

    @Insert
    suspend fun insertItems(items: List<SaleItem>)

    @Query("SELECT * FROM sales ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Sale>>

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :id")
    fun observeWithItems(id: Long): Flow<SaleWithItems?>

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getWithItems(id: Long): SaleWithItems?

    @Query("UPDATE sales SET status = :status WHERE id = :id")
    suspend fun setStatus(id: Long, status: SaleStatus)

    @Query(
        """
        SELECT strftime('%Y-%m-%d', createdAt / 1000, 'unixepoch', 'localtime') AS day,
               SUM(totalCents) AS totalCents, COUNT(*) AS count
        FROM sales WHERE status = 'COMPLETED' AND createdAt >= :from
        GROUP BY day ORDER BY day
        """
    )
    fun observeDailyTotals(from: Long): Flow<List<DailyTotal>>

    @Query(
        """
        SELECT si.name AS name, SUM(si.quantity) AS qty,
               SUM(si.quantity * si.unitPriceCents) AS revenueCents
        FROM sale_items si JOIN sales s ON s.id = si.saleId
        WHERE s.status = 'COMPLETED' AND s.createdAt >= :from
        GROUP BY si.name ORDER BY qty DESC LIMIT :limit
        """
    )
    fun observeTopProducts(from: Long, limit: Int): Flow<List<TopProduct>>

    @Query(
        """
        SELECT COALESCE(SUM(si.quantity * si.unitCostCents), 0)
        FROM sale_items si JOIN sales s ON s.id = si.saleId
        WHERE s.status = 'COMPLETED' AND s.createdAt >= :from
        """
    )
    fun observeCostSince(from: Long): Flow<Long>
}
