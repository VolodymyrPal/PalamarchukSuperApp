package com.hfad.palamarchuksuperapp.core.ui.composables.effects

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.exp
import kotlin.math.pow
import kotlin.ranges.rangeTo

@Composable
fun JumpyRow( // https://medium.com/@kappdev/how-to-create-a-jumpy-row-layout-in-jetpack-compose-45571835f4b2
    modifier: Modifier = Modifier,
    waveWidth: Dp = 200.dp,
    waveHeight: Dp = 25.dp,
    animationSpec: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(2000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    ),
    content: @Composable () -> Unit,
) {
    fun normalizeX(
        x: Float,
        originalMin: Float,
        originalMax: Float,
        targetMin: Float,
        targetMax: Float,
    ): Float {
        return targetMin + ((x - originalMin) / (originalMax - originalMin)) * (targetMax - targetMin)
    }

    fun waveCurve(x: Float): Float {
        return exp(-x.pow(2))
    }

    val infiniteTransition = rememberInfiniteTransition("Wave Transition")
    val waveProgress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = animationSpec,
        label = "Wave Progress"
    ).value
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        // Convert wave dimensions to pixels
        val waveWidthPx = waveWidth.roundToPx()
        val waveHeightPx = waveHeight.roundToPx()

        // Measure items
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        // Calculate row dimensions
        val rowWidth = placeables.sumOf { it.width }
        val maxHeight = placeables.maxOf { it.height }
        val rowHeight = maxHeight + waveHeightPx

        // Define layout
        layout(width = rowWidth, height = rowHeight) {
            // Track x position for placing items
            var xPosition = 0

            // Calculate wave effect bounds
            val totalDistance = rowWidth + waveWidthPx
            val waveStart = -waveWidthPx + (totalDistance * waveProgress)
            val waveEnd = waveStart + waveWidthPx

            // Place items
            placeables.forEach { placeable ->
                // Calculate item's center X
                val itemCenterX = xPosition + (placeable.width / 2f)
                // Calculate Y position without wave effect
                val baseYPosition = rowHeight - placeable.height

                // Apply wave effect if within bounds
                val yPosition = if (itemCenterX in waveStart..waveEnd) {
                    // Normilize x position
                    val normalizedX = normalizeX(itemCenterX, waveStart, waveEnd, -2f, 2f)
                    // Calculate wave effect
                    val waveEffect = waveCurve(normalizedX)
                    // Apply wave effect to Y position
                    (baseYPosition - waveHeightPx * waveEffect).toInt()
                } else {
                    baseYPosition
                }

                // Place the item
                placeable.place(x = xPosition, y = yPosition)
                xPosition += placeable.width
            }
        }
    }
}