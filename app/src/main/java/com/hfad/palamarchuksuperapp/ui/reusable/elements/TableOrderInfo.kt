package com.hfad.palamarchuksuperapp.ui.reusable.elements

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.R

@Composable
fun TableOrderInfo(
    modifier: Modifier = Modifier,
    orderInfoList: List<OrderInfo> = emptyList<OrderInfo>(),
) {
    BoxWithConstraints (
        modifier = modifier.background(Color.Red).padding(10.dp),
    ) {
        Surface(
            modifier = Modifier.background(Color.Yellow).padding(5.dp),
            shape = RectangleShape,
        ) {
            var width  = remember { mutableIntStateOf(0) }
            AppIconInfoField(
                constraintsBack = { width1, _ -> width.value = width1 },
                text = "Testing text",
                icon = painterResource(R.drawable.d_letter)
            )

            Box(
                modifier = Modifier.background(Color.Blue).size(this.maxWidth)
            )
        }
        this.maxWidth
    }
}

data class OrderInfo(
    val title: String,
    val description: String,
    val icon: Painter?,
)