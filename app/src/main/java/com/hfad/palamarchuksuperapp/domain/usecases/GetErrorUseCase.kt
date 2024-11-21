package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

interface GetErrorUseCase {
    operator fun invoke(): MutableSharedFlow<AppError?>
}

class GetErrorUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
    ) : GetErrorUseCase {
    override fun invoke(): MutableSharedFlow<AppError?> {
        return chatAiRepository.errorFlow
    }
}