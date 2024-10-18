package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.DataError
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.MutableStateFlow

interface ChatAiRepository {
    val chatAiChatFlow: MutableStateFlow<PersistentList<MessageAI>>
    val errorFlow: MutableStateFlow<DataError?>
    suspend fun getRespondChatOrImage(message: String)
}