package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ToggleableArrow(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    onToggle: () -> Unit,
) {

    val progress by animateFloatAsState(
        targetValue = if (isOpen) 0.05f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(24.dp)
            .clickable(
                onClick = onToggle,
                indication = null,
                interactionSource = interactionSource
            ),
        contentAlignment = Alignment.Center
    ) {
        val colorScheme = MaterialTheme.colorScheme
        Canvas(modifier = Modifier.fillMaxSize(0.85f)) {
            val width = size.width
            val height = size.height

            val centerX = width / 2f
            val topY = height * progress   // slide top
            val baseY = height * 0.7f

            val wingOffsetX = width * 0.4f
            val wingY = baseY - height * 0.15f

            // Левая линия
            drawLine(
                color = colorScheme.primary,
                start = Offset(centerX, topY),
                end = Offset(
                    centerX - wingOffsetX,
                    wingY
                ),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
            // Правая линия
            drawLine(
                color = colorScheme.primary,
                start = Offset(centerX, topY),
                end = Offset(
                    centerX + wingOffsetX,
                    wingY
                ),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview
@Composable
fun ToggleableArrowPreview() {
    val isOpen = remember { mutableStateOf(false) }
    ToggleableArrow(
        modifier = Modifier.size(86.dp),
        isOpen = isOpen.value,
        onToggle = {
            isOpen.value = !isOpen.value
        }
    )
}