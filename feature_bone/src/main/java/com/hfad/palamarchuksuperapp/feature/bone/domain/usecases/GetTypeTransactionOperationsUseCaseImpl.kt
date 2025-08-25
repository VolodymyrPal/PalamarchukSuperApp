package com.hfad.palamarchuksuperapp.feature.bone.domain.usecases

import android.util.Log
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.TypedTransactionProvider
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject

interface GetTypeTransactionOperationsUseCase {
    suspend operator fun invoke(): Flow<AppResult<List<TypedTransaction>, AppError>>
}

class GetTypeTransactionOperationsUseCaseImpl @Inject constructor(
    private val repositories: Set<@JvmSuppressWildcards TypedTransactionProvider>,
) : GetTypeTransactionOperationsUseCase {
    override suspend operator fun invoke(): Flow<AppResult<List<TypedTransaction>, AppError>> {
        Log.d("GetTypeTransactionOperationsUseCaseImpl", "invoke")
        val repositories =
            repositories.map { it.getTypedTransactionInRange(0, Date().time + 20000) }
        Log.d("Repositories:", "${repositories}")
        return flow { }
    }
}

