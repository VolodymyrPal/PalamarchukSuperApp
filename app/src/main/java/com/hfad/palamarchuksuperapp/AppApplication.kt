package com.hfad.palamarchuksuperapp

import android.app.Application
import android.content.Context
import com.hfad.palamarchuksuperapp.di.AppComponent
import com.hfad.palamarchuksuperapp.di.AppComponent2
import com.hfad.palamarchuksuperapp.di.DaggerAppComponent
import com.hfad.palamarchuksuperapp.di.DaggerAppComponent2
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AppApplication: Application() {

    lateinit var appComponent: AppComponent
    lateinit var appComponent2: AppComponent2


    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.initialize(this)
        runBlocking {
            PreferencesRepository.get().setNightMode(PreferencesRepository.get().storedQuery.first()) }
        appComponent = DaggerAppComponent.create()
        appComponent2 = DaggerAppComponent2.create()
    }
}

@Suppress("RecursivePropertyAccessor")
val Context.appComponent: AppComponent
    get() = when (this) {
        is AppApplication -> appComponent
        else -> this.applicationContext.appComponent
    }

@Suppress("RecursivePropertyAccessor")
val Context.appComponent2: AppComponent2
    get() = when (this) {
        is AppApplication -> appComponent2
        else -> this.applicationContext.appComponent2
    }
