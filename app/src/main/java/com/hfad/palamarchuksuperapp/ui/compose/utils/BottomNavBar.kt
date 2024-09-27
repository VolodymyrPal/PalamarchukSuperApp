package com.hfad.palamarchuksuperapp.ui.compose.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.ui.compose.Routes
import com.hfad.palamarchuksuperapp.domain.models.AppImages
import com.hfad.palamarchuksuperapp.domain.models.TabBarItem
import com.hfad.palamarchuksuperapp.ui.compose.LocalNavController
import kotlinx.coroutines.launch

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
) {
    val navController = LocalNavController.current
    val context: Context = LocalContext.current
    val vibe: Vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        }
    }


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = navBackStackEntry?.destination?.route


    @Suppress("DEPRECATION")
    fun onClickVibro() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibe.vibrate(VibrationEffect.createOneShot(2, 60))
        } else {
            vibe.vibrate(2)
        }
    }

    val appImages = remember { AppImages(context).mainImage }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentScreen) {
        selectedTabIndex = when (currentScreen) {
            Routes.MainScreenConstraint.route -> {
                0
            }
            Routes.Settings.route -> {
                2
            }
            else -> {
                3
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                coroutineScope.launch {
                    appImages.updateMainPhoto()
                }
            }
        }
    )

    val HomeTopLevelRoute = TabBarItem(
        title = "Home",
        route = Routes.MainScreenConstraint,
        selectedIcon = painterResource(id = R.drawable.bicon_home_black_filled),
        unselectedIcon = painterResource(id = R.drawable.bicon_home_black_outlined),
        badgeAmount = null,
        onClick = {
            onClickVibro()
            navController.navigate(Routes.MainScreenConstraint) {
                popUpTo(navController.graph.findStartDestination().id) //оставляет главный экран, как точку возврата
                launchSingleTop = true
                restoreState = true
            }
        }
    )

    val CameraTopLevelRoute = TabBarItem(
        title = "Camera",
        route = "Camera",
        selectedIcon = painterResource(id = R.drawable.bicon_camera_filled),
        unselectedIcon = painterResource(id = R.drawable.bicon_camera_outlined),
        badgeAmount = null,
        onClick = {
            onClickVibro()
            cameraLauncher.launch(appImages.getIntentToUpdatePhoto())
        }
    )


    val SettingTopLevelRoute = TabBarItem(
            title = "Settings",
            route = Routes.Settings,
            selectedIcon = painterResource(id = R.drawable.bicon_settings_filled),
            unselectedIcon = painterResource(id = R.drawable.bicon_settings_outlined),
            badgeAmount = 10,
            onClick = {
                onClickVibro()
                navController.navigate(Routes.Settings) {
                    popUpTo(navController.graph.findStartDestination().id) { //оставляет главный экран, как точку возврата
                        saveState = true
                        inclusive = false
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )


    val tabBarItems = remember { listOf(HomeTopLevelRoute, CameraTopLevelRoute, SettingTopLevelRoute) }
    NavigationBar(
        modifier = modifier.padding(bottom = 25.dp, start = 25.dp, end = 25.dp).clip(RoundedCornerShape(25)),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    tabBarItem.onClick()
                    tabBarItem.badgeAmount = null
                },

                icon = {
                    TabBarIconView(
                        isSelected = selectedTabIndex == index,
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title,
                        badgeAmount = tabBarItem.badgeAmount
                    )
                },
                alwaysShowLabel = true,
                label = { Text(text = tabBarItem.title, modifier = Modifier) },
                modifier = Modifier
            )
        }
    }
}

@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: Painter,
    unselectedIcon: Painter,
    title: String,
    badgeAmount: Int?,
) {

    BadgedBox(badge = {
        if (badgeAmount != null) {
            Badge {
                Text(badgeAmount.toString())
            }
        }
    }) {
        Icon(
            if (isSelected) {
                selectedIcon
            } else {
                unselectedIcon
            }, title
        )
    }
}


@Preview
@Composable
fun MyNavBarPreviewElement() {
    BottomNavBar()
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyNavBarPreviewApp() {
    Scaffold(bottomBar = { BottomNavBar() }) {}
}