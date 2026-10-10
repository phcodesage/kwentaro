package com.phcodesage.kwentaro.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat

// Palengke uses opaque fills: jade bands, mango actions, paper work surfaces.
// Keep the brand colors in sync with docs/brand and res/values/colors.xml.
val Jade = Color(0xFF0B5D4E)
val DeepJade = Color(0xFF08483D)
private val JadeLight = Color(0xFF83D5BD)
val Mango = Color(0xFFF2A541)
val Paper = Color(0xFFFBF7EF)
val Ink = Color(0xFF20251F)
val Clay = Color(0xFF9C4934)

@OptIn(ExperimentalMaterial3Api::class)
private val LightColors = lightColorScheme(
    primary = Jade,
    onPrimary = Color.White,
    primaryContainer = Jade,
    onPrimaryContainer = Paper,
    inversePrimary = JadeLight,
    secondary = Color(0xFF526458),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF283D33),
    onSecondaryContainer = Paper,
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
    errorContainer = Color(0xFFA33226),
    onErrorContainer = Paper,
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
    primaryFixed = Jade,
    primaryFixedDim = DeepJade,
    onPrimaryFixed = Paper,
    onPrimaryFixedVariant = Paper,
    secondaryFixed = Ink,
    secondaryFixedDim = Color(0xFF101E19),
    onSecondaryFixed = Paper,
    onSecondaryFixedVariant = Paper,
    tertiaryFixed = Mango,
    tertiaryFixedDim = Color(0xFFD68B29),
    onTertiaryFixed = Color(0xFF2A1800),
    onTertiaryFixedVariant = Color(0xFF382000),
)

@OptIn(ExperimentalMaterial3Api::class)
private val DarkColors = darkColorScheme(
    primary = DeepJade,
    onPrimary = Paper,
    primaryContainer = Jade,
    onPrimaryContainer = Paper,
    inversePrimary = Jade,
    secondary = JadeLight,
    onSecondary = Color(0xFF243629),
    secondaryContainer = Color(0xFF123B31),
    onSecondaryContainer = Paper,
    tertiary = Mango,
    onTertiary = Color(0xFF422900),
    tertiaryContainer = Mango,
    onTertiaryContainer = Color(0xFF382000),
    background = Color(0xFF101E19),
    onBackground = Paper,
    surface = Color(0xFF101E19),
    onSurface = Paper,
    surfaceVariant = Color(0xFF283D33),
    onSurfaceVariant = Color(0xFFC3CBBE),
    surfaceTint = DeepJade,
    inverseSurface = Color(0xFFE6E6DC),
    inverseOnSurface = Color(0xFF2D332C),
    error = Color(0xFFFFB4A6),
    onError = Color(0xFF60190F),
    errorContainer = Color(0xFFA33226),
    onErrorContainer = Paper,
    outline = Color(0xFF8D9788),
    outlineVariant = Color(0xFF41493F),
    scrim = Color.Black,
    surfaceDim = Color(0xFF101E19),
    surfaceBright = Color(0xFF283D33),
    surfaceContainerLowest = Color(0xFF0B1511),
    surfaceContainerLow = Color(0xFF14271F),
    surfaceContainer = Color(0xFF192F26),
    surfaceContainerHigh = Color(0xFF20372C),
    surfaceContainerHighest = Color(0xFF283D33),
    // Fixed colors are solid brand fills in either appearance.
    primaryFixed = Jade,
    primaryFixedDim = DeepJade,
    onPrimaryFixed = Paper,
    onPrimaryFixedVariant = Paper,
    secondaryFixed = Ink,
    secondaryFixedDim = Color(0xFF101E19),
    onSecondaryFixed = Paper,
    onSecondaryFixedVariant = Paper,
    tertiaryFixed = Mango,
    tertiaryFixedDim = Color(0xFFD68B29),
    onTertiaryFixed = Color(0xFF2A1800),
    onTertiaryFixedVariant = Color(0xFF382000),
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
            val wallpaper = if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
            val brand = if (darkTheme) DarkColors else LightColors
            // Retain wallpaper neutrals while keeping solid, recognizable action colors.
            wallpaper.copy(
                primary = brand.primary, onPrimary = brand.onPrimary,
                primaryContainer = brand.primaryContainer, onPrimaryContainer = brand.onPrimaryContainer,
                secondary = brand.secondary, onSecondary = brand.onSecondary,
                secondaryContainer = brand.secondaryContainer, onSecondaryContainer = brand.onSecondaryContainer,
                tertiary = brand.tertiary, onTertiary = brand.onTertiary,
                tertiaryContainer = brand.tertiaryContainer, onTertiaryContainer = brand.onTertiaryContainer,
                errorContainer = brand.errorContainer, onErrorContainer = brand.onErrorContainer,
                surfaceTint = brand.surfaceTint,
            )
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(colorScheme = colors, typography = KwentaroTypography, shapes = KwentaroShapes, content = content)
}

private fun Context.activity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.activity()
    else -> null
}

/** Style the current window only: camera dialogs keep their own dark system bars. */
@Composable
fun SolidSystemBars(statusBackground: Color, navigationBackground: Color) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = (view.parent as? DialogWindowProvider)?.window ?: view.context.activity()?.window ?: return
    SideEffect {
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = statusBackground.luminance() > 0.179f
            isAppearanceLightNavigationBars = navigationBackground.luminance() > 0.179f
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
    }
}
