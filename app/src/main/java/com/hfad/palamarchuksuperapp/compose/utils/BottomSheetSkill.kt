package com.hfad.palamarchuksuperapp.compose.utils

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsChangeConst
import java.util.Date


@Suppress("detekt.FunctionNaming", "detekt.UnusedParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSkill(
    modifier: Modifier = Modifier.imePadding(),
    onEvent: (SkillsChangeConst) -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit,
) {
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)


    Log.d("KeyboadrHeight: ", "${keyboardHeight}")
    ModalBottomSheet(
        modifier = Modifier.wrapContentSize(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primaryContainer
    ) {

        Surface(modifier = Modifier, color = MaterialTheme.colorScheme.primaryContainer) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var textName by remember { mutableStateOf("") }
                OutlinedHintText(
                    modifier = Modifier.fillMaxWidth(),
                    value = textName,
                    onValueChange = { textName = it
                        Log.d("KeyboadrHeight: ", "${keyboardHeight}")
                    },
                    label = { Text(text = "Skill Name") },
                    placeholder = "Skill Description",
                    hintText = "Jetpack Compose"
                )

                var textDescription by remember { mutableStateOf("") }
                OutlinedHintText(
                    modifier = Modifier.fillMaxWidth(),
                    value = textDescription,
                    onValueChange = { textDescription = it
                        Log.d("KeyboadrHeight: ", "${keyboardHeight}")
                    },
                    hintText = "My hint",
                    label = { Text(text = "State, composition, theme, etc.") },
                    placeholder = "Description",
                )

                var textDate by remember { mutableStateOf(Date()) }
                OutlinedHintText(
                    modifier = Modifier.fillMaxWidth(),
                    value = textDate.toString(),
                    onValueChange = { },
                    hintText = "Date",
                    label = { Text(text = "Date") },
                    placeholder = "Date"
                )

                Button(onClick = { /*TODO*/ },
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(text = "Save")
                }

                Spacer(modifier = Modifier.size(50.dp))
                Spacer(modifier = Modifier.size(50.dp))
                Spacer(modifier = Modifier.size(50.dp))

            }
        }
    }
}


@Suppress("detekt.FunctionNaming", "detekt.UnusedParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun MyBottomSheetPreview() {
    BottomSheetSkill(onDismiss = {})
}

@Suppress(
    "detekt.FunctionNaming",
    "detekt.UnusedParameter",
    "detekt.LongParameterList",
    "detekt.LongMethod"
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedHintText(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    placeholder: String,
    hintText: String = "Put hint",
) {
    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    var focusable by remember { mutableStateOf(false) }

    CompositionLocalProvider {

        BasicTextField(
            value = value,
            modifier = modifier
                .semantics(mergeDescendants = true) {}
                .padding(8.dp)
                .focusable()
                .onFocusChanged {
                    focusable = it.isFocused
                },
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.error),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            decorationBox = { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    visualTransformation = visualTransformation,
                    innerTextField = innerTextField,
                    label = label,
                    placeholder = {
                        if (focusable) Text(
                            text = hintText,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.2f)
                        )
                    },
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    supportingText = supportingText,
                    singleLine = singleLine,
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    container = {
                        OutlinedTextFieldDefaults.ContainerBox(
                            enabled,
                            isError,
                            interactionSource,
                            colors,
                            shape
                        )
                    }
                )

            }
        )
    }
}