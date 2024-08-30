package com.hfad.palamarchuksuperapp.ui.common

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.hfad.palamarchuksuperapp.data.database.DATABASE_MAIN_ENTITY_PRODUCT
import com.hfad.palamarchuksuperapp.data.entities.Product
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@Entity(tableName = DATABASE_MAIN_ENTITY_PRODUCT)
@JsonClass(generateAdapter = true)
@TypeConverters(ProductDomainRWConverters::class)
data class ProductDomainRW (
    @PrimaryKey @Json(name="id") val id : Int = 0,
    @Json(name="product") val product: Product,
    @Json(name = "quantity") val quantity: Int = 0,
    @Json(name = "liked") val liked: Boolean = false,
)

class ProductDomainRWConverters {

    @TypeConverter
    fun fromProduct(product: Product): String {
        return Moshi.Builder().build().adapter(Product::class.java).toJson(product)
    }
    @TypeConverter
    fun toProduct (productDomainRW: String): Product {
        return Moshi.Builder().build().adapter(Product::class.java).fromJson(productDomainRW)!!
    }
}

fun Product.toProductDomainRW() = ProductDomainRW(
    product = this,
    id = this.id
)