package com.hfad.palamarchuksuperapp.core.di

import dagger.Component
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@Component(modules = [CoreModule::class])
interface CoreComponent {

    fun provideHttpClient(): HttpClient

    @Component.Builder
    interface Builder {
        fun build(): CoreComponent
    }
}

@Module
internal object CoreModule {

    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
            engine {
                endpoint {
                    socketTimeout =
                        60_000       // Максимальное время ожидания соединения 30 секунд
                    connectTimeout = 60_000        // Время ожидания соединения 30 секунд
                    requestTimeout = 60_000       // Максимальное время ожидания запроса 30 секунд
                    keepAliveTime =
                        60_0000        // Время жизни соединения после использования 300 секунд
                    maxConnectionsPerRoute = 10 // Максимум 10 соединений на маршрут
                    pipelineMaxSize = 10        // Максимум 10 запросов в пайплайне
                }
                maxConnectionsCount = 10 // Максимум 10 соединений
                https {
                    trustManager // Настройки проверки сертификата, что бы не перехватывать запросы посредине
                } // Настройки HTTPS, которые
                // позволяют конфигурировать параметры TLS/SSL, используемые для защищенных соединений.
                pipelining = false // Отключение пайпелинга
                proxy // Настройки прокси
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                    prettyPrint = true
                    isLenient = true  //TODO lenient for testing
                })
            }
            install(HttpCache) // Добавляем кэширование запросов, чтобы не обращаться к API каждый раз
        }
    }
}