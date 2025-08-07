package com.hfad.palamarchuksuperapp.feature.bone.ui.animation

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.animatedScaleIn(
    durationMillis: Int = 300,
    delayMillis: Int = 25,
    easing: Easing = FastOutSlowInEasing,
    fromScale: Float = 0.7f,
    toScale: Float = 1f,
): Modifier = composed {
    var started by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (started) toScale else fromScale,
        animationSpec = tween(
            durationMillis = durationMillis,
            delayMillis = delayMillis,
            easing = easing
        ),
        label = "scale_in"
    )

    LaunchedEffect(Unit) {
        started = true
    }

    this.graphicsLayer(scaleX = scale, scaleY = scale)
}