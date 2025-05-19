package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.formatTrim
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateExchangeOrderItems
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExchangeOrderCard(
    exchangeOrder: ExchangeOrder,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    value = stringResource(R.string.exchange_card_title, exchangeOrder.id),
                    appTextConfig = appTextConfig(textStyle = MaterialTheme.typography.titleSmall)
                )

                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.exchange_icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    value = "${exchangeOrder.amountToExchange.amount.formatTrim()} ${exchangeOrder.amountToExchange.iconChar}",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    ),
                    color = exchangeOrder.amountToExchange.color
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                AppText(
                    value = "${exchangeOrder.amountCurrency.amount.formatTrim()} ${exchangeOrder.amountCurrency.iconChar}",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    ),
                    color = exchangeOrder.amountCurrency.color
                )
            }

            val baseNumOfDigit = getNumberOfDecimalDigits(exchangeOrder.amountCurrency.currency)
            AppText(
                value = stringResource(
                    R.string.exchange_rate_detailed,
                    exchangeOrder.amountToExchange.currency.name, // код первой валюты
                    exchangeOrder.amountCurrency.currency.name, // код второй валюты
                    (1 / exchangeOrder.exchangeRate).formatTrim(baseNumOfDigit), // единица второй валюты
                ),
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val numOfDigit = getNumberOfDecimalDigits(exchangeOrder.amountToExchange.currency)
            AppText(
                value = stringResource(
                    R.string.exchange_rate_detailed,
                    exchangeOrder.amountCurrency.currency.name, // код первой валюты
                    exchangeOrder.amountToExchange.currency.name, // код второй валюты
                    exchangeOrder.exchangeRate.formatTrim(numOfDigit), // единица второй валюты
                ),
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                ) {
                    AppText(
                        value = stringResource(R.string.exchange_request_date),
                        appTextConfig = appTextConfig(textStyle = MaterialTheme.typography.bodySmall),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AppText(
                        value = exchangeOrder.date.formatLegacy(),
                        appTextConfig = appTextConfig(textStyle = MaterialTheme.typography.bodyMedium)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    AppText(
                        value = stringResource(R.string.exchange_billing_date),
                        appTextConfig = appTextConfig(textStyle = MaterialTheme.typography.bodySmall),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AppText(
                        value = exchangeOrder.billingDate.formatLegacy(),
                        appTextConfig = appTextConfig(textStyle = MaterialTheme.typography.bodyMedium)
                    )
                }
            }
        }
    }
}

private fun getNumberOfDecimalDigits(
    currency: Currency,
    btcDigits: Int = 10,
    baseDigits: Int = 4,
): Int {
    return when (currency) {
        Currency.BTC -> btcDigits
        else -> baseDigits
    }
}

fun Date.formatLegacy(locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", locale)
    return formatter.format(this)
}

@Preview
@Composable
fun ExchangeOrdersCardPreview() {
    AppTheme {
        Column {
            generateExchangeOrderItems().forEach {
                ExchangeOrderCard(
                    exchangeOrder = it,
                )
            }
        }
    }
}

@Preview
@Composable
fun ExchangeOrdersCardNightPreview() {
    AppTheme (
        useDarkTheme = true
    ) {
        Column {
            generateExchangeOrderItems().forEach {
                ExchangeOrderCard(
                    exchangeOrder = it,
                )
            }
        }
    }
}