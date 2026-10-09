package com.phcodesage.kwentaro.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// "Palengke" palette: deep jade for trust, ripe-mango amber for money moments, warm paper neutrals.
private val Jade = Color(0xFF0B5D4E)
private val JadeLight = Color(0xFF7DD8C0)
private val Mango = Color(0xFFF2A541)
private val Clay = Color(0xFFB4532A)
private val Paper = Color(0xFFF8F4EC)
private val Ink = Color(0xFF1B1D1A)

private val LightColors = lightColorScheme(
    primary = Jade,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA6F2DB),
    onPrimaryContainer = Color(0xFF00201A),
    secondary = Color(0xFF4A635C),
    secondaryContainer = Color(0xFFCCE8DF),
    onSecondaryContainer = Color(0xFF06201A),
    tertiary = Color(0xFF8A5100),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDDB8),
    onTertiaryContainer = Color(0xFF2C1600),
    error = Clay,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE6E0D4),
    onSurfaceVariant = Color(0xFF4A4740),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF3EEE4),
    surfaceContainer = Color(0xFFEDE7DC),
    surfaceContainerHigh = Color(0xFFE7E1D5),
    surfaceContainerHighest = Color(0xFFE1DBCF),
    outline = Color(0xFF7B776D),
    outlineVariant = Color(0xFFCCC6B9),
)

private val DarkColors = darkColorScheme(
    primary = JadeLight,
    onPrimary = Color(0xFF00382D),
    primaryContainer = Color(0xFF005142),
    onPrimaryContainer = Color(0xFFA6F2DB),
    secondary = Color(0xFFB1CCC3),
    secondaryContainer = Color(0xFF334B45),
    onSecondaryContainer = Color(0xFFCCE8DF),
    tertiary = Mango,
    onTertiary = Color(0xFF4A2800),
    tertiaryContainer = Color(0xFF6A3C00),
    onTertiaryContainer = Color(0xFFFFDDB8),
    error = Color(0xFFFFB59A),
    background = Color(0xFF121512),
    onBackground = Color(0xFFE3E3DC),
    surface = Color(0xFF121512),
    onSurface = Color(0xFFE3E3DC),
    surfaceVariant = Color(0xFF3F4945),
    onSurfaceVariant = Color(0xFFBFC9C3),
    surfaceContainerLowest = Color(0xFF0C0F0D),
    surfaceContainerLow = Color(0xFF1A1D1A),
    surfaceContainer = Color(0xFF1E211E),
    surfaceContainerHigh = Color(0xFF282B28),
    surfaceContainerHighest = Color(0xFF333633),
    outline = Color(0xFF89938E),
    outlineVariant = Color(0xFF3F4945),
)

private val base = Typography()
private val KwentaroTypography = Typography(
    displaySmall = base.displaySmall.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
    headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.25).sp),
    headlineSmall = base.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
    titleLarge = base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = base.labelLarge.copy(fontWeight = FontWeight.SemiBold),
)

/** Tabular figures so prices line up in columns. */
val MoneyStyle = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)

private val KwentaroShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun KwentaroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(colorScheme = colors, typography = KwentaroTypography, shapes = KwentaroShapes, content = content)
}
