package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.compose.FeatureTheme
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.daggerViewModel
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.AuthRepositoryImpl
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.CryptoServiceKeystoreImpl
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.BoneViewModel
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.OrderPageState
import kotlinx.coroutines.flow.MutableStateFlow
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
            navController = navController,
        )
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class,
)
@Composable
fun BoneScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    boneViewModel: BoneViewModel = daggerViewModel<BoneViewModel>(
        factory = LocalBoneDependencies.current.viewModelFactory,
    ),
) {

    val tabs = listOf<BoneTab>(
        BoneTab(
            title = stringResource(R.string.orders),
            tooltipTitle = stringResource(R.string.order_page_tooltip),
            tooltipDescription = stringResource(R.string.order_page_tooltip_description),
        ),
        BoneTab(
            title = stringResource(R.string.payment),
            tooltipTitle = stringResource(R.string.payment_page_tooltip),
            tooltipDescription = stringResource(R.string.payment_page_tooltip_description),
        ),
        BoneTab(
            title = stringResource(R.string.sales),
            tooltipTitle = stringResource(R.string.sales_page_tooltip),
            tooltipDescription = stringResource(R.string.sales_page_tooltip_description),
        ),
        BoneTab(
            title = stringResource(R.string.balance),
            tooltipTitle = stringResource(R.string.balance_page_tooltip),
            tooltipDescription = stringResource(R.string.balance_page_tooltip_description),
        ),
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
                            matchContentSize = true,
                        ),
                        color = tabColorContent,
                        width = Dp.Unspecified,
                    )
                },
            ) {
                tabs.forEachIndexed { index, tab ->
                    val tooltipState = rememberTooltipState(isPersistent = false)
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(12.dp),
                        tooltip = {
                            RichTooltip(
                                caretSize = TooltipDefaults.caretSize,
                                title = {
                                    Text(tab.tooltipTitle)
                                },
                                colors = TooltipDefaults.richTooltipColors(
                                    containerColor = colorScheme.surface,
                                    contentColor = colorScheme.onSurface,
                                ),
                                tonalElevation = 0.dp,
                                shadowElevation = 5.dp,
                            ) {
                                Text(tab.tooltipDescription) //Add additional information
                            }
                        },
                        state = tooltipState,
                    ) {
                        Tab(
                            modifier = Modifier.clip(RoundedCornerShape(4.dp)),
                            text = {
                                AppText(
                                    value = tab.title,
                                    appTextConfig = appTextConfig(
                                        textAlign = TextAlign.Center,
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
                            },
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
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (expanded) {
                    SmallFloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                val a = AuthRepositoryImpl(
                                    //TODO Test
                                    httpClient = httpClient,
                                    context = context,
                                    cryptoService = CryptoServiceKeystoreImpl(),
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
                        4.dp,
                    ),
                    contentColor = colorScheme.onPrimary,
                    containerColor = colorScheme.primary,
                ) {
                    Icon(
                        modifier = Modifier,
                        imageVector = if (expanded) Icons.Filled.Close else Icons.AutoMirrored.Filled.List,
                        contentDescription = if (expanded) "Close" else "Expand",
                    )
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
                0 -> OrdersPageRoot()
                1 -> PaymentsPageRoot()
                2 -> SalesPageRoot()
                3 -> FinancePageRoot()
            }
        }
    }
}

data class BoneTab(
    val title: String,
    val tooltipTitle: String,
    val tooltipDescription: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BoneScreenPreview() {
    FeatureTheme {
        val tabs = listOf(
            stringResource(R.string.orders),
            stringResource(R.string.payment),
            stringResource(R.string.sales),
            stringResource(R.string.balance),
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
                    },
                ) {
                    tabs.forEachIndexed { index, title ->
                        val tooltipState = rememberTooltipState(isPersistent = false)
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(
                                12.dp,
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
                                    shadowElevation = 5.dp,
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
                                            textAlign = TextAlign.Center,
                                        ),
                                    )
                                },
                                selected = false,
                                onClick = {

                                },
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
                val pagingData = MutableStateFlow(
                    PagingData.from(
                        generateOrderItems(),
                    ),
                ).collectAsLazyPagingItems()
                when (page) {
                    0 -> OrdersPage(
                        event = {},
                        state = remember {
                            mutableStateOf(
                                OrderPageState(),
                            )
                        },
                        navController = null,
                        orderPaging = pagingData,
                    )
                }
            }
        }
    }
}
