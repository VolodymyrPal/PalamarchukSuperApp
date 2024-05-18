package com.hfad.palamarchuksuperapp.compose

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.presentation.screens.MainActivity
import com.hfad.palamarchuksuperapp.R
import java.io.File

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    actionForDayNight: () -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(0.dp),
    activity: AppCompatActivity? = null,
    mainPhotoBitmap: ImageBitmap = BitmapFactory.decodeResource(
        LocalContext.current.resources,
        R.drawable.lion_jpg_21
    ).asImageBitmap()
) {
    val buttonColor = ButtonDefaults.elevatedButtonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onBackground
    )
    val context = LocalContext.current
    val mainPhoto = File(File(context.filesDir, "app_images"), "MainImage.jpg")

    Surface(color = Color.Transparent, modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = modifier,
                    colors = buttonColor,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 5.dp,
                    ),
                    onClick = {
                        val a = Intent(context, MainActivity::class.java)
                        activity!!.startActivity(a)
                        activity.finish()
                    }
                ) {
                    Text(text = "xml view")
                }

                FloatingActionButton(
                    modifier = modifier,
                    onClick = { actionForDayNight() },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Card(
                    elevation = CardDefaults.elevatedCardElevation(10.dp),
                    modifier = modifier
                        .size(320.dp),
                    shape = CircleShape
                ) {
                    Image(
                        bitmap = mainPhotoBitmap,
                        contentDescription = "Users photo",
                        contentScale = ContentScale.Crop,
                        modifier = modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .rotate(if (mainPhoto.exists()) 90f else 0f)
                    )
                }
            }
            Spacer(modifier = modifier.size(16.dp))
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                ButtonToNav(modifier.align(Alignment.TopCenter))
                ButtonToNav(modifier.align(Alignment.TopStart))
                ButtonToNav(modifier.align(Alignment.TopEnd))
                ButtonToNav(modifier.align(Alignment.BottomEnd))
            }
        }
    }
}


@Preview
@Composable
fun ButtonToNav(modifier: Modifier = Modifier) {
    OutlinedCard(
        modifier = Modifier.size(100.dp),
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
                    .fillMaxWidth()
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
fun MainScreenPreview() {
    MainScreen()
}