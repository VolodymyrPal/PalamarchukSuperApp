package com.hfad.palamarchuksuperapp.ui.compose

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
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
import com.hfad.palamarchuksuperapp.data.services.ChatCompletionResponse
import com.hfad.palamarchuksuperapp.data.services.GroqApi
import com.hfad.palamarchuksuperapp.data.services.GroqRequest
import com.hfad.palamarchuksuperapp.data.services.Message
import com.hfad.palamarchuksuperapp.ui.compose.utils.BottomNavBar
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Suppress("detekt.FunctionNaming", "detekt.LongMethod")
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    navController: (Routes) -> Unit?,
    //viewModel: SkillsViewModel = daggerViewModel<SkillsViewModel>(factory = LocalContext.current.appComponent.viewModelFactory()
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(navigate = navController)
        },
        floatingActionButton = {
            val context = LocalContext.current
            FloatingActionButton(
                shape = RoundedCornerShape(33),
                modifier = Modifier,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {},
                content = {
                }
            )
        }
    ) { paddingValues ->
        Surface(
            color = Color.Transparent, modifier = modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            var promptText: String by remember { mutableStateOf("") }
            var responseText: String by remember { mutableStateOf("") }

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
                            val moshi = Moshi.Builder()
                                .add(KotlinJsonAdapterFactory())
                                .build()


                            val retrofit = Retrofit.Builder()
                                .baseUrl("https://api.groq.com")
                                .addConverterFactory(
                                    MoshiConverterFactory.create(moshi).asLenient()
                                )
                                .build()

                            val groqApi = retrofit.create(GroqApi::class.java)

                            val message =
                                Message(role = "user", content = promptText)
                            val request = GroqRequest(
                                messages = listOf(message),
                                model = "llama3-8b-8192"
                            )

                            val call = groqApi.getChatCompletion(
                                apiKey = "Bearer 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",  // Ваш API ключ
                                body = request
                            )

                            call.enqueue(object : Callback<ChatCompletionResponse> {

                                override fun onResponse(
                                    p0: Call<ChatCompletionResponse>,
                                    p1: Response<ChatCompletionResponse>,
                                ) {
                                    if (p1.isSuccessful) {
                                        responseText = p1.body()!!.choices[0].message.content
                                    } else {
                                        Log.d("TAG", "onResponseNotSuccessful: ${p1.code()}")
                                    }
                                }

                                override fun onFailure(
                                    p0: Call<ChatCompletionResponse>,
                                    p1: Throwable,
                                ) {
                                    Log.d("Tag", "OnFailure: $p1")
                                }
                            })
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