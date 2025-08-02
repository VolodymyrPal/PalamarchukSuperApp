package com.hfad.palamarchuksuperapp.feature.bone.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncPreferences @Inject constructor(
    private val context: Context
) {
    private val Context.dataStore by preferencesDataStore(name = "order_prefs")

    private val LAST_SYNC_PREFIX = "last_sync"

    suspend fun getLastSyncTime(status: OrderStatus?): Long? {
        val key = longPreferencesKey("$LAST_SYNC_PREFIX${status?.name ?: "ALL"}")
        val prefs = context.dataStore.data.first()
        return prefs[key]
    }

    suspend fun setLastSyncTime(status: OrderStatus?, timeMillis: Long) {
        val key = longPreferencesKey("$LAST_SYNC_PREFIX${status?.name ?: "ALL"}")
        context.dataStore.edit { prefs ->
            prefs[key] = timeMillis
        }
    }
}