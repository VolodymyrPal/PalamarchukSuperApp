package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import kotlin.random.Random

@Composable
fun SaleCard(
    saleItem: SaleOrder,
    modifier: Modifier = Modifier.Companion,
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
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.Companion.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Box(
                    modifier = Modifier.Companion
                        .padding(8.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                AppText(
                    modifier = Modifier.Companion.weight(1f),
                    value = stringResource(R.string.sale_card_title, saleItem.id),
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Companion.SemiBold,
                    )
                )

                AppIconInfoField(
                    modifier = Modifier.Companion
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
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.Top
            ) {
                Column(
                    modifier = Modifier.Companion.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.AccountBox,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.Companion
                                .padding(4.dp)
                                .size(20.dp)
                        )
                        AppText(
                            value = saleItem.companyName,
                            appTextConfig = appTextConfig(
                                textStyle = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Companion.Bold
                            )
                        )
                    }

                    if (saleItem.productName.isNotBlank()) {
                        AppText(
                            value = saleItem.productName,
                            appTextConfig = appTextConfig(
                                textStyle = MaterialTheme.typography.bodyMedium
                            ),
                            modifier = Modifier.Companion.padding(start = 22.dp)
                        )
                    }
                }

                AppIconInfoField(
                    modifier = Modifier.Companion,
                    description = "Сумма: ${saleItem.totalAmount.formatTrim()} грн",
                    title = "НДС: ${saleItem.vatAmount.formatTrim()} грн",
                    cardColors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = 0.dp
                )
            }

            Card(
                modifier = Modifier.Companion.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.Companion.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(
                        modifier = Modifier.Companion,
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        DetailItem(
                            icon = Icons.Default.DateRange,
                            label = "Дата заявки",
                            value = saleItem.requestDate
                        )

                        DetailItem(
                            icon = Icons.Default.Settings,
                            label = "Дата документов",
                            value = saleItem.documentDate
                        )
                    }

                    Column(
                        modifier = Modifier.Companion,
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        DetailItem(
                            icon = Icons.Default.Share,
                            label = "Комиссия",
                            value = "${saleItem.commissionPercent}%"
                        )

                        DetailItem(
                            icon = if (saleItem.prepayment) Icons.Default.Check else Icons.Default.Close,
                            label = "Предоплата",
                            value = if (saleItem.prepayment) "Да" else "Нет",
                            valueColor = if (saleItem.prepayment)
                                MaterialTheme.colorScheme.tertiary else
                                MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            if (saleItem.order != null) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
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
    Row(verticalAlignment = Alignment.Companion.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.Companion.size(16.dp)
        )
        Spacer(modifier = Modifier.Companion.width(4.dp))
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
                    fontWeight = FontWeight.Companion.Medium
                ),
                color = valueColor
            )
        }
    }
}

@Composable
private fun OrderInfoSection(
    modifier: Modifier = Modifier.Companion,
    order: Order,
) {
    var expanded = remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .clip(CircleShape)
                .clickable { expanded.value = !expanded.value }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.Companion
                    .padding(start = 12.dp)
                    .size(20.dp)
            )
            AppText(
                value = "Информация о заказе №${order.num}",
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Companion.Medium
                ),
                color = MaterialTheme.colorScheme.secondary
            )
            Icon(
                imageVector = if (expanded.value)
                    Icons.Outlined.KeyboardArrowUp else Icons.Outlined.ArrowDropDown,
                contentDescription = if (expanded.value) "Скрыть детали" else "Показать детали",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.Companion.padding(end = 8.dp)
            )
        }

        // Раскрываемый контент
        AnimatedVisibility(
            visible = expanded.value,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            OrderCard(
                modifier = Modifier.Companion.fillMaxWidth(),
                order = order,
                initialExpanded = true
            )
        }
    }
}

// Пример генерации данных для тестирования
fun generateSampleProductSaleItems(): List<SaleOrder> {
    return listOf(
        SaleOrder(
            id = Random.Default.nextInt(10000, 99999),
            productName = "Офисная мебель",
            cargoCategory = "Мебель",
            companyName = "ООО «Офис Плюс»",
            quantity = 10,
            price = 2500,
            totalAmount = 2500,
            vatAmount = 5000.0,
            customerName = "Иванов И.И.",
            status = SaleStatus.COMPLETED,
            requestDate = "10.03.2024",
            documentDate = "15.03.2024",
            commissionPercent = 5.0,
            prepayment = true,
            order = Order(
                id = 1,
                businessEntityNum = 4321,
                num = 48756,
                destinationPoint = "Киев",
                arrivalDate = "20.04.2024",
                departurePoint = "Шанхай",
                cargo = "Офисная мебель",
                manager = "Петров В.П. +380633887542"
            )
        ),
        SaleOrder(
            id = Random.Default.nextInt(10000, 99999),
            productName = "Электроника",
            cargoCategory = "Техника",
            companyName = "ТОВ «Техноимпорт»",
            quantity = 50,
            price = 1200,
            totalAmount = 60000,
            vatAmount = 12000.0,
            customerName = "Смирнов А.В.",
            status = SaleStatus.IN_PROGRESS,
            requestDate = "05.04.2024",
            documentDate = "10.04.2024",
            commissionPercent = 3.5,
            prepayment = false,
            order = null
        ),
        SaleOrder(
            id = Random.Default.nextInt(10000, 99999),
            productName = "Строительные материалы",
            cargoCategory = "Стройматериалы",
            companyName = "ООО «СтройМир»",
            quantity = 200,
            price = 450,
            totalAmount = 90000,
            vatAmount = 18000.0,
            customerName = "Ковалев Д.И.",
            status = SaleStatus.CREATED,
            requestDate = "15.04.2024",
            documentDate = "20.04.2024",
            commissionPercent = 2.0,
            prepayment = true,
            order = Order(
                id = 2,
                businessEntityNum = 6245,
                num = 46123,
                destinationPoint = "Одесса",
                arrivalDate = "12.05.2024",
                containerNumber = "40HC-9876543",
                departurePoint = "Гуанчжоу",
                cargo = "Строительные материалы",
                manager = "Сидорова Е.М. +380671234567"
            )
        )
    )
}

// Пример использования
@Composable
@Preview
fun SalesPageExample() {
    MaterialTheme {
        val sampleItems = generateSampleProductSaleItems()

        LazyColumn(
            modifier = Modifier.Companion.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleItems) { item ->
                SaleCard(saleItem = item)
            }
        }
    }
}