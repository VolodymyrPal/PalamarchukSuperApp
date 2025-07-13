package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.BoneDao
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.BoneApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateExchangeOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.typeApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.ExchangeRepository
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.ExchangeOrderCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Date
import javax.inject.Inject

class ExchangeRepositoryImpl @Inject constructor(
    private val boneDao: BoneDao,
    private val boneApi: BoneApi,
) : ExchangeRepository {

    override val exchanges: AppResult<Flow<List<ExchangeOrder>>, AppError> = trySqlApp {
        boneDao.exchanges
    }

    override suspend fun getExchangeById(id: Int): AppResult<ExchangeOrder, AppError> {
        return when (val exchangeDao = withSqlErrorHandling { boneDao.getExchangeById(id) }) {
            is AppResult.Success -> {
                exchangeDao.data?.let { AppResult.Success(it) } ?: fetchFromApiAndCache(id)
            }

            is AppResult.Error -> fetchFromApiAndCache(id)
        }
    }

    private suspend fun fetchFromApiAndCache(id: Int): AppResult<ExchangeOrder, AppError> {
        val key = typeApi(ExchangeOrder::class, id.toString())
        val exchangeApi = boneApi.getTypeOrderById(key)

        return if (exchangeApi == null || exchangeApi !is ExchangeOrder) {
            AppResult.Error(AppError.CustomError("Exchange not found"))
        } else {
            boneDao.insertOrIgnoreExchanges(listOf(exchangeApi))
            AppResult.Success(exchangeApi)
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
//        val syncedInfo = boneApi.getExchangeById()
        return flowOf(generateExchangeOrderItems())
    }

    private suspend fun getExchangesResultApiWithError(): AppResult<List<ExchangeOrder>, AppError> {
        return safeApiCall {
            val exchanges: List<ExchangeOrder> =
                generateExchangeOrderItems() //boneApi.getExchanges()
            AppResult.Success(exchanges)
        }
    }
}