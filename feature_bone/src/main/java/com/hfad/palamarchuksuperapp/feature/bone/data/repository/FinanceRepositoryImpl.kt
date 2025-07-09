package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.BoneDao
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.BoneApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FinanceRepositoryImpl @Inject constructor(
    private val boneDao: BoneDao,
    private val boneApi: BoneApi,
) : FinanceRepository {

    override val operations: AppResult<Flow<List<TypedTransaction>>, AppError> =
        trySqlApp {
            boneDao.operations
        }

    override val statistic: AppResult<Flow<FinanceStatistic>, AppError> =
        trySqlApp {
            boneDao.financeStatistic
        }

    override suspend fun getOperationById(id: Int): AppResult<TypedTransaction, AppError> {
        return withSqlErrorHandling {
            boneDao.getOperationById(id)
        }
    }

    override suspend fun softRefreshOperations() {
        val operationsResultApi = getOperationsResultApiWithError()
        if (operationsResultApi is AppResult.Success) {
            boneDao.insertOrIgnoreOperations(operationsResultApi.data)
        }
        
        val financeStatisticResultApi = getFinanceStatisticResultApiWithError()
        if (financeStatisticResultApi is AppResult.Success) {
            boneDao.insertOrIgnoreFinanceStatistic(financeStatisticResultApi.data)
        }
    }

    override suspend fun hardRefreshOperations() {
        boneDao.deleteAllOperations()
        val operationsResultApi = getOperationsResultApiWithError()
        if (operationsResultApi is AppResult.Success) {
            boneDao.insertOrIgnoreOperations(operationsResultApi.data)
        }
        
        val financeStatisticResultApi = getFinanceStatisticResultApiWithError()
        if (financeStatisticResultApi is AppResult.Success) {
            boneDao.insertOrIgnoreFinanceStatistic(financeStatisticResultApi.data)
        }
    }

    private suspend fun getOperationsResultApiWithError(): AppResult<List<TypedTransaction>, AppError> {
        return safeApiCall {
            val operations: List<TypedTransaction> = boneApi.getOperationsApi()
            AppResult.Success(operations)
        }
    }
    
    private suspend fun getFinanceStatisticResultApiWithError(): AppResult<FinanceStatistic, AppError> {
        return safeApiCall {
            val financeStatistic: FinanceStatistic = boneApi.getFinanceStatisticApi()
            AppResult.Success(financeStatistic)
        }
    }
} 