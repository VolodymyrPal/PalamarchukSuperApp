package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import java.util.Date

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