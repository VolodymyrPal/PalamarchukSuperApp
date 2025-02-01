package com.hfad.palamarchuksuperapp.ui.reusable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@DslMarker
annotation class AppDialogDsl

@AppDialogDsl
interface AppDialogScope {
    fun Title(content: @Composable () -> Unit)
    fun Text(content: @Composable () -> Unit)
    fun ButtonAgree(text: String, onClick: () -> Unit)
    fun ButtonDismiss(text: String, onClick: () -> Unit)
    fun CustomContent(content: @Composable () -> Unit)
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


class AppDropdownMenuConfig {
    var text: String = ""
    var icon: ImageVector? = null
    var onClick: (() -> Unit)? = null
    var backgroundColor: Color = Color.Transparent
    var textColor: Color = Color.White
    val shapes = mutableListOf<ComposedShape>()
}

@AppDialogDsl
class ComposedShape {
    val shapes = mutableListOf<ShapeConfig>()
}

@AppDialogDsl
sealed class ShapeConfig {
    data class TriangleConfig(var base: Float, var height: Float, var color: Color) : ShapeConfig()
    data class RhombusConfig(var sideLength: Float, var angle: Float, var color: Color) :
        ShapeConfig()
}

fun AppDropdownMenuConfig.composedShape(block: ComposedShape.() -> Unit) {
    shapes.add(ComposedShape().apply(block))
}

fun ComposedShape.triangle(base: Float, height: Float, color: Color) {
    shapes.add(ShapeConfig.TriangleConfig(base, height, color))
}

fun ComposedShape.rhombus(sideLength: Float, angle: Float, color: Color) {
    shapes.add(ShapeConfig.RhombusConfig(sideLength, angle, color))
}

@Composable
fun customButton(configure: AppDropdownMenuConfig.() -> Unit) {
    val config = AppDropdownMenuConfig().apply(configure)

    Button(
        onClick = config.onClick ?: {},
        colors = ButtonDefaults.buttonColors(containerColor = config.backgroundColor),
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Render Shapes
            config.shapes.forEach { composedShape ->
                composedShape.shapes.forEach { shape ->
                    when (shape) {
                        is ShapeConfig.TriangleConfig -> {
                            Text(
                                text = "\u25B2", // Triangle representation
                                color = shape.color,
                                modifier = Modifier.size(shape.base.dp, shape.height.dp)
                            )
                        }

                        is ShapeConfig.RhombusConfig -> {
                            Text(
                                text = "\u25C6", // Rhombus representation
                                color = shape.color,
                                modifier = Modifier.size(shape.sideLength.dp)
                            )
                        }
                    }
                }
            }
            // Render Text and Icon
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                config.icon?.let {
                    Icon(imageVector = it, contentDescription = null, tint = config.textColor)
                }
                if (config.icon != null) Spacer(modifier = Modifier.width(8.dp))
                Text(text = config.text, color = config.textColor)
            }
        }
    }
}

@Composable
fun some() {
    AppDialog(
        onDismissRequest = { },
    ) {
        this.CustomContent {
            Text(text = "Hello")
        }
        Title {
            Text(text = "Hello")
        }
        Title { }
    }
    customButton {
        text = "Hello"
        icon = Icons.Default.Add
        composedShape {
            triangle(10f, 10f, Color.Red)
            rhombus(10f, 10f, Color.Green)
        }
    }
}