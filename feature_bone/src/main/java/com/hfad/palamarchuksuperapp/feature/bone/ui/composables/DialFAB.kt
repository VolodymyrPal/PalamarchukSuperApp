package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class ExpandDirection {
    Up, Down, Left, Right
}

@Composable
fun DialFab(
    modifier: Modifier = Modifier,
    mainIcon: ImageVector = Icons.Filled.Add,
    expandDirection: ExpandDirection = ExpandDirection.Up,
    dialItems: List<DialFabItem>,
    onMainClick: () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    itemSpacing: Dp = 56.dp,
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    withBackdrop: Boolean = true,
    backdropColor: Color = Color.Black.copy(alpha = 0.3f),
    rotationAngle: Float = 45f,
    mainFabSize: Dp = 56.dp,
    subFabSize: Dp = 40.dp,
) {
    val density = LocalDensity.current
    val rotation by animateFloatAsState(
        targetValue = if (expanded) rotationAngle else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "rotation"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            // Sub FABs
            dialItems.forEachIndexed { index, item ->
                val spacingPx = with(density) { itemSpacing.toPx() }
                val animProgress by animateFloatAsState(
                    targetValue = if (expanded) 1f else 0f,
                    animationSpec = tween(300, delayMillis = index * 50, easing = FastOutSlowInEasing),
                    label = "sub_fab_anim"
                )

                val offset = when (expandDirection) {
                    ExpandDirection.Up -> Offset(0f, -spacingPx * (index + 1) * animProgress)
                    ExpandDirection.Down -> Offset(0f, spacingPx * (index + 1) * animProgress)
                    ExpandDirection.Left -> Offset(-spacingPx * (index + 1) * animProgress, 0f)
                    ExpandDirection.Right -> Offset(spacingPx * (index + 1) * animProgress, 0f)
                }

                if (animProgress > 0.01f) {
                    DialFabSubItem(
                        modifier = Modifier
                            .absoluteOffset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                            .alpha(animProgress)
                            .align(Alignment.BottomEnd),
                        icon = item.icon,
                        label = item.label,
                        onClick = {
                            item.onClick()
                            onExpandedChange(false)
                        },
                        containerColor = item.containerColor ?: MaterialTheme.colorScheme.surface,
                        contentColor = item.contentColor ?: MaterialTheme.colorScheme.onSurface,
                        subFabSize = subFabSize
                    )
                }
            }

            // Main FAB
            FloatingActionButton(
                onClick = {
                    if (dialItems.isNotEmpty()) {
                        onExpandedChange(!expanded)
                    } else {
                        onMainClick()
                    }
                },
                shape = CircleShape,
                containerColor = containerColor,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 12.dp
                ),
                modifier = Modifier
                    .size(mainFabSize)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = mainIcon,
                    contentDescription = "Main FAB",
                    tint = contentColor,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotation)
                )
            }
        }
    }
}

@Composable
private fun DialFabSubItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String? = null,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    subFabSize: Dp,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        label?.let {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shadowElevation = 4.dp
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(subFabSize),
            shape = CircleShape,
            containerColor = containerColor,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label ?: "Sub action",
                tint = contentColor,
                modifier = Modifier.size(subFabSize / 2)
            )
        }
    }
}

data class DialFabItem(
    val icon: ImageVector,
    val label: String? = null,
    val onClick: () -> Unit,
    val containerColor: Color? = null,
    val contentColor: Color? = null,
)

@Composable
private fun DialFabSubItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String? = null,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Label (показывается слева от иконки)
        label?.let {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Mini FAB
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(20.dp),
            containerColor = containerColor,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// Пример использования:
@Preview
@Composable
fun ExampleUsagePreview() {
    MaterialTheme {
        val dialItems = listOf(
            DialFabItem(
                icon = Icons.Filled.Person,
                label = "Профиль",
                onClick = { /* действие */ }
            ),
            DialFabItem(
                icon = Icons.Filled.Settings,
                label = "Настройки",
                onClick = { /* действие */ }
            ),
            DialFabItem(
                icon = Icons.Filled.Favorite,
                label = "Избранное",
                onClick = { /* действие */ },
                containerColor = Color.Red.copy(alpha = 0.8f),
                contentColor = Color.White
            )
        )

        DialFab(
            modifier = Modifier,
            expandDirection = ExpandDirection.Up, // или ExpandDirection.Left
            dialItems = dialItems,
            onMainClick = {
            }
        )
    }
}
