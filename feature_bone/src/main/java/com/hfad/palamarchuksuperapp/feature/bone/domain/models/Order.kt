package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import androidx.room.PrimaryKey
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
    override val amountCurrency: AmountCurrency = AmountCurrency(
        currency = Currency.USD,
        amount = 12200f
    ),
    override val billingDate: Date = Date(),
    override val type: TransactionType = TransactionType.CREDIT,
) : TypedTransaction

enum class OrderStatus {
    CREATED, CALCULATED, IN_PROGRESS, DONE
}

enum class CargoType {
    ANY, CONTAINER, TRUCK, AIR
}