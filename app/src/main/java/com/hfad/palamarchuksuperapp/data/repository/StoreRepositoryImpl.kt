package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.data.dao.StoreDao
import com.hfad.palamarchuksuperapp.data.services.FakeStoreApi
import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.Product
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val storeApi: FakeStoreApi,
    private val storeDao: StoreDao,
) : StoreRepository {

    override val fetchProductsAsFlowFromDB: Flow<PersistentList<Product>> =
        storeDao.getAllProductsFromDB().map { list -> list.toPersistentList() }

    suspend fun products(): AppResult<Flow<PersistentList<Product>>, AppError> {
        return withSqlErrorHandling { fetchProductsAsFlowFromDB }
    }

    override suspend fun softRefreshProducts() {
        val productResultApi = getProductWithErrors()
        if (productResultApi is AppResult.Success) {
            storeDao.insertOrIgnoreProducts(productResultApi.data)
        }
    }

    override suspend fun hardRefreshProducts() {
        storeDao.deleteAllProducts()
        val productResultApi = getProductWithErrors()
        if (productResultApi is AppResult.Success) {
            storeDao.insertOrIgnoreProducts(productResultApi.data)
        }
    }

    private suspend fun getProductWithErrors(): AppResult<PersistentList<Product>, AppError> { //}: List<ProductDomainRW> {

        return safeApiCall {
            val storeProducts: PersistentList<Product> =
                storeApi.getProductsDomainRw().toPersistentList()
            AppResult.Success(storeProducts)
        }
    }

    override suspend fun upsertAll(products: PersistentList<Product>) {
        storeDao.insertOrIgnoreProducts(products)
    }

    override suspend fun updateProduct(product: Product) {
        storeDao.updateProduct(product)
    }

    override val errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)

}