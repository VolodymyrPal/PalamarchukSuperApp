package com.hfad.palamarchuksuperapp.data.services

import android.util.Log
import com.hfad.palamarchuksuperapp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class GroqApiHandler @Inject constructor(
    private val httpClient: HttpClient
) {
    val apiKey = BuildConfig.GROQ_KEY
    val url = "https://api.groq.com/v1/chat/completions"

    val promptText get() = "Some text to pass"//TODO

    val message = Message(role = "user", content = promptText)
    val request = GroqRequest(messages = listOf(message), model = "llama3-8b-8192")

    suspend fun sendRequest () {
        httpClient.post(url) {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            header()
        }
    }


    val call = groqApi.getChatCompletion(
        apiKey = BuildConfig.GROQ_KEY,  // Ваш API ключ
        body = request
    )

    call.enqueue(object : Callback<ChatCompletionResponse> {

        override fun onResponse(
            p0: Call<ChatCompletionResponse>,
            p1: Response<ChatCompletionResponse>,
        ) {
            if (p1.isSuccessful) {
                responseText = p1.body()!!.choices[0].message.content
            } else {
                Log.d("TAG", "onResponseNotSuccessful: ${p1.code()}")
            }
        }

        override fun onFailure(
            p0: Call<ChatCompletionResponse>,
            p1: Throwable,
        ) {
            Log.d("Tag", "OnFailure: $p1")
        }
    })
}