package com.hfad.palamarchuksuperapp.ui.compose

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.feature.bone.ui.screens.BoneFeature
import kotlinx.serialization.Serializable

class ComposeMainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val startDestination = intent.getStringExtra("startDestination")?.let {
            when (it) {
                Routes.MainScreenConstraint::class.qualifiedName -> Routes.MainScreenConstraint
                Routes.SkillScreen::class.qualifiedName -> Routes.SkillScreen
                Routes.Settings::class.qualifiedName -> Routes.Settings
                Routes.StoreScreen::class.qualifiedName -> Routes.StoreScreen
                Routes.ChatBotScreen::class.qualifiedName -> Routes.ChatBotScreen
                Routes.ValentinesScreen::class.qualifiedName -> Routes.ValentinesScreen
                Routes.MainScreen::class.qualifiedName -> Routes.MainScreen
                else -> Routes.MainScreen
            }
        } ?: Routes.MainScreen
        setContent {
            //Provide viewModelStore to create viewModel scope for all activity
            //CompositionLocalProvider(LocalParentViewModelStore provides this) {}
            MainContent(startDestination)
        }


        /** Create a reference to the default handler, to call standard handler function*/
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(
            UncaughtExceptionHandler(
                application = application,
                defaultHandler = defaultHandler,
            ),
        )

    }
}

class UncaughtExceptionHandler(
    private val application: Application,
    private val defaultHandler: Thread.UncaughtExceptionHandler?,
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // Handle uncaught exceptions here
        val exception = throwable.cause?.cause?.javaClass?.simpleName ?: throwable.cause

        Log.d("Compose", "Uncaught exception: $exception")
        Toast.makeText(application, exception.toString(), Toast.LENGTH_LONG).show()
        Thread.sleep(2000)
        defaultHandler?.uncaughtException(thread, throwable)
    }
}
//Composition local for viewmodel store to create view model for whole activity
//val LocalParentViewModelStore = compositionLocalOf<ViewModelStoreOwner> {
//    error("ViewModelStoreOwner not provided")
//}

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided")
}
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null } //TODO

@OptIn(ExperimentalSharedTransitionApi::class) //TODO
val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null } //TODO


@OptIn(ExperimentalSharedTransitionApi::class) //TODO
@Suppress("detekt.FunctionNaming")
@Composable
fun MainContent(startDestination: Routes = Routes.MainScreen) {
    val navController = rememberNavController()

//    fun scaleIntoContainer(
//        direction: ScaleTransitionDirection = ScaleTransitionDirection.INWARDS,
//        initialScale: Float = if (direction == ScaleTransitionDirection.OUTWARDS) 1.5f else 0.6f,
//    ): EnterTransition {
//        return fadeIn(animationSpec = tween(100, delayMillis = 100))
//    }
//
//    fun scaleOutOfContainer(
//        direction: ScaleTransitionDirection = ScaleTransitionDirection.OUTWARDS,
//        targetScale: Float = if (direction == ScaleTransitionDirection.INWARDS) 0.1f else 3.1f,
//    ): ExitTransition {
//        return fadeOut(tween(delayMillis = 90))
//    }

    SharedTransitionLayout { //TODO
        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalSharedTransitionScope provides this, //TODO
        ) {
            val context = LocalContext.current
            AppTheme {
                NavHost(
                    navController = LocalNavController.current,
                    startDestination = startDestination,
                ) {
                    navigation<Routes.MainScreen>(startDestination = Routes.MainScreenConstraint) {
                        composable<Routes.MainScreenConstraint> {
                            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                                MainScreenRow()
                            }
                        }
                        composable<Routes.ValentinesScreen> {
                            ValentineScreenRoot()
                        }
                    }

                    composable<Routes.SkillScreen> {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            SkillScreen()
                        }
                    }

                    composable<Routes.Settings> {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            Text(text = "Settings")
                        }
                    }

                    composable<Routes.StoreScreen> {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            StoreScreen()
                        }
                    }

                    composable<Routes.ChatBotScreen> {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            ChatScreenRoot()
                        }
                    }

                    composable<Routes.BoneFeature> {
                        val feature = remember { BoneFeature(context.appComponent) }
                        feature.BoneScreenRooted(
                            parentNavController = LocalNavController.current,
                            modifier = Modifier,
                        )
                    }

//                    featureRegister<Routes.BoneFeature>(
//                        featureApi = BoneFeature(context.appComponent),
//                        navController = navController,
//                        modifier = Modifier,
//                        sharedTransitionScope = this@SharedTransitionLayout,
//                        transitionKey = Routes.BoneFeature.TRANS_KEY,
//                    )
                }
            }
        }
    }
}

@Suppress("detekt.FunctionNaming")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainContentPreview() {
    MainContent()
}

enum class ScaleTransitionDirection {
    INWARDS, OUTWARDS
}

@Serializable
sealed interface Routes {

    //Graphs routes
    @Serializable
    object MainScreen : Routes

    //Nested graphs routes
    @Serializable
    object ValentinesScreen : Routes


    //Routes

    @Serializable
    object Settings : Routes {
        val route = this::class.qualifiedName ?: "Settings"
    }

    @Serializable
    object SkillScreen : Routes

    @Serializable
    object MainScreenConstraint : Routes {
        val route = this::class.qualifiedName ?: "MainScreenConstraint"
    }

    @Serializable
    object StoreScreen : Routes

    @Serializable
    object ChatBotScreen : Routes

    @Serializable
    object BoneFeature : Routes {
        const val TRANS_KEY = "bone"
    }
}
