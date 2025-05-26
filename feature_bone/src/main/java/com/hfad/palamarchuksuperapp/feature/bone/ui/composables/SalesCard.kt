package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.FeatureTheme
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppIconInfoField
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.formatTrim
import com.hfad.palamarchuksuperapp.core.ui.theme.Status
import com.hfad.palamarchuksuperapp.core.ui.theme.statusColor
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateSaleOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.times
import com.hfad.palamarchuksuperapp.feature.bone.ui.theme.appRippleEffect

@Composable
fun SaleCard(
    saleItem: SaleOrder,
    modifier: Modifier = Modifier,
    internalPadding : PaddingValues = PaddingValues()
) {
    val statusColor = when (saleItem.status) {
        SaleStatus.COMPLETED -> statusColor(Status.DONE)
        SaleStatus.IN_PROGRESS -> statusColor(Status.IN_PROGRESS)
        SaleStatus.CREATED -> statusColor(Status.CREATED)
        SaleStatus.DOCUMENT_PROCEED -> statusColor(Status.PROCEED)
        SaleStatus.CANCELED -> statusColor(Status.CANCELED)
    }

    val statusText = when (saleItem.status) {
        SaleStatus.COMPLETED -> stringResource(R.string.sale_status_completed)
        SaleStatus.CREATED -> stringResource(R.string.sale_status_created)
        SaleStatus.IN_PROGRESS -> stringResource(R.string.sale_status_in_progress)
        SaleStatus.DOCUMENT_PROCEED -> stringResource(R.string.sale_status_document_proceed)
        SaleStatus.CANCELED -> stringResource(R.string.sale_status_canceled)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.extraSmall,
        border = BorderStroke(1.dp, colorScheme.outline),
//        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(internalPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                AppText(
                    modifier = Modifier.weight(1f),
                    value = stringResource(R.string.sale_card_title, saleItem.id),
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                )

                AppIconInfoField(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    title = statusText,
                    cardColors = CardDefaults.cardColors(
                        containerColor = statusColor.copy(alpha = 0.2f),
                        contentColor = statusColor
                    ),
                    elevation = 0.dp
                )
            }


            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.AccountBox,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier
                                .padding(4.dp)
                                .size(20.dp)
                        )
                        AppText(
                            value = saleItem.companyName,
                            appTextConfig = appTextConfig(
                                textStyle = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    if (saleItem.productName.isNotBlank()) {
                        AppText(
                            value = saleItem.productName,
                            appTextConfig = appTextConfig(
                                textStyle = MaterialTheme.typography.bodyMedium
                            ),
                            modifier = Modifier.padding(start = 22.dp)
                        )
                    }
                }

                val vat = saleItem.amountCurrency * saleItem.vat

                AppIconInfoField(
                    modifier = Modifier,
                    description = stringResource(
                        R.string.sale_sum_currency,
                        saleItem.amountCurrency.amount.formatTrim(),
                        saleItem.amountCurrency.currency.name.lowercase()
                    ),
                    title = stringResource(
                        R.string.vat_amount_currency,
                        vat.amount.formatTrim(),
                        " ${vat.currency.name.lowercase()}"
                    ),
                    cardColors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = 0.dp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DetailItem(
                            icon = Icons.Default.DateRange,
                            label = stringResource(R.string.order_sale_date),
                            value = saleItem.requestDate
                        )

                        DetailItem(
                            icon = Icons.Default.Share,
                            label = stringResource(R.string.order_sale_commission),
                            value = "${saleItem.commissionPercent}%"
                        )
                    }

                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DetailItem(
                            icon = Icons.Default.DateRange,
                            label = stringResource(R.string.order_sale_doc_date),
                            value = saleItem.documentDate
                        )

                        DetailItem(
                            icon = if (saleItem.prepayment) Icons.Default.Check else Icons.Default.Close,
                            label = stringResource(R.string.order_sale_prepayment),
                            value = if (saleItem.prepayment) stringResource(R.string.yes) else stringResource(
                                R.string.no
                            ),
                            valueColor = if (saleItem.prepayment)
                                colorScheme.tertiary else
                                colorScheme.error.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            if (saleItem.order != null) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
                OrderInfoSection(order = saleItem.order)
            }
        }
    }
}

@Composable
private fun DetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            AppText(
                value = label,
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.bodySmall
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            AppText(
                value = value,
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                ),
            )
        }
    }
}

@Composable
private fun OrderInfoSection(
    modifier: Modifier = Modifier,
    order: Order,
) {
    val expanded = remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .clickable(
                    interactionSource = null,
                    indication = appRippleEffect(),
                ) {
                    expanded.value = !expanded.value
                }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(30.dp)
            )
            AppText(
                value = "Информация о заказе №${order.num}",
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                ),
            )
            Icon(
                imageVector = if (expanded.value)
                    Icons.Outlined.KeyboardArrowUp else Icons.Outlined.ArrowDropDown,
                contentDescription = if (expanded.value) "Скрыть детали" else "Показать детали",
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        AnimatedVisibility(
            visible = expanded.value,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            OrderCard(
                modifier = Modifier.fillMaxWidth(),
                order = order,
                initialExpanded = true
            )
        }
    }
}

@Composable
@Preview
fun SalesPageExample() {
    FeatureTheme {
        val sampleItems = generateSaleOrderItems()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleItems) { item ->
                SaleCard(saleItem = item)
            }
        }
    }
}

@Composable
@Preview
fun SalesPageNightExample() {
    FeatureTheme(
        darkTheme = true
    ) {
        val sampleItems = generateSaleOrderItems()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleItems) { item ->
                SaleCard(saleItem = item)
            }
        }
    }
}