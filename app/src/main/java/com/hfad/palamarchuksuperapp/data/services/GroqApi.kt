package com.hfad.palamarchuksuperapp.data.services

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqApi {
    @POST("openai/v1/chat/completions")
    fun getChatCompletion(
        @Header("Authorization") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body body: GroqRequest
    ): Call<ChatCompletionResponse>
}

data class GroqRequest(
    val messages: List<Message>,
    val model: String
)

@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    val id: String,
    @Json(name = "object")
    val jObject: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage,
    @Json(name = "system_fingerprint")
    val systemFingerprint: String,
    val x_groq: XGroq
)

@JsonClass(generateAdapter = true)
data class Choice(
    val index: Int,
    val message: Message
)

@JsonClass(generateAdapter = true)
data class Message(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class Usage(
    @Json(name = "queue_time")
    val queueTime: Double,
    @Json(name = "prompt_tokens")
    val promptTokens: Int,
    @Json(name = "prompt_time")
    val promptTime: Double,
    @Json(name = "completion_tokens")
    val completionTokens: Int,
    @Json(name = "completion_time")
    val completionTime: Double,
    @Json(name = "total_tokens")
    val totalTokens: Int,
    @Json(name = "total_time")
    val totalTime: Double
)

@JsonClass(generateAdapter = true)
data class XGroq(
    val id: String
)
