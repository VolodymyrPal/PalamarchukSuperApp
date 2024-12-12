package com.hfad.palamarchuksuperapp.domain.models

import androidx.compose.runtime.Stable
import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
@Stable
data class AiHandlerInfo(
    val name: String,
    val isSelected: Boolean,
    val isActive: Boolean,
    val model: AiModel,
    val aiApiKey: String = "",
) {

    companion object {
        val DEFAULT_LIST_AI_HANDLER_INFO by lazy {
            persistentListOf(
                AiHandlerInfo(
                    name = "OpenAI",
                    isSelected = true,
                    isActive = true,
                    model = AiModel.OPENAI_BASE_MODEL,
                    aiApiKey = "123"//BuildConfig.OPEN_AI_KEY_USER
                ),
                AiHandlerInfo(
                    name = "Gemini",
                    isSelected = true,
                    isActive = true,
                    model = AiModel.GEMINI_BASE_MODEL,
                    aiApiKey = BuildConfig.GEMINI_AI_KEY
                ),
                AiHandlerInfo(
                    name = "Groq",
                    isSelected = true,
                    isActive = true,
                    model = AiModel.GROQ_BASE_MODEL,
                    aiApiKey = BuildConfig.GROQ_KEY
                )
            )
        }
    }
}