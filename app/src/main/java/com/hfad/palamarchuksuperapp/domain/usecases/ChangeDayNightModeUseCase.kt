package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.first

class ChangeDayNightModeUseCase {
    suspend operator fun invoke() {
        val preferencesRepository = PreferencesRepository.get()
        preferencesRepository.setStoredNightMode(!preferencesRepository.storedQuery.first())
    }
}