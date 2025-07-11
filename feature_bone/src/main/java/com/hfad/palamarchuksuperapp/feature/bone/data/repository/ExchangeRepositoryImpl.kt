package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.BoneDao
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.BoneApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.ExchangeRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class ExchangeRepositoryImpl @Inject constructor(
    private val boneDao: BoneDao,
    private val boneApi: BoneApi,
) : ExchangeRepository {

    override val exchanges: AppResult<Flow<List<ExchangeOrder>>, AppError> =
        trySqlApp {
            boneDao.exchanges
        }

    override suspend fun getExchangeById(id: Int): AppResult<ExchangeOrder, AppError> {
        return withSqlErrorHandling {
            boneDao.getExchangeById(id)!!
        }
    }

    override suspend fun softRefreshExchanges() {
        val exchangesResultApi = getExchangesResultApiWithError()
        if (exchangesResultApi is AppResult.Success) {
            boneDao.insertOrIgnoreExchanges(exchangesResultApi.data)
        }
    }

    override suspend fun hardRefreshExchanges() {
        boneDao.deleteAllExchanges()
        val exchangesResultApi = getExchangesResultApiWithError()
        if (exchangesResultApi is AppResult.Success) {
            boneDao.insertOrIgnoreExchanges(exchangesResultApi.data)
        }
    }

    override suspend fun syncedDateForPeriod(
        from: Date,
        to: Date,
    ): Flow<List<ExchangeOrder>> {
        TODO("Not yet implemented")
    }

    private suspend fun getExchangesResultApiWithError(): AppResult<List<ExchangeOrder>, AppError> {
        return safeApiCall {
            val exchanges: List<ExchangeOrder> = boneApi.getExchangesApi()
            AppResult.Success(exchanges)
        }
    }
}