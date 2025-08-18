package com.feature.bone

import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.AmountCurrencyEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntityWithServices
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.ServiceOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.OrderDto
import com.hfad.palamarchuksuperapp.feature.bone.data.toDomain
import com.hfad.palamarchuksuperapp.feature.bone.data.toDto
import com.hfad.palamarchuksuperapp.feature.bone.data.toEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.CargoType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ServiceOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ServiceType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.StepperStatus
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.Date
import kotlin.test.Test

class OrderMappersTest {

    private fun createServiceOrderEntity(
        id: Int = 1,
        orderId: Int? = null,
        serviceType: ServiceType = ServiceType.OTHER,
    ) = ServiceOrderEntity(
        id = id,
        orderId = orderId,
        fullTransport = true,
        serviceType = serviceType,
        price = 100.0f,
        durationDay = 5,
        status = StepperStatus.CREATED
    )

    private fun createServiceOrder(
        id: Int = 1,
        orderId: Int? = null,
        serviceType: ServiceType = ServiceType.OTHER,
    ) = ServiceOrder(
        id = id,
        orderId = orderId,
        fullTransport = true,
        serviceType = serviceType,
        price = 100.0f,
        durationDay = 5,
        status = StepperStatus.CREATED
    )

    private fun createOrderEntity(id: Int = 1) = OrderEntity(
        id = id,
        businessEntityNum = 123,
        num = 456,
        status = OrderStatus.CREATED,
        destinationPoint = "New York",
        arrivalDate = Date(),
        containerNumber = "CONT123",
        departurePoint = "London",
        cargo = "Electronics",
        manager = "John Doe",
        amountCurrency = AmountCurrencyEntity(Currency.USD, 1000.0f),
        billingDate = Date(),
        transactionType = TransactionType.DEBIT,
        versionHash = "hash123"
    )

    private fun createOrder(id: Int = 1, serviceList: List<ServiceOrder> = emptyList()) = Order(
        id = id,
        businessEntityNum = 123,
        num = 456,
        serviceList = serviceList,
        status = OrderStatus.CREATED,
        destinationPoint = "New York",
        arrivalDate = Date(),
        containerNumber = "CONT123",
        departurePoint = "London",
        cargo = "Electronics",
        manager = "John Doe",
        amountCurrency = AmountCurrency(Currency.USD, 1000.0f),
        billingDate = Date(),
        transactionType = TransactionType.DEBIT,
        versionHash = "hash123"
    )

    private fun createOrderDto(id: Int = 1, serviceList: List<ServiceOrder> = emptyList()) =
        OrderDto(
            id = id,
            businessEntityNum = 123,
            num = 456,
            serviceList = serviceList,
            status = OrderStatus.CREATED,
            destinationPoint = "New York",
            arrivalDate = Date(),
            containerNumber = "CONT123",
            departurePoint = "London",
            cargo = "Electronics",
            manager = "John Doe",
            amountCurrency = AmountCurrency(Currency.USD, 1000.0f),
            billingDate = Date(),
            transactionType = TransactionType.DEBIT,
            versionHash = "hash123"
        )

    @Test
    fun `ServiceOrderEntity toDomain maps correctly`() {
        val entity = createServiceOrderEntity(id = 5, orderId = 10)
        val domain = entity.toDomain()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.orderId, domain.orderId)
        assertEquals(entity.fullTransport, domain.fullTransport)
        assertEquals(entity.serviceType, domain.serviceType)
        assertEquals(entity.price, domain.price)
        assertEquals(entity.durationDay, domain.durationDay)
        assertEquals(entity.status, domain.status)
    }

    @Test
    fun `ServiceOrder toEntity maps correctly with explicit orderId`() {
        val domain = createServiceOrder(id = 5, orderId = null)
        val entity = domain.toEntity(orderId = 10) // Передаем orderId явно

        assertEquals(domain.id, entity.id)
        assertEquals(10, entity.orderId) // Должен использоваться переданный orderId
        assertEquals(domain.fullTransport, entity.fullTransport)
        assertEquals(domain.serviceType, entity.serviceType)
        assertEquals(domain.price, entity.price)
        assertEquals(domain.durationDay, entity.durationDay)
        assertEquals(domain.status, entity.status)
    }

    @Test
    fun `ServiceOrder toEntity maps correctly using its own orderId when explicit is null`() {
        val domain = createServiceOrder(id = 5, orderId = 20)
        val entity =
            domain.toEntity(orderId = null) // Передаем null, должен использоваться orderId из домена

        assertEquals(domain.id, entity.id)
        assertEquals(
            domain.orderId,
            entity.orderId
        ) // Должен использоваться orderId из ServiceOrder
        assertEquals(domain.fullTransport, entity.fullTransport)
        assertEquals(domain.serviceType, entity.serviceType)
        assertEquals(domain.price, entity.price)
        assertEquals(domain.durationDay, entity.durationDay)
        assertEquals(domain.status, entity.status)
    }


    @Test
    fun `OrderEntityWithServices toDomain maps correctly`() {
        val serviceEntity1 =
            createServiceOrderEntity(id = 101, orderId = 1, serviceType = ServiceType.AIR_FREIGHT)
        val serviceEntity2 =
            createServiceOrderEntity(id = 102, orderId = 1, serviceType = ServiceType.CUSTOMS)

        val orderEntity = createOrderEntity(id = 1)
        val orderEntityWithServices = OrderEntityWithServices(
            order = orderEntity,
            services = listOf(serviceEntity1, serviceEntity2)
        )

        val domain = orderEntityWithServices.toDomain()

        assertEquals(orderEntity.id, domain.id)
        assertEquals(orderEntity.businessEntityNum, domain.businessEntityNum)
        assertEquals(orderEntity.num, domain.num)
        assertEquals(orderEntity.status, domain.status)
        assertEquals(orderEntity.destinationPoint, domain.destinationPoint)
        assertEquals(orderEntity.arrivalDate, domain.arrivalDate)
        assertEquals(orderEntity.containerNumber, domain.containerNumber)
        assertEquals(orderEntity.departurePoint, domain.departurePoint)
        assertEquals(orderEntity.cargo, domain.cargo)
        assertEquals(orderEntity.manager, domain.manager)
        assertEquals(orderEntity.amountCurrency, domain.amountCurrency.toEntity())
        assertEquals(orderEntity.billingDate, domain.billingDate)
        assertEquals(orderEntity.transactionType, domain.transactionType)
        assertEquals(orderEntity.versionHash, domain.versionHash)

        assertEquals(2, domain.serviceList.size)

        assertEquals(serviceEntity1.id, domain.serviceList[0].id)
        assertEquals(serviceEntity1.serviceType, domain.serviceList[0].serviceType)
        assertEquals(serviceEntity2.id, domain.serviceList[1].id)
        assertEquals(serviceEntity2.serviceType, domain.serviceList[1].serviceType)
    }

    @Test
    fun `Order toEntity maps correctly`() {
        val serviceDomain1 = createServiceOrder(id = 101)
        val serviceDomain2 = createServiceOrder(id = 102)

        val order = createOrder(id = 1, serviceList = listOf(serviceDomain1, serviceDomain2))
        val entityWithServices = order.toEntity()

        assertEquals(order.id, entityWithServices.order.id)
        assertEquals(order.businessEntityNum, entityWithServices.order.businessEntityNum)
        assertEquals(order.num, entityWithServices.order.num)
        assertEquals(order.status, entityWithServices.order.status)
        assertEquals(order.destinationPoint, entityWithServices.order.destinationPoint)
        assertEquals(order.arrivalDate, entityWithServices.order.arrivalDate)
        assertEquals(order.containerNumber, entityWithServices.order.containerNumber)
        assertEquals(order.departurePoint, entityWithServices.order.departurePoint)
        assertEquals(order.cargo, entityWithServices.order.cargo)
        assertEquals(order.manager, entityWithServices.order.manager)
        assertEquals(order.amountCurrency.toEntity(), entityWithServices.order.amountCurrency)
        assertEquals(order.billingDate, entityWithServices.order.billingDate)
        assertEquals(order.transactionType, entityWithServices.order.transactionType)
        assertEquals(order.versionHash, entityWithServices.order.versionHash)

        assertEquals(2, entityWithServices.services.size)

        assertEquals(serviceDomain1.id, entityWithServices.services[0].id)
        assertEquals(
            order.id,
            entityWithServices.services[0].orderId
        ) // orderId должен быть установлен
        assertEquals(serviceDomain1.serviceType, entityWithServices.services[0].serviceType)
        assertEquals(serviceDomain2.id, entityWithServices.services[1].id)
        assertEquals(
            order.id,
            entityWithServices.services[1].orderId
        ) // orderId должен быть установлен
        assertEquals(serviceDomain2.serviceType, entityWithServices.services[1].serviceType)
    }

    @Test
    fun `OrderDto toDomain maps correctly`() {
        val serviceOrder1 = createServiceOrder(id = 1)
        val serviceOrder2 = createServiceOrder(id = 2)
        val orderDto = createOrderDto(serviceList = listOf(serviceOrder1, serviceOrder2))

        val domain = orderDto.toDomain()

        assertEquals(orderDto.id, domain.id)
        assertEquals(orderDto.businessEntityNum, domain.businessEntityNum)
        assertEquals(orderDto.num, domain.num)
        assertEquals(orderDto.serviceList, domain.serviceList) // ServiceList передается напрямую
        assertEquals(orderDto.status, domain.status)
        assertEquals(orderDto.destinationPoint, domain.destinationPoint)
        assertEquals(orderDto.arrivalDate, domain.arrivalDate)
        assertEquals(orderDto.containerNumber, domain.containerNumber)
        assertEquals(orderDto.departurePoint, domain.departurePoint)
        assertEquals(orderDto.cargo, domain.cargo)
        assertEquals(orderDto.manager, domain.manager)
        assertEquals(orderDto.amountCurrency, domain.amountCurrency)
        assertEquals(orderDto.billingDate, domain.billingDate)
        assertEquals(orderDto.transactionType, domain.transactionType)
        assertEquals(orderDto.versionHash, domain.versionHash)
    }

    @Test
    fun `OrderDto toEntity maps correctly`() {
        val serviceDomain1 = createServiceOrder(id = 101)
        val serviceDomain2 = createServiceOrder(id = 102)

        val orderDto = createOrderDto(id = 1, serviceList = listOf(serviceDomain1, serviceDomain2))
        val entityWithServices = orderDto.toEntity()

        assertEquals(orderDto.id, entityWithServices.order.id)
        assertEquals(orderDto.businessEntityNum, entityWithServices.order.businessEntityNum)
        assertEquals(orderDto.num, entityWithServices.order.num)
        assertEquals(orderDto.status, entityWithServices.order.status)
        assertEquals(orderDto.destinationPoint, entityWithServices.order.destinationPoint)
        assertEquals(orderDto.arrivalDate, entityWithServices.order.arrivalDate)
        assertEquals(orderDto.containerNumber, entityWithServices.order.containerNumber)
        assertEquals(orderDto.departurePoint, entityWithServices.order.departurePoint)
        assertEquals(orderDto.cargo, entityWithServices.order.cargo)
        assertEquals(orderDto.manager, entityWithServices.order.manager)
        assertEquals(orderDto.amountCurrency.toEntity(), entityWithServices.order.amountCurrency)
        assertEquals(orderDto.billingDate, entityWithServices.order.billingDate)
        assertEquals(orderDto.transactionType, entityWithServices.order.transactionType)
        assertEquals(orderDto.versionHash, entityWithServices.order.versionHash)

        assertEquals(2, entityWithServices.services.size)

        assertEquals(serviceDomain1.id, entityWithServices.services[0].id)
        assertEquals(
            orderDto.id,
            entityWithServices.services[0].orderId
        ) // orderId должен быть установлен
        assertEquals(serviceDomain1.serviceType, entityWithServices.services[0].serviceType)
        assertEquals(serviceDomain2.id, entityWithServices.services[1].id)
        assertEquals(
            orderDto.id,
            entityWithServices.services[1].orderId
        ) // orderId должен быть установлен
        assertEquals(serviceDomain2.serviceType, entityWithServices.services[1].serviceType)
    }

    @Test
    fun `Order toDto maps correctly`() {
        val serviceOrder1 = createServiceOrder(id = 1)
        val serviceOrder2 = createServiceOrder(id = 2)
        val order = createOrder(serviceList = listOf(serviceOrder1, serviceOrder2))

        val dto = order.toDto()

        assertEquals(order.id, dto.id)
        assertEquals(order.businessEntityNum, dto.businessEntityNum)
        assertEquals(order.num, dto.num)
        assertEquals(order.serviceList, dto.serviceList) // ServiceList передается напрямую
        assertEquals(order.status, dto.status)
        assertEquals(order.destinationPoint, dto.destinationPoint)
        assertEquals(order.arrivalDate, dto.arrivalDate)
        assertEquals(order.containerNumber, dto.containerNumber)
        assertEquals(order.departurePoint, dto.departurePoint)
        assertEquals(order.cargo, dto.cargo)
        assertEquals(order.manager, dto.manager)
        assertEquals(order.amountCurrency, dto.amountCurrency)
        assertEquals(order.billingDate, dto.billingDate)
        assertEquals(order.transactionType, dto.transactionType)
        assertEquals(order.versionHash, dto.versionHash)
    }

    @Test
    fun `Order cargoType is AIR when serviceList contains AIR_FREIGHT`() {
        val airFreightService = createServiceOrder(serviceType = ServiceType.AIR_FREIGHT)
        val order = createOrder(serviceList = listOf(airFreightService))
        assertEquals(CargoType.AIR, order.cargoType)
    }

    @Test
    fun `Order cargoType is CONTAINER when serviceList contains FULL_FREIGHT`() {
        val fullFreightService = createServiceOrder(serviceType = ServiceType.FULL_FREIGHT)
        val order = createOrder(serviceList = listOf(fullFreightService))
        assertEquals(CargoType.CONTAINER, order.cargoType)
    }

    @Test
    fun `Order cargoType is TRUCK when serviceList contains EUROPE_TRANSPORT`() {
        val europeTransportService = createServiceOrder(serviceType = ServiceType.EUROPE_TRANSPORT)
        val order = createOrder(serviceList = listOf(europeTransportService))
        assertEquals(CargoType.TRUCK, order.cargoType)
    }

    @Test
    fun `Order cargoType is ANY when no specific freight type is found`() {
        val otherService = createServiceOrder(serviceType = ServiceType.OTHER)
        val order = createOrder(serviceList = listOf(otherService))
        assertEquals(CargoType.ANY, order.cargoType)
    }

    @Test
    fun `OrderDto cargoType is AIR when serviceList contains AIR_FREIGHT`() {
        val airFreightService = createServiceOrder(serviceType = ServiceType.AIR_FREIGHT)
        val orderDto = createOrderDto(serviceList = listOf(airFreightService))
        assertEquals(CargoType.AIR, orderDto.cargoType)
    }

    @Test
    fun `OrderDto cargoType is CONTAINER when serviceList contains FULL_FREIGHT`() {
        val fullFreightService = createServiceOrder(serviceType = ServiceType.FULL_FREIGHT)
        val orderDto = createOrderDto(serviceList = listOf(fullFreightService))
        assertEquals(CargoType.CONTAINER, orderDto.cargoType)
    }

    @Test
    fun `OrderDto cargoType is TRUCK when serviceList contains EUROPE_TRANSPORT`() {
        val europeTransportService = createServiceOrder(serviceType = ServiceType.EUROPE_TRANSPORT)
        val orderDto = createOrderDto(serviceList = listOf(europeTransportService))
        assertEquals(CargoType.TRUCK, orderDto.cargoType)
    }

    @Test
    fun `OrderDto cargoType is ANY when no specific freight type is found`() {
        val otherService = createServiceOrder(serviceType = ServiceType.OTHER)
        val orderDto = createOrderDto(serviceList = listOf(otherService))
        assertEquals(CargoType.ANY, orderDto.cargoType)
    }
}
