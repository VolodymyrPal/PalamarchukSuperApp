package com.hfad.palamarchuksuperapp.feature.bone.ui.theme

import androidx.compose.foundation.Indication
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appRippleEffect(
    bounded: Boolean = true,
    radius: Dp = Dp.Companion.Unspecified,
    color: Color = LocalContentColor.current,
): Indication = ripple(bounded, radius, color)