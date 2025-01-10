package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.models.Role
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.datetime.Clock
import javax.inject.Inject

interface SendChatRequestUseCase {
    suspend operator fun invoke(message: MessageGroup, handlers: List<AiModelHandler>)
}

class SendAiRequestUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : SendChatRequestUseCase {

    override suspend operator fun invoke(
        message: MessageGroup,
        handlers: List<AiModelHandler>,
    ) {
        val chatId = message.chatId

        if (handlers.isEmpty()) {
            chatAiRepository.errorFlow.emit(AppError.CustomError("No handlers provided"))
            return
        }

        chatAiRepository.addMessageGroup(messageGroupWithChatID = message)

        return
        val chat = chatAiRepository.getChatById(chatId)
        val contextMessages = chat.messages.toPersistentList()


        supervisorScope {

            val newMessageIndex = listToSend.size

            val requests: List<Pair<Int, Deferred<Result<MessageAI, AppError>>>> =
                handlers.filter { it.aiHandlerInfo.value.isSelected }.mapIndexed { index, handler ->
                    index to async {
                        handler.getResponse(
                            listToSend,
                            newMessageIndex
                        ) //TODO dealing with newMessageIndex not the best
                    }
                }

            val loadingContent = requests.map { (index, _) ->
                MessageAI(
                    id = index,
                    message = "",
                    model = null,
                    loading = true,
                    messageGroupId = newMessageIndex
                )
            }.toPersistentList()


            val messageToAdd = MessageGroup(
                id = newMessageIndex,
                role = Role.MODEL,
                content = loadingContent,
                type = MessageType.TEXT
            )

            addAiMessageUseCase(messageToAdd)

            val indexOfRequest = chatAiRepository.chatAiChatFlow.value.lastIndex

            requests.forEach { (requestIndex, request) ->
                launch {
                    val result: Result<MessageAI, AppError> = request.await()
                    if (result is Result.Success) {
                        updateAiMessageUseCase.invoke(
                            messageAI =
                                MessageAI(
                                    id = requestIndex,
                                    message = result.data.message,
                                    model = result.data.model,
                                    messageGroupId = result.data.messageGroupId
                                ),
                            messageIndex = indexOfRequest,
                            subMessageIndex = requestIndex
                        )
                    } else {
                        val errorMessage =
                            when ((result as Result.Error<MessageAI, AppError>).error) {
                                is AppError.CustomError -> {
                                    (result.error as AppError.CustomError).errorText
                                        ?: "Undefined Error"
                                }
//TODO update other errors
                                is AppError.Network -> {
                                    "Error with network"
                                }

                                else -> "Undefined error"
                            }
                        updateAiMessageUseCase.invoke(
                            messageAI =
                                MessageAI(
                                    id = requestIndex,
                                    message = errorMessage,
                                    model = result.data?.model,
                                    messageGroupId = result.data?.messageGroupId
                                        ?: requestIndex //TODO it pass info from API, change it
                                ),
                            messageIndex = indexOfRequest,
                            subMessageIndex = requestIndex
                        )
                    }
                }
            }
        }
    }
}