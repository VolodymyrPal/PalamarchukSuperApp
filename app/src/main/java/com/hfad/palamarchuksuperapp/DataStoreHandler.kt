package com.hfad.palamarchuksuperapp

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

typealias JsonListAiHandlerInfo = String
typealias DayNightMode = Boolean

class DataStoreHandler @Inject constructor(
    val context: Context,
) {
    val getAiHandlerList: Flow<JsonListAiHandlerInfo> = context.aiHandlerList.data.map {
        it[AI_HANDLER_LIST] ?: ""
    }

    val getCurrentChatId: Flow<Int> = context.currentId.data.map {
        it[CURRENT_ID_KEY] ?: 0
    }

    suspend fun saveAiHandlerList(aiHandlerList: JsonListAiHandlerInfo) {
        context.aiHandlerList.edit {
            it[AI_HANDLER_LIST] = aiHandlerList
        }
    }

    val storedQuery: Flow<Boolean> = context.appSettingsStore.data.map {
        it[NIGHT_MODE_KEY] ?: true
    }.distinctUntilChanged()

    suspend fun setStoredNightMode(userNightMode: DayNightMode) {
        context.appSettingsStore.edit {
            it[NIGHT_MODE_KEY] = userNightMode
        }
        delay(200)
        setNightMode(userNightMode)
    }

    fun setNightMode(userNightMode: DayNightMode) {
        AppCompatDelegate.setDefaultNightMode(
            if (userNightMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    suspend fun setCurrentChatId(chatId: Int) {
        context.currentId.edit {
            it[CURRENT_ID_KEY] = chatId
        }
    }
}

val NIGHT_MODE_KEY = booleanPreferencesKey("nightModeOn")
val Context.appSettingsStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

val AI_HANDLER_LIST = stringPreferencesKey("ai_handler_list")
val Context.aiHandlerList: DataStore<Preferences> by preferencesDataStore(name = "ai_handler_list")

val CURRENT_ID_KEY = intPreferencesKey("current_id")
val Context.currentId: DataStore<Preferences> by preferencesDataStore(name = "current_id")