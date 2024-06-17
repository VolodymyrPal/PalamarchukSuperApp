package com.hfad.palamarchuksuperapp.compose

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.compose.utils.BottomNavBar
import com.hfad.palamarchuksuperapp.data.entities.Skill
import com.hfad.palamarchuksuperapp.data.repository.SkillsRepositoryImplDummy
import com.hfad.palamarchuksuperapp.presentation.common.SkillDomainRW
import com.hfad.palamarchuksuperapp.presentation.screens.BottomSheetFragment
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsChangeConst
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsViewModel
import com.hfad.palamarchuksuperapp.presentation.viewModels.UiEvent
import com.hfad.palamarchuksuperapp.presentation.viewModels.daggerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("detekt.FunctionNaming", "detekt.LongMethod")
@Composable
fun SkillScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController?,
    context: Context = LocalContext.current,
    viewModelFactory: ViewModelProvider.Factory = context.appComponent.skillsViewModelFactory(),
    viewModel: SkillsViewModel = daggerViewModel<SkillsViewModel>(factory = viewModelFactory)
) {

    DisposableEffect(key1 = Unit) {
        onDispose {
            viewModel.onCleared()
        }
    }
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
                onClick = {
                    val bottomSheetFragment = BottomSheetFragment(
                        viewModel = viewModel
                    )
                    bottomSheetFragment.show((context as FragmentActivity).supportFragmentManager, "BSDialogFragment")
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
            val state by viewModel.state.collectAsState()
            LaunchedEffect(key1 = viewModel) {
                viewModel.handleEvent(UiEvent.GetSkills)
            }

            when {
                state.loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                state.error != null -> {
                    Text(text = "Error: ${state.error}", color = Color.Red)
                }

                else -> {
                    LazyColumn {
                        items(
                            items = state.skills,
                            key = { item: SkillDomainRW -> item.skill.uuid.toString() }
                        ) { item ->
                            AnimatedVisibility(
                                modifier = Modifier.animateItem(),
                                visible = item.isVisible,
                                exit = fadeOut(
                                    animationSpec = TweenSpec(100, 100, LinearEasing)
                                ),
                                enter = fadeIn(
                                    animationSpec = TweenSpec(100, 100, LinearEasing)
                                )
                            ) {
                                ItemListSkill(
                                    item = item,
                                    onEvent = { uiEvent -> viewModel.handleEvent(event = uiEvent) },
                                    viewModel = viewModel
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.size(72.dp))
                        }
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
}

@Composable
@Suppress(
    "detekt.FunctionNaming",
    "detekt.MaxLineLength",
    "detekt.LongMethod",
    "detekt.LongParameterList",
    "detekt.DestructuringDeclarationWithTooManyEntries"
)
fun ItemListSkill(
    modifier: Modifier = Modifier,
    item: SkillDomainRW,
    onEvent: (UiEvent) -> Unit,
    viewModel: SkillsViewModel = SkillsViewModel(SkillsRepositoryImplDummy()),
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = if (!isExpanded) {
            Modifier
                .padding(start = 6.dp, top = 6.dp, end = 6.dp, bottom = 6.dp)
                .fillMaxWidth()
                .clickable {
                    viewModel.updateSkillOrAdd(item, SkillsChangeConst.ChooseOrNotSkill)
                }
        } else {
            Modifier
                .padding(start = 6.dp, top = 6.dp, end = 6.dp, bottom = 6.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable {
                    viewModel.updateSkillOrAdd(item, SkillsChangeConst.ChooseOrNotSkill)
                }
        },
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
                text = item.skill.name,
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
                    text = item.skill.description,
                    maxLines = if (!isExpanded) 2 else Int.MAX_VALUE,
                    overflow = if (!isExpanded) TextOverflow.Ellipsis else TextOverflow.Visible,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    onTextLayout = {
                        if (it.hasVisualOverflow || isExpanded) {
                            isVisible = true
                        } else {
                            isVisible = false
                        }
                    })

            }
            Text(modifier = modifier
                .constrainAs(date) {
                    bottom.linkTo(parent.bottom)
                }
                .padding(start = 2.dp),
                text = SimpleDateFormat("dd MMMM yyyy: HH:mm", Locale.US).format(item.skill.date),
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
                onClick = {
                    expanded = true
                },
                content = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.more_button),
                        contentDescription = "More menu"
                    )
                    MyDropDownMenus(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        onEdit = {
                            val bottomSheetFragment = BottomSheetFragment(
                                viewModel = viewModel,
                                skillDomainRW = item
                            )
                            bottomSheetFragment.show((LocalContext as FragmentActivity).supportFragmentManager, "BSDialogFragment")
                        },
                        onDelete = {
                            onEvent.invoke(UiEvent.DeleteItem(item))
                        },
                        onMoveUp = { onEvent(UiEvent.MoveItemUp(item)) },

                        )
                })

            Checkbox(
                modifier = modifier.constrainAs(choose) {
                    top.linkTo(menu.bottom)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
                onCheckedChange = {
                    viewModel.updateSkillOrAdd(item, SkillsChangeConst.ChooseOrNotSkill)
                },
                checked = item.chosen
            )

            Box(
                modifier = Modifier
                    .constrainAs(expand) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .clickable(enabled = isVisible) {
                        isExpanded = !isExpanded
                    }
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
                    text = if (!isExpanded) "Details >>" else ("<< Hide"),
                )
            }
        }
    }
}

@Suppress("detekt.FunctionNaming", "detekt.LongMethod", "detekt.LongParameterList", "detekt.UnusedParameter")
@Composable
fun MyDropDownMenus(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onAdding: () -> Unit = {},
) {
    if (expanded) {
        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
        ) {
            DropdownMenu(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(16.dp)),
                expanded = expanded,
                onDismissRequest = onDismissRequest
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = { /* Handle refresh! */
                        onEdit()
                        onDismissRequest()
                    }
                )

                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        onDelete()
                        onDismissRequest()
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))

                DropdownMenuItem(
                    text = { Text("Move UP") },
                    onClick = { /* Handle send feedback! */
                        onMoveUp()
                        onDismissRequest()
                    }
                )
            }
        }
    }
}

@Suppress("detekt.FunctionNaming", "detekt.LongMethod", "detekt.LongParameterList")
@Composable
@Preview
fun ListItemSkillPreview() {
    ItemListSkill(
        item = SkillDomainRW(
            skill = Skill(
                name = "MySkill",
                description = "Some good skills, that i know",
                date = Date()
            )
        ),
        onEvent = {
        },
    )
    println("true".toBooleanStrictOrNull())
}

@Suppress("detekt.FunctionNaming", "detekt.LongMethod", "detekt.LongParameterList")
@Composable
@Preview
fun SkillScreenPreview() {
    SkillScreen(navController = null)
}