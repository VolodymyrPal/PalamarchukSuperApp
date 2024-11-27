package com.hfad.palamarchuksuperapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hfad.palamarchuksuperapp.domain.models.AiHandler
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DataStoreHandler @Inject constructor (val context: Context) {
    val appSettings = context.appSettingsStore
    val chatScreenInfo = context.chatScreenInfoStore
    val aiHandlerList = context.aiHandlerList

    suspend fun saveAiHandlerList(list: List<AiModelHandler>) = aiHandlerList.edit { preferences ->
        val newList = Json.encodeToString(AiHandler.serializer(), list[0].aiHandler)
            //list.map { Json.encodeToString(AiHandler.serializer(), it.aiHandler) }
        preferences[AI_HANDLER_LIST] = newList
    }
}
val AI_HANDLER_LIST = stringPreferencesKey("ai_handler_list")

val Context.appSettingsStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
val Context.chatScreenInfoStore: DataStore<Preferences> by preferencesDataStore(name = "chat_screen_info")
val Context.aiHandlerList: DataStore<Preferences> by preferencesDataStore(name = "ai_handler_list")