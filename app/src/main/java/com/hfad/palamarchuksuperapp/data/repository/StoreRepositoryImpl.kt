package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.dao.StoreDao
import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.data.services.FakeStoreApi
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val storeApi: FakeStoreApi,
    private val storeDao: StoreDao,
) : StoreRepository {

    override val fetchProductsAsFlowFromDB: Flow<List<ProductDomainRW>> =
        storeDao.getAllProductsFromDB()


    override suspend fun refreshProducts() {
        storeDao.deleteAllProducts()
        val storeProducts: List<ProductDomainRW> = storeApi.getProductsDomainRw()
        storeDao.deleteAndInsertRefresh(storeProducts)
    }

    override suspend fun upsertAll(products: List<ProductDomainRW>) {
        storeDao.insertOrIgnoreProducts(products)
    }

    override suspend fun updateProduct(product: Product) {
        storeDao.updateCompleted(product.id.toString(), 5) //TODO
    }

}