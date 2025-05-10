package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import java.util.Date
import kotlin.random.Random

data class SaleOrder(
    override val id: Int,
    val productName: String,
    val cargoCategory: String,
    val quantity: Int, //Quantity of what?
    val price: Int,
    val totalAmount: Int,
    val vatAmount: Double,
    val customerName: String,
    val status: SaleStatus,
    val requestDate: String,
    val documentDate: String,
    val companyName: String,
    val commissionPercent: Double,
    val prepayment: Boolean,
    val order: Order? = null,
    override val amountCurrency: AmountCurrency,
    override val billingDate: Date = Date(),
    override val type: TransactionType = TransactionType.CREDIT,
) : TypedTransaction

enum class SaleStatus {
    CREATED, IN_PROGRESS, DOCUMENT_PROCEED, COMPLETED, CANCELED
}

fun generateSaleOrder(): SaleOrder = SaleOrder(
    id = Random.Default.nextInt(10000, 99999),
    productName = "Офисная мебель",
    cargoCategory = "Мебель",
    companyName = "ООО «Офис Плюс»",
    quantity = 10,
    price = 2500,
    totalAmount = 2500,
    vatAmount = 5000.0,
    customerName = "Иванов И.И.",
    status = SaleStatus.COMPLETED,
    requestDate = "10.03.2024",
    documentDate = "15.03.2024",
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
    )
)

fun generateSaleOrderItems(): List<SaleOrder> {
    return listOf(
        SaleOrder(
            id = Random.Default.nextInt(10000, 99999),
            productName = "Офисная мебель",
            cargoCategory = "Мебель",
            companyName = "ООО «Офис Плюс»",
            quantity = 10,
            price = 2500,
            totalAmount = 2500,
            vatAmount = 5000.0,
            customerName = "Иванов И.И.",
            status = SaleStatus.COMPLETED,
            requestDate = "10.03.2024",
            documentDate = "15.03.2024",
            commissionPercent = 5.0,
            prepayment = true,
            order = generateOrder(),
            amountCurrency = AmountCurrency(
                currency = Currency.USD,
                amount = 12200f
            )
        ),
        SaleOrder(
            id = Random.Default.nextInt(10000, 99999),
            productName = "Электроника",
            cargoCategory = "Техника",
            companyName = "ТОВ «Техноимпорт»",
            quantity = 50,
            price = 1200,
            totalAmount = 60000,
            vatAmount = 12000.0,
            customerName = "Смирнов А.В.",
            status = SaleStatus.IN_PROGRESS,
            requestDate = "05.04.2024",
            documentDate = "10.04.2024",
            commissionPercent = 3.5,
            prepayment = false,
            order = null,
            amountCurrency = AmountCurrency(
                currency = Currency.USD,
                amount = 12200f
            )
        ),
        SaleOrder(
            id = Random.Default.nextInt(10000, 99999),
            productName = "Строительные материалы",
            cargoCategory = "Стройматериалы",
            companyName = "ООО «СтройМир»",
            quantity = 200,
            price = 450,
            totalAmount = 90000,
            vatAmount = 18000.0,
            customerName = "Ковалев Д.И.",
            status = SaleStatus.CREATED,
            requestDate = "15.04.2024",
            documentDate = "20.04.2024",
            commissionPercent = 2.0,
            prepayment = true,
            order = generateOrder(),
            amountCurrency = AmountCurrency(
                currency = Currency.USD,
                amount = 12200f
            )
        )
    )
}