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
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.UserSession
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.SalesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class SalesPageViewModel @Inject constructor(
    private val salesRepository: SalesRepository,
    private val authRepository: AuthRepository,
) : GenericViewModel<SalesPageState, SalesPageViewModel.SalesPageEvent, SalesPageViewModel.SalesPageEffect>() {

    override val _dataFlow: Flow<UserSession> = authRepository.currentSession

    private val saleStatusFilter: MutableStateFlow<List<SaleStatus>> = MutableStateFlow(emptyList())
    private val searchQuery: MutableStateFlow<String> = MutableStateFlow("")

    val salesPaging: Flow<PagingData<SaleOrder>> =
        combine(saleStatusFilter, searchQuery) { status, query ->
            status to query
        }
            .distinctUntilChanged()
            .debounce(500)
            .flatMapLatest { (status, query) ->
                salesRepository.pagingSales(status, query).cachedIn(viewModelScope)
            }

    private val statisticFlow: Flow<AppResult<SalesStatistics, AppError>> =
        salesRepository.salesStatistics

    override val uiState: StateFlow<SalesPageState> =
        combine(
            _dataFlow, statisticFlow, saleStatusFilter, searchQuery
        ) { userSession, salesMetrics, status, query ->
            val salesMetrics = if (salesMetrics is AppResult.Success) {
                salesMetrics.data
            } else {
                _errorFlow.emit((salesMetrics as AppResult.Error).error)
                SalesStatistics()
            }
            SalesPageState(
                salesStatistics = salesMetrics,
                saleStatusFilter = status,
                searchQuery = query
            )
        }.onStart {
            salesRepository.refreshStatistic()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SalesPageState()
        )

    override fun event(event: SalesPageEvent) {
        when (event) {
            is SalesPageEvent.LoadSales -> {

            }

            is SalesPageEvent.RefreshSales -> {

            }

            is SalesPageEvent.FilterSaleStatus -> {
                saleStatusFilter.update {
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

            is SalesPageEvent.Search -> {
                Log.d("SalesPageViewModel", "Search query: ${event.query}")
                searchQuery.update { event.query }
            }
        }
    }

    override val _errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)
    override val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    sealed class SalesPageEvent : BaseEvent {
        data class LoadSales(val clientId: Int) : SalesPageEvent()
        data class RefreshSales(val clientId: Int) : SalesPageEvent()
        data class FilterSaleStatus(val status: SaleStatus) : SalesPageEvent()
        data class Search(val query: String) : SalesPageEvent()
    }

    sealed class SalesPageEffect : BaseEffect {
        data class ShowSale(val saleId: Int) : SalesPageEffect()
    }
}
