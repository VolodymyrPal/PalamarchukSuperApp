package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.FeatureTheme
import com.example.compose.financeStatusColor
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.formatTrim
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.CashPaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateSaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.ExchangeOrderCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.OrderCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.SaleCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.ToggleableArrow
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.FinancePageState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun FinancePage(
    modifier: Modifier = Modifier,
    financeState: FinancePageState = FinancePageState(),
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
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
        shape = RoundedCornerShape(1.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppText(
                value = stringResource(R.string.currency_balance),
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
                    amountCurrency = AmountCurrency(
                        currency = Currency.UAH,
                        amount = 150000f,
                    ),
                    label = "Доход",
                    color = Color(0xFF2E7D32)
                )

                FinanceStat(
                    modifier = Modifier.weight(0.33f),
                    icon = Icons.Default.ThumbUp,
                    amountCurrency = AmountCurrency(
                        currency = Currency.USD,
                        amount = -12000f,
                    ),
                    value = "75,000 грн",
                    label = "Расходы",
                    color = Color(0xFFD32F2F)
                )

                FinanceStat(
                    modifier = Modifier.weight(0.33f),
                    icon = Icons.Default.Info,
                    amountCurrency = AmountCurrency(
                        currency = Currency.EUR,
                        amount = 200f,
                    ),
                    value = "75,000 грн",
                    label = "Прибыль",
                    color = Color(0xFF1565C0)
                )

                FinanceStat(
                    modifier = Modifier.weight(0.33f),
                    icon = Icons.Default.Info,
                    amountCurrency = AmountCurrency(
                        currency = Currency.BTC,
                        amount = 200f,
                    ),
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
    amountCurrency: AmountCurrency,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val painter = painterResource(amountCurrency.currencyCountry)
            Image(
                painter,
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.matchParentSize()
            )
        }

        AppText(
            value = amountCurrency.amount.formatTrim() + " " + amountCurrency.iconChar,
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Preview
@Composable
fun FinancePagePreview() {
    FeatureTheme {
        FinancePage()
    }
}

@Composable
@Preview
fun FinanceTransactionCardPreview() {
    FeatureTheme {
        FinanceTransactionCard(
            transaction = generateSaleOrder()
        )
    }
}

@Composable
fun FinanceTransactionCard(
    modifier: Modifier = Modifier,
    transaction: TypedTransaction,
) {
    val colorScheme = MaterialTheme.colorScheme
    val uiTransaction: TransactionUiModel = transaction.toUiModel()
    val isExpanded = remember { mutableStateOf(false) }

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
        shape = RoundedCornerShape(2.dp),
        border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.3f)),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface
        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
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

                BaseDescription(
                    modifier = Modifier.padding(bottom = 4.dp),
                    uiTransaction = uiTransaction
                )
            }
            AnimatedVisibility(
                visible = isExpanded.value,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 8.dp, top = 8.dp),
                ) {
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

                            is ExchangeOrder -> {
                                ExchangeOrderCard(
                                    exchangeOrder = transaction
                                )
                            }
                        }
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .clickable(
                        onClick = { isExpanded.value = !isExpanded.value },
                        indication = null,
                        interactionSource = interactionSource
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
//                Icon(
//                    Icons.Filled.KeyboardArrowDown,
//                    contentDescription = if (isExpanded.value) "Свернуть" else "Развернуть",
//                    modifier = Modifier
//                        .size(16.dp)
//                        .rotate(arrowRotationDegree)
//                )
                ToggleableArrow(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape),
                    isOpen = isExpanded.value,
                    onToggle = { isExpanded.value = !isExpanded.value },
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
        }
    }
}

@Composable
fun BaseDescription(
    modifier: Modifier = Modifier,
    uiTransaction: TransactionUiModel,
) {
    SelectionContainer {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (uiTransaction.additionalAmount != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            uiTransaction.additionalColor.copy(
                                alpha = 0.4f
                            )
                        )
                        .padding(4.dp)
                ) {
                    val additionalValue =
                        if (uiTransaction.additionalType == TransactionType.CREDIT) {
                            "+ ${uiTransaction.additionalAmount.amount.formatTrim()} ${uiTransaction.additionalAmount.currency}"
                        } else {
                            "- ${uiTransaction.additionalAmount.amount.formatTrim()} ${uiTransaction.additionalAmount.currency}"
                        }
                    AppText(
                        value = additionalValue,
                        appTextConfig = appTextConfig(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        uiTransaction.color.copy(
                            alpha = 0.4f
                        )
                    )
                    .padding(4.dp)
            ) {
                val value = if (uiTransaction.transactionType == TransactionType.CREDIT) {
                    "+ ${uiTransaction.amountText.amount.formatTrim()} ${uiTransaction.amountText.currency}"
                } else {
                    "- ${uiTransaction.amountText.amount.formatTrim()} ${uiTransaction.amountText.currency}"
                }
                AppText(
                    value = value,
                    appTextConfig = appTextConfig(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(4.dp)
                )
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
    val additionalType: TransactionType = TransactionType.DEBIT,
    val additionalColor: Color = Color.Transparent,
    val date: String,
    val id: String,
)

@Composable
fun TypedTransaction.toUiModel(): TransactionUiModel = when (this) {
    is Order -> {
        TransactionUiModel(
            iconRes = R.drawable.product_icon,
            color = financeStatusColor(this.type),
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
            color = financeStatusColor(this.type),
            transactionType = this.type,
            amountText = this.amountCurrency,
            date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                this.billingDate
            ),
            additionalType = this.typeToChange,
            additionalAmount = this.amountToExchange,
            additionalColor = financeStatusColor(this.typeToChange),
            id = this.id.toString(),
            transactionName = stringResource(R.string.exchange),
        )
    }

    is CashPaymentOrder -> {
        TransactionUiModel(
            iconRes = R.drawable.money_pack,
            color = financeStatusColor(this.type),
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
            color = financeStatusColor(this.type),
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
            color = financeStatusColor(this.type),
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