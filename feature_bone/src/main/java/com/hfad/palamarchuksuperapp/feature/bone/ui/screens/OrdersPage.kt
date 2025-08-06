package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.compose.FeatureTheme
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.daggerViewModel
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.OrderCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.StepperStatus
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.OrderPageState
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.OrderPageViewModel
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
internal fun OrdersPageRoot(
    modifier: Modifier = Modifier,
    viewModel: OrderPageViewModel = daggerViewModel<OrderPageViewModel>(
        factory = LocalBoneDependencies.current.viewModelFactory
    ),
    navController: NavController? = LocalNavController.current,
) {
    val orderPageState = viewModel.uiState.collectAsStateWithLifecycle()
    val orderPaging = viewModel.orderPaging.collectAsLazyPagingItems()

    OrdersPage(
        modifier = modifier,
        navController = navController,
        event = viewModel::event,
        state = orderPageState,
        orderPaging = orderPaging
    )
}

@Composable
fun OrdersPage(
    modifier: Modifier = Modifier,
    navController: NavController? = LocalNavController.current,
    event: (OrderPageViewModel.OrderPageEvent) -> Unit,
    state: State<OrderPageState>,
    orderPaging: LazyPagingItems<Order>,
) {

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            OrderStatisticCard(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                state = state,
            )
        }
        item {
            if (orderPaging.loadState.refresh == LoadState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(0.5.dp)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer),
                    gapSize = 1.dp
                )
            }
        }

        items(
            orderPaging.itemCount,
            key = { index -> orderPaging.peek(index)?.id ?: index }) { index ->
            val item = orderPaging[index]
            if (item != null) {
                OrderCard(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp, top = 6.dp)
                        .animateItem(
                            fadeInSpec = tween(5000),
                            fadeOutSpec = tween(durationMillis = 500),
                            placementSpec = tween(500)
                        )
                    ,
                    order = item,
                    initialStatus = StepperStatus.entries.random(),
                    internalPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                )
            }
        }
    }
}

@Composable
private fun OrderStatisticCard(
    modifier: Modifier = Modifier,
    state: State<OrderPageState> = remember {
        mutableStateOf(OrderPageState())
    },
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 25.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val title = stringResource(R.string.order_statistic_title)
            AppText(
                value = title, appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center
                ), modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val inProgress = ImageVector.vectorResource(R.drawable.in_progress)

                val inWork = stringResource(R.string.in_work)
                OrderStat(
                    modifier = Modifier.weight(0.3f),
                    icon = inProgress,
                    value = state.value.orderMetrics.inProgressOrders,
                    label = inWork,
                    color = Color.Red
                )

                val completed = stringResource(R.string.completed)
                OrderStat(
                    modifier = Modifier.weight(0.3f),
                    icon = Icons.Default.Check,
                    value = state.value.orderMetrics.completedOrders,
                    label = completed,
                    color = Color(0xFF55940E)
                )

                val weightPainter = ImageVector.vectorResource(R.drawable.kilogram)
                val finishFull = stringResource(R.string.finish_full)
                OrderStat(
                    modifier = Modifier.weight(0.3f),
                    icon = weightPainter,
                    value = state.value.orderMetrics.totalOrderWeight,
                    label = finishFull,
                    color = Color.Blue
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary
            )

            val sumOrderTitle = stringResource(R.string.sum_orders)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    value = "$sumOrderTitle ", appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleMedium
                    ), color = MaterialTheme.colorScheme.primary
                )
                AnimatedValueText(
                    value = state.value.orderMetrics.totalOrderWeight,
                    textStyle = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    isInteger = true,
                    suffix = ""
                )
            }
        }
    }
}

@Composable
private fun OrderStat(
    icon: ImageVector,
    value: Number,
    label: String,
    modifier: Modifier = Modifier,
    color: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(shape = MaterialTheme.shapes.extraSmall)
                .background(color.copy(alpha = 0.2f)), contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(30.dp)
            )
        }

        AnimatedValueText(
            value = value,
            textStyle = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            isInteger = value is Int,
            suffix = if (value is Int) "шт" else "т"
        )
    }
}

@Composable
fun AnimatedValueText(
    value: Number,
    textStyle: TextStyle,
    fontWeight: FontWeight? = null,
    color: Color,
    isInteger: Boolean = true,
    suffix: String = "",
) {
    val currentValue = value.toFloat()

    val animatedValue by animateFloatAsState(
        targetValue = currentValue,
        animationSpec = tween(durationMillis = 750), // Можно настроить анимацию
        label = "AnimatedValueFloat"
    )
    val displayText = if (isInteger) {
        "${animatedValue.toInt()} $suffix"
    } else {
        "${"%.2f".format(animatedValue)} $suffix"
    }

    Box(
        modifier = Modifier.height(24.dp), contentAlignment = Alignment.Center
    ) {
        AppText(
            value = displayText, appTextConfig = appTextConfig(
                textStyle = textStyle, fontWeight = fontWeight
            ), color = color
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OrderCardListPreview() {
    val pagingData = remember {
        MutableStateFlow(
            PagingData.from(
                generateOrderItems()
            )
        )
    }.collectAsLazyPagingItems()

    FeatureTheme(
        darkTheme = false
    ) {
        OrdersPage(
            event = {}, state = remember {
                mutableStateOf(
                    OrderPageState()
                )
            }, navController = null,
            orderPaging = pagingData
        )
    }
}

@Preview
@Composable
private fun OrderStatPreview() {
    Column {
        FeatureTheme(
            darkTheme = false
        ) {
            OrderStatisticCard(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }
        FeatureTheme(
            darkTheme = true
        ) {
            OrderStatisticCard(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun OrdersPagePreview() {
    val pagingData = remember {
        MutableStateFlow(
            PagingData.from(
                generateOrderItems()
            )
        )
    }.collectAsLazyPagingItems()

    FeatureTheme(
        darkTheme = true
    ) {
        OrdersPage(
            event = {}, state = remember {
                mutableStateOf(
                    OrderPageState()
                )
            }, navController = null,
            orderPaging = pagingData
        )
    }
}