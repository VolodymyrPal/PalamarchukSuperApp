package com.hfad.palamarchuksuperapp.data.repository

import android.util.Log
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.entities.Role
import com.hfad.palamarchuksuperapp.data.entities.SubMessageAI
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
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch


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

        CoroutineScope(Dispatchers.IO).launch {

            val responseList: MutableList<SubMessageAI> = mutableListOf()

            val responses = listOf(
                async {
                    groqApiHandler.getResponse(
                        chatAiChatFlow.value,
                        model = AiModel.GroqModels.BASE_MODEL
                    )
                },
                async {
                    geminiApiHandler.getResponse(
                        chatAiChatFlow.value,
                        model = AiModel.GeminiModels.BASE_MODEL
                    )
                },
                async {
                    openAIApiHandler.getResponse(
                        chatAiChatFlow.value,
                        model = AiModel.OpenAIModels.BASE_MODEL
                    )
                }
            ).awaitAll()

            responses.forEach {
                if (it is Result.Success) {
                    responseList.add(it.data)
                } else {
                    errorFlow.emit(AppError.CustomError("$it failed."))
                }
            }

            val messageToAdd = MessageAI(
                role = Role.MODEL,
                content = responseList.toPersistentList(),
                type = MessageType.TEXT
            )
            chatAiChatFlow.update {
                it.add(
                    messageToAdd
                )
            }
            Log.d("AiRepository chat: ", "${chatAiChatFlow.value}")
        }
    }

    override suspend fun getModels(): List<AiModel> {

        val models = currentHandler().value.getModels()
        return listOfModels.value
    }

    override val listOfModels: MutableStateFlow<PersistentList<AiModel>> =
        MutableStateFlow(
            persistentListOf(
                AiModel.GeminiModels.BASE_MODEL,
                AiModel.GroqModels.BASE_MODEL,
                AiModel.OpenAIModels.BASE_MODEL
            )
        )

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