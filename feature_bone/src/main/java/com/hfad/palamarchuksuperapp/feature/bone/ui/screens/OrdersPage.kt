package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.ui.OrderCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.BusinessEntity
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.EntityType
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.Order
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.StepperStatus


@Composable
fun OrdersPage(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            OrderStatistics(
                entity = BusinessEntity(
                    code = 1,
                    name = "12345",
                    manager = "Иван Петров",
                    type = EntityType.OTHER
                ),
                modifier = Modifier
            )
        }

        val items = listOf(
            Order(),
            Order(),
            Order()
        )

        items(items = items) {
            OrderCard(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                order = it,
                initialStatus = StepperStatus.entries.random()
            )
        }
    }
}

@Composable
fun OrderStatistics(
    entity: BusinessEntity,
    modifier: Modifier = Modifier,

    ) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                value = "statisticVal",
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val inProgress = ImageVector.vectorResource(R.drawable.in_progress)

                OrderStat(
                    modifier = Modifier.weight(0.3f),
                    icon = inProgress,
                    value = "2",
                    label = "inVorkVal",
                    color = MaterialTheme.colorScheme.error
                )

                OrderStat(
                    modifier = Modifier.weight(0.3f),
                    icon = Icons.Default.Check,
                    value = "15",
                    label = "completedVal",
                    color = Color(0xFF55940E)
                )

                val weightPainter = ImageVector.vectorResource(R.drawable.kilogram)

                OrderStat(
                    modifier = Modifier.weight(0.3f),
                    icon = weightPainter,
                    value = "450т",
                    label = "finishFull",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    value = "summOrdersTitle",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleMedium
                    )
                )
                AppText(
                    value = "summOrders",
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
fun OrderStat(
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
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderCardListPreview() {
    AppTheme(
        useDarkTheme = true
    ) {
        Column(
            modifier = Modifier
        ) {
            val entity = Order(
                id = 0,
            )
            OrderCard(
                order = entity,
                initialStatus = StepperStatus.IN_PROGRESS,
                currentStep = 3,
                initialExpanded = true,
                modifier = Modifier.padding(16.dp)
            )
            val entity1 = Order(
                id = 1,
            )
            OrderCard(
                order = entity1,
                initialStatus = StepperStatus.DONE,
                currentStep = 7,
                initialExpanded = false,
                modifier = Modifier.padding(16.dp)
            )
            val entity2 = Order(
                id = 2
            )
            OrderCard(
                order = entity2,
                initialStatus = StepperStatus.CANCELED,
                currentStep = 2,
                modifier = Modifier.padding(16.dp)
            )
            val entity3 = Order(
                id = 3
            )
            OrderCard(
                order = entity3,
                initialStatus = StepperStatus.CREATED,
                currentStep = 1,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderStatisticsPreview() {
    AppTheme {
        val entity = BusinessEntity(
            code = 1,
            name = "12345",
            manager = "Иван Петров",
            type = EntityType.OTHER
        )
        OrderStatistics(
            entity = entity,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun OrdersPagePreview() {
    AppTheme(
        useDarkTheme = false
    ) {
        OrdersPage()
    }
} 