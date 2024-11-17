package com.hfad.palamarchuksuperapp.data.repository

import android.util.Log
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.entities.Role
import com.hfad.palamarchuksuperapp.data.entities.SubMessageAI
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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope


class ChatAiRepositoryImpl @Inject constructor(
    private val apiHandlers: Set<@JvmSuppressWildcards AiModelHandler>,
) : ChatAiRepository {

    override val chatAiChatFlow: MutableStateFlow<PersistentList<MessageAI>> =
        MutableStateFlow(persistentListOf())

    override val errorFlow: MutableSharedFlow<AppError?> = MutableSharedFlow()

    override suspend fun getRespondChatOrImage(message: MessageAI, handlers: List<AiModelHandler>) {

        chatAiChatFlow.update { chatAiChatFlow.value.add(message) }

        val listToSend: PersistentList<MessageAI> = chatAiChatFlow.value

        supervisorScope {

            val requests: List<Pair<Int, Deferred<Result<SubMessageAI, AppError>>>> =
                apiHandlers.mapIndexed { index, handler ->
                    index to async {
                        handler.getResponse(
                            listToSend, null
                        )
                    }
                }

            val loadingContent = requests.map { (index, _) ->
                SubMessageAI(
                    id = index,
                    message = "",
                    model = null,
                    loading = true
                )
            }.toPersistentList()


            val messageToAdd = MessageAI(
                role = Role.MODEL,
                content = loadingContent,
                type = MessageType.TEXT
            )

            chatAiChatFlow.update {
                it.add(messageToAdd)
            }
            val lastIndex = chatAiChatFlow.value.lastIndex

            requests.forEach { (requestIndex, request) ->
                launch {
                    request.await().let { result ->
                        chatAiChatFlow.update { list ->
                            val updatedContent = list[lastIndex].content.mapIndexed { index, subMessage ->
                                // Обновляем только соответствующий SubMessageAI
                                if (index == requestIndex) {
                                    when (result) {
                                        is Result.Success -> subMessage.copy(
                                            message = result.data.message,
                                            model = result.data.model,
                                            loading = false
                                        )
                                        is Result.Error -> subMessage.copy(
                                            message = "Error",
                                            loading = false
                                        )
                                    }
                                } else {
                                    subMessage // остальные остаются без изменений
                                }
                            }.toPersistentList()

                            list.set(lastIndex, list[lastIndex].copy(content = updatedContent))
                        }
                    }
                }
            }
        }
    }

    override suspend fun getModels(): List<AiModel> {

        val models = currentHandler().value.getModels()
        return listOfModels.value
    }

    override val listOfModels: MutableStateFlow<PersistentList<AiModel>> = // TODO handlerDispatcher.handlerList
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
                apiHandlers.first()
            }

            is AiModel.GeminiModels -> {
                apiHandlers.first()
            }

            is AiModel.OpenAIModels -> {
                apiHandlers.first()
            }

            else -> throw Error("Handler not found")
        }
    }

    override fun setHandlerOrModel(model: AiModel) {
        currentModel.update { model }
    }
}