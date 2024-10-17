package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.services.GeminiApiHandler
import com.hfad.palamarchuksuperapp.data.services.GroqApiHandler
import com.hfad.palamarchuksuperapp.data.services.GroqContentType
import com.hfad.palamarchuksuperapp.data.services.Message
import com.hfad.palamarchuksuperapp.data.services.OpenAIApiHandler
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


class ChatAiRepositoryImpl @Inject constructor(
    private val groqApiHandler: GroqApiHandler,
    private val geminiApiHandler: GeminiApiHandler,
    private val openAIApiHandler: OpenAIApiHandler,

    ) : ChatAiRepository {
    override val chatAiChatFlow: StateFlow<Message> = TODO()

    override val errorFlow: MutableStateFlow<PersistentList<*>> = MutableStateFlow(
        (persistentListOf<MessageAI>()))

    override fun getRespondChatOrImage(message: Message) {

    }

}

data class MessageAI(
    val role: String,
    val content: String,
)