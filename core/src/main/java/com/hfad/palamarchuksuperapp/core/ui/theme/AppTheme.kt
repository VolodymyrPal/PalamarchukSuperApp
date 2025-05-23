package com.hfad.palamarchuksuperapp.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (!useDarkTheme) {
        LightColors
    } else {
        DarkColors
    }
    val myRippleConfiguration =
        RippleConfiguration(
            color = colors.primary,
            rippleAlpha = RippleAlpha(
                1f,
                1f,
                1f,
                1f
            )
        )


    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,

        ) {
        ProvideTextStyle(
            value = TextStyle(color = Color.Black),
        )
        {
            CompositionLocalProvider(
                LocalRippleConfiguration provides myRippleConfiguration,
                content = content
            )
        }
    }
}


//    @Composable
//    override fun rippleAlpha(): RippleAlpha {
//        return RippleTheme.defaultRippleAlpha(
//            Color.Black,
//            lightTheme = true
//        )
//    }

@Composable
fun statusColor(
    status: Status,
): Color {
    val isDark = isSystemInDarkTheme()
    return when (status) {
        Status.CREATED -> if (!isDark) Color(0xFF42A5F5) else Color(0xFF90CAF9)
        Status.IN_PROGRESS -> if (!isDark) Color(0xFFAB47BC) else Color(0xFFCE93D8)
        Status.PROCEED -> if (!isDark) Color(0xFFFF9800) else Color(0xFFFFB74D)
        Status.DONE -> if (!isDark) Color(0xFF2E7D32) else Color(0xFF66BB6A)
        Status.CANCELED -> if (!isDark) Color(0xFFBBBBBB) else Color(0xFFE0E0E0)
        Status.OVERDUE -> if (!isDark) Color(0xFFE53935) else Color(0xFFEF5350)
    }
}

enum class Status {
    CREATED,
    IN_PROGRESS,
    PROCEED,
    DONE,
    CANCELED,
    OVERDUE
}


private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)
private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)