package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import android.database.SQLException
import com.hfad.palamarchuksuperapp.core.data.mapSQLException
import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.BoneDao
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.BoneApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.CashRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CashRepositoryImpl @Inject constructor(
    private val boneDao: BoneDao,
    private val boneApi: BoneApi,
) : CashRepository {

    override val cashPaymentOrders: AppResult<Flow<List<PaymentOrder>>, AppError> =
        trySqlApp {
            boneDao.getPaymentOrders
        }

    override suspend fun softRefreshOrders() {
        val paymentOrdersResultApi = getPaymentOrdersResultApiWithError()
        if (paymentOrdersResultApi is AppResult.Success) {
            boneDao.insertOrIgnorePaymentOrders(paymentOrdersResultApi.data)
        }
    }

    override suspend fun hardRefreshOrders() {
        boneDao.deleteAllPaymentOrders()
        val paymentOrdersResultApi = getPaymentOrdersResultApiWithError()
        if (paymentOrdersResultApi is AppResult.Success) {
            boneDao.insertOrIgnorePaymentOrders(paymentOrdersResultApi.data)
        }
    }

    private suspend fun getPaymentOrdersResultApiWithError(): AppResult<List<PaymentOrder>, AppError> {
        return safeApiCall {
            val paymentOrders: List<PaymentOrder> = boneApi.getPaymentOrdersApi()
            AppResult.Success(paymentOrders)
        }
    }
}


fun <T> trySqlApp(
    block: () -> T,
): AppResult<T, AppError> {
    return try {
        AppResult.Success(block())
    } catch (e: SQLException) {
        AppResult.Error(mapSQLException(e))
    }
}