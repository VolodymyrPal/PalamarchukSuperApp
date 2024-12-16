package com.hfad.palamarchuksuperapp

import AppComponent
import android.app.Application
import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File

class AppApplication : Application() {

    lateinit var appComponent: AppComponent
    private lateinit var dataStoreHandler: DataStoreHandler


    override fun onCreate() {
        super.onCreate()

        File(this.filesDir, "app_images").mkdir()  // Create app_images folder here for compose preview
        appComponent = DaggerAppComponent.builder().getContext(this).build()

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