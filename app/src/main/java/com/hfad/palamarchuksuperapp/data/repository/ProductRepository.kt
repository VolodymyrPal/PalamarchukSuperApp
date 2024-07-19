package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.services.FakeStoreApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Inject

class ProductRepository @Inject constructor() : FakeStoreApi {
    private var fakeStoreApi: FakeStoreApi

    init {
        val httpClient: OkHttpClient = OkHttpClient.Builder().build()
        val retrofit: Retrofit =
                .addConverterFactory(GsonConverterFactory.create())
            Retrofit.Builder().baseUrl("https://fakestoreapi.com/")
                .client(httpClient)
                .build()
        fakeStoreApi = retrofit.create<FakeStoreApi>()
    }

    override suspend fun fetchProducts() = fakeStoreApi.fetchProducts()

}