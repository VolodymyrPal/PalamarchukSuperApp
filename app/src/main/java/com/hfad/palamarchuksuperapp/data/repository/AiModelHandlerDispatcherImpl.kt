package com.hfad.palamarchuksuperapp.data.repository

import androidx.datastore.core.DataStore
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class AiModelHandlerDispatcherImpl @Inject constructor(
    private val apiHandlers: Set<@JvmSuppressWildcards AiModelHandler>,
    private val chatAiRepository: ChatAiRepository,
    private val handlerSettingStore: DataStore<AiHandlersSettings>,
)  {

//    override val handlerList: List<AiModelHandler> = apiHandlers.toMutableList().onEach {
//
//    }
//    //TODO ("In planning - add server call to get handlers information")
//
//    override fun getHandler(aiModelHandler: AiModelHandler): AiModelHandler {
//        return handlerList[0] //TODO("Not yet implemented")
//    }



}


data class AiHandlersSettings(
    val handlers: PersistentList<String>,
)