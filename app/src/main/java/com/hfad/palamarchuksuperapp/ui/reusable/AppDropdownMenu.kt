package com.hfad.palamarchuksuperapp.ui.reusable

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.domain.models.LLMName
import java.text.SimpleDateFormat
import java.util.Locale

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
    var buttonPart: (@Composable () -> Unit)? = null

    override fun Header(content: @Composable (() -> Unit)) {
        titlePart = content
    }

    override fun Content(content: @Composable (() -> Unit)) {
        contentPart = content
    }

    override fun Actions(
        content: @Composable (() -> Unit),
    ) {
        buttonPart = content
    }

}

@Composable
fun AppDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    dialogProperties: DialogProperties = DialogProperties(),
    content: @Composable AppDialogScope.() -> Unit,
) {
    val dialogState = remember { AppDialogState() }.apply { content() }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = dialogProperties
    ) {
        Surface(
            modifier = modifier
                .widthIn(max = 240.dp)
                .heightIn(max = 240.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                dialogState.titlePart?.invoke()
                dialogState.contentPart?.invoke()
                dialogState.buttonPart?.invoke()
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