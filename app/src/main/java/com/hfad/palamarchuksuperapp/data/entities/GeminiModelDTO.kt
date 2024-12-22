package com.hfad.palamarchuksuperapp.data.entities

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName

data class GeminiModelDTO(
    @SerialName("LLM")
    val llmName: LLMName = LLMName.GEMINI,
    @SerialName("name")
    @Required
    val modelName: String = "models/gemini-1.5-flash-8b",
    val version: String = "1.0.0",
    val displayName: String = "Gemini",
    val description: String = "Gemini is a language model that can generate images using the LLM",
    val supportedGenerationMethods: List<String> = emptyList(),
    @SerialName("isSupported")
    val isSupported: Boolean = supportedGenerationMethods.contains("generateContent"),
)

fun GeminiModelDTO.toGeminiModel () : AiModel.GeminiModel {
    return AiModel.GeminiModel(
        llmName = llmName,
        modelName = modelName,
        isSupported = supportedGenerationMethods.contains("generateContent")
    )
}