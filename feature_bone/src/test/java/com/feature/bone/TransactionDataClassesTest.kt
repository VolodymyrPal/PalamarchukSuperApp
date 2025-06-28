package com.feature.bone

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.CargoType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.CashPaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ServiceOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ServiceType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNotNull
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TransactionDataClassesTest {

    private lateinit var testDate: Date
    private lateinit var testAmountCurrency: AmountCurrency
    private lateinit var testOrder: Order

    @BeforeEach
    fun setUp() {
        testDate = Date()
        testAmountCurrency = AmountCurrency(Currency.USD, 1000f)
        testOrder = Order(
            id = 1, businessEntityNum = 1, num = 1, destinationPoint = "",
            arrivalDate = testDate, departurePoint = "", cargo = "", manager = "", billingDate = testDate
        )
    }

    @Test
    fun `Order should have correct default values`() {

        assertTrue(testOrder is TypedTransaction)

        assertTrue(testOrder.serviceList.isEmpty())
        assertEquals(OrderStatus.CREATED, testOrder.status)
        assertEquals(CargoType.ANY, testOrder.cargoType)
        assertTrue(testOrder.containerNumber.isBlank())
        assertEquals(Currency.USD, testOrder.amountCurrency.currency)
        assertEquals(0f, testOrder.amountCurrency.amount)
        assertEquals(TransactionType.DEBIT, testOrder.type)
    }

    @Test
    fun `Order should determine cargoType based on serviceList`() {
        val airService = ServiceOrder(serviceType = ServiceType.AIR_FREIGHT)
        val fullService = ServiceOrder(serviceType = ServiceType.FULL_FREIGHT)
        val europeService = ServiceOrder(serviceType = ServiceType.EUROPE_TRANSPORT)

        val airOrder = testOrder.copy(serviceList = listOf(airService))
        assertEquals(CargoType.AIR, airOrder.cargoType)

        val containerOrder = testOrder.copy(serviceList = listOf(fullService))
        assertEquals(CargoType.CONTAINER, containerOrder.cargoType)

        val truckOrder = testOrder.copy(serviceList = listOf(europeService))
        assertEquals(CargoType.TRUCK, truckOrder.cargoType)
    }

    @Test
    fun `CashPaymentOrder should have correct default values`() {

        val cashOrder = CashPaymentOrder(
            id = 1, paymentNum = 12345, paymentSum = 500f,
            paymentDateCreation = testDate, billingDate = testDate
        )

        assertTrue(cashOrder is TypedTransaction)
        assertEquals(cashOrder.type, TransactionType.CREDIT)
        assertEquals(cashOrder.amountCurrency.currency, Currency.USD)
        assertEquals(cashOrder.amountCurrency.amount, 0f)
    }

    @Test
    fun `ExchangeOrder should calculate exchange rate correctly`() {
        val amountToExchange = AmountCurrency(Currency.USD, 100f)
        val targetAmount = AmountCurrency(Currency.EUR, 85f)

        val exchangeOrder = ExchangeOrder(
            id = 3,
            amountToExchange = amountToExchange,
            amountCurrency = targetAmount,
            date = testDate,
            billingDate = testDate
        )

        val expectedRate = 100f / 85f
        assertEquals(expectedRate, exchangeOrder.exchangeRate, 0.001f)
        assertEquals(TransactionType.CREDIT, exchangeOrder.type)
        assertEquals(TransactionType.DEBIT, exchangeOrder.typeToChange)
    }

    @Test
    fun `ExchangeOrder should implement TypedTransaction interface`() {
        val exchangeOrder = ExchangeOrder(
            id = 4,
            amountToExchange = testAmountCurrency,
            amountCurrency = testAmountCurrency,
            date = testDate,
            billingDate = testDate
        )

        assertTrue(exchangeOrder is TypedTransaction)
        assertEquals(4, exchangeOrder.id)
        assertEquals(testAmountCurrency, exchangeOrder.amountCurrency)
        assertEquals(testDate, exchangeOrder.billingDate)
        assertEquals(TransactionType.CREDIT, exchangeOrder.type)
    }

    @Test
    fun `PaymentOrder should calculate fullPrice correctly with commission`() {
        val baseAmount = AmountCurrency(Currency.USD, 1000f)
        val commission = 0.05f // 5%

        val paymentOrder = PaymentOrder(
            id = 5,
            factory = "Test Factory",
            productType = "Electronics",
            paymentDate = "2025-06-01",
            dueDate = "2025-07-01",
            status = PaymentStatus.PENDING,
            commission = commission,
            type = TransactionType.DEBIT,
            amountCurrency = baseAmount
        )

        val expectedFullPrice = 1000f + (1000f * 0.05f) + 100f // base + commission + payment price
        assertEquals(expectedFullPrice, paymentOrder.fullPrice.amount, 0.001f)
        assertEquals(Currency.USD, paymentOrder.fullPrice.currency)
    }

    @Test
    fun `PaymentOrder should have correct default values`() {
        val paymentOrder = PaymentOrder(
            id = 6,
            factory = "Factory",
            productType = "Product",
            paymentDate = "2025-06-01",
            dueDate = "2025-07-01",
            status = PaymentStatus.PAID,
            type = TransactionType.CREDIT,
            amountCurrency = testAmountCurrency
        )

        assertEquals(0.0f, paymentOrder.commission)
        assertEquals(100f, paymentOrder.paymentPrice.amount)
        assertEquals(testAmountCurrency.currency, paymentOrder.paymentPrice.currency)
    }

    @Test
    fun `PaymentOrder should implement TypedTransaction interface`() {
        val paymentOrder = PaymentOrder(
            id = 7,
            factory = "Test Factory",
            productType = "Test Product",
            paymentDate = "2025-06-01",
            dueDate = "2025-07-01",
            status = PaymentStatus.OVERDUE,
            type = TransactionType.DEBIT,
            amountCurrency = testAmountCurrency
        )

        assertTrue(paymentOrder is TypedTransaction)
        assertEquals(7, paymentOrder.id)
        assertEquals(testAmountCurrency, paymentOrder.amountCurrency)
        assertEquals(TransactionType.DEBIT, paymentOrder.type)
    }

    @Test
    fun `SaleOrder should have correct default values`() {
        val saleOrder = SaleOrder(
            id = 8,
            productName = "Test Product",
            cargoCategory = "Electronics",
            customerName = "Test Customer",
            status = SaleStatus.CREATED,
            requestDate = "2025-06-01",
            documentDate = "2025-06-02",
            companyName = "Test Company",
            commissionPercent = 5.0,
            prepayment = true,
            amountCurrency = testAmountCurrency
        )

        assertNull(saleOrder.order)
        assertEquals(0.20f, saleOrder.vat)
        assertEquals(TransactionType.DEBIT, saleOrder.type)
    }

    @Test
    fun `SaleOrder should accept Order reference`() {
        val order = generateOrder()
        val saleOrder = SaleOrder(
            id = 9,
            productName = "Product with Order",
            cargoCategory = "Category",
            customerName = "Customer",
            status = SaleStatus.IN_PROGRESS,
            requestDate = "2025-06-01",
            documentDate = "2025-06-02",
            companyName = "Company",
            commissionPercent = 3.5,
            prepayment = false,
            order = order,
            amountCurrency = testAmountCurrency
        )

        assertNotNull(saleOrder.order)
        assertEquals(100, saleOrder.order?.id)
    }

    @Test
    fun `SaleOrder should implement TypedTransaction interface`() {
        val saleOrder = SaleOrder(
            id = 10,
            productName = "Interface Test Product",
            cargoCategory = "Test Category",
            customerName = "Test Customer",
            status = SaleStatus.COMPLETED,
            requestDate = "2025-06-01",
            documentDate = "2025-06-02",
            companyName = "Test Company",
            commissionPercent = 2.5,
            prepayment = true,
            amountCurrency = testAmountCurrency
        )

        assertTrue(saleOrder is TypedTransaction)
        assertEquals(10, saleOrder.id)
        assertEquals(testAmountCurrency, saleOrder.amountCurrency)
        assertEquals(TransactionType.DEBIT, saleOrder.type)
    }

    @Test
    fun `OrderStatus enum should have all expected values`() {
        val statuses = OrderStatus.entries.toTypedArray()
        assertEquals(4, statuses.size)
        assertTrue(statuses.contains(OrderStatus.CREATED))
        assertTrue(statuses.contains(OrderStatus.CALCULATED))
        assertTrue(statuses.contains(OrderStatus.IN_PROGRESS))
        assertTrue(statuses.contains(OrderStatus.DONE))
    }

    @Test
    fun `CargoType enum should have all expected values`() {
        val cargoTypes = CargoType.entries.toTypedArray()
        assertEquals(4, cargoTypes.size)
        assertTrue(cargoTypes.contains(CargoType.ANY))
        assertTrue(cargoTypes.contains(CargoType.CONTAINER))
        assertTrue(cargoTypes.contains(CargoType.TRUCK))
        assertTrue(cargoTypes.contains(CargoType.AIR))
    }

    @Test
    fun `PaymentStatus enum should have all expected values`() {
        val paymentStatuses = PaymentStatus.entries.toTypedArray()
        assertEquals(3, paymentStatuses.size)
        assertTrue(paymentStatuses.contains(PaymentStatus.PAID))
        assertTrue(paymentStatuses.contains(PaymentStatus.PENDING))
        assertTrue(paymentStatuses.contains(PaymentStatus.OVERDUE))
    }

    @Test
    fun `SaleStatus enum should have all expected values`() {
        val saleStatuses = SaleStatus.entries.toTypedArray()
        assertEquals(5, saleStatuses.size)
        assertTrue(saleStatuses.contains(SaleStatus.CREATED))
        assertTrue(saleStatuses.contains(SaleStatus.IN_PROGRESS))
        assertTrue(saleStatuses.contains(SaleStatus.DOCUMENT_PROCEED))
        assertTrue(saleStatuses.contains(SaleStatus.COMPLETED))
        assertTrue(saleStatuses.contains(SaleStatus.CANCELED))
    }

    @Test
    fun `TransactionType enum should have all expected values`() {
        val transactionTypes = TransactionType.values()
        assertEquals(2, transactionTypes.size)
        assertTrue(transactionTypes.contains(TransactionType.CREDIT))
        assertTrue(transactionTypes.contains(TransactionType.DEBIT))
    }

    @Test
    fun `All transaction classes should implement TypedTransaction`() {
        val order = generateOrder()
        val cashOrder = CashPaymentOrder(
            id = 0,
            paymentNum = 0,
            paymentSum = 0f,
            paymentDateCreation = testDate,
            billingDate = testDate
        )
        val exchangeOrder = ExchangeOrder(
            testAmountCurrency,
            date = testDate,
            amountCurrency = testAmountCurrency,
            billingDate = testDate,
            id = 1
        )
        val paymentOrder = PaymentOrder(
            1,
            "Factory",
            "Product",
            "2025-06-01",
            "2025-07-01",
            PaymentStatus.PAID,
            type = TransactionType.DEBIT,
            amountCurrency = testAmountCurrency
        )
        val saleOrder = SaleOrder(
            1,
            "Product",
            "Category",
            "Customer",
            SaleStatus.CREATED,
            "2025-06-01",
            "2025-06-02",
            "Company",
            5.0,
            false,
            amountCurrency = testAmountCurrency
        )

        assertTrue(order is TypedTransaction)
        assertTrue(cashOrder is TypedTransaction)
        assertTrue(exchangeOrder is TypedTransaction)
        assertTrue(paymentOrder is TypedTransaction)
        assertTrue(saleOrder is TypedTransaction)
    }
}