package com.hfad.palamarchuksuperapp

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.usecases.MapAiModelHandlerUseCase
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DataStoreHandler @Inject constructor(
    val context: Context,
    private val mapAiModelHandlerUseCase: MapAiModelHandlerUseCase,
) {
    private val aiHandlerList = context.aiHandlerList

    suspend fun saveAiHandlerList(list: List<AiModelHandler>) =
        aiHandlerList.edit { preferences ->
            val jsonToSave = Json.encodeToString(list.map { it.aiHandlerInfo.value })
            preferences[AI_HANDLER_LIST] = jsonToSave
        }

    suspend fun getAiHandlerList(): List<AiModelHandler> {
        return if (!aiHandlerList.data.first()[AI_HANDLER_LIST].isNullOrBlank()) {
            mapAiModelHandlerUseCase(
                Json.decodeFromString<List<AiHandlerInfo>>(
                    aiHandlerList.data.first()[AI_HANDLER_LIST] ?: ""
                )
            )
        } else {
            val list = AiHandlerInfo.DEFAULT_LIST_AI_HANDLER_INFO
            val listAiHandlerInfo = mapAiModelHandlerUseCase(list)
            saveAiHandlerList(listAiHandlerInfo)
            listAiHandlerInfo
        }
    }
}

val AI_HANDLER_LIST = stringPreferencesKey("ai_handler_list")

val Context.appSettingsStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
val Context.chatScreenInfoStore: DataStore<Preferences> by preferencesDataStore(name = "chat_screen_info")
val Context.aiHandlerList: DataStore<Preferences> by preferencesDataStore(name = "ai_handler_list")