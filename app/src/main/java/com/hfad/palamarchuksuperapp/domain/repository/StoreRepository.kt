package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.Product
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    fun fetchProducts(): Flow<List<Product>>
    suspend fun deleteProduct(product: Product)
    suspend fun addProduct(product: Product)
    suspend fun updateProduct(product: Product)
}