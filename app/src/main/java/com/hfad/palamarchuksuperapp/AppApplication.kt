package com.hfad.palamarchuksuperapp

import AppComponent
import android.app.Application
import android.content.Context
import com.hfad.palamarchuksuperapp.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File

class AppApplication: Application() {

    lateinit var appComponent: AppComponent


    override fun onCreate() {
        super.onCreate()

        File(this.filesDir, "app_images").mkdir()  // Create app_images folder here to fix compose preview

        PreferencesRepository.initialize(this)
        runBlocking {
            PreferencesRepository.get().setNightMode(PreferencesRepository.get().storedQuery.first()) }
        appComponent = DaggerAppComponent.builder().getContext(this).build()
    }
}

@Suppress("RecursivePropertyAccessor")
val Context.appComponent: AppComponent
    get() = when (this) {
        is AppApplication -> appComponent
        else -> this.applicationContext.appComponent
    }