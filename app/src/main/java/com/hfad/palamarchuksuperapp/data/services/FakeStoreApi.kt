package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.data.entities.Product
import retrofit2.http.GET

interface FakeStoreApi {
    @GET ("products")
    suspend fun fetchProducts(): List<Product>
}