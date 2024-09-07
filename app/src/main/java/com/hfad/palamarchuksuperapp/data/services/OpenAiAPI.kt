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