package com.hfad.palamarchuksuperapp.ui.compose.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerDefaults.scrimColor
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Suppress("LongParameterList", "FunctionNaming", "LongMethod")
@Composable
fun MyNavigationDrawer(
    modifier: Modifier = Modifier,
    drawerContentLeftSide: @Composable () -> Unit,
    drawerContentRightSide: @Composable () -> Unit = {},
    drawerStateLeft: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    scrimColor: Color = DrawerDefaults.scrimColor,
    drawerSideAlignment: DrawerWrapper = DrawerWrapper.Left,
    drawerStateRight: DrawerState? = if (drawerSideAlignment != DrawerWrapper.Left)
        rememberDrawerState(DrawerValue.Closed) else null,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val drawerWidth = 360.dp
    val density = LocalDensity.current

//    when (leftRightSided) {
//        DrawerWrapper.Left -> {
//        }
//        DrawerWrapper.Right -> {
//    }

    when (drawerSideAlignment) {
        DrawerWrapper.Left -> {
            DrawerBox(
                modifier = modifier.fillMaxSize(),
                drawerWidthPx = with(density) { drawerWidth.toPx() },
                scope = scope,
                gesturesEnabled = gesturesEnabled,
                drawerState = drawerStateLeft,
                leftToRightSide = true,
                content = { content() } ,
                ) {
                drawerContentLeftSide()
            }
        }
        DrawerWrapper.Right -> {

        }
        DrawerWrapper.Both -> {

        }

    }

    BoxWithConstraints(modifier.fillMaxSize()) {
        val fullWidth = constraints.maxWidth.toFloat()
        val drawerWidthPx = with((density)) { drawerWidth.toPx() }

        val drawerOffset by animateFloatAsState(
            targetValue =
            when (drawerSideAlignment) {
                DrawerWrapper.Left -> {
                    if (drawerStateLeft.isOpen) 0f else -drawerWidthPx
                }

                else -> {
                    if (drawerStateLeft.isOpen) fullWidth - drawerWidthPx else fullWidth
                }
            }, label = "drawerOffset"
        )


        val gestureModifier = if (gesturesEnabled) {
            Modifier.draggable(
                state = rememberDraggableState { delta ->
                    scope.launch {
                        val newValue =
                            (drawerOffset - delta).coerceIn(fullWidth - drawerWidthPx, fullWidth)
                        drawerStateLeft.snapTo(
                            if (newValue <= fullWidth - drawerWidthPx / 2) DrawerValue.Open
                            else DrawerValue.Closed
                        )
                    }
                },
                orientation = Orientation.Horizontal,
                onDragStopped = { velocity ->
                    scope.launch {
                        when (drawerSideAlignment) {
                            DrawerWrapper.Left -> if (velocity < 0) drawerStateLeft.open() else drawerStateLeft.close()
                            else -> if (velocity > 0) drawerStateLeft.open() else drawerStateLeft.close()
                        }
                    }
                }
            )
        } else {
            Modifier
        }

        Box(modifier = gestureModifier) {
            content()

            if (drawerStateLeft.isOpen) {
                Scrim(
                    color = scrimColor,
                    onDismiss = {
                        scope.launch { drawerStateLeft.close() }
                    },
                    visible = drawerStateLeft.isOpen
                )
            }

            Box(
                Modifier
                    .width(drawerWidth)
                    .fillMaxHeight()
                    .offset { IntOffset(drawerOffset.roundToInt(), 0) }
            ) {
                drawerContentLeftSide()
            }
        }
    }
}

@Suppress("LongParameterList")
@Composable
fun DrawerBox(
    modifier: Modifier = Modifier,
    drawerWidthPx: Float,
    scope: CoroutineScope,
    leftToRightSide: Boolean = true,
    gesturesEnabled: Boolean = false,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    content: @Composable () -> Unit,
    drawerContent: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val fullWidth = constraints.maxWidth.toFloat()

        val drawerOffset by animateFloatAsState(
            targetValue =
            when (leftToRightSide) {
                true -> {
                    if (drawerState.isOpen) 0f else -drawerWidthPx
                }
                else -> {
                    if (drawerState.isOpen) fullWidth - drawerWidthPx else fullWidth
                }
            }, label = "drawerOffset"
        )


        val gestureModifier = if (gesturesEnabled) {
            Modifier.draggable(
                state = rememberDraggableState { delta ->
                    scope.launch {
                        val newValue =
                            (drawerOffset - delta).coerceIn(fullWidth - drawerWidthPx, fullWidth)
                        drawerState.snapTo(
                            if (newValue <= fullWidth - drawerWidthPx / 2) DrawerValue.Open
                            else DrawerValue.Closed
                        )
                    }
                },
                orientation = Orientation.Horizontal,
                onDragStopped = { velocity ->
                    scope.launch {
                        when (leftToRightSide) {
                            true -> if (velocity < 0) drawerState.open() else drawerState.close()
                            else -> if (velocity > 0) drawerState.open() else drawerState.close()
                        }
                    }
                }
            )
        } else {
            Modifier
        }

        Box(modifier = gestureModifier) {
            content()

            if (drawerState.isOpen) {
                Scrim(
                    color = scrimColor,
                    onDismiss = {
                        scope.launch { drawerState.close() }
                    },
                    visible = drawerState.isOpen
                )
            }

            Box(
                Modifier
                    .width(drawerWidthPx.dp)
                    .fillMaxHeight()
                    .offset { IntOffset(drawerOffset.roundToInt(), 0) }
            ) {
                drawerContent()
            }
        }
    }
}

enum class DrawerWrapper {
    Left,
    Right,
    Both
}


@Composable
private fun Scrim(
    color: Color,
    onDismiss: () -> Unit,
    visible: Boolean,
) {
    if (visible) {
        Box(
            Modifier
                .fillMaxSize()
                .background(color)
                .clickable(onClick = onDismiss)
        )
    }
}
