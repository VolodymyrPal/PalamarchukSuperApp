package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.hfad.palamarchuksuperapp.core.ui.navigation.FeatureApi
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneComponent
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneDeps
import com.hfad.palamarchuksuperapp.feature.bone.di.DaggerBoneComponent
import kotlinx.serialization.Serializable

class BoneFeature(
    diDependents: BoneDeps,
) : FeatureApi {

    companion object Route {
        val route = FeatureBoneRoutes.Root
    }

    private val component = DaggerBoneComponent.builder().deps(diDependents).build()

    override val homeRoute: FeatureBoneRoutes = FeatureBoneRoutes.BoneScreen

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        modifier: Modifier,
    ) {
        navGraphBuilder.navigation<FeatureBoneRoutes.Root>(
            startDestination = homeRoute,
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

inline fun NavGraphBuilder.navigation(
    coreRoute: Any,
    startDestination: Any,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    builder: NavGraphBuilder.() -> Unit,
): Unit = destination(
    NavGraphBuilder(
        provider,
        startDestination,
        coreRoute::class,
        typeMap
    ).apply(builder)
)

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