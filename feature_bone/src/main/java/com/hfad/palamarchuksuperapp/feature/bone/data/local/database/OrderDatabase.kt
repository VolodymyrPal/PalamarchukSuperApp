package com.hfad.palamarchuksuperapp.feature.bone.data.local.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.ExchangeOrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.FinanceOperationDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.GeneralDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.OrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.PaymentOrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.SaleOrderDao
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics

@Database(
    entities = [Order::class, OrderRemoteKeys::class, OrderStatistics::class,
        ExchangeOrder::class, PaymentOrder::class, PaymentStatistic::class, SaleOrder::class, SalesStatistics::class],
    version = 1,
    exportSchema = true
)
abstract class BoneDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun exchangeOrderDao(): ExchangeOrderDao
    abstract fun financeOperationDao(): FinanceOperationDao
    abstract fun paymentOrderDao(): PaymentOrderDao
    abstract fun saleOrderDao(): SaleOrderDao
    abstract fun generalDao(): GeneralDao
}

@Dao
interface RemoteKeysDao {

    @Query("SELECT * FROM remote_order_keys WHERE orderId = :orderId")
    suspend fun remoteKeysOrderId(orderId: String): OrderRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<OrderRemoteKeys>)

    @Query("DELETE FROM remote_order_keys")
    suspend fun clearRemoteKeys()
}

@Entity(tableName = "remote_order_keys")
data class OrderRemoteKeys(
    @PrimaryKey val orderId: String,
    val prevKey: Int?,
    val nextKey: Int?,
)

const val DATABASE_MAIN_ENTITY_ORDER = "bonedatabase"