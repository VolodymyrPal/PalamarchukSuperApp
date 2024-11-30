package com.hfad.palamarchuksuperapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.LLMName
import com.hfad.palamarchuksuperapp.domain.models.AiHandler
import com.hfad.palamarchuksuperapp.domain.models.ListAiModelHandler
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.usecases.MapAiModelHandlerUseCase
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DataStoreHandler @Inject constructor(
    val context: Context,
    private val mapAiModelHandlerUseCase: MapAiModelHandlerUseCase,
) {
    val appSettings = context.appSettingsStore
    val chatScreenInfo = context.chatScreenInfoStore
    val aiHandlerList = context.aiHandlerList

    suspend fun saveAiHandlerList(list: List<AiModelHandler>) = aiHandlerList.edit { preferences ->
        val listToSave = ListAiModelHandler(list.map { it.aiHandler })
        val newList = Json.encodeToString(ListAiModelHandler.serializer(), listToSave)
        //val newList = list.map { Json.encodeToString(AiHandler.serializer(), it.aiHandler) }
        preferences[AI_HANDLER_LIST] = newList
    }


    suspend fun getAiHandlerList(): List<AiModelHandler> {
        val aiHandlerList = Json.decodeFromString(
            ListAiModelHandler.serializer(),
            aiHandlerList.data.first()[AI_HANDLER_LIST] ?: Json.encodeToString(
                ListAiModelHandler.serializer(),
                ListAiModelHandler(
                    listOf(
                        AiHandler(
                            llmName = LLMName.OPENAI,
                            model = AiModel.OPENAI_BASE_MODEL,
                            chosen = true,
                            enabled = true
                        )
                    )
                )
            )
        )
        return aiHandlerList.list.map { mapAiModelHandlerUseCase(it) }
    }
}

val AI_HANDLER_LIST = stringPreferencesKey("ai_handler_list")

val Context.appSettingsStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
val Context.chatScreenInfoStore: DataStore<Preferences> by preferencesDataStore(name = "chat_screen_info")
val Context.aiHandlerList: DataStore<Preferences> by preferencesDataStore(name = "ai_handler_list")