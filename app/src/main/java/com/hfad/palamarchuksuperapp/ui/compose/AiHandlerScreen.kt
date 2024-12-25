package com.hfad.palamarchuksuperapp.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.LLMName
import com.hfad.palamarchuksuperapp.data.services.GroqApiHandler
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
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
                modifier = Modifier.fillMaxWidth(),
                aiModelHandler = item,
                index = index,
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
    modelList: PersistentList<AiModel> = persistentListOf(),
    dialogAiHandlerState: DialogAiHandlerState = remember { DialogAiHandlerState() },
) {
    if (dialogAiHandlerState.isShowing) {
        Dialog(
            onDismissRequest = { dialogAiHandlerState.dismiss() },
            properties = DialogProperties()
        ) {
            Surface(modifier = modifier.wrapContentSize()) {

                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
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
                    val selectedModelOption: MutableState<AiModel?> =
                        remember { mutableStateOf(dialogAiHandlerState.handler?.aiHandlerInfo?.value?.model) }
                    val apiKey =
                        remember {
                            mutableStateOf(
                                dialogAiHandlerState.handler?.aiHandlerInfo?.value?.aiApiKey ?: ""
                            )
                        }

                    key(name) {
                        TextField(
                            label = {
                                Text(
                                    "Description",
                                    color = if (selectedLLM.value?.name.isNullOrBlank()) {
                                        Color.Black.copy(alpha = 0.4f)
                                    } else {
                                        Color.Black
                                    }
                                )
                            },
                            value = name.value,
                            onValueChange = { name.value = it },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    ExposedDropdownMenuBox(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = isLLMMenuExpanded,
                        onExpandedChange = {
                            isLLMMenuExpanded = !isLLMMenuExpanded
                        },
                    ) {
                        TextField(
                            value = selectedLLM.value?.name ?: "",
                            onValueChange = { },
                            label = {
                                Text(
                                    "Language model",
                                    color = if (selectedLLM.value?.name.isNullOrBlank()) {
                                        Color.Black.copy(alpha = 0.4f)
                                    } else {
                                        Color.Black
                                    }
                                )
                            },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = isLLMMenuExpanded,
                            onDismissRequest = { isLLMMenuExpanded = false }
                        ) {
                            LLMName.entries.forEach { option ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedModelOption.value = null
                                        selectedLLM.value = option
                                        event(ChatBotViewModel.Event.GetModels(option))
                                        isLLMMenuExpanded = false
                                    },
                                    text = { Text(option.name) }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = expandedModelMenu.value,
                        onExpandedChange = {
                            expandedModelMenu.value = !isLLMMenuExpanded
                        },
                    ) {
                        TextField(
                            value = selectedModelOption.value?.modelName ?: "",
                            onValueChange = { },
                            placeholder = {
                                if (selectedModelOption.value?.modelName.isNullOrBlank()) Text(
                                    "Select model of language model",
                                    color = Color.Black.copy(0.4f)
                                )
                            },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedModelMenu.value,
                            onDismissRequest = { expandedModelMenu.value = false }
                        ) {
                            modelList.forEach { option ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedModelOption.value = option
                                        expandedModelMenu.value = false
                                    },
                                    text = { Text(option.modelName) }
                                )
                            }
                        }
                    }

                    key(apiKey) {
                        TextField(
                            value = apiKey.value,
                            placeholder = {
                                if (apiKey.value.isBlank()) Text(
                                    text = "Put api key here",
                                    color = Color.Black.copy(alpha = 0.4f)
                                )
                            },
                            label = { if (apiKey.value.isNotBlank()) Text("API Key") },
                            onValueChange = {
                                apiKey.value = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    IconButton(
                        modifier = Modifier,
                        onClick = {
                            if (dialogAiHandlerState.handler != null) {
                                event.invoke(
                                    ChatBotViewModel.Event.UpdateHandler(
                                        handler = dialogAiHandlerState.handler!!,
                                        aiHandlerInfo = dialogAiHandlerState.handler!!.aiHandlerInfo.value.copy(
                                            name = name.value,
                                            isSelected = true,
                                            isActive = true,
                                            model = selectedModelOption.value!!,
                                            aiApiKey = apiKey.value
                                        )
                                    )
                                )
                                dialogAiHandlerState.dismiss()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add"
                        )
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
            Text(text = "${index + 1}. ", maxLines = 1, modifier = Modifier.weight(0.1f))
            Text(text = handlerInfo.name, maxLines = 1, modifier = Modifier.weight(0.8f))
            IconButton(
                modifier = Modifier.weight(0.1f),
                onClick = {
                    eventToDialog.invoke(handlerInfo)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "Change"
                )
            }
            Checkbox(
                modifier = Modifier.weight(0.1f),
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
                modifier = Modifier.weight(0.1f),
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