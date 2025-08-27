package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEffect
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEvent
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.GenericViewModel
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateExchangeOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateFinanceStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.GetTypeTransactionOperationsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class FinancePageViewModel @Inject constructor(
    private val userRepository: AuthRepository,
    private val getTypeTransactionOperationsUseCase: GetTypeTransactionOperationsUseCase,
) : GenericViewModel<FinancePageState, FinancePageEvent, FinancePageEffect>() {
    override val _dataFlow: Flow<AppResult<List<TypedTransaction>, AppError>> =
        flow {
            val data = getTypeTransactionOperationsUseCase()
            emit(data)
        }

    private val searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    private val financeType: MutableStateFlow<List<TransactionType>> = MutableStateFlow(
        TransactionType.entries,
    )

    private val startDate: MutableStateFlow<Long> = MutableStateFlow(0)
    private val endDate: MutableStateFlow<Long> = MutableStateFlow(0)

    private val statisticFlow: Flow<AppResult<FinanceStatistics, AppError>> =
        flow { emit(AppResult.Success(generateFinanceStatistics())) } // paymentsRepository.paymentStatistics

    override val uiState: StateFlow<FinancePageState> =
        combine(
            _dataFlow,
            statisticFlow,
            searchQuery,
            financeType,
        ) { data, statistics, query, transactionTypes ->
            if (data is AppResult.Success) {
                val data = data.data.toMutableList().also {
                    it.addAll(generateExchangeOrderItems())
                }.shuffled()
                FinancePageState(
                    salesItems = data.filter { transactionTypes.contains(it.transactionType) },
                    financeTypeFilter = transactionTypes,
                    query = query,
                    financeStatistics = if (statistics is AppResult.Success) statistics.data else FinanceStatistics(),
                )
            } else {
                FinancePageState(
                    salesItems = emptyList(),
                )
            }
        }.onStart {
//            paymentsRepository.refreshStatistic()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            FinancePageState(),
        )

    override fun event(event: FinancePageEvent) {
        when (event) {
            is FinancePageEvent.LoadPayments -> {
            }

            is FinancePageEvent.RefreshPayments -> {
            }

            is FinancePageEvent.FilterFinanceType -> {
                financeType.update {
                    val existedStatuses = it.toMutableList()
                    if (event.status in existedStatuses) {
                        existedStatuses.remove(event.status)
                        return@update existedStatuses
                    } else {
                        existedStatuses.add(event.status)
                        return@update existedStatuses
                    }
                }
            }

            is FinancePageEvent.Search -> {
                searchQuery.update { event.query }
            }

            is FinancePageEvent.ChangeStartDate -> {
                startDate.update { event.startDate }
            }

            is FinancePageEvent.ChangeEndDate -> {
                endDate.update { event.endDate }
            }
        }
    }

    override val _errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)
    override val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
}

sealed class FinancePageEvent : BaseEvent {
    data class LoadPayments(
        val clientId: Int,
    ) : FinancePageEvent()

    data class RefreshPayments(
        val clientId: Int,
    ) : FinancePageEvent()

    data class FilterFinanceType(
        val status: TransactionType,
    ) : FinancePageEvent()

    data class Search(
        val query: String,
    ) : FinancePageEvent()

    data class ChangeStartDate(
        val startDate: Long,
    ) : FinancePageEvent()

    data class ChangeEndDate(
        val endDate: Long,
    ) : FinancePageEvent()
}

sealed class FinancePageEffect : BaseEffect {
    data class ShowPayment(
        val paymentId: Int,
    ) : FinancePageEffect()
}
