package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface FinanceOperationDao {
    fun operationsInRange(from: Date, to: Date) : Flow<List<TypedTransaction>>
    val operations : Flow<List<TypedTransaction>>
    val financeStatistics : Flow<FinanceStatistics>
}