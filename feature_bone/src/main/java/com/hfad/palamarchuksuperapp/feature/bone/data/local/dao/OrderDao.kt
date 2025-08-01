package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_ORDERS
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_REMOTE_KEYS
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.OrderRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntityWithServices
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import java.util.Date

@Dao
interface OrderDao {

    @Transaction
    @Query("SELECT * FROM $DATABASE_ORDERS WHERE :orderStatus IS NULL OR status = :orderStatus")
    fun getOrdersWithServices(orderStatus: OrderStatus?): PagingSource<Int, OrderEntityWithServices>


    fun ordersInRange(from: Date, to: Date): List<OrderEntityWithServices>
    suspend fun getOrderById(id: Int): OrderEntityWithServices?
    suspend fun geAllOrders(): List<OrderEntityWithServices>
    suspend fun insertOrIgnoreOrders(orders: List<OrderEntityWithServices>)
    suspend fun deleteAllOrders()
    suspend fun deleteOrdersByStatus(status: OrderStatus?)

    suspend fun clearOrderStatistics()
    suspend fun getOrderStatistics(): OrderStatistics
    suspend fun insertOrIgnoreOrderStatistic(statistic: OrderStatistics)
}

@Dao
interface RemoteKeysDao {

    @Query("SELECT * FROM $DATABASE_REMOTE_KEYS WHERE id = :orderId AND `filter` = :filterStatus")
    suspend fun remoteKeysOrderId(orderId: Int, filterStatus: OrderStatus?): OrderRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(keys: List<OrderRemoteKeys>)

    @Query("DELETE FROM $DATABASE_REMOTE_KEYS where `filter` = :status")
    suspend fun clearRemoteKeysByStatus(status: OrderStatus?)

    @Delete
    suspend fun deleteAll()
}