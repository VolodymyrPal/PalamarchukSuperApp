package com.hfad.palamarchuksuperapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hfad.palamarchuksuperapp.data.database.DATABASE_STORE_NAME
import com.hfad.palamarchuksuperapp.data.entities.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {

    @Query("SELECT * FROM $DATABASE_STORE_NAME")
    fun getAllProductsFromDB(): Flow<List<Product>>

    @Query(value = "DELETE FROM $DATABASE_STORE_NAME")
    suspend fun deleteProducts()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreProducts(products: List<Product>): List<Long>

    @Transaction
    suspend fun deleteAndInsert(genreId: String? = null, movies: List<Product>) {
        deleteProducts()
        insertOrIgnoreProducts(movies)
    }
}