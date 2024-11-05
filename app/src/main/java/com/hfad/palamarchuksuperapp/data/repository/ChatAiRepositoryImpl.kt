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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


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

        val response: Result<MessageAI, AppError> = currentHandler().value.getResponse(
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

        val models = currentHandler().value.getModels()

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

    override val currentModel: MutableStateFlow<AiModel> =
        MutableStateFlow(AiModel.OpenAIModels.BASE_MODEL)

    override suspend fun currentHandler(): StateFlow<AiModelHandler> = currentModel.map {
        Log.d("Model: ", "$it")
        when (it) {
            is AiModel.GroqModels -> {
                groqApiHandler
            }

            is AiModel.GeminiModels -> {
                geminiApiHandler
            }

            is AiModel.OpenAIModels -> {
                openAIApiHandler
            }

            else -> {
                throw Exception("Handler not found")
            }
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = openAIApiHandler  /** TODO change it  */
    )

//        currentModel.collect {
//            when (it) {
//                is AiModel.GroqModels -> {
//                    groqApiHandler
//                }
//
//                is AiModel.GeminiModels -> {
//                    geminiApiHandler
//                }
//
//                is AiModel.OpenAIModels -> {
//                    openAIApiHandler
//                }
//
//                else -> {
//                    throw Exception("Handler not found")
//                }
//            }
//        }


    override fun setHandlerOrModel(model: AiModel) {
        currentModel.update { model }
    }
}