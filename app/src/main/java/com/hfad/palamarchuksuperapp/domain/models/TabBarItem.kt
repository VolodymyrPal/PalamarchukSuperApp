package com.hfad.palamarchuksuperapp.domain.models

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable

@Stable
data class TabBarItem <T: Any>(
    val title: String,
    val route: T,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    var badgeAmount: Int? = null,
    val onClick: () -> Unit = {}
)