package com.hfad.palamarchuksuperapp.ui.compose

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.compose.md_theme_my_royal
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.ui.compose.utils.BottomNavBar
import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.data.entities.ProductRating
import com.hfad.palamarchuksuperapp.data.repository.ProductRepository
import com.hfad.palamarchuksuperapp.data.repository.StoreRepositoryImplForPreview
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.ui.viewModels.State
import com.hfad.palamarchuksuperapp.ui.viewModels.StoreViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    modifier: Modifier = Modifier,
    navController: Navigation?,
    viewModel: StoreViewModel = viewModel(factory = LocalContext.current.appComponent.viewModelFactory()),
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
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
                        "MY SUPPA SHOP FOR CHICKS",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )

        },
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        floatingActionButton = {
            val myBasket by viewModel.basketList.collectAsState()

            FloatingActionButton(
                shape = RoundedCornerShape(33),
                modifier = Modifier,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
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
                .padding(
                    bottom = paddingValues.calculateBottomPadding(),
                    top = paddingValues.calculateTopPadding()
                )
        ) {
            val state by viewModel.uiState.collectAsState()

            when (state) {
                State.Processing -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }

                State.Empty -> {
                    Text(text = "Empty or Error. Please refresh by swipe!")
                }


                is State.Error -> {
                    Text(
                        text = "Error: ${(state as State.Error).exception}",
                        color = Color.Red
                    )
                }


                is State.Success -> {
                    StoreScreenContent(
                        modifier = Modifier,
                        productList = (state as State.Success).data,
                        onEvent = viewModel::event
                    )
                }
            }
        }
    }
}

@Composable
fun StoreScreenContent(
    modifier: Modifier = Modifier,
    productList: List<ProductDomainRW>,
    onEvent: (StoreViewModel.Event) -> Unit,
) {
    val itemSpan = LocalConfiguration.current.screenWidthDp / WIDTH_ITEM
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .background(color = md_theme_my_royal)
            .padding(start = 8.dp, end = 8.dp, top = 8.dp),
        flingBehavior = ScrollableDefaults.flingBehavior(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        columns = GridCells.Adaptive(minSize = WIDTH_ITEM.dp),
        horizontalArrangement = Arrangement.Absolute.Center
    )
    {
        item(span = { GridItemSpan(itemSpan) }) {
            StoreLazyCard(
                modifier = Modifier.fillMaxWidth(),
                onEvent = onEvent,
                productList = productList.filter { productList[0].product.category == it.product.category },
            )
        }

        item(span = { GridItemSpan(itemSpan) }) {

            val productListInter = productList.filter {
                productList[0].product.category != it.product.category
            }

            val finalProductList = productListInter.filter {
                productListInter[0].product.category == it.product.category
            }

            StoreLazyCard(
                modifier = Modifier,
                onEvent = onEvent,
                productList = finalProductList
            )
        }
        items(
//                        items = viewModel.testData.map { it.toProductDomainRW() },
//                        key = { item: ProductDomainRW -> item.product.id } // TODO test rep
            items = productList,
            key = { item: ProductDomainRW -> item.product.id },
        ) { item ->
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
}

@Composable
fun StoreLazyCard(
    modifier: Modifier = Modifier,
    onEvent: (StoreViewModel.Event) -> Unit,
    productList: List<ProductDomainRW>,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
    ) {
        Card(
            modifier = Modifier
                //  .padding(4.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Blue),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                text = productList[0].product.category.uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
            LazyRow(
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp
                ), // Add padding before start and after end
            ) {
                items(
                    items = productList,
                    key = { item: ProductDomainRW -> item.product.id },
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
                            item = item, // TODO test rep
                            onEvent = remember(item) { { event -> onEvent(event) } },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ListItemProduct(
    modifier: Modifier = Modifier,
    item: ProductDomainRW,
    onEvent: (StoreViewModel.Event) -> Unit = {},

    ) {
    Box(modifier = modifier.size(WIDTH_ITEM.dp, HEIGHT_ITEM.dp)) {

        var isVisible by remember { mutableStateOf(false) }
        var job by remember { mutableStateOf<Job?>(null) }
        var isPressed by remember { mutableStateOf(false) }
        var quantityToBuy by remember { mutableIntStateOf(item.quantity) }
        val scope = rememberCoroutineScope()

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
            Box(modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()  // Use fillMaxHeight instead of fillMaxSize to avoid expanding beyond parent
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
                                    Log.d("Doing smthng", "event:")
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
                                Log.d("E: ", "event: ${e.message}")
                                isPressed = false
                                isVisible = false
                            } catch (e: Exception) {
                                job?.cancel()
                                Log.d("Store screen exception: ", "event: ${e.message}")
                                isPressed = false
                                isVisible = false
                            }
                        }
                    )
                })
            Box(modifier = Modifier
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
                    .data(item.product.image)
                    .placeholder(R.drawable.bicon_camera_selector)
                    .error(R.drawable.bicon_home_black_filled)
                    .size(100)
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
                text = item.product.title.uppercase(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                fontSize = TextUnit(12f, TextUnitType.Sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                StarRatingBar(
                    maxStars = 5,
                    rating = item.product.rating.rate.toFloat(),
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
                    text = "${item.product.price}$",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.DarkGray,
                        fontStyle = FontStyle.Italic,
                        fontSize = TextUnit(8f, TextUnitType.Sp)
                    ),
                    modifier = Modifier,
                    textDecoration = TextDecoration.LineThrough
                )

                Text(
                    text = "${item.product.price / 2}$",
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

@Preview
@Composable
fun ListItemProductPreview() {
    ListItemProduct(
        item = ProductDomainRW(
            product = Product(
                id = 8101,
                title = "intellegat",
                price = 1112.0,
                description = "vituperatoribus",
                category = "eripuit",
                image = "himenaeos",
                rating = ProductRating(
                    rate = 102.103,
                    count = 7238
                )
            )
        )
    )
}

@Composable
@Preview
fun StoreLazyListForPreview(
    modifier: Modifier = Modifier,
    viewModel: StoreViewModel = StoreViewModel(
        repository = StoreRepositoryImplForPreview(),
        apiRepository = ProductRepository()
    ),
) {
    StoreLazyCard(
        productList = viewModel.testData,
        onEvent = {})
}

@Composable
@Preview
fun StoreScreenPreview() {
    StoreScreen(
        navController = null,
        viewModel = StoreViewModel(
            repository = StoreRepositoryImplForPreview(),
            apiRepository = ProductRepository()
        )
    )
}

const val HEIGHT_ITEM = 200
const val WIDTH_ITEM = 150


//@Composable
//fun ItemListProduct(
//    modifier: Modifier = Modifier,
//    item: ProductDomainRW,
//    onEvent: (StoreViewModel.Event) -> Unit = {},
//    viewModel: StoreViewModel?,
//) {
//
//    ConstraintLayout(
//        modifier = Modifier
//            .size(WIDTH_ITEM.dp, HEIGHT_ITEM.dp)
//    ) {
//        val (image, quantityMinusButton, quantityMinusField, quantityPlusField, quantity, quantityPlusButton, ratingBar, name, sold, price, discountedPrice, saveText) = createRefs()
//        var isVisible by remember { mutableStateOf(false) }
//
//        var job by remember { mutableStateOf<Job?>(null) }
//
//        var isPressed by remember { mutableStateOf(false) }
//        var quantityToBuy by remember { mutableIntStateOf(item.quantity) }
//        val scope = rememberCoroutineScope()
//
//        LaunchedEffect(item.quantity) {
//            job?.cancelAndJoin()
//            job = launch {
//                delay(2000)
//                isVisible = false
//            }
//        }
//
//        AsyncImage(
//            model = ImageRequest.Builder(LocalContext.current)
//                .data(item.product.category.image)
//                .crossfade(true)
//                .error(R.drawable.custom_popup_background)
//                .placeholder(R.drawable.lion_jpg_21)
//                .build(),
//            contentDescription = "Product Image",
//            contentScale = ContentScale.FillBounds,
//            modifier = Modifier
//                .size(120.dp, 120.dp)
//                .clip(RoundedCornerShape(percent = 12))
//                .constrainAs(image) {
//                    top.linkTo(parent.top)
//                    end.linkTo(parent.end)
//                    start.linkTo(parent.start)
//                }
//        )
//
//        Box(
//            modifier = Modifier
//                .size(50.dp, HEIGHT_ITEM.dp)
//                .alpha(0f)
//                .constrainAs(quantityMinusField) {
//                    start.linkTo(parent.start)
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                }
//                .pointerInput(item) {
//                    detectTapGestures(
//                        onTap = {
//                            if (quantityToBuy > 0) quantityToBuy--
//                            if (!isPressed) viewModel?.event(
//                                StoreViewModel.Event.SetItemToBasket(
//                                    item,
//                                    quantityToBuy
//                                )
//                            )
//                        },
//                        onPress = {
//                            isPressed = true
//                            isVisible = true
//                            job = scope.launch {
//                                delay(500)
//                                while (isPressed) {
//                                    Log.d("Doing smthng", "event:")
//                                    if (quantityToBuy > 0) quantityToBuy--
//                                    delay(20)
//                                }
//                            }
//                            try {
//                                awaitRelease()
//                                job?.cancel()
//                                Log.d("TAG", "event: awaitReleased")
//                                viewModel?.event(
//                                    StoreViewModel.Event.SetItemToBasket(
//                                        item,
//                                        quantityToBuy
//                                    )
//                                )
//                                isPressed = false
//                                isVisible = true
//                            } catch (e: CancellationException) {
//                                Log.d("E: ", "event: ${e.message}")
//                                isPressed = false
//                                isVisible = true
//                            }
//                        }
//                    )
//                }
//        )
//        Icon(
//            modifier = Modifier
//                .size(40.dp)
//                .alpha(if (isVisible || isPressed) 0.95f else 0f)
//                .constrainAs(quantityMinusButton) {
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                    start.linkTo(parent.start)
//                    end.linkTo(quantity.start)
//                },
//            imageVector = ImageVector.vectorResource(id = R.drawable.single_line_outlined),
//            contentDescription = "Decrease Quantity"
//
//        )
//
//
//        Text(
//            text = "$quantityToBuy",
//            style = MaterialTheme.typography.headlineMedium,
//            color = MaterialTheme.colorScheme.onSecondary,
//            fontSize = TextUnit(32f, TextUnitType.Sp),
//            modifier = Modifier
//                .alpha(if (item.quantity > 0 || (isVisible || isPressed)) 0.95f else 0f)
//                .constrainAs(quantity) {
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                }
//        )
//        Box(modifier = Modifier
//            .size(85.dp, HEIGHT_ITEM.dp)
//            .alpha(0f)
//            .constrainAs(quantityPlusField) {
//                top.linkTo(parent.top)
//                bottom.linkTo(parent.bottom)
//                end.linkTo(parent.end)
//            }
//            .pointerInput(item) {
//                detectTapGestures(
//                    onTap = {
//                        if (quantityToBuy >= 0) quantityToBuy++
//                        if (!isPressed) viewModel?.event(
//                            StoreViewModel.Event.SetItemToBasket(
//                                item,
//                                item.quantity
//                            )
//                        )
//                    },
//                    onPress = {
//                        isPressed = true
//                        isVisible = true
//                        job = scope.launch {
//                            delay(500)
//                            isVisible = true
//                            while (isPressed) {
//                                if (quantityToBuy >= 0) quantityToBuy++
//                                delay(20)
//                            }
//                        }
//                        try {
//                            awaitRelease()
//                            job?.cancel()
//                            viewModel?.event(
//                                StoreViewModel.Event.SetItemToBasket(
//                                    item,
//                                    quantityToBuy
//                                )
//                            )
//                            isPressed = false
//                            isVisible = true
//                        } catch (e: CancellationException) {
//                            job?.cancel()
//                            isPressed = false
//                            isVisible = true
//                        } catch (e: Exception) {
//                            job?.cancel()
//                            Log.d("Store screen exception: ", "event: ${e.message}")
//                            isPressed = false
//                            isVisible = true
//                        }
//                    }
//                )
//            }
//        )
//
//        Icon(
//            modifier = Modifier
//                .size(40.dp)
//                .alpha(if (isVisible || isPressed) 0.95f else 0f)
//                .constrainAs(quantityPlusButton) {
//                    start.linkTo(quantity.end)
//                    end.linkTo(parent.end)
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                },
//            imageVector = Icons.Default.Add,
//            contentDescription = "Increase Quantity"
//        )
//
//
//        Text(
//            text = item.product.title.uppercase(),
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
//            fontSize = TextUnit(12f, TextUnitType.Sp),
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//            modifier = Modifier.constrainAs(name) {
//                top.linkTo(image.bottom, 2.dp)
//                start.linkTo(parent.start)
//                end.linkTo(parent.end)
//                width = Dimension.fillToConstraints
//            }
//        )
//
//        var rating by remember { mutableStateOf(5f) }
//        StarRatingBar(
//            maxStars = 5,
//            rating = rating,
//            onRatingChanged = { ratingChange ->
//                rating = ratingChange
//            },
//            modifier = Modifier.constrainAs(ratingBar) {
//                start.linkTo(parent.start)
//                end.linkTo(sold.start)
//                top.linkTo(name.bottom)
//                bottom.linkTo(sold.bottom)
//            }
//        )
//
//        Text(
//            text = "500...",
//            style = MaterialTheme.typography.labelSmall.copy(
//                fontStyle = FontStyle.Italic,
//                color = Color.Gray
//            ),
//            modifier = Modifier.constrainAs(sold) {
//                top.linkTo(name.bottom)
//                end.linkTo(parent.end)
//                start.linkTo(ratingBar.end)
//            }
//        )
//
//        Text(
//            text = "${item.product.price}$",
//            style = MaterialTheme.typography.bodySmall.copy(
//                color = Color.DarkGray,
//                fontStyle = FontStyle.Italic,
//                fontSize = TextUnit(8f, TextUnitType.Sp)
//            ),
//            modifier = Modifier.constrainAs(price) {
//                top.linkTo(discountedPrice.top)
//                bottom.linkTo(discountedPrice.bottom)
//                end.linkTo(discountedPrice.start)
//            },
//            textDecoration = TextDecoration.LineThrough
//        )
//
//        Text(
//            text = "${item.product.price / 2}$",
//            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
//            fontSize = TextUnit(18f, TextUnitType.Sp),
//            modifier = Modifier
//                .constrainAs(discountedPrice) {
//                    top.linkTo(ratingBar.bottom)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                    bottom.linkTo(saveText.top)
//                }
//        )
//
//        Text(
//            text = "SAVE 50% TODAY",
//            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.error),
//            modifier = Modifier.constrainAs(saveText) {
//                bottom.linkTo(parent.bottom)
//                start.linkTo(parent.start)
//                end.linkTo(parent.end)
//            },
//            fontSize = TextUnit(8f, TextUnitType.Sp)
//        )
//    }
//}

//@Preview
//@Composable
//fun ItemListProductPreview() {
//    ItemListProduct(
//        modifier = Modifier.background(color = Color.White),
//        item = ProductDomainRW(
//            Product(
//                id = 6717, title = "eripuit", price = 5869, category = ProductCategory(
//                    id = 3226,
//                    name = "Errol Clemons",
//                    image = "legere"
//                ), images = emptyList() // ProductImages(urls = listOf())
//            )
//        ),
//        viewModel = StoreViewModel(
//            repository = StoreRepositoryImplForPreview(),
//            apiRepository = ProductRepository()
//        ),
//        onEvent = {}
//    )
//}