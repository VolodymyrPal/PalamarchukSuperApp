package com.hfad.palamarchuksuperapp.core.ui.composables.effects

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

@Composable
inline fun HideShowSystemBar() {
    val activityWindow = LocalActivity.current
    if (activityWindow is ComponentActivity) {
        LaunchedEffect(activityWindow) {
            WindowCompat.getInsetsController(activityWindow.window, activityWindow.window.decorView)
                .apply {
                    hide(WindowInsetsCompat.Type.statusBars())
                    hide(WindowInsetsCompat.Type.navigationBars())
                }
        }

        DisposableEffect(activityWindow) {
            onDispose {
                WindowCompat.getInsetsController(
                    activityWindow.window,
                    activityWindow.window.decorView
                ).apply {
                    show(WindowInsetsCompat.Type.statusBars())
                    show(WindowInsetsCompat.Type.navigationBars())
                }
            }
        }
    }
}