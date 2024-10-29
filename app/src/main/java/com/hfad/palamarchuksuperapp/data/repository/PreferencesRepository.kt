package com.hfad.palamarchuksuperapp.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.hfad.palamarchuksuperapp.appSettingsStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PreferencesRepository private constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val NIGHT_MODE_KEY = booleanPreferencesKey("nightModeOn")

        private var INSTANCE: PreferencesRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                val dataStore : DataStore<Preferences> = context.appSettingsStore
                INSTANCE = PreferencesRepository(dataStore)
            }
        }

        fun get(): PreferencesRepository {
            return INSTANCE
                ?: throw IllegalStateException("Preferences Repository must be initialized")
        }
    }

    val storedQuery: Flow<Boolean> = dataStore.data.map {
        it[NIGHT_MODE_KEY] ?: true
    }.distinctUntilChanged()

    suspend fun setStoredNightMode(userNightMode: Boolean) {
        dataStore.edit {
            it[NIGHT_MODE_KEY] = userNightMode
        }
        delay(200)
        setNightMode(userNightMode)
    }


    fun setNightMode(userNightMode: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (userNightMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }


}