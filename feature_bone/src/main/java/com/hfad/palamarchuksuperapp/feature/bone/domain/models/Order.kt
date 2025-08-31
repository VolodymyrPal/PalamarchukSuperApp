package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import androidx.annotation.StringRes
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.StepperStatus
import java.util.Date
import kotlin.random.Random

data class Order(
    override val id: Int,
    val businessEntityNum: Int,
    val num: Int,
    val serviceList: List<ServiceOrder> = emptyList(),
    val status: OrderStatus = OrderStatus.CREATED,
    val destinationPoint: String,
    val arrivalDate: Date,
    val containerNumber: String = "",
    val departurePoint: String,
    val cargo: String,
    val manager: String,
    override val amountCurrency: AmountCurrency = AmountCurrency(
        currency = Currency.USD,
        amount = 0f,
    ),
    override val billingDate: Date,
    override val transactionType: TransactionType = TransactionType.DEBIT,
    override val versionHash: String = "",
) : TypedTransaction {
    override val type: String = getType()
    val cargoType: CargoType
        get() {
            return when {
                serviceList.any { it.serviceType == ServiceType.AIR_FREIGHT } -> CargoType.AIR
                serviceList.any { it.serviceType == ServiceType.FULL_FREIGHT } -> CargoType.CONTAINER
                serviceList.any { it.serviceType == ServiceType.EUROPE_TRANSPORT } -> CargoType.TRUCK
                else -> CargoType.ANY
            }
        }
}

enum class OrderStatus(
    @StringRes val nameStringRes: Int,
) {
    CREATED(R.string.order_status_created),
    CALCULATED(R.string.order_status_calculated),
    IN_PROGRESS(R.string.order_status_in_progress),
    DONE(R.string.order_status_done),
}

enum class CargoType {
    ANY,
    CONTAINER,
    TRUCK,
    AIR,
}

fun generateOrder(): Order { // TODO for test only
    val serviceScenarios = listOf(
        ServiceScenario.NonEuropeContainer.WithFreight.scenario,
        ServiceScenario.ChinaEuropeContainer.scenario,
        ServiceScenario.ChinaUkraineContainer.scenario,
        ServiceScenario.SimpleEurope.scenario,
        ServiceScenario.DynamicScenario(
            listOf(
                ServiceType.AIR_FREIGHT,
                ServiceType.FORWARDING,
                ServiceType.CUSTOMS,
            ),
        ).scenario,
    )
    val selectedScenario = serviceScenarios.random()
    val serviceOrders = selectedScenario.mapIndexed { serviceIndex, serviceType ->
        ServiceOrder(
            id = 100 + serviceIndex,
            orderId = serviceIndex,
            fullTransport = Random.nextBoolean(),
            serviceType = serviceType,
            price = Random.nextFloat() * 10000 + 1000,
            durationDay = Random.nextInt(3, 30),
            status = StepperStatus.entries[Random.nextInt(StepperStatus.entries.size)],
        )
    }

    return Order(
        id = Random.nextInt(10000, 60000),
        businessEntityNum = Random.Default.nextInt(2001, 8300),
        serviceList = serviceOrders,
        status = OrderStatus.entries[Random.nextInt(OrderStatus.entries.size)],
        amountCurrency = AmountCurrency(
            currency = Currency.USD,
            amount = 12200f,
        ),
        num = Random.Default.nextInt(44000, 50000),
        destinationPoint = "Тест: Киев",
        arrivalDate = Date(1747785600000L), // "20.05.2025",
        containerNumber = "Тест: 40HC-7865425",
        departurePoint = "Тест: Гонконг",
        cargo = "Тест: Мебель",
        manager = "Тест: VP +3806338875",
        billingDate = Date(),
        // cargoType будет определен автоматически на основе serviceList в data class
    )
}

fun generateOrderItems(): List<Order> { // TODO for test only
    val serviceScenarios = listOf(
        ServiceScenario.NonEuropeContainer.WithFreight.scenario,
        ServiceScenario.ChinaEuropeContainer.scenario,
        ServiceScenario.ChinaUkraineContainer.scenario,
        ServiceScenario.SimpleEurope.scenario,
        ServiceScenario.DynamicScenario(
            listOf(
                ServiceType.AIR_FREIGHT,
                ServiceType.FORWARDING,
                ServiceType.CUSTOMS,
            ),
        ).scenario,
    )

    return List(10) { index ->
        val selectedScenario = serviceScenarios[index % serviceScenarios.size]
        val serviceOrders = selectedScenario.mapIndexed { serviceIndex, serviceType ->
            ServiceOrder(
                id = index * 100 + serviceIndex,
                orderId = index,
                fullTransport = Random.nextBoolean(),
                serviceType = serviceType,
                price = Random.nextFloat() * 10000 + 1000,
                durationDay = Random.nextInt(3, 30),
                status = StepperStatus.entries[Random.nextInt(StepperStatus.entries.size)],
            )
        }

        Order(
            id = index,
            serviceList = serviceOrders,
            status = OrderStatus.entries[Random.nextInt(OrderStatus.entries.size)],
            amountCurrency = AmountCurrency(
                currency = Currency.USD,
                amount = 12200f,
            ),
            num = Random.Default.nextInt(44000, 50000),
            destinationPoint = "Тест: Киев",
            arrivalDate = Date(1747785600000L), // "20.05.2025",
            containerNumber = "Тест: 40HC-7865425",
            departurePoint = "Тест: Гонконг",
            cargo = "Тест: Мебель",
            businessEntityNum = Random.Default.nextInt(2001, 8300),
            manager = "Тест: VP +3806338875",
            billingDate = Date(),
            // cargoType будет определен автоматически на основе serviceList в data class
        )
    }
}
