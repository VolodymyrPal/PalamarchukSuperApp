package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
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
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.daggerViewModel
import com.hfad.palamarchuksuperapp.core.ui.navigation.FeatureApi
import com.hfad.palamarchuksuperapp.core.ui.navigation.composable
import com.hfad.palamarchuksuperapp.core.ui.navigation.navigation
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneComponent
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneDeps
import com.hfad.palamarchuksuperapp.feature.bone.di.DaggerBoneComponent
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.minutes

class BoneFeature(
    featureDependencies: BoneDeps,
) : FeatureApi {
    private val component = DaggerBoneComponent.builder().deps(featureDependencies).build()

    override val homeRoute: FeatureBoneRoutes = FeatureBoneRoutes.BoneScreen

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        modifier: Modifier,
        coreRoute: KClass<*>, //class name from inline feature API
        sharedTransitionScope: SharedTransitionScope?,
        transitionKey: String
    ) {
        navGraphBuilder.navigation(
            coreRoute = coreRoute,
            startDestination = homeRoute
        ) {
            featureComposable<FeatureBoneRoutes.BoneScreen>(
                component = component,
                navController = navController,
                sharedTransitionScope = sharedTransitionScope
            ) {
                val transition = LocalSharedTransitionScope.current
                val anim = LocalNavAnimatedVisibilityScope.current
                val modifier = if (transition != null) {
                    with(transition) {
                        modifier.sharedElement(
                                rememberSharedContentState(key = transitionKey),
                                anim
                            )
                    }
                } else {
                        modifier
                }
                BoneScreenRoot(
                    modifier = modifier,
                    viewModel = daggerViewModel(component.viewModelFactory)
                )
            }
        }
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
val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null } //TODO


internal val LocalBoneDependencies = compositionLocalOf<BoneComponent> {
    error("BoneDependencies not provided!")
}
internal val LocalNavController = staticCompositionLocalOf<NavController> {
    error("NavController not provided")
}


@Serializable
sealed interface FeatureBoneRoutes {

    @Serializable
    object BoneScreen : FeatureBoneRoutes

}