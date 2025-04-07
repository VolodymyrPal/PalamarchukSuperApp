package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hfad.palamarchuksuperapp.core.ui.navigation.FeatureApi
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

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        modifier: Modifier,
        coreRoute: KClass<*>, //class name from inline feature API
    ) {
        navGraphBuilder.navigation(
            coreRoute = coreRoute,
            startDestination = homeRoute
        ) {
            composable<FeatureBoneRoutes.BoneScreen> {
                CompositionLocalProvider(
                    LocalBoneDependencies provides component,
                    LocalNavController provides navController
                ) {
                    BoneScreenRoot(
                        modifier = modifier,
                    )
                }
            }
        }
    }
}

internal val LocalBoneDependencies = compositionLocalOf<BoneComponent> {
    error("BoneDependencies not provided!")
}
internal val LocalNavController = staticCompositionLocalOf<NavController> {
    error("NavController not provided")
}


@Serializable
sealed interface FeatureBoneRoutes {

    //Graphs routes
    @Serializable
    object Root : FeatureBoneRoutes

    @Serializable
    object BoneScreen : FeatureBoneRoutes

}