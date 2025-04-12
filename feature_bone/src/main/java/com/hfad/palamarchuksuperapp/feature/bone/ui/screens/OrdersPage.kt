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
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.ServiceScenario
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.ServiceType
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.StepperStatus
import kotlin.random.Random


@Composable
fun OrdersPage(
    modifier: Modifier = Modifier,
    orderPageState: OrderPageState = OrderPageState(),
) {
    val orderPageState = OrderPageState(
        orders = generateSampleOrders() //TODO for testing
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            OrderStatistics(
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
fun OrderStatistics(
    orderMetrics: OrderMetrics = OrderMetrics(),
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
                    value = "${orderMetrics.totalOrderWeight} т",
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
fun OrderStat(
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

data class OrderPageState(
    val orderMetrics: OrderMetrics = OrderMetrics(),
    val orders: List<Order> = emptyList(),
)


data class OrderMetrics(
    val totalOrders: Int = 0,
    val completedOrders: Int = 0,
    val inProgressOrders: Int = 0,
    val totalOrderWeight: Double = 0.0,
)


@Preview
@Composable
fun OrdersPagePreview() {
    AppTheme(
        useDarkTheme = true
    ) {
        OrdersPage(
            orderPageState = OrderPageState(
                orders = listOf(
                    Order(), Order(), Order()
                ),
                orderMetrics = OrderMetrics(
                    53, 40, 5, 534.25
                )
            ),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderCardListPreview() {
    AppTheme(
        useDarkTheme = false
    ) {
        OrdersPage(
            orderPageState = OrderPageState(
                orders = listOf(
                    Order(), Order(), Order()
                ),
                orderMetrics = OrderMetrics(
                    53, 40, 5, 534.25
                )
            )
        )
    }
}

@Preview
@Composable
fun OrderStatPreview() {
    Column {
        AppTheme(
            useDarkTheme = false
        ) {
            OrderStatistics(
                OrderMetrics(
                    53, 40, 5, 534.25
                )
            )
        }
        AppTheme(
            useDarkTheme = true
        ) {
            OrderStatistics(
                OrderMetrics(
                    53, 40, 5, 534.25
                )
            )
        }
    }
}