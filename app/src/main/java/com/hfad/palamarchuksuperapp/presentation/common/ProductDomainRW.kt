package com.hfad.palamarchuksuperapp.presentation.common

import com.hfad.palamarchuksuperapp.data.entities.Product

class ProductDomainRW (
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