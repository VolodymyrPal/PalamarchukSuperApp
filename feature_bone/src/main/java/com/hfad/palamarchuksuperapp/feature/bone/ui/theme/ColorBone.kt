package com.example.compose

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
val primaryLight = Color(0xFF000000)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFD0D0D0)
val onPrimaryContainerLight = Color(0xFF000000)

val secondaryLight = Color(0xFF1A1A1A)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFF0F0F0)
val onSecondaryContainerLight = Color(0xFF000000)

val tertiaryLight = Color(0xFF333333)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFE8E8E8)
val onTertiaryContainerLight = Color(0xFF000000)

val errorLight = Color(0xFF000000)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFD0D0D0)
val onErrorContainerLight = Color(0xFF000000)

val backgroundLight = Color(0xFFFFFFFF)
val onBackgroundLight = Color(0xFF000000)
val surfaceLight = Color(0xFFFFFFFF)
val onSurfaceLight = Color(0xFF000000)

val surfaceVariantLight = Color(0xFFF5F5F5)
val onSurfaceVariantLight = Color(0xFF000000)
val outlineLight = Color(0xFF808080)
val outlineVariantLight = Color(0xFFD0D0D0)

val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF000000)
val inverseOnSurfaceLight = Color(0xFFFFFFFF)
val inversePrimaryLight = Color(0xFFD0D0D0)

val surfaceDimLight = Color(0xFFF0F0F0)
val surfaceBrightLight = Color(0xFFFFFFFF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF8F8F8)
val surfaceContainerLight = Color(0xFFF0F0F0)
val surfaceContainerHighLight = Color(0xFFE8E8E8)
val surfaceContainerHighestLight = Color(0xFFD0D0D0)

// Обновленная Dark theme
val primaryDark = Color(0xFFFFFFFF)
val onPrimaryDark = Color(0xFF000000)
val primaryContainerDark = Color(0xFF000000)
val onPrimaryContainerDark = Color(0xFFFFFFFF)

val secondaryDark = Color(0xFFFFFFFF)
val onSecondaryDark = Color(0xFF000000)
val secondaryContainerDark = Color(0xFF000000)
val onSecondaryContainerDark = Color(0xFFFFFFFF)

val tertiaryDark = Color(0xFFFFFFFF)
val onTertiaryDark = Color(0xFF000000)
val tertiaryContainerDark = Color(0xFF000000)
val onTertiaryContainerDark = Color(0xFFFFFFFF)

val errorDark = Color(0xFFFFFFFF)
val onErrorDark = Color(0xFF000000)
val errorContainerDark = Color(0xFF000000)
val onErrorContainerDark = Color(0xFFFFFFFF)

val backgroundDark = Color(0xFF000000)     // Общий фон остается черным
val onBackgroundDark = Color(0xFFFFFFFF)
val surfaceDark = Color(0xFFFFFFFF)        // Основная поверхность (карточки) белая
val onSurfaceDark = Color(0xFF000000)      // Текст на карточках черный

val surfaceVariantDark = Color(0xFFF5F5F5) // Вариант поверхности (светло-серый)
val onSurfaceVariantDark = Color(0xFF000000)
val outlineDark = Color(0xFFCCCCCC)        // Более светлый контур
val outlineVariantDark = Color(0xFF444444)

// Градации поверхностей для карточек
val surfaceDimDark = Color(0xFF000000)
val surfaceBrightDark = Color(0xFFFFFFFF)
val surfaceContainerLowestDark = Color(0xFFFFFFFF)
val surfaceContainerLowDark = Color(0xFFF8F8F8)
val surfaceContainerDark = Color(0xFFF0F0F0)
val surfaceContainerHighDark = Color(0xFFEAEAEA)
val surfaceContainerHighestDark = Color(0xFFE0E0E0)

// Остальные параметры без изменений
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFFFFFFF)
val inverseOnSurfaceDark = Color(0xFF000000)
val inversePrimaryDark = Color(0xFF000000)

val accentLight = Color(0xFF000000)
val onAccentLight = Color(0xFFFFFFFF)
val accentContainerLight = Color(0xFFE0E0E0)
val onAccentContainerLight = Color(0xFF000000)

val accentDark = Color(0xFFFFFFFF)
val onAccentDark = Color(0xFF000000)
val accentContainerDark = Color(0xFF333333)
val onAccentContainerDark = Color(0xFFFFFFFF)

@Composable
fun ComprehensiveColorPreview() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Color Scheme Testing",
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Primary Colors
        item {
            ColorGroupSection(
                title = "Primary Colors",
                colors = listOf(
                    ColorItem("Primary", colorScheme.primary, colorScheme.onPrimary),
                    ColorItem("Primary Container", colorScheme.primaryContainer, colorScheme.onPrimaryContainer),
                    ColorItem("Inverse Primary", colorScheme.inversePrimary, colorScheme.surface)
                )
            )
        }

        // Interactive Elements Demo
        item {
            InteractiveElementsDemo(colorScheme)
        }

        // Surface Colors
        item {
            ColorGroupSection(
                title = "Surface Colors",
                colors = listOf(
                    ColorItem("Surface", colorScheme.surface, colorScheme.onSurface),
                    ColorItem("Surface Variant", colorScheme.surfaceVariant, colorScheme.onSurfaceVariant),
                    ColorItem("Surface Dim", colorScheme.surfaceDim, colorScheme.onSurface),
                    ColorItem("Surface Bright", colorScheme.surfaceBright, colorScheme.onSurface)
                )
            )
        }

        // Surface Containers
        item {
            ColorGroupSection(
                title = "Surface Containers",
                colors = listOf(
                    ColorItem("Container Lowest", colorScheme.surfaceContainerLowest, colorScheme.onSurface),
                    ColorItem("Container Low", colorScheme.surfaceContainerLow, colorScheme.onSurface),
                    ColorItem("Container", colorScheme.surfaceContainer, colorScheme.onSurface),
                    ColorItem("Container High", colorScheme.surfaceContainerHigh, colorScheme.onSurface),
                    ColorItem("Container Highest", colorScheme.surfaceContainerHighest, colorScheme.onSurface)
                )
            )
        }

        // Background and Inverse
        item {
            ColorGroupSection(
                title = "Background & Inverse",
                colors = listOf(
                    ColorItem("Background", colorScheme.background, colorScheme.onBackground),
                    ColorItem("Inverse Surface", colorScheme.inverseSurface, colorScheme.inverseOnSurface)
                )
            )
        }

        // Outline and Scrim
        item {
            OutlineSection(colorScheme)
        }

        // Secondary Colors
        item {
            ColorGroupSection(
                title = "Secondary Colors",
                colors = listOf(
                    ColorItem("Secondary", colorScheme.secondary, colorScheme.onSecondary),
                    ColorItem("Secondary Container", colorScheme.secondaryContainer, colorScheme.onSecondaryContainer)
                )
            )
        }

        // Tertiary Colors
        item {
            ColorGroupSection(
                title = "Tertiary Colors",
                colors = listOf(
                    ColorItem("Tertiary", colorScheme.tertiary, colorScheme.onTertiary),
                    ColorItem("Tertiary Container", colorScheme.tertiaryContainer, colorScheme.onTertiaryContainer)
                )
            )
        }

        // Error Colors
        item {
            ColorGroupSection(
                title = "Error Colors",
                colors = listOf(
                    ColorItem("Error", colorScheme.error, colorScheme.onError),
                    ColorItem("Error Container", colorScheme.errorContainer, colorScheme.onErrorContainer)
                )
            )
        }

        // Surface Colors
        item {
            ColorGroupSection(
                title = "Surface Colors",
                colors = listOf(
                    ColorItem("Surface", colorScheme.surface, colorScheme.onSurface),
                    ColorItem("Surface Variant", colorScheme.surfaceVariant, colorScheme.onSurfaceVariant),
                    ColorItem("Surface Dim", colorScheme.surfaceDim, colorScheme.onSurface),
                    ColorItem("Surface Bright", colorScheme.surfaceBright, colorScheme.onSurface)
                )
            )
        }

        // Surface Containers
        item {
            ColorGroupSection(
                title = "Surface Containers",
                colors = listOf(
                    ColorItem("Container Lowest", colorScheme.surfaceContainerLowest, colorScheme.onSurface),
                    ColorItem("Container Low", colorScheme.surfaceContainerLow, colorScheme.onSurface),
                    ColorItem("Container", colorScheme.surfaceContainer, colorScheme.onSurface),
                    ColorItem("Container High", colorScheme.surfaceContainerHigh, colorScheme.onSurface),
                    ColorItem("Container Highest", colorScheme.surfaceContainerHighest, colorScheme.onSurface)
                )
            )
        }

        // Background and Inverse
        item {
            ColorGroupSection(
                title = "Background & Inverse",
                colors = listOf(
                    ColorItem("Background", colorScheme.background, colorScheme.onBackground),
                    ColorItem("Inverse Surface", colorScheme.inverseSurface, colorScheme.inverseOnSurface)
                )
            )
        }

        // Outline and Scrim
        item {
            OutlineSection(colorScheme)
        }

        // Interactive Elements Demo
        item {
            InteractiveElementsDemo(colorScheme)
        }
    }
}

@Composable
fun ColorGroupSection(
    title: String,
    colors: List<ColorItem>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(colors) { colorItem ->
                ColorSwatch(colorItem)
            }
        }
    }
}

@Composable
fun ColorSwatch(colorItem: ColorItem) {
    Surface(
        color = colorItem.backgroundColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(120.dp)
            .height(80.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = colorItem.name,
                color = colorItem.contentColor,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "#${String.format("%06X", colorItem.backgroundColor.toArgb() and 0xFFFFFF)}",
                color = colorItem.contentColor.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun OutlineSection(colorScheme: ColorScheme) {
    Column {
        Text(
            text = "Outlines & Scrim",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Outline examples
            Surface(
                color = colorScheme.surface,
                border = BorderStroke(2.dp, colorScheme.outline),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Outline",
                    color = colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }

            Surface(
                color = colorScheme.surface,
                border = BorderStroke(2.dp, colorScheme.outlineVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Outline Variant",
                    color = colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Scrim demonstration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(top = 8.dp)
        ) {
            Surface(
                color = colorScheme.primary,
                modifier = Modifier.fillMaxSize()
            ) {}

            Surface(
                color = colorScheme.scrim.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Scrim Overlay Example",
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun InteractiveElementsDemo(colorScheme: ColorScheme) {
    Column {
        Text(
            text = "Interactive Elements",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Button examples
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Text("Primary")
            }

            OutlinedButton(
                onClick = { },
                border = BorderStroke(1.dp, colorScheme.outline)
            ) {
                Text("Outlined", color = colorScheme.primary)
            }

            TextButton(onClick = { }) {
                Text("Text", color = colorScheme.primary)
            }
        }

        // Card example
        Card(
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceContainer,
                contentColor = colorScheme.onSurface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "This is a card using surfaceContainer background",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

data class ColorItem(
    val name: String,
    val backgroundColor: Color,
    val contentColor: Color
)

@Preview(name = "Complete Color Scheme Light", showBackground = true)
@Composable
fun CompleteColorSchemeLightPreview() {
    FeatureTheme(darkTheme = false) {
        ComprehensiveColorPreview()
    }
}

@Preview(name = "Complete Color Scheme Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun CompleteColorSchemeDarkPreview() {
    FeatureTheme(darkTheme = true) {
        ComprehensiveColorPreview()
    }
}