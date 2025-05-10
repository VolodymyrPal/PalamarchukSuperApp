package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.formatTrim
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.core.ui.theme.Status
import com.hfad.palamarchuksuperapp.core.ui.theme.statusColor
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.CashPayment
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateSaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.OrderCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.FinancePageState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun FinancePage(
    modifier: Modifier = Modifier,
    financeState: FinancePageState = FinancePageState(),
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FinanceStatisticCard(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    financeState.salesStatistics
                )
            }
            items(financeState.salesItems) { item ->
                FinanceCard(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    financeTransaction = item
                )
            }
        }
    }
}

@Composable
fun FinanceCard(
    modifier: Modifier = Modifier,
    financeTransaction: TypedTransaction,
) {
    when (financeTransaction) {
        is Order -> {}
        is CashPayment -> {}
        is SaleOrder -> {}
        is PaymentOrder -> {}
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppText(
                    value = "Транзакция #${1000 + financeTransaction.id}",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleSmall
                    )
                )
                AppText(
                    value = "${financeTransaction.id * 1500 + 5000} грн",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleSmall,
                    ),
                    color = if (financeTransaction.id % 2 == 0) MaterialTheme.colorScheme.primary else
                        MaterialTheme.colorScheme.error
                )
            }

            AppText(
                value = "Тип: ${if (financeTransaction.id % 2 == 0) "Доход" else "Расход"}",
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            )

            AppText(
                value = "Дата: 10.${financeTransaction.id + 1}.2023",
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.bodySmall,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FinanceStatisticCard(
    modifier: Modifier = Modifier,
    financeStatistic: FinanceStatistic,
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
            AppText(
                value = "Общие показатели",
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
                FinanceStat(
                    modifier = Modifier.weight(0.33f),
                    icon = Icons.Default.Build,
                    value = "150,000 грн",
                    label = "Доход",
                    color = Color(0xFF2E7D32)
                )

                FinanceStat(
                    modifier = Modifier.weight(0.33f),
                    icon = Icons.Default.ThumbUp,
                    value = "75,000 грн",
                    label = "Расходы",
                    color = Color(0xFFD32F2F)
                )

                FinanceStat(
                    modifier = Modifier.weight(0.33f),
                    icon = Icons.Default.Info,
                    value = "75,000 грн",
                    label = "Прибыль",
                    color = Color(0xFF1565C0)
                )
            }
        }
    }
}

@Composable
fun FinanceStat(
    icon: ImageVector,
    value: String,
    label: String,
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
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
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
            value = value,
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )

        AppText(
            value = label,
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.bodySmall
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Preview
@Composable
fun FinancePagePreview() {
    AppTheme {
        FinancePage()
    }
}

@Composable
@Preview
fun FinanceTransactionCardPreview() {
    FinanceTransactionCard(
        transaction = generateSaleOrder()
    )
}

@Composable
fun FinanceTransactionCard(
    modifier: Modifier = Modifier,
    transaction: TypedTransaction,
    onClick: () -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme

    val icon = when (transaction) {
        is Order -> painterResource(R.drawable.product_icon)
        is CashPayment -> painterResource(R.drawable.money_pack)
        is SaleOrder -> painterResource(R.drawable.freight)
        is PaymentOrder -> painterResource(R.drawable.money_pack)
    }

    // Определяем цвет и иконку в зависимости от типа транзакции
    val (transactionColor, transactionIcon, transactionLabel) = when (transaction.type) {
        TransactionType.CREDIT -> Triple(
            statusColor(Status.DONE),
            icon,
            "Доход"
        )

        TransactionType.DEBIT -> Triple(
            statusColor(Status.CREATED),
            icon,
            "Расход"
        )
    }

    // Форматирование суммы с правильным знаком
    val amountText = when (transaction.type) {
        TransactionType.CREDIT -> "+${transaction.amountCurrency.amount.formatTrim()} ${transaction.amountCurrency.currency}"
        TransactionType.DEBIT -> "-${transaction.amountCurrency.amount.formatTrim()} ${transaction.amountCurrency.currency}"
    }

    // Форматирование даты
    val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val formattedDate = dateFormatter.format(transaction.billingDate)

    val elevation by animateDpAsState(
        targetValue = 2.dp,
        label = "cardElevation"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = interactionSource
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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Верхняя часть с ID и суммой
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Круглая иконка типа транзакции
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(transactionColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = transactionIcon,
                            contentDescription = transactionLabel,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        AppText(
                            value = "Транзакция №${transaction.id}",
                            appTextConfig = appTextConfig(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        // Тип транзакции с цветным индикатором
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(transactionColor)
                            )
                            AppText(
                                value = transactionLabel,
                                appTextConfig = appTextConfig(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = transactionColor
                            )
                        }
                    }
                }

                // Сумма транзакции
                SelectionContainer {
                    AppText(
                        value = amountText,
                        appTextConfig = appTextConfig(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = transactionColor
                    )
                }
            }

            HorizontalDivider(
                color = colorScheme.onSurface.copy(alpha = 0.1f)
            )

            // Детали транзакции
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OrderCard(
                    order = generateOrder()
                )
            }
        }
    }
}