package com.hfad.palamarchuksuperapp.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import kotlinx.serialization.Serializable

class ComposeMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent()
        }
    }
}

@Composable
fun MainContent() {
    val navController = rememberNavController()

    fun scaleIntoContainer(
        direction: ScaleTransitionDirection = ScaleTransitionDirection.INWARDS,
        initialScale: Float = if (direction == ScaleTransitionDirection.OUTWARDS) 1.5f else 0.6f
    ): EnterTransition {
        return fadeIn(animationSpec = tween(100, delayMillis = 100))
    }

    fun scaleOutOfContainer(
        direction: ScaleTransitionDirection = ScaleTransitionDirection.OUTWARDS,
        targetScale: Float = if (direction == ScaleTransitionDirection.INWARDS) 0.1f else 3.1f
    ): ExitTransition {
        return fadeOut(tween(delayMillis = 90))
    }

    AppTheme {

        NavHost(navController = navController, startDestination = Routes.MainScreenConstraint) {
            composable<Routes.MainScreenConstraint> {
//                enterTransition = {
//                    when (ScreenRoute.Home.route) {
//                        "home" ->
//                            slideIntoContainer(
//                                AnimatedContentTransitionScope.SlideDirection.Right,
//                                animationSpec = tween(400)
//                            )
//
//                        else -> null
//                    }
//                },
//                exitTransition = {
//                    when (targetState.destination.route) {
//                        "skills" ->
//                            slideOutOfContainer(
//                                AnimatedContentTransitionScope.SlideDirection.Right,
//                                animationSpec = tween(700)
//                            )
//
//                        else -> null
//                    }
//                                 },
//                popEnterTransition = {
//                    when (initialState.destination.route) {
//                        "details" ->
//                            slideIntoContainer(
//                                AnimatedContentTransitionScope.SlideDirection.Left,
//                                animationSpec = tween(400)
//                            )
//
//                        else -> null
//                    }
//                },
//                popExitTransition = {
//                    when (targetState.destination.route) {
//                        "details" ->
//                            slideOutOfContainer(
//                                AnimatedContentTransitionScope.SlideDirection.Left,
//                                animationSpec = tween(400)
//                            )
//
//                        else -> null
//                    }
//                }
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally() + expandHorizontally(expandFrom = Alignment.End)
                            + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                            + shrinkHorizontally() + fadeOut(),
                ) {
                    MainScreenConstraint(
                        actionSkillsButton = remember { { navController.navigate(Routes.Settings) } },
                        navController = navController
                    )
                }
            }
            composable<Routes.SkillScreen> {
//                enterTransition = {
//                    scaleIntoContainer()
//                },
//                exitTransition = {
//                    scaleOutOfContainer(direction = ScaleTransitionDirection.INWARDS)
//                },
//                popEnterTransition = {
//                    scaleIntoContainer(direction = ScaleTransitionDirection.OUTWARDS)
//                },
//                popExitTransition = {
//                    scaleOutOfContainer()
//                }
                SkillScreen(

                    navController = navController
                )
            }
            composable<Routes.Settings>
//                enterTransition = {
//                    scaleIntoContainer()
//                },
//                exitTransition = {
//                    scaleOutOfContainer(direction = ScaleTransitionDirection.INWARDS)
//                },
//                popEnterTransition = {
//                    scaleIntoContainer(direction = ScaleTransitionDirection.OUTWARDS)
//                },
//                popExitTransition = {
//                    scaleOutOfContainer()
//                }
             {
                Text(text = "Settings")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainContentPreview () {
    MainContent()
}

enum class ScaleTransitionDirection {
    INWARDS, OUTWARDS
}

@Serializable
sealed class Routes {
    @Serializable
    object Settings

    @Serializable
    object SkillScreen

    @Serializable
    object MainScreenConstraint
}
