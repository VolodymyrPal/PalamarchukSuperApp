package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEffect
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEvent
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.GenericViewModel
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.UserSession
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.GetTypeTransactionOperationsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class FinancePageViewModel @Inject constructor(
    private val userRepository: AuthRepository,
    private val getTypeTransactionOperationsUseCase: GetTypeTransactionOperationsUseCase,
) : GenericViewModel<FinancePageState, FinancePageViewModel.FinancePageEvent, FinancePageViewModel.FinancePageEffect>() {

    init {
        viewModelScope.launch { getTypeTransactionOperationsUseCase() }
    }

    override val _dataFlow: Flow<UserSession> = flow { }

    private val searchQuery: MutableStateFlow<String> = MutableStateFlow("")

    private val startDate: MutableStateFlow<Long> = MutableStateFlow(0)
    private val endDate: MutableStateFlow<Long> = MutableStateFlow(0)

    private val statisticFlow: Flow<AppResult<FinanceStatistics, AppError>> =
        flow { emit(AppResult.Success(FinanceStatistics())) } //paymentsRepository.paymentStatistics

    override val uiState: StateFlow<FinancePageState> =
        combine(
            _dataFlow, statisticFlow, searchQuery
        ) { data, statistics, query ->

            FinancePageState(
            )
        }.onStart {
//            paymentsRepository.refreshStatistic()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            FinancePageState()
        )

    override fun event(event: FinancePageEvent) {
        when (event) {
            is FinancePageEvent.LoadPayments -> {

            }

            is FinancePageEvent.RefreshPayments -> {

            }

            is FinancePageEvent.FilterPaymentStatus -> {
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

    sealed class FinancePageEvent : BaseEvent {
        data class LoadPayments(val clientId: Int) : FinancePageEvent()
        data class RefreshPayments(val clientId: Int) : FinancePageEvent()
        data class FilterPaymentStatus(val status: PaymentStatus) : FinancePageEvent()
        data class Search(val query: String) : FinancePageEvent()
        data class ChangeStartDate(val startDate: Long) : FinancePageEvent()
        data class ChangeEndDate(val endDate: Long) : FinancePageEvent()
    }

    sealed class FinancePageEffect : BaseEffect {
        data class ShowPayment(val paymentId: Int) : FinancePageEffect()
    }
}
