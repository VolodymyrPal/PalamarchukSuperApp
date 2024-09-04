package com.hfad.palamarchuksuperapp.data.services

import com.squareup.moshi.JsonClass
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAiAPI {
    @POST("openai/v1/chat/completions/")
    fun getGPTResponse(
        @Header("Content-Type") contentType: String,
        @Header("Authorization") apiKey: String,
        @Body body: GPTRequest
    ): Call<String>
}

@JsonClass(generateAdapter = true)
data class GPTRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<GPTRequestMessage>,
    val temperature: Double = 0.7,
)

@JsonClass(generateAdapter = true)
data class GPTRequestMessage (
    val role: String,
    val content: String
)



data class GPTRespone(
    val id: String,
    val objectG: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
)

//data class OpenAiRequest (
//    model: String,
//    messages: List<OpenAiMessage>
//)
