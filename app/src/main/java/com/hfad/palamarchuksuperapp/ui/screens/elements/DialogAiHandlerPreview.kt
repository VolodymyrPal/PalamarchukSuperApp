package com.hfad.palamarchuksuperapp.ui.screens.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppOutlinedTextField
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppTextField
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.rememberTextFieldConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.elements.AppDialog
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.domain.models.LLMName
import java.text.SimpleDateFormat
import java.util.Locale

@Preview
@Composable
private fun DialogStatement() {
    val isVisible = remember { mutableStateOf(true) }
    AppTheme {
        AppDialog(
            onDismissRequest = { isVisible.value = false }
        ) {
            val text = remember { mutableStateOf("Some test text") }
            Header {
                AppOutlinedTextField(
                    value = text.value,
                    onValueChange = { text.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    labelRes = R.string.model_hint
                )
            }
            Content {
                AppOutlinedTextField(
                    value = "text.value",
                    onValueChange = { text.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    labelRes = R.string.compose
                )

            }
            Actions {
                AppOutlinedTextField(
                    value = "text.value",
                    onValueChange = { text.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    labelRes = R.string.compose,
                )
            }
        }
    }
}


@Preview
@Composable
private fun StoreAppDialog() {
    AppTheme {
        AppDialog(
            modifier = Modifier,
            onDismissRequest = {}
        ) {
            Header {
                AppText(
                    "Order Confirmation",
                    modifier = Modifier.fillMaxWidth(),
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                )
            }
            Content {
                var phone by remember { mutableStateOf("") }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(stringResource(R.string.order_confirmation_message))
                    AppTextField(
                        value = phone,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { phone = it },
                        editTextConfig = rememberTextFieldConfig(
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                }
                            ),
                        )
                    )
                    AppTextField(
                        value = phone,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { phone = it },
                        editTextConfig = rememberTextFieldConfig(
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                }
                            ),
                            enabled = false
                        )
                    )
                    AppTextField(
                        value = phone,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { phone = it },
                        editTextConfig = rememberTextFieldConfig(
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                }
                            )
                        )
                    )
                    AppTextField(
                        value = phone,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { phone = it },
                        editTextConfig = rememberTextFieldConfig(
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                }
                            ),
                            isError = true
                        )
                    )
                }
            }
            Actions {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(
                        modifier = Modifier,
                        onClick = {

                        }
                    ) {
                        Text(
                            modifier = Modifier,
                            textAlign = TextAlign.End,
                            text = stringResource(R.string.order_proceed_button)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AppStoreDialog(
    isOpen: Boolean = true,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.order_confirmation_title))
        },
        text = {
            var phone by remember { mutableStateOf("") }
            Column {
                Text(stringResource(R.string.order_confirmation_message))
                TextField(
                    value = phone,
                    onValueChange = { phone = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                        }
                    )
                )
            }
        },
        onDismissRequest = {

        },
        confirmButton = {
            TextButton(
                onClick = {

                }
            ) {
                Text(stringResource(R.string.order_proceed_button))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun DialogAiHandlerPreview() {
    AppTheme {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Заголовок диалога
                    Text(
                        text = stringResource(
                            R.string.edit_handler_title
//                            R.string.add_handler_title
                        ),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Поля ввода
                    val name = remember {
                        mutableStateOf(
                            "Handler name"
                        )
                    }
                    var isLLMMenuExpanded by remember { mutableStateOf(false) }
                    val selectedLLM = remember {
                        mutableStateOf(
                            "LLmName"
                        )
                    }
                    val expandedModelMenu = remember { mutableStateOf(false) }
                    val selectedModelOption = remember {
                        mutableStateOf(
                            "Model name"
                        )
                    }
                    val apiKey = remember {
                        mutableStateOf(
                            "Api key name"
                        )
                    }

                    // Название
                    OutlinedTextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        label = {
                            Text(
                                stringResource(R.string.handler_name_hint),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // Выбор LLM
                    ExposedDropdownMenuBox(
                        expanded = isLLMMenuExpanded,
                        onExpandedChange = { isLLMMenuExpanded = !isLLMMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedLLM.value,
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text(
                                    stringResource(R.string.model_ai_hint),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isLLMMenuExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                                .fillMaxWidth(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )

                        ExposedDropdownMenu(
                            expanded = isLLMMenuExpanded,
                            onDismissRequest = { isLLMMenuExpanded = false }
                        ) {
                            LLMName.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option.name,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    onClick = {

                                    }
                                )
                            }
                        }
                    }

                    // Выбор модели
                    if (selectedLLM.value != null) {
                        ExposedDropdownMenuBox(
                            expanded = expandedModelMenu.value,
                            onExpandedChange = {
                                expandedModelMenu.value = !expandedModelMenu.value
                            }
                        ) {
                            OutlinedTextField(
                                value = selectedModelOption.value,
                                onValueChange = {},
                                readOnly = true,
                                label = {
                                    Text(
                                        stringResource(R.string.model_hint),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandedModelMenu.value
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                                    .fillMaxWidth(),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedModelMenu.value,
                                onDismissRequest = { expandedModelMenu.value = false }
                            ) {
                                listOf<Int>(0, 1, 2).forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Option 1",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        onClick = {

                                        }
                                    )
                                }
                            }
                        }
                    }

                    // API ключ
                    OutlinedTextField(
                        value = apiKey.value,
                        onValueChange = { apiKey.value = it },
                        label = { Text(stringResource(R.string.api_key_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // Кнопки действий
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {

                            },
                            enabled = name.value.isNotBlank()
                        ) {
                            Text(
                                stringResource(
                                    R.string.save
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun HandlerDropdownMenu(
    expanded: Boolean = true,
    onDismissRequest: () -> Unit = { },
    containerColor: Color = Color.Transparent,
) {
    Dialog(
        onDismissRequest = { }
    ) {
        Surface(
            modifier = Modifier.size(200.dp, 200.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            tonalElevation = 6.dp
        ) {
            // Заголовок диалога
            LazyColumn {
                item {
                    Row {
                        IconButton(
                            onClick = { }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Choose chat", modifier =
                                Modifier.padding(12.dp)
                        )
                        IconButton(
                            onClick = { }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                }
                items(
                    listOf<Int>(0, 1, 2),
                ) { chat ->
                    Row(
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Text(
                            text = "chat.name",
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable(interactionSource = null, indication = null) {
                                }
                        )
                        Text(
                            text = SimpleDateFormat("dd.MM:HH:mm", Locale.US).format(
                                System.currentTimeMillis()
                            ),
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}