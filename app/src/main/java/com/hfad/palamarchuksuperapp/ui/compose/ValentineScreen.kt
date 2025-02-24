package com.hfad.palamarchuksuperapp.ui.compose

import android.media.MediaPlayer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.compose.AppTheme
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.ui.reusable.elements.AppText
import com.hfad.palamarchuksuperapp.ui.reusable.elements.appTextConfig
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ValentineScreenRoot() {
    ValentineScreen()
}

@Composable
private fun ValentineScreen() {

    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val mediaPlayer: MediaPlayer? =
        if (!isPreview) remember { MediaPlayer.create(context, R.raw.song) } else null



    DisposableEffect(Unit) {
        mediaPlayer?.start()
        onDispose {
            mediaPlayer?.release()
        }
    }

    FailingHearts()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
    ) {
        val widhtScreen =
            with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
        val heightScreen =
            with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }

        val text = "❤\uFE0F\n Dear, love you! \n Happy Valentine's Day! "
        val brush = remember {
            mutableStateOf(
                Brush.horizontalGradient(
                    listOf<Color>(
                        Color(0xFFFF0000),
                        Color(0xFFFF0077),
                        Color(0xFFFFC0CB),
                        Color(0xFF8B0000),
                        Color(0xFFFF2400),
                        Color(0xFFDC143C),
                        Color(0xFFFF7F50),
                        Color(0xFFFFB6C1),
                        Color(0xFF800020),
                        Color(0xFFFFD700),
                        Color(0xFFFF4500),
                        Color(0xFFFF6F61),
                    ),
                    tileMode = TileMode.Mirror
                )
            )
        }
        LazyColumn(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                AppText(
                    modifier = Modifier,
                    value = text,
                    appTextConfig = appTextConfig(
                        textStyle = TextStyle.Default.copy(
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            brush = brush.value
                        ),
                    )
                )
            }
            item {
                Spacer(modifier = Modifier.size(60.dp))
            }
            item {
                Spacer(modifier = Modifier.size(60.dp))
            }
            item {
                Spacer(modifier = Modifier.size(60.dp))
            }
        }
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(-1f),
            model = R.drawable.heartbackground,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun FailingHearts() {

    val widhtScreen =
        with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val heightScreen =
        with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }


    val heartsList = remember {
        mutableStateOf<List<ValentitesHear>>(
            generateHearts(
                100,
                widhtScreen,
            )
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            heartsList.value = heartsList.value.map { heart ->

                if (heart.y > heightScreen + heart.size) {
                    heart.copy(
                        y = (-heart.size) - Random.nextFloat() * 100f,
                        x = Random.nextFloat() * widhtScreen, // Перемещаем сердечко в случайное место по горизонтали
                        //size = Random.nextFloat() * 40f + 20f, // Меняем размер сердечка
                        speed = Random.nextFloat() * 2f + 1f, // Меняем скорость падения
                        rotation = Random.nextFloat() * 360 // Меняем начальный угол поворота
                    )
                } else {
                    heart.copy(
                        y = heart.y + heart.speed,
                        x = heart.x + (Random.nextFloat() - 0.5f) * 1f, // Добавляем небольшое смещение по горизонтали
//                        rotation = heart.rotation + 2,
                        //size = heart.size + (Random.nextFloat() - 0.5f) * 0.1f // Меняем размер сердечка
                    )
                }
            }
            delay(16L)
        }
    }

    val heartPainter = painterResource(R.drawable.heart)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        heartsList.value.forEach { heart ->
            val scaleFactor = heart.size / 1280f  // Пропорциональный масштаб
            val centerOffset = (1280f * scaleFactor) / 2  // Смещение к центру после масштабирования

            withTransform({
                translate(heart.x - centerOffset, heart.y - centerOffset)
                scale(scaleFactor, pivot = Offset(centerOffset, centerOffset))
            }) {
                with(heartPainter) {
                    draw(
                        Size(1280f, 1280f),
                        colorFilter = ColorFilter.tint(heart.color)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ValentiteScreenPreview() {
    AppTheme {
        ValentineScreen()
    }
}

data class ValentitesHear(
    val x: Float,
    val y: Float,
    val size: Float,
    val rotation: Float,
    val speed: Float,
    val color: Color,
)

fun generateHearts(count: Int, widthScreen: Float): List<ValentitesHear> {

    val random = Random.Default
    val spaceBetween = widthScreen / count

    return List(count) {
        val size = random.nextFloat() * (160f - 20f) + 20f

        ValentitesHear(
            x = it * spaceBetween + random.nextFloat() * (spaceBetween / 2),
            y = (-size) - random.nextFloat() * 600f,
            size = size,
            rotation = random.nextFloat() * 360,
            speed = random.nextFloat() * 2f + 1f,
            color = listOf<Color>(
                Color(0xFFFF0000),
                Color(0xFFFF0077),
                Color(0xFFFFC0CB),
                Color(0xFF8B0000),
                Color(0xFFFF2400),
                Color(0xFFDC143C),
                Color(0xFFFF7F50),
                Color(0xFFFFB6C1),
                Color(0xFFFFE5B4),
                Color(0xFF800020),
                Color(0xFFFFD700),
                Color(0xFFFF4500),
                Color(0xFFFF6F61),
            ).random().copy(alpha = random.nextDouble(0.3, 0.4).toFloat())
        )
    }
}