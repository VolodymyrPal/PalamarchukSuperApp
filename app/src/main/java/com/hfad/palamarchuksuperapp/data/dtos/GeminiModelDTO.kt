package com.hfad.palamarchuksuperapp.data.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeminiModelDTO(
    @SerialName("name")
    val modelName: String = "models/gemini-1.5-flash-8b",
    val version: String = "1.0.0",
    val displayName: String = "Gemini",
    val description: String = "Gemini is a language model that can generate images using the LLM",
    val supportedGenerationMethods: List<String> = emptyList(),
)