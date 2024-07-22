package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.data.entities.ProductRating
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor() : StoreRepository {
    override fun fetchProducts(): Flow<List<Product>> {
        val one = Product(
            id = 2589,
            title = "nonumy",
            price = 5638.0,
            description = "suavitate",
            category = "nascetur",
            image = "pellentesque",
            rating = ProductRating(
                rate = 22.23,
                count = 1453
            )

        )
        val two = Product(
            id = 8328,
            title = "suas",
            price = 4893.0,
            description = "sodales",
            category = "adipiscing",
            image = "ponderum",
            rating = ProductRating(
                rate = 26.27,
                count = 9346
            )

        )

        val three = Product(
            id = 2588,
            title = "fuisset",
            price = 7040.0,
            description = "ipsum",
            category = "feugiat",
            image = "qualisque",
            rating = ProductRating(
                rate = 30.31,
                count = 6197
            )

        )

        val four = Product(
            id = 2497,
            title = "laoreet",
            price = 2419.0,
            description = "sumo",
            category = "rutrum",
            image = "pulvinar",
            rating = ProductRating(
                rate = 34.35,
                count = 9073
            )


        )

        val five = Product(
            id = 2534,
            title = "consectetur",
            price = 4170.0,
            description = "ad",
            category = "eam",
            image = "alterum",
            rating = ProductRating(
                rate = 38.39,
                count = 6416
            )


        )
        val six = Product(
            id = 1610,
            title = "aenean",
            price = 3957.0,
            description = "definitionem",
            category = "conclusionemque",
            image = "nisi",
            rating = ProductRating(
                rate = 58.59,
                count = 5393
            )


        )
        val seven = Product(
            id = 3645,
            title = "ancillae",
            price = 3688.0,
            description = "consetetur",
            category = "sadipscing",
            image = "graeco",
            rating = ProductRating(
                rate = 54.55,
                count = 1279
            )


        )

        val eight = Product(
            id = 3617,
            title = "suspendisse",
            price = 6301.0,
            description = "quidam",
            category = "wisi",
            image = "sale",
            rating = ProductRating(
                rate = 50.51,
                count = 4509
            )

        )

        val nine = Product(
            id = 3419,
            title = "gravida",
            price = 2200.0,
            description = "tempor",
            category = "prodesset",
            image = "movet",
            rating = ProductRating(
                rate = 46.47,
                count = 1367
            )

        )

        val ten = Product(
            id = 1937,
            title = "ferri",
            price = 7683.0,
            description = "et",
            category = "nulla",
            image = "possim",
            rating = ProductRating(
                rate = 42.43,
                count = 4660
            )

        )

        val productList =
            MutableStateFlow(listOf(one, two, three, four, five, six, seven, eight, nine, ten))
        return productList
    }

    override suspend fun refreshProducts() {
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
            id = 7841,
            title = "mei",
            price = 6628.0,
            description = "fabellas",
            category = "diam",
            image = "definitiones",
            rating = ProductRating(
                rate = 98.99,
                count = 5671
            )

        )
        val two = Product(
            id = 1251,
            title = "sapientem",
            price = 7910.0,
            description = "felis",
            category = "eget",
            image = "at",
            rating = ProductRating(
                rate = 94.95,
                count = 8308
            )

        )

        val three = Product(
            id = 6493,
            title = "fermentum",
            price = 3466.0,
            description = "sumo",
            category = "duo",
            image = "libero",
            rating = ProductRating(
                rate = 90.91,
                count = 6085
            )
        )

        val four = Product(
            id = 4134,
            title = "ridiculus",
            price = 4810.0,
            description = "posse",
            category = "quaeque",
            image = "accumsan",
            rating = ProductRating(
                rate = 86.87,
                count = 7737
            )

        )

        val five = Product(
            id = 6008,
            title = "vel",
            price = 4074.0,
            description = "vulputate",
            category = "docendi",
            image = "nihil",
            rating = ProductRating(
                rate = 82.83,
                count = 7827
            )

        )
        val six = Product(
            id = 5807,
            title = "instructior",
            price = 3108.0,
            description = "inceptos",
            category = "referrentur",
            image = "postea",
            rating = ProductRating(
                rate = 78.79,
                count = 9612
            )

        )
        val seven = Product(
            id = 4485,
            title = "luctus",
            price = 7969.0,
            description = "nam",
            category = "signiferumque",
            image = "arcu",
            rating = ProductRating(
                rate = 74.75,
                count = 6535
            )

        )

        val eight = Product(
            id = 5765,
            title = "perpetua",
            price = 3170.0,
            description = "consul",
            category = "odio",
            image = "mediocritatem",
            rating = ProductRating(
                rate = 70.71,
                count = 2539
            )

        )

        val nine = Product(
            id = 3736,
            title = "auctor",
            price = 5425.0,
            description = "consul",
            category = "litora",
            image = "tritani",
            rating = ProductRating(
                rate = 66.67,
                count = 8740
            )

        )

        val ten = Product(
            id = 5292,
            title = "viris",
            price = 1602.0,
            description = "aperiri",
            category = "partiendo",
            image = "legimus",
            rating = ProductRating(
                rate = 62.63,
                count = 8296
            )

        )
        val productList = MutableStateFlow(listOf(one, two, three, four, five, six, seven, eight, nine, ten))

        return productList
    }

}