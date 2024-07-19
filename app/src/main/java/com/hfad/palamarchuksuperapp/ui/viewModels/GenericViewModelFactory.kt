package com.hfad.palamarchuksuperapp.ui.viewModels

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import javax.inject.Inject
import javax.inject.Provider

class GenericViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?: creators.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("Unknown model class $modelClass")

        @Suppress("UNCHECKED_CAST")
        return creator.get() as T
    }
}

@Composable
inline fun <reified VM : ViewModel> daggerViewModel(factory: ViewModelProvider.Factory): VM {
    val owner = LocalViewModelStoreOwner.current ?: error(
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    )
    return ViewModelProvider(owner, factory)[VM::class.java]
}