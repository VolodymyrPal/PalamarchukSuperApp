package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_ORDERS
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_SERVICE_ORDERS
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_ORDER_STATISTICS
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntityWithServices
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderStatisticsEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.ServiceOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface OrderDao {

    @Transaction
    @Query(
        "SELECT DISTINCT o.* FROM $DATABASE_ORDERS o " +
                "LEFT JOIN $DATABASE_SERVICE_ORDERS s ON o.id = s.orderId " +
                "WHERE (:orderStatus IS NULL OR o.status = :orderStatus) " +
                "AND (:query IS NULL OR :query = '' OR " +
                "   LOWER(CAST(o.num AS TEXT)) LIKE '%' || LOWER(:query) || '%' OR " +
                "   LOWER(o.destinationPoint) LIKE '%' || LOWER(:query) || '%' OR " +
                "   LOWER(o.containerNumber) LIKE '%' || LOWER(:query) || '%' OR " +
                "   LOWER(o.departurePoint) LIKE '%' || LOWER(:query) || '%' OR " +
                //   "   CAST(o.sum AS TEXT) LIKE '%' || :query || '%' OR " + TODO if sum needed in search
                //   "   LOWER(o.currency) LIKE '%' || LOWER(:query) || '%' OR " + TODO if currency needed in search
                "   LOWER(o.cargo) LIKE '%' || LOWER(:query) || '%' OR " +
                "   LOWER(o.status) LIKE '%' || LOWER(:query) || '%' " + // Remove trailing OR
                ")"
    )
    fun getOrdersWithServices(
        orderStatus: OrderStatus?,
        query: String?,
    ): PagingSource<Int, OrderEntityWithServices>

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

    @Query("DELETE FROM $DATABASE_ORDER_STATISTICS")
    suspend fun clearOrderStatistics()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStatistic(statistic: OrderStatisticsEntity)

    @Query("SELECT * FROM $DATABASE_ORDER_STATISTICS WHERE id = 1")
    fun getStatistic(): Flow<OrderStatisticsEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertServices(services: List<ServiceOrderEntity>)
}