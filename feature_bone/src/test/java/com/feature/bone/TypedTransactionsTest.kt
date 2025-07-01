package com.feature.bone

import com.hfad.palamarchuksuperapp.core.di.AppFirstAccessDetector
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.AuthRepositoryImpl
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.LogStatus
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.SessionConfig
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
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl.ObserveLoginStatusUseCaseImpl
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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
        assertEquals(cashOrder.type, TransactionType.CREDIT)
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
            type = TransactionType.DEBIT,
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
        assertEquals(exchangeOrder.type, TransactionType.CREDIT)
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
        assertEquals(saleOrder.type, TransactionType.DEBIT)
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

@ExtendWith(MockKExtension::class)
class ObserveLoginStatusUseCaseImplTest {

    private val authRepository = mockk<AuthRepository>()
    private val sessionConfig = mockk<SessionConfig>()
    private val detector = mockk<AppFirstAccessDetector>()

    private lateinit var useCase: ObserveLoginStatusUseCaseImpl

    private val baseTime = 1000000000L // Фиксированное время для тестов
    private val sessionDurationMs = 3600000L // 1 час в миллисекундах
    private val refreshThresholdMs = 7200000L // 2 часа в миллисекундах

    @BeforeEach
    fun setup() {
        clearAllMocks()
        useCase = ObserveLoginStatusUseCaseImpl(authRepository, sessionConfig, detector)

        // Дефолтные моки
        every { sessionConfig.sessionDuration } returns Date( Date().time + sessionDurationMs)
        every { sessionConfig.refreshThreshold } returns Date(Date().time + refreshThresholdMs)
        every { sessionConfig.autoRefreshEnabled } returns false
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `invoke should clear unremembered session on first access`() = runTest {
        val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))
        every { detector.isFirstAccess("isFirstAccess") } returns true
        every { detector.isFirstAccess("Extend_Session_On_Start") } returns false
        every { authRepository.currentSession } returns flowOf(session)
        every { authRepository.shouldRefreshToken(session) } returns false
        coEvery { authRepository.clearUnrememberedSession() } just Runs

        val result = useCase.invoke().toList()

        coVerify { authRepository.clearUnrememberedSession() }
        assertEquals(LogStatus.LOGGED_IN, result.first())
    }

    private fun createUserSession(
        userStatus: LogStatus,
        loginTimestamp: Date,
        expiresAt: Date = Date(loginTimestamp.time + sessionDurationMs),
    ): AuthRepositoryImpl.UserSession { // Возвращаем реальный UserSession
        return AuthRepositoryImpl.UserSession(
            userStatus = userStatus,
            loginTimestamp = loginTimestamp,
            expiresAt = expiresAt
        )
    }

    @Test
    fun `invoke should not clear session when not first access`() = runTest {
        // Given
        val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))
        every { detector.isFirstAccess("isFirstAccess") } returns false
        every { detector.isFirstAccess("Extend_Session_On_Start") } returns false
        every { authRepository.currentSession } returns flowOf(session)
        every { authRepository.shouldRefreshToken(session) } returns false

        // When
        val result = useCase.invoke().toList()

        // Then
        coVerify(exactly = 0) { authRepository.clearUnrememberedSession() }
        assertEquals(LogStatus.LOGGED_IN, result.first())
    }

    @Test
    fun `determineLoginStatus should return NOT_LOGGED when session status is NOT_LOGGED`() =
        runTest {
            // Given
            val session = createUserSession(LogStatus.NOT_LOGGED, Date(baseTime))
            every { detector.isFirstAccess("isFirstAccess") } returns false
            every { authRepository.currentSession } returns flowOf(session)

            // When
            val result = useCase.invoke().toList()

            // Then
            assertEquals(LogStatus.NOT_LOGGED, result.first())
        }

    @Test
    fun `determineLoginStatus should return LOGGED_IN for active session within duration`() =
        runTest {
            // Given
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess("isFirstAccess") } returns false
            every { detector.isFirstAccess("Extend_Session_On_Start") } returns false
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns false


            // When
            val result = useCase.invoke().toList()

            // Then
            assertEquals(LogStatus.LOGGED_IN, result.first())
        }

    @Test
    fun `determineLoginStatus should return REQUIRE_WEAK_LOGIN when past session duration but within refresh threshold`() =
        runTest {
            // Given
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess("isFirstAccess") } returns false
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns false


            // When
            val result = useCase.invoke().toList()

            // Then
            assertEquals(LogStatus.REQUIRE_WEAK_LOGIN, result.first())
        }

    @Test
    fun `determineLoginStatus should return TOKEN_REFRESH_REQUIRED when shouldRefresh is true and autoRefresh disabled`() =
        runTest {
            // Given
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess("isFirstAccess") } returns false
            every { detector.isFirstAccess("Extend_Session_On_Start") } returns false
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns true
            every { sessionConfig.autoRefreshEnabled } returns false

            // When
            val result = useCase.invoke().toList()

            // Then
            assertEquals(LogStatus.TOKEN_REFRESH_REQUIRED, result.first())
        }

    @Test
    fun `determineLoginStatus should return TOKEN_AUTO_REFRESH when shouldRefresh is true and autoRefresh enabled`() =
        runTest {
            // Given
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess("isFirstAccess") } returns false
            every { detector.isFirstAccess("Extend_Session_On_Start") } returns false
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns true
            every { sessionConfig.autoRefreshEnabled } returns true


            // When
            val result = useCase.invoke().toList()

            // Then
            assertEquals(LogStatus.TOKEN_AUTO_REFRESH, result.first())
        }

    @Test
    fun `determineLoginStatus should return NOT_LOGGED when past refresh threshold and shouldRefresh is false`() =
        runTest {
            // Given
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess("isFirstAccess") } returns false
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns false


            // When
            val result = useCase.invoke().toList()

            // Then
            assertEquals(LogStatus.NOT_LOGGED, result.first())
        }

    @Test
    fun `handleActiveSession should extend session on first access with sufficient time delta`() =
        runTest {
            // Given
            val currentTime = Date(baseTime - 5000L) // Старое время логина на 5 секунд
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess("isFirstAccess") } returns false
            every { detector.isFirstAccess("Extend_Session_On_Start") } returns true
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns false
            coEvery { authRepository.saveSession(any()) } returns AppResult.Success(Unit)


            // When
            val result = useCase.invoke().toList()

            // Then
            coVerify { authRepository.saveSession(any()) }
            assertEquals(LogStatus.LOGGED_IN, result.first())
        }

    @Test
    fun `handleActiveSession should not extend session when time delta is insufficient`() =
        runTest {
            // Given
            val currentTime = Date(baseTime - 500L) // Разница 500ms < 1000ms threshold
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess("isFirstAccess") } returns false
            every { detector.isFirstAccess("Extend_Session_On_Start") } returns true
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns false


            // When
            val result = useCase.invoke().toList()

            // Then
            coVerify(exactly = 0) { authRepository.saveSession(any()) }
            assertEquals(LogStatus.LOGGED_IN, result.first())
        }

    @Test
    fun `handleActiveSession should return NOT_LOGGED when saveSession fails`() = runTest {
        // Given
        val currentTime = Date(baseTime - 5000L)
        val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

        every { detector.isFirstAccess("isFirstAccess") } returns false
        every { detector.isFirstAccess("Extend_Session_On_Start") } returns true
        every { authRepository.currentSession } returns flowOf(session)
        every { authRepository.shouldRefreshToken(session) } returns false
        coEvery { authRepository.saveSession(any()) } returns AppResult.Error(AppError.CustomError("Save failed"))


        // When
        val result = useCase.invoke().toList()

        // Then
        assertEquals(LogStatus.NOT_LOGGED, result.first())
    }

    @Test
    fun `handleActiveSession should not extend session when not first access`() = runTest {
        // Given
        val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

        every { detector.isFirstAccess("isFirstAccess") } returns false
        every { detector.isFirstAccess("Extend_Session_On_Start") } returns false
        every { authRepository.currentSession } returns flowOf(session)
        every { authRepository.shouldRefreshToken(session) } returns false


        // When
        val result = useCase.invoke().toList()

        // Then
        coVerify(exactly = 0) { authRepository.saveSession(any()) }
        assertEquals(LogStatus.LOGGED_IN, result.first())
    }

    @Test
    fun `invoke should emit distinct values only`() = runTest {
        // Given
        val session1 = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))
        val session2 = createUserSession(LogStatus.LOGGED_IN, Date(baseTime)) // Same status
        val session3 = createUserSession(LogStatus.NOT_LOGGED, Date(baseTime)) // Different status

        every { detector.isFirstAccess("isFirstAccess") } returns false
        every { detector.isFirstAccess("Extend_Session_On_Start") } returns false
        every { authRepository.currentSession } returns flowOf(session1, session2, session3)
        every { authRepository.shouldRefreshToken(session1) } returns false
        every { authRepository.shouldRefreshToken(session2) } returns false
        every { authRepository.shouldRefreshToken(session3) } returns false


        // When
        val result = useCase.invoke().toList()

        // Then
        assertEquals(2, result.size) // Should skip duplicate LOGGED_IN
        assertEquals(LogStatus.LOGGED_IN, result[0])
        assertEquals(LogStatus.NOT_LOGGED, result[1])
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `should handle different autoRefreshEnabled configurations`(autoRefreshEnabled: Boolean) =
        runTest {
            // Given
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess(any()) } returns false
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns true
            every { sessionConfig.autoRefreshEnabled } returns autoRefreshEnabled


            // When
            val result = useCase.invoke().toList()

            // Then
            val expectedStatus = if (autoRefreshEnabled) {
                LogStatus.TOKEN_AUTO_REFRESH
            } else {
                LogStatus.TOKEN_REFRESH_REQUIRED
            }
            assertEquals(expectedStatus, result.first())
        }
}