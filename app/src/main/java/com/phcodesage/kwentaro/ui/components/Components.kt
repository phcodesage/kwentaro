package com.phcodesage.kwentaro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.phcodesage.kwentaro.KwentaroApp
import com.phcodesage.kwentaro.data.Product
import java.io.File
import kotlin.math.abs

/** Builds a ViewModel with access to the app-wide repositories. */
@Composable
inline fun <reified VM : ViewModel> appViewModel(key: String? = null, crossinline create: (KwentaroApp) -> VM): VM {
    val app = LocalContext.current.applicationContext as KwentaroApp
    return viewModel(key = key, factory = viewModelFactory { initializer { create(app) } })
}

private val tilePalette = listOf(
    Color(0xFF0B5D4E), Color(0xFFB4532A), Color(0xFF8A5100), Color(0xFF3D5A80),
    Color(0xFF6B4E71), Color(0xFF2E7D32), Color(0xFF9C2F4A), Color(0xFF4F6D7A),
)

/** Product photo, or a colored monogram tile when the product has none. */
@Composable
fun ProductThumb(product: Product, modifier: Modifier = Modifier, shape: Shape = MaterialTheme.shapes.medium) {
    Box(modifier.clip(shape), contentAlignment = Alignment.Center) {
        val path = product.imagePath
        if (path != null && File(path).exists()) {
            AsyncImage(model = File(path), contentDescription = product.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            val color = tilePalette[abs(product.name.hashCode()) % tilePalette.size]
            Box(Modifier.fillMaxSize().background(color.copy(alpha = 0.18f)), contentAlignment = Alignment.Center) {
                Text(
                    product.name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() },
                    color = color, fontWeight = FontWeight.Bold, fontSize = 22.sp,
                )
            }
        }
    }
}

@Composable
fun EmptyState(icon: ImageVector, title: String, body: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(88.dp).clip(MaterialTheme.shapes.extraLarge).background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer) }
        Text(title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}
