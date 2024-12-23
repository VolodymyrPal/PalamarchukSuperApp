package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.data.dtos.ProductDTO
import com.hfad.palamarchuksuperapp.domain.models.Product

interface FakeStoreApi {
    suspend fun fetchProducts(): List<Product>
    suspend fun getProductsDomainRw(): List<ProductDomainRW>
}