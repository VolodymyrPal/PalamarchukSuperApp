package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.core.domain.Result
import com.hfad.palamarchuksuperapp.domain.models.Role
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.repository.ChatController
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.datetime.Clock
import javax.inject.Inject

interface SendChatRequestUseCase {
    suspend operator fun invoke(
        message: MessageGroup,
        handlers: List<AiModelHandler>,
    ): Result<Unit, AppError>
}

class SendAiRequestUseCaseImpl @Inject constructor(
    private val chatController: ChatController,
) : SendChatRequestUseCase {

    override suspend operator fun invoke(
        message: MessageGroup,
        handlers: List<AiModelHandler>,
    ): Result<Unit, AppError> {
        val chatId = message.chatId
        val activeHandlers = handlers.filter { it.aiHandlerInfo.value.isSelected }

        if (activeHandlers.isEmpty()) {
            return Result.Error(AppError.CustomError("No handlers provided"))
        }

        chatController.addMessageGroup(messageGroupWithChatID = message).getOrHandleAppError {
            return Result.Error(it)
        }

        val currentChat =
            chatController.getChatWithMessagesById(chatId).getOrHandleAppError {
                return Result.Error(it)
            }

        val contextMessages = currentChat.messageGroups.toPersistentList()

        val responseMessageGroupId = chatController.addMessageGroup(
            MessageGroup(
                id = 0, //Room will provide the id
                role = Role.MODEL,
                type = MessageType.TEXT,
                chatId = chatId,
                content = emptyList()
            )
        ).getOrHandleAppError {
            return Result.Error(it)
        }
        supervisorScope {
            val requests: List<Pair<MessageAI, Deferred<Result<MessageAI, AppError>>>> =
                activeHandlers.map { handler ->
                    val pendingMessage = chatController.addAndGetMessageAi(
                        MessageAI(
                            id = 0, //Room will provide the id
                            status = MessageStatus.LOADING,
                            model = handler.aiHandlerInfo.value.model,
                            messageGroupId = responseMessageGroupId.toInt(),
                            timestamp = Clock.System.now().toString()
                        )
                    ).getOrHandleAppError {
                        return@supervisorScope Result.Error(it)
                    }
                    pendingMessage to async {
                        handler.getResponse(contextMessages)
                    }
                }

            return@supervisorScope proceedRequest(requests)
        }
        return Result.Success(Unit)
    }

    private suspend fun proceedRequest(
        requests: List<Pair<MessageAI, Deferred<Result<MessageAI, AppError>>>>,
    ): Result<Unit, AppError> {

        supervisorScope {

            requests.forEach { (messageAi, request) ->
                launch {

                    val result = request.await()

                    when (result) {
                        is Result.Success -> {
                            val successMessage = messageAi.copy(
                                message = result.data.message,
                                model = result.data.model,
                                timestamp = Clock.System.now().toString(),
                                status = MessageStatus.SUCCESS
                            )
                            chatController.updateMessageAi(successMessage)
                        }

                        is Result.Error -> { //TODO place to handle error]
                            val error = result.error
                            val errorMessageText =
                                when (error) {
                                    is AppError.NetworkException ->
                                        error.message + if (error.cause != null) "\nPlease contact developer, " +
                                                "error caused by: \n\n${error.cause}" else ""

                                    else -> (result.error as AppError.CustomError).message
                                }
                            val errorMessage = messageAi.copy(
                                message = errorMessageText ?: "Undefined error",
                                timestamp = Clock.System.now().toString(),
                                status = MessageStatus.ERROR
                            )
                            chatController.updateMessageAi(errorMessage)
                        }
                    }
                }
            }
        }
        return Result.Success(Unit)
    }
}