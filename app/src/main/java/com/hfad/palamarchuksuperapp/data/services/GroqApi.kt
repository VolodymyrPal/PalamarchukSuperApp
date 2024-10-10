package com.hfad.palamarchuksuperapp.data.services

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class GroqRequest(
    val messages: List<Message>,
    val model: String,
    @SerialName("max_tokens")
    val maxTokens: Int
)

@Serializable (GroqMessageSerializer::class)
sealed interface Message

@Serializable
data class MessageChat(
    val role: String,
    val content: List<GroqContentType>
) : Message

@Serializable
data class MessageText(
    val role: String,
    val content: String
) : Message


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

object GroqMessageSerializer : JsonContentPolymorphicSerializer<Message>(Message::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Message> {
        val jsonObject = element.jsonObject
        return when {
            "type" in jsonObject -> MessageChat.serializer()
            "content" in jsonObject -> MessageText.serializer()
            else -> throw SerializationException("Unknown Part type")
        }
    }
}








@Serializable
data class ChatCompletionResponse(
    val id: String,
    @SerialName("object")
    val jObject: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage,
    @SerialName("system_fingerprint")
    val systemFingerprint: String,
    val x_groq: XGroq
)

@Serializable
data class Choice(
    val index: Int,
    val message: Message
)

@Serializable
data class Usage(
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
