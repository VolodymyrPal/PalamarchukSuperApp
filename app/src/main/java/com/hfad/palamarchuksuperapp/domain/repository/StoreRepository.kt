package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.domain.models.DataError
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface StoreRepository {
    val fetchProductsAsFlowFromDB: Flow<List<ProductDomainRW>>
    val errorFlow : MutableStateFlow<DataError?>
    suspend fun updateProduct(product: ProductDomainRW)
    suspend fun hardRefreshProducts()
    suspend fun softRefreshProducts()
    suspend fun upsertAll(products: List<ProductDomainRW>)
}