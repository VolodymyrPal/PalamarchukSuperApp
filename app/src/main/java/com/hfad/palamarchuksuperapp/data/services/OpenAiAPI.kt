package com.hfad.palamarchuksuperapp.data.services

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAiAPI {
    @POST("v1/chat/completions")
    fun getGPTResponse(
        @Header("Content-Type") contentType: String,
        @Header("Authorization") apiKey: String,
        @Body body: GPTRequest
    ): Call<String>
}

@Serializable
data class GPTRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<GPTRequestMessage>,
    val temperature: Double = 0.7,
)

@Serializable
data class GPTRequestMessage (
    val role: String,
    val content: TextMessage
)

@Serializable
data class TextMessage (
    val content: String,
    val type: String = "text"
)





@Serializable
data class GPTRequestImage(
    val model: String = "gpt-4o-mini",
    val messages: List<GPTRequestImageMessage>,
    val temperature: Double = 0.7,
)

@Serializable
data class GPTRequestImageMessage (
    val role: String,
    val content: List<ImageMessage>
)

@Serializable
data class ImageMessage (
    val image_url: Image,
    val type: String = "text"
)

@Serializable
data class Image (
    val url: String
)





@Serializable
data class GptRequested(
    val model: String = "gpt-4o-mini",
    val messages: List<RequestRole>,
//    val temperature: Double = 0.7,
)

//@Serializable
//@JsonClassDiscriminator("typeKtor")
//sealed class RoleRequest

@Serializable
data class RequestRole(
    val role: String,
    val content: List<ContentRequest>,
    val maxTokens: Int = 50
) // : RoleRequest()

@Serializable
sealed class ContentRequest

@Serializable
data class ImageMessageRequest (
    @SerialName("type")
    val typeImage: String = "image_url",
    @SerialName("image_url")
    val image_url: ImageRequest
) : ContentRequest()

@Serializable
data class ImageRequest (
    val url: String
)

@Serializable
data class MessageRequest (
    @SerialName("type")
    val typeText : String = "text",
    val text : String
) : ContentRequest()
