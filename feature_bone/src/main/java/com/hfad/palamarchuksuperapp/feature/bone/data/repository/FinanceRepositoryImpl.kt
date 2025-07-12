package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.ExchangeRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.FinanceRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrdersRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.PaymentsRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.SalesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import java.util.Date
import javax.inject.Inject

class FinanceRepositoryImpl @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val ordersRepository: OrdersRepository,
    private val paymentsRepository: PaymentsRepository,
    private val salesRepository: SalesRepository,
) : FinanceRepository {

    override fun operationsFromTo(
        from: Date,
        to: Date,
    ): AppResult<Flow<List<TypedTransaction>>, AppError> {
        // Распаковка результатов
        val exchangeFlow = exchangeRepository.exchanges
        val ordersFlow = ordersRepository.orders
        val paymentsFlow = paymentsRepository.payments
        val salesFlow = salesRepository.saleOrders

        val error = listOf(exchangeFlow, ordersFlow, paymentsFlow, salesFlow)
            .firstOrNull { it is AppResult.Error } as? AppResult.Error

        if (error != null) {
            return AppResult.Error(error.error)
        }

        val combinedFlow = combine(
            (exchangeFlow as AppResult.Success).data,
            (ordersFlow as AppResult.Success).data,
            (paymentsFlow as AppResult.Success).data,
            (salesFlow as AppResult.Success).data
        ) { exchanges, orders, payments, sales ->
            (exchanges + orders + payments + sales)
                .filter { it.billingDate in from..to }
                .sortedByDescending { it.billingDate }
        }

        return AppResult.Success(combinedFlow)
    }

    override val statistic: AppResult<Flow<FinanceStatistic>, AppError> = trySqlApp {
//        boneDao.financeStatistic
        flowOf(FinanceStatistic()) //TODO
    }

    suspend fun syncData() {
//        exchangeRepository.syncData() //TODO
//        ordersRepository.syncData()
//        paymentsRepository.syncData()
//        salesRepository.syncData()
    }
}