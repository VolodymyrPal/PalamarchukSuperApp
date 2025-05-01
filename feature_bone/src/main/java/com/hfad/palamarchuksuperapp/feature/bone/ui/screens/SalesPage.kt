package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.formatTrim
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.core.ui.theme.Status
import com.hfad.palamarchuksuperapp.core.ui.theme.statusColor
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.Currency
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.ProductSaleItem
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.SaleStatus
import kotlin.random.Random

@Composable
fun SalesPage(
    modifier: Modifier = Modifier,
    state: SalesPageState = SalesPageState(),
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                vertical = 12.dp
            )
        ) {
            item {
                SalesStatisticsCard(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    salesStatistics = state.salesStatistics
                )
            }
            items(state.salesItems) { item ->
                SaleCard(
                    saleItem = item,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                )
//                ProductSaleCard(saleItem = item)
            }
        }
    }
}

@Composable
fun SalesStatisticsCard(
    modifier: Modifier = Modifier,
    salesStatistics: SalesStatistics,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppText(
                value = "Статистика продаж",
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f)
            )

            // Основные показатели в виде иконок с числами
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val totalAmount = salesStatistics.totalSalesAmount.amount.formatTrim(0)
                val totalAmountStr = totalAmount + " " + salesStatistics.totalSalesAmount.iconChar
                SalesStat(
                    icon = painterResource(R.drawable.money_pack),
                    value = totalAmountStr,
                    label = stringResource(R.string.summ_sales),
                    color = salesStatistics.totalSalesAmount.color
                )

                val totalSalesNds = salesStatistics.totalSalesNdsAmount.amount.formatTrim(0)
                val totalSalesNdsStr =
                    totalSalesNds + " " + salesStatistics.totalSalesNdsAmount.iconChar

                SalesStat(
                    icon = rememberVectorPainter(Icons.Default.Search),
                    value = totalSalesNdsStr,
                    label = stringResource(R.string.total_nds),
                    color = statusColor(Status.entries.random())
                )

                SalesStat(
                    icon = rememberVectorPainter(Icons.Default.Info),
                    value = salesStatistics.totalBuyers.toString(),
                    label = stringResource(R.string.total_sale_buyer),
                    color = statusColor(Status.entries.random())
                )
            }

//            HorizontalDivider(
//                modifier = Modifier.padding(vertical = 4.dp),
//                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f)
//            )
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                AppText(
//                    value = "Общая выручка: ",
//                    appTextConfig = appTextConfig(
//                        textStyle = MaterialTheme.typography.titleMedium
//                    )
//                )
//                AppText(
//                    value = "1,456,780 грн",
//                    appTextConfig = appTextConfig(
//                        textStyle = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold
//                    ),
//                    color = MaterialTheme.colorScheme.primary
//                )
//            }
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
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        AppText(
            modifier = Modifier,
            value = value,
            appTextConfig = appTextConfig(
                textStyle = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
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

@Composable
fun ProductSaleCard(
    saleItem: ProductSaleItem,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (saleItem.status) {
        else  -> MaterialTheme.colorScheme.primary
//        SaleStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiary
//        SaleStatus.DOCUMENT_PROCEED -> MaterialTheme.colorScheme.error
    }

    val statusText = when (saleItem.status) {
        else -> "Завершено"
//        SaleStatus.IN_PROGRESS -> "Доставляется"
//        SaleStatus.DOCUMENT_PROCEED -> "В обработке"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Заголовок и статус
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    value = saleItem.id,
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleSmall
                    )
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    AppText(
                        value = statusText,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        ),
                        color = statusColor
                    )
                }
            }

            // Название товара и категория
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(end = 8.dp)
                )

                Column {
                    AppText(
                        value = saleItem.productName,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
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
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            // Детали продажи
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    AppText(
                        value = "Количество: ${saleItem.quantity} шт.",
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    )
                    AppText(
                        value = "Цена: ${saleItem.price} грн/шт.",
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    AppText(
                        value = "${saleItem.totalAmount} грн",
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            // Клиент и дата
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    AppText(
                        value = saleItem.customerName,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    AppText(
                        value = saleItem.documentDate,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.bodySmall
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun generateSampleSalesItems(): List<ProductSaleItem> {
    val products = listOf(
        "Сталь листовая", "Металлопрофиль", "Трубы стальные",
        "Арматура", "Швеллер", "Профнастил", "Металлоконструкции"
    )

    val categories = listOf(
        "Металлопрокат", "Строительные материалы", "Сырье",
        "Промышленное оборудование", "Инструменты"
    )

    val customers = listOf(
        "ООО Стройинвест", "АО СтройПрогресс", "ЧП Металлоконструкции",
        "ЗАО ТехМонтаж", "ООО Спецстрой", "ОАО Промстрой"
    )

    val statuses = SaleStatus.values()

    return List(12) { index ->
        val quantity = Random.nextInt(5, 100)
        val price = Random.nextInt(1000, 10000)

        ProductSaleItem(
            id = Random.nextInt(10000, 99999)+index,
            productName = products[Random.nextInt(products.size)],
            cargoCategory = categories[Random.nextInt(categories.size)],
            quantity = quantity,
            price = price,
            totalAmount = quantity * price,
            customerName = customers[Random.nextInt(customers.size)],
            documentDate = "10.${Random.nextInt(1, 12)}.2023",
            status = SaleStatus.entries.random(),
            vatAmount = 999.0,
            requestDate = "12.04.1996",
            companyName = "Some company name",
            commissionPercent = 10.0,
            prepayment = false
        )
    }
}

data class SalesStatistics(
    val totalSalesAmount: AmountCurrency = AmountCurrency(
        currency = Currency.UAH,
        amount = 495000f
    ), //TODO test purpose
    val totalSalesNdsAmount: AmountCurrency = AmountCurrency(
        currency = Currency.UAH,
        amount = totalSalesAmount.amount * (20f/(100+20f))
    ), //TODO test purpose
    val totalBuyers: Int = 12, //TODO test purpose
)

data class SalesPageState(
    val salesItems: List<ProductSaleItem> = generateSampleSalesItems(),
    val salesStatistics: SalesStatistics = SalesStatistics(),
)

@Preview
@Composable
fun SalesPagePreview() {
    AppTheme {
        SalesPage()
    }
}

@Preview
@Composable
fun ProductSaleCardPreview() {
    AppTheme {
        ProductSaleCard(
            saleItem = ProductSaleItem(
                id = Random.nextInt(10000, 99999),
                productName = "Сталь листовая",
                cargoCategory = "Металлопрокат",
                quantity = 25,
                price = 3500,
                totalAmount = 87500,
                customerName = "ООО Стройинвест",
                documentDate = "10.${Random.nextInt(1, 12)}.2023",
                status = SaleStatus.entries.random(),
                vatAmount = 999.0,
                requestDate = "12.04.1996",
                companyName = "Some company name",
                commissionPercent = 10.0,
                prepayment = false
            )
        )
    }
} 