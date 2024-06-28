package com.hfad.palamarchuksuperapp.presentation.viewModels

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
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

@Composable
inline fun <reified VM : ViewModel> daggerViewModel(factory: ViewModelProvider.Factory): VM {
    val owner = LocalViewModelStoreOwner.current ?: error(
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    )
    return ViewModelProvider(owner, factory)[VM::class.java]
}