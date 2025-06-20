package com.hfad.palamarchuksuperapp.feature.bone.domain.usecases

import com.hfad.palamarchuksuperapp.feature.bone.data.repository.LogStatus
import kotlinx.coroutines.flow.Flow

interface ObserveLoginStatusUseCase {
    operator fun invoke(): Flow<LogStatus>
}