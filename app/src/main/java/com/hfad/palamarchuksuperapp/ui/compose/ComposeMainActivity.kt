package com.hfad.palamarchuksuperapp.ui.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
        initialScale: Float = if (direction == ScaleTransitionDirection.OUTWARDS) 1.5f else 0.6f,
    ): EnterTransition {
        return fadeIn(animationSpec = tween(100, delayMillis = 100))
    }

    fun scaleOutOfContainer(
        direction: ScaleTransitionDirection = ScaleTransitionDirection.OUTWARDS,
        targetScale: Float = if (direction == ScaleTransitionDirection.INWARDS) 0.1f else 3.1f,
    ): ExitTransition {
        return fadeOut(tween(delayMillis = 90))
    }

    val navArgClass = remember {
        object : Navigation {
            override fun navigate(route: Routes) {
                navController.navigate(route)
            }
        }
    }
    AppTheme {
        NavHost(
            navController = navController,
            startDestination = Routes.MainScreenConstraint
        ) {
            composable<Routes.MainScreenConstraint> {
                MainScreenRow(
                    actionSkillsButton = remember { { navArgClass.navigate(Routes.Settings) } },
                    navController = navArgClass
                )
            }
            composable<Routes.SkillScreen> {
                SkillScreen(
                    navController = navArgClass
                )
            }
            composable<Routes.Settings> {
                Text(text = "Settings")
            }
            composable<Routes.StoreScreen> {
                StoreScreen(
                    navController = navArgClass
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainContentPreview() {
    MainContent()
}

enum class ScaleTransitionDirection {
    INWARDS, OUTWARDS
}

interface Navigation {
    fun navigate(route: Routes)
}

@Serializable
sealed interface Routes {
    @Serializable
    object Settings : Routes

    @Serializable
    object SkillScreen : Routes

    @Serializable
    object MainScreenConstraint : Routes

    @Serializable
    object StoreScreen : Routes
}
