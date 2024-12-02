package com.hfad.palamarchuksuperapp.data.entities

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface AiModel {

    val llmName: LLMName
    val modelName: String
    val isSupported: Boolean

    companion object {
        val GROQ_BASE_MODEL = GroqModel(llmName = LLMName.GROQ, modelName = "llama-3.2-11b-vision-preview")
        val GEMINI_BASE_MODEL = GeminiModel(llmName = LLMName.GEMINI, modelName = "models/gemini-1.5-flash-8b")
        val OPENAI_BASE_MODEL = OpenAIModel(llmName = LLMName.OPENAI, modelName = "gpt-4o-mini")
    }

    @Serializable
    data class GroqModel @OptIn(ExperimentalSerializationApi::class) constructor(
        @SerialName("LLM")
        @EncodeDefault(EncodeDefault.Mode.ALWAYS)
        override val llmName: LLMName = LLMName.GROQ,
        @SerialName("name")
        @EncodeDefault(EncodeDefault.Mode.ALWAYS)
        override val modelName: String = "llama-3.2-11b-vision-preview",
        @SerialName("isSupported")
        @EncodeDefault(EncodeDefault.Mode.ALWAYS)
        override val isSupported: Boolean = true,
    ) : AiModel

    @Serializable
    data class GeminiModel @OptIn(ExperimentalSerializationApi::class) constructor(
        @SerialName("LLM")
        @EncodeDefault(EncodeDefault.Mode.ALWAYS)
        override val llmName: LLMName = LLMName.GEMINI,
        @SerialName("name")
        @EncodeDefault(EncodeDefault.Mode.ALWAYS)
        override val modelName: String = "models/gemini-1.5-flash-8b",
        val version: String = "1.0.0",
        val displayName: String = "Gemini",
        val description: String = "Gemini is a language model that can generate images using the LLM",
        val supportedGenerationMethods: List<String> = emptyList(),
        @SerialName("isSupported")
        @EncodeDefault(EncodeDefault.Mode.ALWAYS)
        override val isSupported: Boolean = supportedGenerationMethods.contains("generateContent"),
    ) : AiModel

    @Serializable
    data class OpenAIModel @OptIn(ExperimentalSerializationApi::class) constructor(
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