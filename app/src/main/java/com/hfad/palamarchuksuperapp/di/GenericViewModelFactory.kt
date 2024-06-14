package com.hfad.palamarchuksuperapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class GenericViewModelFactory<T1 : ViewModel> @Inject constructor(
    private val provider: Provider<T1>,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(provider.get()::class.java)) {
            return provider.get() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}