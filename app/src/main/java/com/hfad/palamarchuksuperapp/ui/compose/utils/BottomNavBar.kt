package com.hfad.palamarchuksuperapp.ui.compose.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.ui.compose.Routes
import com.hfad.palamarchuksuperapp.domain.models.AppImages
import com.hfad.palamarchuksuperapp.domain.models.TabBarItem
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction1

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    navigate: KFunction1<Routes, Unit>?,
) {
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

    @Suppress("DEPRECATION")
    fun onClickVibro() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibe.vibrate(VibrationEffect.createOneShot(2, 60))
        } else {
            vibe.vibrate(1)
        }
    }

    val appImages = remember { AppImages(context).mainImage }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
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

    val selectedIconHome = painterResource(id = R.drawable.bicon_home_black_filled)
    val unselectedIconHome = painterResource(id = R.drawable.bicon_home_black_outlined)

    val homeTab = remember {
        TabBarItem(
            title = "Home",
            selectedIcon = selectedIconHome,
            unselectedIcon = unselectedIconHome,
            onClick = {
                onClickVibro()
                navigate?.invoke(Routes.MainScreenConstraint)
            }
        )
    }

    val selectedIconCamera = painterResource(id = R.drawable.bicon_camera_filled)
    val unselectedIconCamera = painterResource(id = R.drawable.bicon_camera_outlined)

    val cameraTab = remember {
        TabBarItem(
            title = "Camera",
            selectedIcon = selectedIconCamera,
            unselectedIcon = unselectedIconCamera,
            onClick = {
                onClickVibro()
                cameraLauncher.launch(appImages.getIntentToUpdatePhoto())
            }
        )
    }
    val selectedIconSettings = painterResource(id = R.drawable.bicon_settings_filled)
    val unselectedIconSettings = painterResource(id = R.drawable.bicon_settings_outlined)

    val settingsTab = remember {
        TabBarItem(
            "Settings",
            selectedIcon = selectedIconSettings,
            unselectedIcon = unselectedIconSettings,
            badgeAmount = 10,
            onClick = {
                onClickVibro()
                navigate?.invoke(Routes.Settings)
            }
        )
    }

    val tabBarItems = remember { listOf(homeTab, cameraTab, settingsTab) }
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
    BottomNavBar(navigate = null)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyNavBarPreviewApp() {
    Scaffold(bottomBar = { }) {}
}