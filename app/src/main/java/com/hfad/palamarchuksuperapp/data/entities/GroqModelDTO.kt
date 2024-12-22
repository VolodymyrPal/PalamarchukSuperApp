package com.hfad.palamarchuksuperapp.data.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroqModelDTO(
    @SerialName("LLM")
    val llmName: LLMName = LLMName.GROQ,
    @SerialName("id")
    val modelName: String = "llama-3.2-11b-vision-preview",
    @SerialName("active")
    val isSupported: Boolean = true,
)

fun GroqModelDTO.toGroqModel () : AiModel.GroqModel {
    return AiModel.GroqModel(
        llmName = llmName,
        modelName = modelName,
        isSupported = isSupported
    )
}