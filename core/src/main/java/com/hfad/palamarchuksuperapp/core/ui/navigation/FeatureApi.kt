package com.hfad.palamarchuksuperapp.core.ui.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface FeatureApi {

    val homeRoute: Any

    fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        modifier: Modifier = Modifier,
        coreRoute: KClass<*>,
    )
}

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