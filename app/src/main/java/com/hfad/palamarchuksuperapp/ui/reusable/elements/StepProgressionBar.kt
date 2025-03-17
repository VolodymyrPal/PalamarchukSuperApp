package com.hfad.palamarchuksuperapp.ui.reusable.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
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
    circleScale: Float = 0.6f,
) {

    var componentSize by remember { mutableStateOf(Size.Zero) }
    val colorFilter = MaterialTheme.colorScheme.onPrimaryContainer

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
            letterSpacing = (-0.2).sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Canvas(
            modifier = Modifier
                .matchParentSize()
        ) {
            val widthPx = size.width
            val heightPx = size.height

            // Распределение высоты
            val iconFieldHeight = heightPx * 0.3f
            val stepFieldHeight = heightPx * 0.45f
            val textFieldHeight = heightPx * 0.2f
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

                val checkIconSize = circleRadius * 2
                val strokeWidth = (stepRadius * 0.2f).coerceIn(1.dp.toPx(), 2.dp.toPx())
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
                    radius = circleRadius - strokeWidth / 2 + 1f,
                    center = Offset(stepX, circleSectionY),
                )

                // Соединительная линия между шагами
                if (index < listOfSteps.size - 1) {
                    val lineStartX = stepX + circleRadius + 3.dp.toPx()
                    val lineEndX = stepX + stepSpacing - circleRadius - 3.dp.toPx()

                    if (lineEndX > lineStartX) {
                        drawLine(
                            color = if (currentStep > index) Color(0xFF2E7D32)
                            else Color(0xFFBDBDBD),
                            start = Offset(lineStartX, circleSectionY),
                            end = Offset(lineEndX, circleSectionY),
                            strokeWidth = 1.25.dp.toPx()
                        )
                    }
                }

                // Рисуем иконку галочки для завершенных шагов
                if (isCompleted) {
                    drawIntoCanvas { canvas ->
                        translate(iconOffset.x, iconOffset.y) {
                            with(painterStatusDone) {
                                draw(
                                    Size(checkIconSize, checkIconSize),
                                    colorFilter = ColorFilter.tint(Color.White)
                                )
                            }
                        }
                    }
                }

                // **Рисуем иконку сервиса**
                drawIntoCanvas { canvas ->
                    translate(
                        stepX - serviceIconSize / 2,
                        iconSectionY - serviceIconSize / 2
                    ) {
                        with(painterServiceTypeMap[step.serviceType]!!) {
                            draw(
                                Size(serviceIconSize, serviceIconSize),
                                colorFilter = ColorFilter.tint(colorFilter)
                            )
                        }
                    }
                }

                // **Рисуем текст (дату)**
                val day = String.format("%02d", index + 1)
                val text = "${day}.02.25"

                val fontSize = (stepRadius / 2f).coerceIn(8.sp.toPx(), 12.sp.toPx())
                val adaptiveTextStyle = textStyle.copy(
                    fontSize = fontSize.toSp(),
                    fontWeight = Bold
                )

                val textLayoutInfo = textMeasurer.measure(text, adaptiveTextStyle)

                if (heightPx > 40.dp.toPx() && textLayoutInfo.size.width * 1.02f < stepSpacing) {
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

@Preview
@Composable
fun BoneScreenPreview() {
    AppTheme(useDarkTheme = false) {
        BoneScreen()
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
            defaultElevation = 0.dp
        )
    ) {
        val containerIcon = painterResource(R.drawable.container_svgrepo_com)

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Icon(
                    painter = containerIcon,
                    contentDescription = "Container Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(24.dp)
                )
                AppText(
                    modifier = Modifier,
                    value = "Order: ${entity.name}",
                    appTextConfig = appTextConfig(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    modifier = Modifier,
                )
            }

            if (true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AppIconInfoField(
                            modifier = Modifier,
                            icon = painterResource(R.drawable.warehouse),
                            text = "1fds 23"
                        )
                        AppIconInfoField(
                            modifier = Modifier,
                            icon = painterResource(R.drawable.warehouse),
                            text = "1fds 23"
                        )
                        AppIconInfoField(
                            modifier = Modifier,
                            icon = painterResource(R.drawable.warehouse),
                            text = "1fds 23"
                        )
                    }
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AppIconInfoField(
                            modifier = Modifier,
                            icon = painterResource(R.drawable.warehouse),
                            text = "1fds 23"
                        )
                        AppIconInfoField(
                            modifier = Modifier,
                            icon = painterResource(R.drawable.warehouse),
                            text = "1fds 23"
                        )
                        AppIconInfoField(
                            modifier = Modifier,
                            icon = painterResource(R.drawable.warehouse),
                            text = "1fds 23"
                        )
                    }
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AppIconInfoField(
                            modifier = Modifier,
                            icon = painterResource(R.drawable.warehouse),
                            text = "1fds 23"
                        )
                        AppIconInfoField(
                            modifier = Modifier,
                            icon = painterResource(R.drawable.warehouse),
                            text = "1fds 23"
                        )
                        AppIconInfoField(
                            modifier = Modifier,
                            icon = painterResource(R.drawable.warehouse),
                            text = "1fds 23"
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepProgressionBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(start = 4.dp, end = 4.dp, bottom = 2.dp),
                    listOfSteps = orderServiceList.subList(0, 8),
                    currentStep = 2
                )
            }
        }
    }
}

@Composable
fun painterServiceTypeMap() = ServiceType.entries.associateWith {
    when (it) {
        ServiceType.FREIGHT -> painterResource(R.drawable.sea_freight)
        ServiceType.FORWARDING -> painterResource(R.drawable.freight)
        ServiceType.STORAGE -> painterResource(R.drawable.warehouse)
        ServiceType.PRR -> painterResource(R.drawable.loading_boxes)
        ServiceType.CUSTOMS -> painterResource(R.drawable.freight)
        ServiceType.TRANSPORT -> painterResource(R.drawable.truck)
        ServiceType.EUROPE_TRANSPORT -> painterResource(R.drawable.truck)
        ServiceType.UKRAINE_TRANSPORT -> painterResource(R.drawable.truck)
        ServiceType.OTHER -> rememberVectorPainter(Icons.Default.Search)
    }
}