package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.BoneDatabase
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrdersRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.ClearAllDatabaseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClearAllDatabaseUseCaseImpl @Inject constructor(
    private val boneDatabase: BoneDatabase,
) : ClearAllDatabaseUseCase {
    override suspend fun invoke() {
        withContext(Dispatchers.IO) {
            boneDatabase.clearAllTables()
        }
    }
}