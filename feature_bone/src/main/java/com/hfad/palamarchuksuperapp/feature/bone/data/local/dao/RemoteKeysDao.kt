package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_REMOTE_KEYS_ORDER
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_REMOVE_KEYS_PAYMENT
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_REMOVE_KEYS_SALES
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys.OrderRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys.PaymentRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys.SaleRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus

@Dao
interface RemoteKeysDao {

    // Order Remote Keys
    @Query("SELECT * FROM $DATABASE_REMOTE_KEYS_ORDER WHERE id = :orderId AND status = :filterStatus")
    suspend fun remoteKeysOrderId(orderId: Int, filterStatus: OrderStatus?): OrderRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(keys: List<OrderRemoteKeys>)

    @Query("DELETE FROM $DATABASE_REMOTE_KEYS_ORDER where status = :status")
    suspend fun clearRemoteKeysByStatus(status: OrderStatus?)

    @Query("DELETE FROM $DATABASE_REMOTE_KEYS_ORDER")
    suspend fun clearAllRemoteKeys()

    //     Sales Remote Keys
    @Query("SELECT * FROM $DATABASE_REMOVE_KEYS_SALES WHERE id = :saleId AND `status` = :filterStatus")
    suspend fun remoteKeysSaleId(saleId: Int, filterStatus: SaleStatus?): SaleRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAllSaleKeys(keys: List<SaleRemoteKeys>)

    @Query("DELETE FROM $DATABASE_REMOVE_KEYS_SALES where status = :status")
    suspend fun clearSaleRemoteKeysByStatus(status: SaleStatus?)

    @Query("DELETE FROM $DATABASE_REMOVE_KEYS_SALES")
    suspend fun clearAllSaleRemoteKeys()

    // Payment Remote Keys
    @Query("SELECT * FROM $DATABASE_REMOVE_KEYS_PAYMENT WHERE id = :paymentId AND status = :filterStatus")
    suspend fun remoteKeysPaymentId(
        paymentId: Int,
        filterStatus: PaymentStatus?,
    ): PaymentRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAllPaymentKeys(keys: List<PaymentRemoteKeys>)

    @Query("DELETE FROM $DATABASE_REMOVE_KEYS_PAYMENT where status = :status")
    suspend fun clearPaymentRemoteKeysByStatus(status: PaymentStatus?)

    @Query("DELETE FROM $DATABASE_REMOVE_KEYS_PAYMENT")
    suspend fun clearAllPaymentRemoteKeys()
}