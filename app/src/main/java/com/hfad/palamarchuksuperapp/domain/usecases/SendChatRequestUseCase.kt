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
    suspend operator fun invoke(
        message: MessageGroup,
        handlers: List<AiModelHandler>,
    ): Result<Unit, AppError>
}

class SendAiRequestUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
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

        chatAiRepository.addMessageGroup(messageGroupWithChatID = message).getOrHandleAppError {
            return Result.Error(it)
        }

        val currentChat =
            chatAiRepository.getChatWithMessagesById(chatId).getOrHandleAppError {
                return Result.Error(it)
            }

        val contextMessages = currentChat.messageGroups.toPersistentList()

        val responseMessageGroupId = chatAiRepository.addMessageGroup(
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
                    val pendingMessage = chatAiRepository.addAndGetMessageAi(
                        MessageAI(
                            id = 0, //Room will provide the id
                            status = MessageStatus.LOADING,
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

        val errors = mutableListOf<AppError>()

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
                            chatAiRepository.updateMessageAi(
                                messageAI = successMessage
                            )
                        }

                        is Result.Error -> {
                            val errorMessage =
                                when (result.error) { //TODO could throw internet error, need to check it
                                    is AppError.NetworkException -> "Error with network"
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
                    synchronized(errors) {
                        if (result is Result.Error) {
                            errors.add(result.error)
                        }
                    }
                }
            }
        }
        return if (errors.isNotEmpty()) {
            Result.Error(
                error = AppError.CustomError(
                    "Some handlers failed: ${errors.joinToString()} "
                )
            )
        } else {
            Result.Success(Unit)
        }
    }
}