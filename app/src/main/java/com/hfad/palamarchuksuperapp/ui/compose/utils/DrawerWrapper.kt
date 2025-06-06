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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerDefaults.scrimColor
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Suppress("LongParameterList", "FunctionNaming", "LongMethod")
@Composable
fun MyNavigationDrawer(
    modifier: Modifier = Modifier,
    mainDrawerContent: @Composable () -> Unit,
    subDrawerContent: @Composable () -> Unit = {},
    mainDrawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    drawerWidth: Dp = 360.dp,
    gesturesEnabled: Boolean = true,
    scrimColor: Color = DrawerDefaults.scrimColor,
    drawerSideAlignment: DrawerWrapper = DrawerWrapper.Left,
    subDrawerState: DrawerState? = if (drawerSideAlignment != DrawerWrapper.Left)
        rememberDrawerState(DrawerValue.Closed) else null,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    when (drawerSideAlignment) {
        DrawerWrapper.Left -> {
            DrawerBox(
                drawerWidthPx = with(density) { drawerWidth.toPx() },
                scope = scope,
                gesturesEnabled = gesturesEnabled,
                drawerState = mainDrawerState,
                leftToRightSide = true,
                content = {
                    Box(modifier = Modifier.offset {
                        IntOffset(
                            x = mainDrawerState.currentOffset.toInt(),
                            y = 0
                        )
                    })
                    content()
                },
            ) {
                mainDrawerContent()
            }
        }

        DrawerWrapper.Right -> {
            DrawerBox(
                modifier = modifier.fillMaxSize(),
                drawerWidthPx = with(density) { drawerWidth.toPx() },
                scope = scope,
                gesturesEnabled = gesturesEnabled,
                drawerState = mainDrawerState,
                leftToRightSide = false,
                content = {
                    Box(modifier = Modifier.offset {
                        IntOffset(
                            x = -mainDrawerState.currentOffset.toInt(),
                            y = 0
                        )
                    })
                    content()
                },
            ) {
                mainDrawerContent()
            }
        }

        DrawerWrapper.Both -> {

            BoxWithConstraints(modifier.fillMaxSize()) {
                this.minHeight // Just to remove error
                val fullWidth = constraints.maxWidth.toFloat()
                val drawerWidthPx = with(density) { drawerWidth.toPx() }

                val mainDrawerOffset by animateFloatAsState(
                    targetValue = if (mainDrawerState.isOpen) 0f else -drawerWidthPx,
                    label = "mainDrawerOffset"
                )

                val subDrawerOffset by animateFloatAsState(
                    targetValue = if (subDrawerState!!.isOpen) fullWidth - drawerWidthPx else fullWidth,
                    label = "subDrawerOffset"
                )

                val mainGestureModifier = if (gesturesEnabled) {
                    Modifier.draggable(
                        state = rememberDraggableState {

                        },
                        orientation = Orientation.Horizontal,
                        onDragStopped = { velocity ->
                            scope.launch {
                                if (velocity < 0 && !mainDrawerState.isOpen) subDrawerState!!.open()
                                if (velocity > 0 && !subDrawerState!!.isOpen) mainDrawerState.open()
                                if (subDrawerState!!.isOpen) {
                                    if (velocity > 0) {
                                        subDrawerState.close()
                                    }
                                }
                                if (mainDrawerState.isOpen) {
                                    if (velocity < 0) {
                                        mainDrawerState.close()
                                    }
                                }
                            }
                        }
                    )
                } else {
                    Modifier
                }

                Box(modifier = mainGestureModifier) {
                    val offset by remember {
                        derivedStateOf {
                            -(fullWidth - subDrawerOffset - drawerWidthPx - mainDrawerOffset)
                        }
                    }
                    Box(modifier = Modifier.offset {
                        IntOffset(
                            offset.toInt(),
                            0
                        )
                    }) {
                        content()
                    }


                    if (mainDrawerState.isOpen) {
                        Scrim(
                            color = DrawerDefaults.scrimColor,
                            onDismiss = {
                                scope.launch { mainDrawerState.close() }
                            },
                            visible = mainDrawerState.isOpen
                        )
                    } else if (subDrawerState!!.isOpen) {
                        Scrim(
                            color = DrawerDefaults.scrimColor,
                            onDismiss = {
                                scope.launch { subDrawerState.close() }
                            },
                            visible = subDrawerState.isOpen
                        )
                    }

                    ModalDrawerSheet(
                        drawerShape = RoundedCornerShape(
                            32.dp,
                            0.dp,
                            0.dp,
                            32.dp
                        ),
                        modifier = Modifier
                            .fillMaxHeight()
                            .width((drawerWidthPx / LocalDensity.current.density).dp)
                            .offset { IntOffset(subDrawerOffset.roundToInt(), 0) }
                    ) {
                        subDrawerContent()
                    }

                    ModalDrawerSheet(
                        drawerShape = RoundedCornerShape(0.dp, 32.dp, 32.dp, 0.dp),
                        modifier = Modifier
                            .fillMaxHeight()
                            .width((drawerWidthPx / LocalDensity.current.density).dp)
                            .offset { IntOffset(mainDrawerOffset.roundToInt(), 0) }
                    ) {
                        mainDrawerContent()
                    }
                }
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
    BoxWithConstraints(modifier) {
        this.minHeight // Just to remove error
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
                },
            label = "drawerOffset",
        )

        val gestureModifier = if (gesturesEnabled) {
            Modifier.draggable(
                state = rememberDraggableState { },
                orientation = Orientation.Horizontal,
                onDragStopped = { velocity ->
                    scope.launch {
                        when {
                            (leftToRightSide && velocity > 0) -> drawerState.open()
                            (leftToRightSide && velocity < 0) -> drawerState.close()
                            (!leftToRightSide && velocity < 0) -> drawerState.open()
                            (!leftToRightSide && velocity > 0) -> drawerState.close()
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
            ModalDrawerSheet(
                drawerShape = if (leftToRightSide) RoundedCornerShape(
                    0.dp,
                    32.dp,
                    32.dp,
                    0.dp
                ) else RoundedCornerShape(32.dp, 0.dp, 0.dp, 32.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .width((drawerWidthPx / LocalDensity.current.density).dp)
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
