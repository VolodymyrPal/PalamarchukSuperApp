package com.feature.bone

import android.database.SQLException
import androidx.paging.PagingSource
import androidx.room.R
import androidx.room.withTransaction
import com.hfad.palamarchuksuperapp.core.data.fetchWithCacheFallback
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.OrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.RemoteKeysDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.BoneDatabase
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntityWithServices
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.ServiceOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.OrderApi
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.OrderDto
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.OrdersRepositoryImpl
import com.hfad.palamarchuksuperapp.feature.bone.data.toDto
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ServiceOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ServiceType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.StepperStatus
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNotNull
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersRepositoryImplTestSub {

    private val boneDatabase = mockk<BoneDatabase>()
    private val orderApi = mockk<OrderApi>()
    private val orderDao = mockk<OrderDao>()
    private val remoteKeysDao = mockk<RemoteKeysDao>()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: OrdersRepositoryImpl

    private val testDate = Date()

    private val testServiceOrderEntity = ServiceOrderEntity(
        id = 101,
        orderId = 1,
        fullTransport = true,
        serviceType = ServiceType.EUROPE_TRANSPORT,
        price = 50.0f,
        durationDay = 2,
        status = StepperStatus.DONE
    )
    private val testServiceOrder = ServiceOrder(
        id = 101,
        orderId = 1,
        fullTransport = true,
        serviceType = ServiceType.EUROPE_TRANSPORT,
        price = 50.0f,
        durationDay = 2,
        status = StepperStatus.DONE
    )
    private val testOrderEntity = OrderEntityWithServices(
        order = OrderEntity(
            id = 1,
            businessEntityNum = 12345,
            num = 1001,
            status = OrderStatus.CREATED,
            destinationPoint = "Test Destination",
            arrivalDate = testDate,
            containerNumber = "CONT123",
            departurePoint = "Test Departure",
            cargo = "Test Cargo",
            manager = "Test Manager",
            sum = 100.0f,
            currency = Currency.USD,
            billingDate = testDate,
            transactionType = TransactionType.DEBIT,
            versionHash = "testHash123"
        ),
        services = listOf(testServiceOrderEntity)
    )
    private val testOrder = Order(
        id = 1,
        businessEntityNum = 12345,
        num = 1001,
        serviceList = listOf(testServiceOrder),
        status = OrderStatus.CREATED,
        destinationPoint = "Test Destination",
        arrivalDate = testDate,
        containerNumber = "CONT123",
        departurePoint = "Test Departure",
        cargo = "Test Cargo",
        manager = "Test Manager",
        amountCurrency = AmountCurrency(
            currency = Currency.USD,
            amount = 100.0f
        ),
        billingDate = testDate,
        transactionType = TransactionType.DEBIT,
        versionHash = "testHash123"
    )
    private val testOrderDto = OrderDto(
        id = 1,
        businessEntityNum = 12345,
        num = 1001,
        serviceList = listOf(testServiceOrder),
        status = OrderStatus.CREATED,
        destinationPoint = "Test Destination",
        arrivalDate = testDate,
        containerNumber = "CONT123",
        departurePoint = "Test Departure",
        cargo = "Test Cargo",
        manager = "Test Manager",
        amountCurrency = AmountCurrency(
            currency = Currency.USD,
            amount = 100.0f
        ),
        billingDate = testDate,
        transactionType = TransactionType.DEBIT,
        versionHash = "testHash123"
    )

    private val testOrderStatistics = OrderStatistics(
        totalOrders = 10,
        inProgressOrders = 3,
        completedOrders = 7
    )

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        mockkStatic("androidx.room.RoomDatabaseKt")
        Dispatchers.setMain(testDispatcher)
        every { boneDatabase.orderDao() } returns orderDao
        every { boneDatabase.remoteKeysDao() } returns remoteKeysDao
        every { orderDao.getStatistic() } returns flowOf(testOrderStatistics)
        repository = OrdersRepositoryImpl(boneDatabase, orderApi)

        val transactionLambda = slot<suspend () -> R>()
        coEvery { boneDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `pagingOrders returns paging data flow correctly`() = runTest {
        val status = OrderStatus.IN_PROGRESS
        val pagingSource = mockk<PagingSource<Int, OrderEntityWithServices>>(relaxed = true) {
            coEvery { load(any()) } returns PagingSource.LoadResult.Page(
                data = listOf(testOrderEntity),
                prevKey = null,
                nextKey = null
            )
        }
        every { orderDao.getOrdersWithServices(status) } returns pagingSource

        val resultFlow = repository.pagingOrders(status)
        assertNotNull(resultFlow)

        resultFlow.first()

        verify { orderDao.getOrdersWithServices(status) }
    }

    @Test
    fun `ordersInRange returns error with fallback data when remote fails but local succeeds`() =
        runTest {
            val from = Date()
            val to = Date()
            val localOrderEntities = listOf(testOrderEntity)
            val apiException = RuntimeException("Network error")

            coEvery { orderApi.getOrdersWithRange(from, to) } throws apiException
            coEvery { orderDao.ordersInRange(from, to) } returns localOrderEntities

            val result = repository.ordersInRange(from, to)

            assertTrue(result is AppResult.Error)
            result as AppResult.Error
            assertEquals(listOf(testOrder).first().id, result.data?.first()?.id)
            assertNotNull(result.error)
            coVerify { orderApi.getOrdersWithRange(from, to) }
            coVerify { orderDao.ordersInRange(from, to) }
        }

    @Test
    fun `ordersInRange returns error when both remote and local fail`() = runTest {
        val from = Date()
        val to = Date()
        val apiException = RuntimeException("Network error")
        val dbException = SQLException("Database error")

        coEvery { orderApi.getOrdersWithRange(from, to) } throws apiException
        coEvery { orderDao.ordersInRange(from, to) } throws dbException

        val result = repository.ordersInRange(from, to)

        assertTrue(result is AppResult.Error)
        result as AppResult.Error
        assertTrue(result.error is AppError.CustomError)
        val customError = result.error as AppError.CustomError
        assertTrue(customError.message?.contains("Problem with internet and database") == true)
        coVerify { orderApi.getOrdersWithRange(from, to) }
        coVerify { orderDao.ordersInRange(from, to) }
    }

    @Test
    fun `ordersInRange returns success with remote data when database fails to store`() = runTest {
        val from = Date()
        val to = Date()
        val remoteOrders = listOf(testOrder)
        val dbException = SQLException("Database write error")

        coEvery { orderApi.getOrdersWithRange(from, to) } returns remoteOrders.map { it.toDto() }
        coEvery { orderDao.insertOrIgnoreOrders(any()) } throws dbException
        coEvery { orderDao.ordersInRange(from, to) } returns emptyList()


        val result = repository.ordersInRange(from, to)

        assertTrue(result is AppResult.Success)
        result as AppResult.Success
        assertEquals(remoteOrders.first().id, result.data.first().id)
        assertNotNull(result.error)
        assertTrue(result.error is AppError.DatabaseError)
        coVerify { orderApi.getOrdersWithRange(from, to) }
    }

    @Test
    fun `getOrderById returns success when remote fetch succeeds`() = runTest {
        val orderId = 1
        val remoteOrder = testOrder

        coEvery { orderApi.getOrder(orderId) } returns remoteOrder.toDto()
        coEvery { orderDao.insertOrIgnoreOrders(listOf(testOrderEntity)) } just Runs
        coEvery { orderDao.getOrderById(orderId) } returns testOrderEntity

        val result = repository.getOrderById(orderId)

        assertTrue(result is AppResult.Success)
        result as AppResult.Success
        assertEquals(testOrder.id, result.data?.id)
        coVerify { orderApi.getOrder(orderId) }
        coVerify { orderDao.insertOrIgnoreOrders(listOf(testOrderEntity)) }
        coVerify { orderDao.getOrderById(orderId) }
    }

    @Test
    fun `getOrderById returns success with null when order not found remotely and locally`() =
        runTest {
            val orderId = 1

            coEvery { orderApi.getOrder(orderId) } returns null
            coEvery { orderDao.getOrderById(orderId) } returns null

            val result = repository.getOrderById(orderId)

            assertTrue(result is AppResult.Success)
            result as AppResult.Success
            assertEquals(null, result.data)
            coVerify { orderApi.getOrder(orderId) }
            coVerify { orderDao.getOrderById(orderId) }
            coVerify(exactly = 0) { orderDao.insertOrIgnoreOrders(any()) }
        }

    @Test
    fun `getOrderById returns error with fallback when remote fails`() = runTest {
        val orderId = 1
        val apiException = RuntimeException("Network error")

        coEvery { orderApi.getOrder(orderId) } throws apiException
        coEvery { orderDao.getOrderById(orderId) } returns testOrderEntity

        val result = repository.getOrderById(orderId)

        assertTrue(result is AppResult.Error)
        result as AppResult.Error
        assertEquals(testOrder.id, result.data?.id)
        assertNotNull(result.error)
        coVerify { orderApi.getOrder(orderId) }
        coVerify { orderDao.getOrderById(orderId) }
    }

    @Test
    fun `softRefreshStatistic returns success when remote fetch succeeds`() = runTest {
        val remoteStatistics = testOrderStatistics

        coEvery { orderApi.getOrderStatistics() } returns remoteStatistics
        coEvery { orderDao.insertOrIgnoreOrderStatistic(remoteStatistics) } just Runs
        coEvery { orderDao.getOrderStatistics() } returns testOrderStatistics

        val result = repository.softRefreshStatistic()

        assertTrue(result is AppResult.Success)
        result as AppResult.Success
        assertEquals(testOrderStatistics, result.data)
        coVerify { orderApi.getOrderStatistics() }
        coVerify { orderDao.insertOrIgnoreOrderStatistic(remoteStatistics) }
        coVerify { orderDao.getOrderStatistics() }
    }

    @Test
    fun `softRefreshStatistic returns error with fallback when remote fails`() = runTest {
        val apiException = RuntimeException("Network error")
        val localStatistics = testOrderStatistics

        coEvery { orderApi.getOrderStatistics() } throws apiException
        coEvery { orderDao.getOrderStatistics() } returns localStatistics


        val result = repository.softRefreshStatistic()

        assertTrue(result is AppResult.Error)
        result as AppResult.Error
        assertEquals(testOrderStatistics, result.data)
        assertNotNull(result.error)
        coVerify { orderApi.getOrderStatistics() }
        coVerify { orderDao.getOrderStatistics() }
    }

    @Test
    fun `softRefreshStatistic returns success with remote data when database fails to store`() =
        runTest {
            val remoteStatistics = testOrderStatistics
            val dbException = SQLException("Database write error")

            coEvery { orderApi.getOrderStatistics() } returns remoteStatistics
            coEvery { boneDatabase.withTransaction(any<suspend () -> OrderStatistics>()) } throws dbException
            coEvery { orderDao.insertOrUpdateStatistic(any()) } just Runs

            val result = repository.refreshStatistic()

            assertTrue(result is AppResult.Success)
            result as AppResult.Success
            assertEquals(remoteStatistics, result.data)
//            assertNotNull(result.error)
//            assertTrue(result.error is AppError.DatabaseError)
//            coVerify { orderApi.getOrderStatistics() }
        }
}

class FetchWithCacheFallbackTest {

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `fetchWithCacheFallback returns success when remote and store succeed`() = runTest {
        val remoteData = "remote_data"
        val storedData = "stored_data"

        val fetchRemote: suspend () -> String = mockk()
        val storeAndRead: suspend (String) -> String = mockk()
        val fallbackFetch: suspend () -> String = mockk()

        coEvery { fetchRemote() } returns remoteData
        coEvery { storeAndRead(remoteData) } returns storedData

        val result = fetchWithCacheFallback(fetchRemote, storeAndRead, fallbackFetch)

        assertTrue(result is AppResult.Success)
        result as AppResult.Success
        assertEquals(storedData, result.data)
        coVerify { fetchRemote() }
        coVerify { storeAndRead(remoteData) }
        coVerify(exactly = 0) { fallbackFetch() }
    }

    @Test
    fun `fetchWithCacheFallback returns success with remote data when store fails`() = runTest {
        val remoteData = "remote_data"
        val dbException = SQLException("Database error")

        val fetchRemote: suspend () -> String = mockk()
        val storeAndRead: suspend (String) -> String = mockk()
        val fallbackFetch: suspend () -> String = mockk()

        coEvery { fetchRemote() } returns remoteData
        coEvery { storeAndRead(remoteData) } throws dbException

        val result = fetchWithCacheFallback(fetchRemote, storeAndRead, fallbackFetch)

        assertTrue(result is AppResult.Success)
        result as AppResult.Success
        assertEquals(remoteData, result.data)
        assertNotNull(result.error)
        assertTrue(result.error is AppError.DatabaseError)
        coVerify { fetchRemote() }
        coVerify { storeAndRead(remoteData) }
        coVerify(exactly = 0) { fallbackFetch() }
    }

    @Test
    fun `fetchWithCacheFallback returns error with fallback data when remote fails but fallback succeeds`() =
        runTest {
            val apiException = RuntimeException("Network error")
            val fallbackData = "fallback_data"

            val fetchRemote: suspend () -> String = mockk()
            val storeAndRead: suspend (String) -> String = mockk()
            val fallbackFetch: suspend () -> String = mockk()

            coEvery { fetchRemote() } throws apiException
            coEvery { fallbackFetch() } returns fallbackData

            val result = fetchWithCacheFallback(fetchRemote, storeAndRead, fallbackFetch)

            assertTrue(result is AppResult.Error)
            result as AppResult.Error
            assertEquals(fallbackData, result.data)
            assertNotNull(result.error)
            assertTrue(result.error is AppError.NetworkException)
            coVerify { fetchRemote() }
            coVerify { fallbackFetch() }
            coVerify(exactly = 0) { storeAndRead(any()) }
        }

    @Test
    fun `fetchWithCacheFallback returns error when both remote and fallback fail`() = runTest {
        val apiException = RuntimeException("Network error")
        val dbException = SQLException(
            "Problem with internet and database",
            RuntimeException("Network error"),
        )

        val fetchRemote: suspend () -> String = mockk()
        val storeAndRead: suspend (String) -> String = mockk()
        val fallbackFetch: suspend () -> String = mockk()

        coEvery { fetchRemote() } throws apiException
        coEvery { fallbackFetch() } throws dbException

        val result = fetchWithCacheFallback(fetchRemote, storeAndRead, fallbackFetch)

        assertTrue(result is AppResult.Error)
        result as AppResult.Error
        assertTrue(result.error is AppError.CustomError)
        val customError = result.error as AppError.CustomError
        assertTrue(customError.message?.contains("Problem with internet and database") == true)
        assertTrue(customError.message?.contains("Network error") == true)
        assertTrue(customError.message?.contains("unknown DB error") == true)
        coVerify { fetchRemote() }
        coVerify { fallbackFetch() }
        coVerify(exactly = 0) { storeAndRead(any()) }
    }
}