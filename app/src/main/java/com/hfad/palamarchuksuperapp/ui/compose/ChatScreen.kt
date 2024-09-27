package com.hfad.palamarchuksuperapp.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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


@Suppress("detekt.FunctionNaming", "detekt.LongMethod")
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    chatBotViewModel: ChatBotViewModel = daggerViewModel<ChatBotViewModel>
        (LocalContext.current.appComponent.viewModelFactory()),
) {

    val navController = LocalNavController.current

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
            var responseText: String by remember { mutableStateOf("") }

            val myState by chatBotViewModel.message_flow.collectAsStateWithLifecycle()

//            LaunchedEffect(Unit) {
//                val groqKey = BuildConfig.GROQ_KEY
//                try {
//                    val response = client.post(
//                        "https://api.openai.com/v1/chat/completions"
//                    ) {
//                        url()
//                        headers {
//                            append("Authorization", "Bearer $groqKey")
//                            append("Content-Type", "application/json")
//                        }
//                        setBody(request)
//                    }
//                    Log.d("TAG", "Response: $response")
//                } catch (e: Exception) {
//                    Log.d("TAG", "Error: $e")
//                }
//            }

            LazyColumn(
                modifier = Modifier,
                verticalArrangement = Arrangement.Top
            ) {
                item {
                    TextField(
                        value = promptText,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { text: String -> promptText = text })
                }
                item {
                    Button(
                        onClick = {
                            chatBotViewModel.event(ChatBotViewModel.Event.SentText(promptText))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send message to Bot")
                    }
                }
                item {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1000.dp)
                    ) {
                        items(myState.size) {
                            if (myState[it] is MessageText) {
                                Text(text = (myState[it] as MessageText).content)
                            }
                            if (myState[it] is MessageChat) {
                                Text(text = ((myState[it] as MessageChat).content.first() as ContentText).text)
                            }
                        }
                    }
                    Text(
                        text = responseText,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }
        }
    }
}
