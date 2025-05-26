package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
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
        val maxGridHeight = remember { mutableStateOf(10) }
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
            maxGridHeight.value = placeable.maxOfOrNull { it.height } ?: 10
            layout(0, 0) {}
        }

        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Fixed(numberOfGrid),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(orderInfoList.size) { index ->
                AppIconInfoField(
                    modifier = Modifier.height(with(density) { maxGridHeight.value.toDp() }),
                    icon = orderInfoList[index].icon,
                    title = orderInfoList[index].title,
                    description = orderInfoList[index].description
                )
            }
        }
//        LazyVerticalStaggeredGrid(
//            modifier = Modifier,
//            columns = StaggeredGridCells.Fixed(numberOfGrid),
//            verticalItemSpacing = 14.dp,
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            contentPadding = PaddingValues(bottom = 12.dp)
//        ) {
//            items(orderInfoList.size) { index ->
//                AppIconInfoField(
//                    modifier = Modifier,
//                    icon = orderInfoList[index].icon,
//                    title = orderInfoList[index].title,
//                    description = orderInfoList[index].description
//                )
//            }
//        }
    }
}
@Stable
data class OrderInfo(
    val title: String,
    val description: String,
    val icon: Painter?,
)

@Composable
@Preview
fun TableOrderInfoPreview() {
    Column {
        AppTheme {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                TableOrderInfo(
                    orderInfoList = listOf(
                        OrderInfo(
                            "Пункт отправления",
                            "Шанхай, Китай",
                            painterResource(R.drawable.freight)
                        ),
                        OrderInfo(
                            "Пункт назначения",
                            "Одесса, Украина",
                            painterResource(R.drawable.truck)
                        ),
                        OrderInfo(
                            "Статус доставки",
                            "В пути",
                            painterResource(R.drawable.truck)
                        ),
                        OrderInfo(
                            "Ожидаемая дата прибытия",
                            "24.02.2025",
                            painterResource(R.drawable.freight)
                        ),
                        OrderInfo(
                            "Контейнер",
                            "40HC-7865425",
                            painterResource(R.drawable.container_svgrepo_com)
                        ),
                        OrderInfo("Груз", "Электроника", painterResource(R.drawable.warehouse)),
                        OrderInfo(
                            "Менеджер",
                            "Ванько",
                            painterResource(R.drawable.baseline_shopping_basket_24)
                        )
                    )
                )
            }
        }
        AppTheme(
            useDarkTheme = true
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                TableOrderInfo(
                    orderInfoList = listOf(
                        OrderInfo(
                            "Пункт отправления",
                            "Шанхай, Китай",
                            painterResource(R.drawable.freight)
                        ),
                        OrderInfo(
                            "Пункт назначения",
                            "Одесса, Украина",
                            painterResource(R.drawable.truck)
                        ),
                        OrderInfo(
                            "Статус доставки",
                            "В пути",
                            painterResource(R.drawable.truck)
                        ),
                        OrderInfo(
                            "Ожидаемая дата прибытия",
                            "24.02.2025",
                            painterResource(R.drawable.freight)
                        ),
                        OrderInfo(
                            "Контейнер",
                            "40HC-7865425",
                            painterResource(R.drawable.container_svgrepo_com)
                        ),
                        OrderInfo("Груз", "Электроника", painterResource(R.drawable.warehouse)),
                        OrderInfo(
                            "Менеджер",
                            "Ванько",
                            painterResource(R.drawable.baseline_shopping_basket_24)
                        )
                    )
                )
            }
        }
    }
}