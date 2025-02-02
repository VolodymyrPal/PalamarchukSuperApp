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
}

@Composable
fun AppDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    dialogProperties: DialogProperties = DialogProperties(),
    content: @Composable AppDialogScope.() -> Unit,
) {
    val dialogState = remember { MyDialogState() }
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
                // Заголовок
                dialogState.title?.invoke()

                // Текст
                dialogState.text?.invoke()

                // Кастомный контент
                dialogState.customContent?.invoke()

                // Кнопки
                Row(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dialogState.dismissButton?.invoke()
                    dialogState.agreeButton?.invoke()
                }
            }
        }
    }
}

// Внутренний State для хранения элементов
private class MyDialogState : AppDialogScope {
    var title: (@Composable () -> Unit)? = null
    var text: (@Composable () -> Unit)? = null
    var agreeButton: (@Composable () -> Unit)? = null
    var dismissButton: (@Composable () -> Unit)? = null
    var customContent: (@Composable () -> Unit)? = null

    override fun Title(content: @Composable (() -> Unit)) {
        title = content
    }

    override fun Text(content: @Composable (() -> Unit)) {
        text = content
    }

    override fun ButtonAgree(text: String, onClick: () -> Unit) {
        agreeButton = {
            Button(onClick) {
                Text(text)
            }
        }
    }

    override fun ButtonDismiss(text: String, onClick: () -> Unit) {
        dismissButton = {
            TextButton(onClick = onClick) {
                Text(text)
            }
        }
    }

    override fun CustomContent(content: @Composable (() -> Unit)) {
        customContent = content
    }

}


@Composable
fun SomeScreen() {
    val isVisible = remember { mutableStateOf(true) }

    AppDialog(
        onDismissRequest = { isVisible.value = false }
    ) {
        this.Title { Text("Hi, man") }
        this.Text { Text("just some testing text") }
        this.ButtonAgree("Ok") { isVisible.value = false }
    }

}

@Preview
@Composable
fun AppDialogPreview() {
    SomeScreen()
}