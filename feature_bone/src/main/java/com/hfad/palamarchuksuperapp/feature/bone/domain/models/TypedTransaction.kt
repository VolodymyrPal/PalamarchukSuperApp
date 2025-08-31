package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import androidx.annotation.StringRes
import com.hfad.palamarchuksuperapp.feature.bone.R
import java.util.Date
import kotlin.reflect.KClass

sealed interface TypedTransaction {
    val type: String
    val transactionType: TransactionType
    val id: Int
    val amountCurrency: AmountCurrency
    val billingDate: Date
    val versionHash: String
}

enum class TransactionType(
    @StringRes val nameStringRes: Int,
) {
    CREDIT(R.string.credit),
    DEBIT(R.string.debit)
}

fun TypedTransaction.getType(): String {
    return when (this) {
        is Order -> "Order"
        is CashPaymentOrder -> "CashPaymentOrder"
        is ExchangeOrder -> "ExchangeOrder"
        is PaymentOrder -> "PaymentOrder"
        is SaleOrder -> "SaleOrder"
    }
}

fun typeApi(type: KClass<out TypedTransaction>, id: String): String {
    return when (type) {
        Order::class -> "Order:$id"
        CashPaymentOrder::class -> "CashPaymentOrder:$id"
        ExchangeOrder::class -> "ExchangeOrder:$id"
        PaymentOrder::class -> "PaymentOrder:$id"
        SaleOrder::class -> "SaleOrder:$id"
        else -> "Unknown:$id"
    }
}
