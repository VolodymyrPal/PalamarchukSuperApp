package com.hfad.palamarchuksuperapp.ui.common

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.hfad.palamarchuksuperapp.data.database.DATABASE_MAIN_ENTITY_PRODUCT
import com.hfad.palamarchuksuperapp.data.entities.Product
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = DATABASE_MAIN_ENTITY_PRODUCT)
@TypeConverters(ProductDomainRWConverters::class)
data class ProductDomainRW (
    @PrimaryKey
    val id : Int = 0,
    val product: Product,
    val quantity: Int = 0,
    val liked: Boolean = false,
)
//
class ProductDomainRWConverters {

    @TypeConverter
    fun fromProduct(product: Product): String {
        return Json.encodeToString(product)
    }
    @TypeConverter
    fun toProduct (productDomainRW: String): Product {
        return Json.decodeFromString<Product>(productDomainRW)
    }
}

fun Product.toProductDomainRW() = ProductDomainRW(
    product = this,
    id = this.id
)