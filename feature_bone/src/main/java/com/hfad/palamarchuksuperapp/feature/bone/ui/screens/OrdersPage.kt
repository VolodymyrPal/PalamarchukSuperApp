package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.ui.OrderCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.Order
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.OrderService
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.OrderStatistic
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.ServiceScenario
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.ServiceType
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.StepperStatus
import kotlin.random.Random


@Composable
internal fun OrdersPage(
    modifier: Modifier = Modifier,
    orderPageState: OrderPageState = OrderPageState(),
) {
    val orderPageState = OrderPageState(
        orders = generateSampleOrders(), //TODO for testing
        orderMetrics = generateOrderStatistic() //TODO for testing
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            OrderStatisticCard(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                orderMetrics = orderPageState.orderMetrics
            )
        }

        items(items = orderPageState.orders) {
            OrderCard(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                order = it,
                initialStatus = StepperStatus.entries.random()
            )
        }
    }
}

@Composable
private fun OrderStatisticCard(
    orderMetrics: OrderStatistic = OrderStatistic(),
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val title = stringResource(R.string.order_statistic_title)
            AppText(
                value = title,
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val inProgress = ImageVector.vectorResource(R.drawable.in_progress)

                val inWork = stringResource(R.string.in_work)
                OrderStat(
                    modifier = Modifier.weight(0.3f),
                    icon = inProgress,
                    value = "${orderMetrics.inProgressOrders}",
                    label = inWork,
                    color = MaterialTheme.colorScheme.error
                )

                val completed = stringResource(R.string.completed)
                OrderStat(
                    modifier = Modifier.weight(0.3f),
                    icon = Icons.Default.Check,
                    value = "${orderMetrics.completedOrders}",
                    label = completed,
                    color = Color(0xFF55940E)
                )

                val weightPainter = ImageVector.vectorResource(R.drawable.kilogram)
                val finishFull = stringResource(R.string.finish_full)
                OrderStat(
                    modifier = Modifier.weight(0.3f),
                    icon = weightPainter,
                    value = "${(orderMetrics.totalOrderWeight * 100).toInt() / 100.0} т",
                    label = finishFull,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )

            val sumOrderTitle = stringResource(R.string.summ_orders)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    value = "$sumOrderTitle ",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleMedium
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                AppText(
                    value = "${orderMetrics.totalOrders}",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun OrderStat(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    color: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        AppText(
            modifier = Modifier,
            value = value,
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        )

        AppText(
            value = label,
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

internal data class OrderPageState(
    val orderMetrics: OrderStatistic = OrderStatistic(),
    val orders: List<Order> = emptyList(),
)

private fun generateSampleOrders(): List<Order> {
    val serviceScenarios = listOf(
        ServiceScenario.NonEuropeContainer.WithFreight.scenario,
        ServiceScenario.ChinaEuropeContainer.scenario,
        ServiceScenario.ChinaUkraineContainer.scenario,
        ServiceScenario.SimpleEurope.scenario,
        ServiceScenario.DynamicScenario(
            listOf(
                ServiceType.AIR_FREIGHT,
                ServiceType.FORWARDING,
                ServiceType.CUSTOMS
            )
        ).scenario
    )

    return List(10) { index ->
        val selectedScenario = serviceScenarios[index % serviceScenarios.size]
        val orderServices = selectedScenario.mapIndexed { serviceIndex, serviceType ->
            OrderService(
                id = index * 100 + serviceIndex,
                orderId = index,
                fullTransport = Random.nextBoolean(),
                serviceType = serviceType,
                price = Random.nextFloat() * 10000 + 1000,
                duration = Random.nextInt(3, 30),
                status = StepperStatus.entries[Random.nextInt(StepperStatus.entries.size)],
                icon = when (serviceType) {
                    ServiceType.FULL_FREIGHT -> R.drawable.in_progress
                    ServiceType.AIR_FREIGHT -> R.drawable.kilogram
                    ServiceType.CUSTOMS -> R.drawable.lock_outlined
                    else -> R.drawable.in_progress
                }
            )
        }

        Order(
            id = index,
            serviceList = orderServices,
            status = OrderStatus.entries[Random.nextInt(OrderStatus.entries.size)],
            // cargoType будет определен автоматически на основе serviceList в data class
        )
    }
}

private fun generateOrderStatistic(): OrderStatistic {
    return OrderStatistic(
        totalOrders = Random.nextInt(20, 30),
        completedOrders = Random.nextInt(5, 15),
        inProgressOrders = Random.nextInt(1, 5),
        totalOrderWeight = Random.nextDouble(10.0, 100.0)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OrderCardListPreview() {
    AppTheme(
        useDarkTheme = false
    ) {
        OrdersPage(
            orderPageState = OrderPageState(
                orderMetrics = OrderStatistic(
                    53, 40, 5, 534.25
                )
            )
        )
    }
}

@Preview
@Composable
private fun OrderStatPreview() {
    Column {
        AppTheme(
            useDarkTheme = false
        ) {
            OrderStatisticCard(
                OrderStatistic(
                    53, 40, 5, 534.25
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }
        AppTheme(
            useDarkTheme = true
        ) {
            OrderStatisticCard(
                OrderStatistic(
                    53, 40, 5, 534.25
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun OrdersPagePreview() {
    AppTheme(
        useDarkTheme = true
    ) {
        OrdersPage(
            orderPageState = OrderPageState(
                orderMetrics = OrderStatistic(
                    53, 40, 5, 534.25
                )
            ),
        )
    }
}