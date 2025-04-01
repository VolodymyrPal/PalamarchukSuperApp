package com.hfad.palamarchuksuperapp.feature.bone.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.GenericViewModelFactory
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.BoneFeatureViewModel
import dagger.Binds
import dagger.Component
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Scope
import kotlin.properties.Delegates.notNull
import kotlin.reflect.KClass

@FeatureScope
@Component (dependencies = [BoneDeps::class], modules = [ViewModelsModule::class, BoneModule::class])
internal interface BoneComponent : BoneDeps  {

    val viewModelFactory : ViewModelProvider.Factory

    @Component.Builder
    interface Builder {
        fun deps(boneDeps: BoneDeps) : Builder
        fun build(): BoneComponent
    }
}
@Module
internal abstract class BoneModule {
    @Binds
    abstract fun bindViewModelFactory(factory: GenericViewModelFactory): ViewModelProvider.Factory
}

@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(BoneFeatureViewModel::class)
    abstract fun bindSkillsViewModel(viewModel: BoneFeatureViewModel): ViewModel
}


interface BoneDeps {}


@Retention(AnnotationRetention.RUNTIME)
@Scope
annotation class FeatureScope

@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)