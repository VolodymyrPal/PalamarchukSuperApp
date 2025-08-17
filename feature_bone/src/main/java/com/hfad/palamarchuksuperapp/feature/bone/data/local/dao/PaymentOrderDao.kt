package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_PAYMENTS
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_PAYMENTS_STATISTICS
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.PaymentOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.statistics.PaymentStatisticEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

// TODO change all dao class to update existed objects
@Dao
interface PaymentOrderDao {

    @Query(
        "SELECT * FROM $DATABASE_PAYMENTS " +
                "WHERE (:paymentStatus IS NULL OR status = :paymentStatus) " +
                "AND (:query IS NULL OR :query = '' OR " +
                "   LOWER(CAST(id AS TEXT)) LIKE '%' || LOWER(:query) || '%' OR " +
                "   LOWER(factory) LIKE '%' || LOWER(:query) || '%' OR " +
                "   LOWER(productType) LIKE '%' || LOWER(:query) || '%' OR " +
                "   LOWER(status) LIKE '%' || LOWER(:query) || '%' " +
                ")"
    )
    fun getPaymentsWithPaging(
        paymentStatus: PaymentStatus?,
        query: String?,
    ): PagingSource<Int, PaymentOrderEntity>

    @Query("SELECT * FROM $DATABASE_PAYMENTS WHERE billingDate BETWEEN :from AND :to")
    suspend fun paymentsInRange(from: Date, to: Date): List<PaymentOrderEntity>

    @Query("SELECT * FROM $DATABASE_PAYMENTS WHERE id = :id")
    suspend fun getPaymentOrderById(id: Int): PaymentOrderEntity?

    @Query("SELECT * FROM $DATABASE_PAYMENTS")
    suspend fun getAllPayments(): List<PaymentOrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrIgnorePayments(payments: List<PaymentOrderEntity>)

    @Query("DELETE FROM $DATABASE_PAYMENTS")
    suspend fun deleteAllPayments()

    @Query("DELETE FROM $DATABASE_PAYMENTS WHERE :status IS NULL OR status = :status")
    suspend fun deletePaymentsByStatus(status: PaymentStatus?)

    @Query("DELETE FROM $DATABASE_PAYMENTS_STATISTICS")
    suspend fun clearPaymentStatistics()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStatistic(statistic: PaymentStatisticEntity)

    @Query("SELECT * FROM $DATABASE_PAYMENTS_STATISTICS WHERE id = 1")
    fun getStatistic(): Flow<PaymentStatisticEntity?>
}