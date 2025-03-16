package com.hfad.palamarchuksuperapp.ui.reusable.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.R

@Composable
fun AppIconInfoField(
    modifier: Modifier = Modifier,
    icon: Painter,
    text: String,
) {
    Box(
        modifier = modifier
            .sizeIn(maxWidth = 130.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = "Icon",
                modifier = Modifier.size(24.dp)
            )
            Column(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
            ) {
                AppText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    value = "Title: Price",
                    color = defaultAppTextColor().copy(alpha = 0.5f),
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.labelSmall
                    )
                )
                AppText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    value = "Some information about the item",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.labelMedium,
                        fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                    )
                )
            }
        }
    }
}


@Preview
@Composable
fun IconInfoFieldPreview() {
    val iconPainter = painterResource(R.drawable.d_letter)
    AppIconInfoField(
        icon = iconPainter,
        text = "Text"
    )
}