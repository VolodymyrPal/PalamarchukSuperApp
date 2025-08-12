package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import android.icu.text.SimpleDateFormat
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
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
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.serviceOrderLists
import com.hfad.palamarchuksuperapp.feature.bone.ui.theme.appRippleEffect

@Composable
fun OrderCard(
    modifier: Modifier = Modifier,
    order: Order,
    currentStep: Int = 2,
    initialExpanded: Boolean = false,
    internalPadding: PaddingValues = PaddingValues(),
) {
    val expanded = remember { mutableStateOf(initialExpanded) }

    val statusColor = when (order.status) {
        OrderStatus.DONE -> statusColor(Status.DONE)
        OrderStatus.IN_PROGRESS -> statusColor(Status.IN_PROGRESS)
        OrderStatus.CREATED -> statusColor(Status.CREATED)
        OrderStatus.CALCULATED -> statusColor(Status.CANCELED)
    }

    val statusText = when (order.status) {
        OrderStatus.DONE -> R.string.order_status_done
        OrderStatus.IN_PROGRESS -> R.string.order_status_in_progress
        OrderStatus.CREATED -> R.string.order_status_created
        OrderStatus.CALCULATED -> R.string.order_status_calculated
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
        border = BorderStroke(1.dp, colorScheme.secondary),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface
        ),
    ) {
        Column(
            modifier = Modifier.padding(internalPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
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
                        modifier = Modifier.size(24.dp),
                        tint = colorScheme.primary
                    )

                    Column {
                        AppText(
                            value = stringResource(R.string.order_num, order.num),
                            appTextConfig = appTextConfig(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = colorScheme.primary
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
                RotateArrow(
                    modifier = Modifier,
                    expanded = expanded.value,
                    onClick = {
                        expanded.value = !expanded.value
                    }
                )
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
                    currentStep = currentStep,
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(bottom = 8.dp),
                        thickness = 1.dp,
                        color = colorScheme.secondary
                    )
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
fun RotateArrow(
    modifier: Modifier = Modifier,
    expanded: Boolean, // Передавайте Boolean вместо MutableState
    onClick: () -> Unit,
) {
    // Запомните colorScheme

    // Или используйте LocalContentColor для более стабильной работы
    val containerColor = colorScheme.primaryContainer
    val contentColor = colorScheme.onPrimaryContainer

    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(300),
        label = "rotationAnimation"
    )

    Box(
        modifier = modifier
            .size(36.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(containerColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() }, // Запомните interactionSource
                indication = appRippleEffect()
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Expand",
            modifier = Modifier.graphicsLayer {
                rotationZ = rotationState
            },
            tint = contentColor
        )
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
            SimpleDateFormat("dd.MM.yyyy").format(this.arrivalDate),
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
        FeatureTheme(
            dynamicColor = true
        ) {
            OrderCard(
                modifier = Modifier
                    .padding(5.dp)
                    .height(450.dp),
                order = generateOrder(), //TODO for test
                initialExpanded = true
            )
        }
        FeatureTheme(darkTheme = true) {
            OrderCard(
                modifier = Modifier
                    .padding(5.dp)
                    .height(450.dp),
                order = generateOrder(), //TODO for test
                initialExpanded = true
            )
        }
    }
}