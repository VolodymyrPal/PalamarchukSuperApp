package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppOutlinedTextField
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appEditOutlinedTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.formatTrim
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.daggerViewModel
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import com.hfad.palamarchuksuperapp.feature.bone.ui.animation.animatedScaleIn
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.SaleCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.PaymentsPageViewModel
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
    val shownQuery = remember { mutableIntStateOf(99) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            SalesStatisticsCard(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
                salesStatistics = state.value.salesStatistics
            )
        }
        item {
            val isLoading = salesPaging.loadState.refresh == LoadState.Loading

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
                        onValueChange = { event(SalesPageViewModel.SalesPageEvent.Search(it)) },
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
                            .wrapContentWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(SaleStatus.entries.toTypedArray()) { status ->
                            FilterChip(
                                onClick = {
                                    event.invoke(
                                        SalesPageViewModel.SalesPageEvent.FilterSaleStatus(
                                            status
                                        )
                                    ) //TODO add different classes check for status with list
                                },
                                label = {
                                    Text(text = status.displayName)
                                },
                                selected = state.value.saleStatusFilter.contains(status),
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 4.dp),
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