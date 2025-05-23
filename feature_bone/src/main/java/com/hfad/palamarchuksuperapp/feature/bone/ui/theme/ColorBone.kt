package com.example.compose

import androidx.compose.ui.graphics.Color

val primaryLight = Color(0xFF000000)           // Чистый черный
val onPrimaryLight = Color(0xFFFFFFFF)         // Чистый белый
val primaryContainerLight = Color(0xFFD0D0D0)  // Более контрастный серый
val onPrimaryContainerLight = Color(0xFF000000) // Чистый черный

val secondaryLight = Color(0xFF1A1A1A)         // Почти черный
val onSecondaryLight = Color(0xFFFFFFFF)       // Чистый белый
val secondaryContainerLight = Color(0xFFF0F0F0) // Очень светлый серый
val onSecondaryContainerLight = Color(0xFF000000) // Чистый черный

val tertiaryLight = Color(0xFF333333)          // Темно-серый
val onTertiaryLight = Color(0xFFFFFFFF)        // Чистый белый
val tertiaryContainerLight = Color(0xFFE8E8E8)  // Светло-серый
val onTertiaryContainerLight = Color(0xFF000000) // Чистый черный

val errorLight = Color(0xFF000000)            // Чистый черный для ошибок
val onErrorLight = Color(0xFFFFFFFF)          // Чистый белый
val errorContainerLight = Color(0xFFD0D0D0)   // Контрастный серый
val onErrorContainerLight = Color(0xFF000000) // Чистый черный

val backgroundLight = Color(0xFFFFFFFF)       // Чистый белый фон
val onBackgroundLight = Color(0xFF000000)     // Чистый черный текст
val surfaceLight = Color(0xFFFFFFFF)          // Чистый белый
val onSurfaceLight = Color(0xFF000000)        // Чистый черный

val surfaceVariantLight = Color(0xFFF5F5F5)   // Очень светлый серый
val onSurfaceVariantLight = Color(0xFF000000) // Чистый черный
val outlineLight = Color(0xFF808080)          // Средний серый для контуров
val outlineVariantLight = Color(0xFFD0D0D0)   // Светлый серый

val scrimLight = Color(0xFF000000)            // Чистый черный
val inverseSurfaceLight = Color(0xFF000000)   // Чистый черный
val inverseOnSurfaceLight = Color(0xFFFFFFFF) // Чистый белый
val inversePrimaryLight = Color(0xFFD0D0D0)   // Контрастный серый

val surfaceDimLight = Color(0xFFF0F0F0)
val surfaceBrightLight = Color(0xFFFFFFFF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF8F8F8)
val surfaceContainerLight = Color(0xFFF0F0F0)
val surfaceContainerHighLight = Color(0xFFE8E8E8)
val surfaceContainerHighestLight = Color(0xFFD0D0D0)

val primaryDark = Color(0xFFFFFFFF)           // Чистый белый
val onPrimaryDark = Color(0xFF000000)         // Чистый черный
val primaryContainerDark = Color(0xFF2A2A2A)  // Темно-серый
val onPrimaryContainerDark = Color(0xFFFFFFFF) // Чистый белый

val secondaryDark = Color(0xFFE0E0E0)         // Светло-серый
val onSecondaryDark = Color(0xFF000000)       // Чистый черный
val secondaryContainerDark = Color(0xFF1F1F1F) // Очень темный серый
val onSecondaryContainerDark = Color(0xFFFFFFFF) // Чистый белый

val tertiaryDark = Color(0xFFC0C0C0)          // Светлый серый
val onTertiaryDark = Color(0xFF000000)        // Чистый черный
val tertiaryContainerDark = Color(0xFF2A2A2A) // Темно-серый
val onTertiaryContainerDark = Color(0xFFFFFFFF) // Чистый белый

val errorDark = Color(0xFFFFFFFF)            // Чистый белый для ошибок
val onErrorDark = Color(0xFF000000)          // Чистый черный
val errorContainerDark = Color(0xFF1F1F1F)   // Очень темный серый
val onErrorContainerDark = Color(0xFFFFFFFF) // Чистый белый

val backgroundDark = Color(0xFF000000)       // Чистый черный фон
val onBackgroundDark = Color(0xFFFFFFFF)     // Чистый белый текст
val surfaceDark = Color(0xFF000000)          // Чистый черный
val onSurfaceDark = Color(0xFFFFFFFF)        // Чистый белый

val surfaceVariantDark = Color(0xFF1A1A1A)   // Почти черный
val onSurfaceVariantDark = Color(0xFFFFFFFF) // Чистый белый
val outlineDark = Color(0xFF808080)          // Средний серый
val outlineVariantDark = Color(0xFF1A1A1A)   // Почти черный

val scrimDark = Color(0xFF000000)            // Чистый черный
val inverseSurfaceDark = Color(0xFFFFFFFF)   // Чистый белый
val inverseOnSurfaceDark = Color(0xFF000000) // Чистый черный
val inversePrimaryDark = Color(0xFF000000)   // Чистый черный

val surfaceDimDark = Color(0xFF000000)
val surfaceBrightDark = Color(0xFF1A1A1A)
val surfaceContainerLowestDark = Color(0xFF000000)
val surfaceContainerLowDark = Color(0xFF0A0A0A)
val surfaceContainerDark = Color(0xFF111111)
val surfaceContainerHighDark = Color(0xFF1A1A1A)
val surfaceContainerHighestDark = Color(0xFF2A2A2A)

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