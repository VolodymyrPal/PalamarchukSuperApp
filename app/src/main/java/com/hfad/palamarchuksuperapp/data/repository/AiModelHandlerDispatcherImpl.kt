package com.hfad.palamarchuksuperapp.data.repository

import androidx.datastore.core.DataStore
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandlerDispatcher
import kotlinx.collections.immutable.PersistentList
import javax.inject.Inject

class AiModelHandlerDispatcherImpl @Inject constructor(
    private val apiHandlers: Set<@JvmSuppressWildcards AiModelHandler>,
    val handlerSettingStore: DataStore<AiHandlersSettings>,
) : AiModelHandlerDispatcher {

    override val handlerList: List<AiModelHandler> = apiHandlers.toMutableList().onEach {

    }
    //TODO ("In planning - add server call to get handlers information")

    override fun getHandler(aiModelHandler: AiModelHandler): AiModelHandler {
        return handlerList[0] //TODO("Not yet implemented")
    }

}

data class AiHandlersSettings(
    val handlers: PersistentList<String>,
)