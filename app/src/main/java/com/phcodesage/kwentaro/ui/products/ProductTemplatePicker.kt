package com.phcodesage.kwentaro.ui.products

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.phcodesage.kwentaro.data.ProductTemplate
import com.phcodesage.kwentaro.data.ProductTemplates

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductTemplatePicker(
    productName: String,
    selectedKey: String?,
    onSelected: (ProductTemplate) -> Unit,
    onDismiss: () -> Unit,
) {
    var search by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf<String?>(null) }
    val categories = remember { ProductTemplates.all.map { it.category }.distinct() }
    val suggestions = remember(productName) { ProductTemplates.suggest(productName).take(6) }
    val templates = remember(search, category) {
        val matches = if (search.isBlank()) ProductTemplates.all else ProductTemplates.suggest(search)
        matches.filter { category == null || it.category == category }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        // Headers scroll with the tiles so the keyboard/large text never hides the grid.
        LazyVerticalGrid(
            columns = GridCells.Adaptive(96.dp),
            modifier = Modifier.fillMaxWidth().imePadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(key = "picker-title", span = { GridItemSpan(maxLineSpan) }) {
                Text("Choose image", style = MaterialTheme.typography.titleLarge)
            }
            item(key = "picker-search", span = { GridItemSpan(maxLineSpan) }) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Search images") },
                    leadingIcon = { Icon(Icons.Rounded.Search, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item(key = "picker-categories", span = { GridItemSpan(maxLineSpan) }) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(selected = category == null, onClick = { category = null }, label = { Text("All") })
                    }
                    items(categories, key = { it }) { name ->
                        FilterChip(selected = category == name, onClick = { category = name }, label = { Text(name) })
                    }
                }
            }
            if (suggestions.isNotEmpty() && search.isBlank()) {
                item(key = "picker-suggestions", span = { GridItemSpan(maxLineSpan) }) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Suggested", style = MaterialTheme.typography.titleSmall)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(suggestions, key = { it.key }) { template ->
                                TemplateTile(template, selectedKey, onSelected, Modifier.width(104.dp))
                            }
                        }
                    }
                }
            }
            item(key = "picker-results", span = { GridItemSpan(maxLineSpan) }) {
                Text(category ?: "All images", style = MaterialTheme.typography.titleSmall)
            }
            if (templates.isEmpty()) {
                item(key = "picker-empty", span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        "No images found. Try another name or category.",
                        modifier = Modifier.padding(vertical = 24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(templates, key = { it.key }) { template ->
                    TemplateTile(template, selectedKey, onSelected)
                }
            }
        }
    }
}

@Composable
private fun TemplateTile(
    template: ProductTemplate,
    selectedKey: String?,
    onSelected: (ProductTemplate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = selectedKey == template.key
    Surface(
        onClick = { onSelected(template) },
        modifier = modifier.semantics { selected = isSelected },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
    ) {
        Column(
            Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Image(painterResource(template.res), contentDescription = null, modifier = Modifier.size(56.dp))
            Text(
                template.label,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.heightIn(min = 32.dp),
            )
        }
    }
}
