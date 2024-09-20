package com.hfad.palamarchuksuperapp.data.services

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

typealias Base64 = String

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent> = listOf(GeminiContent()),
)
@Serializable
data class GeminiContent(
    val parts: List<Part> = emptyList(),
    val role : String = "user"
)

@Serializable (PartSerializer :: class)
sealed interface Part

@Serializable
data class TextPart(val text: String = "Is my request completed?" ) : Part

@Serializable
data class ImagePart (val inlineData: InlineData, ) : Part

@Serializable
data class InlineData (
    @SerialName (value = "mime_type") val mimeType: String = "image/jpeg",
    val data: Base64)


object PartSerializer : JsonContentPolymorphicSerializer<Part>(Part::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Part> {
        val jsonObject = element.jsonObject
        return when {
            "text" in jsonObject -> TextPart.serializer()
            "inlineData" in jsonObject -> ImagePart.serializer()
            else -> throw SerializationException("Unknown Part type")
        }
    }
}

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

@Serializable
data class GeminiCandidate(
    val content: GeminiTextResponse,
    val role: String = "model"
)

@Serializable
data class GeminiTextResponse (
    val parts: List<TextPart>
)