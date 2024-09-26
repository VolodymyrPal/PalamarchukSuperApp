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
    val url_image = "https://api.groq.com/openai/v1/chat/generate"

    suspend fun sendMessageChatImage(message: Message) {
        chatHistory.update {
            chatHistory.value.plus(message)
        }
        val request = httpClient.post(url_image) {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(GroqRequest(model = "123", messages = chatHistory.value))
        }
        if (request.status == HttpStatusCode.OK) {
            val response = request.body<ChatCompletionResponse>()

            val responseMessage = GroqContentBuilder.Builder().let {
                it.role = "assistant"
                it.text("${response.choices[0].message.content}")
            }.build()
            chatHistory.update {
                chatHistory.value.plus(responseMessage)
            }
        } else {
            Log.d("TAG", "onResponseNotSuccessful: ${request.status}")
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



enum class Models(val value: String) {
    GROQ_SIMPLE_TEXT("llama3-8b-8192"),
    GROQ_IMAGE("llava-v1.5-7b-4096-preview"),
}