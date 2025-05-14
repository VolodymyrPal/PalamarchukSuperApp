package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.CashPaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateSaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.OrderCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.SaleCard
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
                FinanceTransactionCard(
                    transaction = item,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                )
            }
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
    val uiTransaction: TransactionUiModel = transaction.toUiModel()
    val isExpanded = remember { mutableStateOf(false) }
    val arrowRotationDegree by animateFloatAsState(
        targetValue = if (isExpanded.value) 180f else 0f,
        label = "arrowRotation"
    )

    val elevation by animateDpAsState(
        targetValue = 2.dp,
        label = "cardElevation"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = { isExpanded.value = !isExpanded.value },
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
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(0.8f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(uiTransaction.iconRes),
                        contentDescription = uiTransaction.transactionName,
                        tint = colorScheme.primary,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                    AppText(
                        value = "${uiTransaction.transactionName} №${uiTransaction.id}",
                        appTextConfig = appTextConfig(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                SelectionContainer {
                    when (uiTransaction.transactionType) {
                        TransactionType.CREDIT -> {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(uiTransaction.color.copy(alpha = 0.2f))
                                    .padding(4.dp)
                            ) {
                                AppText(
                                    value = "+ ${uiTransaction.amountText.amount.formatTrim()} ${uiTransaction.amountText.currency}",
                                    appTextConfig = appTextConfig(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                )
                            }
                        }

                        TransactionType.DEBIT -> {
                            if (transaction is ExchangeOrder) {
                                Column(
                                    modifier = Modifier,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (uiTransaction.additionalAmount != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    uiTransaction.color.copy(
                                                        alpha = 0.2f
                                                    )
                                                )
                                                .padding(4.dp)
                                        ) {
                                            Row {
                                                AppText(
                                                    value = "- ${uiTransaction.additionalAmount.amount.formatTrim()} ",
                                                    appTextConfig = appTextConfig(
                                                        fontSize = 15.sp,
                                                    ),
                                                )

                                                AppText(
                                                    value = "${uiTransaction.additionalAmount.currency}",
                                                    appTextConfig = appTextConfig(
                                                        fontSize = 15.sp,
                                                    ),
                                                )
                                            }
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                uiTransaction.additionalColor.copy(
                                                    alpha = 0.2f
                                                )
                                            )
                                            .padding(4.dp)
                                    ) {
                                        Row(

                                        ) {
                                            AppText(
                                                value = "+ ${uiTransaction.amountText.amount.formatTrim()} ",
                                                appTextConfig = appTextConfig(
                                                    fontSize = 15.sp,
                                                ),
                                            )

                                            AppText(
                                                value = "${uiTransaction.amountText.currency}",
                                                appTextConfig = appTextConfig(
                                                    fontSize = 15.sp,
                                                ),
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            uiTransaction.color.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                        .padding(4.dp)
                                ) {
                                    AppText(
                                        value = "- ${uiTransaction.amountText.amount.formatTrim()} ${uiTransaction.amountText.currency}",
                                        appTextConfig = appTextConfig(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .clip(CircleShape)
                    .clickable(
                        onClick = { isExpanded.value = !isExpanded.value },
                    ),
            ) {
                AppText(
                    value = uiTransaction.date,
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    ),
                    color = colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier
                )
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded.value) "Свернуть" else "Развернуть",
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(arrowRotationDegree)
                )
                AppText(
                    value = uiTransaction.date,
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    ),
                    color = colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier
                )
            }

            AnimatedVisibility(
                visible = isExpanded.value,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    HorizontalDivider(
                        color = colorScheme.onSurface.copy(alpha = 0.1f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (transaction) {
                            is Order -> {
                                OrderCard(
                                    order = transaction
                                )
                            }

                            is CashPaymentOrder -> {
//                                CashPaymentCard(
//                                    payment = transaction
//                                )
                            }

                            is SaleOrder -> {
                                SaleCard(
                                    saleItem = transaction
                                )
                            }

                            is PaymentOrder -> {
                                PaymentCard(
                                    payment = transaction
                                )
                            }

                            is ExchangeOrder -> {}
                        }
                    }
                }
            }
        }
    }
}

data class TransactionUiModel(
    @DrawableRes val iconRes: Int,
    val transactionName: String,
    val color: Color,
    val transactionType: TransactionType = TransactionType.DEBIT,
    val amountText: AmountCurrency,
    val additionalAmount: AmountCurrency? = null,
    val additionalColor: Color = Color.Transparent,
    val date: String,
    val id: String,
)

@Composable
fun TypedTransaction.toUiModel(): TransactionUiModel = when (this) {
    is Order -> {
        TransactionUiModel(
            iconRes = R.drawable.product_icon,
            color = if (this.type == TransactionType.CREDIT) statusColor(Status.DONE)
            else statusColor(Status.CREATED),
            transactionType = this.type,
            amountText = this.amountCurrency,
            date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                this.billingDate
            ),
            id = this.id.toString(),
            transactionName = stringResource(R.string.order),
        )
    }

    is ExchangeOrder -> {
        TransactionUiModel(
            iconRes = R.drawable.exchange_icon,
            color = if (this.type == TransactionType.CREDIT) statusColor(Status.DONE)
            else statusColor(Status.CREATED),
            transactionType = this.type,
            amountText = this.amountCurrency,
            date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                this.billingDate
            ),
            additionalAmount = this.amountToExchange,
            additionalColor = statusColor(Status.DONE),
            id = this.id.toString(),
            transactionName = stringResource(R.string.exchange),
        )
    }

    is CashPaymentOrder -> {
        TransactionUiModel(
            iconRes = R.drawable.money_pack,
            color = if (this.type == TransactionType.CREDIT) statusColor(Status.DONE)
            else statusColor(Status.CREATED),
            transactionType = this.type,
            amountText = this.amountCurrency,
            date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                this.billingDate
            ),
            id = this.id.toString(),
            transactionName = stringResource(R.string.cashPayment),
        )
    }

    is SaleOrder -> {
        TransactionUiModel(
            iconRes = R.drawable.freight,
            color = if (this.type == TransactionType.CREDIT) statusColor(Status.DONE)
            else statusColor(Status.CREATED),
            transactionType = this.type,
            amountText = this.amountCurrency,
            date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                this.billingDate
            ),
            id = this.id.toString(),
            transactionName = stringResource(R.string.sale),
        )
    }

    is PaymentOrder -> {
        TransactionUiModel(
            iconRes = R.drawable.factory_icon,
            color = if (this.type == TransactionType.CREDIT) statusColor(Status.DONE)
            else statusColor(Status.CREATED),
            transactionType = this.type,
            amountText = this.amountCurrency,
            date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                this.billingDate
            ),
            id = this.id.toString(),
            transactionName = stringResource(R.string.payment),
        )
    }
}


//@Composable
//fun FinanceCard(
//    modifier: Modifier = Modifier,
//    financeTransaction: TypedTransaction,
//) {
//    Card(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface
//        )
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(4.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                AppText(
//                    value = "Транзакция #${1000 + financeTransaction.id}",
//                    appTextConfig = appTextConfig(
//                        textStyle = MaterialTheme.typography.titleSmall
//                    )
//                )
//                AppText(
//                    value = "${financeTransaction.id * 1500 + 5000} грн",
//                    appTextConfig = appTextConfig(
//                        textStyle = MaterialTheme.typography.titleSmall,
//                    ),
//                    color = if (financeTransaction.id % 2 == 0) MaterialTheme.colorScheme.primary else
//                        MaterialTheme.colorScheme.error
//                )
//            }
//
//            AppText(
//                value = "Тип: ${if (financeTransaction.id % 2 == 0) "Доход" else "Расход"}",
//                appTextConfig = appTextConfig(
//                    textStyle = MaterialTheme.typography.bodyMedium
//                )
//            )
//
//            AppText(
//                value = "Дата: 10.${financeTransaction.id + 1}.2023",
//                appTextConfig = appTextConfig(
//                    textStyle = MaterialTheme.typography.bodySmall,
//                ),
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//        }
//    }
//}