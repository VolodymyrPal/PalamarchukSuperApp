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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
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
import com.example.compose.FeatureTheme
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppIconInfoField
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.formatTrim
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.core.ui.theme.Status
import com.hfad.palamarchuksuperapp.core.ui.theme.statusColor
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generatePaymentOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.PaymentPageState
import kotlin.random.Random

@Composable
fun PaymentsPage(
    modifier: Modifier = Modifier,
    paymentPageState: PaymentPageState = generatePaymentSample(), //TODO for test only
) {

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            PaymentsStatisticsCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 25.dp),
                paymentStatistic = paymentPageState.paymentStatistic
            )
        }
        items(paymentPageState.payments) { payment ->
            PaymentCard(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                payment = payment,
                internalPadding = PaddingValues(horizontal = 20.dp, vertical = 25.dp)
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
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = MaterialTheme.shapes.extraSmall,
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
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
                itemsIndexed(gridItems) { index, currencyAmount ->
                    CurrencyStat(
                        modifier = Modifier,
                        amountCurrency = currencyAmount,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
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
                .clip(MaterialTheme.shapes.small)
                .background(amountCurrency.color.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            AppText(
                value = amountCurrency.currency.toString(),
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                ),
                color = amountCurrency.color,
                modifier = Modifier.padding(4.dp)
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

@Composable
fun StatItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: Painter,
) {
    Row(
        modifier = modifier.width(100.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
        )

        Column(
            modifier = Modifier
                .padding(vertical = 2.dp),
            verticalArrangement = Arrangement.Center
        ) {
            AppText(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(IntrinsicSize.Min),
                value = value,
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )

            AppText(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(IntrinsicSize.Min),
                value = title,
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
fun PaymentCard(
    payment: PaymentOrder,
    modifier: Modifier = Modifier,
    internalPadding: PaddingValues = PaddingValues(),
) {
    val statusColor = when (payment.status) {
        PaymentStatus.PAID -> statusColor(Status.DONE)
        PaymentStatus.PENDING -> statusColor(Status.IN_PROGRESS)
        PaymentStatus.OVERDUE -> statusColor(Status.OVERDUE)
    }

    val statusText = when (payment.status) {
        PaymentStatus.PAID -> stringResource(R.string.payment_paid)
        PaymentStatus.PENDING -> stringResource(R.string.payment_pending)
        PaymentStatus.OVERDUE -> stringResource(R.string.payment_overdue)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.extraSmall,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.padding(internalPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    value = stringResource(R.string.payment_payment_card_title, payment.id),
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleSmall
                    )
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    AppText(
                        modifier = Modifier.padding(8.dp),
                        value = statusText,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        ),
                        color = statusColor
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                SelectionContainer {
                    AppText(
                        value = payment.amountCurrency.amount.formatTrim(),
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(payment.amountCurrency.color.copy(alpha = 0.1f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    AppText(
                        value = payment.amountCurrency.currency.toString(),
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        ),
                        color = payment.amountCurrency.color
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )

            AppIconInfoField(
                modifier = Modifier.fillMaxWidth(),
                icon = painterResource(R.drawable.factory_icon),
                iconSize = 40.dp,
                title = stringResource(R.string.factory),
                description = payment.factory,
            )

            AppIconInfoField(
                modifier = Modifier.fillMaxWidth(),
                icon = painterResource(R.drawable.product_icon),
                iconSize = 40.dp,
                title = stringResource(R.string.cargo),
                description = payment.productType,
            )


            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )

            // Dates
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

private fun generatePaymentSample(): PaymentPageState {
    return PaymentPageState(
        payments = generatePaymentOrderItems(),
        paymentStatistic = PaymentStatistic(
            totalPayment = Random.nextInt(10, 20),
            totalReceiver = Random.nextInt(1, 4),
            daysToSend = 1
        )
    )
}

@Preview
@Composable
fun PaymentsPagePreview() {
    FeatureTheme {
        PaymentsPage()
    }
}

@Preview
@Composable
fun PaymentCardPreview() {
    Column {
        FeatureTheme {
            PaymentCard(
                modifier = Modifier.padding(8.dp),
                payment = PaymentOrder(
                    id = Random.nextInt(100, 200),
                    amountCurrency = AmountCurrency(
                        currency = Currency.entries.random(),
                        amount = Random.nextDouble(1000.0, 10000000.0).toFloat()
                    ),
                    factory = "Guangzhou Metal Works",
                    productType = "Металлопрокат",
                    paymentDate = "15.09.2023",
                    dueDate = "15.10.2023",
                    status = PaymentStatus.PAID,
                    type = TransactionType.CREDIT
                )
            )
        }
        FeatureTheme(darkTheme = true) {
            PaymentCard(
                modifier = Modifier.padding(8.dp),
                payment = PaymentOrder(
                    id = Random.nextInt(100, 200),
                    amountCurrency = AmountCurrency(
                        currency = Currency.entries.random(),
                        amount = Random.nextDouble(1000.0, 100000.0).toFloat()
                    ),
                    factory = "Guangzhou Metal Works",
                    productType = "Металлопрокат",
                    paymentDate = "15.09.2023",
                    dueDate = "15.10.2023",
                    status = PaymentStatus.PAID,
                    type = TransactionType.CREDIT
                )
            )
        }
    }
}