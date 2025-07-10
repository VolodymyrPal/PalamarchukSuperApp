package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.BoneDao
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class FinanceRepositoryImpl @Inject constructor(
    private val boneDao: BoneDao,
) : FinanceRepository {

    override fun operationsFromTo(
        from: Date,
        to: Date,
    ): AppResult<Flow<List<TypedTransaction>>, AppError> {
        return trySqlApp {
            boneDao.operationsFromTo(from, to)
        }
    }

    override val statistic: AppResult<Flow<FinanceStatistic>, AppError> = trySqlApp {
        boneDao.financeStatistic
    }
} 