package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Modifier.fadingEdge(
    brush: Brush = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.05f to Color.Transparent,
            0.5f to Color.Black,
            0.95f to Color.Transparent,
            1f to Color.Transparent
        )
    },
) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(
            brush = brush, blendMode = BlendMode.DstIn
        )
    }