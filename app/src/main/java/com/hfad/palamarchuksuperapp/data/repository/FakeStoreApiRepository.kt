package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.data.services.FakeStoreApi
import com.hfad.palamarchuksuperapp.ui.common.toProductDomainRW
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

@Suppress("MaxLineLength")
class FakeStoreApiRepository @Inject constructor(
    private val httpClient: HttpClient
) : FakeStoreApi {

    override suspend fun fetchProducts() : List<Product> {
        val request =  httpClient.get("https://fakestoreapi.com/products")
        val products = request.body<List<Product>>()
        return products
    }

    override suspend fun getProductsDomainRw() = fetchProducts().map { it.toProductDomainRW() }

}