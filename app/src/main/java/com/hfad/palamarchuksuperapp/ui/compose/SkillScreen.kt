package com.hfad.palamarchuksuperapp.ui.compose

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.defaultAppTextColor
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.daggerViewModel
import com.hfad.palamarchuksuperapp.data.dtos.SkillEntity
import com.hfad.palamarchuksuperapp.domain.models.Skill
import com.hfad.palamarchuksuperapp.ui.compose.utils.BottomNavBar
import com.hfad.palamarchuksuperapp.ui.screens.BottomSheetFragment
import com.hfad.palamarchuksuperapp.ui.viewModels.SkillsChangeConst
import com.hfad.palamarchuksuperapp.ui.viewModels.SkillsViewModel
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
    val animatedContentScope = LocalNavAnimatedVisibilityScope.current ?: error(
        IllegalStateException("No AnimatedVisibility found")
    )

    with(localTransitionScope) {//TODO
        Scaffold(
            modifier = modifier.fillMaxSize(),
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
                        this.rememberSharedContentState("skill"), animatedContentScope
                    )
                    .padding()
            ) {
                val state by viewModel.uiState.collectAsState()

                if (state.loading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }

                if (state.items.isEmpty()) {
                    AppText(value = R.string.error_refresh_or_add_skill_screen)
                }


                if (state.error != null) {
                    AppText(
                        value = stringResource(
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
                        viewModelEvent = viewModel::event,
                        bottomSpace = paddingValues.calculateBottomPadding()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyList(
    modifier: Modifier = Modifier,
    skillList: List<Skill>,
    viewModelEvent: (SkillsViewModel.Event) -> Unit,
    bottomSpace: Dp = 0.dp,
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = skillList,
            key = { item: Skill ->
                item.skillEntity.id
            }
        ) { skill ->
            NewItemListSkill(
                item = skill,
                onEvent = viewModelEvent,
                modifier = Modifier.animateItem(
                    fadeInSpec = spring(stiffness = Spring.StiffnessMedium),
                    placementSpec = spring(
                        stiffness = 100f,
                        dampingRatio = 0.6f,
                        visibilityThreshold = IntOffset.VisibilityThreshold
                    ),
                    fadeOutSpec =
                        spring(stiffness = 1500f),
                )
            )
        }
        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomSpace)
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
                    })

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.delete)) },
                    onClick = remember {
                        {
                            onEvent.invoke(SkillsViewModel.Event.DeleteItem(item))
                            onDismissRequest()
                        }
                    })

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.delete_all_chosen)) },
                    onClick = {
                        onEvent.invoke(SkillsViewModel.Event.DeleteAllChosen)
                        onDismissRequest()
                    })

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

@Composable
@Suppress(
    "detekt.FunctionNaming",
    "detekt.MaxLineLength",
    "detekt.LongMethod",
    "detekt.LongParameterList",
    "detekt.DestructuringDeclarationWithTooManyEntries"
)
fun NewItemListSkill(
    modifier: Modifier = Modifier,
    item: Skill,
    onEvent: (SkillsViewModel.Event) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        onClick = {
            onEvent.invoke(
                SkillsViewModel.Event.EditItem(
                    item,
                    SkillsChangeConst.ChooseOrNotSkill
                )
            )
        },
        shape = MaterialTheme.shapes.medium,
        modifier = if (!isExpanded) {
            modifier
                .padding(start = 6.dp, top = 6.dp, end = 6.dp, bottom = 6.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        } else {
            modifier
                .padding(start = 6.dp, top = 6.dp, end = 6.dp, bottom = 6.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        border = BorderStroke(width = 0.5.dp, color = MaterialTheme.colorScheme.onPrimaryContainer)

    ) {
        var isVisible by remember { mutableStateOf(true) }

        Row(
            modifier = Modifier.height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AppText(
                    modifier = Modifier
                        .padding(4.dp, 4.dp, 4.dp, 4.dp)
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth(),
                    value = item.skillEntity.name,
                    appTextConfig = appTextConfig(
                        fontWeight = FontWeight.Bold,
                        textStyle = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                    )
                )
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.TopStart
                ) {
                    AppText(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 6.dp, top = 2.dp, bottom = 2.dp),
                        value = item.skillEntity.description,
                        appTextConfig = AppTextConfig(
                            maxLines = if (!isExpanded) 3 else Int.MAX_VALUE,
                            overflow = if (!isExpanded) TextOverflow.Ellipsis else TextOverflow.Visible,
                            onTextLayout = { textLayoutResult ->
                                isVisible = textLayoutResult.hasVisualOverflow || isExpanded
                            },
                            textAlign = TextAlign.Justify
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 2.dp, top = 2.dp)
                ) {
                    AppText(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .width(IntrinsicSize.Max),
                        value = SimpleDateFormat(
                            "dd MMMM yyyy: HH:mm", Locale.US
                        ).format(item.skillEntity.date),
                        appTextConfig = appTextConfig(
                            TextStyle(
                                fontStyle = FontStyle.Italic,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Left,
                            )
                        ),
                        color = defaultAppTextColor().copy(alpha = 0.4f)
                    )
                    if (isVisible) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isVisible) {
                                AppText(
                                    value = if (!isExpanded) {
                                        stringResource(R.string.details)
                                    } else {
                                        stringResource(R.string.hide)
                                    },
                                    modifier = Modifier
                                        .animateContentSize()
                                        .align(Alignment.CenterEnd)
                                        .then(remember(isVisible) {
                                            Modifier.clickable(
                                                interactionSource = null,
                                                indication = null,
                                                onClick = { isExpanded = !isExpanded })
                                        }),
                                    color = defaultAppTextColor().copy(alpha = 0.5f),
                                    appTextConfig = appTextConfig(
                                        textStyle = TextStyle(
                                            fontSize = 10.sp,
                                            fontStyle = FontStyle.Italic,
                                            textAlign = TextAlign.Right
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
            ) {
                var expanded by remember { mutableStateOf(false) }
                val context = LocalContext.current

                IconButton(
                    onClick = { expanded = true },
                    enabled = true,
                ) {
                    Icon(
                        ImageVector.vectorResource(id = R.drawable.more_button),
                        contentDescription = "More menu"
                    )
                }
                MyDropDownMenus(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    onEdit = remember(item) {
                        {
                            val bottomSheetFragment = BottomSheetFragment(
                                viewModelEvent = onEvent, skill = item
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
                    modifier = modifier, onCheckedChange = {
                        onEvent.invoke(
                            SkillsViewModel.Event.EditItem(
                                item, SkillsChangeConst.ChooseOrNotSkill
                            )
                        )
                    }, checked = item.chosen
                )
            }
        }
    }
}

@Composable
@Preview
fun NewListItemSkillPreview() {
    NewItemListSkill(
        item = Skill(
            skillEntity = SkillEntity(
                id = 1,
                name = "Skill name",
                description = "LONG SKILL DESCRIPTION LONG SKILL DESCRIPTION LONG SKILL DESCRIPTION LONG " +
                        "SKILL DESCRIPTION LONG SKILL DESCRIPTION LONG SKILL DESCRIPTION LONG SKILL " +
                        "DESCRIPTION LONG SKILL DESCRIPTION LONG SKILL DESCRIPTION LONG SKILL DESCRIPTION " +
                        "LONG SKILL DESCRIPTION LONG SKILL DESCRIPTION LONG SKILL DESCRIPTION ",
                date = Date(),
            ),
            chosen = true,
        ),
        onEvent = {}
    )
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
//@Composable
//@Suppress(
//    "detekt.FunctionNaming",
//    "detekt.MaxLineLength",
//    "detekt.LongMethod",
//    "detekt.LongParameterList",
//    "detekt.DestructuringDeclarationWithTooManyEntries"
//)
//fun ItemListSkill(
//    modifier: Modifier = Modifier,
//    item: Skill,
//    onEvent: (SkillsViewModel.Event) -> Unit,
//) {
//    var isExpanded by remember { mutableStateOf(false) }
//
//    Card(
//        onClick = {
//            onEvent.invoke(
//                SkillsViewModel.Event.EditItem(
//                    item, SkillsChangeConst.ChooseOrNotSkill
//                )
//            )
//        },
//        shape = MaterialTheme.shapes.medium, modifier = if (!isExpanded) {
//            modifier
//                .padding(start = 6.dp, top = 6.dp, end = 6.dp, bottom = 6.dp)
//                .fillMaxWidth()
//                .height(IntrinsicSize.Min)
//
//        } else {
//            modifier
//                .padding(start = 6.dp, top = 6.dp, end = 6.dp, bottom = 6.dp)
//                .fillMaxWidth()
//                .height(IntrinsicSize.Min)
//        },
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer
//        ),
//        border = BorderStroke(width = 0.5.dp, color = Color.Gray)
//
//    ) {
//        var isVisible by remember { mutableStateOf(false) }
//
//        Column(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.SpaceBetween
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(IntrinsicSize.Min),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                Column(
//                    Modifier.weight(0.9f)
//                ) {
//
//                    AppText(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(0.dp, 4.dp, 0.dp, 4.dp),
//                        value = item.skillEntity.name,
//                        appTextConfig = appTextConfig(
//                            fontWeight = FontWeight.Bold,
//                            textStyle = MaterialTheme.typography.titleMedium,
//                            fontFamily = FontFamily.Serif,
//                            textAlign = TextAlign.Center,
//                        )
//                    )
//                    Box(
//                        modifier = Modifier.padding(start = 6.dp, end = 6.dp),
//                        contentAlignment = Alignment.TopStart
//                    ) {
//                        AppText(
//                            modifier = Modifier,
//                            value = item.skillEntity.description,
//                            appTextConfig = AppTextConfig(
//                                maxLines = if (!isExpanded) 2 else Int.MAX_VALUE,
//                                overflow = if (!isExpanded) TextOverflow.Ellipsis else TextOverflow.Visible,
//                                onTextLayout = { textLayoutResult ->
//                                    isVisible = textLayoutResult.hasVisualOverflow || isExpanded
//                                }
//                            )
//                        )
//                    }
//                }
//
//                Column(
//                    modifier = Modifier.height(IntrinsicSize.Min),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    var expanded by remember { mutableStateOf(false) }
//                    val context = LocalContext.current
//
//                    IconButton(
//                        onClick = { expanded = true },
//                        enabled = true,
//                    ) {
//                        Icon(
//                            ImageVector.vectorResource(id = R.drawable.more_button),
//                            contentDescription = "More menu"
//                        )
//                    }
//                    MyDropDownMenus(
//                        expanded = expanded,
//                        onDismissRequest = { expanded = false },
//                        onEdit = remember(item) {
//                            {
//                                val bottomSheetFragment = BottomSheetFragment(
//                                    viewModelEvent = onEvent, skill = item
//                                )
//                                bottomSheetFragment.show(
//                                    (context as FragmentActivity).supportFragmentManager,
//                                    "BSDialogFragment"
//                                )
//                            }
//                        },
//                        item = item,
//                        onEvent = onEvent,
//                    )
//
//                    Checkbox(
//                        modifier = modifier, onCheckedChange = {
//                            onEvent.invoke(
//                                SkillsViewModel.Event.EditItem(
//                                    item, SkillsChangeConst.ChooseOrNotSkill
//                                )
//                            )
//                        }, checked = item.chosen
//                    )
//                }
//            }
//        }
//        Row(
//            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom
//        ) {
//            BasicText(
//                modifier = Modifier
//                    .padding(start = 8.dp)
//                    .weight(0.4f), text = SimpleDateFormat(
//                    "dd MMMM yyyy: HH:mm", Locale.US
//                ).format(item.skillEntity.date), style = TextStyle(
//                    fontStyle = FontStyle.Italic,
//                    fontSize = 11.sp,
//                    textAlign = TextAlign.Left,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            )
//
//            if (isVisible) {
//                BasicText(
//                    modifier = Modifier
//                        .weight(0.5f)
//                        .animateContentSize()
//                        .then(remember(isVisible) {
//                            Modifier.clickable { isExpanded = !isExpanded }
//                        }), style = TextStyle(
//                        fontSize = 10.sp,
//                        fontStyle = FontStyle.Italic,
//                        color = if (!isVisible) MaterialTheme.colorScheme.onSurfaceVariant.copy(
//                            alpha = 0f
//
//                        ) else MaterialTheme.colorScheme.onSurfaceVariant.copy(
//                            alpha = 0.5f
//                        ),
//                        textAlign = TextAlign.Left
//                    ),
//
//                    text = if (!isExpanded) {
//                        stringResource(R.string.details) + " >>"
//                    } else {
//                        stringResource(R.string.hide) + " <<"
//                    }
//                )
//            }
//        }
//    }
//}
//@Suppress("detekt.FunctionNaming", "detekt.LongMethod", "detekt.LongParameterList")
//@Composable
//@Preview
//fun ListItemSkillPreview() {
//    ItemListSkill(
//        item = Skill(
//            skillEntity = SkillEntity(),
//            chosen = false
//        ),
//        onEvent = {},
//    )
//    println("true".toBooleanStrictOrNull())
//}