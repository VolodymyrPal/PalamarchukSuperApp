package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.ShoppingCart
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.FeatureTheme
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.theme.Status
import com.hfad.palamarchuksuperapp.core.ui.theme.statusColor
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ServiceType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateOrder
import com.hfad.palamarchuksuperapp.feature.bone.ui.theme.appRippleEffect
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.serviceOrderLists

@Composable
fun OrderCard(
    modifier: Modifier = Modifier,
    order: Order,
    initialStatus: StepperStatus = StepperStatus.IN_PROGRESS,
    currentStep: Int = 2,
    initialExpanded: Boolean = false,
    internalPaddingValues: PaddingValues = PaddingValues(),
) {
    val expanded = remember { mutableStateOf(initialExpanded) }
    val orderStatus = remember { mutableStateOf(initialStatus) }
    val currentStepCount = remember { mutableStateOf(currentStep) }

    val rotationState by animateFloatAsState(
        targetValue = if (expanded.value) 180f else 0f,
        label = "rotationAnimation"
    )

    val elevation by animateDpAsState(
        targetValue = if (expanded.value) 6.dp else 2.dp,
        label = "elevationAnimation"
    )

    val colorScheme = MaterialTheme.colorScheme

    val statusColor = when (orderStatus.value) {
        StepperStatus.DONE -> statusColor(Status.DONE)
        StepperStatus.IN_PROGRESS -> statusColor(Status.IN_PROGRESS)
        StepperStatus.CREATED -> statusColor(Status.CREATED)
        StepperStatus.CANCELED -> statusColor(Status.CANCELED)
    }

    val statusText = when (orderStatus.value) {
        StepperStatus.DONE -> "Завершен"
        StepperStatus.IN_PROGRESS -> "В процессе"
        StepperStatus.CREATED -> "Создан"
        StepperStatus.CANCELED -> "Отменен"
    }
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    expanded.value = !expanded.value
                },
                indication = null,
                interactionSource = interactionSource
            ),
        shape = MaterialTheme.shapes.extraSmall,
        border = BorderStroke(1.dp, colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface
        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
    ) {
        Column(
            modifier = Modifier.padding(internalPaddingValues)
        ) {
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
                    Icon(
                        painter = painterResource(R.drawable.container_svgrepo_com),
                        contentDescription = "Container Icon",
                        modifier = Modifier.size(24.dp)
                    )

                    Column {
                        AppText(
                            value = "Заказ № ${order.num}",
                            appTextConfig = appTextConfig(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

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

                // Arrow
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(colorScheme.primaryContainer)
                        .clickable(
                            interactionSource = null,
                            indication = appRippleEffect()
                        ) {
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            ) {
                StepProgressionBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(86.dp),
                    listOfSteps = serviceOrderLists.subList(0, 7),
                    currentStep = currentStepCount.value,
                    roundCorner = 0.3f
                )
            }

            this.AnimatedVisibility(
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
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {

                    TableOrderInfo(
                        modifier = Modifier.fillMaxWidth(),
                        orderInfoList = order.mapForOrderInfo()
                    )
                }
            }
        }
    }
}

@Composable
private fun Order.mapForOrderInfo(): List<OrderInfo> {
    val list = listOf(
        OrderInfo(
            stringResource(R.string.departure_point),
            this.departurePoint,
            rememberVectorPainter(Icons.Outlined.Place)
        ),
        OrderInfo(
            stringResource(R.string.destination_point),
            this.destinationPoint,
            rememberVectorPainter(Icons.Outlined.Home)
        ),
        OrderInfo(
            stringResource(R.string.delivery_status),
            when (this.status) {
                OrderStatus.CREATED -> stringResource(R.string.order_status_created)
                OrderStatus.CALCULATED -> stringResource(R.string.order_status_calculated)
                OrderStatus.IN_PROGRESS -> stringResource(R.string.order_status_in_progress)
                OrderStatus.DONE -> stringResource(R.string.order_status_done)
            },
            painterResource(R.drawable.truck)
        ),
        OrderInfo(
            stringResource(R.string.expected_arrival_date),
            this.arrivalDate,
            rememberVectorPainter(Icons.Outlined.DateRange)
        ),
        OrderInfo(
            stringResource(R.string.conatiner_number),
            this.containerNumber,
            painterResource(R.drawable.container_svgrepo_com)
        ),
        OrderInfo(
            stringResource(R.string.cargo),
            this.cargo,
            rememberVectorPainter(Icons.Outlined.ShoppingCart)
        ),
        OrderInfo(
            stringResource(R.string.manager),
            this.manager,
            rememberVectorPainter(Icons.Outlined.Person)
        )
    )
    return list
}

@Composable
fun painterServiceTypeMap() = ServiceType.entries.associateWith {
    when (it) {
        ServiceType.FULL_FREIGHT -> painterResource(R.drawable.sea_freight)
        ServiceType.FORWARDING -> painterResource(R.drawable.freight)
        ServiceType.STORAGE -> painterResource(R.drawable.warehouse)
        ServiceType.PRR -> painterResource(R.drawable.loading_boxes)
        ServiceType.CUSTOMS -> painterResource(R.drawable.freight)
        ServiceType.TRANSPORT -> painterResource(R.drawable.truck)
        ServiceType.EUROPE_TRANSPORT -> painterResource(R.drawable.truck)
        ServiceType.UKRAINE_TRANSPORT -> painterResource(R.drawable.truck)
        ServiceType.OTHER -> rememberVectorPainter(Icons.Default.Search)
        ServiceType.AIR_FREIGHT -> painterResource(R.drawable.plane)
    }
}

@Preview(heightDp = 920)
@Composable
fun OrderCardPreview() {
    Column {
        FeatureTheme {
            OrderCard(
                modifier = Modifier
                    .padding(5.dp)
                    .height(450.dp),
                order = generateOrder(),
                initialExpanded = true
            )
        }
        FeatureTheme(darkTheme = true) {
            OrderCard(
                modifier = Modifier
                    .padding(5.dp)
                    .height(450.dp),
                order = generateOrder(),
                initialExpanded = true
            )
        }
    }
}