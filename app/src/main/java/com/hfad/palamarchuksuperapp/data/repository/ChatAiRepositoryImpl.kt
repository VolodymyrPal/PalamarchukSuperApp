package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.services.GeminiApiHandler
import com.hfad.palamarchuksuperapp.data.services.GroqApiHandler
import com.hfad.palamarchuksuperapp.data.services.OpenAIApiHandler
import com.hfad.palamarchuksuperapp.data.services.toGeminiRequest
import com.hfad.palamarchuksuperapp.domain.models.DataError
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


class ChatAiRepositoryImpl @Inject constructor(
    private val groqApiHandler: GroqApiHandler,
    private val geminiApiHandler: GeminiApiHandler,
    private val openAIApiHandler: OpenAIApiHandler,
) : ChatAiRepository {

    override val chatAiChatFlow: MutableStateFlow<PersistentList<MessageAI>> =
        MutableStateFlow(persistentListOf())

    override val errorFlow: MutableStateFlow<DataError?> = MutableStateFlow(null)

    override suspend fun getRespondChatOrImage(message: MessageAI) {
        try {
            chatAiChatFlow.update { chatAiChatFlow.value.add(message) }
            sendRequestToAI()
        } catch (e: Exception) {
            errorFlow.emit(DataError.CustomError(e.message ?: "Error"))
        }

    }

    private suspend fun sendRequestToAI() {
        val response = when (currentModel) {
            is AiModels.GroqModels -> {
                groqApiHandler.getResponse(chatAiChatFlow.value)
            }

            is AiModels.GeminiModels -> {
                geminiApiHandler.sendRequestWithResponse(chatAiChatFlow.value.toGeminiRequest()) //TODO request
            }


            is AiModels.OpenAIModels,
                -> { openAIApiHandler.sendRequestWithResponse()
                MessageAI()} //TODO request
            else -> {
                MessageAI()
            }
        }

        chatAiChatFlow.update { chatAiChatFlow.value.add(response) }

    }

    private val currentModel: AiModels = AiModels.GeminiModels.BASE_MODEL


}

interface AiModels {

    val value: String

    enum class GroqModels(override val value: String) : AiModels {
        BASE_IMAGE_MODEL("llama-3.2-11b-vision-preview"),
        TEXT_MODEL("llama3-groq-8b-8192-tool-use-preview"),
    }

    enum class GeminiModels(override val value: String) : AiModels {
        BASE_MODEL("gemini-1.5-flash"),
        GEMINI_IMAGE("")
    }

    enum class OpenAIModels(override val value: String) : AiModels {
        BASE_MODEL(""),
        OPENAI_IMAGE("")
    }

}