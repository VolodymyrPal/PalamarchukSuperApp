package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import java.util.Date
import kotlin.random.Random

data class SaleOrder(
    override val id: Int,
    val productName: String,
    val cargoCategory: String,
    val customerName: String,
    val status: SaleStatus,
    val requestDate: String,
    val documentDate: String,
    val companyName: String,
    val commissionPercent: Double,
    val prepayment: Boolean,
    val order: Order? = null,
    val vat: Float = 0.20f,
    override val amountCurrency: AmountCurrency,
    override val billingDate: Date = Date(),
    override val type: TransactionType = TransactionType.DEBIT,
) : TypedTransaction

enum class SaleStatus {
    CREATED, IN_PROGRESS, DOCUMENT_PROCEED, COMPLETED, CANCELED
}

fun generateSaleOrder(): SaleOrder = SaleOrder(
    id = Random.Default.nextInt(10000, 99999),
    productName = "Офисная мебель",
    cargoCategory = "Мебель",
    customerName = "Иванов И.И.",
    status = SaleStatus.COMPLETED,
    requestDate = "10.03.2024",
    documentDate = "15.03.2024",
    companyName = "ООО «Офис Плюс»",
    commissionPercent = 5.0,
    prepayment = true,
    order = Order(
        id = 1,
        businessEntityNum = 4321,
        num = 48756,
        destinationPoint = "Киев",
        arrivalDate = "20.04.2024",
        departurePoint = "Шанхай",
        cargo = "Офисная мебель",
        manager = "Петров В.П. +380633887542",
        amountCurrency = AmountCurrency(
            currency = Currency.USD,
            amount = 12200f
        )
    ),
    amountCurrency = AmountCurrency(
        currency = Currency.USD,
        amount = 12200f
    ),
    vat = 0.2f
)

fun generateSaleOrderItems(): List<SaleOrder> {
    return listOf(
        SaleOrder(
            id = Random.Default.nextInt(10000, 99999),
            productName = "Офисная мебель",
            cargoCategory = "Мебель",
            customerName = "Иванов И.И.",
            status = SaleStatus.COMPLETED,
            requestDate = "10.03.2024",
            documentDate = "15.03.2024",
            companyName = "ООО «Офис Плюс»",
            commissionPercent = 5.0,
            prepayment = true,
            order = generateOrder(),
            amountCurrency = AmountCurrency(
                currency = Currency.UAH,
                amount = 12200f
            ),
            vat = 0.2f
        ),
        SaleOrder(
            id = Random.Default.nextInt(10000, 99999),
            productName = "Электроника",
            cargoCategory = "Техника",
            customerName = "Смирнов А.В.",
            status = SaleStatus.IN_PROGRESS,
            requestDate = "05.04.2024",
            documentDate = "10.04.2024",
            companyName = "ТОВ «Техноимпорт»",
            commissionPercent = 3.5,
            prepayment = false,
            amountCurrency = AmountCurrency(
                currency = Currency.UAH,
                amount = 12200f
            ),
            vat = 0.2f
        ),
        SaleOrder(
            id = Random.Default.nextInt(10000, 99999),
            productName = "Строительные материалы",
            cargoCategory = "Стройматериалы",
            customerName = "Ковалев Д.И.",
            status = SaleStatus.CREATED,
            requestDate = "15.04.2024",
            documentDate = "20.04.2024",
            companyName = "ООО «СтройМир»",
            commissionPercent = 2.0,
            prepayment = true,
            order = generateOrder(),
            amountCurrency = AmountCurrency(
                currency = Currency.UAH,
                amount = 12200f
            ),
            vat = 0.2f
        )
    )
}