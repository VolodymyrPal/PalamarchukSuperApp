package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.daggerViewModel
import com.hfad.palamarchuksuperapp.core.ui.navigation.FeatureApi
import com.hfad.palamarchuksuperapp.core.ui.navigation.composable
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneComponent
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneDeps
import com.hfad.palamarchuksuperapp.feature.bone.di.DaggerBoneComponent
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.minutes

class BoneFeature(
    featureDependencies: BoneDeps,
) : FeatureApi {
    private val component = DaggerBoneComponent.builder().deps(featureDependencies).build()
    private val controller = LoggerDataStoreHandler(component.context)
    override val homeRoute: FeatureBoneRoutes = FeatureBoneRoutes.BoneScreen

    @Composable
    override fun BoneScreenRooted(
        parentNavController: NavController,
        modifier: Modifier,
    ) {
        val navController = rememberNavController()
        CompositionLocalProvider(
            LocalNavController provides parentNavController
        ) {
            val a = navController.currentBackStack.collectAsState()
            Log.d("Child nav", a.value.size.toString())
            Log.d("Parent nav", parentNavController.currentDestination?.route.toString())


            NavHost(
                startDestination = homeRoute,
                navController = navController,
                route = FeatureBoneRoutes.RouteRoot::class
            ) {
                composable<FeatureBoneRoutes.BoneScreen> {
                    BoneScreenRoot(
                        modifier = modifier,
                        viewModel = daggerViewModel(component.viewModelFactory),
                    )
                }
                composable<FeatureBoneRoutes.LoginScreen> {
                    BoneScreenRoot(
                        modifier = modifier,
                        viewModel = daggerViewModel(component.viewModelFactory)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        modifier: Modifier,
        coreRoute: KClass<*>, //class name from inline feature API
        sharedTransitionScope: SharedTransitionScope?,
        transitionKey: String,
    ) {
//        navGraphBuilder.navigation(
//            coreRoute = coreRoute,
//            startDestination = FeatureBoneRoutes.BoneScreen//homeRoute
//        ) {
//            featureComposable<FeatureBoneRoutes.BoneScreen>(
//                component = component,
//                navController = navController,
//                sharedTransitionScope = sharedTransitionScope
//            ) {
//                val transition = LocalSharedTransitionScope.current
//                val anim = LocalNavAnimatedVisibilityScope.current
//                val modifier = if (transition != null) {
//                    with(transition) {
//                        modifier.sharedElement(
//                            rememberSharedContentState(key = transitionKey),
//                            anim
//                        )
//                    }
//                } else {
//                    modifier
//                }
//                BoneScreenRoot(
//                    modifier = modifier,
//                    viewModel = daggerViewModel(component.viewModelFactory)
//                )
//            }
//
//            featureComposable<FeatureBoneRoutes.LoginScreen>(
//                component = component,
//                navController = navController,
//                sharedTransitionScope = sharedTransitionScope
//            ) {
//                val coroutineScope = rememberCoroutineScope()
//                val transition = LocalSharedTransitionScope.current
//                val anim = LocalNavAnimatedVisibilityScope.current
//                val modifier = if (transition != null) {
//                    with(transition) {
//                        modifier.sharedElement(
//                            rememberSharedContentState(key = transitionKey),
//                            anim
//                        )
//                    }
//                } else {
//                    modifier
//                }
//                Surface(
//                    modifier = modifier
//                        .fillMaxSize(),
//                    onClick = {
//                        coroutineScope.launch {
//                            Log.d("Click", "CLicked")
//                            controller.setIsLogged()
//                        }
//                    }
//                ) {}
//            }
//        }
    }
}

/**
 * Расширение для NavGraphBuilder, регистрирующее composable с автоматической обёрткой зависимостей.
 *
 * @param route маршрут для экрана
 * @param component общий компонент (зависимость)
 * @param navController NavController, используемый для навигации
 * @param sharedTransitionScope опциональный shared transition scope
 * @param content Composable, представляющий содержимое экрана
 */
@OptIn(ExperimentalSharedTransitionApi::class)
internal inline fun <reified T : Any> NavGraphBuilder.featureComposable(
    component: BoneComponent,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope?,
    noinline content: @Composable (NavBackStackEntry) -> Unit,
) {
    composable(route = T::class) { navBackStackEntry ->
        CompositionLocalProvider(
            LocalBoneDependencies provides component,
            LocalNavController provides navController,
            LocalNavAnimatedVisibilityScope provides this,
            LocalSharedTransitionScope provides sharedTransitionScope
        ) {
            content(navBackStackEntry)
        }
    }
}

internal val LocalNavAnimatedVisibilityScope =
    compositionLocalOf<AnimatedVisibilityScope> { error("Animated visibility scope not provided") }

@OptIn(ExperimentalSharedTransitionApi::class) //TODO
val LocalSharedTransitionScope =
    staticCompositionLocalOf<SharedTransitionScope?> { null } //TODO


internal val LocalBoneDependencies = compositionLocalOf<BoneComponent> {
    error("BoneDependencies not provided!")
}
internal val LocalNavController = staticCompositionLocalOf<NavController> {
    error("NavController not provided")
}

private val IS_LOGGED_KEY = booleanPreferencesKey("is_logged")
private val Context.isLogged: DataStore<Preferences> by preferencesDataStore(name = "is_logged")


@Serializable
sealed interface FeatureBoneRoutes {

    @Serializable
    object RouteRoot : FeatureBoneRoutes

    @Serializable
    object BoneScreen : FeatureBoneRoutes

    @Serializable
    object LoginScreen : FeatureBoneRoutes

}

class LoggerDataStoreHandler(
    val context: Context,
) {
    private val minuteToLogout = 15.minutes

    suspend fun setIsLogged() {
        context.isLogged.edit {
            it[IS_LOGGED_KEY] = true
        }
    }

    suspend fun clearUser() {
        context.isLogged.edit {
            it.remove(IS_LOGGED_KEY)
        }
    }

    val isLoggedFlow = context.isLogged.data.map {
        it[IS_LOGGED_KEY] ?: false
    }.distinctUntilChanged()
}