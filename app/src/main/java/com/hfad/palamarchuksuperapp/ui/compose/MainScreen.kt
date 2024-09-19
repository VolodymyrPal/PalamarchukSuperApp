package com.hfad.palamarchuksuperapp.ui.compose

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.FileObserver
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.R
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hfad.palamarchuksuperapp.data.repository.PreferencesRepository
import com.hfad.palamarchuksuperapp.ui.compose.utils.BottomNavBar
import com.hfad.palamarchuksuperapp.domain.models.AppImages
import com.hfad.palamarchuksuperapp.domain.usecases.ActivityKey
import com.hfad.palamarchuksuperapp.domain.usecases.SwitchToActivityUseCase
import kotlinx.coroutines.launch

@Composable
fun MainScreenRow(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                navController = navController,
            )
        }
    ) { paddingValues ->
        val buttonColor = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
        val context = LocalContext.current
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
                LocalContext.current.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            LocalContext.current.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        }

        val prefRepository = remember { PreferencesRepository.get() }

        @Suppress("DEPRECATION")
        fun onClickVibro() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibe.vibrate(VibrationEffect.createOneShot(2, 60))
            } else {
                vibe.vibrate(1)
            }
        }

        val scope = rememberCoroutineScope()

        DisposableEffect(LocalLifecycleOwner.current) {
            fileObserver.startWatching()
            onDispose {
                fileObserver.stopWatching()
            }
        }


        Surface(
            color = Color.Transparent, modifier = modifier
                .wrapContentSize()
                .padding(bottom = paddingValues.calculateBottomPadding())

        ) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                item {
                    val dayNightMode by prefRepository.storedQuery.collectAsState(false)
                    Log.d("TAG", "MainScreen: $dayNightMode")
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
                        prefRepository = prefRepository,
                        buttonColor = buttonColor,
                        dayNightMode = dayNightMode
                    )
                }
                item {
                    Card(
                        elevation = CardDefaults.elevatedCardElevation(10.dp),
                        modifier = modifier
                            .size(320.dp),
                        shape = CircleShape
                    ) {

                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(mainPhoto)
                                .setParameter(key = "timestamp", value = timestamp.longValue, null)
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(32.dp, 16.dp, 32.dp, 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier) {
                            ButtonToNavConstraint(
                                modifier = Modifier,
                                action = { navController.navigate(Routes.StoreScreen) },
                                imagePath = R.drawable.store_image,
                                text = "S T O R E"
                            )
                        }
                        Column(
                            modifier = Modifier.fillParentMaxHeight(fraction = 0.39f),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            ButtonToNavConstraint(
                                modifier = Modifier,
                                action = { navController.navigate(Routes.SkillScreen) },
                                text = "S K I L L S"
                            )

                            ButtonToNavConstraint(
                                modifier = Modifier,
                                action = { navController.navigate(Routes.ChatBotScreen) },
                                text = "S K I L L S"
                            )
                        }
                        Column(modifier = Modifier) {
                            ButtonToNavConstraint(
                                modifier = Modifier,
                                action = { navController.navigate(Routes.Settings) },
                                text = "S K I L L S"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonToNavConstraint(
    modifier: Modifier,
    action: () -> Unit = {},
    imagePath: Int = R.drawable.skills_image,
    text: String = "S K I L L S",
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
        modifier = modifier.size(HEIGHT_BIG_BUTTON.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    )
    {
        Column(
            Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imagePath).build(),
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp, top = 5.dp)
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
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun TopRowMainScreen(
    modifier: Modifier = Modifier,
    actionForView: () -> Unit = {},
    actionForNight: () -> Unit = {},
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
                actionForNight()
            }
        ) {
            Text(text = "xml view", style = MaterialTheme.typography.titleSmall)
        }

        FloatingActionButton(
            modifier = Modifier.size(56.dp),
            onClick = {
                actionForView()
            },
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(
                defaultElevation = 5.dp,
                pressedElevation = 30.dp
            ),
            interactionSource = remember { MutableInteractionSource() }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.darkmod_icon_outlined),
                "Night mode",
                //modifier = modifier.size(24.dp)
            )
        }
    }
}

const val HEIGHT_BIG_BUTTON = 100

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
    MainScreenRow()
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