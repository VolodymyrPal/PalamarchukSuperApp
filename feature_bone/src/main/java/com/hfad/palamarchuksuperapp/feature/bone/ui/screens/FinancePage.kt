package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateSaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.EqualWidthFlowRow
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.ExchangeOrderCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.OrderCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.SaleCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.ToggleableArrow
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.getNumberOfDecimalDigits
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
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                financeState.financeStatistics
            )
        }
        items(financeState.salesItems) { item ->
            FinanceTransactionCard(
                transaction = item,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
            )
        }
    }
}

@Composable
fun FinanceStatisticCard(
    modifier: Modifier = Modifier,
    financeStatistics: FinanceStatistics,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = MaterialTheme.shapes.extraSmall,
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 25.dp),
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
                modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 4.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )

            val gridItems = listOf(
                AmountCurrency(Currency.UAH, 500f),
                AmountCurrency(Currency.USD, 200000f),
                AmountCurrency(Currency.EUR, 99_999_999_999f),
                AmountCurrency(Currency.BTC, 0.00005f),
            )
            EqualWidthFlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            ) {
                gridItems.forEachIndexed { index, currencyAmount ->
                    FinanceStat(
                        amountCurrency = currencyAmount,
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }
    }
}

@Composable
fun FinanceStat(
    amountCurrency: AmountCurrency,
    modifier: Modifier = Modifier,
) {
    val numOfDigit = getNumberOfDecimalDigits(amountCurrency.currency)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(48.dp, 36.dp)
                .clip(shape = MaterialTheme.shapes.extraSmall),
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
            value = amountCurrency.amount.formatTrim(numOfDigit) + " " + amountCurrency.iconChar,
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
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
        border = BorderStroke(1.dp, colorScheme.secondary),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface
        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
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
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    AppText(
                        value = "${uiTransaction.transactionName} №${uiTransaction.id}",
                        appTextConfig = appTextConfig(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
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
                                    order = transaction,
                                    internalPadding = PaddingValues(8.dp)
                                )
                            }

                            is CashPaymentOrder -> {
//                                CashPaymentCard(
//                                    payment = transaction
//                                )
                            }

                            is SaleOrder -> {
                                SaleCard(
                                    saleItem = transaction,
                                    internalPadding = PaddingValues(8.dp)
                                )
                            }

                            is PaymentOrder -> {
                                PaymentCard(
                                    payment = transaction,
                                    internalPadding = PaddingValues(8.dp)
                                )
                            }

                            is ExchangeOrder -> {
                                ExchangeOrderCard(
                                    exchangeOrder = transaction,
                                    internalPadding = PaddingValues(8.dp)
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
                        .clip(shape = MaterialTheme.shapes.extraSmall),
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
                    modifier = Modifier.padding(4.dp),
                    color = uiTransaction.additionalColor
                )
            }
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
                modifier = Modifier.padding(4.dp),
                color = uiTransaction.color
            )
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
            color = financeStatusColor(this.transactionType),
            transactionType = this.transactionType,
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
            color = financeStatusColor(this.transactionType),
            transactionType = this.transactionType,
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
            color = financeStatusColor(this.transactionType),
            transactionType = this.transactionType,
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
            color = financeStatusColor(this.transactionType),
            transactionType = this.transactionType,
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
            color = financeStatusColor(this.transactionType),
            transactionType = this.transactionType,
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