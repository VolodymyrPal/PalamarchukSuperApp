package com.hfad.palamarchuksuperapp.core.di

import android.content.Context
import dagger.Component
import dagger.Module

@Component(dependencies = [AppDependencies :: class], modules = [CoreModule :: class])
internal interface CoreComponent {

}

@Module
internal object CoreModule {

}

//Зависимости что будем получать с app модуля, основы приложения
interface AppDependencies {
    fun context() : Context
}