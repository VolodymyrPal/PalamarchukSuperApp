package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppIconInfoField
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.feature.bone.R

@Composable
fun TableOrderInfo(
    modifier: Modifier = Modifier,
    orderInfoList: List<OrderInfo> = emptyList(),
) {
    EqualWidthFlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
    ) {
        orderInfoList.forEach { orderInfo ->
            AppIconInfoField(
                modifier = Modifier.width(IntrinsicSize.Max),
                icon = orderInfo.icon,
                title = orderInfo.title,
                description = orderInfo.description
            )
        }
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