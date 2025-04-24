package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppIconInfoField
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.ui.OrderCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.Order
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.OrderService
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.ProductSaleItem
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.SaleStatus

@Composable
fun EnhancedProductSaleCard(
    saleItem: ProductSaleItem,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (saleItem.status) {
        SaleStatus.COMPLETED -> MaterialTheme.colorScheme.primary
        SaleStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiary
        SaleStatus.CREATED -> MaterialTheme.colorScheme.tertiary
        SaleStatus.DOCUMENT_PROCEED -> MaterialTheme.colorScheme.primary
        SaleStatus.CANCELED -> MaterialTheme.colorScheme.error
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
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header part
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    modifier = Modifier.weight(0.5f),
                    value = "Заявка №${saleItem.id}",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Unspecified
                    )
                )

                AppIconInfoField(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .weight(0.5f),
                    icon = rememberVectorPainter(Icons.Default.DateRange),
                    title = saleItem.status.toString()
                )
            }


            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            // Информация о компании и товаре
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
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        AppText(
                            value = saleItem.companyName,
                            appTextConfig = appTextConfig(
                                textStyle = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    AppText(
                        value = saleItem.productName,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodyMedium
                        ),
                        modifier = Modifier.padding(start = 22.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        AppText(
                            value = saleItem.cargoCategory,
                            appTextConfig = appTextConfig(
                                textStyle = MaterialTheme.typography.bodySmall
                            ),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                // Ценовая информация
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        AppText(
                            value = "${saleItem.totalAmount} грн",
                            appTextConfig = appTextConfig(
                                textStyle = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        AppText(
                            value = "НДС: ${saleItem.vatAmount} грн",
                            appTextConfig = appTextConfig(
                                textStyle = MaterialTheme.typography.bodySmall
                            ),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Детали продажи (даты, комиссия)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
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

            // Если есть предоплата, показываем информацию о заказе
            if (saleItem.prepayment && saleItem.order != null) {
                OrderInfoSection(order = saleItem.order)
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Нижняя панель с контактами и дополнительной информацией
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    AppText(
                        value = saleItem.customerName,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    )
                }

                Row {
                    IconButton(
                        onClick = { /* Действие при нажатии на кнопку звонка */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Позвонить",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { /* Действие при нажатии на кнопку сообщения */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Отправить сообщение",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { /* Действие при нажатии на кнопку дополнительной информации */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Дополнительные действия",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
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
                color = valueColor
            )
        }
    }
}

@Composable
private fun OrderInfoSection(order: Order) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Заголовок секции заказа с возможностью раскрытия
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                AppText(
                    value = "Информация о заказе №${order.num}",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Icon(
                imageVector = if (expanded)
                    Icons.Default.MailOutline else Icons.Default.Edit,
                contentDescription = if (expanded) "Скрыть детали" else "Показать детали",
                tint = MaterialTheme.colorScheme.secondary
            )
        }

        // Раскрываемый контент
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            OrderCard(
                modifier = Modifier.fillMaxWidth(),
                order = order,
                initialExpanded = true
            )
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
//                ),
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(12.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    // Основная информация
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        OrderDetailItem(
//                            label = "Бизнес-сущность",
//                            value = "#${order.businessEntityNum}"
//                        )
//
//                        OrderDetailItem(
//                            label = "Статус",
//                            value = when (order.status) {
//                                else -> "Неизвестно"
//                            }
//                        )
//                    }
//
//                    // Тип груза и содержимое
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        OrderDetailItem(
//                            label = "Тип груза",
//                            value = when (order.cargoType) {
//                                else -> "Неизвестно"
//                            }
//                        )
//
//                        OrderDetailItem(
//                            label = "Груз",
//                            value = order.cargo
//                        )
//                    }
//
//                    HorizontalDivider(
//                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
//                    )
//
//                    // Маршрут
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            modifier = Modifier.weight(1f)
//                        ) {
//                            AppText(
//                                value = "Отправление",
//                                appTextConfig = appTextConfig(
//                                    textStyle = MaterialTheme.typography.bodySmall
//                                ),
//                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
//                                    alpha =
//                                        0.7f
//                                )
//                            )
//                            AppText(
//                                value = order.departurePoint,
//                                appTextConfig = appTextConfig(
//                                    textStyle = MaterialTheme.typography.bodyMedium,
//                                    fontWeight = FontWeight.Medium
//                                )
//                            )
//                        }
//
//                        Icon(
//                            imageVector = Icons.Default.ArrowForward,
//                            contentDescription = null,
//                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
//                            modifier = Modifier.padding(horizontal = 8.dp)
//                        )
//
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            modifier = Modifier.weight(1f)
//                        ) {
//                            AppText(
//                                value = "Прибытие",
//                                appTextConfig = appTextConfig(
//                                    textStyle = MaterialTheme.typography.bodySmall
//                                ),
//                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
//                                    alpha =
//                                        0.7f
//                                )
//                            )
//                            AppText(
//                                value = order.destinationPoint,
//                                appTextConfig = appTextConfig(
//                                    textStyle = MaterialTheme.typography.bodyMedium,
//                                    fontWeight = FontWeight.Medium
//                                )
//                            )
//                        }
//                    }
//
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        OrderDetailItem(
//                            label = "Дата прибытия",
//                            value = order.arrivalDate
//                        )
//
//                        // Если это контейнерная перевозка, показываем номер контейнера
//                        OrderDetailItem(
//                            label = "Номер контейнера",
//                            value = order.containerNumber
//                        )
//                    }
//
//                    // Информация о менеджере
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 4.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Add,
//                            contentDescription = null,
//                            tint = MaterialTheme.colorScheme.secondary,
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        AppText(
//                            value = "Менеджер: ${order.manager}",
//                            appTextConfig = appTextConfig(
//                                textStyle = MaterialTheme.typography.bodyMedium
//                            )
//                        )
//                    }
//
//                    // Услуги, если они есть
//                    if (order.serviceList.isNotEmpty()) {
//                        HorizontalDivider(
//                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f),
//                            modifier = Modifier.padding(vertical = 4.dp)
//                        )
//
//                        AppText(
//                            value = "Услуги:",
//                            appTextConfig = appTextConfig(
//                                textStyle = MaterialTheme.typography.bodyMedium,
//                                fontWeight = FontWeight.Medium
//                            )
//                        )
//
//                        LazyRow(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp),
//                            contentPadding = PaddingValues(vertical = 4.dp)
//                        ) {
//                            items(order.serviceList) { service ->
//                                ServiceChip(service = service)
//                            }
//                        }
//                    }
//                }
//            }
        }
    }
}

@Composable
private fun OrderDetailItem(
    label: String,
    value: String,
) {
    Column {
        AppText(
            value = label,
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.bodySmall
            ),
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )
        AppText(
            value = value,
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun ServiceChip(service: OrderService) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (service.serviceType) {
                    else -> Icons.Default.ThumbUp
                },
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            AppText(
                value = service.serviceType.title,
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

// Пример генерации данных для тестирования
fun generateSampleProductSaleItems(): List<ProductSaleItem> {
    return listOf(
        ProductSaleItem(
            id = "SO-2024-001",
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
        ProductSaleItem(
            id = "SO-2024-002",
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
        ProductSaleItem(
            id = "SO-2024-003",
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
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleItems) { item ->
                EnhancedProductSaleCard(saleItem = item)
            }
        }
    }
}