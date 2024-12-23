package com.hfad.palamarchuksuperapp.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.hfad.palamarchuksuperapp.data.database.DATABASE_MAIN_ENTITY_PRODUCT
import com.hfad.palamarchuksuperapp.data.dtos.ProductDTO
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = DATABASE_MAIN_ENTITY_PRODUCT)
@TypeConverters(ProductConverters::class)
data class Product (
    @PrimaryKey
    val id : Int = 0,
    val productDTO: ProductDTO,
    val quantity: Int = 0,
    val liked: Boolean = false,
)

class ProductConverters {

    @TypeConverter
    fun fromProduct(productDTO: ProductDTO): String {
        return Json.encodeToString(productDTO)
    }
    @TypeConverter
    fun toProduct (product: String): ProductDTO {
        return Json.decodeFromString<ProductDTO>(product)
    }
}