package com.hfad.palamarchuksuperapp.data.repository

import android.util.Log
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.services.GeminiApiHandler
import com.hfad.palamarchuksuperapp.data.services.GroqApiHandler
import com.hfad.palamarchuksuperapp.data.services.OpenAIApiHandler
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler


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

    override suspend fun getModels(): List<AiModel> {

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