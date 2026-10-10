package com.phcodesage.kwentaro.ui.theme

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance

/** Dark jade is a fill; light jade is readable text on the dark work surfaces. */
val actionTextColor
    @Composable get() = MaterialTheme.colorScheme.let {
        if (it.surface.luminance() < 0.179f) it.secondary else it.primary
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun solidTopAppBarColors() = MaterialTheme.colorScheme.let {
    TopAppBarDefaults.topAppBarColors(
        containerColor = it.primary,
        scrolledContainerColor = it.primary,
        navigationIconContentColor = it.onPrimary,
        titleContentColor = it.onPrimary,
        actionIconContentColor = it.onPrimary,
    )
}

@Composable
fun SolidFilterChip(selected: Boolean, onClick: () -> Unit, label: @Composable () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    FilterChip(
        selected = selected, onClick = onClick, label = label, modifier = modifier,
        border = null,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = colors.surfaceContainerHighest,
            labelColor = colors.onSurface,
            selectedContainerColor = colors.tertiaryContainer,
            selectedLabelColor = colors.onTertiaryContainer,
            selectedLeadingIconColor = colors.onTertiaryContainer,
            disabledContainerColor = colors.surfaceContainerHighest,
            disabledLabelColor = colors.onSurfaceVariant,
            disabledSelectedContainerColor = colors.surfaceContainerHighest,
        ),
    )
}

@Composable
fun solidButtonColors() = MaterialTheme.colorScheme.let {
    ButtonDefaults.buttonColors(
        containerColor = it.primary, contentColor = it.onPrimary,
        disabledContainerColor = it.surfaceContainerHighest, disabledContentColor = it.onSurfaceVariant,
    )
}

@Composable
fun solidTextButtonColors() = ButtonDefaults.textButtonColors(contentColor = actionTextColor)

@Composable
fun solidTonalIconButtonColors() = MaterialTheme.colorScheme.let {
    IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = it.secondaryContainer, contentColor = it.onSecondaryContainer,
        disabledContainerColor = it.surfaceContainerHighest, disabledContentColor = it.onSurfaceVariant,
    )
}

@Composable
fun solidTextFieldColors() = MaterialTheme.colorScheme.let {
    OutlinedTextFieldDefaults.colors(
        focusedContainerColor = it.surfaceContainerLowest,
        unfocusedContainerColor = it.surfaceContainerLowest,
        disabledContainerColor = it.surfaceContainerHighest,
        errorContainerColor = it.surfaceContainerLowest,
        focusedTextColor = it.onSurface,
        unfocusedTextColor = it.onSurface,
        focusedBorderColor = actionTextColor,
        focusedLabelColor = actionTextColor,
        cursorColor = actionTextColor,
    )
}
