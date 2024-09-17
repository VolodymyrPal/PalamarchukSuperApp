package com.hfad.palamarchuksuperapp.data.services

import com.squareup.moshi.Json
import kotlinx.serialization.DeserializationStrategy
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
)

@Serializable (PartSerializer :: class)
sealed interface Part

@Serializable
data class TextPart(val text: String = "Is my request completed?") : Part

@Serializable
data class ImagePart (val inlineData: InlineData) : Part

@Serializable
data class InlineData (
    @Json(name = "mime_type") val mimeType: String = "image/jpeg",
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