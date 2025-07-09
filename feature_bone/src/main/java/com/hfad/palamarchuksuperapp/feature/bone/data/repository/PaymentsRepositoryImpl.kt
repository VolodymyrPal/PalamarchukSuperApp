package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.BoneDao
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.BoneApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.PaymentsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PaymentsRepositoryImpl @Inject constructor(
    private val boneDao: BoneDao,
    private val boneApi: BoneApi,
) : PaymentsRepository {

    override val payment: AppResult<Flow<List<PaymentOrder>>, AppError> =
        trySqlApp {
            boneDao.paymentOrders
        }

    override val paymentStatistic: AppResult<Flow<List<PaymentStatistic>>, AppError> =
        trySqlApp {
            boneDao.paymentStatistics
        }

    override suspend fun getPaymentById(id: Int): AppResult<PaymentOrder, AppError> {
        return withSqlErrorHandling {
            boneDao.getPaymentOrderById(id)
        }
    }

    override suspend fun softRefreshPayments() {
        val paymentsResultApi = getPaymentsResultApiWithError()
        if (paymentsResultApi is AppResult.Success) {
            boneDao.insertOrIgnorePaymentOrders(paymentsResultApi.data)
        }

        val paymentStatisticResultApi = getPaymentStatisticResultApiWithError()
        if (paymentStatisticResultApi is AppResult.Success) {
            boneDao.insertOrIgnorePaymentStatistics(paymentStatisticResultApi.data)
        }
    }

    override suspend fun hardRefreshPayments() {
        boneDao.deleteAllPaymentOrders()
        val paymentsResultApi = getPaymentsResultApiWithError()
        if (paymentsResultApi is AppResult.Success) {
            boneDao.insertOrIgnorePaymentOrders(paymentsResultApi.data)
        }

        val paymentStatisticResultApi = getPaymentStatisticResultApiWithError()
        if (paymentStatisticResultApi is AppResult.Success) {
            boneDao.insertOrIgnorePaymentStatistics(paymentStatisticResultApi.data)
        }
    }

    private suspend fun getPaymentsResultApiWithError(): AppResult<List<PaymentOrder>, AppError> {
        return safeApiCall {
            val payments: List<PaymentOrder> = boneApi.getPaymentOrdersApi()
            AppResult.Success(payments)
        }
    }

    private suspend fun getPaymentStatisticResultApiWithError(): AppResult<List<PaymentStatistic>, AppError> {
        return safeApiCall {
            val paymentStatistic: PaymentStatistic = boneApi.getPaymentStatisticApi()
            AppResult.Success(listOf(paymentStatistic))
        }
    }
}