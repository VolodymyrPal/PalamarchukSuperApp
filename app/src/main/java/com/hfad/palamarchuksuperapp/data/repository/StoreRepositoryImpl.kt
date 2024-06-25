package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.data.entities.ProductCategory
import com.hfad.palamarchuksuperapp.data.entities.ProductImages
import com.hfad.palamarchuksuperapp.data.entities.Skill
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import java.util.UUID

class StoreRepositoryImpl : StoreRepository {
    override fun fetchProducts(): Flow<List<Product>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(product: Product) {
        TODO("Not yet implemented")
    }

    override suspend fun addProduct(product: Product) {
        TODO("Not yet implemented")
    }

    override suspend fun updateProduct(product: Product) {
        TODO("Not yet implemented")
    }

}

class StoreRepositoryImplForPreview : StoreRepository {
    override fun fetchProducts(): Flow<List<Product>> {
        val one = Product(
            id = 5839, title = "adversarium", price = 2103, category = ProductCategory(
                id = 8971,
                name = "Troy Mays",
                image = "ligula"
            ), images = ProductImages(urls = listOf())
        )
        val two = Product(
            id = 8777, title = "vivendo", price = 3570, category = ProductCategory(
                id = 5173,
                name = "Dave Franks",
                image = "quo"
            ), images = ProductImages(urls = listOf())
        )

        val three = Product(
            id = 8777, title = "vivendo", price = 3570, category = ProductCategory(
                id = 5173,
                name = "Dave Franks",
                image = "quo"
            ), images = ProductImages(urls = listOf())
        )

        val four = Product(
            id = 8777, title = "vivendo", price = 3570, category = ProductCategory(
                id = 5173,
                name = "Dave Franks",
                image = "quo"
            ), images = ProductImages(urls = listOf())
        )

        val five = Product(
            id = 8777, title = "vivendo", price = 3570, category = ProductCategory(
                id = 5173,
                name = "Dave Franks",
                image = "quo"
            ), images = ProductImages(urls = listOf())
        )
        val productList = MutableStateFlow(listOf(one, two, three, four, five))

        return productList
    }
    override suspend fun deleteProduct(product: Product) {
        TODO("Not yet implemented")
    }
    override suspend fun addProduct(product: Product) {
        TODO("Not yet implemented")
    }
    override suspend fun updateProduct(product: Product) {
        TODO("Not yet implemented")
    }

}