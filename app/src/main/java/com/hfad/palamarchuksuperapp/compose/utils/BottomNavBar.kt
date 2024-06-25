package com.hfad.palamarchuksuperapp.compose.utils

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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.compose.Routes
import com.hfad.palamarchuksuperapp.domain.models.AppImages
import com.hfad.palamarchuksuperapp.domain.models.TabBarItem
import kotlinx.coroutines.launch

@Composable
fun BottomNavBar(modifier: Modifier = Modifier, context: Context = LocalContext.current, navController: NavController?) {
    val vibe: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            LocalContext.current.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        LocalContext.current.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
    }

    @Suppress("DEPRECATION")
    fun onClickVibro() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibe.vibrate(VibrationEffect.createOneShot(2, 60))
        } else {
            vibe.vibrate(1)
        }
    }

    val appImages = AppImages(context).mainImage
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                coroutineScope.launch {
                    appImages.updateMainPhoto()
                }
            }
        })

    val homeTab = TabBarItem(
        title = "Home",
        selectedIcon = painterResource(id = R.drawable.bicon_home_black_filled),
        unselectedIcon = painterResource(id = R.drawable.bicon_home_black_outlined),
        onClick = { onClickVibro()
            navController?.navigate(MainScreenConstraint)
        }
    )
    val cameraTab = TabBarItem(
        title = "Camera",
        selectedIcon = painterResource(id = R.drawable.bicon_camera_filled),
        unselectedIcon = painterResource(id = R.drawable.bicon_camera_outlined),
        onClick = {
            onClickVibro()
            cameraLauncher.launch(appImages.getIntentToUpdatePhoto())
        })

    val settingsTab = TabBarItem(
        "Settings",
        selectedIcon = painterResource(id = R.drawable.bicon_settings_filled),
        unselectedIcon = painterResource(id = R.drawable.bicon_settings_outlined),
        badgeAmount = 10,
        onClick = { onClickVibro()
            navController?.navigate(Settings)
            }
    )

    val tabBarItems = listOf(homeTab, cameraTab, settingsTab)

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {

        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    onClickVibro()
                    selectedTabIndex = index
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
                label = { Text(text = tabBarItem.title, modifier = modifier) },
                modifier = modifier
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
    badgeAmount: Int?
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
    BottomNavBar(navController = null)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyNavBarPreviewApp() {
    Scaffold(bottomBar = {  }) {}
}