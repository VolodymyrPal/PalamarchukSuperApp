package com.hfad.palamarchuksuperapp.ui.compose

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.room.Room
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.chargemap.compose.numberpicker.NumberPicker
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppTextField
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.rememberTextFieldConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.elements.AppDialog
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.daggerViewModel
import com.hfad.palamarchuksuperapp.core.ui.theme.md_theme_my_royal
import com.hfad.palamarchuksuperapp.data.database.DATABASE_MAIN_ENTITY_PRODUCT
import com.hfad.palamarchuksuperapp.data.database.StoreDatabase
import com.hfad.palamarchuksuperapp.data.dtos.ProductDTO
import com.hfad.palamarchuksuperapp.data.dtos.ProductRating
import com.hfad.palamarchuksuperapp.data.repository.FakeStoreApiRepository
import com.hfad.palamarchuksuperapp.data.repository.StoreRepositoryImpl
import com.hfad.palamarchuksuperapp.domain.models.Product
import com.hfad.palamarchuksuperapp.ui.compose.utils.BottomNavBar
import com.hfad.palamarchuksuperapp.ui.compose.utils.DrawerWrapper
import com.hfad.palamarchuksuperapp.ui.compose.utils.MyNavigationDrawer
import com.hfad.palamarchuksuperapp.ui.viewModels.StoreViewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("LongParameterList", "FunctionNaming", "LongMethod", "MagicNumber")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun StoreScreen(
    modifier: Modifier = Modifier,
    viewModel: StoreViewModel = daggerViewModel(
        factory = LocalContext.current.appComponent.viewModelFactory(),
    ),
) {
    val navController: NavHostController =
        if (!LocalInspectionMode.current) LocalNavController.current else
            NavHostController(LocalContext.current) // If preview - create Mock object for NavHostController

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val myState by viewModel.uiState.collectAsStateWithLifecycle()

    val mainDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val subDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val myBasket by viewModel.baskList.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    MyNavigationDrawer(
        mainDrawerContent = {

        },
        gesturesEnabled = true,
        drawerWidth = 360.dp,
        mainDrawerState = mainDrawerState,
        drawerSideAlignment = DrawerWrapper.Both,
        subDrawerContent = {
            if (subDrawerState.isOpen) {
                SubDrawerContent(
                    closeDrawerEvent = {
                        coroutineScope.launch { subDrawerState.close() }
                    },
                    onEvent = viewModel::event,
                    items = myBasket
                )
            }
        },
        subDrawerState = subDrawerState
    ) {
        LaunchedEffect(Unit) {

//            val a = viewModel.gptRepository.getGPTResponse(
//                contentType = "application/json",
//                apiKey = "Bearer $OPEN_AI_KEY_USER",
//                body = GPTRequest(
//                    messages = listOf(
//                        GPTRequestMessage(
//                            role = "user",
//                            content = "I am looking for something in the store"
//                        )
//                    )
//                )
//            )
//            a.enqueue(object : Callback<String> {
//
//                override fun onResponse(call: Call<String>, response: Response<String>) {
//                    if (response.isSuccessful) {
//                        Log.d("answer:", "${response.body()}")
//                    } else {
//                        Log.d("OPEN FAIL", "fuck ${response.code()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Log.d("OPEN FAIL", "onFailure: ${t.message}, $call")
//                }
//            })
        }
        val localTransitionScope = LocalSharedTransitionScope.current
            ?: error(IllegalStateException("No SharedElementScope found"))
        val animatedContentScope = LocalNavAnimatedVisibilityScope.current
            ?: error(IllegalStateException("No AnimatedVisibility found"))

        with(localTransitionScope) {
            Scaffold(
                modifier = modifier
                    .fillMaxSize()
                    .sharedBounds(
                        rememberSharedContentState("store"),
                        animatedContentScope
                    )
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    MediumTopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onPrimary,             // Hiding top bar
                            titleContentColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.onPrimaryContainer,
                            scrolledContainerColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onPrimary      // Expanded top bar
                        ),
                        title = {
                            Text(
                                stringResource(R.string.store_title),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Localized description"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { viewModel.event(StoreViewModel.Event.OnHardRefresh) }) {
                                if (myState.loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.Yellow
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
                bottomBar = { BottomNavBar() },
                floatingActionButton = {
                    FloatingActionButton(
                        shape = RoundedCornerShape(33),
                        modifier = Modifier,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        onClick = {
                            coroutineScope.launch { subDrawerState.open() }
                        },
                        content = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_shopping_basket_24),
                                    "Floating action button."
                                )
                                if (myBasket.isNotEmpty()) {
                                    Text(text = myBasket.sumOf { it.quantity }.toString())
                                }
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Surface(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(
                            paddingValues
                        )
                ) {
                    StoreScreenContent(
                        items = myState.items,
                        onEvent = viewModel::event,
                        error = myState.error,
                        snackBarHost = snackbarHostState,
                        bottomSpace = paddingValues.calculateBottomPadding()
                    )
                }
            }
        }
    }
}

@Suppress("LongParameterList", "FunctionNaming", "LongMethod", "MagicNumber")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StoreScreenContent(
    modifier: Modifier = Modifier,
    items: List<Product>? = emptyList(),
    error: AppError?,
    onEvent: (StoreViewModel.Event) -> Unit,
    snackBarHost: SnackbarHostState,
    bottomSpace: Dp = 0.dp,
) {
    val itemSpan = LocalConfiguration.current.screenWidthDp / WIDTH_ITEM
    val pullRefreshState =
        rememberPullRefreshState(
            refreshing = false,
            onRefresh = { onEvent(StoreViewModel.Event.OnSoftRefresh) }
        )
    val scope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .background(color = md_theme_my_royal)
            .padding(PaddingValues(start = 8.dp, end = 8.dp))
    ) {
        LazyVerticalGrid(
            flingBehavior = ScrollableDefaults.flingBehavior(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            columns = GridCells.Adaptive(minSize = WIDTH_ITEM.dp),
            horizontalArrangement = Arrangement.Absolute.Center
        ) {

            if (error != null) {

                val textMessage = when (error) {
                    is AppError.NetworkException -> {
                        "Network error"
                    }

                    else -> {
                        "Else error"
                    }
                }

                scope.launch {
                    val result: SnackbarResult = snackBarHost.showSnackbar(
                        message = textMessage,
                        actionLabel = "Refresh",
                        duration = SnackbarDuration.Indefinite
                    )
                    when (result) {
                        SnackbarResult.ActionPerformed -> onEvent(StoreViewModel.Event.OnSoftRefresh)
                        SnackbarResult.Dismissed -> {}
                    }
                }
            } else {
                snackBarHost.currentSnackbarData?.dismiss()
            }

            if (!items.isNullOrEmpty()) {
                item(span = { GridItemSpan(itemSpan) }) {
                    StoreLazyCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        onEvent = onEvent,
                        productDTOList = items.filter { items[0].productDTO.category == it.productDTO.category },
                    )
                }


                item(span = { GridItemSpan(itemSpan) }) {

                    val productListInter = items.filter {
                        items[0].productDTO.category != it.productDTO.category
                    }

                    val finalProductList = productListInter.filter {
                        productListInter[0].productDTO.category == it.productDTO.category
                    }

                    StoreLazyCard(
                        modifier = Modifier,
                        onEvent = onEvent,
                        productDTOList = finalProductList
                    )
                }
                items(
                    items = items,
                    key = { item: Product -> item.id },
                ) { item ->
                    GridItem(
                        modifier = Modifier.animateItem(),
                        item = item,
                        onEvent = onEvent
                    )
                    AnimatedVisibility(
                        modifier = Modifier
                            .animateItem(),
                        // .padding(0.dp, 0.dp, 10.dp, 10.dp),
                        visible = true,
                        exit = fadeOut(
                            animationSpec = TweenSpec(100, 100, LinearEasing)
                        ),
                        enter = fadeIn(
                            animationSpec = TweenSpec(100, 100, LinearEasing)
                        )
                    ) {
                        ListItemProduct(
                            item = item,
                            onEvent = remember(item) { { event -> onEvent(event) } },
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.size(bottomSpace))
            }
        }
        PullRefreshIndicator(
            refreshing = false,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            scale = true,
        )
    }
}

@Suppress("LongParameterList", "FunctionNaming", "LongMethod", "MagicNumber")
@Composable
fun GridItem(
    modifier: Modifier,
    item: Product,
    onEvent: (StoreViewModel.Event) -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        // .padding(0.dp, 0.dp, 10.dp, 10.dp),
        visible = true,
        exit = fadeOut(
            animationSpec = TweenSpec(100, 100, LinearEasing)
        ),
        enter = fadeIn(
            animationSpec = TweenSpec(100, 100, LinearEasing)
        )
    ) {
        ListItemProduct(
            item = item,
            onEvent = onEvent,
        )
    }
}

@Suppress("LongParameterList", "FunctionNaming", "LongMethod", "MagicNumber")
@Composable
fun StoreLazyCard(
    modifier: Modifier = Modifier,
    onEvent: (StoreViewModel.Event) -> Unit,
    productDTOList: List<Product> = listOf(
        Product(
            productDTO = ProductDTO(
                id = 2890,
                title = "vidisse",
                price = 0.1,
                description = "ornare",
                category = "delicata",
                image = "sollicitudin",
                rating = ProductRating(
                    rate = 2.3,
                    count = 4066
                )
            )
        )
    ),
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
    ) {
        Card(
            modifier = Modifier
            //  .padding(4.dp)
            ,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inversePrimary),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                text = productDTOList[0].productDTO.category.uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
            LazyRow(
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp
                ),
            ) {
                items(
                    items = productDTOList,
                    key = { item: Product -> item.id },
                ) { item ->
                    AnimatedVisibility(
                        modifier = Modifier
                            .animateItem(),
                        visible = true,
                        exit = fadeOut(
                            animationSpec = TweenSpec(100, 100, LinearEasing)
                        ),
                        enter = fadeIn(
                            animationSpec = TweenSpec(100, 100, LinearEasing)
                        )
                    ) {
                        ListItemProduct(
                            item = item,
                            onEvent = onEvent,
                        )
                    }
                }
            }
        }
    }
}

@Suppress(
    "LongParameterList",
    "FunctionNaming",
    "MaxLineLength",
    "LongMethod",
    "MagicNumber",
    "CyclomaticComplexMethod",
    "SwallowedException"
)
@Composable
fun ListItemProduct(
    modifier: Modifier = Modifier,
    item: Product,
    onEvent: (StoreViewModel.Event) -> Unit = {},
) {
    Box(modifier = modifier.size(WIDTH_ITEM.dp, HEIGHT_ITEM.dp)) {

        var isVisible by remember { mutableStateOf(false) }
        var job by remember { mutableStateOf<Job?>(null) }
        var isPressed by remember { mutableStateOf(false) }
        var quantityToBuy by remember { mutableIntStateOf(item.quantity) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(item.quantity) { quantityToBuy = item.quantity }

        LaunchedEffect(item.quantity) {
            job?.cancelAndJoin()
            job = launch {
                delay(2000)
                isVisible = false
            }
        }

        Row(
            modifier = Modifier.matchParentSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .alpha(0f)
                    .pointerInput(item) {
                        detectTapGestures(
                            onTap = {
                                if (quantityToBuy > 0) quantityToBuy--
                                if (!isPressed) onEvent(
                                    StoreViewModel.Event.SetItemToBasket(
                                        item,
                                        quantityToBuy
                                    )
                                )
                            },
                            onPress = {
                                isPressed = true
                                isVisible = true
                                job = scope.launch {
                                    delay(500)
                                    while (isPressed) {
                                        if (quantityToBuy > 0) quantityToBuy--
                                        delay(20)
                                    }
                                }
                                try {

                                    awaitRelease()
                                    job?.cancel()
                                    onEvent(
                                        StoreViewModel.Event.SetItemToBasket(
                                            item,
                                            quantityToBuy
                                        )
                                    )
                                    isPressed = false
                                    isVisible = true
                                } catch (e: CancellationException) {
                                    isPressed = false
                                    isVisible = false
                                } catch (e: Exception) {
                                    job?.cancel()
                                    isPressed = false
                                    isVisible = false
                                }
                            }
                        )
                    })
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()  // Use fillMaxHeight instead of fillMaxSize to avoid expanding beyond parent
                    .alpha(0f)
                    .pointerInput(item) {
                        detectTapGestures(

                            onTap = {
                                if (quantityToBuy >= 0) quantityToBuy++
                                if (!isPressed) onEvent(
                                    StoreViewModel.Event.SetItemToBasket(
                                        item,
                                        item.quantity
                                    )
                                )
                            },
                            onPress = {
                                isPressed = true
                                isVisible = true
                                job = scope.launch {
                                    delay(500)
                                    isVisible = true
                                    while (isPressed) {
                                        if (quantityToBuy >= 0) quantityToBuy++
                                        delay(20)
                                    }
                                }
                                try {
                                    awaitRelease()
                                    job?.cancel()
                                    onEvent(
                                        StoreViewModel.Event.SetItemToBasket(
                                            item,
                                            quantityToBuy
                                        )
                                    )
                                    isPressed = false
                                    isVisible = true
                                } catch (e: CancellationException) {
                                    job?.cancel()
                                    isPressed = false
                                    isVisible = false
                                } catch (e: Exception) {
                                    job?.cancel()
                                    Log.d("Store screen exception: ", "event: ${e.message}")
                                    isPressed = false
                                    isVisible = true
                                }
                            }
                        )
                    }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize(), // Wrap content size to avoid expanding beyond parent size
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.productDTO.image)
                    .placeholder(R.drawable.bicon_camera_selector)
                    .error(R.drawable.bicon_home_black_filled)
                    .size(66)
                    .crossfade(true)
                    .build(),
                loading = { CircularProgressIndicator() },
                contentDescription = "Product Image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(120.dp, 120.dp)
                    .clip(RoundedCornerShape(percent = 12))
            )
            Text(
                text = item.productDTO.title.uppercase(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                fontSize = TextUnit(12f, TextUnitType.Sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                StarRatingBar(
                    maxStars = 5,
                    rating = item.productDTO.rating.rate.toFloat(),
                    onRatingChanged = { },
                    modifier = Modifier
                )

                Text(
                    text = "500...",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray
                    ),
                    modifier = Modifier
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.productDTO.price}$",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.DarkGray,
                        fontStyle = FontStyle.Italic,
                        fontSize = TextUnit(8f, TextUnitType.Sp)
                    ),
                    modifier = Modifier,
                    textDecoration = TextDecoration.LineThrough
                )

                Text(
                    text = "${item.productDTO.price / 2}$",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    modifier = Modifier
                )
            }
            Text(
                text = "SAVE 50% TODAY",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.error),
                modifier = Modifier,
                fontSize = TextUnit(8f, TextUnitType.Sp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "$quantityToBuy",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = TextUnit(32f, TextUnitType.Sp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(if (quantityToBuy > 0 || (isVisible || isPressed)) 0.95f else 0f)
                    .background(Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(50))
            )
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .alpha(if (isVisible || isPressed) 0.95f else 0f),
                imageVector = ImageVector.vectorResource(id = R.drawable.single_line_outlined),
                contentDescription = "Decrease Quantity"
            )
            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterEnd)
                    .alpha(if (isVisible || isPressed) 0.95f else 0f),
                imageVector = Icons.Default.Add,
                contentDescription = "Increase Quantity"
            )
        }
    }
}

@Suppress(
    "LongParameterList",
    "FunctionNaming",
    "MaxLineLength",
    "LongMethod",
    "MagicNumber",
    "CyclomaticComplexMethod",
    "SwallowedException"
)
@Composable
fun StarRatingBar(
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    rating: Float,
    onRatingChanged: (Float) -> Unit,
) {
    val density = LocalDensity.current.density
    val starSize = (8f * density).dp
    val starSpacing = (0.01f * density).dp

    Row(
        modifier = modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Default.Star
            val iconTintColor = if (isSelected) Color(0xFFFFC700) else Color(0x20FFFFFF)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    .selectable(
                        selected = isSelected,
                        onClick = {

                        }
                    )
                    .width(starSize)
                    .height(starSize)
            )

            if (i < maxStars) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}

@Composable
@Preview
fun StoreLazyListForPreview(
    modifier: Modifier = Modifier,
    viewModel: StoreViewModel? = null,
) {
    StoreLazyCard(
        onEvent = { }
    )
}

@Composable
@Preview
fun StoreScreenPreview(
    viewModel : StoreViewModel = StoreViewModel(
        repository = StoreRepositoryImpl(
            storeApi = FakeStoreApiRepository(HttpClient()),
            storeDao = Room.databaseBuilder(
                context = LocalContext.current.applicationContext,
                klass = StoreDatabase::class.java,
                name = DATABASE_MAIN_ENTITY_PRODUCT
            ).fallbackToDestructiveMigration()
                .build().storeDao()
        ),
        ioDispatcher = Dispatchers.IO,
        mainDispatcher = Dispatchers.Main
    )
) {
    StoreScreen(
        viewModel = viewModel
        //daggerViewModel<StoreViewModel>(LocalContext.current.appComponent.viewModelFactory())
    )
}

@Composable
@Preview
fun SubDrawerContent(
    modifier: Modifier = Modifier,
    closeDrawerEvent: () -> Unit = {},
    onEvent: (StoreViewModel.Event) -> Unit = {},
    items: List<Product> = emptyList(),
) {
    val openAlertDialog = remember { mutableStateOf(false) }
    Column(
        modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = closeDrawerEvent
            ) {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close drawer",
                )
            }
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.basket_title),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            items(
                key = { item: Product -> item.productDTO.id },
                items = items,
            ) {
                Box(
                    Modifier
                        .padding(4.dp)
                        .fillMaxSize()
                        .border(1.dp, Color.Black)
                ) {
                    val summ = "%.2f".format(it.productDTO.price / 2 * it.quantity)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            modifier = Modifier.weight(0.2f),
                            model = it.productDTO.image,
                            contentDescription = "Product Image"
                        )
                        Text(modifier = Modifier.weight(0.5f), text = it.productDTO.description)
                        NumberPicker(
                            modifier = Modifier
                                .weight(0.2f)
                                .wrapContentWidth(),
                            value = it.quantity,
                            onValueChange = { value ->
                                onEvent.invoke(
                                    StoreViewModel.Event.SetItemToBasket(
                                        it,
                                        value
                                    )
                                )
                            },
                            range = 1..99
                        )
                        Text(
                            modifier = Modifier.wrapContentWidth(),
                            text = stringResource(R.string.product_sum, summ)
                        )
                    }
                }
            }
        }
        val summ =
            "%.2f".format(items.sumOf { item -> item.productDTO.price * item.quantity } * 0.5)
        Text(
            text = stringResource(R.string.to_pay, summ),
            fontSize = 24.sp,
            fontStyle = FontStyle.Italic
        )
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            openAlertDialog.value = !openAlertDialog.value
        }) {
            Text(
                text = stringResource(R.string.order_proceed_button),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    when {
        openAlertDialog.value -> {
            val context = LocalContext.current
            fun showToast() {
                Toast.makeText(context, "Order confirmed", Toast.LENGTH_SHORT).show()
            }

            AppDialog(
                onDismissRequest = { openAlertDialog.value = false },
            ) {
                Header {
                    AppText(
                        value = R.string.order_confirmation_title,
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.titleMedium
                        )
                    )
                }
                Content {
                    Column {
                        var phone by remember { mutableStateOf("") }

                        AppText(
                            R.string.order_confirmation_message
                        )
                        AppTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            editTextConfig = rememberTextFieldConfig(
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        for (item in items) {
                                            onEvent.invoke(
                                                StoreViewModel.Event.SetItemToBasket(
                                                    item,
                                                    0
                                                )
                                            )
                                        }
                                        closeDrawerEvent()
                                        showToast()
                                        openAlertDialog.value = false
                                    }
                                )
                            )
                        )
                    }
                }
                Actions {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        TextButton(
                            modifier = Modifier,
                            onClick = {
                                for (item in items) {
                                    onEvent.invoke(
                                        StoreViewModel.Event.SetItemToBasket(
                                            item,
                                            0
                                        )
                                    )
                                }
                                closeDrawerEvent()
                                showToast()
                                openAlertDialog.value = false
                            }
                        ) {
                            AppText(stringResource(R.string.order_proceed_button))
                        }
                    }
                }
            }
        }
    }
}


const val HEIGHT_ITEM = 200
const val WIDTH_ITEM = 150
