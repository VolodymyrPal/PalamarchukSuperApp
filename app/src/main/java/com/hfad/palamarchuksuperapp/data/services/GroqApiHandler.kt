package com.hfad.palamarchuksuperapp.data.services

import android.util.Log
import com.hfad.palamarchuksuperapp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class GroqApiHandler @Inject constructor(
    private val httpClient: HttpClient,
) {

    private val apiKey = BuildConfig.GROQ_KEY
    private val max_tokens: Int = 100
    private val adminRoleMessage: Message = GroqContentBuilder.Builder().let {
        it.role = "system"
        it.text("You are tutor and trying to solve users problem on image")
    }.build()

    private val chatHistory: MutableStateFlow<List<Message>> =
        MutableStateFlow(mutableListOf(adminRoleMessage))

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