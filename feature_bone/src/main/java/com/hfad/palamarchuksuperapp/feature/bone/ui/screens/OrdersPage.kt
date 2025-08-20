package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.paging.compose.itemKey
import com.example.compose.FeatureTheme
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppOutlinedTextField
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appEditOutlinedTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.daggerViewModel
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.ui.animation.animatedScaleIn
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.OrderCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.OrderPageState
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.OrderPageViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersPage(
    modifier: Modifier = Modifier,
    navController: NavController? = LocalNavController.current,
    event: (OrderPageViewModel.OrderPageEvent) -> Unit,
    state: State<OrderPageState>,
    orderPaging: LazyPagingItems<Order>,
) {
    val shownQuery = remember { mutableIntStateOf(99) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            OrderStatisticCard(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
                state = state.value.orderMetrics,
            )
        }

        item {
            val isLoading = orderPaging.loadState.refresh == LoadState.Loading

            AnimatedContent(
                targetState = isLoading,
                transitionSpec = {
                    fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                },
                label = "loading_or_send_icon"
            ) { loading ->
                if (loading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitleQueryField(
                    modifier = Modifier,
                    sectionIcon = Icons.Default.Search,
                    onClick = {
                        shownQuery.intValue = if (shownQuery.intValue == 0) 99 else 0
                    },
                    expanded = shownQuery.intValue == 0
                )

                TitleQueryField(
                    modifier = Modifier,
                    sectionIcon = Icons.Default.DateRange,
                    onClick = {
                        shownQuery.intValue = if (shownQuery.intValue == 1) 99 else 1
                    },
                    expanded = shownQuery.intValue == 1
                )

                TitleQueryField(
                    modifier = Modifier,
                    onClick = {
                        shownQuery.intValue = if (shownQuery.intValue == 2) 99 else 2
                    },
                    expanded = shownQuery.intValue == 2,
                    sectionIcon = Icons.Default.Check
                )
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = shownQuery.intValue == 0,
                    enter = fadeIn(animationSpec = tween(300)) +
                            slideInVertically(animationSpec = tween(300)) { -it } +
                            expandVertically(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(250)) +
                            slideOutVertically(animationSpec = tween(250)) { -it } +
                            shrinkVertically(animationSpec = tween(250))
                ) {
                    AppOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                            )
                            .padding(4.dp),
                        value = state.value.searchQuery,
                        onValueChange = { event(OrderPageViewModel.OrderPageEvent.Search(it)) },
                        placeholderRes = R.string.orders_query_example,
                    )
                }

                AnimatedVisibility(
                    visible = shownQuery.intValue == 1,
                    enter = fadeIn(animationSpec = tween(300)) +
                            slideInVertically(animationSpec = tween(300)) { -it } +
                            expandVertically(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(250)) +
                            slideOutVertically(animationSpec = tween(250)) { -it } +
                            shrinkVertically(animationSpec = tween(250))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppOutlinedTextField(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    // Обработка клика - открыть календарь "от"
                                }
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                ),
                            value = "12.03.2023",
                            onValueChange = { },
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                textAlign = TextAlign.Center,
                            ),
                            outlinedTextConfig = appEditOutlinedTextConfig()
                        )

                        Text(
                            text = "—",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )

                        AppOutlinedTextField(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    // Обработка клика - открыть календарь "до"
                                }
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                ),
                            value = "12.04.2023",
                            onValueChange = { },
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                textAlign = TextAlign.Center,
                            ),
                            outlinedTextConfig = appEditOutlinedTextConfig()
                        )
                    }
                }

                val scope = rememberCoroutineScope()
                val listState = rememberLazyListState()

                val hScroll = rememberScrollableState { delta ->
                    scope.launch { listState.scrollBy(-delta) }
                    delta
                }

                AnimatedVisibility(
                    visible = shownQuery.intValue == 2,
                    enter = fadeIn(animationSpec = tween(300)) +
                            slideInVertically(animationSpec = tween(300)) { -it } +
                            expandVertically(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(250)) +
                            slideOutVertically(animationSpec = tween(250)) { -it } +
                            shrinkVertically(animationSpec = tween(250))
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .scrollable(
                                state = hScroll,
                                orientation = Orientation.Horizontal,
                                flingBehavior = ScrollableDefaults.flingBehavior(),
                                reverseDirection = false
                            )
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                            .padding(8.dp)
                            ,
                        userScrollEnabled = false,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        state = listState
                    ) {
                        items(OrderStatus.entries.toTypedArray()) { status ->
                            FilterChip(
                                onClick = {
                                    event.invoke(
                                        OrderPageViewModel.OrderPageEvent.FilterOrderStatus(
                                            status
                                        )
                                    ) //TODO add different classes check for status with list
                                },
                                label = {
                                    Text(text = status.displayName)
                                },
                                selected = state.value.orderStatusFilter.contains(status),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        items(
            orderPaging.itemCount,
            key = orderPaging.itemKey { it.id }
        ) { index ->
            val item = orderPaging[index]
            if (item != null) {
                OrderCard(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .animatedScaleIn(),
                    order = item,
                    currentStep = Random.nextInt(7), //TODO better logic for current step
                    internalPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                )
            }
        }
    }
}

@Composable
fun TitleQueryField(
    modifier: Modifier = Modifier,
    sectionIcon: ImageVector = Icons.Default.ArrowDropDown,
    expanded: Boolean = false,
    onClick: () -> Unit,
) {
    SectionTitleIcon(
        title = stringResource(R.string.order_statistic_title),
        icon = sectionIcon,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .background(
                animateColorAsState(
                    targetValue = when (expanded) {
                        true -> MaterialTheme.colorScheme.surfaceVariant
                        else -> Color.Transparent
                    },
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = FastOutSlowInEasing
                    ),
                    label = "background_color_animation"
                ).value
            )
            .padding(4.dp),
        tint = animateColorAsState(
            targetValue = when (expanded) {
                true -> MaterialTheme.colorScheme.onSurfaceVariant
                else -> MaterialTheme.colorScheme.outline
            },
            animationSpec = tween(
                durationMillis = 200,
                easing = FastOutSlowInEasing
            ),
            label = "icon_color_animation"
        ).value
    )
}

@Composable
fun SectionTitleIcon(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector = Icons.Default.ArrowDropDown,
    tint: Color = MaterialTheme.colorScheme.outline,
) {
    Icon(
        modifier = modifier,
        imageVector = icon,
        contentDescription = title,
        tint = tint
    )
}

@Composable
private fun OrderStatisticCard(
    modifier: Modifier = Modifier,
    state: OrderStatistics = OrderStatistics(),
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
                    value = state.inProgressOrders,
                    label = inWork,
                    color = Color.Red
                )

                val completed = stringResource(R.string.completed)
                OrderStat(
                    modifier = Modifier.weight(0.3f),
                    icon = Icons.Default.Check,
                    value = state.completedOrders,
                    label = completed,
                    color = Color(0xFF55940E)
                )

                val weightPainter = ImageVector.vectorResource(R.drawable.kilogram)
                val finishFull = stringResource(R.string.finish_full)
                OrderStat(
                    modifier = Modifier.weight(0.3f),
                    icon = weightPainter,
                    value = state.totalOrderWeight,
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
                    value = state.totalOrderWeight,
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