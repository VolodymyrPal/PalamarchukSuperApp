package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_SALES
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_SALES_STATISTICS
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.SaleOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.statistics.SalesStatisticsEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

// TODO change all dao class to update existed objects
@Dao
interface SaleOrderDao {

    @Query(
        "SELECT * FROM $DATABASE_SALES " +
                "WHERE (:saleStatus IS NULL OR status = :saleStatus) " +
                "AND (:query IS NULL OR :query = '' OR " +
                "   LOWER(CAST(id AS TEXT)) LIKE '%' || LOWER(:query) || '%' OR " +
                "   LOWER(productName) LIKE '%' || LOWER(:query) || '%' OR " +
                "   LOWER(cargoCategory) LIKE '%' || LOWER(:query) || '%' OR " +
                "   LOWER(customerName) LIKE '%' || LOWER(:query) || '%' OR " +
                "   LOWER(companyName) LIKE '%' || LOWER(:query) || '%' OR " +
                "   LOWER(status) LIKE '%' || LOWER(:query) || '%' " +
                ")"
    )
    fun getSalesWithPaging(
        saleStatus: SaleStatus?,
        query: String?,
    ): PagingSource<Int, SaleOrderEntity>

    @Query("SELECT * FROM $DATABASE_SALES WHERE billingDate BETWEEN :from AND :to")
    suspend fun salesInRange(from: Date, to: Date): List<SaleOrderEntity>

    @Query("SELECT * FROM $DATABASE_SALES WHERE id = :id")
    suspend fun getSaleOrderById(id: Int): SaleOrderEntity?

    @Query("SELECT * FROM $DATABASE_SALES")
    suspend fun getAllSales(): List<SaleOrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrIgnoreSales(sales: List<SaleOrderEntity>)

    @Query("DELETE FROM $DATABASE_SALES")
    suspend fun deleteAllSales()

    @Query("DELETE FROM $DATABASE_SALES WHERE :status IS NULL OR status = :status")
    suspend fun deleteSalesByStatus(status: SaleStatus?)

    @Query("DELETE FROM $DATABASE_SALES_STATISTICS")
    suspend fun clearSalesStatistics()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStatistic(statistic: SalesStatisticsEntity)

    @Query("SELECT * FROM $DATABASE_SALES_STATISTICS WHERE id = 1")
    fun getStatistic(): Flow<SalesStatisticsEntity?>
}