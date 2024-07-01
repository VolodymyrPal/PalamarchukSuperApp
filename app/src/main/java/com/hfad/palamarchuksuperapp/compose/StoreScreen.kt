package com.hfad.palamarchuksuperapp.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.fragment.app.FragmentActivity
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.compose.utils.BottomNavBar
import com.hfad.palamarchuksuperapp.data.repository.StoreRepositoryImplForPreview
import com.hfad.palamarchuksuperapp.presentation.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.presentation.viewModels.State
import com.hfad.palamarchuksuperapp.presentation.viewModels.StoreViewModel
import com.hfad.palamarchuksuperapp.presentation.viewModels.daggerViewModel
import java.text.SimpleDateFormat
import java.util.Locale

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
            color = Color.Transparent, modifier = modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())

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
                        viewModel = viewModel,
                    )
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
        }
    }
}

@Composable
fun StoreScreenContent(
    modifier: Modifier = Modifier,
    viewModel: StoreViewModel,
) {
    Column {
        StoreLazyCard(
            viewModel = viewModel
        )
    }
}

@Composable
fun StoreLazyCard(
    modifier: Modifier = Modifier,
    viewModel: StoreViewModel = StoreViewModel(repository = StoreRepositoryImplForPreview()),
) {
    val productList by viewModel.uiState.collectAsState()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(text = "Lions")
        LazyColumn {
            items(
                items = (productList as State.Success<List<ProductDomainRW>>).data,
                key = { item: ProductDomainRW -> item.product.id }
            ) { item ->
                AnimatedVisibility(
                    modifier = Modifier.animateItem(),
                    visible = true,
                    exit = fadeOut(
                        animationSpec = TweenSpec(100, 100, LinearEasing)
                    ),
                    enter = fadeIn(
                        animationSpec = TweenSpec(100, 100, LinearEasing)
                    )
                ) {
                    ItemListProduct(
                        item = item,
                        onEvent = remember { { event -> viewModel.event(event) } },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ItemListProduct(
    modifier: Modifier = Modifier,
    item: ProductDomainRW,
    onEvent: (StoreViewModel.Event) -> Unit,
    viewModel: StoreViewModel = StoreViewModel(repository = StoreRepositoryImplForPreview()),
) {
    Card(
        modifier =
        Modifier
            .padding(start = 6.dp, top = 6.dp, end = 6.dp, bottom = 6.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .then(remember(item) {
                Modifier.clickable {

                }
            }),

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        border = BorderStroke(width = 0.5.dp, color = Color.Gray)

    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .animateContentSize()
        ) {
            val (name, description, date, menu, choose, expand) = createRefs()

            var isVisible by remember { mutableStateOf(false) }

            Text(modifier = modifier
                .constrainAs(name) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
                .padding(
                    paddingValues = PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = 4.dp
                    )
                ),
                text = item.product.title,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(modifier = Modifier
                .fillMaxWidth()
                .constrainAs(description) {
                    top.linkTo(name.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(menu.start)
                    bottom.linkTo(date.top)
                    width = Dimension.fillToConstraints
                }
                .wrapContentWidth()

                .padding(start = 6.dp, end = 6.dp),
                contentAlignment = Alignment.TopStart) {
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = item.product.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis ,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

            }
            Text(modifier = modifier
                .constrainAs(date) {
                    bottom.linkTo(parent.bottom)
                }
                .padding(start = 2.dp),
                text = SimpleDateFormat("dd MMMM yyyy: HH:mm", Locale.US).format(item.product.price),
                fontStyle = FontStyle.Italic,
                fontSize = 11.sp,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            var expanded by remember { mutableStateOf(false) }


            IconButton(
                modifier = modifier
                    .constrainAs(menu) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .wrapContentSize(),
                onClick = remember {
                    {
                        expanded = true
                    }
                },
                content = {
                    val context = LocalContext.current
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.more_button),
                        contentDescription = "More menu"
                    )
                })

            Checkbox(
                modifier = modifier.constrainAs(choose) {
                    top.linkTo(menu.bottom)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
                onCheckedChange = {
                },
                checked = true
            )

            Box(
                modifier = Modifier
                    .constrainAs(expand) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .then(remember(isVisible) {
                        Modifier.clickable {

                        }
                    })
            ) {
                BasicText(
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        color = if (!isVisible) MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0f

                        ) else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.5f
                        )
                    ),
                    modifier = Modifier.animateContentSize(),
                    text = "Details >>",
                )
            }
        }
    }
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