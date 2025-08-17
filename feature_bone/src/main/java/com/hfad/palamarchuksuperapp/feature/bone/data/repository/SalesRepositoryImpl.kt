package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import android.database.SQLException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.hfad.palamarchuksuperapp.core.data.fetchWithCacheFallback
import com.hfad.palamarchuksuperapp.core.data.mapSQLException
import com.hfad.palamarchuksuperapp.core.data.tryApiRequest
import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.BoneDatabase
import com.hfad.palamarchuksuperapp.feature.bone.data.mediator.SaleRemoteMediator
import com.hfad.palamarchuksuperapp.feature.bone.data.toDomain
import com.hfad.palamarchuksuperapp.feature.bone.data.toEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.SaleOrderApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.SalesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class SalesRepositoryImpl @Inject constructor(
    private val boneDatabase: BoneDatabase,
    private val saleApi: SaleOrderApi,
) : SalesRepository {

    private val saleDao = boneDatabase.saleOrderDao()

    private val pagerCache = mutableMapOf<Pair<SaleStatus?, String>, Flow<PagingData<SaleOrder>>>()

    override fun pagingSales(status: SaleStatus?, query: String): Flow<PagingData<SaleOrder>> {
        val key = status to query
        return pagerCache.getOrPut(key) {
            Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = true),
                remoteMediator = SaleRemoteMediator(
                    saleApi = saleApi,
                    database = boneDatabase,
                    status = status,
                ),
                pagingSourceFactory = {
                    saleDao.getSalesWithPaging(status, query)
                },
            ).flow.map { pagingData ->
                pagingData.map { saleEntity ->
                    saleEntity.toDomain()
                }
            }
        }
    }

    override suspend fun saleOrdersInRange(
        from: Date,
        to: Date,
    ): AppResult<List<SaleOrder>, AppError> {

        return fetchWithCacheFallback(
            fetchRemote = { saleApi.getSalesWithRange(from, to).map { it.toDomain() } },
            storeAndRead = { sales ->
                boneDatabase.withTransaction {
                    saleDao.insertOrIgnoreSales(sales.map { it.toEntity() })
                    val localSales = saleDao.salesInRange(from, to)
                    localSales.map { it.toDomain() }
                }
            },
            fallbackFetch = {
                saleDao.salesInRange(from, to).map { it.toDomain() }
            }
        )
    }

    override suspend fun getSaleOrderById(id: Int): AppResult<SaleOrder?, AppError> {

        return fetchWithCacheFallback(
            fetchRemote = { saleApi.getSaleOrder(id)?.toDomain() },
            storeAndRead = {
                boneDatabase.withTransaction {
                    if (it != null) saleDao.insertOrIgnoreSales(listOf(it.toEntity()))
                    saleDao.getSaleOrderById(id)?.toDomain()
                }
            },
            fallbackFetch = {
                saleDao.getSaleOrderById(id)?.toDomain()
            }
        )
    }

    override suspend fun refreshStatistic(): AppResult<SalesStatistics, AppError> {
        val statisticApi = tryApiRequest { saleApi.getSalesStatistics() }
        return if (statisticApi is AppResult.Success) {
            val dbCall = withSqlErrorHandling {
                saleDao.insertOrUpdateStatistic(statisticApi.data.toEntity())
            }
            if (dbCall is AppResult.Error) {
                return AppResult.Error(dbCall.error, data = statisticApi.data)
            }
            AppResult.Success(statisticApi.data)
        } else {
            statisticApi as AppResult.Error
            AppResult.Error(statisticApi.error)
        }
    }

    override val salesStatistics: Flow<AppResult<SalesStatistics, AppError>> =
        saleDao.getStatistic().map {
            AppResult.Success<SalesStatistics, AppError>(it?.toDomain() ?: SalesStatistics())
        }.catch { e ->
            if (e is SQLException) {
                AppResult.Error<SalesStatistics, AppError>(mapSQLException(e))
            } else {
                AppResult.Error<SalesStatistics, AppError>(
                    AppError.CustomError(
                        "Unknown error",
                        cause = e
                    )
                )
            }
        }
}