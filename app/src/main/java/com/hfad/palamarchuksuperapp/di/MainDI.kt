import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.hfad.palamarchuksuperapp.data.SkillsRepositoryImpl
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.domain.repository.PreferencesRepository
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.presentation.screens.MainActivity
import com.hfad.palamarchuksuperapp.presentation.screens.MainScreenFragment
import com.hfad.palamarchuksuperapp.presentation.screens.SkillsFragment
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(fragmentActivity: FragmentActivity)
    fun inject(fragmentActivity: SkillsFragment)
    fun inject(mainScreenFragment: MainScreenFragment)
    fun appVibrator(): AppVibrator

    @Component.Builder
    interface Builder {
        fun getContext(@BindsInstance context: Context): Builder
        fun build(): AppComponent
    }
}


@Module(includes = [RepositoryModule::class, ModelsModule::class])
object AppModule {

}

@Module
object ModelsModule {

}

@Module
object RepositoryModule {

    @Provides
    fun provideSkillsRepository(): PreferencesRepository {
        return PreferencesRepository.get()
    }

    @Provides
    fun provideSkillRepository(): SkillRepository {
        return SkillsRepositoryImpl()
    }
}
