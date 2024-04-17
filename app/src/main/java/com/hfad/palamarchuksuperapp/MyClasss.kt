package com.hfad.palamarchuksuperapp

import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Inject

class Automobile @Inject constructor() {

}

class NewClass {
    @Inject lateinit var automobile: Automobile
    @Inject lateinit var skillsViewModel: SkillsViewModel

    init {
        DaggerAppComponent.create().inject(this)
    }
}

@Component (modules = [AppModule :: class, OtherModule::class])
interface AppComponent {

    fun inject(newClass: NewClass)
    fun inject(skillsFragment: SkillsFragment)

    val viewModel: SkillsViewModel

}

@Module
object AppModule {
    @Provides
    fun getVooModel () : SkillsViewModel {
        return SkillsViewModel()
    }
}
@Module
class OtherModule {
    @Provides
    fun getAuto (): Automobile {
        return Automobile()
    }
}

