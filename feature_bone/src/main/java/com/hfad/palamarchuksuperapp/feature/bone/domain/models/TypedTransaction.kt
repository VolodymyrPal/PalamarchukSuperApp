package com.hfad.palamarchuksuperapp.feature.bone.domain.models

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

enum class TransactionType {
    CREDIT, DEBIT
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