package com.hfad.palamarchuksuperapp.core.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.ComposeNavigatorDestinationBuilder
import androidx.navigation.get
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface FeatureApi {

    val homeRoute: Any

    /**
     * Describe feature navigation graph.
     * Directly not used, only with function [featureRegister] from [NavGraphBuilder] of app module.
     *
     * @param navGraphBuilder Builder for navigation graph
     * @param navController Navigation controller for navigation threw this feature in parent graph
     * @param modifier Modifier for composable screen of feature
     * @param coreRoute KClass<*> of base route from app module
     *
     * @sample com.hfad.palamarchuksuperapp.core.ui.navigation.registerGraphSample
     */
    @OptIn(ExperimentalSharedTransitionApi::class)
    fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        modifier: Modifier = Modifier,
        coreRoute: KClass<*>,
        sharedTransitionScope: SharedTransitionScope? = null,
    )
}

/**
 * Connect feature class/navigation graph to main navigation graph.
 *
 * @param featureApi Feature API implementation
 * @param navController Navigation controller for navigation threw this feature in parent graph
 * @param modifier Modifier for composable screen of feature
 */
@OptIn(ExperimentalSharedTransitionApi::class)
inline fun <reified T : Any> NavGraphBuilder.featureRegister(
    featureApi: FeatureApi,
    navController: NavController,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
) {
    featureApi.registerGraph(
        navGraphBuilder = this,
        navController = navController,
        modifier = modifier,
        coreRoute = T::class,
        sharedTransitionScope = sharedTransitionScope
    )
}

/**
 * Construct a nested [NavGraph]
 *
 * @param coreRoute the graph's unique route from a KClass<T>
 * @param startDestination the starting destination's route feature from an Object for this NavGraph.
 * The graph's unique route used by the parent NavHost or navigation to navigate into this nested
 * graph, represented by a KClass<*>..
 * @param typeMap A mapping of KType to custom NavType<*> in the [T]. May be empty if [T] does not
 *   use custom NavTypes.
 * @param builder the builder used to construct the graph
 * @return the newly constructed nested NavGraph
 */
inline fun NavGraphBuilder.navigation(
    coreRoute: KClass<*>,
    startDestination: Any,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    builder: NavGraphBuilder.() -> Unit,
) {
    destination(
        NavGraphBuilder(
            provider,
            startDestination,
            coreRoute,
            typeMap
        ).apply(builder)
    )
}


fun registerGraphSample() {
    /**
    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        modifier: Modifier,
        coreRoute: AppFeatureRoute, //class name from inline feature API
    ) {
        navGraphBuilder.navigation(
            coreRoute = coreRoute ,
            startDestination = FeatureRoutes.Home
        ) {
            composable<FeatureRoutes.Home> {
                Screen(
                    modifier = modifier,
                    navController = navController
                )
            }
        }
    } */
}