package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.compose.FeatureTheme
import com.hfad.palamarchuksuperapp.core.ui.navigation.FeatureApi
import com.hfad.palamarchuksuperapp.core.ui.navigation.composable
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneComponent
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneDeps
import com.hfad.palamarchuksuperapp.feature.bone.di.DaggerBoneComponent
import com.hfad.palamarchuksuperapp.feature.bone.ui.login.LoginScreenRoot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

class BoneFeature(
    featureDependencies: BoneDeps,
) : FeatureApi {
    private val component = DaggerBoneComponent.builder().deps(featureDependencies).build()
    override val homeRoute: FeatureBoneRoutes = FeatureBoneRoutes.LoginScreen

    @OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun BoneScreenRooted(
        parentNavController: NavController,
        modifier: Modifier,
    ) {
        val navController = rememberNavController()
        SharedTransitionLayout { //TODO
            CompositionLocalProvider(
                LocalNavController provides navController,
                LocalSharedTransitionScope provides this, //TODO
                LocalRippleConfiguration provides RippleConfiguration(
                    color = LocalContentColor.current,
                    rippleAlpha = RippleAlpha(
                        pressedAlpha = 0.7f,
                        focusedAlpha = 0.4f,
                        hoveredAlpha = 0.2f,
                        draggedAlpha = 0.3f
                    )
                ),
                LocalBoneDependencies provides component,
            ) {
                FeatureTheme {
                    LaunchedEffect(navController) {
                        navController.currentBackStack.collect { backStackList ->
                            if (backStackList.isEmpty()) {
                                parentNavController.popBackStack()
                            }
                        }
                    }

                    NavHost(
                        startDestination = homeRoute,
                        navController = navController,
                        route = FeatureBoneRoutes.BaseFeatureNavarrete::class
                    ) {
                        composable<FeatureBoneRoutes.BoneScreen> { backStackEntry ->
                            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                                val loginName =
                                    backStackEntry.toRoute<FeatureBoneRoutes.BoneScreen>()
                                BoneScreenRoot(
                                    modifier = modifier,
                                    loginName = loginName.loginName
                                )
                            }
                        }
                        composable<FeatureBoneRoutes.LoginScreen> {
                            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                                LoginScreenRoot(
                                    onLoginClick = { loginName, _ ->
                                        navController.navigate(
                                            FeatureBoneRoutes.BoneScreen(
                                                loginName
                                            )
                                        ) {
                                            popUpTo(FeatureBoneRoutes.LoginScreen) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
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

@Serializable
sealed interface FeatureBoneRoutes {

    @Serializable
    object BaseFeatureNavarrete : FeatureBoneRoutes

    @Serializable
    class BoneScreen(val loginName: String = "bone") : FeatureBoneRoutes

    @Serializable
    object LoginScreen : FeatureBoneRoutes

}

internal val Context.userSession: DataStore<Preferences> by preferencesDataStore(name = "user_session")