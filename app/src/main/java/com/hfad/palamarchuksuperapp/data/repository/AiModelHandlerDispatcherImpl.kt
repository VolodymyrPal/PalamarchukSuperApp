package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandlerDispatcher
import javax.inject.Inject

class AiModelHandlerDispatcherImpl @Inject constructor(
    private val apiHandlers: Set<@JvmSuppressWildcards AiModelHandler>,
) : AiModelHandlerDispatcher {

    override val handlerList: List<AiModelHandler> = apiHandlers.toMutableList().onEach {
        it.chosen = false
    }

    override fun getHandler(aiModelHandler: AiModelHandler): AiModelHandler {
        TODO("Not yet implemented")
    }

}