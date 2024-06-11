
import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.hfad.palamarchuksuperapp.data.SkillsRepositoryImpl
import com.hfad.palamarchuksuperapp.domain.repository.PreferencesRepository
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.presentation.screens.MainActivity
import com.hfad.palamarchuksuperapp.presentation.screens.MainScreenFragment
import com.hfad.palamarchuksuperapp.presentation.screens.SkillsFragment
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsViewModel
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsViewModel_Factory
import dagger.Binds
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
    fun skillsViewModel () : SkillsViewModel

    @Component.Builder
    interface Builder {
        fun getContext(@BindsInstance context: Context): Builder
        fun build(): AppComponent
    }
}


@Module (includes = [RepositoryModule::class])
object AppModule  {

    @Provides
    fun getVibrator(context: Context): Vibrator {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            return vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            return context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        }
    }

    @Provides
    fun provideSkillsViewModel(): SkillsViewModel {
        return SkillsViewModel(skillRepositoryImpl())
    }

    @Provides
    fun skillRepositoryImpl () : SkillRepository {
        return SkillsRepositoryImpl ()
    }
}

@Module
object RepositoryModule {

    @Provides
    fun provideSkillsRepository(): PreferencesRepository {
        return PreferencesRepository.get()
    }

}

@Module
abstract class ViewModelFactoryModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: SkillsViewModel_Factory): ViewModelProvider.Factory
}