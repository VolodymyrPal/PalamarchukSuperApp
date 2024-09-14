package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface StoreRepository {
    val fetchProductsAsFlowFromDB: Flow<List<ProductDomainRW>>
    val errorFlow : MutableSharedFlow<Exception?>
    suspend fun updateProduct(product: ProductDomainRW)
    suspend fun hardRefreshProducts()
    suspend fun softRefreshProducts()
    suspend fun upsertAll(products: List<ProductDomainRW>)
}