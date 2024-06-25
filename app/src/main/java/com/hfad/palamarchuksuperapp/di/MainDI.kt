import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.hfad.palamarchuksuperapp.data.dao.SkillsDao
import com.hfad.palamarchuksuperapp.data.database.DATABASE_PROJECT_NAME
import com.hfad.palamarchuksuperapp.data.database.SkillsDatabase
import com.hfad.palamarchuksuperapp.data.repository.SkillsRepositoryImpl
import com.hfad.palamarchuksuperapp.data.repository.StoreRepositoryImplForPreview
import com.hfad.palamarchuksuperapp.presentation.viewModels.GenericViewModelFactory
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.domain.repository.PreferencesRepository
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.presentation.screens.MainActivity
import com.hfad.palamarchuksuperapp.presentation.screens.MainScreenFragment
import com.hfad.palamarchuksuperapp.presentation.screens.SkillsFragment
import com.hfad.palamarchuksuperapp.presentation.screens.StoreFragment
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsViewModel
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Provider
import javax.inject.Singleton

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
object ModelsModule {
    @Provides
    fun provideSkillsViewModelFactory(provider: Provider<SkillsViewModel>): ViewModelProvider.Factory {
        return GenericViewModelFactory(provider)
    }
}

@Module
object NetworkModule {
    @Provides
    fun StoreRepository(): StoreRepository {
        return StoreRepositoryImplForPreview()
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
    fun provideCountryDB(context: Context): SkillsDatabase {
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
