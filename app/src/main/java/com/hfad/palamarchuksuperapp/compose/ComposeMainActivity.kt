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
import com.hfad.palamarchuksuperapp.PreferencesRepository
import com.hfad.palamarchuksuperapp.data.ScreenRoute
import com.hfad.palamarchuksuperapp.compose.utils.MyNavBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class ComposeMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent()
        }
    }
}


@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    preferencesRepository: PreferencesRepository? = PreferencesRepository.get(),
    context: Context = LocalContext.current,
    activity: AppCompatActivity? = context as AppCompatActivity
) {

    val navController = rememberNavController()

    AppTheme {

        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = { MyNavBar() }
        ) { paddingValues ->
            NavHost(navController = navController, startDestination = ScreenRoute.Home.route) {
                composable(ScreenRoute.Home.route) {
                    MainScreenConstraint(
                        paddingValues = paddingValues,
                        activity = activity,
                        actionForDayNight = {
                            CoroutineScope(Dispatchers.Main).launch {
                                preferencesRepository?.setStoredNightMode(!preferencesRepository.storedQuery.first())
                            }
                        },
                        actionSkillsButton = { navController.navigate("skills") }
                    )
                }
                composable(ScreenRoute.Skills.route) {
                    MyNavBar()
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainContentPreview(modifier: Modifier = Modifier) {
    MainContent(preferencesRepository = null, activity = null)
}

