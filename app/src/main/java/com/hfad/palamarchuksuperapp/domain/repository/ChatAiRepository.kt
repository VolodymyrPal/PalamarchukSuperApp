package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.services.Message
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ChatAiRepository {
    val chatAiChatFlow: StateFlow<*>
    val errorFlow: MutableStateFlow<PersistentList<*>>
    fun getRespondChatOrImage(message : Message)
}