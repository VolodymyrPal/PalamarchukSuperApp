package com.hfad.palamarchuksuperapp.core.ui.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface FeatureApi {

    val homeRoute : Any

    fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        modifier: Modifier = Modifier,
    )
}

fun < T: Any> NavGraphBuilder.featureRegister(
    featureApi: FeatureApi,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    featureApi.registerGraph(
        navGraphBuilder = this,
        navController = navController,
        modifier = modifier,
    )
}