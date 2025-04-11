package com.hfad.palamarchuksuperapp.feature.bone.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.effects.HideShowSystemBar
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.BoneFeatureViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BoneScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: BoneFeatureViewModel,
) {
    val a = viewModel
    val navController = LocalNavController.current

    HideShowSystemBar()
    BoneScreen(
        modifier = modifier,
        navController = navController
    )
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
) {
    val tabs = listOf("Заказы", "Платежи", "Продажи", "Финансы")
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
                    Tab(
                        modifier = Modifier.clip(CircleShape),
                        text = {
                            AppText(
                                value = title,
                                appTextConfig = appTextConfig(),
                                color = if (pagerState.currentPage == index)
                                    tabColorContent
                                else
                                    tabColorContent.copy(alpha = 0.6f)
                            )
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        },
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding()
                )
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.primaryContainer),
                userScrollEnabled = true,
                beyondViewportPageCount = 1,
            ) { page ->
                when (page) {
                    0 -> OrdersPage()
                    1 -> PaymentsPage()
                    2 -> SalesPage()
                    3 -> FinancePage()
                }
            }
        }
    }
}

@Preview
@Composable
fun BoneScreenPreview() {
    AppTheme(
        useDarkTheme = false
    ) {
        BoneScreen()
    }
}