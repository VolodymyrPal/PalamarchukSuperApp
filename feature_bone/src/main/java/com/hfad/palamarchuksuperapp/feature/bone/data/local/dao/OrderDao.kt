package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.ORDERS
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface OrderDao {
    @Query("SELECT * FROM $ORDERS WHERE :orderStatus IS NULL OR status = :orderStatus")
    fun getOrders(orderStatus: OrderStatus?): PagingSource<Int, Order>

    fun ordersInRange(from: Date, to: Date): Flow<List<Order>>
    suspend fun getOrderById(id: Int): Flow<Order?>
    suspend fun geAllOrders(): List<Order>
    suspend fun insertOrIgnoreOrders(orders: List<Order>)
    suspend fun deleteAllOrders()

    suspend fun clearOrderStatistics()
    suspend fun insertOrIgnoreOrderStatistic(statistic: OrderStatistics)
}