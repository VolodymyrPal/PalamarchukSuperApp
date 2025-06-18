package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.compose.FeatureTheme
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.daggerViewModel
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.AuthRepositoryImpl
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.OrderCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.StepperStatus
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.OrderPageState
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.OrderPageViewModel
import kotlinx.coroutines.launch


@Composable
internal fun OrdersPageRoot(
    modifier: Modifier = Modifier,
    viewModel: OrderPageViewModel = daggerViewModel<OrderPageViewModel>(
        factory = LocalBoneDependencies.current.viewModelFactory
    ),
    navController: NavController? = LocalNavController.current,
) {
    val orderPageState = viewModel.uiState.collectAsStateWithLifecycle()

    OrdersPage(
        modifier = modifier,
        navController = navController,
        event = viewModel::event,
        state = orderPageState
    )
}

@Composable
fun OrdersPage(
    modifier: Modifier = Modifier,
    navController: NavController? = LocalNavController.current,
    event: (OrderPageViewModel.OrderPageEvent) -> Unit,
    state: State<OrderPageState>,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            val httpClient = LocalBoneDependencies.current.httpClient //TODO
            val context = LocalContext.current //TODO
            val coroutineScope = rememberCoroutineScope() //TODO

            OrderStatisticCard(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .clickable { //TODO
                        val a = AuthRepositoryImpl( //TODO Test
                            httpClient = httpClient,
                            context = context
                        )
                        coroutineScope.launch {
                            a.logout()
                        }
                    },
                state = state,
            )
        }

        items(items = state.value.orders) {
            OrderCard(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                order = it,
                initialStatus = StepperStatus.entries.random(),
                internalPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            )
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
                value = title,
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val inProgress = ImageVector.vectorResource(R.drawable.in_progress)

                val inWork = stringResource(R.string.in_work)
                OrderStat(
                    modifier = Modifier.weight(0.3f),
                    icon = inProgress,
                    value = "${state.value.orderMetrics.inProgressOrders}",
                    label = inWork,
                    color = Color.Red
                )

                val completed = stringResource(R.string.completed)
                OrderStat(
                    modifier = Modifier.weight(0.3f),
                    icon = Icons.Default.Check,
                    value = "${state.value.orderMetrics.completedOrders}",
                    label = completed,
                    color = Color(0xFF55940E)
                )

                val weightPainter = ImageVector.vectorResource(R.drawable.kilogram)
                val finishFull = stringResource(R.string.finish_full)
                OrderStat(
                    modifier = Modifier.weight(0.3f),
                    icon = weightPainter,
                    value = "${(state.value.orderMetrics.totalOrderWeight * 100).toInt() / 100.0} т",
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
                    value = "$sumOrderTitle ",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleMedium
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                AppText(
                    value = "${state.value.orderMetrics.totalOrders}",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun OrderStat(
    icon: ImageVector,
    value: String,
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
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(30.dp)
            )
        }

        AppText(
            modifier = Modifier,
            value = value,
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.primary
        )

        AppText(
            value = label,
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OrderCardListPreview() {
    FeatureTheme(
        darkTheme = false
    ) {
        OrdersPage(
            event = {},
            state = remember {
                mutableStateOf(
                    OrderPageState()
                )
            },
            navController = null
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
    FeatureTheme(
        darkTheme = true
    ) {
        OrdersPage(
            event = {},
            state = remember {
                mutableStateOf(
                    OrderPageState()
                )
            },
            navController = null
        )
    }
}