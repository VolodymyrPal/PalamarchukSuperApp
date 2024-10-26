package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface StoreRepository {
    val fetchProductsAsFlowFromDB: Flow<PersistentList<ProductDomainRW>>
    val errorFlow : MutableStateFlow<AppError?>
    suspend fun updateProduct(product: ProductDomainRW)
    suspend fun hardRefreshProducts()
    suspend fun softRefreshProducts()
    suspend fun upsertAll(products: PersistentList<ProductDomainRW>)
}