package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
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
//            chatAiRepository.errorFlow.emit(AppError.CustomError("No handlers provided")) TODO how to handle error better
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
                            status = MessageStatus.LOADING,
                            messageGroupId = responseMessageGroup.toInt(),
                            timestamp = Clock.System.now().toString()
                        )
                    ) to async {
                        handler.getResponse(contextMessages)
                    }
                }

            requests.forEach { (messageAi, request) ->
                launch {

                    val result = request.await()
                    when (result) {
                        is Result.Success -> {
                            chatAiRepository.updateMessageAi(
                                messageAI = messageAi.copy(
                                    message = result.data.message,
                                    model = result.data.model,
                                    timestamp = Clock.System.now().toString(),
                                    status = MessageStatus.SUCCESS
                                )
                            )
                        }

                        is Result.Error -> {
                            val errorMessage = when (result.error) {
                                is AppError.CustomError -> result.error.errorText
                                    ?: "Undefined Error"

                                is AppError.Network -> "Error with network"
                                else -> "Undefined error"
                            }
                            chatAiRepository.updateMessageAi(
                                messageAI = messageAi.copy(
                                    message = errorMessage,
                                    model = result.data?.model,
                                    timestamp = Clock.System.now().toString(),
                                    status = MessageStatus.ERROR
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}