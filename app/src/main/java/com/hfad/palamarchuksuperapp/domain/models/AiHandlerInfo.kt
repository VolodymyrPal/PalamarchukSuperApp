package com.hfad.palamarchuksuperapp.domain.models

import com.hfad.palamarchuksuperapp.data.entities.AiModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class AiHandlerInfo(
    val name: String,
    val isSelected: Boolean,
    val isActive: Boolean,
    val model: AiModel,
) {

    companion object {
        val DEFAULT_LIST_AI_HANDLER_INFO by lazy {
            persistentListOf(
                AiHandlerInfo(
                    name = "OpenAI",
                    isSelected = true,
                    isActive = true,
                    model = AiModel.OPENAI_BASE_MODEL
                ),
                AiHandlerInfo(
                    name = "Gemini",
                    isSelected = true,
                    isActive = true,
                    model = AiModel.GEMINI_BASE_MODEL
                ),
                AiHandlerInfo(
                    name = "Groq",
                    isSelected = true,
                    isActive = true,
                    model = AiModel.GROQ_BASE_MODEL
                )
            )
        }
    }
}