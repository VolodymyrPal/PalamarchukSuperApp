package com.hfad.palamarchuksuperapp.ui.compose

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.data.services.GroqRequest
import com.hfad.palamarchuksuperapp.data.services.Message
import com.hfad.palamarchuksuperapp.ui.compose.utils.BottomNavBar
import com.hfad.palamarchuksuperapp.ui.viewModels.ChatBotViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


@Suppress("detekt.FunctionNaming", "detekt.LongMethod")
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    chatBotViewModel: ChatBotViewModel = ChatBotViewModel(),
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

            val client = remember {
                HttpClient {
                    install(ContentNegotiation) {
                        json(Json {
                            prettyPrint = true
                            isLenient = true  //TODO lenient for testing
                        })
                    }
                }
            }

            val message = remember { Message(role = "user", content = promptText) }
            val request =
                remember { GroqRequest(messages = listOf(message), model = "llama3-8b-8192") }

            LaunchedEffect(Unit) {
                val groqKey = BuildConfig.GROQ_KEY
                try {
                    val response = client.post(
                        "https://api.openai.com/v1/chat/completions"
                    ) {
                        url()
                        headers {
                            append("Authorization", "Bearer $groqKey")
                            append("Content-Type", "application/json")
                        }
                        setBody(request)
                    }
                    Log.d("TAG", "Response: $response")
                } catch (e: Exception) {
                    Log.d("TAG", "Error: $e")
                }
            }

//            call.enqueue(object : Callback<ChatCompletionResponse> {
//
//                override fun onResponse(
//                    p0: Call<ChatCompletionResponse>,
//                    p1: Response<ChatCompletionResponse>,
//                ) {
//                    if (p1.isSuccessful) {
//                        responseText = p1.body()!!.choices[0].message.content
//                    } else {
//                        Log.d("TAG", "onResponseNotSuccessful: ${p1.code()}")
//                    }
//                }
//
//                override fun onFailure(
//                    p0: Call<ChatCompletionResponse>,
//                    p1: Throwable,
//                ) {
//                    Log.d("Tag", "OnFailure: $p1")
//                }
//            })


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

                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send message to Bot")
                    }
                }
                item {
                    Text(
                        text = responseText,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }
        }
    }
}
