package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.compose.FeatureTheme
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.daggerViewModel
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.AuthRepositoryImpl
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.CryptoServiceKeystoreImpl
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.OrderCard
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.StepperStatus
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.BoneViewModel
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.OrderPageState
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BoneScreenRoot(
    modifier: Modifier = Modifier,
) {
    val navController = LocalNavController.current

    //    HideShowSystemBar()

    val localTransitionScope = LocalSharedTransitionScope.current
        ?: error(IllegalStateException("No SharedElementScope found"))
    val animatedContentScope = LocalNavAnimatedVisibilityScope.current
        ?: error(IllegalStateException("No AnimatedVisibility found"))

    with(localTransitionScope) { //TODO
        BoneScreen(
            modifier = modifier.sharedElement(
                this.rememberSharedContentState("bone"),
                animatedContentScope,
            ),
            navController = navController
        )
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun BoneScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    boneViewModel: BoneViewModel = daggerViewModel<BoneViewModel>(
        factory = LocalBoneDependencies.current.viewModelFactory
    ),
) {

    val lazyPagingItems = boneViewModel.pagingDataFlow.collectAsLazyPagingItems()

    val tabs = listOf(
        stringResource(R.string.orders),
        stringResource(R.string.payment),
        stringResource(R.string.sales),
        stringResource(R.string.balance),
        "Settings"
    )
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    val coroutineScope = rememberCoroutineScope()

//    Call lambda on page change
//    LaunchedEffect(pagerState) {
//        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
//        }
//    }
    val tabColorContent = colorScheme.onSurface
    val tabColorBackground = colorScheme.surface

    BackHandler {
        if (pagerState.currentPage != 0) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(0)
            }
        } else {
            navController?.popBackStack()
        }
    }
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            PrimaryTabRow(
                modifier = Modifier.statusBarsPadding(),
                selectedTabIndex = pagerState.currentPage,
                containerColor = tabColorBackground,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(
                            pagerState.currentPage,
                            matchContentSize = true
                        ),
                        color = tabColorContent,
                        width = Dp.Unspecified,
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    val tooltipState = rememberTooltipState(isPersistent = false)
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(12.dp),
                        tooltip = {
                            RichTooltip(
                                caretSize = TooltipDefaults.caretSize,
                                title = {
                                    Text(title)
                                },
                                colors = TooltipDefaults.richTooltipColors(
                                    containerColor = colorScheme.secondaryContainer,
                                    contentColor = colorScheme.secondary,
                                ),
                                tonalElevation = 0.dp,
                                shadowElevation = 5.dp
                            ) {
                                Text("This is the main content of the rich tooltip") //Add additional information
                            }
                        },
                        state = tooltipState,
                    ) {
                        Tab(
                            modifier = Modifier.clip(RoundedCornerShape(4.dp)),
                            text = {
                                AppText(
                                    value = title,
                                    appTextConfig = appTextConfig(
                                        textAlign = TextAlign.Center
                                    ),
                                    color = if (pagerState.currentPage == index)
                                        tabColorContent
                                    else
                                        tabColorContent.copy(alpha = 0.6f),
                                )
                            },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    if (pagerState.currentPage == index) {
                                        tooltipState.show()
                                    } else {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            var expanded by remember { mutableStateOf(false) }
            val httpClient = LocalBoneDependencies.current.getFeatureHttpClient() //TODO
            val context = LocalContext.current
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (expanded) {
                    SmallFloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                val a = AuthRepositoryImpl( //TODO Test
                                    httpClient = httpClient,
                                    context = context,
                                    cryptoService = CryptoServiceKeystoreImpl()
                                )
                                a.logout()
                                navController?.navigate(FeatureBoneRoutes.LoginScreen) {
                                    popUpTo(FeatureBoneRoutes.BoneScreen) {
                                        inclusive = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier,
                        contentColor = colorScheme.onPrimary,
                        containerColor = colorScheme.primary,
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Edit")
                    }
                }

                SmallFloatingActionButton(
                    onClick = { expanded = !expanded },
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = FloatingActionButtonDefaults.elevation(
                        4.dp,
                        4.dp,
                        4.dp,
                        4.dp
                    ),
                    contentColor = colorScheme.onPrimary,
                    containerColor = colorScheme.primary,
                ) {
                    Icon(
                        modifier = Modifier,
                        imageVector = if (expanded) Icons.Filled.Close else Icons.AutoMirrored.Filled.List,
                        contentDescription = if (expanded) "Close" else "Expand"
                    )
                }
            }
        }
    ) { paddingValues ->
        if (true) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                color = Color.Red
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues)
            ) {
                items(count = lazyPagingItems.itemCount) { index ->
                    OrderCard(
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                        order = lazyPagingItems[index]!!,
                        initialStatus = StepperStatus.entries.random(),
                        internalPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                    )
                }


                lazyPagingItems.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            // You can add shimmer here
                        }

                        loadState.append is LoadState.Loading -> {
                            item {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }

                        loadState.append is LoadState.Error -> {
                            // Handle error
                        }
                    }
                }
            }
//        HorizontalPager(
//            state = pagerState,
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues),
//            userScrollEnabled = true,
//            beyondViewportPageCount = 4,
//        ) { page ->
//            when (page) {
//                0 -> OrdersPageRoot()
//                1 -> PaymentsPage()
//                2 -> SalesPage()
//                3 -> FinancePage()
//            }
//        }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BoneScreenPreview() {
    FeatureTheme {
        val tabs = listOf(
            stringResource(R.string.orders),
            stringResource(R.string.payment),
            stringResource(R.string.sales),
            stringResource(R.string.balance)
        )
        val pagerState = rememberPagerState(initialPage = 0) { tabs.size }

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                PrimaryTabRow(
                    modifier = Modifier.statusBarsPadding(),
                    selectedTabIndex = 0,
                    indicator = {
                        TabRowDefaults.PrimaryIndicator()
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        val tooltipState = rememberTooltipState(isPersistent = false)
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(
                                12.dp
                            ),
                            tooltip = {
                                RichTooltip(
                                    caretSize = TooltipDefaults.caretSize,
                                    title = {
                                        Text(title)
                                    },
                                    colors = TooltipDefaults.richTooltipColors(
                                        containerColor = colorScheme.secondaryContainer,
                                        contentColor = colorScheme.secondary,
                                    ),
                                    tonalElevation = 0.dp,
                                    shadowElevation = 5.dp
                                ) {
                                    Text("This is the main content of the rich tooltip") //Add additional information
                                }
                            },
                            state = tooltipState,
                        ) {
                            Tab(
                                modifier = Modifier.clip(RoundedCornerShape(4.dp)),
                                text = {
                                    AppText(
                                        value = title,
                                        appTextConfig = appTextConfig(
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                },
                                selected = false,
                                onClick = {

                                }
                            )
                        }
                    }
                }
            },
        ) { paddingValues ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                userScrollEnabled = true,
                beyondViewportPageCount = 4,
            ) { page ->
                when (page) {
                    0 -> OrdersPage(
                        event = {},
                        state = remember {
                            mutableStateOf(
                                OrderPageState()
                            )
                        },
                        navController = null
                    )
                }
            }
        }
    }
}