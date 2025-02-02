package com.hfad.palamarchuksuperapp.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.res.stringResource
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
import androidx.fragment.app.FragmentActivity
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.domain.models.Skill
import com.hfad.palamarchuksuperapp.ui.compose.utils.BottomNavBar
import com.hfad.palamarchuksuperapp.ui.screens.BottomSheetFragment
import com.hfad.palamarchuksuperapp.ui.viewModels.SkillsChangeConst
import com.hfad.palamarchuksuperapp.ui.viewModels.SkillsViewModel
import com.hfad.palamarchuksuperapp.ui.viewModels.daggerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalSharedTransitionApi::class)
@Suppress("detekt.FunctionNaming", "detekt.LongMethod")
@Composable
fun SkillScreen(
    modifier: Modifier = Modifier,
    viewModel: SkillsViewModel = daggerViewModel<SkillsViewModel>(
        factory = LocalContext.current.appComponent.viewModelFactory()
    ),
) {

    val localTransitionScope = LocalSharedTransitionScope.current
        ?: error(IllegalStateException("No SharedElementScope found"))
    val animatedContentScope = LocalNavAnimatedVisibilityScope.current
        ?: error(IllegalStateException("No AnimatedVisibility found"))

    with(localTransitionScope) {//TODO
        Scaffold(
            modifier = modifier
                .fillMaxSize(),
            bottomBar = {
                BottomNavBar()
            },
            floatingActionButton = {
                val context = LocalContext.current
                FloatingActionButton(
                    shape = RoundedCornerShape(33),
                    modifier = Modifier,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    onClick = remember {
                        {
                            val bottomSheetFragment = BottomSheetFragment(
                                viewModelEvent = viewModel::event
                            )
                            bottomSheetFragment.show(
                                (context as FragmentActivity).supportFragmentManager,
                                "BSDialogFragment"
                            )
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
                    .sharedBounds(
                        this.rememberSharedContentState("skill"),
                        animatedContentScope
                    )
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                val state by viewModel.uiState.collectAsState()
                //viewModel.fetchSkills()
                if (state.loading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }

                if (state.items.isEmpty()) {
                    Text(text = stringResource(R.string.error_refresh_or_add_skill_screen))
                }


                if (state.error != null) {
                    Text(
                        text = stringResource(
                            R.string.error_with_error,
                            state.error ?: "Unknown error"
                        ),
                        color = Color.Red
                    )
                }

                if (!state.items.isEmpty()) {
                    LazyList(
                        modifier = Modifier.fillMaxSize(),
                        skillList = state.items,
                        viewModelEvent = viewModel::event
                    )
                }

            }
        }
    }
}

@Composable
fun LazyList(
    modifier: Modifier = Modifier,
    skillList: List<Skill>,
    viewModelEvent: (SkillsViewModel.Event) -> Unit,
) {
    LazyColumn {
        items(
            items = skillList,
            key = { item: Skill -> item.uuid.toString() }
        ) { skill ->
            AnimatedVisibility(
                modifier = Modifier.animateItem(),
                visible = skill.isVisible,
                exit = fadeOut(
                    animationSpec = TweenSpec(100, 100, LinearEasing)
                ),
                enter = fadeIn(
                    animationSpec = TweenSpec(100, 100, LinearEasing)
                )
            ) {
                ItemListSkill(
                    item = skill,
                    onEvent = viewModelEvent,
                )
            }
        }

        item {
            Spacer(modifier = Modifier.size(72.dp))
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
    item: Skill,
    onEvent: (SkillsViewModel.Event) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = if (!isExpanded) {
            modifier
                .padding(start = 6.dp, top = 6.dp, end = 6.dp, bottom = 6.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .then(remember(item) {
                    Modifier.clickable {
                        onEvent.invoke(
                            SkillsViewModel.Event.EditItem(
                                item,
                                SkillsChangeConst.ChooseOrNotSkill
                            )
                        )
                    }
                })
        } else {
            modifier
                .padding(start = 6.dp, top = 6.dp, end = 6.dp, bottom = 6.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .then(remember(item) {
                    Modifier.clickable {
                        onEvent.invoke(
                            SkillsViewModel.Event.EditItem(
                                item,
                                SkillsChangeConst.ChooseOrNotSkill
                            )
                        )
                    }
                })
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        border = BorderStroke(width = 0.5.dp, color = Color.Gray)

    ) {
        var isVisible by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Column(
                    Modifier.weight(0.9f)
                ) {

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 6.dp, end = 6.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Text(
                            modifier = Modifier,
                            text = item.description,
                            maxLines = if (!isExpanded) 2 else Int.MAX_VALUE,
                            overflow = if (!isExpanded) TextOverflow.Ellipsis else TextOverflow.Visible,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            onTextLayout = remember(isExpanded) {
                                { textLayoutResult ->
                                    isVisible =
                                        textLayoutResult.hasVisualOverflow || isExpanded
                                }
                            }
                        )
                    }
                }

                Column(
                    modifier = Modifier.wrapContentHeight(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    val context = LocalContext.current

                    Icon(
                        modifier = Modifier.clickable {
                            expanded = true
                        },
                        imageVector = ImageVector.vectorResource(id = R.drawable.more_button),
                        contentDescription = "More menu"
                    )
                    MyDropDownMenus(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        onEdit = remember(item) {
                            {
                                val bottomSheetFragment = BottomSheetFragment(
                                    viewModelEvent = onEvent,
                                    skill = item
                                )
                                bottomSheetFragment.show(
                                    (context as FragmentActivity).supportFragmentManager,
                                    "BSDialogFragment"
                                )
                            }
                        },
                        item = item,
                        onEvent = onEvent,
                    )

                    Checkbox(
                        modifier = modifier,
                        onCheckedChange = {
                            onEvent.invoke(
                                SkillsViewModel.Event.EditItem(
                                    item,
                                    SkillsChangeConst.ChooseOrNotSkill
                                )
                            )
                        },
                        checked = item.chosen
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            BasicText(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(0.4f),
                text = SimpleDateFormat(
                    "dd MMMM yyyy: HH:mm",
                    Locale.US
                ).format(item.date),
                style = TextStyle(
                    fontStyle = FontStyle.Italic,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Left,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            BasicText(
                modifier = Modifier
                    .weight(0.5f)
                    .animateContentSize()
                    .then(remember(isVisible) {
                        Modifier.clickable { isExpanded = !isExpanded }
                    }),
                style = TextStyle(
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic,
                    color = if (!isVisible) MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0f

                    ) else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.5f
                    ),
                    textAlign = TextAlign.Left
                ),

                text = if (!isExpanded) {
                    stringResource(R.string.details) + " >>"
                } else {
                    stringResource(R.string.hide) + " <<"
                }
            )
        }
    }
}


@Suppress(
    "detekt.FunctionNaming",
    "detekt.LongMethod",
    "detekt.LongParameterList",
    "detekt.UnusedParameter"
)
@Composable
fun MyDropDownMenus(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    item: Skill,
    onEvent: (SkillsViewModel.Event) -> Unit,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
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
                    text = { Text(stringResource(R.string.edit)) },
                    onClick = remember {
                        {
                            onEdit()
                            onDismissRequest()
                        }
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.delete)) },
                    onClick = remember {
                        {
                            onEvent.invoke(SkillsViewModel.Event.DeleteItem(item))
                            onDismissRequest()
                        }
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.delete_all_chosen)) },
                    onClick = {
                        onEvent.invoke(SkillsViewModel.Event.DeleteAllChosen)
                        onDismissRequest()
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.move_up)) },
                    onClick = remember {
                        {
                            onEvent(
                                SkillsViewModel.Event.MoveToFirstPosition(
                                    item
                                )
                            )
                            onDismissRequest()
                        }
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
        item = Skill(
            id = 1,
            name = "name",
            date = Date(),
            chosen = false
        ),
        onEvent = {
        },
    )
    println("true".toBooleanStrictOrNull())
}

//@Suppress("detekt.FunctionNaming", "detekt.LongMethod", "detekt.LongParameterList")
//@Composable
//@Preview   //TODO
//fun SkillScreenPreview() {
//    SkillScreen(
//        viewModel = SkillsViewModel(
//            SkillsRepositoryImplForPreview(),
//            mainDispatcher = Dispatchers.Main,
//            ioDispatcher = Dispatchers.IO
//        ),
//    )
//}