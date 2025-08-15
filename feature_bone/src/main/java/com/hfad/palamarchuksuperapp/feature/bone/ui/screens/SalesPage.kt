package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.hfad.palamarchuksuperapp.core.ui.composables.formatTrim
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.core.ui.theme.Status
import com.hfad.palamarchuksuperapp.core.ui.theme.statusColor
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import com.hfad.palamarchuksuperapp.feature.bone.ui.animation.animatedScaleIn
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.SaleCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.SalesPageState
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.SalesPageViewModel

@Composable
internal fun SalesPageRoot(
    modifier: Modifier = Modifier,
    viewModel: SalesPageViewModel = daggerViewModel<SalesPageViewModel>(
        factory = LocalBoneDependencies.current.viewModelFactory
    ),
    navController: NavController? = LocalNavController.current,
) {
    val salesPageState = viewModel.uiState.collectAsStateWithLifecycle()
    val salesPaging = viewModel.salesPaging.collectAsLazyPagingItems()

    SalesPage(
        modifier = modifier,
        navController = navController,
        event = viewModel::event,
        state = salesPageState,
        salesPaging = salesPaging
    )
}

@Composable
fun SalesPage(
    modifier: Modifier = Modifier,
    navController: NavController? = LocalNavController.current,
    event: (SalesPageViewModel.SalesPageEvent) -> Unit,
    state: State<SalesPageState>,
    salesPaging: LazyPagingItems<SaleOrder>,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            SalesStatisticsCard(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                salesStatistics = state.value.salesStatistics
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val lookIcon = remember { Icons.Default.Search }
                val searchText = remember { mutableStateOf("") }

                AppOutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer
                        ),
                    value = state.value.searchQuery,
                    onValueChange = { event(SalesPageViewModel.SalesPageEvent.Search(it)) },
                    outlinedTextConfig = appEditOutlinedTextConfig(
                        leadingIcon = {
                            Icon(
                                imageVector = lookIcon,
                                contentDescription = "Filter",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            val isLoading = salesPaging.loadState.refresh == LoadState.Loading

                            AnimatedContent(
                                targetState = isLoading,
                                transitionSpec = {
                                    fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                                },
                                label = "loading_or_send_icon"
                            ) { loading ->
                                if (loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    IconButton(
                                        modifier = Modifier.size(28.dp),
                                        onClick = {
                                            salesPaging.refresh()
                                            searchText.value = ""
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = "Отправить",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    )
                )

                val statusIcon = Icons.Default.Check

                AnimatedContent(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    targetState = statusIcon,
                    transitionSpec = {
                        (fadeIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        ) + scaleIn(
                            initialScale = 0.8f,
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        ) + slideInVertically(
                            initialOffsetY = { it / 4 },
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        )).togetherWith(
                            fadeOut(
                                animationSpec = tween(
                                    durationMillis = 200,
                                    easing = FastOutLinearInEasing
                                )
                            ) + scaleOut(
                                targetScale = 1.2f,
                                animationSpec = tween(
                                    durationMillis = 200,
                                    easing = FastOutLinearInEasing
                                )
                            ) + slideOutVertically(
                                targetOffsetY = { -it / 4 },
                                animationSpec = tween(
                                    durationMillis = 200,
                                    easing = FastOutLinearInEasing
                                )
                            )
                        )
                    },
                    label = "status_filter_icon_animation"
                ) { targetIcon ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                animateColorAsState(
                                    targetValue = when (state.value.saleStatusFilter) {
                                        SaleStatus.COMPLETED -> MaterialTheme.colorScheme.surfaceVariant
                                        else -> Color.Transparent
                                    },
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = FastOutSlowInEasing
                                    ),
                                    label = "background_color_animation"
                                ).value
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    val nextStatus = when (state.value.saleStatusFilter) {
                                        SaleStatus.COMPLETED -> null
                                        else -> SaleStatus.COMPLETED
                                    }
                                    event(
                                        SalesPageViewModel.SalesPageEvent.FilterSaleStatus(
                                            nextStatus
                                        )
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = targetIcon,
                            contentDescription = when (state.value.saleStatusFilter) {
                                SaleStatus.COMPLETED -> "Фильтр: завершенные продажи"
                                else -> "Фильтр продаж"
                            },
                            tint = animateColorAsState(
                                targetValue = when (state.value.saleStatusFilter) {
                                    SaleStatus.COMPLETED -> MaterialTheme.colorScheme.onSurfaceVariant
                                    else -> MaterialTheme.colorScheme.outline
                                },
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing
                                ),
                                label = "icon_color_animation"
                            ).value,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        items(
            salesPaging.itemCount,
            key = salesPaging.itemKey { it.id }
        ) { index ->
            val item = salesPaging[index]
            if (item != null) {
                SaleCard(
                    saleItem = item,
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp, top = 6.dp)
                        .animatedScaleIn(),
                    internalPadding = PaddingValues(horizontal = 20.dp, vertical = 25.dp)
                )
            }
        }
    }
}

@Composable
fun SalesStatisticsCard(
    modifier: Modifier = Modifier,
    salesStatistics: com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 25.dp, horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppText(
                value = stringResource(R.string.sales_statistics),
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 4.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val totalAmount = salesStatistics.totalSalesAmount.amount.formatTrim(0)
                val totalAmountStr = totalAmount + " " + salesStatistics.totalSalesAmount.iconChar
                SalesStat(
                    modifier = Modifier.weight(0.33f),
                    icon = painterResource(R.drawable.money_pack),
                    value = totalAmountStr,
                    label = stringResource(R.string.summ_sales),
                    color = salesStatistics.totalSalesAmount.color
                )

                val totalSalesNds = salesStatistics.totalSalesNdsAmount.amount.formatTrim(0)
                val totalSalesNdsStr =
                    totalSalesNds + " " + salesStatistics.totalSalesNdsAmount.iconChar

                SalesStat(
                    modifier = Modifier.weight(0.33f),
                    icon = rememberVectorPainter(Icons.Default.Search),
                    value = totalSalesNdsStr,
                    label = stringResource(R.string.total_nds),
                    color = MaterialTheme.colorScheme.primary
                )

                SalesStat(
                    modifier = Modifier.weight(0.33f),
                    icon = rememberVectorPainter(Icons.Default.Check),
                    value = salesStatistics.totalBuyers.toString(),
                    label = stringResource(R.string.total_sale_buyer),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun SalesStat(
    icon: Painter,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(shape = MaterialTheme.shapes.small)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
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
                textAlign = TextAlign.Center
            )
        )

        AppText(
            value = label,
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            ),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
        )
    }
}