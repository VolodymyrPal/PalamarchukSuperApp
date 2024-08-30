package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    val fetchProductsAsFlowFromDB: Flow<List<ProductDomainRW>>
    suspend fun updateProduct(product: Product)
    suspend fun refreshProducts()
    suspend fun upsertAll(products: List<ProductDomainRW>)
}