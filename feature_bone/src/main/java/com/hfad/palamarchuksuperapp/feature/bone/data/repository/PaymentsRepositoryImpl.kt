package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import android.database.SQLException
import android.util.Log
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
import com.hfad.palamarchuksuperapp.feature.bone.data.mediator.PaymentRemoteMediator
import com.hfad.palamarchuksuperapp.feature.bone.data.toDomain
import com.hfad.palamarchuksuperapp.feature.bone.data.toEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.PaymentOrderApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.PaymentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PaymentsRepositoryImpl @Inject constructor(
    private val boneDatabase: BoneDatabase,
    private val paymentApi: PaymentOrderApi,
) : PaymentsRepository {

    private val paymentDao = boneDatabase.paymentOrderDao()

    @OptIn(ExperimentalPagingApi::class)
    private val pagerCache =
        mutableMapOf<Pair<List<PaymentStatus>, String>, Flow<PagingData<PaymentOrder>>>()

    @OptIn(ExperimentalPagingApi::class)
    override fun pagingPayments(
        status: List<PaymentStatus>,
        query: String,
    ): Flow<PagingData<PaymentOrder>> {
        val key = status to query
        return pagerCache.getOrPut(key) {
            Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = true),
                remoteMediator = PaymentRemoteMediator(
                    paymentApi = paymentApi,
                    database = boneDatabase,
                    status = status,
                ),
                pagingSourceFactory = {
                    paymentDao.getPaymentsWithPaging(status.takeIf { it.isNotEmpty() }, query)
                },
            ).flow.map { pagingData ->
                pagingData.map { paymentEntity ->
                    paymentEntity.toDomain()
                }
            }
        }
    }

    override suspend fun getTypedTransactionInRange(
        from: Long,
        to: Long
    ): AppResult<List<TypedTransaction>, AppError> {
        return fetchWithCacheFallback(
            fetchRemote = { paymentApi.getPaymentsWithRange(from, to).map { it.toDomain() } },
            storeAndRead = { payments ->
                boneDatabase.withTransaction {
                    paymentDao.insertOrIgnorePayments(payments.map { it.toEntity() })
                    val localPayments = paymentDao.paymentsInRange(from, to)
                    localPayments.map { it.toDomain() }
                }
            },
            fallbackFetch = {
                paymentDao.paymentsInRange(from, to).map { it.toDomain() }
            }
        )
    }

    override suspend fun getPaymentById(id: Int): AppResult<PaymentOrder?, AppError> {

        return fetchWithCacheFallback(
            fetchRemote = { paymentApi.getPaymentOrder(id)?.toDomain() },
            storeAndRead = {
                boneDatabase.withTransaction {
                    if (it != null) paymentDao.insertOrIgnorePayments(listOf(it.toEntity()))
                    paymentDao.getPaymentOrderById(id)?.toDomain()
                }
            },
            fallbackFetch = {
                paymentDao.getPaymentOrderById(id)?.toDomain()
            }
        )
    }

    override suspend fun refreshStatistic(): AppResult<PaymentStatistic, AppError> {
        val statisticApi = tryApiRequest { paymentApi.getPaymentStatistics() }
        return if (statisticApi is AppResult.Success) {
            val dbCall = withSqlErrorHandling {
                paymentDao.insertOrUpdateStatistic(statisticApi.data.toEntity())
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

    override val paymentStatistics: Flow<AppResult<PaymentStatistic, AppError>> =
        paymentDao.getStatistic().map {
            Log.d("PaymentsRepositoryImpl", "Statistic: $it")
            AppResult.Success<PaymentStatistic, AppError>(it?.toDomain() ?: PaymentStatistic())
        }.catch { e ->
            if (e is SQLException) {
                AppResult.Error<PaymentStatistic, AppError>(mapSQLException(e))
            } else {
                AppResult.Error<PaymentStatistic, AppError>(
                    AppError.CustomError(
                        "Unknown error",
                        cause = e
                    )
                )
            }
        }
}