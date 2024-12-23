package com.hfad.palamarchuksuperapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hfad.palamarchuksuperapp.data.database.DATABASE_MAIN_ENTITY_PRODUCT
import com.hfad.palamarchuksuperapp.domain.models.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {

    @Query("SELECT * FROM $DATABASE_MAIN_ENTITY_PRODUCT")
    fun getAllProductsFromDB(): Flow<List<Product>>

    @Query(value = "DELETE FROM $DATABASE_MAIN_ENTITY_PRODUCT")
    suspend fun deleteAllProducts()

    @Query("UPDATE $DATABASE_MAIN_ENTITY_PRODUCT SET quantity = :query WHERE id = :taskId")
    suspend fun updateCompleted(taskId: String, query: Int)

    @Update
    suspend fun updateProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreProducts(products: List<Product>): List<Long>
}