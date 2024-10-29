package com.hfad.palamarchuksuperapp.data.repository

import coil.network.HttpException
import com.hfad.palamarchuksuperapp.data.dao.StoreDao
import com.hfad.palamarchuksuperapp.data.services.FakeStoreApi
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Error
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.lang.RuntimeException

class StoreRepositoryImpl @Inject constructor(
    private val storeApi: FakeStoreApi,
    private val storeDao: StoreDao,
) : StoreRepository {

    override val fetchProductsAsFlowFromDB: Flow<PersistentList<ProductDomainRW>> =
        storeDao.getAllProductsFromDB().map { list -> list.toPersistentList() }

    suspend fun products(): Result<Flow<PersistentList<ProductDomainRW>>, Error> {
        return try {
            Result.Success(fetchProductsAsFlowFromDB)
        } catch (e: HttpException) {
            Result.Error(AppError.Network.ServerError.InternalServerError)
        }
    }

    override suspend fun softRefreshProducts() {
        val productResultApi = getProductWithErrors()
        if (productResultApi is Result.Success) {
            storeDao.insertOrIgnoreProducts(productResultApi.data)
        }
    }

    override suspend fun hardRefreshProducts() {
        storeDao.deleteAllProducts()
        val productResultApi = getProductWithErrors()
        if (productResultApi is Result.Success) {
            storeDao.insertOrIgnoreProducts(productResultApi.data)
        }
    }

    private suspend fun getProductWithErrors(): Result<PersistentList<ProductDomainRW>, Error> { //}: List<ProductDomainRW> {

        return try {
            val storeProducts: PersistentList<ProductDomainRW> = storeApi.getProductsDomainRw().toPersistentList()

            Result.Success(storeProducts)
        } catch (e: RuntimeException) {
            Result.Error(AppError.LocalData.DatabaseError)
        } catch (e: Exception) {
            Result.Error(AppError.Network.ServerError.InternalServerError)
        }

//        return try {
//            val storeProducts: List<ProductDomainRW> = storeApi.getProductsDomainRw()
//            delay(1000)
//            if (Random.nextInt(0, 100) < 33) throw Exception("Error")
//            storeProducts
//        } catch (e: Exception) {
//            errorFlow.update { e }
//            fetchProductsAsFlowFromDB.first()
//        }
    }

    override suspend fun upsertAll(products: PersistentList<ProductDomainRW>) {
        storeDao.insertOrIgnoreProducts(products)
    }

    override suspend fun updateProduct(product: ProductDomainRW) {
        storeDao.updateProduct(product)
    }

    override val errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)

}