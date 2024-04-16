package com.hfad.palamarchuksuperapp.data

import androidx.compose.ui.graphics.painter.Painter

data class TabBarItem(
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    var badgeAmount: Int? = null,
    val onClick: () -> Unit = {}
)