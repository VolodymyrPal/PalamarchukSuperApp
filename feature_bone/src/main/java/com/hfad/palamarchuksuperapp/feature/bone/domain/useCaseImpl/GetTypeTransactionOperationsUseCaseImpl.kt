package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.TypedTransactionProvider
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.GetTypeTransactionOperationsUseCase
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class GetTypeTransactionOperationsUseCaseImpl @Inject constructor(
    private val repositories: Set<@JvmSuppressWildcards TypedTransactionProvider>,
) : GetTypeTransactionOperationsUseCase {
    override suspend operator fun invoke(): AppResult<List<TypedTransaction>, AppError> {
        val results: AppResult<List<TypedTransaction>, AppError> =
            coroutineScope {
                val successResultList = mutableListOf<TypedTransaction>()
                val errorList = mutableListOf<AppError>()
                val info =
                    repositories
                        .map { repo ->
                            async {
                                repo.getTypedTransactionInRange(0, Date().time + 20000)
                            }
                        }.awaitAll()

                info.forEach { result ->
                    if (result is AppResult.Success) {
                        result.data.forEach { successResultList.add(it) }
                    } else {
                        result as AppResult.Error
                        errorList.add(result.error)
                    }
                }

                if (errorList.isNotEmpty()) {
                    return@coroutineScope AppResult.Error(
                        error =
                            AppError.CustomError(
                                errorList.joinToString(),
                            ),
                    )
                }

                return@coroutineScope AppResult.Success(
                    successResultList,
                )
            }
        return results
    }
}
