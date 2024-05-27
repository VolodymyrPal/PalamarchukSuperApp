package com.hfad.palamarchuksuperapp.compose

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.FileObserver
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.R
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hfad.palamarchuksuperapp.compose.utils.BottomNavBar
import com.hfad.palamarchuksuperapp.domain.models.AppImages
import com.hfad.palamarchuksuperapp.domain.models.ScreenRoute
import com.hfad.palamarchuksuperapp.domain.usecases.ActivityKey
import com.hfad.palamarchuksuperapp.domain.usecases.ChangeDayNightModeUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.SwitchToActivityUseCase
import kotlinx.coroutines.runBlocking

@Composable
fun MainScreenConstraint(
    modifier: Modifier = Modifier,
    actionSkillsButton: () -> Unit = {},
    navController: NavHostController = rememberNavController()
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
        val timestap = remember { mutableLongStateOf(System.currentTimeMillis()) }
        val mainPhoto = AppImages.MainImage(context).mainPhoto
        val fileObserver = object : FileObserver(mainPhoto.parentFile?.path) {
            override fun onEvent(event: Int, path: String?) {
                if (event == MOVED_TO) {
                    timestap.longValue = System.currentTimeMillis()
                }
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
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())

        ) {
            ConstraintLayout {
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

                val (topRow, userImage, skills, secondProgram, thirdProgram, fourthProgram) = createRefs()
                Row(
                    modifier = modifier
                        .constrainAs(topRow) {
                            top.linkTo(parent.top)
                        }
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = modifier,
                        colors = buttonColor,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp,
                        ),
                        onClick = {
                            onClickVibro()
                            SwitchToActivityUseCase()(
                                context as Activity,
                                key = ActivityKey.ActivityXML
                            )
                        }
                    ) {
                        Text(text = "xml view", style = MaterialTheme.typography.titleSmall)
                    }

                    FloatingActionButton(
                        modifier = modifier,
                        onClick = {
                            runBlocking { ChangeDayNightModeUseCase()() }
                            onClickVibro()
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
                            modifier = modifier.size(24.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier.constrainAs(userImage) {
                        top.linkTo(parent.top, margin = 72.dp)
                        centerHorizontallyTo(parent)
                    }
                ) {
                    Card(
                        elevation = CardDefaults.elevatedCardElevation(10.dp),
                        modifier = modifier
                            .size(320.dp),
                        shape = CircleShape
                    ) {

                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(mainPhoto)
                                .setParameter(key = "timestamp", value = timestap.longValue, null)
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


                ButtonToNavConstraint(
                    modifier = modifier.constrainAs(skills) {
                        top.linkTo(userImage.bottom, margin = 16.dp)
                        centerHorizontallyTo(userImage)
                    },
                    action = { navController.navigate(ScreenRoute.Skills.route) }
                )
                ButtonToNavConstraint(modifier.constrainAs(secondProgram) {
                    start.linkTo(parent.start, margin = 32.dp)
                    top.linkTo(skills.bottom)
                    bottom.linkTo(fourthProgram.top)
                })
                ButtonToNavConstraint(modifier.constrainAs(thirdProgram) {
                    end.linkTo(parent.end, margin = 32.dp)
                    top.linkTo(skills.bottom)
                    bottom.linkTo(fourthProgram.top)
                })
                ButtonToNavConstraint(modifier.constrainAs(fourthProgram) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                })
            }
        }
    }
}

@Composable
fun ButtonToNavConstraint(modifier: Modifier, action: () -> Unit = {}) {
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
        modifier = modifier.size(100.dp),
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
            Image(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp, top = 5.dp)
                    .weight(0.9f),
                contentScale = ContentScale.FillBounds,
                imageVector = ImageVector.vectorResource(id = R.drawable.skills_image),
                contentDescription = "Skills"
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f),
                textAlign = TextAlign.Center,
                text = "S K I L L S",
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}


@Composable
@Preview(showSystemUi = true, showBackground = true)
fun MainScreenConstraintPreview() {
    MainScreenConstraint()
}