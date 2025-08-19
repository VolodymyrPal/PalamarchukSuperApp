package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppIconInfoField
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppOutlinedTextField
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appEditOutlinedTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.formatTrim
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.daggerViewModel
import com.hfad.palamarchuksuperapp.core.ui.theme.Status
import com.hfad.palamarchuksuperapp.core.ui.theme.statusColor
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.ui.animation.animatedScaleIn
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.EqualWidthFlowRow
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.OrderPageViewModel
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.PaymentsPageState
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.PaymentsPageViewModel

@Composable
internal fun PaymentsPageRoot(
    modifier: Modifier = Modifier,
    viewModel: PaymentsPageViewModel = daggerViewModel<PaymentsPageViewModel>(
        factory = LocalBoneDependencies.current.viewModelFactory
    ),
    navController: NavController? = LocalNavController.current,
) {
    val paymentsPageState = viewModel.uiState.collectAsStateWithLifecycle()
    val paymentsPaging = viewModel.paymentsPaging.collectAsLazyPagingItems()

    PaymentsPage(
        modifier = modifier,
        navController = navController,
        event = viewModel::event,
        state = paymentsPageState,
        paymentsPaging = paymentsPaging
    )
}

@Composable
fun PaymentsPage(
    modifier: Modifier = Modifier,
    navController: NavController? = LocalNavController.current,
    event: (PaymentsPageViewModel.PaymentsPageEvent) -> Unit,
    state: State<PaymentsPageState>,
    paymentsPaging: LazyPagingItems<PaymentOrder>,
) {
    val shownQuery = remember { mutableIntStateOf(99) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            PaymentsStatisticsCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 12.dp),
                paymentStatistic = state.value.paymentStatistic
            )
        }
        item {
            val isLoading = paymentsPaging.loadState.refresh == LoadState.Loading

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
                        onValueChange = { event(PaymentsPageViewModel.PaymentsPageEvent.Search(it)) },
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
                        items(PaymentStatus.entries.toTypedArray()) { status ->
                            FilterChip(
                                onClick = {
                                    event.invoke(
                                        PaymentsPageViewModel.PaymentsPageEvent.FilterPaymentStatus(
                                            status
                                        )
                                    ) //TODO add different classes check for status with list
                                },
                                label = {
                                    Text(text = status.displayName)
                                },
                                selected = state.value.paymentStatusFilter.contains(status),
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
            paymentsPaging.itemCount,
            key = paymentsPaging.itemKey { it.id }
        ) { index ->
            val item = paymentsPaging[index]
            if (item != null) {
                PaymentCard(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .animatedScaleIn(),
                    payment = item,
                    internalPadding = PaddingValues(horizontal = 20.dp, vertical = 25.dp)
                )
            }
        }
    }
}

@Composable
fun PaymentsStatisticsCard(
    paymentStatistic: PaymentStatistic = PaymentStatistic(),
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppText(
                value = stringResource(R.string.payment_statistic_title),
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

            val gridItems = paymentStatistic.paymentsByCurrency

            EqualWidthFlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            ) {
                gridItems.forEach { currencyAmount ->
                    CurrencyStat(
                        modifier = Modifier.width(IntrinsicSize.Max),
                        amountCurrency = currencyAmount
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 4.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    modifier = Modifier,
                    title = stringResource(R.string.total_payment),
                    value = paymentStatistic.totalPayment.toString(),
                    icon = rememberVectorPainter(Icons.Outlined.Star)
                )

                StatItem(
                    modifier = Modifier,
                    title = stringResource(R.string.factory_payment_amount),
                    value = "${paymentStatistic.totalReceiver}",
                    icon = painterResource(R.drawable.truck)
                )

                StatItem(
                    modifier = Modifier,
                    title = stringResource(R.string.avg_payment_due_date),
                    value = "${paymentStatistic.daysToSend} day",
                    icon = rememberVectorPainter(Icons.Outlined.DateRange),
                )
            }
        }
    }
}

@Composable
fun CurrencyStat(
    amountCurrency: AmountCurrency,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(amountCurrency.color.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            AppText(
                value = amountCurrency.currency.toString(),
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                ),
                color = amountCurrency.color,
                modifier = Modifier.padding(4.dp)
            )
        }

        AppText(
            value = amountCurrency.amount.formatTrim() + " " + amountCurrency.iconChar,
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun StatItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: Painter,
) {
    Row(
        modifier = modifier.width(100.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
        )

        Column(
            modifier = Modifier
                .padding(vertical = 2.dp),
            verticalArrangement = Arrangement.Center
        ) {
            AppText(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(IntrinsicSize.Min),
                value = value,
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                ),
            )

            AppText(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(IntrinsicSize.Min),
                value = title,
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                ),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
fun PaymentCard(
    payment: PaymentOrder,
    modifier: Modifier = Modifier,
    internalPadding: PaddingValues = PaddingValues(),
) {
    val statusColor = when (payment.status) {
        PaymentStatus.PAID -> statusColor(Status.DONE)
        PaymentStatus.PENDING -> statusColor(Status.IN_PROGRESS)
        PaymentStatus.OVERDUE -> statusColor(Status.OVERDUE)
    }

    val statusText = when (payment.status) {
        PaymentStatus.PAID -> stringResource(R.string.payment_paid)
        PaymentStatus.PENDING -> stringResource(R.string.payment_pending)
        PaymentStatus.OVERDUE -> stringResource(R.string.payment_overdue)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.extraSmall,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
    ) {
        Column(
            modifier = Modifier.padding(internalPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    value = stringResource(R.string.payment_payment_card_title, payment.id),
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleSmall
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    AppText(
                        modifier = Modifier.padding(8.dp),
                        value = statusText,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        ),
                        color = statusColor
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                SelectionContainer {
                    AppText(
                        value = payment.fullPrice.amount.formatTrim(),
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        ),
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(payment.amountCurrency.color.copy(alpha = 0.1f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    AppText(
                        value = payment.amountCurrency.currency.toString(),
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        ),
                        color = payment.amountCurrency.color
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 4.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary
            )

            AppIconInfoField(
                modifier = Modifier.fillMaxWidth(),
                icon = painterResource(R.drawable.factory_icon),
                iconSize = 30.dp,
                title = stringResource(R.string.factory),
                description = payment.factory,
                cardColors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            )

            AppIconInfoField(
                modifier = Modifier.fillMaxWidth(),
                icon = painterResource(R.drawable.product_icon),
                iconSize = 30.dp,
                title = stringResource(R.string.cargo),
                description = payment.productType,
                cardColors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            )

            AppIconInfoField(
                modifier = Modifier.fillMaxWidth(),
                icon = painterResource(R.drawable.usd_sign),
                iconSize = 30.dp,
                title = "To send",
                description = "${payment.amountCurrency.amount.formatTrim()} ${payment.fullPrice.iconChar}",
                cardColors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            )

            val paymentCommission = StringBuilder().apply {
                append("${payment.commission}%")
                if (payment.paymentPrice.amount != 0.0f) {
                    append(" + ${payment.paymentPrice.amount.formatTrim()}")
                    append(" ${payment.paymentPrice.iconChar}")
                }
            }.toString()

            AppIconInfoField(
                modifier = Modifier.fillMaxWidth(),
                icon = painterResource(R.drawable.money_pack),
                iconSize = 30.dp,
                title = stringResource(R.string.payment_commission),
                description = paymentCommission,
                cardColors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 4.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary
            )

            // Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    AppText(
                        value = "Дата платежа:",
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodySmall
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    AppText(
                        value = payment.paymentDate,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodyMedium
                        ),
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    AppText(
                        value = "Срок оплаты:",
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodySmall
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    AppText(
                        value = payment.dueDate,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodyMedium
                        ),
                        color = if (payment.status == PaymentStatus.OVERDUE)
                            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}