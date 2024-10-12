package com.hfad.palamarchuksuperapp.domain.models

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter

@Stable
data class TabBarItem <T: Any>(
    val title: String,
    val route: T,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    var badgeAmount: Int? = null,
    val onClick: () -> Unit = {}
)