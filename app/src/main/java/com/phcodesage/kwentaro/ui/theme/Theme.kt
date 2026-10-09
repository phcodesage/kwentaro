package com.phcodesage.kwentaro.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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

// "Palengke": jade anchors the shop, mango marks money moments, paper softens the surfaces.
// Keep the brand colors in sync with docs/brand and res/values/colors.xml.
private val Jade = Color(0xFF0B5D4E)
private val JadeLight = Color(0xFF83D5BD)
private val Mango = Color(0xFFF2A541)
private val Paper = Color(0xFFFBF7EF)
private val Ink = Color(0xFF20251F)

@OptIn(ExperimentalMaterial3Api::class)
private val LightColors = lightColorScheme(
    primary = Jade,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC6EBDD),
    onPrimaryContainer = Color(0xFF073B30),
    inversePrimary = JadeLight,
    secondary = Color(0xFF526458),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6E8D7),
    onSecondaryContainer = Color(0xFF23372A),
    tertiary = Color(0xFF855000),
    onTertiary = Color.White,
    tertiaryContainer = Mango,
    onTertiaryContainer = Color(0xFF382000),
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Color(0xFFEAE5D9),
    onSurfaceVariant = Color(0xFF50564D),
    surfaceTint = Jade,
    inverseSurface = Color(0xFF2D332C),
    inverseOnSurface = Color(0xFFF5F1E7),
    error = Color(0xFFA33226),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD2),
    onErrorContainer = Color(0xFF59180F),
    outline = Color(0xFF6B7267),
    outlineVariant = Color(0xFFCCC7B9),
    scrim = Color.Black,
    surfaceDim = Color(0xFFDED9CE),
    surfaceBright = Paper,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF5F1E7),
    surfaceContainer = Color(0xFFEFEADF),
    surfaceContainerHigh = Color(0xFFE9E4D8),
    surfaceContainerHighest = Color(0xFFE3DED2),
    primaryFixed = Color(0xFFC6EBDD),
    primaryFixedDim = JadeLight,
    onPrimaryFixed = Color(0xFF002117),
    onPrimaryFixedVariant = Color(0xFF174F40),
    secondaryFixed = Color(0xFFD6E8D7),
    secondaryFixedDim = Color(0xFFB9CCBA),
    onSecondaryFixed = Color(0xFF102516),
    onSecondaryFixedVariant = Color(0xFF3A5140),
    tertiaryFixed = Color(0xFFFFDDA6),
    tertiaryFixedDim = Mango,
    onTertiaryFixed = Color(0xFF2A1800),
    onTertiaryFixedVariant = Color(0xFF613C00),
)

@OptIn(ExperimentalMaterial3Api::class)
private val DarkColors = darkColorScheme(
    primary = JadeLight,
    onPrimary = Color(0xFF00382B),
    primaryContainer = Color(0xFF0A5142),
    onPrimaryContainer = Color(0xFFC6EBDD),
    inversePrimary = Jade,
    secondary = Color(0xFFB9CCBA),
    onSecondary = Color(0xFF243629),
    secondaryContainer = Color(0xFF3A5140),
    onSecondaryContainer = Color(0xFFD6E8D7),
    tertiary = Mango,
    onTertiary = Color(0xFF422900),
    tertiaryContainer = Color(0xFF634009),
    onTertiaryContainer = Color(0xFFFFDDA6),
    background = Color(0xFF141813),
    onBackground = Color(0xFFE6E6DC),
    surface = Color(0xFF141813),
    onSurface = Color(0xFFE6E6DC),
    surfaceVariant = Color(0xFF41493F),
    onSurfaceVariant = Color(0xFFC3CBBE),
    surfaceTint = JadeLight,
    inverseSurface = Color(0xFFE6E6DC),
    inverseOnSurface = Color(0xFF2D332C),
    error = Color(0xFFFFB4A6),
    onError = Color(0xFF60190F),
    errorContainer = Color(0xFF81281C),
    onErrorContainer = Color(0xFFFFDAD2),
    outline = Color(0xFF8D9788),
    outlineVariant = Color(0xFF41493F),
    scrim = Color.Black,
    surfaceDim = Color(0xFF141813),
    surfaceBright = Color(0xFF393E36),
    surfaceContainerLowest = Color(0xFF0E120D),
    surfaceContainerLow = Color(0xFF1C211A),
    surfaceContainer = Color(0xFF20251E),
    surfaceContainerHigh = Color(0xFF2A2F27),
    surfaceContainerHighest = Color(0xFF353A32),
    // Fixed roles deliberately retain their light appearance in either theme.
    primaryFixed = Color(0xFFC6EBDD),
    primaryFixedDim = JadeLight,
    onPrimaryFixed = Color(0xFF002117),
    onPrimaryFixedVariant = Color(0xFF174F40),
    secondaryFixed = Color(0xFFD6E8D7),
    secondaryFixedDim = Color(0xFFB9CCBA),
    onSecondaryFixed = Color(0xFF102516),
    onSecondaryFixedVariant = Color(0xFF3A5140),
    tertiaryFixed = Color(0xFFFFDDA6),
    tertiaryFixedDim = Mango,
    onTertiaryFixed = Color(0xFF2A1800),
    onTertiaryFixedVariant = Color(0xFF613C00),
)

private val base = Typography()
private val KwentaroTypography = Typography(
    displayLarge = base.displayLarge.copy(fontSize = 57.sp, lineHeight = 64.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-1).sp),
    displayMedium = base.displayMedium.copy(fontSize = 45.sp, lineHeight = 52.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.75).sp),
    displaySmall = base.displaySmall.copy(fontSize = 36.sp, lineHeight = 44.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.5).sp),
    headlineLarge = base.headlineLarge.copy(fontSize = 32.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
    headlineMedium = base.headlineMedium.copy(fontSize = 28.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.4).sp),
    headlineSmall = base.headlineSmall.copy(fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.25).sp),
    titleLarge = base.titleLarge.copy(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.2).sp),
    titleMedium = base.titleMedium.copy(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp),
    titleSmall = base.titleSmall.copy(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp),
    bodyLarge = base.bodyLarge.copy(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Normal, letterSpacing = 0.1.sp),
    bodyMedium = base.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal, letterSpacing = 0.15.sp),
    bodySmall = base.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Normal, letterSpacing = 0.2.sp),
    labelLarge = base.labelLarge.copy(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp),
    labelMedium = base.labelMedium.copy(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.2.sp),
    labelSmall = base.labelSmall.copy(fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp),
)

/** Merge last: typography.titleLarge.merge(MoneyStyle) keeps prices in tabular figures. */
val MoneyStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.SemiBold,
    fontFeatureSettings = "tnum",
    letterSpacing = 0.sp,
)

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
