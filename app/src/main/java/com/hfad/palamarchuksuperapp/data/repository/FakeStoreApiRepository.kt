package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.data.services.FakeStoreApi
import com.hfad.palamarchuksuperapp.ui.common.toProductDomainRW
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

@Suppress("MaxLineLength")
class FakeStoreApiRepository @Inject constructor() : FakeStoreApi {
    private var fakeStoreApi: FakeStoreApi

    init {
        val httpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
        val retrofit: Retrofit =
            Retrofit.Builder().baseUrl("https://fakestoreapi.com/")
                .addConverterFactory(MoshiConverterFactory.create().asLenient())  // TODO asLenient good only for testing, not production
                .client(httpClient)
                .build()
        fakeStoreApi = retrofit.create<FakeStoreApi>()
    }

    override suspend fun fetchProducts() =  fakeStoreApi.fetchProducts()
    override suspend fun getProductsDomainRw() = fakeStoreApi.fetchProducts().map { it.toProductDomainRW() }

}