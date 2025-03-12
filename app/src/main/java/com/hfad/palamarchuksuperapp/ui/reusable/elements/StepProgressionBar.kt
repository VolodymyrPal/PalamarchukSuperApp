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
            val painterServiceTypeMap = ServiceType.entries.associateWith {
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

            val painterStatusDone = rememberVectorPainter(image = Icons.Default.Check)
            val textMeasurer = rememberTextMeasurer()
            val textStyle = MaterialTheme.typography.bodySmall.copy(
                fontSize = 8.sp,
                letterSpacing = -0.4.sp
            )
            val lineColorPrimary = MaterialTheme.colorScheme.primary

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp) // Фиксированная высота для Canvas
            ) {
                val widthPx = size.width
                val heightPx = size.height

                // Динамически вычисляем радиус круга в зависимости от количества шагов и ширины
                val maxNumSteps = listOfSteps.size
                val minStepRadius = 8.dp.toPx()
                val maxStepRadius = 12.dp.toPx()

                // Адаптивный размер круга: больше при меньшем количестве шагов
                val stepRadius = (maxStepRadius -
                        ((maxNumSteps - 1) * 0.5f).coerceIn(0f, maxStepRadius - minStepRadius)
                        ).coerceAtLeast(minStepRadius)

                // Разделяем канвас на 3 области: верхнюю для иконок, среднюю для кругов и нижнюю для дат
                val topAreaHeight = heightPx * 0.3f
                val centerY = topAreaHeight + (heightPx - topAreaHeight) * 0.3f
                val bottomAreaY = centerY + stepRadius

                // Рассчитываем минимальное расстояние между кругами
                val minHorizontalPadding = 24.dp.toPx()

                // Рассчитываем доступное пространство для шагов
                val availableWidth = widthPx - (2 * minHorizontalPadding)

                // Рассчитываем оптимальный интервал между шагами
                val stepSpacing = if (maxNumSteps > 1) {
                    availableWidth / (maxNumSteps - 1)
                } else {
                    0f
                }

        listOfSteps.forEachIndexed { index, step ->

            val stepX = stepRadius + index * stepSpacing
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

            val iconSize = 12.dp.toPx()
            val iconOffset = Offset(
                stepX - iconSize / 2,
                centerY - iconSize / 2
            )

            val strokeWidth = 2.dp.toPx()

//            val textLayout = textMeasurer.measure(step.serviceType.name, textStyle)

            drawCircle(   //outer stroke color
                color = outerColor,
                radius = stepRadius,
                center = Offset(stepX, centerY),
                style = Stroke(width = strokeWidth)
            )

            drawCircle(
                //inner fill color
                color = innerColor,
                radius = stepRadius - strokeWidth / 2 + 1f,
                center = Offset(stepX, centerY),
            )

            if (index < listOfSteps.size - 1) {
                val prevX = stepX + stepRadius
                val newX = prevX + stepSpacing - stepRadius * 2
                drawLine(
                    color = if (currentStep >= index) Color(
                        0xFF2E7D32
                    ) else lineColorPrimary.copy(alpha = 0.5f),
                    start = Offset(prevX + 10, centerY),
                    end = Offset(newX - 10, centerY),
                    strokeWidth = (1).dp.toPx()
                )
            }

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

            val text = "0${index + 1}.02.25"
//                SimpleDateFormat("dd.MM.yy", Locale.US).format(
//                Date()
//            )

//            val text: String = StringBuilder()
//                .append(Random.nextInt(1, 28))
//                .append(".${Random.nextInt(1, 12)}")
//                .toString()

            val textLayoutInfo = textMeasurer.measure(text, textStyle)

            Log.d("Center Y", "Center Y: $centerY")
            drawText(
                textMeasurer = textMeasurer,
                text = text,
                style = textStyle,
                topLeft = Offset(
                    stepX - textLayoutInfo.size.width / 2,
                    centerY
                )
            )

            drawIntoCanvas { canvas ->
                translate(
                    stepX - iconSize / 2,
                    center.y - stepRadius * 2 - 10
                ) {
                    with(painterServiceTypeMap[step.serviceType]!!) {
                        draw(
                            Size(iconSize, iconSize),
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

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = containerIcon,
                contentDescription = "Container Icon",
                tint = Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = 4.dp)
            )
            AppText(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.TopCenter),
                value = "Order: ${entity.name}",
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp),
            )
            StepProgressionBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 4.dp, end = 4.dp)
                    .height(50.dp),
                listOfSteps = orderServiceList.subList(0, 8),
                currentStep = 2
            )
        }
    }
}