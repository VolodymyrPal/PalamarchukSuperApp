package com.hfad.palamarchuksuperapp.data.services

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GptRequested(
    val model: String = "gpt-4o-mini",
    val messages: List<RequestRole>,
    val temperature: Double = 0.7,
)

@Serializable
data class RequestRole(
    val role: String,
    val content: List<MessageRequest>,
)

@Serializable
sealed class MessageRequest

@SerialName("text")
@Serializable
data class TextMessageRequest (
    val text: String? = null
) : MessageRequest()

@SerialName("image_url")
@Serializable
data class ImageMessageRequest (
    @SerialName("image_url")
    val image_url: ImageRequest?,
) : MessageRequest()

@Serializable
data class ImageRequest (
    val url: String
)