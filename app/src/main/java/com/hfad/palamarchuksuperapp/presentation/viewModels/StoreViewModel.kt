package com.hfad.palamarchuksuperapp.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.presentation.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.presentation.common.ProductToProductDomainRW
import com.hfad.palamarchuksuperapp.presentation.common.SkillDomainRW
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoreViewModel @Inject constructor(private val repository: StoreRepository) :
    UiStateViewModel<List<ProductDomainRW>>() {

    fun fetchProducts() {
        viewModelScope.launch {
            val a = repository.fetchProducts().first().map { ProductToProductDomainRW.map(it) }
            emitState(RepoResult.Success(data = a))
        }
    }
}