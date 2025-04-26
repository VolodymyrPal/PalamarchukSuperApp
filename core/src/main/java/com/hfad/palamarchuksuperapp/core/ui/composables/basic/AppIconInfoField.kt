package com.hfad.palamarchuksuperapp.core.ui.composables.basic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme

@Suppress("LongParameterList")
@Composable
fun AppIconInfoField(
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    title: String = "",
    description: String = "",
    iconSize: Dp = 24.dp,
    iconPadding: Dp = 6.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
    elevation: Dp = 2.dp,
    onClick: (() -> Unit)? = null,
    cardColors : CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
) {
    val boxModifier = if (onClick != null) {
        modifier
            .sizeIn(maxWidth = appIconInfoFieldWidth.dp)
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min)
            .clickable(onClick = onClick)
    } else {
        modifier
            .sizeIn(maxWidth = appIconInfoFieldWidth.dp)
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min)
    }
    Box(
        modifier = boxModifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            colors = cardColors,
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .padding(contentPadding)
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(iconPadding)
            ) {
                if (icon != null) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(vertical = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (title.isNotBlank()) AppText(
                        value = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.7f)
                            .width(IntrinsicSize.Max),
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2,
                            textAlign = TextAlign.Unspecified,
                        ),
                    )

                    if (description.isNotBlank()) AppText(
                        value = description,
                        modifier = Modifier
                            .fillMaxWidth()
                            .width(IntrinsicSize.Max),
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.labelLarge,
                            fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Unspecified,
                        ),
                    )
                }
            }
        }
    }
}

const val appIconInfoFieldWidth = 150

@Preview
@Composable
fun IconInfoFieldPreview() {
    Column {
        AppTheme {
            val iconPainter = rememberVectorPainter(Icons.Default.Info)
            Column {
                AppIconInfoField(
                    modifier = Modifier.padding(4.dp),
                    icon = iconPainter,
                    description = "Description",
                    title = "123"
                )
                AppIconInfoField(
                    modifier = Modifier.padding(4.dp),
                    description = "Description",
                    title = "123",
                )

            }
        }
        AppTheme(
            useDarkTheme = true
        ) {
            val iconPainter = rememberVectorPainter(Icons.Default.Info)
            Column {
                AppIconInfoField(
                    modifier = Modifier.padding(4.dp),
                    icon = iconPainter,
                    description = "Description",
                    title = "123"
                )
                AppIconInfoField(
                    modifier = Modifier.padding(4.dp),
                    description = "Description",
                    title = "123",
                )
            }
        }
    }
}