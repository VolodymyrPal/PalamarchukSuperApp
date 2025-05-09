package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import java.util.Date

sealed interface TypedTransaction {
    val type: TransactionType
    val id: Int
    val amount: Float
    val billingDate: Date
}

enum class TransactionType {
    CREDIT, DEBIT
}