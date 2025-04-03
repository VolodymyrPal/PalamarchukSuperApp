package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneComponent
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneDeps
import com.hfad.palamarchuksuperapp.feature.bone.di.DaggerBoneComponent
import kotlinx.serialization.Serializable

fun NavGraphBuilder.featureBoneNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    diDependents: BoneDeps,
) {
    navigation<FeatureBoneRoutes.Root>(
        startDestination = FeatureBoneRoutes.BoneScreen
    ) {
        val component = DaggerBoneComponent.builder().deps(diDependents).build()

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

internal val LocalBoneDependencies = compositionLocalOf<BoneComponent> {
    error("BoneDependencies not provided!")
}
internal val LocalNavController = staticCompositionLocalOf<NavHostController> {
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