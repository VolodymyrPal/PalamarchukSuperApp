package com.hfad.palamarchuksuperapp.ui.compose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.entities.Role
import com.hfad.palamarchuksuperapp.data.repository.ChatAiRepositoryImpl
import com.hfad.palamarchuksuperapp.data.services.GeminiApiHandler
import com.hfad.palamarchuksuperapp.data.services.GroqApiHandler
import com.hfad.palamarchuksuperapp.data.services.OpenAIApiHandler
import com.hfad.palamarchuksuperapp.domain.models.Error
import com.hfad.palamarchuksuperapp.ui.viewModels.ChatBotViewModel
import com.hfad.palamarchuksuperapp.ui.viewModels.daggerViewModel
import com.theapache64.rebugger.Rebugger
import io.ktor.client.HttpClient
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


@OptIn(ExperimentalMaterial3Api::class)
@Suppress("detekt.FunctionNaming", "detekt.LongMethod")
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    chatBotViewModel: ChatBotViewModel = daggerViewModel<ChatBotViewModel>
        (factory = LocalContext.current.appComponent.viewModelFactory()),
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val myState by chatBotViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        chatBotViewModel.effect.collect { effect ->
            when (effect) {
                is ChatBotViewModel.Effect.ShowToast -> {
                    Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Chat ${
                            myState.currentModel.modelName.replace("models/", "")
                        }",
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    val isExpanded = remember { mutableStateOf(false) }
                    DropdownMenu(
                        expanded = isExpanded.value,
                        onDismissRequest = { isExpanded.value = false },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        for (item in myState.listOfModels.filter { it.isSupported }) {
                            DropdownMenuItem(
                                text = { Text(item.modelName) },
                                onClick = {
                                    chatBotViewModel.event(ChatBotViewModel.Event.ChangeAiModel(item))
                                    isExpanded.value = false
                                }
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            Log.d("TAG", "${isExpanded.value}")
                            isExpanded.value = !isExpanded.value
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            color = Color.Transparent,
            modifier = modifier
                .fillMaxSize()
                .padding(
                    bottom = paddingValues.calculateBottomPadding(),
                    top = paddingValues.calculateTopPadding()
                )
        ) {

            LazyChatScreen(
                modifier = Modifier.fillMaxWidth(),
                messagesList = myState.listMessage,
                loading = myState.isLoading,
                event = chatBotViewModel::event,
                error = myState.error
            )
        }
    }
}

@Composable
fun LazyChatScreen(
    modifier: Modifier = Modifier,
    messagesList: PersistentList<MessageAI> = persistentListOf(),
    loading: Boolean = false,
    event: (ChatBotViewModel.Event) -> Unit = {},
    error: Error? = null,
) {
    val brush = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        )
    )
    val state = rememberLazyListState()
    Rebugger(
        trackMap = mapOf(
            "listMessage" to messagesList,
            "isLoading" to loading,
            "event" to event,
            "modifier" to modifier,
            "error" to error,
            "Lazy state" to state
        )
    )

    LaunchedEffect(messagesList.size) {
        launch {
            state.animateScrollToItem(messagesList.lastIndex + 3)
        }
    }
    LazyColumn(
        modifier = modifier.background(brush),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        state = state,
        contentPadding = PaddingValues(10.dp, 10.dp, 10.dp, 0.dp)
    ) {
        items(messagesList.size) {
            when (messagesList[it].type) {
                MessageType.TEXT -> {
                    MessageBox(
                        text = messagesList[it].content.trimEnd(),
                        isUser = messagesList[it].role == Role.USER
                    )
                }

                MessageType.IMAGE -> {
                    MessageBox(
                        text = messagesList[it].content.trimEnd(),
                        isUser = messagesList[it].role == Role.USER
                    )
                    val imageBytes =
                        Base64.decode(messagesList[it].otherContent as String, Base64.DEFAULT)
                    val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    AsyncImage(
                        model = image,
                        contentDescription = "Image u push to AI"
                    )
                }
            }
        }
//            when (error as AppError.Network) {
//                else -> {} // TODO if specific error, show something new
//            }

//        item {
//            Button(
//                onClick = {
//                    if (promptText.value.isNotBlank()) {
//                        event.invoke(
//                            ChatBotViewModel.Event.SendImage(
//                                promptText.value,
//                                image = "https://n1s1.hsmedia.ru/b3/10/ae/b310ae7a1baeaec4df75db18b5465ebc/1501x843_0x4U9bTTLH_1708972820638352229.jpg"
//                            )
//                        )
//                        promptText.value = ""
//                    } else {
//                        event.invoke(ChatBotViewModel.Event.ShowToast("Please enter a message"))
//                    }
//                },
//                modifier = Modifier,
//                enabled = loading.not()
//            ) {
//                Text("Send photoMessage to Bot")
//            }
//        }
        item {
            RequestPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                onEvent = event,
                loading = loading
            )
        }
    }
}

@Composable
fun MessageBox(
    modifier: Modifier = Modifier,
    text: String = "",
    isUser: Boolean = true,
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            modifier = modifier
                .align(if (isUser) Alignment.CenterEnd else Alignment.CenterStart)
                .fillMaxWidth(0.8f)
                .wrapContentSize(if (isUser) Alignment.CenterEnd else Alignment.CenterStart)
                .sizeIn(minWidth = 50.dp)
                .background(
                    if (isUser) MaterialTheme.colorScheme.primaryContainer
                    else Color.Transparent,
                    shape = RoundedCornerShape(10.dp, 10.dp, if (isUser) 0.dp else 10.dp, 10.dp)
                )
                .padding(15.dp, 5.dp, 15.dp, 5.dp),
            text = text,
            color = if (!isUser) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun RequestPanel(
    modifier: Modifier = Modifier,
    onEvent: (ChatBotViewModel.Event) -> Unit = {},
    loading: Boolean = false,
    promptText: MutableState<String> = remember { mutableStateOf("") },
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val context = LocalContext.current
        val imageBitmap = remember { mutableStateOf<Bitmap?>(null) }
        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                imageBitmap.value = context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)
                }
            }
        }

        IconButton(
            modifier = Modifier
                .weight(0.1f)
                .align(Alignment.Bottom),
            colors = IconButtonColors(
                Color.Transparent,
                MaterialTheme.colorScheme.onPrimaryContainer,
                Color.Transparent,
                Color.Transparent
            ),
            onClick = {
                galleryLauncher.launch("image/*")
            }
        ) {
            Icon(
                modifier = Modifier,
                imageVector = Icons.Filled.Add,
                contentDescription = "Add image",
            )
        }

        if (imageBitmap.value != null) {
            AsyncImage(
                model = imageBitmap.value, contentDescription = "image to send",
                modifier = Modifier.weight(0.1f)
            )
        }
        TextFieldRequest(
            promptText = promptText,
            onValueChange = { text: String -> promptText.value = text },
            modifier = Modifier.weight(0.7f)
        )

        IconButton(
            modifier = Modifier
                .weight(0.1f)
                .align(Alignment.Bottom),
            colors = IconButtonColors(
                Color.Transparent,
                MaterialTheme.colorScheme.onPrimaryContainer,
                Color.Transparent,
                Color.Transparent
            ),
            onClick = {
                if (promptText.value.isNotBlank()) {
                    when (imageBitmap.value) {
                        null -> {
                            onEvent.invoke(
                                ChatBotViewModel.Event.SendText(
                                    promptText.value,
                                )
                            )
                        }

                        else -> {

                            val imgByteCode = ByteArrayOutputStream().let {
                                imageBitmap.value?.compress(Bitmap.CompressFormat.JPEG, 80, it)
                                Base64.encodeToString(it.toByteArray(), Base64.NO_WRAP)
                            }

                            onEvent.invoke(
                                ChatBotViewModel.Event.SendImage(
                                    promptText.value,
                                    imgByteCode
                                )
                            )
                        }
                    }
                    promptText.value = ""
                } else {
                    onEvent.invoke(ChatBotViewModel.Event.ShowToast("Please enter a message"))
                }
            },
            enabled = loading.not()
        ) {
            Icon(
                modifier = Modifier,
                imageVector = Icons.AutoMirrored.Rounded.Send,
                contentDescription = "Send",
            )
        }
    }
}

@Composable
fun TextFieldRequest(
    promptText: MutableState<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = promptText.value,
        modifier = modifier,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedTextColor = Color.Red,
            unfocusedTextColor = Color.Red,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            if (promptText.value.isBlank()) Text(
                "Enter a message",
                color = Color.Gray.copy(alpha = 0.5f)
            )
        },
        maxLines = 3,
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)

    )
}

@Preview
@Composable
fun RequestPanelPreview() {
    RequestPanel(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
    )
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        modifier = Modifier.fillMaxSize(),
        chatBotViewModel = ChatBotViewModel(
            chatAiRepository = ChatAiRepositoryImpl(
                groqApiHandler = GroqApiHandler(
                    httpClient = HttpClient()
                ),
                geminiApiHandler = GeminiApiHandler(
                    httpClient = HttpClient()
                ), openAIApiHandler = OpenAIApiHandler(
                    httpClient = HttpClient()
                )
            )
        )
    )
}