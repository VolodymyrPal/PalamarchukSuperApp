package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.StepperStatus
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.ServiceScenario
import java.util.Date
import kotlin.random.Random

data class Order(
    @PrimaryKey
    override val id: Int = 0,
    val businessEntityNum: Int = Random.Default.nextInt(2001, 8300),  //TODO change
    val num: Int = Random.Default.nextInt(44000, 50000),              //TODO change
    val serviceList: List<ServiceOrder> = listOf(),
    val status: OrderStatus = OrderStatus.CREATED,
    val cargoType: CargoType = when {
        serviceList.any { it.serviceType == ServiceType.AIR_FREIGHT } -> CargoType.AIR
        serviceList.any { it.serviceType == ServiceType.FULL_FREIGHT } -> CargoType.CONTAINER
        serviceList.any { it.serviceType == ServiceType.EUROPE_TRANSPORT } -> CargoType.TRUCK
        else -> CargoType.ANY
    },
    val destinationPoint: String = "Тест: Киев",
    val arrivalDate: String = "Тест: 20.05.2025",
    val containerNumber: String = "Тест: 40HC-7865425",
    val departurePoint: String = "Тест: Гонконг",
    val cargo: String = "Тест: Мебель",
    val manager: String = "Тест: VP +3806338875",
    override val amountCurrency: AmountCurrency,
    override val billingDate: Date = Date(),
    override val type: TransactionType = TransactionType.DEBIT,
) : TypedTransaction

enum class OrderStatus {
    CREATED, CALCULATED, IN_PROGRESS, DONE
}

enum class CargoType {
    ANY, CONTAINER, TRUCK, AIR
}

fun generateOrder() : Order {
    val serviceScenarios = listOf(
        ServiceScenario.NonEuropeContainer.WithFreight.scenario,
        ServiceScenario.ChinaEuropeContainer.scenario,
        ServiceScenario.ChinaUkraineContainer.scenario,
        ServiceScenario.SimpleEurope.scenario,
        ServiceScenario.DynamicScenario(
            listOf(
                ServiceType.AIR_FREIGHT,
                ServiceType.FORWARDING,
                ServiceType.CUSTOMS
            )
        ).scenario
    )
    val selectedScenario = serviceScenarios.random()
    val serviceOrders = selectedScenario.mapIndexed { serviceIndex, serviceType ->
        ServiceOrder(
            id = 100 + serviceIndex,
            orderId = serviceIndex,
            fullTransport = Random.nextBoolean(),
            serviceType = serviceType,
            price = Random.nextFloat() * 10000 + 1000,
            duration = Random.nextInt(3, 30),
            status = StepperStatus.entries[Random.nextInt(StepperStatus.entries.size)],
            icon = when (serviceType) {
                ServiceType.FULL_FREIGHT -> R.drawable.in_progress
                ServiceType.AIR_FREIGHT -> R.drawable.kilogram
                ServiceType.CUSTOMS -> R.drawable.lock_outlined
                else -> R.drawable.in_progress
            }
        )
    }

    return Order(
        id = Random.nextInt(10000, 60000),
        serviceList = serviceOrders,
        status = OrderStatus.entries[Random.nextInt(OrderStatus.entries.size)],
        amountCurrency = AmountCurrency(
            currency = Currency.USD,
            amount = 12200f
        )
        // cargoType будет определен автоматически на основе serviceList в data class
    )

}

fun generateOrderItems(): List<Order> {
    val serviceScenarios = listOf(
        ServiceScenario.NonEuropeContainer.WithFreight.scenario,
        ServiceScenario.ChinaEuropeContainer.scenario,
        ServiceScenario.ChinaUkraineContainer.scenario,
        ServiceScenario.SimpleEurope.scenario,
        ServiceScenario.DynamicScenario(
            listOf(
                ServiceType.AIR_FREIGHT,
                ServiceType.FORWARDING,
                ServiceType.CUSTOMS
            )
        ).scenario
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
                duration = Random.nextInt(3, 30),
                status = StepperStatus.entries[Random.nextInt(StepperStatus.entries.size)],
                icon = when (serviceType) {
                    ServiceType.FULL_FREIGHT -> R.drawable.in_progress
                    ServiceType.AIR_FREIGHT -> R.drawable.kilogram
                    ServiceType.CUSTOMS -> R.drawable.lock_outlined
                    else -> R.drawable.in_progress
                }
            )
        }

        Order(
            id = index,
            serviceList = serviceOrders,
            status = OrderStatus.entries[Random.nextInt(OrderStatus.entries.size)],
            amountCurrency = AmountCurrency(
                currency = Currency.USD,
                amount = 12200f
            )
            // cargoType будет определен автоматически на основе serviceList в data class
        )
    }
}