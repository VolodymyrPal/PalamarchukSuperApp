package com.hfad.palamarchuksuperapp.ui.reusable.elements

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
    val textStyle =
        MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp, letterSpacing = -0.4.sp)
    val lineColorPrimary = MaterialTheme.colorScheme.primary



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