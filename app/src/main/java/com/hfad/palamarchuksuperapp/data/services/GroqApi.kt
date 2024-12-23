package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.data.dtos.GroqModelDTO
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class GroqRequest(
    @SerialName("messages")
    val groqMessages: List<GroqMessage>,
    val model: String,
    @SerialName("max_tokens")
    val maxTokens: Int
)

@Serializable (GroqMessageSerializer::class)
sealed interface GroqMessage

@Serializable
data class GroqMessageChat(
    val role: String,
    val content: List<GroqContentType>
) : GroqMessage

@Serializable
data class GroqMessageText(
    val role: String,
    val content: String
) : GroqMessage


@Serializable (GroqContentSerializer::class)
sealed interface GroqContentType

@SerialName("text")
@Serializable
data class ContentText(
    val type: String = "text",
    val text: String = ""
) : GroqContentType

@Suppress("ConstructorParameterNaming")
@SerialName("image_url")
@Serializable
data class ContentImage(
    val type: String = "image_url",
    @SerialName("image_url")
    val image_url: ImageUrl
) : GroqContentType

@Serializable
data class ImageUrl(
    val url: String
)


object GroqContentSerializer : JsonContentPolymorphicSerializer<GroqContentType>(GroqContentType::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<GroqContentType> {
        val jsonObject = element.jsonObject
        return when {
            "text" in jsonObject -> ContentText.serializer()
            "inlineData" in jsonObject -> ContentImage.serializer()
            else -> throw SerializationException("Unknown Part type")
        }
    }
}

object GroqMessageSerializer : JsonContentPolymorphicSerializer<GroqMessage>(GroqMessage::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<GroqMessage> {
        val jsonObject = element.jsonObject
        return when {
            "type" in jsonObject -> GroqMessageChat.serializer()
            "content" in jsonObject -> GroqMessageText.serializer()
            else -> throw SerializationException("Unknown Part type")
        }
    }
}







//@SerialName("ChatCompletionResponse")
@Serializable
data class GroqChatCompletionResponse(
    val id: String,
    @SerialName("object")
    val jObject: String,
    val created: Long,
    val model: String,
    @SerialName("choices")
    val groqChoices: List<GroqChoice>,
    @SerialName("usage")
    val groqUsage: GroqUsage,
    @SerialName("system_fingerprint")
    val systemFingerprint: String,
    val x_groq: XGroq
)

@Serializable
data class GroqChoice(
    val index: Int,
    @SerialName("message")
    val groqMessage: GroqMessage
)

@Serializable
data class GroqUsage(
    @SerialName("queue_time")
    val queueTime: Double,
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("prompt_time")
    val promptTime: Double,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("completion_time")
    val completionTime: Double,
    @SerialName("total_tokens")
    val totalTokens: Int,
    @SerialName("total_time")
    val totalTime: Double
)

@Serializable
data class XGroq(
    val id: String
)

@Serializable
data class GroqModelList(
    @SerialName(value = "object")
    val objectType: String,
    val data: List<GroqModelDTO>
)

@Serializable
data class GroqError (
    val error: GroqErrorDetails
)

@Serializable
data class GroqErrorDetails (
    val message: String,
    val type: String,
    val code: String
)