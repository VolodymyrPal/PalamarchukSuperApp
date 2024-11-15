package com.hfad.palamarchuksuperapp.domain.repository

interface AiModelHandlerDispatcher {
    fun getHandler(aiModelHandler: AiModelHandler): AiModelHandler
    val handlerList : List<AiModelHandler>
}