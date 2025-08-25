package com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto


import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.CargoType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ServiceOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ServiceType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType

data class OrderDto(
    val id: Int,
    val businessEntityNum: Int,
    val num: Int,
    val serviceList: List<ServiceOrder> = emptyList(),
    val status: OrderStatus = OrderStatus.CREATED,
    val destinationPoint: String,
    val arrivalDate: Long,
    val containerNumber: String = "",
    val departurePoint: String,
    val cargo: String,
    val manager: String,
    val amountCurrency: AmountCurrency = AmountCurrency(
        currency = Currency.USD,
        amount = 0f
    ),
    val billingDate: Long,
    val transactionType: TransactionType = TransactionType.DEBIT,
    val versionHash: String = ""
) {
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