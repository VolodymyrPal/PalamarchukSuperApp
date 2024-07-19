package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.data.entities.ProductRating
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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

    override fun fetchProductsTest(): Flow<List<Product>> {
        val one = Product(
            id = 5839, title = "adversarium", price = 2103, category = ProductCategory(
                id = 8971,
                name = "Troy Mays",
                image = "ligula"
            ), images = emptyList() //ProductImages(urls = listOf())
        )
        val two = Product(
            id = 8777, title = "vivendo", price = 1234, category = ProductCategory(
                id = 2222,
                name = "Tigers",
                image = "quo"
            ), images = emptyList() // ProductImages(urls = listOf())
        )

        val three = Product(
            id = 8772, title = "vivendo", price = 110, category = ProductCategory(
                id = 3333,
                name = "Tigers",
                image = "quo"
            ), images = emptyList()// ProductImages(urls = listOf())
        )

        val four = Product(
            id = 9727, title = "viris", price = 5347, category = ProductCategory(
                id = 6016,
                name = "Jay Berger",
                image = "leo"
            ), images = emptyList()//ProductImages(urls = listOf())

        )

        val five = Product(
            id = 3490, title = "meliore", price = 4427, category = ProductCategory(
                id = 8213,
                name = "Truman Welch",
                image = "scelerisque"
            ), images = emptyList()//ProductImages(urls = listOf())

        )
        val productList = MutableStateFlow(listOf(one, two, three, four, five))

        return productList
    }
}

class StoreRepositoryImplForPreview : StoreRepository {
    override fun fetchProducts(): Flow<List<Product>> {
        val one = Product(
            id = 5839, title = "adversarium", price = 2103, category = ProductCategory(
                id = 8971,
                name = "Troy Mays",
                image = "ligula"
            ), images = emptyList()//ProductImages(urls = listOf())
        )
        val two = Product(
            id = 8777, title = "vivendo", price = 1234, category = ProductCategory(
                id = 2222,
                name = "Tigers",
                image = "quo"
            ), images = emptyList()//ProductImages(urls = listOf())
        )

        val three = Product(
            id = 8772, title = "vivendo", price = 110, category = ProductCategory(
                id = 3333,
                name = "Tigers",
                image = "quo"
            ), images = emptyList()//ProductImages(urls = listOf())
        )

        val four = Product(
            id = 9727, title = "viris", price = 5347, category = ProductCategory(
                id = 6016,
                name = "Jay Berger",
                image = "leo"
            ), images = emptyList()//ProductImages(urls = listOf())

        )

        val five = Product(
            id = 3490, title = "meliore", price = 4427, category = ProductCategory(
                id = 8213,
                name = "Truman Welch",
                image = "scelerisque"
            ), images = emptyList()//ProductImages(urls = listOf())

        )
        val six = Product(
            id = 7820, title = "quem", price = 1640, category = ProductCategory(
                id = 1628,
                name = "Christi Glover",
                image = "taciti"
            ), images = emptyList()//ProductImages(urls = listOf())

        )
        val seven = Product(
            id = 5049, title = "petentium", price = 5247, category = ProductCategory(
                id = 5868,
                name = "Belinda Carroll",
                image = "sea"
            ), images = emptyList()//ProductImages(urls = listOf())

        )

        val eight = Product(
            id = 8710, title = "ne", price = 1622, category = ProductCategory(
                id = 5070,
                name = "Tomas Osborn",
                image = "scripta"
            ), images = emptyList()//ProductImages(urls = listOf())

        )

        val nine = Product(
            id = 5423, title = "pretium", price = 8339, category = ProductCategory(
                id = 4317,
                name = "Saundra Valdez",
                image = "magnis"
            ), images = emptyList()//ProductImages(urls = listOf())


        )

        val ten = Product(
            id = 7184, title = "definitiones", price = 7724, category = ProductCategory(
                id = 5407,
                name = "Michele Le",
                image = "senectus"
            ), images = emptyList()//ProductImages(urls = listOf())

        )

        val productList =
            MutableStateFlow(listOf(one, two, three, four, five, six, seven, eight, nine, ten))
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

    override fun fetchProductsTest(): Flow<List<Product>> {
        val one = Product(
            id = 5839, title = "adversarium", price = 2103, category = ProductCategory(
                id = 8971,
                name = "Troy Mays",
                image = "ligula"
            ), images = emptyList()//ProductImages(urls = listOf())
        )
        val two = Product(
            id = 8777, title = "vivendo", price = 1234, category = ProductCategory(
                id = 2222,
                name = "Tigers",
                image = "quo"
            ), images = emptyList()//ProductImages(urls = listOf())
        )

        val three = Product(
            id = 8772, title = "vivendo", price = 110, category = ProductCategory(
                id = 3333,
                name = "Tigers",
                image = "quo"
            ), images = emptyList()//ProductImages(urls = listOf())
        )

        val four = Product(
            id = 9727, title = "viris", price = 5347, category = ProductCategory(
                id = 6016,
                name = "Jay Berger",
                image = "leo"
            ), images = emptyList()//ProductImages(urls = listOf())

        )

        val five = Product(
            id = 3490, title = "meliore", price = 4427, category = ProductCategory(
                id = 8213,
                name = "Truman Welch",
                image = "scelerisque"
            ), images = emptyList()//ProductImages(urls = listOf())

        )
        val six = Product(
            id = 7820, title = "quem", price = 1640, category = ProductCategory(
                id = 1628,
                name = "Christi Glover",
                image = "taciti"
            ), images = emptyList()//ProductImages(urls = listOf())

        )
        val seven = Product(
            id = 5049, title = "petentium", price = 5247, category = ProductCategory(
                id = 5868,
                name = "Belinda Carroll",
                image = "sea"
            ), images = emptyList()//ProductImages(urls = listOf())

        )

        val eight = Product(
            id = 8710, title = "ne", price = 1622, category = ProductCategory(
                id = 5070,
                name = "Tomas Osborn",
                image = "scripta"
            ), images = emptyList()//ProductImages(urls = listOf())

        )

        val nine = Product(
            id = 5423, title = "pretium", price = 8339, category = ProductCategory(
                id = 4317,
                name = "Saundra Valdez",
                image = "magnis"
            ), images = emptyList()//ProductImages(urls = listOf())


        )

        val ten = Product(
            id = 7184, title = "definitiones", price = 7724, category = ProductCategory(
                id = 5407,
                name = "Michele Le",
                image = "senectus"
            ), images = emptyList()//ProductImages(urls = listOf())

        )
        val productList = MutableStateFlow(listOf(one, two, three, four, five, six, seven, eight, nine, ten))

        return productList
    }

}