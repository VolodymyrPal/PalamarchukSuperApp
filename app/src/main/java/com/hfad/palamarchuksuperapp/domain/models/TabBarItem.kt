package com.hfad.palamarchuksuperapp.domain.models

import androidx.compose.ui.graphics.painter.Painter

data class TabBarItem(
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    var badgeAmount: Int? = null,
    val onClick: () -> Unit = {}
)