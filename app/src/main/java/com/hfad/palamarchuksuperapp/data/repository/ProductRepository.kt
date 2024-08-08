package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.services.FakeStoreApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Inject

@Suppress("MaxLineLength")
class ProductRepository @Inject constructor() : FakeStoreApi {
    private var fakeStoreApi: FakeStoreApi

    init {
        val httpClient: OkHttpClient = OkHttpClient.Builder().build()
        val retrofit: Retrofit =
            Retrofit.Builder().baseUrl("https://fakestoreapi.com/")
                .addConverterFactory(MoshiConverterFactory.create().asLenient())  // TODO asLenient good only for testing, not production
                .client(httpClient)
                .build()
        fakeStoreApi = retrofit.create<FakeStoreApi>()
    }

    override suspend fun fetchProducts() = fakeStoreApi.fetchProducts()

}