package com.hfad.palamarchuksuperapp.core.ui.composables.elements

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@DslMarker
annotation class AppDialogDsl

@AppDialogDsl
interface AppDialogScope {
    fun Header(content: @Composable () -> Unit)
    fun Content(content: @Composable () -> Unit)
    fun Actions(content: @Composable () -> Unit)
}

// Внутренний State для хранения элементов
private class AppDialogState : AppDialogScope {
    var titlePart: (@Composable () -> Unit)? = null
    var contentPart: (@Composable () -> Unit)? = null
    var actionPart: (@Composable () -> Unit)? = null

    override fun Header(content: @Composable () -> Unit) {
        titlePart = content
    }

    override fun Content(content: @Composable (() -> Unit)) {
        contentPart = content
    }

    override fun Actions(content: @Composable (() -> Unit)) {
        actionPart = content
    }

}

@Composable
fun AppDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    dialogProperties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable AppDialogScope.() -> Unit,
) {
    val dialogState = remember { AppDialogState() }.apply { content() }
    val configuration = LocalConfiguration.current
    val maxWidth = configuration.screenWidthDp.dp * 0.85f
    val maxHeight = configuration.screenHeightDp.dp * 0.85f

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = dialogProperties
    ) {
        val shape = MaterialTheme.shapes.medium

        Box(
            modifier = Modifier.background(Color.Red)
        ) {

            Surface(
                shape = shape,
                modifier = modifier
                    .sizeIn(maxWidth = maxWidth, maxHeight = maxHeight)
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
                    .clip(shape)
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 600,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                color = MaterialTheme.colorScheme.onPrimary,
            ) {
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    dialogState.titlePart?.invoke()
                    dialogState.contentPart?.invoke()
                    dialogState.actionPart?.invoke()
                }
            }
        }
    }
}
