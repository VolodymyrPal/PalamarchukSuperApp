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
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_STATISTICS
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.OrderRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntityWithServices
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.ServiceOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import java.util.Date

@Dao
interface OrderDao {

    @Transaction
    @Query("SELECT * FROM $DATABASE_ORDERS WHERE :orderStatus IS NULL OR status = :orderStatus")
    fun getOrdersWithServices(orderStatus: OrderStatus?): PagingSource<Int, OrderEntityWithServices>

    @Transaction
    @Query("SELECT * FROM $DATABASE_ORDERS WHERE arrivalDate BETWEEN :from AND :to")
    suspend fun ordersInRange(from: Date, to: Date): List<OrderEntityWithServices>

    @Transaction
    @Query("SELECT * FROM $DATABASE_ORDERS WHERE id = :id")
    suspend fun getOrderById(id: Int): OrderEntityWithServices?

    @Transaction
    @Query("SELECT * FROM $DATABASE_ORDERS")
    suspend fun geAllOrders(): List<OrderEntityWithServices>

    @Transaction
    suspend fun insertOrIgnoreOrders(orders: List<OrderEntityWithServices>) {
        orders.forEach { orderWithServices ->
            insertOrder(orderWithServices.order)
            insertServices(orderWithServices.services)
        }
    }

    @Query("DELETE FROM $DATABASE_ORDERS")
    suspend fun deleteAllOrders()

    @Query("DELETE FROM $DATABASE_ORDERS WHERE :status IS NULL OR status = :status")
    suspend fun deleteOrdersByStatus(status: OrderStatus?)

    @Query("DELETE FROM $DATABASE_STATISTICS")
    suspend fun clearOrderStatistics()

    @Query("SELECT * FROM $DATABASE_STATISTICS LIMIT 1")
    suspend fun getOrderStatistics(): OrderStatistics

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreOrderStatistic(statistic: OrderStatistics)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertServices(services: List<ServiceOrderEntity>)
}

@Dao
interface RemoteKeysDao {

    @Query("SELECT * FROM $DATABASE_REMOTE_KEYS WHERE id = :orderId AND `filter` = :filterStatus")
    suspend fun remoteKeysOrderId(orderId: Int, filterStatus: OrderStatus?): OrderRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(keys: List<OrderRemoteKeys>)

    @Query("DELETE FROM $DATABASE_REMOTE_KEYS where `filter` = :status")
    suspend fun clearRemoteKeysByStatus(status: OrderStatus?)

    @Query("DELETE FROM $DATABASE_REMOTE_KEYS")
    suspend fun clearAllRemoteKeys()

}