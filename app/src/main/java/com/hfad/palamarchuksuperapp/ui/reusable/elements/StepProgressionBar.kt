package com.hfad.palamarchuksuperapp.ui.reusable.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.hfad.palamarchuksuperapp.ui.viewModels.OrderService
import com.hfad.palamarchuksuperapp.ui.viewModels.Stepper

@Composable
fun StepProgressionBar(
    modifier: Modifier = Modifier,
    listOfSteps: List<Stepper>,
    currentStep: Int = 0,
) {

    val stepperHeight = 36.dp
    val painter = rememberVectorPainter(Icons.Default.Check)

    Canvas(
        modifier = modifier
            .width(250.dp)
            .height(stepperHeight)
    ) {
        val widthPx = size.width
        val stepRadius = 10.dp.toPx()
        val centerY = size.height / 2

        val stepSpacing = if (listOfSteps.size > 1) {
            (widthPx - 2 * stepRadius) / (listOfSteps.size - 1)
        } else {
            0f
        }

        listOfSteps.forEachIndexed { index, step ->

            val stepX = stepRadius + index * stepSpacing
            val isCompleted = index < currentStep
            val isCurrent = index == currentStep

            val outerColor =
                if (isCompleted || isCurrent) Color(0xFF2E7D32) else Color(0xFFBDBDBD)
            val innerColor = when {
                isCompleted -> Color(0xFF2E7D32)
                isCurrent -> Color(0xFFFFD54F).copy(red = 0.7f, blue = 0.85f)
                else -> Color(0xFFF5F5F5)
            }

            val strokeWidth = 2.dp.toPx()

            drawCircle(
                color = outerColor,
                radius = stepRadius - strokeWidth / 2,
                center = Offset(stepX, centerY),
                style = Stroke(width = strokeWidth)
            )

            drawCircle(
                color = innerColor,
                radius = stepRadius - strokeWidth,
                center = Offset(stepX, centerY),
            )

            if (index < listOfSteps.size - 1) {
                val prevX = stepX + stepRadius
                val newX = prevX + stepSpacing - stepRadius * 2
                drawLine(
                    color = Color.Black,
                    start = Offset(prevX, centerY),
                    end = Offset(newX, centerY),
                    strokeWidth = (1.5).dp.toPx()
                )
            }
            drawIntoCanvas { canvas ->
                if (isCompleted) {
                    with(painter) {
                        draw(
                            Size(40f, 40f),
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun StepProgressionBarNewPreview(

) {
    AppTheme {
        Box(modifier = Modifier.size(250.dp, 50.dp)) {
            StepProgressionBar(
                listOfSteps = listOf(
                    OrderService(isComplete = true),
                    OrderService(),
                    OrderService(isComplete = true),
                    OrderService(),
                    OrderService(),
                    OrderService(),
                    OrderService(),
                    OrderService(),
//                    OrderService(),
//                    OrderService(),
//                    OrderService(),
//                    OrderService(),
//                    OrderService(),
//                    OrderService(),
                ),
                currentStep = 5
            )
        }
    }
}