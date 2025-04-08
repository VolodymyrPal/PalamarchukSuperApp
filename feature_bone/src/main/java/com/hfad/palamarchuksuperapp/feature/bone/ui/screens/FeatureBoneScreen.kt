package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.hfad.palamarchuksuperapp.core.ui.navigation.FeatureApi
import com.hfad.palamarchuksuperapp.core.ui.navigation.composable
import com.hfad.palamarchuksuperapp.core.ui.navigation.navigation
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneComponent
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneDeps
import com.hfad.palamarchuksuperapp.feature.bone.di.DaggerBoneComponent
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

class BoneFeature(
    featureDependenties: BoneDeps,
) : FeatureApi {
    private val component = DaggerBoneComponent.builder().deps(featureDependenties).build()

    override val homeRoute: FeatureBoneRoutes = FeatureBoneRoutes.BoneScreen

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        modifier: Modifier,
        coreRoute: KClass<*>, //class name from inline feature API
        sharedTransitionScope: SharedTransitionScope?,
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
                BoneScreenRoot(
                    modifier = modifier,
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
        // Оборачиваем содержимое экрана в CompositionLocalProvider, чтобы передать необходимые зависимости
        CompositionLocalProvider(
            LocalBoneDependencies provides component,
            LocalNavController provides navController,
            // Передаем текущий NavGraphBuilder в качестве LocalNavAnimatedVisibilityScope,
            // если это требуется логикой приложения
            LocalNavAnimatedVisibilityScope provides this,
            LocalSharedTransitionScope provides sharedTransitionScope
        ) {
            content(navBackStackEntry)
        }
    }
}

internal val LocalNavAnimatedVisibilityScope =
    compositionLocalOf<AnimatedVisibilityScope?> { null } //TODO

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