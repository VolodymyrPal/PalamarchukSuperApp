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
    private val updateAiMessageUseCase: UpdateAiMessageUseCase
) : SendChatRequestUseCase {

    override suspend operator fun invoke(message: MessageAI, handlers: List<AiModelHandler>) {

        if (handlers.isEmpty()) {
            chatAiRepository.errorFlow.emit(AppError.CustomError("No handlers provided"))
            return
        }

        addAiMessageUseCase(message)

        val listToSend: PersistentList<MessageAI> = getAiChatUseCase().first()

        supervisorScope {
            val requests: List<Pair<Int, Deferred<Result<SubMessageAI, AppError>>>> =
                handlers.filter { it.aiHandlerInfo.value.isSelected }.mapIndexed { index, handler ->
                    index to async {
                        handler.getResponse(listToSend)
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
                id = listToSend.size,
                role = Role.MODEL,
                content = loadingContent,
                type = MessageType.TEXT
            )

            addAiMessageUseCase(messageToAdd)

            val indexOfRequest = chatAiRepository.chatAiChatFlow.value.lastIndex

            requests.forEach { (requestIndex, request) ->
                launch {
                    val result = request.await()
                    val updatedContent =
                        getAiChatUseCase().first()[indexOfRequest].content.mapIndexed { index, subMessage ->
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

                    updateAiMessageUseCase(updatedContent, indexOfRequest)
                }
            }
        }
    }
}