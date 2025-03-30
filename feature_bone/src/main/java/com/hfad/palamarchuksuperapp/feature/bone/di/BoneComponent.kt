package com.hfad.palamarchuksuperapp.feature.bone.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.BoneFeatureViewModel
import dagger.Binds
import dagger.Component
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import io.ktor.client.HttpClient
import javax.inject.Scope
import kotlin.properties.Delegates.notNull
import kotlin.reflect.KClass

@FeatureScope
@Component (dependencies = [BoneDeps::class], modules = [ViewModelsModule::class])
internal interface BoneComponent  {

    val viewModelFactory : ViewModelProvider.Factory

    @Component.Builder
    interface Builder {
        fun deps(boneDeps: BoneDeps) : Builder
        fun build(): BoneComponent
    }
}

@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(BoneFeatureViewModel::class)
    abstract fun bindSkillsViewModel(viewModel: BoneFeatureViewModel): ViewModel
}


interface BoneDeps {
    val viewModelFactory: ViewModelProvider.Factory
}


interface BoneDepsProvider {
    var deps: BoneDeps
    companion object : BoneDepsProvider by BoneDepsStore
}

object BoneDepsStore : BoneDepsProvider {
    override var deps: BoneDeps by notNull()
}


@Retention(AnnotationRetention.RUNTIME)
@Scope
annotation class FeatureScope

@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)