package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.core.domain.AppResult
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
    ): AppResult<Unit, AppError>
}

class SendAiRequestUseCaseImpl @Inject constructor(
    private val chatController: ChatController,
) : SendChatRequestUseCase {

    override suspend operator fun invoke(
        message: MessageGroup,
        handlers: List<AiModelHandler>,
    ): AppResult<Unit, AppError> {
        val chatId = message.chatId
        val activeHandlers = handlers.filter { it.aiHandlerInfo.value.isSelected }

        if (activeHandlers.isEmpty()) {
            return AppResult.Error(AppError.CustomError("No handlers provided"))
        }

        chatController.addMessageGroup(messageGroupWithChatID = message).getOrHandleAppError {
            return AppResult.Error(it)
        }

        val currentChat =
            chatController.getChatWithMessagesById(chatId).getOrHandleAppError {
                return AppResult.Error(it)
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
            return AppResult.Error(it)
        }
        supervisorScope {
            val requests: List<Pair<MessageAI, Deferred<AppResult<MessageAI, AppError>>>> =
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
                        return@supervisorScope AppResult.Error(it)
                    }
                    pendingMessage to async {
                        handler.getResponse(contextMessages)
                    }
                }

            return@supervisorScope proceedRequest(requests)
        }
        return AppResult.Success(Unit)
    }

    private suspend fun proceedRequest(
        requests: List<Pair<MessageAI, Deferred<AppResult<MessageAI, AppError>>>>,
    ): AppResult<Unit, AppError> {

        supervisorScope {

            requests.forEach { (messageAi, request) ->
                launch {

                    val result = request.await()

                    when (result) {
                        is AppResult.Success -> {
                            val successMessage = messageAi.copy(
                                message = result.data.message,
                                model = result.data.model,
                                timestamp = Clock.System.now().toString(),
                                status = MessageStatus.SUCCESS
                            )
                            chatController.updateMessageAi(successMessage)
                        }

                        is AppResult.Error -> { //TODO place to handle error]
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
        return AppResult.Success(Unit)
    }
}