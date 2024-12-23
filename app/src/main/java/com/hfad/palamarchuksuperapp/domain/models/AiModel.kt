package com.hfad.palamarchuksuperapp.domain.models

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
        @SerialName("LLM")
        override val llmName: LLMName = LLMName.GROQ,
        @SerialName("id")
        override val modelName: String = "llama-3.2-11b-vision-preview",
        @SerialName("active")
        override val isSupported: Boolean = true,
    ) : AiModel

    @Serializable
    data class GeminiModel(
        @SerialName("LLM")
        override val llmName: LLMName = LLMName.GEMINI,
        @SerialName("name")
        @Required
        override val modelName: String = "models/gemini-1.5-flash-8b",
        val version: String = "1.0.0",
        val displayName: String = "Gemini",
        val description: String = "Gemini is a language model that can generate images using the LLM",
        val supportedGenerationMethods: List<String> = emptyList(),
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

@Serializable
enum class LLMName {
    OPENAI,
    GEMINI,
    GROQ
}