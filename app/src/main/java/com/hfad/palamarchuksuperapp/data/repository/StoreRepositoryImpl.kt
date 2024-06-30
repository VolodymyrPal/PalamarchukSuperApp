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
            id = 8777, title = "vivendo", price = 1234, category = ProductCategory(
                id = 2222,
                name = "Tigers",
                image = "quo"
            ), images = ProductImages(urls = listOf())
        )

        val three = Product(
            id = 8772, title = "vivendo", price = 110, category = ProductCategory(
                id = 3333,
                name = "Tigers",
                image = "quo"
            ), images = ProductImages(urls = listOf())
        )

        val four = Product(
            id = 9727, title = "viris", price = 5347, category = ProductCategory(
                id = 6016,
                name = "Jay Berger",
                image = "leo"
            ), images = ProductImages(urls = listOf())

        )

        val five = Product(
            id = 3490, title = "meliore", price = 4427, category = ProductCategory(
                id = 8213,
                name = "Truman Welch",
                image = "scelerisque"
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