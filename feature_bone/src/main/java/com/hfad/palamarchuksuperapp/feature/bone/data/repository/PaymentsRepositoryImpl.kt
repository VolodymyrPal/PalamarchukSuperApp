package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.PaymentOrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.PaymentOrderApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generatePaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.PaymentsRepository
import kotlinx.coroutines.flow.Flow

class PaymentsRepositoryImpl //@Inject constructor
    (
    private val boneControllerDao: PaymentOrderDao,
    private val boneApi: PaymentOrderApi,
) : PaymentsRepository {

    override val payments: AppResult<Flow<List<PaymentOrder>>, AppError> = trySqlApp {
        boneControllerDao.paymentOrders
    }

    override val paymentStatistic: AppResult<Flow<PaymentStatistic>, AppError> =
        trySqlApp {
            boneControllerDao.paymentStatistics
        }

    override suspend fun getPaymentById(id: Int): AppResult<PaymentOrder, AppError> = withSqlErrorHandling {
            boneControllerDao.getPaymentOrderById(id)!!
        }

    override suspend fun softRefreshPayments() {
        val paymentsResultApi = getPaymentsResultApiWithError()
        if (paymentsResultApi is AppResult.Success) {
            boneControllerDao.insertOrIgnorePaymentOrders(paymentsResultApi.data)
        }

        val paymentStatisticResultApi = getPaymentStatisticResultApiWithError()
        if (paymentStatisticResultApi is AppResult.Success) {
            boneControllerDao.insertOrIgnorePaymentStatistics(paymentStatisticResultApi.data)
        }
    }

    override suspend fun hardRefreshPayments() {
        boneControllerDao.deleteAllPaymentOrders()
        val paymentsResultApi = getPaymentsResultApiWithError()
        if (paymentsResultApi is AppResult.Success) {
            boneControllerDao.insertOrIgnorePaymentOrders(paymentsResultApi.data)
        }

        val paymentStatisticResultApi = getPaymentStatisticResultApiWithError()
        if (paymentStatisticResultApi is AppResult.Success) {
            boneControllerDao.insertOrIgnorePaymentStatistics(paymentStatisticResultApi.data)
        }
    }

    private suspend fun getPaymentsResultApiWithError(): AppResult<List<PaymentOrder>, AppError> {
        return safeApiCall {
//            val payments: List<PaymentOrder> = boneApi.getPaymentOrdersByPage(1)
            AppResult.Success(emptyList())
        }
    }

    private suspend fun getPaymentStatisticResultApiWithError(): AppResult<List<PaymentStatistic>, AppError> {
        return safeApiCall {
//            val paymentStatistic: PaymentStatistic = boneApi.syncPaymentStatistic()
            AppResult.Success(listOf(generatePaymentStatistic()))
        }
    }
}