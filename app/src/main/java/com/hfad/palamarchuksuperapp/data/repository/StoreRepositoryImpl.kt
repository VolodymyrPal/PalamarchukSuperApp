package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.dao.StoreDao
import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.data.services.FakeStoreApi
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject

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
            storeProducts
        } catch (e: Exception) {
            errorFlow.update { e }
            fetchProductsAsFlowFromDB.first()
        }
    }

    override suspend fun upsertAll(products: List<ProductDomainRW>) {
        storeDao.insertOrIgnoreProducts(products)
    }

    override suspend fun updateProduct(product: Product) {
        storeDao.updateCompleted(product.id.toString(), 5) //TODO
    }

    override val errorFlow: MutableStateFlow<Exception?> = MutableStateFlow(null)

}