package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.data.entities.AiModel
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
    val role: String = "user",
)

@Serializable(PartSerializer::class)
sealed interface Part

@Serializable
data class TextPart(val text: String = "Is my request completed?") : Part

@Serializable

data class ImagePart(
    @SerialName(value = "inline_data") val inlineData: InlineData) : Part

@Serializable
data class InlineData(
    @SerialName(value = "mime_type") val mimeType: String = "image/jpeg",
    val data: Base64,
)


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
    val candidates: List<GeminiCandidate>,
)

@Serializable
data class GeminiCandidate(
    val content: GeminiTextResponse,
    val role: String = "model",
)

@Serializable
data class GeminiTextResponse(
    val parts: List<TextPart>,
)

@Serializable
data class GeminiModelsResponse(
    val models: List<AiModel.GeminiModel>,
)


class GeminiBuilder {

    class RequestBuilder {

        private var parts: MutableList<Part> = arrayListOf()
        private var contents: MutableList<GeminiContent> = arrayListOf()

        @JvmName("addContentText")
        fun contentText(role: String, content: String) = apply {
            contents.add(GeminiContent(listOf(TextPart(text = content)), role = role))
        }

        @JvmName("addContent")
        fun contentImage(role: String, content: Base64) = apply {
            contents.add(GeminiContent(listOf(ImagePart(InlineData(data = content))), role = role))
        }

        @JvmName("addPart")
        fun <T : Part> part(data: T) = apply { parts.add(data) }

        @JvmName("addText")
        fun text(text: String)  = part(TextPart(text))

        @JvmName("addImage")
        fun image(image: Base64) = part(ImagePart(InlineData(data = image)))

        fun buildSingleRequest(): GeminiRequest = GeminiRequest(listOf(GeminiContent(parts)))

        fun buildChatRequest(): GeminiRequest = GeminiRequest(contents)

    }
}


