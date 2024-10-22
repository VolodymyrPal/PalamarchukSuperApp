package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: Int = 0,
    val title: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val category: String = "",
    val image: String = "",
    val rating: ProductRating = ProductRating()
)

@Serializable
data class ProductRating (
    val rate: Double = 0.0,
    val count: Int = 0
)