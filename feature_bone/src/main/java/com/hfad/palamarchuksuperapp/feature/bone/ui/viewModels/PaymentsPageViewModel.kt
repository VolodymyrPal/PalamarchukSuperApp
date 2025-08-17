package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEffect
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEvent
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.GenericViewModel
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.UserSession
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generatePaymentOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.PaymentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.random.Random

class PaymentsPageViewModel @Inject constructor (
    private val paymentsRepository: PaymentsRepository,
    private val userRepository: AuthRepository
) : GenericViewModel<PaymentsPageState, PaymentsPageViewModel.PaymentsPageEvent, PaymentsPageViewModel.PaymentsPageEffect>() {

    override val _dataFlow: Flow<UserSession> = userRepository.currentSession

    private val paymentStatusFilter: MutableStateFlow<PaymentStatus?> = MutableStateFlow(null)
    private val searchQuery: MutableStateFlow<String> = MutableStateFlow("")

    val paymentsPaging: Flow<PagingData<PaymentOrder>> =
        combine(paymentStatusFilter, searchQuery) { status, query ->
            status to query
        }
            .distinctUntilChanged()
            .debounce(500)
            .flatMapLatest { (status, query) ->
                paymentsRepository.pagingPayments(status, query).cachedIn(viewModelScope)
            }

    private val statisticFlow: Flow<AppResult<PaymentStatistic, AppError>> = paymentsRepository.paymentStatistics

    override val uiState: StateFlow<PaymentsPageState> =
        combine(_dataFlow, statisticFlow, paymentStatusFilter, searchQuery
        ) { userSession, paymentMetrics, status, query ->
            val paymentMetrics = if (paymentMetrics is AppResult.Success) {
                Log.d("PaymentsPageViewModel", "Payment metrics: ${paymentMetrics.data}")
                paymentMetrics.data
            } else {
                _errorFlow.emit((paymentMetrics as AppResult.Error).error)
                PaymentStatistic()
            }
            PaymentsPageState(
                paymentStatistic = paymentMetrics,
                paymentStatusFilter = status,
                searchQuery = query
            )
        }.onStart {
            paymentsRepository.refreshStatistic()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PaymentsPageState()
        )

    override fun event(event: PaymentsPageEvent) {
        when (event) {
            is PaymentsPageEvent.LoadPayments -> {

            }

            is PaymentsPageEvent.RefreshPayments -> {

            }

            is PaymentsPageEvent.FilterPaymentStatus -> {
                paymentStatusFilter.update { event.status }
            }

            is PaymentsPageEvent.Search -> {
                Log.d("PaymentsPageViewModel", "Search query: ${event.query}")
                searchQuery.update { event.query }
            }
        }
    }

    override val _errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)
    override val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    sealed class PaymentsPageEvent : BaseEvent {
        data class LoadPayments(val clientId: Int) : PaymentsPageEvent()
        data class RefreshPayments(val clientId: Int) : PaymentsPageEvent()
        data class FilterPaymentStatus(val status: PaymentStatus?) : PaymentsPageEvent()
        data class Search(val query: String) : PaymentsPageEvent()
    }

    sealed class PaymentsPageEffect : BaseEffect {
        data class ShowPayment(val paymentId: Int) : PaymentsPageEffect()
    }
}
