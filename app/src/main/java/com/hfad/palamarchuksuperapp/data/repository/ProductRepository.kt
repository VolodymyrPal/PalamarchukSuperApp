package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.services.FakeStoreApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Inject

class ProductRepository @Inject constructor() : PlatziApi {
    private var platziApi: PlatziApi

    init {
        val httpClient: OkHttpClient = OkHttpClient.Builder().build()
        val retrofit: Retrofit =
            Retrofit.Builder().baseUrl("https://api.escuelajs.co/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
        platziApi = retrofit.create<PlatziApi>()
    }

    override suspend fun fetchProducts() = platziApi.fetchProducts()

}