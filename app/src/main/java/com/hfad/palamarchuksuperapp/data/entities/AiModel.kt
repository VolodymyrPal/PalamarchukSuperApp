package com.hfad.palamarchuksuperapp.data.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = AiModel.AiModelSerializer::class)
sealed interface AiModel {

    @SerialName(value = "name")
    val modelName: String
    val isSupported: Boolean

    companion object {
        val GROQ_BASE_MODEL = GroqModel("llama-3.2-11b-vision-preview")
        val GEMINI_BASE_MODEL = GeminiModel("models/gemini-1.5-flash-8b")
        val OPENAI_BASE_MODEL = OpenAIModel("gpt-4o-mini")
    }

    @Serializable
    data class GroqModel(
        @SerialName(value = "name")
        override val modelName: String = "llama-3.2-11b-vision-preview",
        override val isSupported: Boolean = true
    ) : AiModel

    @Serializable
    data class GeminiModel(
        @SerialName(value = "name") override val modelName: String,
        val version: String = "1.0.0",
        val displayName: String = "Gemini",
        val description: String = "Gemini is a language model that can generate images using the LLM",
        val supportedGenerationMethods: List<String> = emptyList(),
        override val isSupported: Boolean = supportedGenerationMethods.contains("generateContent"),
    ) : AiModel

    @Serializable
    data class OpenAIModel(
        override val modelName: String = "openai-1",
        override val isSupported: Boolean = true,
    ) : AiModel

    object AiModelSerializer : JsonContentPolymorphicSerializer<AiModel>(AiModel::class) {
        override fun selectDeserializer(element: JsonElement) =
            when (element.jsonObject["llmName"]?.jsonPrimitive?.contentOrNull) {
                LLMName.GROQ.name -> GroqModel.serializer()
                LLMName.GEMINI.name -> GeminiModel.serializer()
                LLMName.OPENAI.name-> OpenAIModel.serializer()
                else -> throw SerializationException("Unknown Model type")
            }
    }
}

enum class AiProviderName {
    OPENAI,
    GEMINI,
    GROQ
}