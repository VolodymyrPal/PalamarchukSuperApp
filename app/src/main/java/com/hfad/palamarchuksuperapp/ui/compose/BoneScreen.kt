package com.hfad.palamarchuksuperapp.ui.compose

import android.content.Context
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BoneScreenRoot(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    navController: NavHostController = LocalNavController.current,
) {
    val localTransitionScope = LocalSharedTransitionScope.current
        ?: error(IllegalStateException("No SharedElementScope found"))
    val animatedContentScope = LocalNavAnimatedVisibilityScope.current
        ?: error(IllegalStateException("No AnimatedVisibility found"))

    with(localTransitionScope) {
        BoneScreen(
            modifier = modifier.sharedElement(
                this.rememberSharedContentState("bone"),
                animatedContentScope,
            ),
            viewModel = ViewModel(),
            navController = navController,
        )
    }
}

@Composable
fun BoneScreen(
    modifier: Modifier = Modifier,
    viewModel: ViewModel? = null,
    navController: NavController = LocalNavController.current
) {

}