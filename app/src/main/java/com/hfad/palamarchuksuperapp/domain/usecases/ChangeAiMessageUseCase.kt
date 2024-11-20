package com.hfad.palamarchuksuperapp.domain.usecases

import javax.inject.Inject

interface ChangeAiMessageUseCase {
    suspend operator fun invoke()
}

class ChangeAiMessageUseCaseImpl @Inject constructor(

) : ChangeAiMessageUseCase {
    override suspend operator fun invoke() {

    }
}