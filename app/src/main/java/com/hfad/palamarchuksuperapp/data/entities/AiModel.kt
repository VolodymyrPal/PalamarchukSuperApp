package com.hfad.palamarchuksuperapp.data.entities

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface AiModel {

    val llmName: LLMName
    val modelName: String
    val isSupported: Boolean

    @Serializable
    data class GroqModel(
//        @Required
        @SerialName("LLM")
        override val llmName: LLMName = LLMName.GROQ,
//        @Required
        @SerialName("id")
        override val modelName: String = "llama-3.2-11b-vision-preview",
//        @Required
        @SerialName("active")
        override val isSupported: Boolean = true,
    ) : AiModel

    @Serializable
    data class GeminiModel(
//        @Required
        @SerialName("LLM")
        override val llmName: LLMName = LLMName.GEMINI,
        @SerialName("name")
        @Required
        override val modelName: String = "models/gemini-1.5-flash-8b",
        val version: String = "1.0.0",
        val displayName: String = "Gemini",
        val description: String = "Gemini is a language model that can generate images using the LLM",
        val supportedGenerationMethods: List<String> = emptyList(),
//        @Required
        @SerialName("isSupported")
        override val isSupported: Boolean = supportedGenerationMethods.contains("generateContent"),
    ) : AiModel

    @Serializable
    data class OpenAIModel(
        @SerialName("LLM")
        @Required
        override val llmName: LLMName = LLMName.OPENAI,
        @SerialName("name")
        @Required
        override val modelName: String = "openai-1",
        @SerialName("isSupported")
        @Required
        override val isSupported: Boolean = true,
    ) : AiModel
}

fun AiModel.toGroqModelDTO(): GroqModelDTO {
    return GroqModelDTO(
        llmName = llmName,
        modelName = modelName,
        isSupported = isSupported
    )
}

fun AiModel.toGeminiModelDTO(): GeminiModelDTO {
    return GeminiModelDTO(
        llmName = llmName,
        modelName = modelName,
        isSupported = isSupported
    )
}

fun AiModel.toOpenAIModelDTO(): OpenAIModelDTO {
    return OpenAIModelDTO(
        llmName = llmName,
        modelName = modelName,
        isSupported = isSupported
    )
}

@Serializable
enum class LLMName {
    OPENAI,
    GEMINI,
    GROQ
}