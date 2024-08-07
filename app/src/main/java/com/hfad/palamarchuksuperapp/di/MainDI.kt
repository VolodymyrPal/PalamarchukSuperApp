import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.hfad.palamarchuksuperapp.data.dao.SkillsDao
import com.hfad.palamarchuksuperapp.data.database.DATABASE_PROJECT_NAME
import com.hfad.palamarchuksuperapp.data.database.SkillsDatabase
import com.hfad.palamarchuksuperapp.data.repository.ProductRepository
import com.hfad.palamarchuksuperapp.data.repository.SkillsRepositoryImpl
import com.hfad.palamarchuksuperapp.data.repository.StoreRepositoryImpl
import com.hfad.palamarchuksuperapp.data.services.FakeStoreApi
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.data.repository.PreferencesRepository
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.ui.screens.MainActivity
import com.hfad.palamarchuksuperapp.ui.screens.MainScreenFragment
import com.hfad.palamarchuksuperapp.ui.screens.SkillsFragment
import com.hfad.palamarchuksuperapp.ui.screens.StoreFragment
import com.hfad.palamarchuksuperapp.ui.viewModels.SkillsViewModel
import com.hfad.palamarchuksuperapp.ui.viewModels.StoreViewModel
import com.hfad.palamarchuksuperapp.ui.viewModels.GenericViewModelFactory
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
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

    @Component.Builder
    interface Builder {
        fun getContext(@BindsInstance context: Context): Builder
        fun build(): AppComponent
    }
}


@Module(includes = [DatabaseModule::class, ModelsModule::class, NetworkModule::class])
object AppModule {

}

@Module
abstract class ModelsModule {

    @Binds
    abstract fun bindViewModelFactory(factory: GenericViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SkillsViewModel::class)
    abstract fun bindSkillsViewModel(viewModel: SkillsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StoreViewModel::class)
    abstract fun bindStoreViewModel(viewModel: StoreViewModel): ViewModel
}

@Module
object NetworkModule {
    @Provides
    fun StoreRepository(): StoreRepository {
        return StoreRepositoryImpl()
    }
    @Provides
    fun PlatziApiImpl(): FakeStoreApi {
        return ProductRepository()
    }
}

@Module
object DatabaseModule {

    @Provides
    fun provideSkillsRepository(): PreferencesRepository {
        return PreferencesRepository.get()
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

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)