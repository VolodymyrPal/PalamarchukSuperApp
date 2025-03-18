package com.hfad.palamarchuksuperapp.ui.reusable.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.R

@Suppress("LongParameterList")
@Composable
fun AppIconInfoField(
    modifier: Modifier = Modifier,
    icon: Painter?,
    title: String = "Title",
    description: String = "Description",
    iconSize: Dp = 24.dp,
    iconPadding: Dp = 6.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
    elevation: Dp = 2.dp,
    onClick: (() -> Unit)? = null,
) {
    val cardModifier = if (onClick != null) {
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

    Card(
        modifier = cardModifier,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(iconPadding)
        ) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                        .padding(end = 4.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AppText(
                    value = title,
                    modifier = Modifier
                        .alpha(0.5f)
                        .width(IntrinsicSize.Min),
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    ),
                )

                AppText(
                    value = description,
                    modifier = Modifier.width(250.dp),
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.labelMedium,
                        fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    ),
                )
            }
        }
    }
}

const val appIconInfoFieldWidth = 150


@Preview
@Composable
fun IconInfoFieldPreview() {
    val iconPainter = painterResource(R.drawable.d_letter)
    AppIconInfoField(
        icon = iconPainter,
        description = "Description",
        title = "123"
    )
}