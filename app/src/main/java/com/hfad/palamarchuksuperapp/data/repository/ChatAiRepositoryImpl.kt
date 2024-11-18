package com.hfad.palamarchuksuperapp.data.repository

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
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope


class ChatAiRepositoryImpl @Inject constructor() : ChatAiRepository {

    override val chatAiChatFlow: MutableStateFlow<PersistentList<MessageAI>> =
        MutableStateFlow(persistentListOf())

    override val errorFlow: MutableSharedFlow<AppError?> = MutableSharedFlow()

    override suspend fun getRespondChatOrImage(message: MessageAI, handlers: List<AiModelHandler>) {

        chatAiChatFlow.update { chatAiChatFlow.value.add(message) }

        val listToSend: PersistentList<MessageAI> = chatAiChatFlow.value

        supervisorScope {

            val requests: List<Pair<Int, Deferred<Result<SubMessageAI, AppError>>>> =
                handlers.mapIndexed { index, handler ->
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
                            val updatedContent =
                                list[lastIndex].content.mapIndexed { index, subMessage ->
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

    override suspend fun getModels(handler: AiModelHandler): List<AiModel> {
        val result = handler.getModels()
        return if (result is Result.Success) {
            result.data
        } else {
            errorFlow.emit(AppError.CustomError((result as Result.Error).error.toString()))
            listOf()
        }
    }

    override suspend fun getModels(handlers: List<AiModelHandler>): List<AiModel> {
        return handlers.flatMap { handler ->
            val result = handler.getModels()
            when (result) {
                is Result.Success -> {
                    result.data
                }

                is Result.Error -> {
                    errorFlow.emit(AppError.CustomError(result.error.toString()))
                    listOf()
                }
            }
        }
    }
}