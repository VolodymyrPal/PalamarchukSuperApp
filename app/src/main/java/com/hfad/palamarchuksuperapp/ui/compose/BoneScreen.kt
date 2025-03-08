package com.hfad.palamarchuksuperapp.ui.compose

import android.content.Context
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.compose.AppTheme
import com.hfad.palamarchuksuperapp.ui.reusable.elements.AppText
import com.hfad.palamarchuksuperapp.ui.reusable.elements.appTextConfig
import com.hfad.palamarchuksuperapp.ui.viewModels.BusinessEntity

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BoneScreenRoot(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    navController: NavHostController = LocalNavController.current,
) {
    val localTransitionScope = LocalSharedTransitionScope.current
        ?: error(IllegalStateException("No SharedElementScope found"))
    val animatedContentScope = LocalNavAnimatedVisibilityScope.current
        ?: error(IllegalStateException("No AnimatedVisibility found"))

    with(localTransitionScope) {
        BoneScreen(
            modifier = modifier.sharedElement(
                this.rememberSharedContentState("bone"),
                animatedContentScope,
            ),
//            viewModel = ViewModel(),
            navController = navController,
        )
    }
}

@Composable
fun BoneScreen(
    modifier: Modifier = Modifier,
//    viewModel: ViewModel? = null,
    navController: NavController? = null,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {

        },
        floatingActionButton = {

        },
        bottomBar = {

        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding()
                )
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppText(
                    modifier = Modifier.padding(8.dp),
                    value = "Orders",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.displayMedium,
                    )
                )
                val items = listOf(
                    BusinessEntity(name = "43222", manager = "VP +3806338875"),
                    BusinessEntity(name = "42224", manager = "VP +3806338875"),
                    BusinessEntity(name = "43226", manager = "VP +3806338875")
                )
                Box(modifier = Modifier.padding(16.dp)) {
                    LazyColumn {
                        items(items = items) {
                            OrderCard(
                                modifier = Modifier
                                    .padding(top = 8.dp),
                                entity = it
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun OrderCard(
    modifier: Modifier = Modifier,
    entity: BusinessEntity,
) {
    Card(
        modifier = modifier.fillMaxWidth(0.9f),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, Color.Blue),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
        ) {
            AppText(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.TopCenter),
                value = "Order: ${entity.name}",
            )
            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = {},
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                )
            }
            StepProgressionBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun StepProgressionBar(
    modifier: Modifier = Modifier,
    numberOfSteps: Int = 3,
    currentStep: Int = 1,
) {

    Box(modifier = modifier.background(Color.Transparent)) {
        Divider(
            modifier = Modifier.align(Alignment.CenterStart),
            color = Color.Black.copy(1f),
            thickness = (1.5).dp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (step in 0..numberOfSteps - 1) {
                Step(
                    modifier = Modifier,
                    isCompete = step < currentStep,
                    isCurrent = step == currentStep,
                )
            }
        }
    }
}

@Composable
fun Step(
    modifier: Modifier = Modifier,
    isCurrent: Boolean = false,
    isCompete: Boolean = false,
) {
    val color = if (isCompete || isCurrent) Color(0xFF2E7D32) else Color(0xFFBDBDBD)
    val innerCircleColor =
        if (isCompete) Color(0xFF2E7D32)
        else if (isCurrent) {
            Color(0xFFFFD54F).copy(red = 0.7f, blue = 0.85f)
        } else Color(0xFFF5F5F5)
    val painter = rememberVectorPainter(Icons.Default.Check)



    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .size(15.dp)
                .align(Alignment.CenterEnd)
                .border(
                    shape = CircleShape,
                    width = 2.dp,
                    color = color
                ),
            onDraw = {
                drawCircle(color = innerCircleColor)
                drawIntoCanvas { canvas ->
                    if (isCompete) {
                        with(painter) {
                            draw(
                                Size(40f, 40f),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }
                }
            }
        )
    }
}


@Composable
fun HolderEntityCard(
    modifier: Modifier = Modifier,
    entity: BusinessEntity,
) {
    Surface(
        modifier = modifier.size(150.dp, 150.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {
            AppText(
                modifier = Modifier.align(Alignment.TopStart),
                value = "Name: ${entity.name}",
            )
            AppText(
                modifier = Modifier.align(Alignment.BottomEnd),
                value = "Type: ${entity.entityType}",
            )
            AppText(
                modifier = Modifier.align(Alignment.Center),
                value = "Manager: ${entity.manager}",
            )
        }
    }
}


@Preview
@Composable
fun BoneScreenPreview() {
    AppTheme {
        BoneScreen()
    }
}


@Preview
@Composable
fun OrderCardPreview() {
    AppTheme {
        OrderCard(
            modifier = Modifier,
            entity = BusinessEntity(
                name = "Client #1",
                manager = "VP +3806338875"
            )
        )
    }
}

@Preview
@Composable
fun HolderEntityCardPreview(
    modifier: Modifier = Modifier,
    entity: BusinessEntity = BusinessEntity(
        name = "Client #1",
        manager = "VP +3806338875"
    ),
) {
    AppTheme {
        HolderEntityCard(
            modifier = modifier,
            entity = entity
        )
    }
}
