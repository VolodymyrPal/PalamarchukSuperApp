package com.hfad.palamarchuksuperapp.core.ui.genericViewModel

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.hfad.palamarchuksuperapp.appComponent
import javax.inject.Inject
import javax.inject.Provider

class GenericViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>,
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
inline fun <reified VM : ViewModel> daggerViewModel(
    factory: ViewModelProvider.Factory = LocalContext.current.appComponent.viewModelFactory(),
    owner: ViewModelStoreOwner = LocalViewModelStoreOwner.current ?: error(
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    ),
): VM {
    return ViewModelProvider(owner, factory)[VM::class.java]
}