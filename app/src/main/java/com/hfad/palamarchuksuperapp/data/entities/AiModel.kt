package com.hfad.palamarchuksuperapp.data.entities

import android.util.Log
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
    @SerialName("llmName") val llmName: LLMName

    companion object {
        val GROQ_BASE_MODEL = GroqModel("llama-3.2-11b-vision-preview")
        val GEMINI_BASE_MODEL = GeminiModel("models/gemini-1.5-flash-8b")
        val OPENAI_BASE_MODEL = OpenAIModel("gpt-4o-mini")
    }

    @Serializable
    @SerialName(value = "groq_model")
    data class GroqModel(
        override val modelName: String,
        override val isSupported: Boolean = true,
        @SerialName(value = "llmName") override val llmName: LLMName = LLMName.GROQ,
    ) : AiModel

    @Serializable
    @SerialName(value = "gemini_model")
    data class GeminiModel(
        @SerialName(value = "gemini_model_name") override val modelName: String,
        val version: String = "1.0.0",
        val displayName: String = "Gemini",
        val description: String = "Gemini is a language model that can generate images using the LLM",
        val supportedGenerationMethods: List<String> = emptyList(),
        override val isSupported: Boolean = supportedGenerationMethods.contains("generateContent"),
        @SerialName(value = "llmName") override val llmName: LLMName = LLMName.GEMINI,
    ) : AiModel

    @Serializable
    @SerialName(value = "openai_model")
    data class OpenAIModel(
        @SerialName(value = "openai_model_name")
        override val modelName: String = "openai-1",
        @SerialName("open_is_supported")
        override val isSupported: Boolean = true,
        @SerialName(value = "llmName") override val llmName: LLMName = LLMName.OPENAI,
    ) : AiModel

    object AiModelSerializer : JsonContentPolymorphicSerializer<AiModel>(AiModel::class) {
        override fun selectDeserializer(element: JsonElement) =
            when (element.jsonObject["llmName"]?.jsonPrimitive?.contentOrNull) {
                LLMName.GROQ.name -> GroqModel.serializer()
                LLMName.GEMINI.name -> GeminiModel.serializer()
                LLMName.OPENAI.name -> OpenAIModel.serializer()
                else -> {
                    Log.d("Current tag: ", "${element.jsonObject["llmName"]?.jsonPrimitive?.contentOrNull}")
                    throw SerializationException("Unknown Model type")
                }
            }
    }
}

val aiModelModule = SerializersModule {
    polymorphic(AiModel::class) {
        subclass(AiModel.GroqModel::class, AiModel.GroqModel.serializer())
        subclass(AiModel.GeminiModel::class, AiModel.GeminiModel.serializer())
        subclass(AiModel.OpenAIModel::class, AiModel.OpenAIModel.serializer())
    }
}

@Serializable
enum class LLMName {
    OPENAI,
    GEMINI,
    GROQ
}