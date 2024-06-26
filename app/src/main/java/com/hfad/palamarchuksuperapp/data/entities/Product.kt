package com.hfad.palamarchuksuperapp.data.entities


data class Product(
    val id: Int = 0,
    val title: String = "",
    val price: Int = 0,
    val category: ProductCategory = ProductCategory() ,
    val images: ProductImages = ProductImages(),
)

data class ProductCategory (
    val id: Int = 0,
    val name: String = "",
    val image: String = "",
)

data class ProductImages (
    val urls: List<String> = emptyList()
)