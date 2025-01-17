package com.hfad.palamarchuksuperapp.ui.compose

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.FileObserver
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hfad.palamarchuksuperapp.DataStoreHandler
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.domain.models.AppImages
import com.hfad.palamarchuksuperapp.domain.usecases.ActivityKey
import com.hfad.palamarchuksuperapp.domain.usecases.SwitchToActivityUseCase
import com.hfad.palamarchuksuperapp.ui.compose.utils.BottomNavBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreenRow(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    dataStore: DataStoreHandler? = remember {
        context.appComponent.provideDataStoreHandler()
    },
) {
    val localTransitionScope = LocalSharedTransitionScope.current
        ?: error(IllegalStateException("No SharedElementScope found"))
    val animatedContentScope = LocalNavAnimatedVisibilityScope.current
        ?: error(IllegalStateException("No AnimatedVisibility found"))

    val navController: NavHostController =
        if (LocalInspectionMode.current)
            NavHostController(context) // If preview - create Mock object for NavHostController
        else LocalNavController.current

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavBar()
        }
    ) { paddingValues ->
        val buttonColor = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
        val timestamp = remember { mutableLongStateOf(System.currentTimeMillis()) }
        val mainPhoto = AppImages.MainImage(context).mainPhoto
        val fileObserver = object : FileObserver(mainPhoto.parentFile?.path, ALL_EVENTS) {
            override fun onEvent(event: Int, path: String?) {
                if (event == MOVED_TO) {
                    timestamp.longValue = System.currentTimeMillis()
                }
            }
        }
        val vibe: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        }

        @Suppress("DEPRECATION")
        fun onClickVibro() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibe.vibrate(VibrationEffect.createOneShot(2, 60))
            } else {
                vibe.vibrate(1)
            }
        }

        DisposableEffect(LocalLifecycleOwner.current) {
            fileObserver.startWatching()
            onDispose {
                fileObserver.stopWatching()
            }
        }


        Surface(
            color = Color.Transparent, modifier = modifier
        ) {
            with(localTransitionScope) {//TODO
                LazyColumn(
                    modifier = modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    item {
                        val dayNightMode = dataStore?.storedQuery?.collectAsState(false)

                        TopRowMainScreen(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 16.dp, 16.dp, 0.dp),
                            actionForActivity = {
                                onClickVibro()
                                SwitchToActivityUseCase()(
                                    context as Activity,
                                    key = ActivityKey.ActivityXML
                                )
                            },
                            prefRepository = dataStore,
                            buttonColor = buttonColor,
                            dayNightMode = dayNightMode?.value == true
                        )
                    }
                    item {
                        Card(
                            modifier = modifier
                                .size(320.dp)
                                .border((2.5).dp, Color.Gray.copy(0.3f), CircleShape)
                                .padding((10).dp),
                            shape = CircleShape
                        ) {

                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(mainPhoto)
                                    .setParameter(
                                        key = "timestamp",
                                        value = timestamp.longValue,
                                        null
                                    )
                                    .crossfade(true)
                                    .error(R.drawable.lion_jpg_21)
                                    .build(),
                                contentDescription = "Users photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                            )
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier.defaultMinSize(minHeight = 250.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.1f)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.4f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ButtonToNavConstraint(
                                    modifier = Modifier,
                                    action = { navController.navigate(Routes.SkillScreen) },
                                    text = stringResource(R.string.skills_button_name),
                                    position = Modifier.offset(15.dp, 15.dp),
                                    modifierToTransit = Modifier.sharedBounds(
                                        rememberSharedContentState("skill"),
                                        animatedContentScope
                                    )
                                )

                                ButtonToNavConstraint(
                                    modifier = Modifier,
                                    action = { navController.navigate(Routes.StoreScreen) },
                                    imagePath = R.drawable.store_image,
                                    text = stringResource(R.string.store_button_name),
                                    position = Modifier.offset((-15).dp, 15.dp),
                                    modifierToTransit = Modifier.sharedBounds(
                                        rememberSharedContentState("store"),
                                        animatedContentScope
                                    )
                                )
                            }

                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.2f)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.4f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ButtonToNavConstraint(
                                    modifier = Modifier,
                                    action = { navController.navigate(Routes.ChatBotScreen) },
                                    imagePath = R.drawable.lock_outlined,
                                    text = stringResource(R.string.chat_button_name),
                                    position = Modifier.offset(15.dp, (-15).dp),
                                    enable = true,
                                    modifierToTransit = Modifier.sharedElement(
                                        rememberSharedContentState("chat"),
                                        animatedContentScope
                                    )
                                )
                                ButtonToNavConstraint(
                                    modifier = Modifier.sharedBounds(
                                        rememberSharedContentState("key"),
                                        animatedContentScope
                                    ),
                                    action = { navController.navigate(Routes.Settings) },
                                    imagePath = R.drawable.lock_outlined,
                                    text = stringResource(R.string.locked_button_name),
                                    position = Modifier.offset((-15).dp, (-15).dp),
                                    enable = false
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding() + 10.dp))
                    }
                }
            }
        }
    }
}

@Suppress("LongParameterList", "FunctionNaming")
@Composable
fun ButtonToNavConstraint(
    modifier: Modifier,
    action: () -> Unit = {},
    imagePath: Int = R.drawable.skills_image,
    text: String = stringResource(R.string.locked_button_name),
    position: Modifier = Modifier.offset(15.dp, 15.dp),
    enable: Boolean = true,
    modifierToTransit: Modifier = Modifier,
) {
    val vibe: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            LocalContext.current.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        LocalContext.current.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
    }

    @Suppress("DEPRECATION")
    fun onClickVibro() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibe.vibrate(VibrationEffect.createOneShot(2, 60))
        } else {
            vibe.vibrate(1)
        }
    }

    OutlinedCard(
        onClick = {
            onClickVibro()
            action()
        },
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .size(WIDTH_BIG_BUTTON.dp, HEIGHT_BIG_BUTTON.dp)
            .background(
                MaterialTheme.colorScheme.onSecondaryContainer.copy(if (enable) 0.5f else 0.25f),
                RoundedCornerShape(20.dp)
            )
            .then(position)
            .then(modifierToTransit)
            .alpha(if (enable) 0.95f else 0.75f),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        enabled = enable
    ) {
        Column(
            Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imagePath).build(),
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp)
                    .weight(0.9f),
                contentScale = ContentScale.FillBounds,
                //imageVector = imageVector,
                contentDescription = text
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f),
                textAlign = TextAlign.Center,
                text = text,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun TopRowMainScreen(
    modifier: Modifier = Modifier,
    actionForActivity: () -> Unit = {},
    dayNightMode: Boolean = false,
    prefRepository: DataStoreHandler? = null,
    buttonColor: ButtonColors = ButtonDefaults.buttonColors(),
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Button(
            modifier = Modifier,
            colors = buttonColor,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 5.dp,
            ),
            onClick = {
                actionForActivity()
            }
        ) {
            Text(text = "xml view", style = MaterialTheme.typography.titleSmall)
        }

        val scope = rememberCoroutineScope()

        val isNightMode by rememberSaveable(dayNightMode) { mutableStateOf(dayNightMode) }

        Switch(
            modifier = Modifier.border(0.dp, Color.Transparent),
            checked = isNightMode,
            enabled = true,
            onCheckedChange = {
                scope.launch {
                    prefRepository?.setStoredNightMode(!dayNightMode) //TODO
                }
            },
            thumbContent = {
                Icon(
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                    imageVector = ImageVector.vectorResource(id = R.drawable.darkmod_icon_outlined),
                    contentDescription = "nightMode"
                )
            },
            colors = SwitchDefaults.colors(
                uncheckedBorderColor = Color.Transparent,
                checkedBorderColor = Color.Transparent,
                checkedThumbColor = MaterialTheme.colorScheme.onBackground,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                checkedIconColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.onBackground,
                uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedIconColor = MaterialTheme.colorScheme.primaryContainer,
            )
        )
    }
}

const val HEIGHT_BIG_BUTTON = 95
const val WIDTH_BIG_BUTTON = 110

@Composable
@Preview
fun TopRowMainScreenPreview(
    modifier: Modifier = Modifier,
) {
    TopRowMainScreen(modifier.fillMaxWidth())
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun MainScreenConstraintPreview() {
    MainScreenRow(
        dataStore = null,
    )
}


// Old implementing of MainScreen but with constraint layout
//@Composable
//fun MainScreenConstraint(
//    modifier: Modifier = Modifier,
//    actionSkillsButton: () -> Unit = {},
//    navController: Navigation?,
//) {
//    Scaffold(
//        modifier = modifier.fillMaxSize(),
//        bottomBar = {
//            BottomNavBar(
//                navController = navController,
//            )
//        }
//    ) { paddingValues ->
//        val buttonColor = ButtonDefaults.elevatedButtonColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer,
//            contentColor = MaterialTheme.colorScheme.onBackground
//        )
//        val context = LocalContext.current
//        val timestamp = remember { mutableLongStateOf(System.currentTimeMillis()) }
//        val mainPhoto = AppImages.MainImage(context).mainPhoto
//        val fileObserver = object : FileObserver(mainPhoto.parentFile?.path) {
//            override fun onEvent(event: Int, path: String?) {
//                if (event == MOVED_TO) {
//                    timestamp.longValue = System.currentTimeMillis()
//                }
//            }
//        }
//
//        DisposableEffect(LocalLifecycleOwner.current) {
//            fileObserver.startWatching()
//            onDispose {
//                fileObserver.stopWatching()
//            }
//        }
//
//
//
//        Surface(
//            color = Color.Transparent, modifier = modifier
//                .wrapContentSize()
//                .padding(bottom = paddingValues.calculateBottomPadding())
//
//        ) {
//            ConstraintLayout {
//                val vibe: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    val vibratorManager =
//                        LocalContext.current.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
//                    vibratorManager.defaultVibrator
//                } else {
//                    @Suppress("DEPRECATION")
//                    LocalContext.current.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
//                }
//
//                @Suppress("DEPRECATION")
//                fun onClickVibro() {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        vibe.vibrate(VibrationEffect.createOneShot(2, 60))
//                    } else {
//                        vibe.vibrate(1)
//                    }
//                }
//
//                val (topRow, userImage, skills, secondProgram, thirdProgram, fourthProgram) = createRefs()
//                Row(
//                    modifier = modifier
//                        .constrainAs(topRow) {
//                            top.linkTo(parent.top)
//                        }
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Button(
//                        modifier = modifier,
//                        colors = buttonColor,
//                        elevation = ButtonDefaults.buttonElevation(
//                            defaultElevation = 5.dp,
//                        ),
//                        onClick = {
//                            onClickVibro()
//                            SwitchToActivityUseCase()(
//                                context as Activity,
//                                key = ActivityKey.ActivityXML
//                            )
//                        }
//                    ) {
//                        Text(text = "xml view", style = MaterialTheme.typography.titleSmall)
//                    }
//
//                    FloatingActionButton(
//                        modifier = modifier,
//                        onClick = {
//                            runBlocking { ChangeDayNightModeUseCase()() }
//                            onClickVibro()
//                        },
//                        shape = CircleShape,
//                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(
//                            defaultElevation = 5.dp,
//                            pressedElevation = 30.dp
//                        ),
//                        interactionSource = remember { MutableInteractionSource() }
//                    ) {
//                        Icon(
//                            imageVector = ImageVector.vectorResource(id = R.drawable.darkmod_icon_outlined),
//                            "Night mode",
//                            modifier = modifier.size(24.dp)
//                        )
//                    }
//                }
//                Box(
//                    modifier = Modifier.constrainAs(userImage) {
//                        top.linkTo(parent.top, margin = 72.dp)
//                        centerHorizontallyTo(parent)
//                    }
//                ) {
//                    Card(
//                        elevation = CardDefaults.elevatedCardElevation(10.dp),
//                        modifier = modifier
//                            .size(320.dp),
//                        shape = CircleShape
//                    ) {
//
//                        AsyncImage(
//                            model = ImageRequest.Builder(LocalContext.current)
//                                .data(mainPhoto)
//                                .setParameter(key = "timestamp", value = timestamp.longValue, null)
//                                .crossfade(true)
//                                .error(R.drawable.lion_jpg_21)
//                                .build(),
//                            contentDescription = "Users photo",
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .clip(CircleShape),
//                        )
//                    }
//                }
//
//
//                ButtonToNavConstraint(
//                    modifier = modifier.constrainAs(skills) {
//                        top.linkTo(userImage.bottom, margin = 16.dp)
//                        centerHorizontallyTo(userImage)
//                    },
//                    action = { navController?.navigate(Routes.SkillScreen) },
//                    text = "S K I L L S"
//                )
//                ButtonToNavConstraint(
//                    modifier.constrainAs(secondProgram) {
//                        start.linkTo(parent.start, margin = 32.dp)
//                        top.linkTo(skills.bottom)
//                        bottom.linkTo(fourthProgram.top)
//                    },
//                    action = { navController?.navigate(Routes.StoreScreen) },
//                    imagePath = R.drawable.store_image,
//                    text = "S T O R E"
//                )
//                ButtonToNavConstraint(modifier.constrainAs(thirdProgram) {
//                    end.linkTo(parent.end, margin = 32.dp)
//                    top.linkTo(skills.bottom)
//                    bottom.linkTo(fourthProgram.top)
//                })
//                ButtonToNavConstraint(modifier.constrainAs(fourthProgram) {
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                    bottom.linkTo(parent.bottom, margin = 16.dp)
//                })
//            }
//        }
//    }
//}