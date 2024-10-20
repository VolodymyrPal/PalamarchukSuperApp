package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.repository.AiModels
import kotlinx.collections.immutable.PersistentList

interface AiModelHandler {
    suspend fun getResponse(
        messageList: PersistentList<MessageAI>,
        model: AiModels? = null,
    ): MessageAI
}