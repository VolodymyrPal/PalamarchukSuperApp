package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Product
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface StoreRepository {
    val fetchProductsAsFlowFromDB: Flow<PersistentList<Product>>
    val errorFlow : MutableStateFlow<AppError?>
    suspend fun updateProduct(product: Product)
    suspend fun hardRefreshProducts()
    suspend fun softRefreshProducts()
    suspend fun upsertAll(products: PersistentList<Product>)
}