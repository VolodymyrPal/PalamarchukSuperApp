package com.hfad.palamarchuksuperapp

import AppComponent
import android.app.Application
import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AppApplication: Application() {

    lateinit var appComponent: AppComponent


    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.initialize(this)
        runBlocking {
            PreferencesRepository.get().setNightMode(PreferencesRepository.get().storedQuery.first()) }
        appComponent = DaggerAppComponent.create()
    }
}

@Suppress("RecursivePropertyAccessor")
val Context.appComponent: AppComponent
    get() = when (this) {
        is AppApplication -> appComponent
        else -> this.applicationContext.appComponent
    }