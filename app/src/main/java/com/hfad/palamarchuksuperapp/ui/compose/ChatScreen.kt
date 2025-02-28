package com.hfad.palamarchuksuperapp.ui.compose

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hfad.palamarchuksuperapp.BackgroundMusicService
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.data.repository.MockChat
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageAiContent
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Role
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.ui.reusable.shimmerLoading
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
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalSharedTransitionApi::class)
@Suppress("detekt.FunctionNaming", "detekt.LongMethod")
@Composable
fun RootChatScreen(
    modifier: Modifier = Modifier,
    chatBotViewModel: ChatBotViewModel = daggerViewModel<ChatBotViewModel>
        (factory = LocalContext.current.appComponent.viewModelFactory()),
    navController: NavHostController? = LocalNavController.current,
    context: Context = LocalContext.current,
) {
    val intent = Intent(context, BackgroundMusicService::class.java)
    context.startService(intent)

    val localTransitionScope = LocalSharedTransitionScope.current
        ?: error(IllegalStateException("No SharedElementScope found"))
    val animatedContentScope = LocalNavAnimatedVisibilityScope.current
        ?: error(IllegalStateException("No AnimatedVisibility found"))

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
    with(localTransitionScope) { //TODO
        ChatScreen(
            modifier = modifier.sharedElement(
                this.rememberSharedContentState("chat"),
                animatedContentScope,
            ),
            navController = navController,
            event = chatBotViewModel::event,
            state = myState
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Suppress("detekt.FunctionNaming", "detekt.LongMethod")
@NonRestartableComposable
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = LocalNavController.current,
    event: (ChatBotViewModel.Event) -> Unit,
    state: State<ChatBotViewModel.StateChat>,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ChatTopBar(
                state = state,
                event = event,
                navController = navController,
            )
        },
        floatingActionButton = {
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

            FabScrollLastItem(
                modifier = Modifier
                    .offset(0.dp, 25.dp),
                visible = showFab.value,
                onScroll = {
                    scope.launch {
                        listState.animateScrollToItem(
                            state.value.chat.messageGroups.size.plus(1)
                        )
                    }
                }
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
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding()
                )
        ) {
            LazyChatScreen(
                modifier = Modifier.fillMaxWidth(),
                messagesList = state.value.chat.messageGroups.toPersistentList(),
                event = event,
                error = state.value.error,
                bottomPaddings = paddingValues.calculateBottomPadding(),
                state = listState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    state: State<ChatBotViewModel.StateChat>,
    event: (ChatBotViewModel.Event) -> Unit,
    navController: NavHostController?,
) {
    val isExpandedChats = remember { mutableStateOf(false) }
    val isExpandedHandlers = remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        title = {
            ChatTitle(
                isExpanded = isExpandedChats,
                state = state,
                event = event,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            BackButton(navController = navController)
        },
        actions = {
            MenuButton(
                isExpanded = isExpandedHandlers.value,
                listAiModelHandler = state.value.listHandler,
                aiModelList = state.value.modelList,
                onValueChange = { boolean: Boolean -> isExpandedHandlers.value = boolean },
                event = event
            )
        }
    )
}

@Composable
private fun ChatTitle(
    isExpanded: MutableState<Boolean>,
    state: State<ChatBotViewModel.StateChat>,
    event: (ChatBotViewModel.Event) -> Unit,
) {
    Row(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                isExpanded.value = true
            }
        ),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            state.value.chat.name,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Expand",
            tint = MaterialTheme.colorScheme.primary
        )
    }

    DropdownMenu(
        expanded = isExpanded.value,
        onDismissRequest = { isExpanded.value = false },
        containerColor = Color.Transparent
    ) {
        Dialog(
            onDismissRequest = { isExpanded.value = false }
        ) {
            Surface(
                modifier = Modifier.size(200.dp, 200.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.onPrimary,
                tonalElevation = 6.dp
            ) {
                // Заголовок диалога
                LazyColumn {
                    item {
                        Row {
                            IconButton(
                                onClick = { event.invoke(ChatBotViewModel.Event.CreateNewChat) }
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
                                onClick = { event.invoke(ChatBotViewModel.Event.ClearAllChats) }
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
                        state.value.chatList,
                    ) { chat ->
                        Row(
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                text = chat.name,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clickable(interactionSource = null, indication = null) {
                                        event.invoke(ChatBotViewModel.Event.SelectChat(chat.id))
                                    }
                            )
                            Text(
                                text = SimpleDateFormat("dd.MM:HH:mm", Locale.US).format(
                                    chat.timestamp
                                ),
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BackButton(navController: NavHostController?) {
    IconButton(
        onClick = { navController?.popBackStack() }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun MenuButton(
    isExpanded: Boolean,
    listAiModelHandler: PersistentList<AiModelHandler> = persistentListOf(),
    aiModelList: PersistentList<AiModel> = persistentListOf(),
    onValueChange: (Boolean) -> Unit,
    event: (ChatBotViewModel.Event) -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = {
            onValueChange.invoke(false)
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        AiHandlerScreen(
            modifier = Modifier.size(250.dp, 200.dp),
            listAiModelHandler = listAiModelHandler,
            event = event,
            aiModelList = aiModelList,
        )
    }

    IconButton(
        onClick = { onValueChange(!isExpanded) }
    ) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = "Menu",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun FabScrollLastItem(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onScroll: () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
    ) {
        SmallFloatingActionButton(
            shape = CircleShape,
            modifier = Modifier,
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0f),
            onClick = onScroll,
            interactionSource = object : MutableInteractionSource {
                override val interactions: Flow<Interaction> = emptyFlow()
                override suspend fun emit(interaction: Interaction) {}
                override fun tryEmit(interaction: Interaction) = true
            },
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            content = {
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    "Scroll to bottom",
                )
            }
        )
    }
}

@Composable
@Suppress("LongParameterList", "FunctionNaming")
fun LazyChatScreen(
    modifier: Modifier = Modifier,
    messagesList: PersistentList<MessageGroup>,
    event: (ChatBotViewModel.Event) -> Unit,
    error: AppError?,
    state: LazyListState,
    bottomPaddings: Dp,
) {
    val brush = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        )
    )

    LaunchedEffect(messagesList.size) {
        if (messagesList.isNotEmpty()) {
            state.animateScrollToItem(messagesList.size + 2)
        }
    }
    LazyColumn(
        modifier = modifier.background(brush),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        state = state,
        contentPadding = PaddingValues(5.dp, 10.dp, 5.dp, 0.dp)
    ) {
        items(
            items = messagesList,
            key = { it.id }
        ) { messageGroup ->
            when (messageGroup.type) {
                MessageType.TEXT -> {
                    MessageBox(
                        isUser = messageGroup.role == Role.USER,
                        event = event,
                        messageGroup = messageGroup
                    )
                }

                MessageType.IMAGE -> {
                    // Обработка изображений
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
    messageGroup: MessageGroup,
    isUser: Boolean,
    event: (ChatBotViewModel.Event) -> Unit,
    pagerState: PagerState = rememberPagerState(pageCount = { messageGroup.content.size }),
) {
    val chosenPageIndex = remember(messageGroup.content) {
        messageGroup.content.indexOfFirst { it.isChosen }
    }
    LaunchedEffect(chosenPageIndex) {
        if (chosenPageIndex != -1 && pagerState.currentPage != chosenPageIndex) {
            pagerState.requestScrollToPage(chosenPageIndex)
        }
    }
    LaunchedEffect(pagerState.settledPage) {
        if (pagerState.pageCount > 0) {
            event(
                ChatBotViewModel.Event.ChooseSubMessage(
                    messageGroup.content[pagerState.currentPage] // TODO check
                )
            )
        }
    }

    HorizontalPager(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 250,
                    easing = LinearEasing
                )
            ),
        state = pagerState,
        flingBehavior = PagerDefaults.flingBehavior(
            state = pagerState, pagerSnapDistance =
                PagerSnapDistance.atMost(3)
        ),
        verticalAlignment = Alignment.CenterVertically
    ) { page ->
        val currentMessage = remember(messageGroup.content[page]) {
            messageGroup.content[page]
        }
        when (currentMessage.otherContent == null) {
            true -> {
                TextMessage(
                    message = currentMessage,
                    isUser = isUser,
                )
            }

            false -> {
                ImageMessage(message = currentMessage)
            }
        }
    }

    if (messageGroup.content.size > 1) {
        PagerIndicator(
            totalPages = messageGroup.content.size,
            currentPage = pagerState.currentPage,
            loadingStates = messageGroup.content.map { it.status }
        )
    }
}

@Composable
private fun TextMessage(
    message: MessageAI,
    isUser: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(1f)
            .padding(15.dp, 5.dp, 15.dp, 5.dp)
            .then(
                if (message.status == MessageStatus.LOADING) {
                    Modifier
                        .height(50.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .shimmerLoading()

                } else Modifier
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            key(message.message) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    MarkdownText(
                        modifier = Modifier
                            .wrapContentHeight()
                            .background(
                                brush = if (isUser) Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 1f),
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 1f),
                                    )
                                ) else SolidColor(Color.Transparent),
                                shape = RoundedCornerShape(
                                    if (isUser) 10.dp else (0.5).dp,
                                    10.dp,
                                    if (isUser) (0.5).dp else 10.dp,
                                    10.dp
                                )
                            )
                            .padding(5.dp),
                        markdown = message.message.trimEnd(),
                        style = TextStyle(textAlign = TextAlign.Start),
                    )
                }
            }
            if (message.model != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = message.model.modelName,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                    textAlign = TextAlign.End,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun ImageMessage(
    message: MessageAI,
    modifier: Modifier = Modifier,
) {
    val imageBytes = remember(message) {
        Base64.decode(
            (message.otherContent as MessageAiContent.Image).image,
            Base64.DEFAULT
        )
    }
    val image = remember(imageBytes) {
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
    AsyncImage(
        model = image,
        contentDescription = "Image u push to AI",
        modifier = modifier
    )
}

@Composable
private fun PagerIndicator(
    totalPages: Int,
    currentPage: Int,
    loadingStates: List<MessageStatus>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        items(totalPages) { index ->
            if (loadingStates[index] == MessageStatus.LOADING) {
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
                        .background(
                            if (currentPage == index) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.primaryContainer
                        )
                        .size(6.dp)
                )
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
                                    imageBitmap.value?.compress(
                                        Bitmap.CompressFormat.JPEG,
                                        80,
                                        it
                                    )
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
                stringResource(R.string.enter_a_message),
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
                listHandler = persistentListOf(),
                modelList = persistentListOf(),
                chat = MessageChat(
                    messageGroups = MockChat()
                )
            )
        )
    )
}