package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_REMOTE_KEYS
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_SALES
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.OrderRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.PaymentRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.SaleRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus

@Dao
interface RemoteKeysDao {

    // Order Remote Keys
    @Query("SELECT * FROM ${DATABASE_REMOTE_KEYS} WHERE id = :orderId AND `filter` = :filterStatus")
    suspend fun remoteKeysOrderId(orderId: Int, filterStatus: OrderStatus?): OrderRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(keys: List<OrderRemoteKeys>)

    @Query("DELETE FROM ${DATABASE_REMOTE_KEYS} where `filter` = :status")
    suspend fun clearRemoteKeysByStatus(status: OrderStatus?)

    @Query("DELETE FROM ${DATABASE_REMOTE_KEYS}")
    suspend fun clearAllRemoteKeys()

    // Sales Remote Keys
    @Query("SELECT * FROM ${DATABASE_SALES} WHERE id = :saleId AND `status` = :filterStatus")
    suspend fun remoteKeysSaleId(saleId: Int, filterStatus: SaleStatus?): SaleRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAllSaleKeys(keys: List<SaleRemoteKeys>)

    @Query("DELETE FROM remote_sale_keys where `filter` = :status")
    suspend fun clearSaleRemoteKeysByStatus(status: SaleStatus?)

    @Query("DELETE FROM remote_sale_keys")
    suspend fun clearAllSaleRemoteKeys()

    // Payment Remote Keys
    @Query("SELECT * FROM remote_payment_keys WHERE id = :paymentId AND `filter` = :filterStatus")
    suspend fun remoteKeysPaymentId(paymentId: Int, filterStatus: PaymentStatus?): PaymentRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAllPaymentKeys(keys: List<PaymentRemoteKeys>)

    @Query("DELETE FROM remote_payment_keys where `filter` = :status")
    suspend fun clearPaymentRemoteKeysByStatus(status: PaymentStatus?)

    @Query("DELETE FROM remote_payment_keys")
    suspend fun clearAllPaymentRemoteKeys()
}