package com.hfad.palamarchuksuperapp.data.repository

import android.util.Log
import com.hfad.palamarchuksuperapp.data.dao.StoreDao
import com.hfad.palamarchuksuperapp.data.services.FakeStoreApi
import com.hfad.palamarchuksuperapp.domain.models.DataError
import com.hfad.palamarchuksuperapp.domain.models.Error
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import retrofit2.HttpException
import java.lang.RuntimeException
import kotlin.random.Random

class StoreRepositoryImpl @Inject constructor(
    private val storeApi: FakeStoreApi,
    private val storeDao: StoreDao,
) : StoreRepository {

    override val fetchProductsAsFlowFromDB: Flow<List<ProductDomainRW>> =
        storeDao.getAllProductsFromDB()

    suspend fun products(): Result<Flow<List<ProductDomainRW>>, Error> {
        return try {
            Result.Success(fetchProductsAsFlowFromDB)
        } catch (e: HttpException) {
            Result.Error(DataError.Network.InternalServerError)
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

    private suspend fun getProductWithErrors(): Result<List<ProductDomainRW>, Error> { //}: List<ProductDomainRW> {

        return try {
            val storeProducts: List<ProductDomainRW> = storeApi.getProductsDomainRw()
            Log.d("TAG", "getProductWithErrors: $storeProducts")
            Result.Success(storeProducts)
        } catch (e: RuntimeException) {
            Result.Error(DataError.Local.DatabaseError)
        } catch (e: Exception) {
            Result.Error(DataError.Network.InternalServerError)
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

    override val errorFlow: MutableSharedFlow<DataError?> = MutableSharedFlow()

}