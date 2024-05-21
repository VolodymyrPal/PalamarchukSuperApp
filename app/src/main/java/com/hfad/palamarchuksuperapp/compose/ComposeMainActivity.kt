package com.hfad.palamarchuksuperapp.compose

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.hfad.palamarchuksuperapp.domain.models.ScreenRoute
import com.hfad.palamarchuksuperapp.compose.utils.MyNavBar

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
    AppTheme {

        NavHost(navController = navController, startDestination = ScreenRoute.Home.route) {

            composable(ScreenRoute.Home.route) {
                MainScreenConstraint(
                    actionSkillsButton = { navController.navigate("skills") },
                    navController = navController
                )
            }
            composable(ScreenRoute.Skills.route) {
            }
        }

//        Scaffold(
//            modifier = modifier.fillMaxSize(),
//            bottomBar = {
//                MyNavBar()
//            }
//        ) { paddingValues ->
//            NavHost(
//                navController = navController,
//                startDestination = ScreenRoute.Home.route
//            ) {
//                composable(ScreenRoute.Home.route) {
//                    MainScreenConstraint(
//                        actionSkillsButton = { navController.navigate("skills") }
//                    )
//                }
//                composable(ScreenRoute.Skills.route) {
//
//                }
//
//                composable(ScreenRoute.Settings.route) {
//
//                }
//            }
//        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainContentPreview () {
    MainContent()
}

