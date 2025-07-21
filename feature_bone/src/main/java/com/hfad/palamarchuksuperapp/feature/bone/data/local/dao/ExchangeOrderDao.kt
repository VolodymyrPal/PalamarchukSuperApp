package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import androidx.room.Dao
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeOrderDao {
    val exchanges : Flow<List<ExchangeOrder>>
    suspend fun getExchangeById(id: Int): ExchangeOrder?
    suspend fun getAllExchangeOrders(): List<ExchangeOrder>
    suspend fun insertOrIgnoreExchanges(exchanges : List<ExchangeOrder>)
    suspend fun deleteAllExchanges()
}