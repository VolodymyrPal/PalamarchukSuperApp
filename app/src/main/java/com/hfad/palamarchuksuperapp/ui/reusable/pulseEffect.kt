package com.hfad.palamarchuksuperapp.ui.reusable

import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.scale

@Composable
fun Modifier.pulseEffect(
    targetScale: Float = 0.6f,
    initialScale: Float = 0.3f,
    color: Brush = SolidColor(Color.Black.copy(0.32f)),
    shape: Shape = CircleShape,
    animationSpec: DurationBasedAnimationSpec<Float> = tween(1200),
): Modifier {
    val pulseTransition = rememberInfiniteTransition("PulseTransition")

    val pulseScale by pulseTransition.animateFloat(
        initialValue = initialScale,
        targetValue = targetScale,
        animationSpec = infiniteRepeatable(animationSpec),
        label = "Pulse Scale"
    )

    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(animationSpec),
        label = "PulseAlpha"
    )

    return this.drawBehind {
        val outline = shape.createOutline(size, layoutDirection, this)
        scale(pulseScale) {
            drawOutline(outline, color, pulseAlpha)
        }
    }
}

@Composable
fun Modifier.doublePulseEffect(
    targetScale: Float = 1f,
    initialScale: Float = 0.5f,
    color: Brush = SolidColor(Color.Black.copy(0.32f)),
    shape: Shape = CircleShape,
): Modifier {

    return this
        .pulseEffect(
            targetScale = targetScale,
            initialScale = initialScale,
            color = color,
            shape = shape,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
        .pulseEffect(
            targetScale = targetScale,
            initialScale = initialScale,
            color = color,
            shape = shape,
            animationSpec = tween(
                durationMillis = (800*0.7).toInt(),
                delayMillis = (800*0.3).toInt(),
                easing = LinearEasing
            )
        )
}