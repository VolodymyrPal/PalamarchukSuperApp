package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import kotlin.random.Random

@Composable
fun PaymentsPage(
    modifier: Modifier = Modifier,
) {
    val payments = generateSamplePayments()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp, bottom = 16.dp
        )
    ) {
        item() {
            PaymentsStatCard()
        }
        items(payments.size) { index ->
            PaymentCard(payment = payments[index])
        }
    }
}

@Composable
fun PaymentsStatCard() {
    Card(
        modifier = Modifier
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppText(
                value = "Payments info",
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CurrencyStat(
                    currency = "USD",
                    amount = "542,800",
                    color = Color(0xFF2E7D32)
                )

                CurrencyStat(
                    currency = "EUR",
                    amount = "387,600",
                    color = Color(0xFF1565C0)
                )

                CurrencyStat(
                    currency = "CNY",
                    amount = "1,250,000",
                    color = Color(0xFFD32F2F)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            // Итоговая статистика
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    title = "Всего платежей",
                    value = "42",
                    icon = Icons.Default.ThumbUp
                )

                StatItem(
                    title = "Фабрики",
                    value = "7",
                    icon = Icons.Default.Info
                )

                StatItem(
                    title = "Просрочено",
                    value = "3",
                    icon = Icons.Default.DateRange,
                    isNegative = true
                )
            }
        }
    }
}

@Composable
fun CurrencyStat(
    currency: String,
    amount: String,
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
            value = amount,
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    icon: ImageVector,
    isNegative: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
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
                    fontWeight = FontWeight.Bold
                ),
                color = if (isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )

            AppText(
                value = title,
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.bodySmall
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
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

// Генерация примеров платежей
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
    AppTheme {
        PaymentCard(
            payment = PaymentData(
                id = "Платеж #1001",
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