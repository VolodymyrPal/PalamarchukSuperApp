package com.hfad.palamarchuksuperapp.core.ui.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
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
    fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        modifier: Modifier = Modifier,
        coreRoute: KClass<*>,
    )
}

/**
 * Connect feature class/navigation graph to main navigation graph.
 *
 * @param featureApi Feature API implementation
 * @param navController Navigation controller for navigation threw this feature in parent graph
 * @param modifier Modifier for composable screen of feature
 */
inline fun <reified T : Any> NavGraphBuilder.featureRegister(
    featureApi: FeatureApi,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    featureApi.registerGraph(
        navGraphBuilder = this,
        navController = navController,
        modifier = modifier,
        coreRoute = T::class
    )
}

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