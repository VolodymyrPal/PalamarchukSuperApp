package com.hfad.palamarchuksuperapp.ui.compose.boneScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.ui.reusable.elements.AppText
import com.hfad.palamarchuksuperapp.ui.reusable.elements.StepProgressionBar
import com.hfad.palamarchuksuperapp.ui.reusable.elements.appTextConfig
import com.hfad.palamarchuksuperapp.ui.viewModels.BusinessEntity
import com.hfad.palamarchuksuperapp.ui.viewModels.ServiceType
import com.hfad.palamarchuksuperapp.ui.viewModels.StepperStatus
import com.hfad.palamarchuksuperapp.ui.viewModels.orderServiceList

@Composable
fun OrderCard(
    modifier: Modifier = Modifier,
    entity: BusinessEntity,
) {
    var expanded = remember { mutableStateOf(false) }
    val orderStatus = remember { mutableStateOf(StepperStatus.IN_PROGRESS) } // Симулируем статус заказа
    val currentStepCount = remember { mutableStateOf(3) } // Симулируем текущий шаг

    val rotationState by animateFloatAsState(
        targetValue = if (expanded.value) 180f else 0f,
        label = "rotationAnimation"
    )

    val elevation by animateDpAsState(
        targetValue = if (expanded.value) 6.dp else 2.dp,
        label = "elevationAnimation"
    )

    val colorScheme = MaterialTheme.colorScheme

    // Определяем цвет статуса заказа
    val statusColor = when (orderStatus.value) {
        StepperStatus.DONE -> colorScheme.primary
        StepperStatus.IN_PROGRESS -> colorScheme.tertiary
        StepperStatus.CREATED -> colorScheme.outline
        StepperStatus.CANCELED -> colorScheme.error
    }

    // Определяем текст статуса заказа
    val statusText = when (orderStatus.value) {
        StepperStatus.DONE -> "Завершен"
        StepperStatus.IN_PROGRESS -> "В процессе"
        StepperStatus.CREATED -> "Создан"
        StepperStatus.CANCELED -> "Отменен"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    expanded.value = !expanded.value
                },
                indication = null,
                interactionSource = MutableInteractionSource()
            ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.3f)),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
    ) {
        Column {
            // Заголовок карточки
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Иконка в круге
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.container_svgrepo_com),
                            contentDescription = "Container Icon",
                            tint = colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Номер заказа и статус
                    Column {
                        AppText(
                            value = "Заказ №${entity.name}",
                            appTextConfig = appTextConfig(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        // Статус заказа с цветовым индикатором
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            AppText(
                                value = statusText,
                                appTextConfig = appTextConfig(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = statusColor
                            )
                        }
                    }
                }

                // Стрелка раскрытия
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(colorScheme.primaryContainer.copy(alpha = 0.5f))
                        .clickable {
                            expanded.value = !expanded.value
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand",
                        modifier = Modifier.rotate(rotationState),
                        tint = colorScheme.onPrimaryContainer
                    )
                }
            }

            // Индикатор прогресса транспортировки (отображается всегда)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                StepProgressionBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    listOfSteps = orderServiceList.subList(0, 7),
                    currentStep = currentStepCount.value
                )
            }

            // Расширяемая секция с дополнительной информацией
            AnimatedVisibility(
                enter = fadeIn(animationSpec = tween(300)) +
                        expandVertically(
                            expandFrom = Alignment.Top,
                            animationSpec = tween(300)
                        ),
                exit = fadeOut(animationSpec = tween(300)) +
                        shrinkVertically(
                            shrinkTowards = Alignment.Top,
                            animationSpec = tween(300)
                        ),
                visible = expanded.value
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Горизонтальный разделитель
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(vertical = 8.dp)
                            .background(colorScheme.outline.copy(alpha = 0.2f))
                    )

                    TableOrderInfo(
                        modifier = Modifier.fillMaxWidth(),
                        orderInfoList = listOf(
                            OrderInfo(
                                "Пункт отправления",
                                "Шанхай, Китай",
                                painterResource(R.drawable.freight)
                            ),
                            OrderInfo(
                                "Пункт назначения",
                                "Одесса, Украина",
                                painterResource(R.drawable.truck)
                            ),
                            OrderInfo("Статус доставки", "В пути", painterResource(R.drawable.truck)),
                            OrderInfo("Ожидаемая дата прибытия", "24.02.2025", painterResource(R.drawable.freight)),
                            OrderInfo(
                                "Контейнер",
                                "40HC-7865425",
                                painterResource(R.drawable.container_svgrepo_com)
                            ),
                            OrderInfo("Груз", "Электроника", painterResource(R.drawable.warehouse)),
                            OrderInfo("Менеджер", entity.manager, painterResource(R.drawable.baseline_shopping_basket_24))
                        )
                    )
                }
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