package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import androidx.room.Dao
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleOrderDao {
    val saleOrders : Flow<List<SaleOrder>>
    val salesStatistics : Flow<SalesStatistics>
    suspend fun getSaleOrderById(id: Int): SaleOrder?
    suspend fun getAllSaleOrders(): List<SaleOrder>
    suspend fun insertOrIgnoreSaleOrders(orders : List<SaleOrder>)
    suspend fun deleteAllSaleOrders()
    suspend fun clearSalesStatistics()
    suspend fun insertOrIgnoreSalesStatistics(statistics : SalesStatistics)
}