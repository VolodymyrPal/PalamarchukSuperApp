package com.hfad.palamarchuksuperapp.ui.reusable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@DslMarker
annotation class AppDialogDsl

@AppDialogDsl
interface AppDialogScope {
    fun TitlePart(modifier: Modifier = Modifier, content: @Composable () -> Unit)
    fun ContentPart(modifier: Modifier = Modifier, content: @Composable () -> Unit)
    fun ButtonPart(modifier: Modifier = Modifier, content: @Composable () -> Unit)
}

// Внутренний State для хранения элементов
private class AppDialogState : AppDialogScope {
    var titlePart: (@Composable () -> Unit)? = null
    var titlePartModifier: Modifier = Modifier
    var contentPart: (@Composable () -> Unit)? = null
    var contentPartModifier: Modifier = Modifier
    var buttonPart: (@Composable () -> Unit)? = null
    var buttonPartModifier: Modifier = Modifier

    override fun TitlePart(
        modifier: Modifier,
        content: @Composable (() -> Unit),
    ) {
        titlePart = content
        titlePartModifier = modifier
    }

    override fun ContentPart(
        modifier: Modifier,
        content: @Composable (() -> Unit),
    ) {
        contentPart = content
        contentPartModifier = modifier
    }

    override fun ButtonPart(
        modifier: Modifier,
        content: @Composable (() -> Unit),
    ) {
        buttonPart = content
        buttonPartModifier = modifier
    }

}

@Composable
fun AppDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    dialogProperties: DialogProperties = DialogProperties(),
    content: @Composable AppDialogScope.() -> Unit,
) {
    val dialogState = remember { AppDialogState() }
    dialogState.content() // Собираем контент через Scope

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = dialogProperties
    ) {
        Surface(
            modifier = modifier.widthIn(max = 480.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                dialogState.titlePart?.let { content ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.1f)
                            .then(
                                dialogState.titlePartModifier
                            )
                    ) {
                        content()
                    }
                }
                dialogState.contentPart?.let { content ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.8f)
                            .then(
                                dialogState.contentPartModifier
                            )
                    ) {
                        content()
                    }
                }
                dialogState.buttonPart?.let { content ->
                    Box(
                        modifier = dialogState.buttonPartModifier
                            .fillMaxWidth()
                            .weight(0.1f)
                            .then(
                                dialogState.buttonPartModifier
                            )
                    ) {
                        content()
                    }
                }
            }
        }
    }
}


@Composable
fun SomeScreen() {
    val isVisible = remember { mutableStateOf(true) }

    AppDialog(
        onDismissRequest = { isVisible.value = false }
    ) {
        Column {
            TitlePart(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
            ) { Text("Title part") }
            ContentPart(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
            ) { Text("Content part") }
            ButtonPart(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
            ) { Text("Button part") }
        }
    }

}

@Preview
@Composable
fun AppDialogPreview() {
    SomeScreen()
}