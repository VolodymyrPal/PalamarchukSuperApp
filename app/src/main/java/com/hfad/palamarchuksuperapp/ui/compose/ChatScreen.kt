package com.hfad.palamarchuksuperapp.ui.compose

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.data.services.ContentImage
import com.hfad.palamarchuksuperapp.data.services.ContentText
import com.hfad.palamarchuksuperapp.data.services.GroqApiHandler
import com.hfad.palamarchuksuperapp.data.services.Message
import com.hfad.palamarchuksuperapp.data.services.MessageChat
import com.hfad.palamarchuksuperapp.data.services.MessageText
import com.hfad.palamarchuksuperapp.ui.viewModels.ChatBotViewModel
import com.hfad.palamarchuksuperapp.ui.viewModels.daggerViewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Suppress("detekt.FunctionNaming", "detekt.LongMethod")
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    chatBotViewModel: ChatBotViewModel = daggerViewModel<ChatBotViewModel>
        (factory = LocalContext.current.appComponent.viewModelFactory()),
) {
    val context = LocalContext.current

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
                title = { Text("Chat", color = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = { Text("Choose AI", color = MaterialTheme.colorScheme.primary) },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
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
            val myState by chatBotViewModel.uiState.collectAsStateWithLifecycle()

            LazyChatScreen(
                modifier = Modifier,
                messagesList = myState.listMessage,
                loading = myState.isLoading,
                event = chatBotViewModel::event
            )
        }
    }
}

@Composable
fun LazyChatScreen(
    modifier: Modifier = Modifier,
    messagesList: List<Message> = emptyList(),
    loading: Boolean = false,
    event: ((ChatBotViewModel.Event) -> Unit)? = null,
) {
    var promptText: String by remember { mutableStateOf("") }
    val state = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        state = state,
        contentPadding = PaddingValues(10.dp, 10.dp, 10.dp, 0.dp)
    ) {
        items(messagesList.size) {
            LaunchedEffect(messagesList.size) {
                launch {
                    state.animateScrollToItem(messagesList.lastIndex + 3)
                }
            }
            when (messagesList[it]) {
                is MessageChat -> {
                    val isUser = remember { mutableStateOf((messagesList[it] as MessageChat).role == "user") }
                    val content = (messagesList[it] as MessageChat).content
                    for (messages in content) {
                        when (messages) {
                            is ContentText -> {
                                Text(
                                    modifier = Modifier,
                                    text = messages.text,
                                    color = Color.DarkGray,
                                    textAlign = if (isUser.value) TextAlign.End else TextAlign.Start
                                )
                            }

                            is ContentImage -> {
                                Text(
                                    text = messages.image_url.url,
                                    color = Color.Black,
                                    textAlign = if (isUser.value) TextAlign.End else TextAlign.Start
                                )
                            }
                        }
                    }
                }

                is MessageText -> {
                    val isUser = remember { mutableStateOf((messagesList[it] as MessageText).role == "user") }
                    Text(
                        text = (messagesList[it] as MessageText).content,
                        color = if ((messagesList[it]
                                    as MessageText).role == "user"
                        ) Color.Green else Color.Blue,
                        textAlign = if (isUser.value) TextAlign.End else TextAlign.Start
                    )
                }
            }

            Spacer(modifier = Modifier.size(20.dp))
        }

//        item {
//            Button(
//                onClick = {
//                    if (promptText.isNotBlank()) {
//                        event?.invoke(
//                            ChatBotViewModel.Event.SendImage(
//                                promptText,
//                                image = "https://n1s1.hsmedia.ru/b3/10/ae/b310ae7a1baeaec4df75db18b5465ebc/1501x843_0x4U9bTTLH_1708972820638352229.jpg"
//                            )
//                        )
//                        promptText = ""
//                    } else {
//                        event?.invoke(ChatBotViewModel.Event.ShowToast("Please enter a message"))
//                    }
//                },
//                modifier = Modifier,
//                enabled = loading.not()
//            ) {
//                Text("Send photoMessage to Bot")
//            }
//        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.weight(0.15f),
                    colors = IconButtonColors(
                        Color.Transparent,
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        Color.Transparent,
                        Color.Transparent
                    ),
                    onClick = {

                    }
                ) {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.AutoMirrored.Rounded.List,
                        contentDescription = "Add image",
                    )
                }
                TextField(
                    value = promptText,
                    modifier = Modifier.weight(0.7f),
                    onValueChange = { text: String -> promptText = text },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.Red,
                        unfocusedTextColor = Color.Red,
                    ),
                    maxLines = 3,
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
                IconButton(
                    modifier = Modifier.weight(0.15f),
                    colors = IconButtonColors(
                        Color.Transparent,
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        Color.Transparent,
                        Color.Transparent
                    ),
                    onClick = {
                        if (promptText.isNotBlank()) {
                            event?.invoke(
                                ChatBotViewModel.Event.SendText(
                                    promptText,
                                )
                            )
                            promptText = ""
                        } else {
                            event?.invoke(ChatBotViewModel.Event.ShowToast("Please enter a message"))
                        }
                    },
                    enabled = loading.not()
                ) {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Send",
                    )
                }
            }
        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        modifier = Modifier.fillMaxSize(),
        chatBotViewModel = ChatBotViewModel(groqApi = GroqApiHandler(httpClient = HttpClient()))
    )
}