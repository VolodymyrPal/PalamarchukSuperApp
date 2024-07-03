package com.hfad.palamarchuksuperapp.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.compose.md_theme_my_royal
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.compose.utils.BottomNavBar
import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.data.entities.ProductCategory
import com.hfad.palamarchuksuperapp.data.entities.ProductImages
import com.hfad.palamarchuksuperapp.data.repository.StoreRepositoryImplForPreview
import com.hfad.palamarchuksuperapp.presentation.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.presentation.common.toProductDomainRW
import com.hfad.palamarchuksuperapp.presentation.viewModels.State
import com.hfad.palamarchuksuperapp.presentation.viewModels.StoreViewModel
import com.hfad.palamarchuksuperapp.presentation.viewModels.daggerViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StoreScreen(
    modifier: Modifier = Modifier,
    navController: Navigation?,
    viewModel: StoreViewModel = daggerViewModel<StoreViewModel>(
        factory = LocalContext.current.appComponent.viewModelFactory()
    ),
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        floatingActionButton = {

            FloatingActionButton(
                shape = RoundedCornerShape(33),
                modifier = Modifier,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = remember {
                    {

                    }
                },
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.add_fab_button),
                        "Floating action button."
                    )
                }
            )
        }
    ) { paddingValues ->
        Surface(
            color = md_theme_my_royal,
            modifier = modifier
                .padding(bottom = paddingValues.calculateBottomPadding())

        ) {
            val state by viewModel.uiState.collectAsState()
            viewModel.event(StoreViewModel.Event.FetchSkills)
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
                        viewModel = viewModel,
                    )
                }
            }
        }
    }
}

@Composable
fun StoreScreenContent(
    modifier: Modifier = Modifier,
    viewModel: StoreViewModel,
) {
    LazyColumn(
        modifier = modifier
            .background(color = md_theme_my_royal),
        flingBehavior = ScrollableDefaults.flingBehavior()
    )
    {
        item {
            StoreLazyCard(
                modifier = Modifier,
                viewModel = viewModel
            )
        }
        item {
            StoreLazyCard(
                modifier = Modifier,
                viewModel = viewModel
            )
        }
        item {
            StoreLazyCard(
                modifier = Modifier,
                horizontal = false,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun StoreLazyCard(
    modifier: Modifier = Modifier,
    horizontal: Boolean = true,
    viewModel: StoreViewModel = StoreViewModel(repository = StoreRepositoryImplForPreview()),
) {
    val productList by viewModel.uiState.collectAsState()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
    ) {
        Card(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                text = "Lions",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            if (!horizontal) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp),
                    columns = GridCells.Adaptive(minSize = 130.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    items(
                        items = viewModel.testData.map { it.toProductDomainRW() },
//                items = (productList as State.Success<List<ProductDomainRW>>).data,
//                key = { item: ProductDomainRW -> item.product.id},
                        key = { item: ProductDomainRW -> item.product.id } // TODO test rep
                    ) { item ->
                        AnimatedVisibility(
                            modifier = Modifier
                                .animateItem()
                                .padding(0.dp, 0.dp, 10.dp, 10.dp),
                            visible = true,
                            exit = fadeOut(
                                animationSpec = TweenSpec(100, 100, LinearEasing)
                            ),
                            enter = fadeIn(
                                animationSpec = TweenSpec(100, 100, LinearEasing)
                            )
                        ) {
                            ItemListProduct(
//                        item = item,
                                item = item, // TODO test rep
                                onEvent = remember { { event -> viewModel.event(event) } },
                                viewModel = viewModel
                            )
                        }
                    }
                }
            } else {
                LazyRow {
                    items(
                        items = viewModel.testData,
//                items = (productList as State.Success<List<ProductDomainRW>>).data,
//                key = { item: ProductDomainRW -> item.product.id},
                        key = { item: Product -> item.id } // TODO test rep
                    ) { item ->
                        AnimatedVisibility(
                            modifier = Modifier
                                .animateItem()
                                .padding(0.dp, 0.dp, 10.dp, 10.dp),
                            visible = true,
                            exit = fadeOut(
                                animationSpec = TweenSpec(100, 100, LinearEasing)
                            ),
                            enter = fadeIn(
                                animationSpec = TweenSpec(100, 100, LinearEasing)
                            )
                        ) {
                            ItemListProduct(
//                        item = item,
                                item = item.toProductDomainRW(), // TODO test rep
                                onEvent = remember { { event -> viewModel.event(event) } },
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemListProduct(
    modifier: Modifier = Modifier,
    item: ProductDomainRW,
    onEvent: (StoreViewModel.Event) -> Unit = {},
    viewModel: StoreViewModel = StoreViewModel(repository = StoreRepositoryImplForPreview()),
) {
//    Card(
//        modifier =
//        modifier
//            .size(150.dp, 200.dp)
//            .then(remember(item) {
//                Modifier.clickable {
//
//                }
//            }),
//
//        colors = CardDefaults.cardColors(
//            containerColor = Color.Transparent
//        ),
//
//        ) {
    ConstraintLayout(modifier = Modifier
        .padding(4.dp)
    ) {
        val (image, quantityMinus, quantity, quantityPlus, ratingBar, name, sold, price, discountedPrice, saveText) = createRefs()
        var quantityToBuy by remember { mutableIntStateOf(item.quantity) }
        var isVisible by remember { mutableStateOf(false) }

        var job by remember { mutableStateOf<Job?>(null) }

        LaunchedEffect(quantityToBuy) {
            job?.cancelAndJoin()
            job = launch {
                delay(2000)
                isVisible = false
            }
        }



        Image(
            painter = painterResource(id = R.drawable.lion_jpg_21),
            contentDescription = "Product Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(120.dp, 120.dp)
                .clip(RoundedCornerShape(percent = 12))
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
                .then(Modifier.clickable {
                    isVisible = true
                    quantityToBuy++
                })
        )

        IconButton(
            onClick = {
                isVisible = true
                if (quantityToBuy > 0) quantityToBuy--
            },
            modifier = Modifier
                .size(35.dp)
                .alpha(if (isVisible) 0.65f else 0f)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
                .constrainAs(quantityMinus) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(quantity.start)
                }

        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Decrease Quantity"
            )
        }

        Text(
            text = "$quantityToBuy",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
                .alpha(if (quantityToBuy > 0 || isVisible) 1f else 0f)
                .constrainAs(quantity) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        IconButton(
            onClick = {
                isVisible = true
                quantityToBuy++
            },
            modifier = Modifier
                .size(35.dp)
                .alpha(if (isVisible) 0.65f else 0f)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
                .constrainAs(quantityPlus) {
                    start.linkTo(quantity.end)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase Quantity")
        }

        Text(
            text = item.product.title.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(name) {
                top.linkTo(image.bottom, 2.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        var rating by remember { mutableStateOf(5f) }
        StarRatingBar(
            maxStars = 5,
            rating = rating,
            onRatingChanged = { ratingChange ->
                rating = ratingChange
            },
            modifier = Modifier.constrainAs(ratingBar) {
                start.linkTo(parent.start)
                end.linkTo(sold.start)
                top.linkTo(name.bottom)
            }
        )

        Text(
            text = "500...",
            style = MaterialTheme.typography.labelSmall.copy(
                fontStyle = FontStyle.Italic,
                color = Color.Gray
            ),
            modifier = Modifier.constrainAs(sold) {
                top.linkTo(name.bottom)
                end.linkTo(parent.end)
                start.linkTo(ratingBar.end)
            }
        )

        Text(
            text = "${item.product.price}$",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.DarkGray,
                fontStyle = FontStyle.Italic
            ),
            modifier = Modifier.constrainAs(price) {
                top.linkTo(discountedPrice.top)
                bottom.linkTo(discountedPrice.bottom)
                end.linkTo(discountedPrice.start)
            },
            textDecoration = TextDecoration.LineThrough
        )

        Text(
            text = "${item.product.price / 2}$",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.constrainAs(discountedPrice) {
                top.linkTo(ratingBar.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(saveText.top)
            }.then(Modifier.clickable {
                isVisible = true
                quantityToBuy++
            })

        )

        Text(
            text = "SAVE 50% TODAY",
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.error),
            modifier = Modifier.constrainAs(saveText) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
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
    val starSize = (5f * density).dp
    val starSpacing = (0.05f * density).dp

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
                            onRatingChanged(i.toFloat())
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
fun ItemListProductPreview() {
    ItemListProduct(
        modifier = Modifier.background(color = Color.White),
        item = ProductDomainRW(
            Product(
                id = 6717, title = "eripuit", price = 5869, category = ProductCategory(
                    id = 3226,
                    name = "Errol Clemons",
                    image = "legere"
                ), images = ProductImages(urls = listOf())
            )
        ),
        onEvent = {}
    )
}

@Composable
@Preview
fun StoreLazyListForPreview(
    modifier: Modifier = Modifier,
    viewModel: StoreViewModel = StoreViewModel(repository = StoreRepositoryImplForPreview()),
) {
    StoreLazyCard(viewModel = viewModel)
}

@Composable
@Preview
fun StoreScreenPreview() {
    StoreScreen(
        navController = null,
        viewModel = StoreViewModel(repository = StoreRepositoryImplForPreview())
    )
}