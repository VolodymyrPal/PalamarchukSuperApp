package com.hfad.palamarchuksuperapp.domain.models

import androidx.compose.runtime.Stable
import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
@Stable
data class AiHandlerInfo(
    val id: String = "${System.currentTimeMillis()}-${Random.nextInt(0, 9999)}",
    val name: String,
    val isSelected: Boolean,
    val isActive: Boolean,
    val model: AiModel,
    val aiApiKey: String = "",
) {

    companion object {
        val DEFAULT_AI_HANDLER_INFO_OPEN_AI by lazy {
            AiHandlerInfo(
                id = "0",
                name = "OpenAI",
                isSelected = true,
                isActive = true,
                model = AiModel.OPENAI_BASE_MODEL,
                aiApiKey = BuildConfig.OPEN_AI_KEY_USER
            )
        }
        val DEFAULT_AI_HANDLER_INFO_GEMINI by lazy {
            AiHandlerInfo(
                id = "1",
                name = "Gemini",
                isSelected = true,
                isActive = true,
                model = AiModel.GEMINI_BASE_MODEL,
                aiApiKey = BuildConfig.GEMINI_AI_KEY
            )
        }
        val DEFAULT_AI_HANDLER_INFO_GROQ by lazy {
            AiHandlerInfo(
                id = "2",
                name = "Groq",
                isSelected = true,
                isActive = true,
                model = AiModel.GROQ_BASE_MODEL,
                aiApiKey = BuildConfig.GROQ_KEY
            )
        }

        val DEFAULT_LIST_AI_HANDLER_INFO by lazy {
            persistentListOf(
                DEFAULT_AI_HANDLER_INFO_GROQ,
                DEFAULT_AI_HANDLER_INFO_GEMINI,
                DEFAULT_AI_HANDLER_INFO_OPEN_AI
            )
        }
    }
}