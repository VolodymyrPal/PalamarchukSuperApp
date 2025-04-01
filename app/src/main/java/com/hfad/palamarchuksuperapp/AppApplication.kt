package com.hfad.palamarchuksuperapp

import AppComponent
import DaggerAppComponent
import android.app.Application
import android.content.Context
import com.hfad.palamarchuksuperapp.core.di.CoreComponent
import com.hfad.palamarchuksuperapp.core.di.DaggerCoreComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File

class AppApplication : Application()
//, ViewModelStoreOwner - implementing this to create view model for whole app
{
    lateinit var appComponent: AppComponent
    lateinit var coreComponent: CoreComponent
    private lateinit var dataStoreHandler: DataStoreHandler


    override fun onCreate() {
        super.onCreate()

        File(this.filesDir, "app_images").mkdir()  // Create app_images folder here for compose preview
        coreComponent = DaggerCoreComponent.builder().build()
        appComponent = DaggerAppComponent.builder().getContext(this).coreComponent(coreComponent).build()

        dataStoreHandler = appComponent.provideDataStoreHandler()

        runBlocking {
            dataStoreHandler.setNightMode(dataStoreHandler.storedQuery.first())
        }
    }
}

@Suppress("RecursivePropertyAccessor")
val Context.appComponent: AppComponent
    get() = when (this) {
        is AppApplication -> appComponent
        else -> this.applicationContext.appComponent
    }