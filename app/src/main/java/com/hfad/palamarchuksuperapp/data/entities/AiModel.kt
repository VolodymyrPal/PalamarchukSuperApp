package com.hfad.palamarchuksuperapp.data.entities

import com.squareup.moshi.Json
import kotlinx.serialization.Serializable

interface AiModel {

    val modelName: String

    @Serializable
    data class GroqModel(
        @Json(name = "name")
        override val modelName: String = "llama-3.2-11b-vision-preview",
    ) : AiModel

    @Serializable
    data class GeminiModel(
        override val modelName: String = "gemini-1.5-flash",
        val version: String = "1.0.0",
        val displayName: String = "Gemini",
        val description: String = "Gemini is a language model that can generate images using the LLM",
        val supportedGenerationMethods: List<String> = emptyList(),
        val isSupported: Boolean = supportedGenerationMethods.contains("generateContent"),
    ) : AiModel

    @Serializable
    data class OpenAIModel(
        override val modelName: String = "openai-1",
    ) : AiModel


    enum class GroqModels(override val modelName: String) : AiModel {
        BASE_MODEL("llama-3.2-11b-vision-preview"),
        TEXT_MODEL("llama3-groq-8b-8192-tool-use-preview")
    }

    enum class GeminiModels(override val modelName: String) : AiModel {
        BASE_MODEL("gemini-1.5-flash"),
        GEMINI_IMAGE("")
    }

    enum class OpenAIModels(override val modelName: String) : AiModel {
        BASE_MODEL(""),
        OPENAI_IMAGE("")
    }

}