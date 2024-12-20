package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.SubMessageAI
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.coroutines.flow.update


class ChatAiRepositoryImpl @Inject constructor() : ChatAiRepository {

    override val chatAiChatFlow: MutableStateFlow<PersistentList<MessageAI>> =
        MutableStateFlow(persistentListOf())

    override val errorFlow: MutableSharedFlow<AppError?> = MutableSharedFlow()

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

    override suspend fun addMessage(messageAI: MessageAI) {
        chatAiChatFlow.update { it.add(messageAI) }
    }

    override suspend fun updateChat(listOfMessageAI: PersistentList<MessageAI>) {
        chatAiChatFlow.update { listOfMessageAI }
    }

    override suspend fun updateMessage(index: Int, updatedContent: MessageAI) {
        chatAiChatFlow.update {
            it.set(index, it[index].copy(content = updatedContent.content))
        }
    }

    override suspend fun updateSubMessages(
        index: Int,
        subMessageList: PersistentList<SubMessageAI>,
    ) {
        chatAiChatFlow.update {
            it.set(index, it[index].copy(content = subMessageList))
        }
    }

    override suspend fun updateSubMessage(
        index: Int,
        subMessageAI: SubMessageAI,
        indexSubMessage: Int,
    ) {
        chatAiChatFlow.update {
            it.set(
                index,
                it[index].copy(content = it[index].content.set(indexSubMessage, subMessageAI))
            )
        }
    }

}