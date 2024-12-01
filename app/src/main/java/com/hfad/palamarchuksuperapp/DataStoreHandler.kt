package com.hfad.palamarchuksuperapp

import android.content.Context
import android.util.Log
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
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DataStoreHandler @Inject constructor(
    val context: Context,
    private val mapAiModelHandlerUseCase: MapAiModelHandlerUseCase,
) {
    private val aiHandlerList = context.aiHandlerList

    private suspend fun saveAiHandlerList(list: List<AiModelHandler>) =
        aiHandlerList.edit { preferences ->
            val listToSave = ListAiModelHandler(list.map { it.aiHandler })
            Log.d("Saved list: ", "$listToSave")
            val newList = Json.encodeToString(ListAiModelHandler.serializer(), listToSave)
            Log.d("Saved Json list: ", newList)
            preferences[AI_HANDLER_LIST] = newList
        }

    suspend fun getAiHandlerList(): List<AiModelHandler> {
        Log.d("My saved data: ", "${aiHandlerList.data.first()[AI_HANDLER_LIST]}")
//        Log.d(
//            "Serialized data: ", "${
//                Json.decodeFromString(
//                    ListAiModelHandler.serializer(),
//                    aiHandlerList.data.first()[AI_HANDLER_LIST] ?: ""
//                )
//            }"
//        )
//        return if (aiHandlerList.data.first()[AI_HANDLER_LIST] != null) {
//            val a = Json.decodeFromString(
//                ListAiModelHandler.serializer(),
//                aiHandlerList.data.first()[AI_HANDLER_LIST] ?: ""
//            )
//            mapAiModelHandlerUseCase(
//                a.list
//            )
//        } else {
        val list = persistentListOf(
            mapAiModelHandlerUseCase(
                AiHandler(
                    LLMName.OPENAI,
                    chosen = true,
                    enabled = true,
                    model = AiModel.OPENAI_BASE_MODEL
                )
            ),
            mapAiModelHandlerUseCase(
                AiHandler(
                    LLMName.GEMINI,
                    chosen = false,
                    enabled = false,
                    model = AiModel.GEMINI_BASE_MODEL
                )
            ),
            mapAiModelHandlerUseCase(
                AiHandler(
                    LLMName.GROQ,
                    chosen = false,
                    enabled = false,
                    model = AiModel.GROQ_BASE_MODEL
                )
            )
        )
        saveAiHandlerList(list)
        return list
        //}
    }
}

val AI_HANDLER_LIST = stringPreferencesKey("ai_handler_list")

val Context.appSettingsStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
val Context.chatScreenInfoStore: DataStore<Preferences> by preferencesDataStore(name = "chat_screen_info")
val Context.aiHandlerList: DataStore<Preferences> by preferencesDataStore(name = "ai_handler_list")