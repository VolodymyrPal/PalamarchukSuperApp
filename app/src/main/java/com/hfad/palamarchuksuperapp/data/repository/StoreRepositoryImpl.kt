package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.dao.StoreDao
import com.hfad.palamarchuksuperapp.data.services.FakeStoreApi
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.random.Random

class StoreRepositoryImpl @Inject constructor(
    private val storeApi: FakeStoreApi,
    private val storeDao: StoreDao,
) : StoreRepository {

    override val fetchProductsAsFlowFromDB: Flow<List<ProductDomainRW>> get() {
        return storeDao.getAllProductsFromDB().catch { errorFlow.update { it } }
    }

    override suspend fun softRefreshProducts() {
        storeDao.insertOrIgnoreProducts(getProductWithErrors())
    }

    override suspend fun hardRefreshProducts() {
        storeDao.deleteAllProducts()
        storeDao.insertOrIgnoreProducts(getProductWithErrors())
    }

    private suspend fun getProductWithErrors(): List<ProductDomainRW> {
        return try {
            val storeProducts: List<ProductDomainRW> = storeApi.getProductsDomainRw()
            delay(1000)
            if (Random.nextInt(0, 100) < 33) throw Exception("Error")
            storeProducts
        } catch (e: Exception) {
            errorFlow.update { e }
            fetchProductsAsFlowFromDB.first()
        }
    }

    override suspend fun upsertAll(products: List<ProductDomainRW>) {
        storeDao.insertOrIgnoreProducts(products)
    }

    override suspend fun updateProduct(product: ProductDomainRW) {
        storeDao.updateProduct(product)
    }

    override val errorFlow: MutableStateFlow<Exception?> = MutableStateFlow(null)

}