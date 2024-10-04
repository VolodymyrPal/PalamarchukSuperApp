package com.hfad.palamarchuksuperapp.ui.compose

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.data.services.ContentText
import com.hfad.palamarchuksuperapp.data.services.MessageChat
import com.hfad.palamarchuksuperapp.data.services.MessageText
import com.hfad.palamarchuksuperapp.ui.compose.utils.BottomNavBar
import com.hfad.palamarchuksuperapp.ui.viewModels.ChatBotViewModel
import com.hfad.palamarchuksuperapp.ui.viewModels.daggerViewModel
import kotlinx.coroutines.delay


@Suppress("detekt.FunctionNaming", "detekt.LongMethod")
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    chatBotViewModel: ChatBotViewModel = daggerViewModel<ChatBotViewModel>
        (factory = LocalContext.current.appComponent.viewModelFactory()),
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        chatBotViewModel.effect.collect { effect ->
            when (effect) {
                is ChatBotViewModel.Effect.ShowToast -> {
                    Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar()
        },
    ) { paddingValues ->
        Surface(
            color = Color.Transparent, modifier = modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            var promptText: String by remember { mutableStateOf("") }
            val myState by chatBotViewModel.uiState.collectAsStateWithLifecycle()

            LazyColumn(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                items(myState.listMessage.size) {
                    when (myState.listMessage[it]) {
                        is MessageChat -> {
                            Text(
                                text = ((myState.listMessage[it] as MessageChat
                                        ).content.first() as ContentText).text,
                                color = Color.Yellow
                            )
                        }

                        is MessageText -> {
                            Text(
                                text = (myState.listMessage[it] as MessageText).content,
                                color = if ((myState.listMessage[it]
                                            as MessageText).role == "user"
                                ) Color.Green else Color.Blue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(20.dp))
                }
                item {
                    if (myState.error != null) {
                        val showError = remember { mutableStateOf(true) }
                        LaunchedEffect(myState.error) {
                            delay(2000)
                            showError.value = false
                        }
                        if (showError.value) {
                            Text(text = myState.error.toString(), color = Color.Red)
                        }
                    }
                }
                item {
                    Button(
                        onClick = {
                            chatBotViewModel.effect(ChatBotViewModel.Effect.ShowToast("Error"))
                            //chatBotViewModel.event(ChatBotViewModel.Event.SentText(promptText))
                        },
                        modifier = Modifier,
                        enabled = !myState.isLoading
                    ) {
                        Text("Send message to Bot")
                    }
                }
                item {
                    TextField(
                        value = promptText,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { text: String -> promptText = text })
                }
            }
        }
    }
}