package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.hfad.palamarchuksuperapp.feature.bone.R

/*
 Need to be refactored with separeting presentation and domain layers, as Resources belong to presentation layer
 */
data class AmountCurrency(
    val currency: Currency,
    val amount: Float,
) {
    @get: DrawableRes
    val iconResource: Int = when (currency) {
        Currency.USD -> R.drawable.usd_sign
        Currency.EUR -> R.drawable.euro_sign
        Currency.CNY -> R.drawable.cny_sign
        Currency.UAH -> R.drawable.uah_sign
        Currency.PLN -> R.drawable.zloty_sign
        Currency.BTC -> R.drawable.btc_sign
        Currency.OTHER -> R.drawable.shekel_sign
    }

    val iconChar: Char = when (currency) {
        Currency.USD -> '$'
        Currency.EUR -> '€'
        Currency.CNY -> '¥'
        Currency.UAH -> '₴'
        Currency.PLN -> 'z'
        Currency.BTC -> '₿'
        Currency.OTHER -> '₪'
    }

    val color: Color = when (currency) {
        Currency.USD -> Color(0xFF4CAF50)
        Currency.EUR -> Color(0xFF2196F3)
        Currency.CNY -> Color(0xFFF44336)
        Currency.UAH -> Color(0xFF3F51B5)
        Currency.PLN -> Color(0xFF9C27B0)
        Currency.BTC -> Color(0xFFFF5722)
        Currency.OTHER -> Color(0xFF9E9E9E)
    }

    @get:StringRes
    val currencyTextRes: Int = when (currency) {
        Currency.USD -> R.string.usd
        Currency.EUR -> R.string.eur
        Currency.CNY -> R.string.yuan
        Currency.UAH -> R.string.uah
        Currency.PLN -> R.string.zloty
        Currency.BTC -> R.string.bitcoin
        Currency.OTHER -> R.string.undefined_currency
    }

    @get:DrawableRes
    val currencyCountry: Int = when (currency) {
        Currency.USD -> R.drawable.usa
        Currency.EUR -> R.drawable.europe
        Currency.CNY -> R.drawable.china
        Currency.UAH -> R.drawable.ukraine
        Currency.PLN -> R.drawable.poland
        Currency.BTC -> R.drawable.bitcoin
        Currency.OTHER -> R.drawable.world
    }
}

enum class Currency {
    USD, EUR, CNY, UAH, PLN, BTC, OTHER
}

operator fun AmountCurrency.plus(other: AmountCurrency): AmountCurrency {
    require(this.currency == other.currency) { "Currencies must match for arithmetic operations" }
    return this.copy(amount = this.amount + other.amount)
}

operator fun AmountCurrency.minus(other: AmountCurrency): AmountCurrency {
    require(this.currency == other.currency) { "Currencies must match for arithmetic operations" }
    return this.copy(amount = this.amount - other.amount)
}

operator fun AmountCurrency.times(other: AmountCurrency): AmountCurrency {
    require(this.currency == other.currency) { "Currencies must match for arithmetic operations" }
    return this.copy(amount = this.amount * other.amount)
}

operator fun AmountCurrency.div(other: AmountCurrency): AmountCurrency {
    require(this.currency == other.currency) { "Currencies must match for arithmetic operations" }
    require(other.amount != 0f) { "Cannot divide by zero" }
    return this.copy(amount = this.amount / other.amount)
}

operator fun AmountCurrency.plus(value: Number): AmountCurrency {
    return this.copy(amount = this.amount + value.toFloat())
}

operator fun AmountCurrency.minus(value: Number): AmountCurrency {
    return this.copy(amount = this.amount - value.toFloat())
}

operator fun AmountCurrency.times(value: Number): AmountCurrency {
    return this.copy(amount = this.amount * value.toFloat())
}

operator fun AmountCurrency.div(value: Number): AmountCurrency {
    require(value.toFloat() != 0f) { "Cannot divide by zero" }
    return this.copy(amount = this.amount / value.toFloat())
}

operator fun Number.plus(amountCurrency: AmountCurrency): AmountCurrency {
    return amountCurrency.copy(amount = this.toFloat() + amountCurrency.amount)
}

operator fun Number.minus(amountCurrency: AmountCurrency): AmountCurrency {
    return AmountCurrency(
        currency = amountCurrency.currency,
        amount = this.toFloat() - amountCurrency.amount
    )
}

operator fun Number.times(amountCurrency: AmountCurrency): AmountCurrency {
    return amountCurrency.copy(amount = this.toFloat() * amountCurrency.amount)
}

operator fun Number.div(amountCurrency: AmountCurrency): AmountCurrency {
    require(amountCurrency.amount != 0f) { "Cannot divide by zero" }
    return AmountCurrency(
        currency = amountCurrency.currency,
        amount = this.toFloat() / amountCurrency.amount
    )
}

operator fun AmountCurrency.unaryMinus(): AmountCurrency {
    return this.copy(amount = -this.amount)
}

operator fun AmountCurrency.unaryPlus(): AmountCurrency {
    return this
}

operator fun AmountCurrency.compareTo(other: AmountCurrency): Int {
    require(this.currency == other.currency) { "Currencies must match for comparison" }
    return this.amount.compareTo(other.amount)
}