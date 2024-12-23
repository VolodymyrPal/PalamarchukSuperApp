package com.hfad.palamarchuksuperapp.data.dtos

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "products")
data class ProductDTO(
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