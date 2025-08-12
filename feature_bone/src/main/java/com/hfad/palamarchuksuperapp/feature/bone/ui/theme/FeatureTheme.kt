package com.example.compose

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AppTypography
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType

//import com.hfad.palamarchuksuperapp.core.ui.theme.*

@Composable
fun FeatureTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    customTheme: Int = 0,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        customTheme != 0 -> when (customTheme) {
            else -> {
                ColorScheme(
                    primary = Color(0xFF00D4AA),
                    onPrimary = Color(0xFF000000),
                    primaryContainer = Color(0xFF005B4F),
                    onPrimaryContainer = Color(0xFFFFFFFF),
                    inversePrimary = Color(0xFF7FFFD4),

                    secondary = Color(0xFF3A3D47),
                    onSecondary = Color(0xFFFFFFFF),
                    secondaryContainer = Color(0xFF2A2D35),
                    onSecondaryContainer = Color(0xFFE0E0E0),

                    tertiary = Color(0xFFFF6B6B),
                    onTertiary = Color(0xFFFFFFFF),
                    tertiaryContainer = Color(0xFF4A2C2C),
                    onTertiaryContainer = Color(0xFFFFDADA),

                    error = Color(0xFFFF4757),
                    onError = Color(0xFFFFFFFF),
                    errorContainer = Color(0xFF5D2A2A),
                    onErrorContainer = Color(0xFFFFDADA),

                    background = Color(0xFF1A1D29),
                    onBackground = Color(0xFFE8E8E8),
                    surface = Color(0xFF282F3D),
                    onSurface = Color(0xFFE8E8E8),
                    surfaceVariant = Color(0xFF313B4D),
                    onSurfaceVariant = Color(0xFFB0B0B0),
                    surfaceTint = Color(0xFF00D4AA),
                    inverseSurface = Color(0xFFE8E8E8),
                    inverseOnSurface = Color(0xFF1A1D29),

                    outline = Color(0xFF404354),
                    outlineVariant = Color(0xFF4A4D5F),
                    scrim = Color(0x99000000),

                    surfaceContainerLowest = Color(0xFF1A1D29),  // Совпадает с background - самый темный
                    surfaceContainerLow = Color(0xFF1F2634),     // Чуть светлее фона с легким бирюзовым
                    surfaceContainer = Color(0xFF282F3D),        // Базовый уровень карточек с бирюзовым, совпадает с surface
                    surfaceContainerHigh = Color(0xFF313B4D),    // Выше базового с бирюзовым подтоном
                    surfaceContainerHighest = Color(0xFF3A4759), // Самый "поднятый" элемент с бирюзовым
                    surfaceDim = Color(0xFF151822),              // Для затемненных эффектов - темнее фона
                    surfaceBright = Color(0xFF435466),
                )
            }
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = shapes,
        content = content
    )
}

val shapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp), // For card
    small = RoundedCornerShape(2.dp), // For button
    medium = RoundedCornerShape(4.dp), //
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun financeStatusColor(
    status: TransactionType,
): Color {
    val isDark = isSystemInDarkTheme()
    return when (status) {
        TransactionType.CREDIT -> Color(0xFF81C784)//if (!isDark) Color(0xFFE6FFEC) else Color(0xFFC8E6C9)
        TransactionType.DEBIT -> Color(0xFF90A4AE) //if (!isDark) Color(0xFFFBE6E7) else Color(0xFFFFCDD2)
    }
}

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)


@Preview(name = "Complete Color Scheme Light", showBackground = true)
@Composable
fun CompleteColorSchemeLightPreview2() {
    FeatureTheme(darkTheme = false) {
        ComprehensiveColorPreview()
    }
}

@Preview(
    name = "Complete Color Scheme Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun CompleteColorSchemeDarkPreview2() {
    FeatureTheme(darkTheme = true) {
        ComprehensiveColorPreview()
    }
}