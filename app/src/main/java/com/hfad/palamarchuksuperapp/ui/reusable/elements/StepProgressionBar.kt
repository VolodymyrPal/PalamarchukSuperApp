package com.hfad.palamarchuksuperapp.ui.reusable.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.AppTheme
import com.hfad.palamarchuksuperapp.ui.compose.boneScreen.BoneScreen
import com.hfad.palamarchuksuperapp.ui.compose.boneScreen.painterServiceTypeMap
import com.hfad.palamarchuksuperapp.ui.viewModels.Stepper
import com.hfad.palamarchuksuperapp.ui.viewModels.StepperStatus
import com.hfad.palamarchuksuperapp.ui.viewModels.orderServiceList

@Composable
fun StepProgressionBar(
    modifier: Modifier = Modifier,
    listOfSteps: List<Stepper>,
    currentStep: Int = 0,
    circleScale: Float = 0.6f,
) {

    var componentSize by remember { mutableStateOf(Size.Zero) }

    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary
    val primaryContainerColor = colorScheme.primaryContainer
    val onPrimaryContainerColor = colorScheme.onPrimaryContainer
    val surfaceColor = colorScheme.surface

    // Кэш иконок
    val painterServiceTypeMap = painterServiceTypeMap()
    val painterStatusDone = rememberVectorPainter(image = Icons.Default.Check)


    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 20.dp, minWidth = 100.dp) // Увеличиваем базовую высоту
            .onSizeChanged { size ->
                componentSize = Size(size.width.toFloat(), size.height.toFloat())
            }
    ) {
        val maxNumSteps = listOfSteps.size

        // Текстовые настройки
        val textMeasurer = rememberTextMeasurer()
        val textStyle = MaterialTheme.typography.bodySmall.copy(
            fontSize = 8.sp,
            letterSpacing = (-0.8).sp,
            color = onPrimaryContainerColor,
            fontWeight = FontWeight.Medium
        )

        Canvas(
            modifier = Modifier
                .matchParentSize()
        ) {
            val widthPx = size.width
            val heightPx = size.height

            // Распределение высоты
            val iconFieldHeight = heightPx * 0.32f
            val stepFieldHeight = heightPx * 0.43f
            val textFieldHeight = heightPx * 0.22f
            val spacing = heightPx * 0.01f

            val iconSectionY = iconFieldHeight / 2 + spacing
            val circleSectionY = iconFieldHeight + spacing + (stepFieldHeight / 2)
            val textSectionY =
                iconFieldHeight + stepFieldHeight + (2 * spacing) + (textFieldHeight / 2)

            val eachStepWidth = widthPx / maxNumSteps
            val stepRadius = minOf(stepFieldHeight / 2, eachStepWidth / 2)
            val circleRadius = minOf(stepRadius * circleScale, eachStepWidth / 2)
            val serviceIconSize = minOf(iconFieldHeight, eachStepWidth / 2)

            val minHorizontalPadding = stepRadius
            val availableWidth = widthPx - (2 * minHorizontalPadding)
            val stepSpacing = if (maxNumSteps > 1) availableWidth / (maxNumSteps - 1) else 0f
            val leftPadding = minHorizontalPadding

            listOfSteps.forEachIndexed { index, step ->
                val stepX = leftPadding + index * stepSpacing
                val isCompleted = index < currentStep || step.status == StepperStatus.DONE
                val isCurrent = index == currentStep

                val outerColor = when {
                    isCurrent -> primaryColor.copy(alpha = 0.75f)
                    isCompleted && index < currentStep -> primaryColor
                    index < currentStep -> primaryColor
                    else -> colorScheme.outline.copy(alpha = 0.7f)
                }

                val innerColor = when {
                    isCompleted && index < currentStep -> primaryColor.copy(alpha = 0.7f)
                    isCompleted && index > currentStep -> primaryColor.copy(alpha = 0.7f)
                    isCurrent -> primaryContainerColor.copy(alpha = 0.4f)
                    else -> surfaceColor
                }

                val checkIconSize = circleRadius * 1.8f
                val strokeWidth = (stepRadius * 0.18f).coerceIn(1.dp.toPx(), 2.dp.toPx())
                val iconOffset =
                    Offset(stepX - checkIconSize / 2, circleSectionY - checkIconSize / 2)

                // Рисуем внешний круг
                drawCircle(
                    color = outerColor,
                    radius = circleRadius,
                    center = Offset(stepX, circleSectionY),
                    style = Stroke(width = strokeWidth)
                )

                // Рисуем внутренний круг
                drawCircle(
                    color = innerColor,
                    radius = circleRadius - strokeWidth / 2 + 0.5f,
                    center = Offset(stepX, circleSectionY),
                )

                // Соединительная линия между шагами
                if (index < listOfSteps.size - 1) {
                    val lineStartX = stepX + circleRadius + 3.dp.toPx()
                    val lineEndX = stepX + stepSpacing - circleRadius - 3.dp.toPx()

                    if (lineEndX > lineStartX) {
                        // Фоновая линия (неактивная)
                        drawLine(
                            color = colorScheme.outline.copy(alpha = 0.4f),
                            start = Offset(lineStartX, circleSectionY),
                            end = Offset(lineEndX, circleSectionY),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.cornerPathEffect(2.dp.toPx())
                        )

                        // Активная линия (только до текущего шага)
                        if (currentStep > index) {
                            drawLine(
                                color = primaryColor,
                                start = Offset(lineStartX, circleSectionY),
                                end = Offset(lineEndX, circleSectionY),
                                strokeWidth = 1.5.dp.toPx(),
                                pathEffect = PathEffect.cornerPathEffect(2.dp.toPx())
                            )
                        }
                    }
                }

                // Рисуем иконку галочки для завершенных шагов
                if (isCompleted) {
                    drawIntoCanvas { canvas ->
                        translate(iconOffset.x, iconOffset.y) {
                            with(painterStatusDone) {
                                draw(
                                    Size(checkIconSize, checkIconSize),
                                    colorFilter = ColorFilter.tint(surfaceColor)
                                )
                            }
                        }
                    }
                }

                // **Рисуем иконку сервиса**
                val iconTint = when {
                    index <= currentStep -> onPrimaryContainerColor
                    else -> onPrimaryContainerColor.copy(alpha = 0.75f)
                }

                drawIntoCanvas { canvas ->
                    translate(
                        stepX - serviceIconSize / 2,
                        iconSectionY - serviceIconSize / 2
                    ) {
                        with(painterServiceTypeMap[step.serviceType]!!) {
                            draw(
                                Size(serviceIconSize, serviceIconSize),
                                colorFilter = ColorFilter.tint(iconTint)
                            )
                        }
                    }
                }

                // **Рисуем текст (дату)**
                val day = String.format("%02d", index + 1) //TODO Test only
                val text = "${day}.02.25"

                val fontSize = (stepRadius / 2f).coerceIn(8.sp.toPx(), 12.sp.toPx())
                val adaptiveTextStyle = textStyle.copy(
                    fontSize = fontSize.toSp(),
                    fontWeight = Bold,
                    color = if (index < currentStep) onPrimaryContainerColor else
                        onPrimaryContainerColor.copy(
                            alpha = 0.7f
                        )

                )

                val textLayoutInfo = textMeasurer.measure(text, adaptiveTextStyle)

                if (heightPx > 40.dp.toPx() && textLayoutInfo.size.width * 0.95f < stepSpacing) {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = text,
                        style = adaptiveTextStyle,
                        topLeft = Offset(
                            stepX - textLayoutInfo.size.width / 2,
                            textSectionY - textLayoutInfo.size.height / 2
                        )
                    )
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
        )
        {
            StepProgressionBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                listOfSteps = orderServiceList.subList(0, 12),
                currentStep = 2
            )
        }
    }
}