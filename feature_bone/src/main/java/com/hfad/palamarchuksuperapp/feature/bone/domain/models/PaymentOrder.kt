package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import java.util.Date
import kotlin.random.Random

data class PaymentOrder(
    override val type: String = "PaymentOrder",
    override val id: Int,
    val factory: String,
    val productType: String,
    val paymentDate: String,
    val dueDate: String,
    val status: PaymentStatus,
    val commission: Float = 0.0f,
    override val transactionType: TransactionType,
    override val billingDate: Date = Date(),
    override val amountCurrency: AmountCurrency,
    val paymentPrice: AmountCurrency = AmountCurrency(
        currency = amountCurrency.currency,
        amount = 100f
    ),
    override val versionHash: String = ""
) : TypedTransaction {
    val fullPrice: AmountCurrency get() = amountCurrency + amountCurrency * commission + paymentPrice
}

enum class PaymentStatus {
    PAID, PENDING, OVERDUE
}

internal fun generatePaymentOrderSample() : PaymentOrder {
    val factories = listOf(
        "Guangzhou Metal Works", "Berlin Precision Manufacturing",
        "Shanghai Industrial Group", "Warsaw Production Facility",
        "Tokyo Electronics Ltd", "Mumbai Steel Plant", "Detroit Auto Parts"
    )

    val productTypes = listOf(
        "Металлопрокат", "Электроника", "Полупроводники",
        "Автозапчасти", "Сталь", "Микрочипы", "Сырье"
    )
    val isPaid = Random.nextInt(10) > 3
    val isOverdue = !isPaid && Random.nextInt(10) > 5

    val status = when {
        isPaid -> PaymentStatus.PAID
        isOverdue -> PaymentStatus.OVERDUE
        else -> PaymentStatus.PENDING
    }

    val month = Random.nextInt(1, 13)
    val day = Random.nextInt(1, 29)
    val paymentDate = "$day.${month.toString().padStart(2, '0')}.2023"

    val dueMonth = if (month < 12) month + 1 else 1
    val dueYear = if (month < 12) 2023 else 2024
    val dueDate = "$day.${dueMonth.toString().padStart(2, '0')}.$dueYear"

    return PaymentOrder(
        id = Random.nextInt(100, 200),
        amountCurrency = AmountCurrency(
            currency = Currency.entries.random(),
            amount = Random.nextDouble(1000.0, 100000.0).toFloat()
        ),
        factory = factories[Random.nextInt(factories.size)],
        productType = productTypes[Random.nextInt(productTypes.size)],
        paymentDate = paymentDate,
        dueDate = dueDate,
        status = status,
        transactionType = TransactionType.DEBIT
    )
}


internal fun generatePaymentOrderItems(): List<PaymentOrder> {
    val factories = listOf(
        "Guangzhou Metal Works", "Berlin Precision Manufacturing",
        "Shanghai Industrial Group", "Warsaw Production Facility",
        "Tokyo Electronics Ltd", "Mumbai Steel Plant", "Detroit Auto Parts"
    )

    val productTypes = listOf(
        "Металлопрокат", "Электроника", "Полупроводники",
        "Автозапчасти", "Сталь", "Микрочипы", "Сырье"
    )

    return List(6) { index ->

        val isPaid = Random.nextInt(10) > 3
        val isOverdue = !isPaid && Random.nextInt(10) > 5

        val status = when {
            isPaid -> PaymentStatus.PAID
            isOverdue -> PaymentStatus.OVERDUE
            else -> PaymentStatus.PENDING
        }

        val month = Random.nextInt(1, 7)
        val day = Random.nextInt(1, 29)
        val paymentDate = "$day.${month.toString().padStart(2, '0')}.2025"

        val dueMonth = if (month < 12) month + 1 else 1
        val dueYear = if (month < 12) 2025 else 2026
        val dueDate = "$day.${dueMonth.toString().padStart(2, '0')}.$dueYear"

        PaymentOrder(
            id = Random.nextInt(2000, 4000),
            amountCurrency = AmountCurrency(
                currency = Currency.entries.random(),
                amount = Random.nextDouble(1000.0, 100000.0).toFloat()
            ),
            factory = factories[Random.nextInt(factories.size)],
            productType = productTypes[Random.nextInt(productTypes.size)],
            paymentDate = paymentDate,
            dueDate = dueDate,
            status = status,
            transactionType = TransactionType.DEBIT
        )
    }
}