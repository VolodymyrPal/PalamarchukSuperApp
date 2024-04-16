package com.hfad.palamarchuksuperapp.data

import androidx.annotation.StringRes
import com.hfad.palamarchuksuperapp.R

sealed class ScreenRoute (val route: String, @StringRes val resourceId: Int) {
    object Home: ScreenRoute ("home", R.string.home)
    object Skills: ScreenRoute ("skills", R.string.skills)
    object Settings: ScreenRoute ("setting", R.string.settings)
}