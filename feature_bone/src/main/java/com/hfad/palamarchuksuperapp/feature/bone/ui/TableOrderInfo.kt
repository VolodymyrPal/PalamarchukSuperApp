package com.hfad.palamarchuksuperapp.feature.bone.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppIconInfoField
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.feature.bone.R
import kotlin.math.max

@Composable
fun TableOrderInfo(
    modifier: Modifier = Modifier,
    orderInfoList: List<OrderInfo> = emptyList(),
) {
    BoxWithConstraints(
        modifier = modifier
            .padding(4.dp)
            .heightIn(max = 2500.dp),
    ) {
        val density = LocalDensity.current
        val maxGridWidth = remember { mutableStateOf(20) }
        val numberOfGrid = remember(maxGridWidth.value) {
            max(1, (this.maxWidth / with(density) { maxGridWidth.value.toDp() }).toInt())
        }


        SubcomposeLayout { constraints ->
            val placeable = orderInfoList.take(10).map { orderInfo ->
                subcompose("measure $orderInfo") {
                    AppIconInfoField(
                        icon = orderInfo.icon,
                        title = orderInfo.title,
                        description = orderInfo.description
                    )
                }.map { it.measure(constraints) }
            }.flatten()
            maxGridWidth.value = placeable.maxOfOrNull { it.width } ?: 0
            layout(0, 0) {}
        }

        LazyVerticalStaggeredGrid(
            modifier = Modifier,
            columns = StaggeredGridCells.Fixed(numberOfGrid),
            verticalItemSpacing = 14.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(orderInfoList.size) { index ->
                AppIconInfoField(
                    modifier = Modifier,
                    icon = orderInfoList[index].icon,
                    title = orderInfoList[index].title,
                    description = orderInfoList[index].description
                )
            }
        }
    }
}

data class OrderInfo(
    val title: String,
    val description: String,
    val icon: Painter?,
)

@Composable
@Preview
fun TableOrderInfoPreview() {
    val iconPainter = painterResource(R.drawable.d_letter)
    TableOrderInfo(
        orderInfoList = listOf(
            OrderInfo("123321", "123321", iconPainter),
            OrderInfo("1233", "1233", iconPainter),
            OrderInfo("12", "12", iconPainter),
            OrderInfo("123321", "1233gfgffgfgfsdfdfgfdgsdfsfgfgf21", iconPainter),
            OrderInfo("1233", "1233", iconPainter),
            OrderInfo("12", "12", iconPainter),
            OrderInfo("123321", "123321", iconPainter),
            OrderInfo("1233", "1233", iconPainter),
            OrderInfo("12", "12", iconPainter),
        )
    )
}