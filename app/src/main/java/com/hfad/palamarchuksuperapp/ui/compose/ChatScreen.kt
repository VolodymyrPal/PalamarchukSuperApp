package com.hfad.palamarchuksuperapp.ui.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageAiContent
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Role
import com.hfad.palamarchuksuperapp.domain.models.SubMessageAI
import com.hfad.palamarchuksuperapp.data.repository.MockChat
import com.hfad.palamarchuksuperapp.domain.models.Error
import com.hfad.palamarchuksuperapp.ui.reusable.doublePulseEffect
import com.hfad.palamarchuksuperapp.ui.viewModels.ChatBotViewModel
import com.hfad.palamarchuksuperapp.ui.viewModels.daggerViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@Suppress("detekt.FunctionNaming", "detekt.LongMethod")
@Composable
fun RootChatScreen(
    modifier: Modifier = Modifier,
    chatBotViewModel: ChatBotViewModel = daggerViewModel<ChatBotViewModel>
        (factory = LocalContext.current.appComponent.viewModelFactory()),
    navController: NavHostController? = LocalNavController.current,
    context: Context = LocalContext.current,

    ) {
    LaunchedEffect(Unit) {
        chatBotViewModel.effect.collect { effect ->
            when (effect) {
                is ChatBotViewModel.Effect.ShowToast -> {
                    Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val myState = chatBotViewModel.uiState.collectAsStateWithLifecycle()

    ChatScreen(
        modifier = modifier,
        navController = navController,
        event = chatBotViewModel::event,
        state = myState
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Suppress("detekt.FunctionNaming", "detekt.LongMethod")
@NonRestartableComposable
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = LocalNavController.current,
    event: (ChatBotViewModel.Event) -> Unit,
    state: State<ChatBotViewModel.StateChat> = mutableStateOf(
        ChatBotViewModel.StateChat(modelList = persistentListOf())
    ),
) {
    val listState = rememberLazyListState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Chat",
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController?.popBackStack()
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
                        AiHandlerScreen(
                            modifier = Modifier.size(200.dp),
                            listAiModelHandler = state.value.listHandler,
                            event = event,
                            aiModelList = state.value.modelList
                        )
                    }
                    IconButton(
                        onClick = {
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
        },
        floatingActionButton = {
            FabScrollLastItem(
                modifier = Modifier
                    .offset(0.dp, 25.dp)
                    .doublePulseEffect(), //TODO pulse effect улучшить
                listState = listState,
                list = { state.value.listMessage },
            )
        },
        bottomBar = {
            RequestPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 0.dp, 10.dp, 5.dp),
                onEvent = event,
                loading = state.value.isLoading,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    ) { paddingValues ->
        Surface(
            color = Color.Transparent,
            modifier = modifier
                .fillMaxSize()
                .padding(
                    //bottom = paddingValues.calculateBottomPadding(),
                    top = paddingValues.calculateTopPadding()
                )
        ) {
            LazyChatScreen(
                modifier = Modifier
                    .fillMaxWidth(),
                messagesList = { state.value.listMessage },
                loading = { state.value.isLoading },
                event = event,
                error = { state.value.error },
                bottomPaddings = paddingValues.calculateBottomPadding(),
                state = listState
            )
        }
    }
}

@Composable
@Suppress("LongParameterList", "FunctionNaming")
fun LazyChatScreen(
    modifier: Modifier = Modifier,
    messagesList: () -> PersistentList<MessageAI> = { persistentListOf() }, // TODO lambda passing
    loading: () -> Boolean = { false }, // TODO lambda passing
    event: (ChatBotViewModel.Event) -> Unit = {},
    error: () -> Error? = { null }, // TODO lambda passing
    state: LazyListState = rememberLazyListState(),
    bottomPaddings: Dp = 0.dp,
) {
    val brush = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        )
    )

    LaunchedEffect(messagesList().size) { //TODO lambda invoke
        launch {
            state.animateScrollToItem(messagesList().lastIndex + 3)
        }
    }
    LazyColumn(
        modifier = modifier.background(brush),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        state = state,
        contentPadding = PaddingValues(10.dp, 10.dp, 10.dp, 0.dp)
    ) {
        items(
            messagesList(),
            key = { it.id }
        ) {
            when (it.type) {
                MessageType.TEXT -> {
                    MessageBox(
                        subMessageList = it.content,
                        isUser = it.role == Role.USER,
                        event = event,
                        messageAiIndex = { it.id }
                    )
                }

                else -> {

                }
            }
        }
        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomPaddings)
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
fun MessageBox(
    modifier: Modifier = Modifier,
    subMessageList: PersistentList<SubMessageAI> = persistentListOf(SubMessageAI(message = "test")),
    isUser: Boolean = true,
    event: (ChatBotViewModel.Event) -> Unit,
    messageAiIndex: () -> Int = { 0 },
    pagerState: PagerState = rememberPagerState(pageCount = { subMessageList.size }),
) {
    HorizontalPager(
        modifier = modifier.fillMaxWidth(),
        state = pagerState,
        // contentPadding = PaddingValues(10.dp, 10.dp, 10.dp, 0.dp)
    ) { page ->
        LaunchedEffect(pagerState.currentPage) {
            event(
                ChatBotViewModel.Event.ChooseSubMessage(
                    messageAiIndex(),
                    pagerState.currentPage
                )
            )
        }
        when (subMessageList[page].otherContent == null) {
            true -> {
                Box {
                    Box(
                        modifier = modifier
                            .align(if (isUser) Alignment.CenterEnd else Alignment.CenterStart)
                            .fillMaxWidth(1f)
                            .wrapContentSize(if (isUser) Alignment.CenterEnd else Alignment.CenterStart)
                            .sizeIn(minWidth = 50.dp)
                            .background(
                                if (isUser) MaterialTheme.colorScheme.primaryContainer
                                else Color.Transparent,
                                shape = RoundedCornerShape(
                                    10.dp,
                                    10.dp,
                                    if (isUser) 0.dp else 10.dp,
                                    10.dp
                                )
                            )
                            .padding(15.dp, 5.dp, 15.dp, 5.dp),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MarkdownText(subMessageList[page].message.trimEnd())
                            if (subMessageList[page].model != null) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "${
                                        subMessageList[page].model?.modelName?.replace(
                                            "models/",
                                            ""
                                        )
                                    }",
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    fontSize = TextUnit(12f, TextUnitType.Sp),
                                    textAlign = TextAlign.End,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }

            false -> {
                val imageBytes =
                    Base64.decode(
                        (subMessageList[page].otherContent as MessageAiContent.Image).image,
                        Base64.DEFAULT
                    )
                val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                AsyncImage(
                    model = image,
                    contentDescription = "Image u push to AI"
                )
            }
        }
    }
    if (subMessageList.size > 1) {
        LazyRow(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            items(subMessageList.size) {
                val color =
                    if (pagerState.currentPage == it) Color.DarkGray else Color.LightGray
                if (subMessageList[it].loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(10.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(6.dp)
                    )
                }
            }
        }
    }
}

@Suppress("LongParameterList")
@Composable
fun RequestPanel(
    modifier: Modifier = Modifier,
    onEvent: (ChatBotViewModel.Event) -> Unit = {},
    loading: Boolean = false,
    containerColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    promptText: MutableState<String> = rememberSaveable { mutableStateOf("") },
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = CircleShape,
        modifier = modifier
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
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
                    .align(Alignment.CenterVertically),
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
                modifier = Modifier.weight(0.8f)
            )

            IconButton(
                modifier = Modifier
                    .weight(0.1f)
                    .align(Alignment.CenterVertically),
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
}

@Composable
fun FabScrollLastItem(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    list: () -> List<MessageAI> = { emptyList() },
) {
    val coroutineScope = rememberCoroutineScope()
    val showFab = remember {
        derivedStateOf {
            if (listState.layoutInfo.visibleItemsInfo.lastOrNull() == null) {
                false
            } else {
                listState.layoutInfo.visibleItemsInfo.lastOrNull()!!.index !=
                        listState.layoutInfo.totalItemsCount - 1
            }
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = showFab.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        SmallFloatingActionButton(
            shape = CircleShape,
            modifier = Modifier,
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0f),
            onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(list().lastIndex + 1)
                }
            },
            interactionSource = object : MutableInteractionSource {
                override val interactions: Flow<Interaction> = emptyFlow()
                override suspend fun emit(interaction: Interaction) {}
                override fun tryEmit(interaction: Interaction) = true
            },
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            content = {
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    "Floating action button.",
                )
            }
        )
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
        event = {},
        navController = null,
        state = mutableStateOf(
            ChatBotViewModel.StateChat(
                listMessage = MockChat.value.toPersistentList(),
                modelList = persistentListOf()
            )
        )
    )
}