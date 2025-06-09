package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.compose.FeatureTheme
import kotlin.random.Random

/**
 * A custom layout that arranges items in a horizontal flow using equal widths per row.
 * Each row uses the maximum intrinsic width (clamped to constraints) to compute equal-width items.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param horizontalSpacing Spacing between items in a row.
 * @param verticalSpacing Spacing between rows.
 * @param horizontalArrangement Defines the alignment of items inside each row (Start, End, Center).
 *                              Does not support SpaceBetween/Evenly/Around.
 * @param verticalAlignment Vertical alignment of items within each row (Top, CenterVertically, Bottom).
 * @param maxItemsInRow Limits the maximum number of items per row.
 * @param content Composable content that will be measured and placed in the layout.
 *
 * Example usage:
 * ```
 * EqualWidthFlowRow(
 *     horizontalSpacing = 8.dp,
 *     verticalSpacing = 8.dp,
 *     horizontalArrangement = Arrangement.Center,
 *     verticalAlignment = Alignment.CenterVertically,
 *     maxItemsInRow = 3
 * ) {
 *     repeat(7) {
 *         Box(
 *             modifier = Modifier
 *                 .height(40.dp)
 *                 .background(Color.Blue)
 *         )
 *     }
 * }
 * ```
 */
@Composable
fun EqualWidthFlowRow(
    modifier: Modifier = Modifier.Companion,
    horizontalSpacing: Dp = 6.dp,
    verticalSpacing: Dp = 6.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.Companion.Top,
    maxItemsInRow: Int = Int.MAX_VALUE,
    equalHeight: Boolean = true,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        if (measurables.isEmpty()) return@Layout layout(0, 0) {}

        // Converts spacing from dp to pixels for layout math
        val horizontalSpacingPx = horizontalSpacing.roundToPx()
        val verticalSpacingPx = verticalSpacing.roundToPx()
        val maxRowWidth = constraints.maxWidth.coerceAtLeast(constraints.minWidth)

        // We determine the minimum and maximum intrinsic widths to find a balance between compactness and uniformity
        val itemWidths = measurables.map {
            it.minIntrinsicWidth(constraints.maxHeight)
        }
        val minItemWidth = itemWidths.minOrNull() ?: 0
        val maxIntrinsicWidth = itemWidths.maxOrNull() ?: 0

        // Choosing a target width: wide enough for content, but doesn't exceed row width
        val itemWidth = when {
            maxIntrinsicWidth <= 0 -> maxRowWidth // fallback for degenerate case
            maxIntrinsicWidth > maxRowWidth -> maxRowWidth // prevent overflow
            else -> maxIntrinsicWidth
        }.coerceAtLeast(minItemWidth.coerceAtLeast(1)) // avoid zero-width items

        // Figure out how many items can realistically fit in one row
        val itemsPerRow = if (itemWidth + horizontalSpacingPx <= 0) {
            1
        } else {
            maxOf(
                1,
                minOf(
                    maxItemsInRow,
                    (maxRowWidth + horizontalSpacingPx) / (itemWidth + horizontalSpacingPx)
                )
            )
        }

        // Recalculate width so items evenly span full width of row
        val actualItemWidth = when {
            itemsPerRow <= 1 -> minOf(itemWidth, maxRowWidth)
            else -> {
                val availableWidth = maxRowWidth - horizontalSpacingPx * (itemsPerRow - 1)
                maxOf(1, availableWidth / itemsPerRow)
            }
        }

        // Group measurables into rows to determine height requirements
        val measurableRows = measurables.chunked(itemsPerRow)

        // If equal height is needed, calculate the maximum height for each row using intrinsic measurements
        val rowHeightConstraints = if (equalHeight) {
            measurableRows.map { rowMeasurables ->
                // Get intrinsic heights for items in this row with the target width
                val rowHeights = rowMeasurables.map { measurable ->
                    measurable.minIntrinsicHeight(actualItemWidth)
                }
                val maxRowHeight = rowHeights.maxOrNull() ?: 0
                maxRowHeight
            }
        } else {
            emptyList()
        }

        // Measure all children with appropriate constraints
        val placeables = measurables.mapIndexed { index, measurable ->
            val rowIndex = index / itemsPerRow

            val itemConstraints = if (equalHeight && rowIndex < rowHeightConstraints.size) {
                // Force equal width and height within the row
                val targetHeight = rowHeightConstraints[rowIndex]
                constraints.copy(
                    minWidth = actualItemWidth.coerceAtMost(constraints.maxWidth),
                    maxWidth = actualItemWidth.coerceAtMost(constraints.maxWidth),
                    minHeight = targetHeight.coerceAtMost(constraints.maxHeight),
                    maxHeight = targetHeight.coerceAtMost(constraints.maxHeight)
                )
            } else {
                // Force equal width for visual consistency
                constraints.copy(
                    minWidth = actualItemWidth.coerceAtMost(constraints.maxWidth),
                    maxWidth = actualItemWidth.coerceAtMost(constraints.maxWidth)
                )
            }

            measurable.measure(itemConstraints)
        }

        // Group measured items into rows, based on max items per row
        val rows = placeables.chunked(itemsPerRow)

        // Get max height of each row for proper vertical spacing and alignment
        val rowHeights = rows.map { row -> row.maxOfOrNull { it.height } ?: 0 }

        // Calculate total vertical size needed, including spacing between rows
        val totalHeight = rowHeights.sum() + verticalSpacingPx * (rows.size - 1).coerceAtLeast(0)
        val finalHeight = totalHeight.coerceIn(constraints.minHeight, constraints.maxHeight)

        layout(constraints.maxWidth, finalHeight) {
            var yPos = 0

            rows.forEachIndexed { rowIndex, row ->
                val rowHeight = rowHeights[rowIndex]

                // Determine horizontal alignment start position
                val totalRowWidth =
                    row.size * actualItemWidth + (row.size - 1) * horizontalSpacingPx
                val startX = when (horizontalArrangement) {
                    Arrangement.Start -> if (layoutDirection == LayoutDirection.Ltr) 0 else constraints.maxWidth - totalRowWidth
                    Arrangement.End -> if (layoutDirection == LayoutDirection.Ltr) constraints.maxWidth - totalRowWidth else 0
                    else -> (constraints.maxWidth - totalRowWidth) / 2
                }.coerceAtLeast(0)

                row.forEachIndexed { itemIndex, placeable ->
                    val x = startX + itemIndex * (actualItemWidth + horizontalSpacingPx)

                    // Align item vertically within its row (top/middle/bottom)
                    val y = when (verticalAlignment) {
                        Alignment.Companion.Top -> 0
                        Alignment.Companion.CenterVertically -> (rowHeight - placeable.height) / 2
                        Alignment.Companion.Bottom -> rowHeight - placeable.height
                        else -> 0
                    }.coerceAtLeast(0)

                    placeable.placeRelative(x, yPos + y)
                }

                // Move to next row vertically
                yPos += rowHeight + verticalSpacingPx
            }
        }
    }
}

@Preview
@Composable
fun EqualWidthFlowRowPreview() {
    FeatureTheme {
        val items = List(10) { "Item ${it * Random.nextInt(200000)}" }
        EqualWidthFlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            equalHeight = true,
        ) {
            items.forEach { item ->
                Text(
                    text = item,
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .background(Color.Green)
                )
            }
        }
    }
}