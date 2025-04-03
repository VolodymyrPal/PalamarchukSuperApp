package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneComponent
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneDeps
import com.hfad.palamarchuksuperapp.feature.bone.di.DaggerBoneComponent
import kotlinx.serialization.Serializable

@Composable
fun FeatureBoneScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    diDependents: BoneDeps,
) {
    val component = remember {
        DaggerBoneComponent.builder().deps(diDependents).build()
    }
    CompositionLocalProvider(
        LocalBoneDependencies provides component,
        LocalNavController provides navHostController
    ) {
        NavHost(
            modifier = modifier,
            navController = navHostController,
            startDestination = FeatureBoneRoutes.BoneScreen,
        ) {
            composable<FeatureBoneRoutes.BoneScreen> {
                BoneScreenRoot(
                    navController = navHostController
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