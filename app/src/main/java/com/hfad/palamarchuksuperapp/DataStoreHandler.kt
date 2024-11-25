package com.hfad.palamarchuksuperapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject

class DataStoreHandler @Inject constructor (val context: Context) {
    val appSettings = context.appSettingsStore
    val chatScreenInfo = context.chatScreenInfoStore
    val aiHandlerList = context.aiHandlerList
}

val Context.appSettingsStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
val Context.chatScreenInfoStore: DataStore<Preferences> by preferencesDataStore(name = "chat_screen_info")
val Context.aiHandlerList: DataStore<Preferences> by preferencesDataStore(name = "ai_handler_list")