package com.hfad.palamarchuksuperapp.ui.common

import androidx.compose.runtime.Immutable
import com.hfad.palamarchuksuperapp.data.entities.Product

@Immutable
data class ProductDomainRW (
    val product: Product,
    val quantity: Int = 0,
    val liked: Boolean = false,
)

object ProductToProductDomainRW : Mapper<Product, ProductDomainRW> {
    override fun map(from: Product) = ProductDomainRW (
        product = from,
    )
}

fun Product.toProductDomainRW() = ProductDomainRW(
    product = this,
)