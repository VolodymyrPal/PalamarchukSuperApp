package com.hfad.palamarchuksuperapp.data.repository

import android.util.Log
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.services.GeminiApiHandler
import com.hfad.palamarchuksuperapp.data.services.GroqApiHandler
import com.hfad.palamarchuksuperapp.data.services.OpenAIApiHandler
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import com.squareup.moshi.Json
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import javax.inject.Inject
import com.hfad.palamarchuksuperapp.domain.models.Result


class ChatAiRepositoryImpl @Inject constructor(
    private val groqApiHandler: GroqApiHandler,
    private val geminiApiHandler: GeminiApiHandler,
    private val openAIApiHandler: OpenAIApiHandler,
) : ChatAiRepository {

    override val chatAiChatFlow: MutableStateFlow<PersistentList<MessageAI>> =
        MutableStateFlow(persistentListOf())

    override val errorFlow: MutableSharedFlow<AppError?> = MutableSharedFlow()

    override suspend fun getRespondChatOrImage(message: MessageAI) {

        chatAiChatFlow.update { chatAiChatFlow.value.add(message) }

        val response: Result<MessageAI, AppError> = when (currentModel) {
            is AiModels.GroqModels -> {
                groqApiHandler.getResponse(chatAiChatFlow.value) // TODO correct result
            }

            is AiModels.GeminiModels -> {
                geminiApiHandler.getResponse(chatAiChatFlow.value)
            }

            is AiModels.OpenAIModels -> {
                openAIApiHandler.sendRequestWithResponse()
                Result.Success(MessageAI())
            } //TODO request
            else -> {
                Result.Success(MessageAI()) //TODO correct result
            }

        }
        when (response) {
            is Result.Success -> {
                chatAiChatFlow.update { chatAiChatFlow.value.add(response.data) }
            }

            is Result.Error -> {
                errorFlow.emit(AppError.CustomError(errorText = response.error.toString()))
            }
        }
    }

    override suspend fun getModels(): List<AiModels> {

        when (val response = geminiApiHandler.getModels()) {
            is Result.Success -> {
                Log.d("Gemini models:", "${response}")
                Log.d(
                    "Gemini models:",
                    "${response.data.forEach { Log.d("Gemini models:", "${it.modelName}") }}"
                )
                response.data.forEach { model ->
                    chatAiChatFlow.update {
                        it.add(MessageAI(role = "user", content = model.displayName))
                    }
                }
                return response.data
            }

            is Result.Error -> {
                errorFlow.emit(AppError.CustomError(errorText = response.error.toString()))
                return emptyList()
            }
        }
    }

    private val currentModel: AiModels = AiModels.GeminiModels.BASE_MODEL

}

interface AiModels {

    val modelName: String

    @Serializable
    data class GroqModel(
        @Json(name = "name")
        override val modelName: String = "llama-3.2-11b-vision-preview",
    ) : AiModels

    @Serializable
    data class GeminiModel(
        override val modelName: String = "gemini-1.5-flash",
        val version: String = "1.0.0",
        val displayName: String = "Gemini",
        val description: String = "Gemini is a language model that can generate images using the LLM",
        val supportedGenerationMethods: List<String> = emptyList(),
        val isSupported: Boolean = supportedGenerationMethods.contains("generateContent"),
    ) : AiModels

    @Serializable
    data class OpenAIModel(
        override val modelName: String = "openai-1",
    ) : AiModels


    enum class GroqModels(override val modelName: String) : AiModels {
        BASE_MODEL("llama-3.2-11b-vision-preview"),
        TEXT_MODEL("llama3-groq-8b-8192-tool-use-preview")
    }

    enum class GeminiModels(override val modelName: String) : AiModels {
        BASE_MODEL("gemini-1.5-flash"),
        GEMINI_IMAGE("")
    }

    enum class OpenAIModels(override val modelName: String) : AiModels {
        BASE_MODEL(""),
        OPENAI_IMAGE("")
    }

}