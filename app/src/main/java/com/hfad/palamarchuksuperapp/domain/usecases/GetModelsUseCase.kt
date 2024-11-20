package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import javax.inject.Inject

interface GetModelsUseCase {
    suspend operator fun invoke(handler: AiModelHandler) : Result<List<AiModel>, AppError>
}

class GetModelsUseCaseImpl @Inject constructor(

) : GetModelsUseCase {
    override suspend operator fun invoke(handler: AiModelHandler) : Result<List<AiModel>, AppError> {
        return handler.getModels()
    }

}