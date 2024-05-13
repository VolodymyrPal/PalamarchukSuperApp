package com.hfad.palamarchuksuperapp.compose.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.MediaStore
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.data.ScreenRoute
import com.hfad.palamarchuksuperapp.data.TabBarItem
import java.io.File

@Composable
fun MyNavBar(modifier: Modifier = Modifier, context: Context = LocalContext.current) {
    val vibe: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            LocalContext.current.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        LocalContext.current.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
    }
    @Suppress("DEPRECATION")
    fun onClickVibro () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibe.vibrate(VibrationEffect.createOneShot(2, 60))
        } else {
            vibe.vibrate(1)
        }
    }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var tempImageFile: File? = null
    val mainPhoto = File(File(context.filesDir, "app_images"), "MainPhoto")

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                mainPhoto.delete()
                tempImageFile!!.renameTo(mainPhoto)
            }
        })

    val homeTab = TabBarItem(
        title = stringResource(id = ScreenRoute.Home.resourceId),
        selectedIcon = painterResource(id = R.drawable.bicon_home_black_filled),
        unselectedIcon = painterResource(id = R.drawable.bicon_home_black_outlined),
        onClick = {onClickVibro()}
    )
    val cameraTab = TabBarItem(
        title = stringResource(id = ScreenRoute.Skills.resourceId),
        selectedIcon = painterResource(id = R.drawable.bicon_camera_filled),
        unselectedIcon = painterResource(id = R.drawable.bicon_camera_outlined),
        onClick = {
            onClickVibro()
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            tempImageFile = File.createTempFile(
                "image",
                "temp",
                File(context.filesDir, "app_images")
            )
            val tempUri = FileProvider.getUriForFile(
                context, "${context.packageName}.provider",
                tempImageFile!!
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri)
            cameraLauncher.launch(cameraIntent)
        })

    val settingsTab = TabBarItem(
        "Settings",
        selectedIcon = painterResource(id = R.drawable.bicon_settings_filled),
        unselectedIcon = painterResource(id = R.drawable.bicon_settings_outlined),
        badgeAmount = 10,
        onClick = {onClickVibro()}
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
fun MyNavBarPreviewElement () {
    MyNavBar()
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview (showBackground = true, showSystemUi = true)
@Composable
fun MyNavBarPreviewApp () {
    Scaffold (bottomBar = { MyNavBar() }) {}
}