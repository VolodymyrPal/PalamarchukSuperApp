package com.hfad.palamarchuksuperapp.data.services

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroqRequest(
    val messages: List<Message>,
    val model: String
)

@Serializable
data class Message(
    val role: String,
    val content: List<GroqContentType>
)

@Serializable
sealed interface GroqContentType

@SerialName("text")
@Serializable
data class ContentText(
    val text: String = ""
) : GroqContentType

@Suppress("ConstructorParameterNaming")
@SerialName("image_url")
@Serializable
data class ContentImage(
    @SerialName("image_url")
    val image_url: ImageUrl
) : GroqContentType

@Serializable
data class ImageUrl(
    val url: String
) : GroqContentType





@Serializable
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

@Serializable
data class Choice(
    val index: Int,
    val message: Message
)

@Serializable
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

@Serializable
@JsonClass(generateAdapter = true)
data class XGroq(
    val id: String
)
