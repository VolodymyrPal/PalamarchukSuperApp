package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.entities.Role
import com.hfad.palamarchuksuperapp.data.entities.SubMessageAI
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

interface SendChatRequestUseCase {
    suspend operator fun invoke(message: MessageAI, handlers: List<AiModelHandler>)
}

class SendChatRequestUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
    private val addAiMessageUseCase: AddAiMessageUseCase
) : SendChatRequestUseCase {

    override suspend operator fun invoke(message: MessageAI, handlers: List<AiModelHandler>) {

        addAiMessageUseCase(message)

        val listToSend: PersistentList<MessageAI> = chatAiRepository.chatAiChatFlow.value

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

            addAiMessageUseCase(messageToAdd)

            val lastIndex = chatAiRepository.chatAiChatFlow.value.lastIndex

            requests.forEach { (requestIndex, request) ->
                launch {
                    request.await().let { result ->

                        chatAiRepository.chatAiChatFlow.update { list ->
                            val updatedContent =
                                list[lastIndex].content.mapIndexed { index, subMessage ->
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
}