package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.dao.StoreDao
import com.hfad.palamarchuksuperapp.data.services.FakeStoreApi
import com.hfad.palamarchuksuperapp.domain.models.DataError
import com.hfad.palamarchuksuperapp.domain.models.Error
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.hfad.palamarchuksuperapp.domain.models.Result
import retrofit2.HttpException

class StoreRepositoryImpl @Inject constructor(
    private val storeApi: FakeStoreApi,
    private val storeDao: StoreDao,
) : StoreRepository {

    override val fetchProductsAsFlowFromDB: Flow<List<ProductDomainRW>> =
        storeDao.getAllProductsFromDB().catch { errorFlow.update { it } }

    suspend fun products(): Result<Flow<List<ProductDomainRW>>, Error> {
        return try {
            Result.Success(fetchProductsAsFlowFromDB)
        } catch (e: HttpException) {
            Result.Error(DataError.Network.InternalServerError)
        }
    }

    override suspend fun softRefreshProducts() {
        storeDao.insertOrIgnoreProducts(getProductWithErrors())
    }

    override suspend fun hardRefreshProducts() {
        storeDao.deleteAllProducts()
        storeDao.insertOrIgnoreProducts(getProductWithErrors())
    }

    private suspend fun getProductWithErrors() : Result<List<ProductDomainRW>, Error> { //}: List<ProductDomainRW> {

        return try {
                Result.Success(storeApi.getProductsDomainRw())
            } catch (e: Exception) {
                try {
                    Result.Error(DataError.Network.InternalServerError)
                } catch (e: Exception) {
                    Result.Error(DataError.Local.DatabaseError)
                }
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

    override suspend fun upsertAll(products: List<ProductDomainRW>) {
        storeDao.insertOrIgnoreProducts(products)
    }

    override suspend fun updateProduct(product: ProductDomainRW) {
        storeDao.updateProduct(product)
    }

    override val errorFlow: MutableStateFlow<Exception?> = MutableStateFlow(null)

}