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

        val response: Result<MessageAI, AppError> = currentHandler.value.getResponse(
            chatAiChatFlow.value, model = currentModel.value
        )

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

        val models = currentHandler.value.getModels()

        when (models) {
            is Result.Success -> {
                Log.d("Models: ", "${models.data}")

                models.data.forEach {
                    Log.d("Gemini models:", "${it.modelName}")
                }
                listOfModels.update {
                    it.addAll(models.data)
                }
                Log.d("My list: ", "${listOfModels.value}")
                return models.data
            }

            is Result.Error -> {
                errorFlow.emit(AppError.CustomError(errorText = "Error"))
                return emptyList()
            }
        }
    }

    override val listOfModels: MutableStateFlow<PersistentList<AiModel>> =
        MutableStateFlow(persistentListOf())

    private val currentHandler: MutableStateFlow<AiModelHandler> =
        MutableStateFlow(groqApiHandler)

    override val currentModel: MutableStateFlow<AiModel> =
        MutableStateFlow(AiModel.GeminiModels.BASE_MODEL)

    override fun setHandlerOrModel(model: AiModel) {
        when (model) {
            is AiModel.GroqModels -> {
                currentHandler.value = geminiApiHandler
            }
            is AiModel.GeminiModels -> {
                currentHandler.value = geminiApiHandler
            }
            is AiModel.OpenAIModels -> {
                currentHandler.value = openAIApiHandler
            }
        }
        currentModel.update {
            model
        }
    }

}