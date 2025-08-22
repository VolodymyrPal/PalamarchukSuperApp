package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import kotlin.math.abs

fun Modifier.detectCenter(
    lazyListState: LazyListState,
    onCentered: (Boolean) -> Unit
) = this.onGloballyPositioned { layoutCoordinates ->
    val viewportCenter = lazyListState.layoutInfo.viewportSize.height / 2
    val itemStartOffset = layoutCoordinates.positionInParent().y
    val itemCenterOffset = itemStartOffset + layoutCoordinates.size.height / 2
    val isCentered = abs(viewportCenter - itemCenterOffset) < layoutCoordinates.size.height / 2

    onCentered(isCentered)
}