package com.hfad.palamarchuksuperapp.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.runtime.getValue
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
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.LLMName
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
) {
    val dialogShown = remember { mutableStateOf(false) }
    if (dialogShown.value) DialogAiHandler(
        modifier = Modifier.fillMaxSize(),
        event = event,
        onDismiss = { dialogShown.value = false }
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
                        dialogShown.value = true
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
                modifier = Modifier,
                aiModelHandler = item,
                index = index,
                event = event
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("FunctionNaming", "LongMethod", "MagicNumber")
@Composable
fun DialogAiHandler(
    modifier: Modifier = Modifier,
    event: (ChatBotViewModel.Event) -> Unit,
    onDismiss: () -> Unit,
    modelList : List<AiModel> = listOf(AiModel.GROQ_BASE_MODEL, AiModel.GEMINI_BASE_MODEL, AiModel.OPENAI_BASE_MODEL)
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Surface(modifier = modifier.wrapContentSize()) {

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val name = remember { mutableStateOf("") }
                var expandedLLMMenu by remember { mutableStateOf(false) }
                var selectedLLMOption by remember { mutableStateOf("") }
                var expandedModelMenu by remember { mutableStateOf(false) }
                var selectedModelOption by remember { mutableStateOf("") }
                val apiKey = remember { mutableStateOf("") }

                TextField(
                    placeholder = {
                        if (name.value.isBlank()) Text(
                            "Put name here",
                            color = Color.Black.copy(alpha = 0.4f)
                        )
                    },
                    value = name.value,
                    onValueChange = { name.value = it },
                    modifier = Modifier.fillMaxWidth(),
                )

                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expandedLLMMenu,
                    onExpandedChange = {
                        expandedLLMMenu = true
                    },
                ) {
                    TextField(
                        value = selectedLLMOption,
                        onValueChange = { selectedLLMOption = it },
                        placeholder = {
                            if (selectedLLMOption.isBlank()) Text(
                                "Select language model",
                                color = Color.Black.copy(0.4f)
                            )
                        },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedLLMMenu,
                        onDismissRequest = { expandedLLMMenu = false }
                    ) {
                        LLMName.entries.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedLLMOption = option.name
                                    expandedLLMMenu = false
                                },
                                text = { Text(option.name) }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expandedModelMenu,
                    onExpandedChange = {
                        expandedModelMenu = true
                    },
                ) {
                    TextField(
                        value = selectedModelOption,
                        onValueChange = { selectedModelOption = it },
                        placeholder = {
                            if (selectedModelOption.isBlank()) Text(
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
                        expanded = expandedModelMenu,
                        onDismissRequest = { expandedModelMenu = false }
                    ) {
                        modelList.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedModelOption = option.modelName
                                    expandedModelMenu = false
                                },
                                text = { Text(option.modelName) }
                            )
                        }
                    }
                }

                TextField(
                    value = apiKey.value,
                    placeholder = {
                        if (apiKey.value.isBlank()) Text(
                            text = "Put api key here",
                            color = Color.Black.copy(alpha = 0.4f)
                        )
                    },
                    onValueChange = {
                        apiKey.value = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                )


                IconButton(
                    modifier = Modifier,
                    onClick = {
                        event.invoke(
                            ChatBotViewModel.Event.AddAiHandler(
                                aiHandlerInfo = AiHandlerInfo(
                                    name = "New Model",
                                    isSelected = true,
                                    isActive = true,
                                    model = AiModel.GeminiModel(),
                                    aiApiKey = ""
                                )
                            )
                        )
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

@Composable
fun AiHandlerBox(
    modifier: Modifier = Modifier,
    aiModelHandler: AiModelHandler,
    index: Int,
    event: (ChatBotViewModel.Event) -> Unit,
) {
    val handlerInfo by aiModelHandler.aiHandlerInfo.collectAsStateWithLifecycle()
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${index + 1}. ")
            Text(text = handlerInfo.name)
            Checkbox(
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
                initAiHandlerInfo = AiHandlerInfo(
                    name = "Gemini fast",
                    isSelected = true,
                    isActive = false,
                    model = AiModel.GROQ_BASE_MODEL
                )
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
        onDismiss = {  }
    )
}