package com.hfad.palamarchuksuperapp.core.ui.composables.effects

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

/**
 * Расширение Modifier для создания эффекта мерцания (shimmer) под адаптивный дизайн
 * @param durationMillis продолжительность одного цикла анимации в миллисекундах
 */
@Composable
fun Modifier.shimmerLoading(
    durationMillis: Int = 1000,
): Modifier {
    val transition = rememberInfiniteTransition(label = "Shimmer transition")
    val shimmerFloatAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Shimmer animation",
    )

    return graphicsLayer {
        alpha = 0.95f
        clip = true
    }
        .blur(
            radius = 10.dp,
            edgeTreatment = BlurredEdgeTreatment.Unbounded
        )
        .drawWithContent {
            drawContent()

            val width = size.width
            val height = size.height
            val diagonal =
                sqrt(width * width + height * height)
            val startX =
                -diagonal + (diagonal * 2 * shimmerFloatAnimation.value)
            val startY = -diagonal + (diagonal * 2 * shimmerFloatAnimation.value)

            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.LightGray.copy(alpha = 0.10f),
                        Color.LightGray.copy(alpha = 0.7f),
                        Color.LightGray.copy(alpha = 0.10f),
                        Color.Transparent,
                    ),
                    start = Offset(
                        x = startX,
                        y = startY
                    ),
                    end = Offset(
                        x = startX + diagonal * 0.3f,
                        y = startY + diagonal * 0.3f
                    ),
                )
            )
        }
}