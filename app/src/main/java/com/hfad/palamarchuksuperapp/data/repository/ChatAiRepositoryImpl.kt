package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.models.Result


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
}