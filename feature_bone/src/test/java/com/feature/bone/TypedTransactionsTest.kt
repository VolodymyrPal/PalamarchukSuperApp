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
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OrderDataClassesTest {

    private lateinit var testDate: Date
    private lateinit var testAmountCurrency: AmountCurrency
    private lateinit var testOrder: Order

    @BeforeEach
    fun setUp() {
        testDate = Date()
        testAmountCurrency = AmountCurrency(Currency.USD, 1000f)
        testOrder = Order(
            id = 1,
            businessEntityNum = 1,
            num = 1,
            destinationPoint = "",
            arrivalDate = testDate,
            departurePoint = "",
            cargo = "",
            manager = "",
            billingDate = testDate
        )
    }

    @Test
    fun `Order should have correct default values`() {


        assertTrue(testOrder.serviceList.isEmpty())
        assertEquals(OrderStatus.CREATED, testOrder.status)
        assertEquals(CargoType.ANY, testOrder.cargoType)
        assertTrue(testOrder.containerNumber.isBlank())
        assertEquals(Currency.USD, testOrder.amountCurrency.currency)
        assertEquals(0f, testOrder.amountCurrency.amount)
        assertEquals(TransactionType.DEBIT, testOrder.transactionType)
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
}

class CashPaymentOrderTest {

    private lateinit var testDate: Date
    private lateinit var cashOrder: CashPaymentOrder

    @BeforeEach
    fun setUp() {
        testDate = Date()
        cashOrder = CashPaymentOrder(
            id = 1,
            paymentNum = 12345,
            paymentSum = 500f,
            paymentDateCreation = testDate,
            billingDate = testDate
        )
    }

    @Test
    fun `CashPaymentOrder should have correct default values`() {
        assertEquals(cashOrder.transactionType, TransactionType.CREDIT)
        assertEquals(cashOrder.amountCurrency.currency, Currency.USD)
        assertEquals(cashOrder.amountCurrency.amount, 0f)
    }
}

class PaymentOrderTest {

    private lateinit var testAmountCurrency: AmountCurrency
    private lateinit var paymentOrder: PaymentOrder

    @BeforeEach
    fun setUp() {
        testAmountCurrency = AmountCurrency(Currency.USD, 1000f)
        paymentOrder = PaymentOrder(
            id = 5,
            factory = "Test Factory",
            productType = "Test Product",
            paymentDate = "2025-06-01",
            dueDate = "2025-07-01",
            status = PaymentStatus.PENDING,
            transactionType = TransactionType.DEBIT,
            amountCurrency = testAmountCurrency
        )
    }

    @Test
    fun `PaymentOrder should calculate fullPrice correctly with commission`() {

        val customCommission = 0.2f
        val paymentOrder = paymentOrder.copy(commission = customCommission)

        val expectedFullPrice =
            1000f + (1000f * customCommission) + 100f // base + commission + payment price
        assertEquals(paymentOrder.fullPrice.amount, expectedFullPrice, 0.001f)
        assertEquals(paymentOrder.fullPrice.currency, Currency.USD)
    }

    @Test
    fun `PaymentOrder should have correct default values`() {

        assertEquals(paymentOrder.commission, 0.0f)
        assertEquals(paymentOrder.paymentPrice.amount, 100f)
        assertEquals(paymentOrder.paymentPrice.currency, testAmountCurrency.currency)
    }
}

class ExchangeOrderTest {

    private lateinit var exchangeOrder: ExchangeOrder

    @BeforeEach
    fun setUp() {
        exchangeOrder = ExchangeOrder(
            id = 3,
            amountToExchange = AmountCurrency(Currency.USD, 100f),
            amountCurrency = AmountCurrency(Currency.EUR, 85f),
            date = Date(),
            billingDate = Date()
        )
    }

    @Test
    fun `ExchangeOrder should calculate exchange rate correctly`() {

        val expectedRate: Float =
            exchangeOrder.amountToExchange.amount / exchangeOrder.amountCurrency.amount
        assertEquals(exchangeOrder.exchangeRate, expectedRate, 0.01f)
        assertEquals(exchangeOrder.transactionType, TransactionType.CREDIT)
        assertEquals(exchangeOrder.typeToChange, TransactionType.DEBIT)
    }

}

class SaleOrderTest {

    private lateinit var testAmountCurrency: AmountCurrency
    private lateinit var testDate: Date
    private lateinit var saleOrder: SaleOrder

    @BeforeEach
    fun setUp() {
        testAmountCurrency = AmountCurrency(Currency.USD, 1000f)
        testDate = Date()
        saleOrder = SaleOrder(
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
    }


    @Test
    fun `SaleOrder should have correct default values`() {

        assertNull(saleOrder.order)
        assertEquals(saleOrder.vat, 0.20f)
        assertEquals(saleOrder.transactionType, TransactionType.DEBIT)
    }

    @Test
    fun `SaleOrder should accept Order reference`() {
        val order = generateOrder()
        val saleOrder = saleOrder.copy(order = order)

        assertNotNull(saleOrder.order)
        assertEquals(saleOrder.order.id, order.id)
    }
}


class EnumClassesTest {

    private lateinit var testDate: Date
    private lateinit var testAmountCurrency: AmountCurrency

    @BeforeEach
    fun setUp() {
        testDate = Date()
        testAmountCurrency = AmountCurrency(Currency.USD, 1000f)
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
        val transactionTypes = TransactionType.entries
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
            amountToExchange = testAmountCurrency,
            date = testDate,
            amountCurrency = testAmountCurrency,
            billingDate = testDate,
            id = 1,
        )
        val paymentOrder = PaymentOrder(
            id = 1,
            factory = "Factory",
            productType = "Product",
            paymentDate = "2025-06-01",
            dueDate = "2025-07-01",
            status = PaymentStatus.PAID,
            transactionType = TransactionType.DEBIT,
            amountCurrency = testAmountCurrency
        )
        val saleOrder = SaleOrder(
            id = 1,
            productName = "Product",
            cargoCategory = "Category",
            customerName = "Customer",
            status = SaleStatus.CREATED,
            requestDate = "2025-06-01",
            documentDate = "2025-06-02",
            companyName = "Company",
            commissionPercent = 5.0,
            prepayment = false,
            amountCurrency = testAmountCurrency
        )

        assertTrue(order is TypedTransaction)
        assertTrue(cashOrder is TypedTransaction)
        assertTrue(exchangeOrder is TypedTransaction)
        assertTrue(paymentOrder is TypedTransaction)
        assertTrue(saleOrder is TypedTransaction)
    }
}