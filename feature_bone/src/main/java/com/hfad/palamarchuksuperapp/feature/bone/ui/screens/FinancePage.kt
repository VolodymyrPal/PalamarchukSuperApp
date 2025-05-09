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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
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
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Payment
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.FinancePageState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun FinancePage(
    modifier: Modifier = Modifier,
    financeState : FinancePageState = FinancePageState(),
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
    financeTransaction: FinanceTransaction
) {
    when (financeTransaction) {
        is Order -> {}
        else -> {}
    }
    Card(
        modifier = Modifier
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
                    icon = Icons.Default.Build,
                    value = "150,000 грн",
                    label = "Доход",
                    color = Color(0xFF2E7D32)
                )

                FinanceStat(
                    icon = Icons.Default.ThumbUp,
                    value = "75,000 грн",
                    label = "Расходы",
                    color = Color(0xFFD32F2F)
                )

                FinanceStat(
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
                fontWeight = FontWeight.Bold
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
        transaction = Order(1, type = TransactionType.DEBIT ),
    )
}

@Composable
fun FinanceTransactionCard(
    modifier: Modifier = Modifier,
    transaction: TypedTransaction,
    onClick: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    // Определяем цвет и иконку в зависимости от типа транзакции
    val (transactionColor, transactionIcon, transactionLabel) = when (transaction.type) {
        TransactionType.CREDIT -> Triple(
            statusColor(Status.DONE),
            Icons.Default.Build,
            "Доход"
        )
        TransactionType.DEBIT -> Triple(
            statusColor(Status.CANCELED),
            Icons.Default.ThumbUp,
            "Расход"
        )
    }

    // Форматирование суммы с правильным знаком
    val amountText = when (transaction.type) {
        TransactionType.CREDIT -> "+${transaction.amount.formatTrim()} грн"
        TransactionType.DEBIT -> "-${transaction.amount.formatTrim()} грн"
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
                            imageVector = transactionIcon,
                            contentDescription = transactionLabel,
                            tint = transactionColor,
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
                // Категория (если доступна для подтипа)
                if (transaction is Payment) {
                    TransactionDetailRow(
                        icon = Icons.Default.Check,
                        label = "Категория",
                        value = "Company name"
                    )
                } else if (transaction is SaleOrder) {
                    TransactionDetailRow(
                        icon = Icons.Default.Edit,
                        label = "Компания",
                        value = transaction.companyName
                    )
                }

                // Дата транзакции
                TransactionDetailRow(
                    icon = Icons.Default.DateRange,
                    label = "Дата",
                    value = formattedDate
                )

                // Дополнительная информация в зависимости от типа
                when (transaction) {
                    is Order -> {
                    }
                    is Payment -> {
                        TransactionDetailRow(
                            icon = Icons.Default.Add,
                            label = "Срок оплаты",
                            value = "Data opl"
                        )
                    }
                    is SaleOrder -> {
                        if (transaction.prepayment) {
                            TransactionDetailRow(
                                icon = Icons.Default.Check,
                                label = "Предоплата",
                                value = "Да",
                                valueColor = colorScheme.tertiary
                            )
                        }
                    }

                    is PaymentOrder -> {}
                }
            }

            // Если есть дополнительная информация для подтипов
            when (transaction) {
                is Order -> {}
                is Payment -> {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = colorScheme.onSurface.copy(alpha = 0.1f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            AppText(
                                value = "Kakayta info",
                                appTextConfig = appTextConfig(
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                ),
                            )
                        }
                    }
                }
                is SaleOrder -> {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = colorScheme.onSurface.copy(alpha = 0.1f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppText(
                            value = "НДС: ${transaction.vatAmount.formatTrim()} грн",
                            appTextConfig = appTextConfig(
                                textStyle = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            ),
                            color = colorScheme.primary
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    when (transaction.status) {
                                        SaleStatus.COMPLETED -> statusColor(Status.DONE)
                                        SaleStatus.IN_PROGRESS -> statusColor(Status.IN_PROGRESS)
                                        SaleStatus.CREATED -> statusColor(Status.CREATED)
                                        SaleStatus.DOCUMENT_PROCEED -> statusColor(Status.PROCEED)
                                        SaleStatus.CANCELED -> statusColor(Status.CANCELED)
                                    }.copy(alpha = 0.1f)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            AppText(
                                value = when (transaction.status) {
                                    SaleStatus.COMPLETED -> "Завершено"
                                    SaleStatus.IN_PROGRESS -> "В процессе"
                                    SaleStatus.CREATED -> "Создано"
                                    SaleStatus.DOCUMENT_PROCEED -> "Документы обработаны"
                                    SaleStatus.CANCELED -> "Отменено"
                                },
                                appTextConfig = appTextConfig(
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = when (transaction.status) {
                                    SaleStatus.COMPLETED -> statusColor(Status.DONE)
                                    SaleStatus.IN_PROGRESS -> statusColor(Status.IN_PROGRESS)
                                    SaleStatus.CREATED -> statusColor(Status.CREATED)
                                    SaleStatus.DOCUMENT_PROCEED -> statusColor(Status.PROCEED)
                                    SaleStatus.CANCELED -> statusColor(Status.CANCELED)
                                }
                            )
                        }
                    }
                }

                is PaymentOrder -> {}
            }
        }
    }
}

@Composable
@Preview
private fun  TransactionDetailRowPreview() {
    TransactionDetailRow(
        icon = Icons.Default.Face,
        label = "Some label",
        value = "Value"
    )
}


@Composable
private fun TransactionDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )

        AppText(
            value = "$label:",
            appTextConfig = appTextConfig(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp)
        )

        AppText(
            value = value,
            appTextConfig = appTextConfig(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            ),
            color = valueColor
        )
    }
}
