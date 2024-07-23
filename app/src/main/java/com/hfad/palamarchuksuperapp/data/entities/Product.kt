package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.hfad.palamarchuksuperapp.data.database.DATABASE_STORE_NAME
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = DATABASE_STORE_NAME)
@JsonClass(generateAdapter = true)
@TypeConverters(ProductConverters::class)
data class Product(
    @PrimaryKey
    @Json(name="id")
    val id: Int = 0,
    @Json(name="title")
    val title: String = "",
    @Json(name="price")
    val price: Double = 0.0,
    @Json(name="description")
    val description: String = "",
    @Json(name="category")
    val category: String = "",
    @Json(name="image")
    val image: String = "",
    @Json(name = "rating")
    val rating: ProductRating = ProductRating()
)

class ProductConverters {
    @TypeConverter
    fun fromProductRating(rating: ProductRating): String {
        return "${rating.rate},${rating.count}"
    }
    @TypeConverter
    fun toProductRating (rating: String): ProductRating {
        val parts = rating.split(",")
        return ProductRating(parts[0].toDouble(), parts[1].toInt())
    }
}

@JsonClass (generateAdapter = true)
data class ProductRating (
    @Json(name="rate")
    val rate: Double = 0.0,
    @Json(name="count")
    val count: Int = 0
)