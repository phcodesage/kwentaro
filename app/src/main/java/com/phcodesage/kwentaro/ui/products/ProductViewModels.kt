package com.phcodesage.kwentaro.ui.products

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phcodesage.kwentaro.data.PosRepository
import com.phcodesage.kwentaro.data.Product
import com.phcodesage.kwentaro.data.SettingsRepository
import com.phcodesage.kwentaro.ui.camera.productPhotoFile
import com.phcodesage.kwentaro.util.centsToInput
import com.phcodesage.kwentaro.util.parseMoneyToCents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductsListViewModel(private val repo: PosRepository, settingsRepo: SettingsRepository) : ViewModel() {
    val products = repo.allProducts
    val settings = settingsRepo.settings
    suspend fun find(code: String) = repo.findByBarcode(code)
}

data class ProductForm(
    val id: Long = 0,
    val name: String = "",
    val barcode: String = "",
    val category: String = "General",
    val price: String = "",
    val cost: String = "",
    val stock: String = "0",
    val lowStock: String = "5",
    val imagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val loaded: Boolean = false,
) {
    val priceCents get() = price.parseMoneyToCents()
    val nameError get() = name.isBlank()
    val priceError get() = priceCents == null || priceCents!! < 0
    val valid get() = !nameError && !priceError
}

class ProductEditorViewModel(
    private val repo: PosRepository,
    settingsRepo: SettingsRepository,
    private val productId: Long,
) : ViewModel() {
    val form = MutableStateFlow(ProductForm(loaded = productId == 0L))
    val categories = repo.categories
    val settings = settingsRepo.settings
    val isNew get() = productId == 0L

    init {
        if (productId != 0L) viewModelScope.launch {
            repo.product(productId)?.let { p ->
                form.value = ProductForm(
                    id = p.id, name = p.name, barcode = p.barcode.orEmpty(), category = p.category,
                    price = p.priceCents.centsToInput(), cost = p.costCents.centsToInput(),
                    stock = p.stock.toString(), lowStock = p.lowStockThreshold.toString(),
                    imagePath = p.imagePath, createdAt = p.createdAt, loaded = true,
                )
            }
        }
    }

    fun edit(transform: (ProductForm) -> ProductForm) = form.update(transform)

    fun importPhoto(context: Context, uri: Uri) = viewModelScope.launch {
        val path = withContext(Dispatchers.IO) {
            val dest = productPhotoFile(context)
            context.contentResolver.openInputStream(uri)?.use { input -> dest.outputStream().use { input.copyTo(it) } }
            dest.absolutePath
        }
        edit { it.copy(imagePath = path) }
    }

    /** Returns an error message, or null after saving. */
    suspend fun save(): String? {
        val f = form.value
        if (!f.valid) return "Name and a valid price are required"
        val code = f.barcode.trim().ifEmpty { null }
        if (code != null) {
            val clash = repo.findByBarcode(code)
            if (clash != null && clash.id != f.id) return "Barcode already used by ${clash.name}"
        }
        repo.saveProduct(
            Product(
                id = f.id,
                name = f.name.trim(),
                barcode = code,
                category = f.category.trim().ifEmpty { "General" },
                priceCents = f.priceCents ?: 0,
                costCents = f.cost.parseMoneyToCents() ?: 0,
                stock = f.stock.toIntOrNull() ?: 0,
                lowStockThreshold = f.lowStock.toIntOrNull() ?: 5,
                imagePath = f.imagePath,
                createdAt = f.createdAt,
            )
        )
        return null
    }

    suspend fun delete() {
        if (productId != 0L) repo.archiveProduct(productId)
    }
}
