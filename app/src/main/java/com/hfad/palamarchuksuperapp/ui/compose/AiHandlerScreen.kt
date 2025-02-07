package com.hfad.palamarchuksuperapp.ui.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.data.services.GroqApiHandler
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.LLMName
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.ui.reusable.AppDialog
import com.hfad.palamarchuksuperapp.ui.reusable.ConfirmationDialog
import com.hfad.palamarchuksuperapp.ui.reusable.elements.AppEditOutlinedText
import com.hfad.palamarchuksuperapp.ui.reusable.elements.AppText
import com.hfad.palamarchuksuperapp.ui.reusable.elements.rememberAppTextConfig
import com.hfad.palamarchuksuperapp.ui.viewModels.ChatBotViewModel
import io.ktor.client.HttpClient
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Composable
@Suppress("FunctionNaming")
fun AiHandlerScreen(
    modifier: Modifier = Modifier,
    listAiModelHandler: PersistentList<AiModelHandler>,
    event: (ChatBotViewModel.Event) -> Unit = {},
    aiModelList: PersistentList<AiModel> = persistentListOf(),
) {
    val dialogState = remember { DialogAiHandlerState() }
    DialogAiHandler(
        event = event,
        modelList = aiModelList,
        dialogAiHandlerState = dialogState
    )

    LazyColumn(
        modifier = modifier
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        dialogState.show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }
        }
        itemsIndexed(listAiModelHandler) { index, item ->
            AiHandlerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                aiModelHandler = item,
                event = event,
                eventToDialog = dialogState::show
            )
        }
    }
}

@Stable
class DialogAiHandlerState(
    isShowing: Boolean = false,
    handler: AiModelHandler? = null,
) {
    var isShowing by mutableStateOf(isShowing)
    var handler by mutableStateOf(handler)

    fun dismiss() {
        isShowing = false
        handler = null
    }

    fun show(handler: AiModelHandler? = null) {
        this.handler = handler
        isShowing = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("FunctionNaming", "LongMethod", "MagicNumber")
@Composable
fun DialogAiHandler(
    modifier: Modifier = Modifier,
    event: (ChatBotViewModel.Event) -> Unit,
    modelList: PersistentList<AiModel>,
    dialogAiHandlerState: DialogAiHandlerState,
) {
    if (dialogAiHandlerState.isShowing) {
        AppDialog(
            onDismissRequest = { dialogAiHandlerState.dismiss() },
            modifier = modifier
        ) {
            val name = remember {
                mutableStateOf(
                    dialogAiHandlerState.handler?.aiHandlerInfo?.value?.name ?: ""
                )
            }
            var isLLMMenuExpanded by remember { mutableStateOf(false) }
            val selectedLLM = remember {
                mutableStateOf(
                    dialogAiHandlerState.handler?.aiHandlerInfo?.value?.model?.llmName
                )
            }
            val expandedModelMenu = remember { mutableStateOf(false) }
            val selectedModelOption = remember {
                mutableStateOf(dialogAiHandlerState.handler?.aiHandlerInfo?.value?.model)
            }
            val apiKey = remember {
                mutableStateOf(
                    dialogAiHandlerState.handler?.aiHandlerInfo?.value?.aiApiKey ?: ""
                )
            }

            Header {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText(
                        modifier = Modifier,
                        stringRes = if (dialogAiHandlerState.handler != null) {
                            R.string.edit_handler_title
                        } else {
                            R.string.add_handler_title
                        },
                        appTextConfig = rememberAppTextConfig(textStyle = MaterialTheme.typography.displayMedium,
                            textAlign = TextAlign.Center)
                    )
                }
            }
            Content {
                // Название
                AppEditOutlinedText(
                    value = name.value,
                    onValueChanged = { name.value = it },
                    labelRes = R.string.handler_name_hint,
                    placeholderRes = R.string.app_name,
                    modifier = Modifier
                )

                    // Выбор LLM
                    ExposedDropdownMenuBox(
                        expanded = isLLMMenuExpanded,
                        onExpandedChange = { isLLMMenuExpanded = !isLLMMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedLLM.value?.name ?: "",
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
                                .menuAnchor()
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
                                        selectedModelOption.value = null
                                        selectedLLM.value = option
                                        event(ChatBotViewModel.Event.GetModels(option))
                                        isLLMMenuExpanded = false
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
                                value = selectedModelOption.value?.modelName ?: "",
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
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedModelMenu.value,
                                onDismissRequest = { expandedModelMenu.value = false }
                            ) {
                                modelList.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                option.modelName,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        onClick = {
                                            selectedModelOption.value = option
                                            expandedModelMenu.value = false
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
                            onClick = { dialogAiHandlerState.dismiss() }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (dialogAiHandlerState.handler != null) {
                                    event(
                                        ChatBotViewModel.Event.UpdateHandler(
                                            handler = dialogAiHandlerState.handler!!,
                                            aiHandlerInfo = dialogAiHandlerState.handler!!
                                                .aiHandlerInfo.value.copy(
                                                    name = name.value,
                                                    isSelected = true,
                                                    isActive = true,
                                                    model = selectedModelOption.value!!,
                                                    aiApiKey = apiKey.value
                                                )
                                        )
                                    )
                                } else {
                                    if (selectedModelOption.value != null) {
                                        event(
                                            ChatBotViewModel.Event.AddAiHandler(
                                                aiHandlerInfo = AiHandlerInfo(
                                                    name = name.value.ifBlank { "Новая модель" },
                                                    isSelected = true,
                                                    isActive = true,
                                                    model = selectedModelOption.value!!,
                                                    aiApiKey = apiKey.value
                                                )
                                            )
                                        )
                                    }
                                }
                                dialogAiHandlerState.dismiss()
                            },
                            enabled = selectedModelOption.value != null && name.value.isNotBlank()
                        ) {
                            Text(
                                stringResource(
                                    if (dialogAiHandlerState.handler != null) {
                                        R.string.save
                                    } else {
                                        R.string.add
                                    }

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
fun AiHandlerBox(
    modifier: Modifier = Modifier,
    aiModelHandler: AiModelHandler,
    event: (ChatBotViewModel.Event) -> Unit,
    eventToDialog: (AiModelHandler) -> Unit,
) {
    val handlerInfo by aiModelHandler.aiHandlerInfo.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier
            .animateContentSize()
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        border = BorderStroke(0.1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Информация о модели
            Column(
                modifier = Modifier.weight(0.3f)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    text = handlerInfo.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    text = handlerInfo.model.modelName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопка редактирования
                IconButton(
                    onClick = { eventToDialog(aiModelHandler) },
                    modifier = Modifier
                        .size(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Переключатель активности
                Switch(
                    modifier = Modifier.scale(0.65f),
                    checked = handlerInfo.isSelected,
                    onCheckedChange = {
                        event(
                            ChatBotViewModel.Event.UpdateHandler(
                                handler = aiModelHandler,
                                aiHandlerInfo = handlerInfo.copy(isSelected = !handlerInfo.isSelected)
                            )
                        )
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primaryContainer,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.error,
                        uncheckedTrackColor = MaterialTheme.colorScheme.errorContainer
                    )
                )
                val isDeleteDialogShow = remember { mutableStateOf(false) }
                // Кнопка удаления
                IconButton(
                    onClick = {
                        isDeleteDialogShow.value = true
                    },
                    modifier = Modifier.size(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                ConfirmationDialog(
                    modifier = Modifier.wrapContentSize(),
                    show = isDeleteDialogShow.value,
                    onDismiss = {
                        isDeleteDialogShow.value = !isDeleteDialogShow.value
                    },
                    onConfirm = {
                        event(ChatBotViewModel.Event.DeleteHandler(handler = aiModelHandler))
                    },
                    title = stringResource(
                        R.string.delete_ai_handler,
                        aiModelHandler.aiHandlerInfo.value.name
                    ),
                    description = ""
                )
            }
        }
    }
}

@Preview
@Composable
fun AiHandlerBoxPreview() {
    AiHandlerBox(
        aiModelHandler = GroqApiHandler(
            httpClient = HttpClient(),
            initAiHandlerInfo = AiHandlerInfo.DEFAULT_AI_HANDLER_INFO_GROQ
        ),
        event = {},
        eventToDialog = {}
    )
}

@Preview
@Composable
fun AiHandlerScreenPreview() {
    AiHandlerScreen(
        modifier = Modifier
            .wrapContentSize(),
        listAiModelHandler = persistentListOf(
            GroqApiHandler(
                httpClient = HttpClient(),
                initAiHandlerInfo = AiHandlerInfo.DEFAULT_AI_HANDLER_INFO_GROQ
            )
        )
    )
}

@Preview
@Composable
fun DialogAiHandlerPreview() {
    DialogAiHandler(
        modifier = Modifier,
        event = {},
        dialogAiHandlerState = DialogAiHandlerState(isShowing = true),
        modelList = persistentListOf()
    )
}