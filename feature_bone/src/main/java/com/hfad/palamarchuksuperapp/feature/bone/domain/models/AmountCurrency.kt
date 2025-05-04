package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import androidx.compose.ui.graphics.Color
import com.hfad.palamarchuksuperapp.feature.bone.R

/*
 Need to be refactored with separeting presentation and domain layers, as Resources belong to presentation layer
 */
data class AmountCurrency(
    val currency: Currency,
    val amount: Float,
    val iconResource: Int = when (currency) {
        Currency.USD -> R.drawable.usd_sign
        Currency.EUR -> R.drawable.euro_sign
        Currency.CNY -> R.drawable.cny_sign
        Currency.UAH -> R.drawable.uah_sign
        Currency.PLN -> R.drawable.zloty_sign
        Currency.BTC -> R.drawable.btc_sign
        Currency.OTHER -> R.drawable.shekel_sign
    },
    val iconChar: Char = when (currency) {
        Currency.USD -> '$'
        Currency.EUR -> '€'
        Currency.CNY -> '¥'
        Currency.UAH -> '₴'
        Currency.PLN -> 'z'
        Currency.BTC -> '₿'
        Currency.OTHER -> '₪'
    },
    val color: Color = when (currency) {
        Currency.USD -> Color(0xFF4CAF50)
        Currency.EUR -> Color(0xFF2196F3)
        Currency.CNY -> Color(0xFFF44336)
        Currency.UAH -> Color(0xFF3F51B5)
        Currency.PLN -> Color(0xFF9C27B0)
        Currency.BTC -> Color(0xFFFF5722)
        Currency.OTHER -> Color(0xFF9E9E9E)
    },
    val currencyTextRes: Int = when (currency) {
        Currency.USD -> R.string.usd
        Currency.EUR -> R.string.eur
        Currency.CNY -> R.string.yuan
        Currency.UAH -> R.string.uah
        Currency.PLN -> R.string.zloty
        Currency.BTC -> R.string.bitcoin
        Currency.OTHER -> R.string.undefined_currency
    },
)

enum class Currency {
    USD, EUR, CNY, UAH, PLN, BTC, OTHER
}