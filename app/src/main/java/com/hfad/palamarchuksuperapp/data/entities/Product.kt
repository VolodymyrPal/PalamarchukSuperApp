package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "products")
@JsonClass(generateAdapter = true)
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

@JsonClass (generateAdapter = true)
data class ProductRating (
    @Json(name="rate")
    val rate: Double = 0.0,
    @Json(name="count")
    val count: Int = 0
)