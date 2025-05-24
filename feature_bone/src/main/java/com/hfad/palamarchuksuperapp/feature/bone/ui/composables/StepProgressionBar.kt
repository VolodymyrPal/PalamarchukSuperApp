package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.CornerRadius
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
import com.example.compose.FeatureTheme
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ServiceType
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.serviceOrderLists

@Composable
fun StepProgressionBar(
    modifier: Modifier = Modifier,
    listOfSteps: List<Stepper>,
    currentStep: Int = 0,
    circleScale: Float = 0.6f,
    roundCorner: Float = 1f // from 0 to 1
) {

    var componentSize by remember { mutableStateOf(Size.Zero) }

    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.onSurfaceVariant // or colorScheme.primary
    val primaryContainerColor = colorScheme.primaryContainer
    val onSurface = colorScheme.onSurface
    val surfaceColor = colorScheme.surface

    // Кэш иконок
    val painterServiceTypeMap = painterServiceTypeMap()
    val painterStatusDone = rememberVectorPainter(image = Icons.Default.Check)

    val roundCorner = roundCorner.coerceIn(0f, 1f)

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
            color = onSurface,
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
                    isCompleted -> primaryColor.copy(alpha = 0.7f)
                    index < currentStep -> primaryColor
                    else -> colorScheme.outline.copy(alpha = 0.5f)
                }

                val innerColor = when {
                    isCompleted && index < currentStep -> primaryColor.copy(alpha = 0.7f)
                    isCompleted && index > currentStep -> primaryColor.copy(alpha = 0.6f)
                    isCurrent -> primaryContainerColor.copy(alpha = 0.5f)
                    else -> surfaceColor
                }

                val checkIconSize = circleRadius * 1.8f
                val strokeWidth = (stepRadius * 0.18f).coerceIn(1.dp.toPx(), 2.dp.toPx())
                val iconOffset =
                    Offset(stepX - checkIconSize / 2, circleSectionY - checkIconSize / 2)

                // outer circle
//                drawCircle(
//                    color = outerColor,
//                    radius = circleRadius,
//                    center = Offset(stepX, circleSectionY),
//                    style = Stroke(width = strokeWidth)
//                )
//
//                // Inner circle
//                drawCircle(
//                    color = innerColor,
//                    radius = circleRadius - strokeWidth / 2 + 0.5f,
//                    center = Offset(stepX, circleSectionY),
//                )

                val cornerRadius = (circleRadius * roundCorner)

                if (roundCorner >= 1f) {
                    // Рисуем внешний круг (обводка)
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
                } else {
                    // Рисуем внешний скругленный прямоугольник (обводка)
                    drawRoundRect(
                        color = outerColor,
                        topLeft = Offset(stepX - circleRadius, circleSectionY - circleRadius),
                        size = Size(circleRadius * 2, circleRadius * 2),
                        cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                        style = Stroke(width = strokeWidth)
                    )

                    // Рисуем внутренний скругленный прямоугольник
                    val innerSize = circleRadius * 2 - strokeWidth + 1f
                    val innerOffset = strokeWidth / 2 - 0.5f
                    drawRoundRect(
                        color = innerColor,
                        topLeft = Offset(stepX - circleRadius + innerOffset, circleSectionY - circleRadius + innerOffset),
                        size = Size(innerSize, innerSize),
                        cornerRadius = CornerRadius(cornerRadius - innerOffset, cornerRadius - innerOffset)
                    )
                }



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
                    index <= currentStep -> onSurface
                    else -> onSurface.copy(alpha = 0.75f)
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

                val day = String.format("%02d", index + 1) //TODO Test only
                val text = "${day}.06.25"

                val fontSize = (stepRadius / 2f).coerceIn(10.sp.toPx(), 18.sp.toPx())
                val adaptiveTextStyle = textStyle.copy(
                    fontSize = fontSize.toSp(),
                    fontWeight = Bold,
                    color = if (index < currentStep) onSurface else
                        onSurface.copy(alpha = 0.4f)

                )

                val textLayoutInfo = textMeasurer.measure(text, adaptiveTextStyle)

                if (heightPx > 40.dp.toPx() && textLayoutInfo.size.width < stepSpacing) {
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

enum class StepperStatus {
    DONE, CANCELED, IN_PROGRESS, CREATED
}

interface Stepper {
    val status: StepperStatus
    val serviceType: ServiceType

    @get:DrawableRes
    val icon: Int
}

@Composable
@Preview
fun StepProgressionBarDarkPreview(

) {
    FeatureTheme (
        darkTheme = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        )
        {
            StepProgressionBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                listOfSteps = serviceOrderLists.subList(0, 6),
                currentStep = 2,
                roundCorner = 0.1f
            )
        }
    }
}

@Composable
@Preview
fun StepProgressionBarLightPreview(

) {
    FeatureTheme (darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        )
        {
            StepProgressionBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                listOfSteps = serviceOrderLists.subList(0, 12),
                currentStep = 2,
                roundCorner = 0.7f
            )
        }
    }
}