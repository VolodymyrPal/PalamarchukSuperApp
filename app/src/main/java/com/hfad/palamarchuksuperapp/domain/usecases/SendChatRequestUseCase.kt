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
        val activeHandlers = handlers.filter { it.aiHandlerInfo.value.isSelected }

        if (activeHandlers.isEmpty()) {
            chatAiRepository.errorFlow.emit(AppError.CustomError("No handlers provided"))
            return
        }

        chatAiRepository.addMessageGroup(messageGroupWithChatID = message)

        val currentChat = chatAiRepository.getChatWithMessagesById(chatId)

        val contextMessages = currentChat.messageGroups.toPersistentList()

        val responseMessageGroup = chatAiRepository.addMessageGroup(
            MessageGroup(
                id = 0, //Room will provide the id
                role = Role.MODEL,
                type = MessageType.TEXT,
                chatId = chatId,
                content = emptyList()
            )
        )

        supervisorScope {

            val requests: List<Pair<MessageAI, Deferred<Result<MessageAI, AppError>>>> =
                activeHandlers.map { handler ->
                    chatAiRepository.addAndGetMessageAi(
                        MessageAI(
                            id = 0, //Room will provide the id
                            message = "",
                            model = null,
                            loading = true,
                            messageGroupId = responseMessageGroup.toInt(),
                            timestamp = Clock.System.now().toString()
                        )
                    ) to async {
                        handler.getResponse(contextMessages)
                    }
                }

            requests.forEach { (messageAi, request) ->
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