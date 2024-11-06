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
        return listOfModels.value
    }

    override val listOfModels: MutableStateFlow<PersistentList<AiModel>> =
        MutableStateFlow(persistentListOf(
            AiModel.GeminiModels.BASE_MODEL,
            AiModel.GroqModels.BASE_MODEL,
            AiModel.OpenAIModels.BASE_MODEL
        ))

    override val currentModel: MutableStateFlow<AiModel> =
        MutableStateFlow(AiModel.OpenAIModels.BASE_MODEL)

    override suspend fun currentHandler(): StateFlow<AiModelHandler> = currentModel.map {
        resolveHanlder()
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = resolveHanlder()
    )

    private fun resolveHanlder(): AiModelHandler {
        val model = currentModel.value
        Log.d("Model: ", "$model")
        return when (model) {
            is AiModel.GroqModels -> {
                groqApiHandler
            }

            is AiModel.GeminiModels -> {
                geminiApiHandler
            }

            is AiModel.OpenAIModels -> {
                openAIApiHandler
            }

            else -> throw Error("Handler not found")
        }
    }

    override fun setHandlerOrModel(model: AiModel) {
        currentModel.update { model }
    }
}