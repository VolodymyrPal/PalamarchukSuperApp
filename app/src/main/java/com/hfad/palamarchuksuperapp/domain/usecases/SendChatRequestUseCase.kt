package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Role
import com.hfad.palamarchuksuperapp.domain.models.SubMessageAI
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

interface SendChatRequestUseCase {
    suspend operator fun invoke(message: MessageAI, handlers: List<AiModelHandler>)
}

class SendAiRequestUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
    private val addAiMessageUseCase: AddAiMessageUseCase,
    private val getAiChatUseCase: GetAiChatUseCase,
    private val updateAiMessageUseCase: UpdateAiMessageUseCase,
) : SendChatRequestUseCase {

    override suspend operator fun invoke(message: MessageAI, handlers: List<AiModelHandler>) {

        if (handlers.isEmpty()) {
            chatAiRepository.errorFlow.emit(AppError.CustomError("No handlers provided"))
            return
        }

        addAiMessageUseCase(message)
        /**
         *
         *
         * Need to better handler error message and loading messages.
         *
         */
        val listToSend: PersistentList<MessageAI> = getAiChatUseCase().first()

        supervisorScope {

            val newMessageIndex = listToSend.size

            val requests: List<Pair<Int, Deferred<Result<SubMessageAI, AppError>>>> =
                handlers.filter { it.aiHandlerInfo.value.isSelected }.mapIndexed { index, handler ->
                    index to async {
                        handler.getResponse(
                            listToSend,
                            newMessageIndex
                        ) //TODO dealing with newMessageIndex not the best
                    }
                }

            val loadingContent = requests.map { (index, _) ->
                SubMessageAI(
                    id = index,
                    message = "",
                    model = null,
                    loading = true,
                    messageAiID = newMessageIndex
                )
            }.toPersistentList()


            val messageToAdd = MessageAI(
                id = newMessageIndex,
                role = Role.MODEL,
                content = loadingContent,
                type = MessageType.TEXT
            )

            addAiMessageUseCase(messageToAdd)

            val indexOfRequest = chatAiRepository.chatAiChatFlow.value.lastIndex

            requests.forEach { (requestIndex, request) ->
                launch {
                    val result: Result<SubMessageAI, AppError> = request.await()
                    if (result is Result.Success) {
                        updateAiMessageUseCase.invoke(
                            subMessageAI =
                                SubMessageAI(
                                    id = requestIndex,
                                    message = result.data.message,
                                    model = result.data.model,
                                    messageAiID = result.data.messageAiID
                                ),
                            messageIndex = indexOfRequest,
                            subMessageIndex = requestIndex
                        )
                    } else {
                        val errorMessage =
                            when ((result as Result.Error<SubMessageAI, AppError>).error) {
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
                            subMessageAI =
                                SubMessageAI(
                                    id = requestIndex,
                                    message = errorMessage,
                                    model = result.data?.model,
                                    messageAiID = result.data?.messageAiID
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