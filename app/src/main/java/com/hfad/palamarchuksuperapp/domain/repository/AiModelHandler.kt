package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import kotlinx.collections.immutable.PersistentList

interface AiModelHandler {
    suspend fun getResponse(messageList: PersistentList<MessageAI>): MessageAI
}