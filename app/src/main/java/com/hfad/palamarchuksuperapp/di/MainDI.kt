import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.hfad.palamarchuksuperapp.DataStoreHandler
import com.hfad.palamarchuksuperapp.data.dao.MessageChatDao
import com.hfad.palamarchuksuperapp.data.dao.SkillsDao
import com.hfad.palamarchuksuperapp.data.dao.StoreDao
import com.hfad.palamarchuksuperapp.data.database.DATABASE_MAIN_ENTITY_PRODUCT
import com.hfad.palamarchuksuperapp.data.database.DATABASE_MESSAGE_CHAT
import com.hfad.palamarchuksuperapp.data.database.DATABASE_PROJECT_NAME
import com.hfad.palamarchuksuperapp.data.database.MessageChatDatabase
import com.hfad.palamarchuksuperapp.data.database.SkillsDatabase
import com.hfad.palamarchuksuperapp.data.database.StoreDatabase
import com.hfad.palamarchuksuperapp.data.repository.AiHandlerRepository
import com.hfad.palamarchuksuperapp.data.repository.AiHandlerRepositoryImpl
import com.hfad.palamarchuksuperapp.data.repository.ChatAiRepositoryImpl
import com.hfad.palamarchuksuperapp.data.repository.FakeStoreApiRepository
import com.hfad.palamarchuksuperapp.data.repository.SkillsRepositoryImpl
import com.hfad.palamarchuksuperapp.data.repository.StoreRepositoryImpl
import com.hfad.palamarchuksuperapp.data.services.FakeStoreApi
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.domain.usecases.AddAiHandlerUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.AddAiHandlerUseCaseImpl
import com.hfad.palamarchuksuperapp.domain.usecases.AddMessageGroupUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.AddMessageGroupUseCaseImpl
import com.hfad.palamarchuksuperapp.domain.usecases.ChooseMessageAiUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.ChooseMessageAiUseCaseImpl
import com.hfad.palamarchuksuperapp.domain.usecases.DeleteAiHandlerUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.DeleteAiHandlerUseCaseImpl
import com.hfad.palamarchuksuperapp.domain.usecases.GetAiChatUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.GetAiChatUseCaseImpl
import com.hfad.palamarchuksuperapp.domain.usecases.GetModelsUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.GetModelsUseCaseImpl
import com.hfad.palamarchuksuperapp.domain.usecases.MapAiModelHandlerUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.MapAiModelHandlerUseCaseImpl
import com.hfad.palamarchuksuperapp.domain.usecases.ObserveAiHandlersUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.ObserveAiHandlersUseCaseImpl
import com.hfad.palamarchuksuperapp.domain.usecases.ObserveAllChatsInfoUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.ObserveAllChatsInfoUseCaseImpl
import com.hfad.palamarchuksuperapp.domain.usecases.ObserveChatAiUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.ObserveChatAiUseCaseImpl
import com.hfad.palamarchuksuperapp.domain.usecases.SendAiRequestUseCaseImpl
import com.hfad.palamarchuksuperapp.domain.usecases.SendChatRequestUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.UpdateAiHandlerUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.UpdateAiHandlerUseCaseImpl
import com.hfad.palamarchuksuperapp.domain.usecases.UpdateMessageStatusUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.UpdateMessageStatusUseCaseImpl
import com.hfad.palamarchuksuperapp.ui.screens.MainActivity
import com.hfad.palamarchuksuperapp.ui.screens.MainScreenFragment
import com.hfad.palamarchuksuperapp.ui.screens.SkillsFragment
import com.hfad.palamarchuksuperapp.ui.screens.StoreFragment
import com.hfad.palamarchuksuperapp.ui.viewModels.ChatBotViewModel
import com.hfad.palamarchuksuperapp.ui.viewModels.GenericViewModelFactory
import com.hfad.palamarchuksuperapp.ui.viewModels.SkillsViewModel
import com.hfad.palamarchuksuperapp.ui.viewModels.StoreViewModel
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(fragmentActivity: FragmentActivity)
    fun inject(fragmentActivity: SkillsFragment)
    fun inject(mainScreenFragment: MainScreenFragment)
    fun appVibrator(): AppVibrator
    fun viewModelFactory(): ViewModelProvider.Factory
    fun inject(storeFragment: StoreFragment)
    fun getHttpClient(): HttpClient
    fun getAiHandlerDispatcher(): AiHandlerRepositoryImpl
    fun provideDataStoreHandler(): DataStoreHandler

    @Component.Builder
    interface Builder {
        fun getContext(@BindsInstance context: Context): Builder
        fun build(): AppComponent
    }
}


@Module(
    includes = [DatabaseModule::class, ViewModelsModule::class, NetworkModule::class,
        AiModelHandlerModule::class, UseCaseModule::class]
)
object AppModule {
    @IoDispatcher
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @DefaultDispatcher
    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @MainDispatcher
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}

@Module
abstract class AiModelHandlerModule {

    @Binds
    @Singleton
    abstract fun provideAiHandlerHandler(aiHandlerRepository: AiHandlerRepositoryImpl): AiHandlerRepository


}

@Module
interface UseCaseModule {

    @Singleton
    @Binds
    fun bindDeleteAiHandlerUseCase(deleteAiHandlerUseCaseImpl: DeleteAiHandlerUseCaseImpl): DeleteAiHandlerUseCase

    @Singleton
    @Binds
    fun bindAddAiHandlerUseCase(addAiHandlerUseCaseImpl: AddAiHandlerUseCaseImpl): AddAiHandlerUseCase

    @Singleton
    @Binds
    fun bindUpdateAiHandlerUseCase(updateAiHandlerUseCaseImpl: UpdateAiHandlerUseCaseImpl): UpdateAiHandlerUseCase

    @Singleton
    @Binds
    fun bindGetAiHandlersUseCase(getAiHandlersUseCaseImpl: ObserveAiHandlersUseCaseImpl): ObserveAiHandlersUseCase

    @Singleton
    @Binds
    fun bindAddAiMessageUseCase(addAiMessageUseCaseImpl: AddMessageGroupUseCaseImpl): AddMessageGroupUseCase

    @Singleton
    @Binds
    fun bindMapAiModelHandlerUseCase(mapAiModelHandlerUseCaseImpl: MapAiModelHandlerUseCaseImpl)
            : MapAiModelHandlerUseCase

//    @Singleton
//    @Binds
//    fun bindChangeAiMessageUseCase(changeAiMessageUseCase: UpdateAiMessageUseCaseImpl): UpdateAiMessageUseCase

    @Singleton
    @Binds
    fun bindGetGetAiChatUseCase(getAiChatUseCaseImpl: GetAiChatUseCaseImpl): GetAiChatUseCase

    @Singleton
    @Binds
    fun bindGetModelsUseCase(getModelsUseCaseImpl: GetModelsUseCaseImpl): GetModelsUseCase

    @Singleton
    @Binds
    fun bindSendChatRequestUseCase(sendAiRequestUseCaseImpl: SendAiRequestUseCaseImpl): SendChatRequestUseCase

    @Singleton
    @Binds
    fun bindChooseMessageAiUseCase(chooseMessageAiUseCaseImpl: ChooseMessageAiUseCaseImpl): ChooseMessageAiUseCase

    @Singleton
    @Binds
    fun bindGetChatAiUseCase(chatAiUseCaseImpl: ObserveChatAiUseCaseImpl): ObserveChatAiUseCase

    @Singleton
    @Binds
    fun bindUpdateMessageStatusUseCase(
        updateMessageStatusUseCaseImpl: UpdateMessageStatusUseCaseImpl,
    ): UpdateMessageStatusUseCase
}


@Module
abstract class ViewModelsModule {


    @Singleton
    @Binds
    abstract fun bindChatAiRepository(chatAiRepositoryImpl: ChatAiRepositoryImpl): ChatAiRepository

    @Binds
    abstract fun bindViewModelFactory(factory: GenericViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SkillsViewModel::class)
    abstract fun bindSkillsViewModel(viewModel: SkillsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChatBotViewModel::class)
    abstract fun bindMainScreenViewModel(viewModel: ChatBotViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StoreViewModel::class)
    abstract fun bindStoreViewModel(viewModel: StoreViewModel): ViewModel

    @Singleton
    @Binds
    abstract fun bindObserveAllChatsInfoUseCase (
        observeAllChatsInfoUseCaseImpl : ObserveAllChatsInfoUseCaseImpl
    ) : ObserveAllChatsInfoUseCase
}

@Module
object NetworkModule {
    @Provides
    fun platziApiImpl(): FakeStoreApi {
        return FakeStoreApiRepository(provideHttpClient())
    }

    @Provides
    fun provideStoreRepository(
        storeApi: FakeStoreApi,
        storeDao: StoreDao,
    ): StoreRepository {
        return StoreRepositoryImpl(storeApi, storeDao)
    }

    @Singleton
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

@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideMessageChatDB(context: Context): MessageChatDatabase {
        return Room.databaseBuilder(
            context = context.applicationContext,
            klass = MessageChatDatabase::class.java,
            name = DATABASE_MESSAGE_CHAT
        ).fallbackToDestructiveMigration() //TODO Don't forget to remove this in production
            .build()
    }

    @Provides
    fun provideMessageChatDao(messageChatDatabase: MessageChatDatabase): MessageChatDao {
        return messageChatDatabase.messageChatDao()
    }

    @Singleton
    @Provides
    fun provideStoreDB(context: Context): StoreDatabase {
        return Room.databaseBuilder(
            context = context.applicationContext,
            klass = StoreDatabase::class.java,
            name = DATABASE_MAIN_ENTITY_PRODUCT
        ).fallbackToDestructiveMigration() //TODO Don't forget to remove this in production
            .build()
    }

    @Provides
    fun provideStoreDao(storeDatabase: StoreDatabase): StoreDao {
        return storeDatabase.storeDao()
    }


    @Singleton
    @Provides
    fun provideSkillDB(context: Context): SkillsDatabase {
        return Room.databaseBuilder(
            context = context.applicationContext,
            klass = SkillsDatabase::class.java,
            name = DATABASE_PROJECT_NAME
        )
            .fallbackToDestructiveMigration()
            .createFromAsset("newDatabase.db")
            .build()
    }

    @Provides
    fun provideSkillsDao(skillsDatabase: SkillsDatabase): SkillsDao {
        return skillsDatabase.skillsDao()
    }

    @Provides
    fun provideSkillRepository(skillsDao: SkillsDao): SkillRepository {
        return SkillsRepositoryImpl(skillsDao = skillsDao)
    }

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)