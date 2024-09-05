package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.services.GPTRequest
import com.hfad.palamarchuksuperapp.data.services.OpenAiAPI
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Suppress("MaxLineLength")
class OpenAIApiRepository @Inject constructor() : OpenAiAPI {
        private var openAiAPI: OpenAiAPI

        init {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val httpClient: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()
            val retrofit: Retrofit =
                Retrofit.Builder().baseUrl("https://api.openai.com/v1/")
                    .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())  // TODO asLenient good only for testing, not production
                    .client(httpClient)
                    .build()
            openAiAPI = retrofit.create<OpenAiAPI>()
        }

    override fun getGPTResponse(
        contentType: String,
        apiKey: String,
        body: GPTRequest,
    ): Call<String> {
        return openAiAPI.getGPTResponse(contentType, apiKey, body)
    }


}