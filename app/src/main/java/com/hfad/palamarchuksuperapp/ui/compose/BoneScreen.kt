package com.hfad.palamarchuksuperapp.ui.compose

import android.content.Context
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.compose.AppTheme
import com.hfad.palamarchuksuperapp.ui.reusable.elements.AppText
import com.hfad.palamarchuksuperapp.ui.reusable.elements.OrderCard
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
            TopAppBar(
                title = {
                    AppText(
                        modifier = Modifier,
                        value = "Orders",
                        appTextConfig = appTextConfig(
                            textStyle = MaterialTheme.typography.displaySmall,
                            textAlign = TextAlign.Center
                        )
                    )
                },
                elevation = 0.dp,
                contentColor = MaterialTheme.colorScheme.onSurface,
                backgroundColor = MaterialTheme.colorScheme.surface,
            )
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
            var searchQuery = remember { mutableStateOf("") }

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedTextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    modifier = Modifier
                        .fillMaxWidth(0.8f),
                    placeholder = { Text("Search orders...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.value.isNotEmpty()) {
                            IconButton(onClick = { searchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    singleLine = true
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
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(IntrinsicSize.Max),
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
