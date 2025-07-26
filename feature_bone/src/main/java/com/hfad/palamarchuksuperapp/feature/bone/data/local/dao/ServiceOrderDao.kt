package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_SERVICE_ORDERS
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.ServiceOrderEntity

@Dao
interface ServiceOrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<ServiceOrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: ServiceOrderEntity)

    @Query("SELECT * FROM $DATABASE_SERVICE_ORDERS WHERE orderId = :orderId")
    suspend fun getByOrderId(orderId: Int): List<ServiceOrderEntity>

    @Delete
    suspend fun delete(service: ServiceOrderEntity)

    @Query("DELETE FROM $DATABASE_SERVICE_ORDERS WHERE orderId = :orderId")
    suspend fun deleteByOrderId(orderId: Int)
}