package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import java.util.Date

sealed interface TypedTransaction {
    val type: TransactionType
    val id: Int
    val amountCurrency: AmountCurrency
    val billingDate: Date
}

enum class TransactionType {
    CREDIT, DEBIT
}