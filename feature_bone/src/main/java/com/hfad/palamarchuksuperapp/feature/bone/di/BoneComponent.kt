package com.hfad.palamarchuksuperapp.feature.bone.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.GenericViewModelFactory
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.BoneFeatureViewModel
import dagger.Binds
import dagger.Component
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.delay
import javax.inject.Qualifier
import javax.inject.Scope
import kotlin.reflect.KClass

@FeatureScope
@Component(dependencies = [BoneDeps::class], modules = [ViewModelsModule::class, BoneModule::class])
internal interface BoneComponent : BoneDeps {

    val viewModelFactory: ViewModelProvider.Factory
    override val context: Context
    override fun getFeatureHttpClient(): HttpClient

    @FeatureClient
    val httpClient: HttpClient

    @Component.Builder
    interface Builder {
        fun deps(boneDeps: BoneDeps): Builder
        fun build(): BoneComponent
    }
}

@Module
internal abstract class BoneModule {
    @Binds
    abstract fun bindViewModelFactory(factory: GenericViewModelFactory): ViewModelProvider.Factory
}

@Module
internal object DifferentClasses {

    @Provides
    @FeatureClient
    fun provideHttpClient(
        httpClient: HttpClient,
    ): HttpClient {
        return httpClient.also { client ->
            client.plugin(HttpSend).intercept { request ->

                val modifiedRequest = request.apply {
//                    headers.append("Authorization", "Bearer your_token")    // Add authorization header
//                    headers.append("Custom-Header", "SomeValue")            // Add custom header
                }

                val maxRetries = 3
                val initialDelayMillis = 500L
                var delayMillis = initialDelayMillis
                var lastCall: HttpClientCall? = null
                var lastException: Throwable? = null

                for (attempt in 1..maxRetries) {
                    try {
                        val call = execute(modifiedRequest)
                        lastCall = call

                        if (call.response.status.value !in 400..599) {
                            println("✅ Success at attempt $attempt: ${call.response.status}")
                            return@intercept call
                        } else {
                            println("⚠️ Server responded with ${call.response.status}")
                        }
                    } catch (e: Exception) {
                        lastException = e
                        println("❌ Exception at attempt $attempt: ${e.message}")
                    }

                    if (attempt < maxRetries) {
                        delay(delayMillis)
                        delayMillis *= 2
                    }
                }

                lastCall?.let { return@intercept it }
                throw lastException ?: IllegalStateException("All retries failed")
            }
        }
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
    val context: Context
    fun getFeatureHttpClient(): HttpClient
}


@Retention(AnnotationRetention.RUNTIME)
@Scope
annotation class FeatureScope

@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class FeatureClient