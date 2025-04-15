package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.PaymentStatistic
import kotlin.random.Random

@Composable
fun PaymentsPage(
    modifier: Modifier = Modifier,
    paymentPageState: PaymentPageState = PaymentPageState(),
) {
    val paymentPageState = generatePaymentSample() //TODO for test only

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            PaymentsStatisticsCard(
                paymentPageState.paymentStatistic
            )
        }
        items(paymentPageState.payments) { payment ->
            PaymentCard(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                payment = payment
            )
        }
    }
}

@Composable
fun PaymentsStatisticsCard(
    paymentStatistic: PaymentStatistic = PaymentStatistic(),
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppText(
                value = stringResource(R.string.payment_statistic_title),
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )
            val gridItems = remember { paymentStatistic.paymentsByCurrency }
            LazyVerticalGrid(
                modifier = Modifier
                    .sizeIn(maxHeight = 800.dp)
                    .fillMaxWidth(),
                columns = object : GridCells {
                    override fun Density.calculateCrossAxisCellSizes(
                        availableSize: Int,
                        spacing: Int,
                    ): List<Int> {
                        val maxCount = minOf(
                            (availableSize + spacing) / (100.dp.roundToPx() + spacing),
                            gridItems.size
                        )
                        val count = maxOf(maxCount, 1)
                        val cellSize = (availableSize - spacing * (count - 1)) / count
                        return List(count) { cellSize }
                    }
                },
                contentPadding = PaddingValues(
                    horizontal = 4.dp,
                    vertical = 4.dp
                ),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                itemsIndexed(gridItems) { index, (currency, amount) ->
                    CurrencyStat(
                        modifier = Modifier,
                        currency = currency,
                        amount = amount,
                        color = colorSet.elementAt(index % colorSet.size),
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    modifier = Modifier,
                    title = stringResource(R.string.total_payment),
                    value = paymentStatistic.totalPayment.toString(),
                    icon = rememberVectorPainter(Icons.Outlined.Star)
                )

                StatItem(
                    modifier = Modifier,
                    title = stringResource(R.string.factory_payment_amount),
                    value = "${paymentStatistic.totalReceiver}",
                    icon = painterResource(R.drawable.truck)
                )

                StatItem(
                    modifier = Modifier,
                    title = stringResource(R.string.avg_payment_due_date),
                    value = "${paymentStatistic.daysToSend} day",
                    icon = rememberVectorPainter(Icons.Outlined.DateRange),
                )
            }
        }
    }
}

@Composable
fun CurrencyStat(
    currency: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            AppText(
                value = currency,
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
        }

        AppText(
            value = "${((amount * 100).toInt() / 100.0)}",
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun StatItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    isNegative: Boolean = false,
) {
    Row(
        modifier = modifier
            .height(intrinsicSize = IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 4.dp)
        )

        Column {
            AppText(
                value = value,
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                ),
                color = if (isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            )

            AppText(
                value = title,
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(
                    horizontal = 4.dp
                )
            )
        }
    }
}

data class PaymentData(
    val id: String,
    val amount: String,
    val currency: String,
    val factory: String,
    val productType: String,
    val batchInfo: String,
    val paymentDate: String,
    val dueDate: String,
    val status: PaymentStatus,
)

enum class PaymentStatus {
    PAID, PENDING, OVERDUE
}

@Composable
fun PaymentCard(
    payment: PaymentData,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (payment.status) {
        PaymentStatus.PAID -> MaterialTheme.colorScheme.primary
        PaymentStatus.PENDING -> MaterialTheme.colorScheme.tertiary
        PaymentStatus.OVERDUE -> MaterialTheme.colorScheme.error
    }

    val statusText = when (payment.status) {
        PaymentStatus.PAID -> "Оплачено"
        PaymentStatus.PENDING -> "Ожидается"
        PaymentStatus.OVERDUE -> "Просрочено"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),

        ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Заголовок и статус
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    value = payment.id,
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleSmall
                    )
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    AppText(
                        value = statusText,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        ),
                        color = statusColor
                    )
                }
            }

            // Сумма платежа с валютой
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText(
                        value = payment.amount,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        AppText(
                            value = payment.currency,
                            appTextConfig = appTextConfig(
                                textStyle = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            // Информация о фабрике и партии товаров
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    AppText(
                        value = payment.factory,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    AppText(
                        value = payment.productType,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    )
                }
            }

            AppText(
                value = "Партия: ${payment.batchInfo}",
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.bodySmall
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 24.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            // Даты платежей
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    AppText(
                        value = "Дата платежа:",
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodySmall
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    AppText(
                        value = payment.paymentDate,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    AppText(
                        value = "Срок оплаты:",
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodySmall
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    AppText(
                        value = payment.dueDate,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodyMedium
                        ),
                        color = if (payment.status == PaymentStatus.OVERDUE)
                            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}


private fun generateSamplePayments(): List<PaymentData> {
    val factories = listOf(
        "Guangzhou Metal Works", "Berlin Precision Manufacturing",
        "Shanghai Industrial Group", "Warsaw Production Facility",
        "Tokyo Electronics Ltd", "Mumbai Steel Plant", "Detroit Auto Parts"
    )

    val productTypes = listOf(
        "Металлопрокат", "Электроника", "Полупроводники",
        "Автозапчасти", "Сталь", "Микрочипы", "Сырье"
    )

    val currencies = listOf("USD", "EUR", "CNY", "GBP", "JPY")
    val statuses = PaymentStatus.values()

    return List(15) { index ->
        val currencyIdx = Random.nextInt(currencies.size)
        val currencySymbol = currencies[currencyIdx]

        val amount = when (currencySymbol) {
            "USD", "EUR", "GBP" -> "${Random.nextInt(10, 100)},${Random.nextInt(100, 999)}"
            "CNY" -> "${Random.nextInt(100, 999)},${Random.nextInt(100, 999)}"
            "JPY" -> "${Random.nextInt(1000, 9999)},${Random.nextInt(100, 999)}"
            else -> "${Random.nextInt(10, 100)},${Random.nextInt(100, 999)}"
        }

        val isPaid = Random.nextInt(10) > 3
        val isOverdue = !isPaid && Random.nextInt(10) > 5

        val status = when {
            isPaid -> PaymentStatus.PAID
            isOverdue -> PaymentStatus.OVERDUE
            else -> PaymentStatus.PENDING
        }

        val month = Random.nextInt(1, 13)
        val day = Random.nextInt(1, 29)
        val paymentDate = "$day.${month.toString().padStart(2, '0')}.2023"

        val dueMonth = if (month < 12) month + 1 else 1
        val dueYear = if (month < 12) 2023 else 2024
        val dueDate = "$day.${dueMonth.toString().padStart(2, '0')}.$dueYear"

        PaymentData(
            id = "Платеж #${1001 + index}",
            amount = amount,
            currency = currencySymbol,
            factory = factories[Random.nextInt(factories.size)],
            productType = productTypes[Random.nextInt(productTypes.size)],
            batchInfo = "B-${Random.nextInt(1000, 9999)}-${Random.nextInt(10, 100)}",
            paymentDate = paymentDate,
            dueDate = dueDate,
            status = status
        )
    }
}

private fun generatePaymentSample(): PaymentPageState {
    return PaymentPageState(
        payments = generateSamplePayments(),
        paymentStatistic = PaymentStatistic(
            totalPayment = Random.nextInt(10, 20),
            totalReceiver = Random.nextInt(1, 4),
            daysToSend = 1
        )
    )
}

data class PaymentPageState(
    val payments: List<PaymentData> = generateSamplePayments(), //TODO for test only
    val paymentStatistic: PaymentStatistic = PaymentStatistic(),
)

internal val colorSet = setOf(
    Color(0xFF2196F3),
    Color(0xFF2E7D32),
    Color(0xFF1565C0),
    Color(0xFFD32F2F),
    Color(0xFF823333),
    Color(0xFFB8860B),
    Color(0xFF9C27B0),
)

@Preview
@Composable
fun PaymentsPagePreview() {
    AppTheme {
        PaymentsPage()
    }
}

@Preview
@Composable
fun PaymentCardPreview() {
    Column {
        AppTheme {
            PaymentCard(
                modifier = Modifier.padding(8.dp),
                payment = PaymentData(
                    id = Random.nextInt(100, 200),
                    amount = "25,430",
                    currency = "USD",
                    factory = "Guangzhou Metal Works",
                    productType = "Металлопрокат",
                    batchInfo = "B-2354-42",
                    paymentDate = "15.09.2023",
                    dueDate = "15.10.2023",
                    status = PaymentStatus.PAID
                )
            )
        }
        AppTheme(useDarkTheme = true) {
            PaymentCard(
                modifier = Modifier.padding(8.dp),
                payment = PaymentData(
                    id = Random.nextInt(100, 200),
                    amount = "25,430",
                    currency = "USD",
                    factory = "Guangzhou Metal Works",
                    productType = "Металлопрокат",
                    batchInfo = "B-2354-42",
                    paymentDate = "15.09.2023",
                    dueDate = "15.10.2023",
                    status = PaymentStatus.PAID
                )
            )
        }
    }
}