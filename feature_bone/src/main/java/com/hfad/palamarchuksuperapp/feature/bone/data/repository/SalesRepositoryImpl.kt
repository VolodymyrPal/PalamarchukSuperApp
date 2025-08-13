package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.SaleOrderDao
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateSaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateSalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.SaleOrderApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.SalesRepository
import kotlinx.coroutines.flow.Flow

class SalesRepositoryImpl// @Inject constructor
    (
    private val boneControllerDao: SaleOrderDao,
    private val boneApi: SaleOrderApi,
) : SalesRepository {

    override val saleOrders: AppResult<Flow<List<SaleOrder>>, AppError> =
        trySqlApp {
            boneControllerDao.saleOrders
        }

    override val salesStatistics: AppResult<Flow<SalesStatistics>, AppError> =
        trySqlApp {
            boneControllerDao.salesStatistics
        }

    override suspend fun getSaleOrderById(id: Int): AppResult<SaleOrder, AppError> {
        return withSqlErrorHandling {
            generateSaleOrder()
//            boneDao.getSaleOrderById(id)
        }
    }

    override suspend fun softRefreshSaleOrders() {
        val saleOrdersResultApi = getSaleOrdersResultApiWithError()
        if (saleOrdersResultApi is AppResult.Success) {
            boneControllerDao.insertOrIgnoreSaleOrders(saleOrdersResultApi.data)
        }
        
        val salesStatisticsResultApi = getSalesStatisticsResultApiWithError()
        if (salesStatisticsResultApi is AppResult.Success) {
            boneControllerDao.insertOrIgnoreSalesStatistics(salesStatisticsResultApi.data)
        }
    }

    override suspend fun hardRefreshSaleOrders() {
        boneControllerDao.deleteAllSaleOrders()
        val saleOrdersResultApi = getSaleOrdersResultApiWithError()
        if (saleOrdersResultApi is AppResult.Success) {
            boneControllerDao.insertOrIgnoreSaleOrders(saleOrdersResultApi.data)
        }
        
        val salesStatisticsResultApi = getSalesStatisticsResultApiWithError()
        if (salesStatisticsResultApi is AppResult.Success) {
            boneControllerDao.insertOrIgnoreSalesStatistics(salesStatisticsResultApi.data)
        }
    }

    private suspend fun getSaleOrdersResultApiWithError(): AppResult<List<SaleOrder>, AppError> {
        return safeApiCall {
//            val saleOrders: List<SaleOrder> = boneApi.getSaleOrdersByPage(1)
            AppResult.Success(emptyList())
        }
    }
    
    private suspend fun getSalesStatisticsResultApiWithError(): AppResult<SalesStatistics, AppError> {
        return safeApiCall {
            val salesStatistics: SalesStatistics =
                generateSalesStatistics()// boneApi.syncSaleStatistics()
            AppResult.Success(salesStatistics)
        }
    }
}