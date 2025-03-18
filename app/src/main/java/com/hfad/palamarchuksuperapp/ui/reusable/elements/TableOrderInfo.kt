package com.hfad.palamarchuksuperapp.ui.reusable.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.R
import kotlin.math.max

@Composable
fun TableOrderInfo(
    modifier: Modifier = Modifier,
    orderInfoList: List<OrderInfo> = emptyList<OrderInfo>(),
) {
    BoxWithConstraints(
        modifier = modifier
            .padding(10.dp)
            .heightIn(max = 2500.dp),
    ) {
        val density = LocalDensity.current
        val maxGridWidth = remember { mutableStateOf(20) }
        val numberOfGrid = remember(maxGridWidth.value) {
            max(1, (this.maxWidth / with(density) { maxGridWidth.value.toDp() }).toInt())
        }


        Box(modifier = Modifier.alpha(0f)) {
            orderInfoList.forEach { orderInfo ->
                AppIconInfoField(
                    modifier = Modifier.onSizeChanged { size ->
                        if (size.width > maxGridWidth.value) {
                            maxGridWidth.value = size.width
                        }
                    },
                    icon = orderInfo.icon,
                    title = orderInfo.title,
                    description = orderInfo.description
                )
            }
        }


        LazyVerticalStaggeredGrid(
            modifier = Modifier,
            columns = StaggeredGridCells.Fixed(numberOfGrid),
            verticalItemSpacing = 14.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
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