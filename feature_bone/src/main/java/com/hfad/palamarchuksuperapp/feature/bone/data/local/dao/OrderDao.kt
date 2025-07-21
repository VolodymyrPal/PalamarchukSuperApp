package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import androidx.room.Dao
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface OrderDao {
    val cachedOrders : Flow<List<Order>>
    val cachedOrderStatistics : Flow<OrderStatistics>
    fun ordersInRange(from: Date, to: Date) : Flow<List<Order>>
    suspend fun getOrderById(id: Int): Flow<Order?>
    suspend fun getOrdersByPage(size: Int) : List<Order>
    suspend fun geAllOrders(): List<Order>
    suspend fun insertOrIgnoreOrders(orders : List<Order>)
    suspend fun deleteAllOrders()

    suspend fun clearOrderStatistics()
    suspend fun insertOrIgnoreOrderStatistic(statistic : OrderStatistics)
}