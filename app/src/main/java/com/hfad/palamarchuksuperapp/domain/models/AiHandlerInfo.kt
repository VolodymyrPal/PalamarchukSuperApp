package com.hfad.palamarchuksuperapp.domain.models

import androidx.compose.runtime.Stable
import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.AiModel.GeminiModel
import com.hfad.palamarchuksuperapp.data.entities.AiModel.GroqModel
import com.hfad.palamarchuksuperapp.data.entities.AiModel.OpenAIModel
import com.hfad.palamarchuksuperapp.data.entities.LLMName
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
                model = OpenAIModel(
                    llmName = LLMName.OPENAI,
                    modelName = "gpt-4o-mini"
                ),
                aiApiKey = BuildConfig.OPEN_AI_KEY_USER
            )
        }
        val DEFAULT_AI_HANDLER_INFO_GEMINI by lazy {
            AiHandlerInfo(
                id = "1",
                name = "Gemini",
                isSelected = true,
                isActive = true,
                model = GeminiModel(
                    llmName = LLMName.GEMINI,
                    modelName = "models/gemini-1.5-flash-8b"
                ),
                aiApiKey = BuildConfig.GEMINI_AI_KEY
            )
        }
        val DEFAULT_AI_HANDLER_INFO_GROQ by lazy {
            AiHandlerInfo(
                id = "2",
                name = "Groq",
                isSelected = true,
                isActive = true,
                model = GroqModel(
                    llmName = LLMName.GROQ,
                    modelName = "llama-3.2-11b-vision-preview"
                ),
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