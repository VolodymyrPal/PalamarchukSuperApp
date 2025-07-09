package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.BoneDao
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.BoneApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.SalesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SalesRepositoryImpl @Inject constructor(
    private val boneDao: BoneDao,
    private val boneApi: BoneApi,
) : SalesRepository {

    override val saleOrders: AppResult<Flow<List<SaleOrder>>, AppError> =
        trySqlApp {
            boneDao.saleOrders
        }

    override val salesStatistics: AppResult<Flow<SalesStatistics>, AppError> =
        trySqlApp {
            boneDao.salesStatistics
        }

    override suspend fun getSaleOrderById(id: Int): AppResult<SaleOrder, AppError> {
        return withSqlErrorHandling {
            boneDao.getSaleOrderById(id)
        }
    }

    override suspend fun softRefreshSaleOrders() {
        val saleOrdersResultApi = getSaleOrdersResultApiWithError()
        if (saleOrdersResultApi is AppResult.Success) {
            boneDao.insertOrIgnoreSaleOrders(saleOrdersResultApi.data)
        }
        
        val salesStatisticsResultApi = getSalesStatisticsResultApiWithError()
        if (salesStatisticsResultApi is AppResult.Success) {
            boneDao.insertOrIgnoreSalesStatistics(salesStatisticsResultApi.data)
        }
    }

    override suspend fun hardRefreshSaleOrders() {
        boneDao.deleteAllSaleOrders()
        val saleOrdersResultApi = getSaleOrdersResultApiWithError()
        if (saleOrdersResultApi is AppResult.Success) {
            boneDao.insertOrIgnoreSaleOrders(saleOrdersResultApi.data)
        }
        
        val salesStatisticsResultApi = getSalesStatisticsResultApiWithError()
        if (salesStatisticsResultApi is AppResult.Success) {
            boneDao.insertOrIgnoreSalesStatistics(salesStatisticsResultApi.data)
        }
    }

    private suspend fun getSaleOrdersResultApiWithError(): AppResult<List<SaleOrder>, AppError> {
        return safeApiCall {
            val saleOrders: List<SaleOrder> = boneApi.getSaleOrdersApi()
            AppResult.Success(saleOrders)
        }
    }
    
    private suspend fun getSalesStatisticsResultApiWithError(): AppResult<SalesStatistics, AppError> {
        return safeApiCall {
            val salesStatistics: SalesStatistics = boneApi.getSalesStatisticsApi()
            AppResult.Success(salesStatistics)
        }
    }
}