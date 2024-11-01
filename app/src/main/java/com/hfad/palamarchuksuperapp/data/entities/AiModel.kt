package com.hfad.palamarchuksuperapp.data.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface AiModel {

    @SerialName(value = "name")
    val modelName: String
    val isSupported: Boolean

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


    enum class GroqModels(override val modelName: String, override val isSupported: Boolean = true) : AiModel {
        BASE_MODEL("llama-3.2-11b-vision-preview" ),
        TEXT_MODEL("llama3-groq-8b-8192-tool-use-preview")
    }

    enum class GeminiModels(override val modelName: String, override val isSupported: Boolean = true) : AiModel {
        BASE_MODEL("models/gemini-1.5-flash-8b"),
        GEMINI_IMAGE("")
    }

    enum class OpenAIModels(override val modelName: String, override val isSupported: Boolean = true) : AiModel {
        BASE_MODEL(""),
        OPENAI_IMAGE("")
    }

}