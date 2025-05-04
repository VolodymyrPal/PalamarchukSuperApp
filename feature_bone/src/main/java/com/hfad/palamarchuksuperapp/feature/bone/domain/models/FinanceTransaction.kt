package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import java.util.Date

interface FinanceTransaction {
    val id: Int
    val amount: Float
    val billingDate: Date
}

enum class TransactionType {
    CREDIT, DEBIT
}

interface TypedTransaction {
    val type: TransactionType
    val transaction: FinanceTransaction
}

sealed class Transaction : TypedTransaction {
    // Фабричные методы для создания транзакций
    companion object {
        fun createCredit(transaction: FinanceTransaction): Credit {
            return Credit(transaction)
        }

        fun createDebit(transaction: FinanceTransaction): Debit {
            return Debit(transaction)
        }
    }

    // Кредитовая операция (приход)
    data class Credit(
        override val transaction: FinanceTransaction
    ) : Transaction() {
        override val type: TransactionType = TransactionType.CREDIT
    }

    // Дебетовая операция (расход)
    data class Debit(
        override val transaction: FinanceTransaction
    ) : Transaction() {
        override val type: TransactionType = TransactionType.DEBIT
    }
}