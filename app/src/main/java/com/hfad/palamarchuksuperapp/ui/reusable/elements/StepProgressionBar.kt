package com.hfad.palamarchuksuperapp.ui.reusable.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.compose.AppTheme
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.ui.viewModels.BusinessEntity
import com.hfad.palamarchuksuperapp.ui.viewModels.ServiceType
import com.hfad.palamarchuksuperapp.ui.viewModels.Stepper
import com.hfad.palamarchuksuperapp.ui.viewModels.StepperStatus
import com.hfad.palamarchuksuperapp.ui.viewModels.orderServiceList

@Composable
fun StepProgressionBar(
    modifier: Modifier = Modifier,
    listOfSteps: List<Stepper>,
    currentStep: Int = 0,
) {
    Layout(
        modifier = modifier,
        content = {

            // Icons for stepper
            val painterServiceTypeMap = painterServiceTypeMap()
            val painterStatusDone = rememberVectorPainter(image = Icons.Default.Check)

            //Text policies
            val textMeasurer = rememberTextMeasurer()
            val textStyle = MaterialTheme.typography.bodySmall.copy(
                fontSize = 8.sp, //TODO SIZE PARAMETER
                letterSpacing = -0.2.sp
            )

            // Colors
            val lineColorPrimary = MaterialTheme.colorScheme.primary
            val iconColor = MaterialTheme.colorScheme.onPrimaryContainer

            val maxNumSteps = listOfSteps.size

            Canvas(modifier = Modifier) {
                val widthPx = size.width
                val heightPx = size.height

                // Динамически вычисляем радиус круга в зависимости от количества шагов и ширины
                // Адаптивный размер круга: больше при меньшем количестве шагов

                // Разделяем канвас на 3 области: верхнюю для иконок, среднюю для кругов и нижнюю для дат
                val topAreaHeight = heightPx * 0.35f
                val centerY = heightPx * 0.55f
                val bottomAreaY = centerY + topAreaHeight / 2


                // Рассчитываем минимальное расстояние между кругами
                val eachStepWidth = widthPx / maxNumSteps

                val stepRadius1: Float = (centerY * 0.3).toFloat()
                val stepRadius = minOf(
                    stepRadius1,
                    eachStepWidth
                )

                val minHorizontalPadding = stepRadius + stepRadius / 2

                // Рассчитываем доступное пространство для шагов
                val availableWidth = widthPx - (2 * minHorizontalPadding)

                // Рассчитываем оптимальный интервал между шагами
                val stepSpacing = if (maxNumSteps > 1) {
                    availableWidth / (maxNumSteps - 1)
                } else {
                    0f
                }

                // Определяем левую и правую границы для размещения шагов (отступы)
                val leftPadding = minHorizontalPadding

                listOfSteps.forEachIndexed { index, step ->
                    val stepX = leftPadding + index * stepSpacing
                    val isCompleted = index < currentStep || step.status == StepperStatus.DONE
                    val isCurrent = index == currentStep

                    val outerColor = when {
                        isCurrent -> Color(0xFF2E7D32)
                        isCompleted && index > currentStep -> Color(0xFFA5D6A7)
                        index < currentStep -> Color(0xFF2E7D32)
                        else -> Color(0xFFBDBDBD)
                    }

                    val innerColor = when {
                        isCompleted && index < currentStep -> Color(0xFF2E7D32)
                        isCompleted && index > currentStep -> Color(0xFFA5D6A7)
                        isCurrent -> Color(0xFFB2D5D8).copy(alpha = 0.9f)
                        else -> Color(0xFFF5F5F5)
                    }

                    val iconSize = (stepRadius * 1.5f)//.coerceAtMost(16.dp.toPx())
                    val iconOffset = Offset(stepX - iconSize / 2, centerY - iconSize / 2)
                    val strokeWidth = (stepRadius * 0.2f).coerceIn(1.dp.toPx(), 2.dp.toPx())

                    // Рисуем внешний круг
                    drawCircle(
                        color = outerColor,
                        radius = stepRadius,
                        center = Offset(stepX, centerY),
                        style = Stroke(width = strokeWidth)
                    )

                    // Рисуем внутренний круг
                    drawCircle(
                        color = innerColor,
                        radius = stepRadius - strokeWidth / 2 + 1f,
                        center = Offset(stepX, centerY),
                    )

                    // Рисуем соединительную линию между шагами с адаптивной длиной
                    if (index < listOfSteps.size - 1) {
                        val lineStartX = stepX + stepRadius + 4.dp.toPx()
                        val lineEndX = stepX + stepSpacing - stepRadius - 4.dp.toPx()

                        // Проверяем, что линия имеет положительную длину
                        if (lineEndX > lineStartX) {
                            drawLine(
                                color = if (currentStep > index) Color(0xFF2E7D32)
                                else lineColorPrimary.copy(alpha = 0.5f),
                                start = Offset(lineStartX, centerY),
                                end = Offset(lineEndX, centerY),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    }

                    // Рисуем иконку галочки для завершенных шагов
                    if (isCompleted) {
                        drawIntoCanvas { canvas ->
                            translate(iconOffset.x, iconOffset.y) {
                                with(painterStatusDone) {
                                    draw(
                                        Size(iconSize, iconSize),
                                        colorFilter = ColorFilter.tint(Color.White)
                                    )
                                }
                            }
                        }
                    }

                    // Форматируем и отображаем дату
                    val day = String.format("%02d", index + 1)
                    val text = "${day}.02.25"

                    // Адаптируем размер текста в зависимости от доступного пространства
                    val adaptiveTextStyle = textStyle.copy(
                        color = iconColor,
                        fontSize = ((stepRadius / 10.dp.toPx()) * 8).sp
                    )

                    val textLayoutInfo = textMeasurer.measure(text, adaptiveTextStyle)

                    // Отображаем текст даты под кругом, с учетом нижней границы
                    drawText(
                        textMeasurer = textMeasurer,
                        text = text,
                        style = adaptiveTextStyle,
                        topLeft = Offset(
                            stepX - textLayoutInfo.size.width / 2,
                            bottomAreaY
                        )
                    )

                    // Отображаем иконку сервиса над кругом
                    drawIntoCanvas { canvas ->
                        val serviceIconSize =
                            (stepRadius * 1.6f).coerceIn(12.dp.toPx(), 20.dp.toPx())
                        translate(
                            stepX - serviceIconSize / 2,
                            topAreaHeight / 2 - serviceIconSize / 2
                        ) {
                            with(painterServiceTypeMap[step.serviceType]!!) {
                                draw(
                                    Size(serviceIconSize, serviceIconSize),
                                    colorFilter = ColorFilter.tint(iconColor)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { measurables, constraints ->
        // Важное исправление: явно задаем максимальную высоту
        val maxHeight = 64.dp.roundToPx()

        // Создаем новые ограничения с фиксированной высотой
        val newConstraints = constraints.copy(
            minHeight = constraints.minHeight.coerceAtMost(maxHeight),
            maxHeight = maxHeight
        )

        // Используем новые ограничения для измерения
        val placeables = measurables.map { it.measure(newConstraints) }

        // Определяем итоговые размеры макета
        val width = constraints.maxWidth
        val height = maxHeight.coerceAtMost(constraints.maxHeight)

        layout(width, height) {
            placeables.forEach { it.place(0, 0) }
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
                .wrapContentHeight()
        )
        {
            StepProgressionBar(
                listOfSteps = orderServiceList.subList(0, 8),
                currentStep = 2
            )
        }
    }
}

@Preview
@Composable
fun BoneScreenPreview() {
    AppTheme {
        com.hfad.palamarchuksuperapp.ui.reusable.elements.BoneScreen()
    }
}

@Composable
fun BoneScreen(
    modifier: Modifier = Modifier,
//    viewModel: ViewModel? = null,
    navController: NavController? = null,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {

        },
        floatingActionButton = {

        },
        bottomBar = {

        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding()
                )
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppText(
                    modifier = Modifier.padding(8.dp),
                    value = "Orders",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.displayMedium,
                    )
                )
                val items = listOf(
                    BusinessEntity(name = "43222", manager = "VP +3806338875"),
                    BusinessEntity(name = "42224", manager = "VP +3806338875"),
                    BusinessEntity(name = "43226", manager = "VP +3806338875")
                )
                Box(modifier = Modifier.wrapContentSize()) {
                    LazyColumn {
                        items(items = items) {
                            OrderCard(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(top = 8.dp),
                                entity = it
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    modifier: Modifier = Modifier,
    entity: BusinessEntity,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, Color.Blue),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        val containerIcon = painterResource(R.drawable.container_svgrepo_com)

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = containerIcon,
                    contentDescription = "Container Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(start = 8.dp, top = 4.dp)
                )
                AppText(
                    modifier = Modifier,
                    value = "Order: ${entity.name}",
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    modifier = Modifier.padding(end = 8.dp, top = 8.dp),
                )
            }

            StepProgressionBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp),
                listOfSteps = orderServiceList.subList(0, 8),
                currentStep = 2
            )
        }
    }
}