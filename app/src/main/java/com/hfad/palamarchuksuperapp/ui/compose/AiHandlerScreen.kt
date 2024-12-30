package com.hfad.palamarchuksuperapp.ui.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hfad.palamarchuksuperapp.data.services.GroqApiHandler
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.LLMName
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
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
        modifier = Modifier,
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

//@OptIn(ExperimentalMaterial3Api::class)
//@Suppress("FunctionNaming", "LongMethod", "MagicNumber")
//@Composable
//fun DialogAiHandler(
//    modifier: Modifier = Modifier,
//    event: (ChatBotViewModel.Event) -> Unit,
//    modelList: PersistentList<AiModel> = persistentListOf(),
//    dialogAiHandlerState: DialogAiHandlerState = remember { DialogAiHandlerState() },
//) {
//    if (dialogAiHandlerState.isShowing) {
//        Dialog(
//            onDismissRequest = { dialogAiHandlerState.dismiss() },
//            properties = DialogProperties()
//        ) {
//            Surface(modifier = modifier.wrapContentSize()) {
//
//                Column(
//                    verticalArrangement = Arrangement.SpaceEvenly,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//
//                    val name = remember {
//                        mutableStateOf(
//                            dialogAiHandlerState.handler?.aiHandlerInfo?.value?.name ?: ""
//                        )
//                    }
//                    var isLLMMenuExpanded by remember { mutableStateOf(false) }
//                    val selectedLLM = remember {
//                        mutableStateOf(
//                            dialogAiHandlerState.handler?.aiHandlerInfo?.value?.model?.llmName
//                        )
//                    }
//                    val expandedModelMenu = remember { mutableStateOf(false) }
//                    val selectedModelOption: MutableState<AiModel?> =
//                        remember { mutableStateOf(dialogAiHandlerState.handler?.aiHandlerInfo?.value?.model) }
//                    val apiKey =
//                        remember {
//                            mutableStateOf(
//                                dialogAiHandlerState.handler?.aiHandlerInfo?.value?.aiApiKey ?: ""
//                            )
//                        }
//
//                    key(name) {
//                        TextField(
//                            label = {
//                                Text(
//                                    "Description",
//                                    color = Color.Black.copy(alpha = 0.4f)
//                                )
//                            },
//                            value = name.value,
//                            onValueChange = { name.value = it },
//                            modifier = Modifier.fillMaxWidth(),
//                        )
//                    }
//
//                    ExposedDropdownMenuBox(
//                        modifier = Modifier.fillMaxWidth(),
//                        expanded = isLLMMenuExpanded,
//                        onExpandedChange = {
//                            isLLMMenuExpanded = !isLLMMenuExpanded
//                        },
//                    ) {
//                        TextField(
//                            value = selectedLLM.value?.name ?: "",
//                            onValueChange = { },
//                            label = {
//                                Text(
//                                    "Language model",
//                                    color = Color.Black.copy(alpha = 0.4f)
//                                )
//                            },
//                            readOnly = true,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .menuAnchor()
//                        )
//                        ExposedDropdownMenu(
//                            expanded = isLLMMenuExpanded,
//                            onDismissRequest = { isLLMMenuExpanded = false }
//                        ) {
//                            LLMName.entries.forEach { option ->
//                                DropdownMenuItem(
//                                    onClick = {
//                                        selectedModelOption.value = null
//                                        selectedLLM.value = option
//                                        event(ChatBotViewModel.Event.GetModels(option))
//                                        isLLMMenuExpanded = false
//                                    },
//                                    text = { Text(option.name) }
//                                )
//                            }
//                        }
//                    }
//
//                    if (selectedLLM.value != null) {
//                        ExposedDropdownMenuBox(
//                            modifier = Modifier.fillMaxWidth(),
//                            expanded = expandedModelMenu.value,
//                            onExpandedChange = {
//                                expandedModelMenu.value = !isLLMMenuExpanded
//                            },
//                        ) {
//                            TextField(
//                                value = selectedModelOption.value?.llmName?.name ?: "",
//                                onValueChange = { },
//                                label = {
//                                    Text(
//                                        "Select model of language model",
//                                        color = Color.Black.copy(alpha = 0.4f)
//                                    )
//                                },
//                                readOnly = true,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .menuAnchor()
//                            )
//                            ExposedDropdownMenu(
//                                expanded = expandedModelMenu.value,
//                                onDismissRequest = { expandedModelMenu.value = false }
//                            ) {
//                                modelList.forEach { option ->
//                                    DropdownMenuItem(
//                                        onClick = {
//                                            selectedModelOption.value = option
//                                            expandedModelMenu.value = false
//                                        },
//                                        text = { Text(option.modelName) }
//                                    )
//                                }
//                            }
//                        }
//                    }
//
//                    key(apiKey) {
//                        TextField(
//                            value = apiKey.value,
//                            label = {
//                                Text(
//                                    "API Key",
//                                    color = Color.Black.copy(alpha = 0.4f)
//                                )
//                            },
//                            onValueChange = {
//                                apiKey.value = it
//                            },
//                            modifier = Modifier.fillMaxWidth(),
//                        )
//                    }
//
//                    IconButton(
//                        modifier = Modifier,
//                        onClick = {
//                            if (dialogAiHandlerState.handler != null) {
//                                event.invoke(
//                                    ChatBotViewModel.Event.UpdateHandler(
//                                        handler = dialogAiHandlerState.handler!!,
//                                        aiHandlerInfo = dialogAiHandlerState.handler!!.aiHandlerInfo.value.copy(
//                                            name = name.value,
//                                            isSelected = true,
//                                            isActive = true,
//                                            model = selectedModelOption.value!!,
//                                            aiApiKey = apiKey.value
//                                        )
//                                    )
//                                )
//                            } else {
//                                if (selectedModelOption.value != null) {
//                                    event.invoke(
//                                        ChatBotViewModel.Event.AddAiHandler(
//                                            aiHandlerInfo = AiHandlerInfo(
//                                                name = name.value.ifBlank { "New Model" },
//                                                isSelected = true,
//                                                isActive = true,
//                                                model = selectedModelOption.value!!,
//                                                aiApiKey = apiKey.value
//                                            )
//                                        )
//                                    )
//                                }
//                            }
//                            dialogAiHandlerState.dismiss()
//                        }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Add,
//                            contentDescription = "Add"
//                        )
//                    }
//                }
//            }
//        }
//    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@Suppress("FunctionNaming", "LongMethod", "MagicNumber")
@Composable
fun DialogAiHandler(
    modifier: Modifier = Modifier,
    event: (ChatBotViewModel.Event) -> Unit,
    modelList: PersistentList<AiModel>,
    dialogAiHandlerState: DialogAiHandlerState
) {
    if (dialogAiHandlerState.isShowing) {
        Dialog(
            onDismissRequest = { dialogAiHandlerState.dismiss() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
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
                        text = if (dialogAiHandlerState.handler != null)
                            "Редактировать модель" else "Добавить новую модель",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Поля ввода
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

                    // Название
                    OutlinedTextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        label = { Text("Название") },
                        modifier = Modifier.fillMaxWidth(),
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
                            label = { Text("Языковая модель") },
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
                                    text = { Text(option.name) },
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
                            onExpandedChange = { expandedModelMenu.value = !expandedModelMenu.value }
                        ) {
                            OutlinedTextField(
                                value = selectedModelOption.value?.modelName ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Модель") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandedModelMenu.value
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedModelMenu.value,
                                onDismissRequest = { expandedModelMenu.value = false }
                            ) {
                                modelList.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.modelName) },
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
                        label = { Text("API Ключ") },
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
                            Text("Отмена")
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
                                if (dialogAiHandlerState.handler != null)
                                    "Сохранить" else "Добавить"
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
    index: Int,
    event: (ChatBotViewModel.Event) -> Unit,
    eventToDialog: (AiModelHandler) -> Unit,
) {
    val handlerInfo by aiModelHandler.aiHandlerInfo.collectAsStateWithLifecycle()
    Box(modifier = modifier) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${index + 1}. ", maxLines = 1, modifier = Modifier.weight(0.2f))
            Text(text = handlerInfo.name, maxLines = 1, modifier = Modifier.weight(0.4f))
            IconButton(
                modifier = Modifier.weight(0.2f),
                onClick = {
                    eventToDialog.invoke(aiModelHandler)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "Change"
                )
            }
            Checkbox(
                modifier = Modifier.weight(0.2f),
                checked = handlerInfo.isSelected,
                onCheckedChange = {
                    event.invoke(
                        ChatBotViewModel.Event.UpdateHandler(
                            handler = aiModelHandler,
                            aiHandlerInfo = handlerInfo.copy(isSelected = !handlerInfo.isSelected)
                        )
                    )
                }
            )
            IconButton(
                modifier = Modifier.weight(0.2f),
                onClick = {
                    event.invoke(
                        ChatBotViewModel.Event.DeleteHandler(
                            handler = aiModelHandler
                        )
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Add"
                )
            }
        }
    }
}

@Preview
@Composable
fun AiHandlerScreenPreview() {
    AiHandlerScreen(
        modifier = Modifier
            .wrapContentSize()
            .background(Color.White),
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
        dialogAiHandlerState = DialogAiHandlerState(isShowing = true)
    )
}