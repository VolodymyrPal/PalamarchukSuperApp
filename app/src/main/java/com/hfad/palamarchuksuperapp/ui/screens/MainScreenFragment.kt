package com.hfad.palamarchuksuperapp.ui.screens

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.hfad.palamarchuksuperapp.data.repository.PreferencesRepository
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.data.services.GptRequested
import com.hfad.palamarchuksuperapp.data.services.ImageMessageRequest
import com.hfad.palamarchuksuperapp.data.services.ImageRequest
import com.hfad.palamarchuksuperapp.data.services.MessageRequest
import com.hfad.palamarchuksuperapp.data.services.RequestRole
import com.hfad.palamarchuksuperapp.databinding.FragmentMainScreenBinding
import com.hfad.palamarchuksuperapp.domain.models.AppImages
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.domain.usecases.ActivityKey
import com.hfad.palamarchuksuperapp.domain.usecases.SwitchToActivityUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject


class MainScreenFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var appImages: AppImages

    @Inject
    lateinit var vibe: AppVibrator

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        updatePhoto()

        lifecycleScope.launch {
            binding.dayNightButton.isChecked = preferencesRepository.storedQuery.first()
        }


        binding.dayNightButton.setOnCheckedChangeListener { _, isChecked ->
            vibe.standardClickVibration()
            lifecycleScope.launch {
                delay(200)
                preferencesRepository.setStoredNightMode(isChecked)
            }
        }

        binding.composeButton.setOnClickListener {
            vibe.standardClickVibration()
            SwitchToActivityUseCase()(
                oldActivity = requireActivity(),
                key = ActivityKey.ActivityCompose
            )
        }

        binding.apply {
            skillButton1.setOnClickListener {
                val scale = ObjectAnimator.ofPropertyValuesHolder(
                    it,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f, 1f)
                )
                scale.setDuration(200)
                scale.start()
                scale.doOnEnd {
                    findNavController().navigate(MainScreenFragmentDirections.toSkillsFragment())
                    vibe.standardClickVibration()
                }
            }
            skillButton2.setOnClickListener {
                val scale = ObjectAnimator.ofPropertyValuesHolder(
                    it,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f, 1f)
                )
                scale.setDuration(200)
                scale.start()
                scale.doOnEnd {
                    findNavController().navigate(MainScreenFragmentDirections.actionMainScreenFragmentToStoreFragment())
                    vibe.standardClickVibration()
                }
            }
            skillButton3.setOnClickListener {
                val scale = ObjectAnimator.ofPropertyValuesHolder(
                    it,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f, 1f)
                )
                scale.setDuration(200)
                scale.start()
                vibe.standardClickVibration()
            }
            skillButton4.setOnClickListener {
                val scale = ObjectAnimator.ofPropertyValuesHolder(
                    it,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f, 1f)
                )
                scale.setDuration(200)
                scale.start()
                vibe.standardClickVibration()
            }
        }

        return view
    }


    private fun updatePhoto() {
        binding.imageView.load(appImages.mainImage.mainPhoto) {
            placeholder(R.drawable.lion_jpg_21)
            this.error(R.drawable.lion_jpg_21)
        }
    }

    override fun onResume() {
        super.onResume()
        updatePhoto()

        runBlocking {
            val client = HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        classDiscriminator = "typeKtor"
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                        prettyPrint = true
                        isLenient = true  //TODO lenient for testing
                    })
                }
            }

            //https://i.pinimg.com/236x/f0/04/f5/f004f5fb1e9aa1dcf83b383fab6fde5f.jpg

            val messageRequest = MessageRequest(
                typeText = "text",
                text = "What is on the picture?"
            )
            val imageMessageRequest = ImageMessageRequest(
                typeImage = "image_url",
                image_url = ImageRequest("https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg")
            )

            val roleRequest = RequestRole(
                role = "user",
                content = listOf(messageRequest, imageMessageRequest)
            )

            val gptRequest = GptRequested(
                messages = listOf(roleRequest)
            )

            val jsonRequest = Json.encodeToString(gptRequest)
            Log.d("My Json ", jsonRequest)

//            try {
//                val response = client.post("https://api.openai.com/v1/chat/completions") {
//                    contentType(ContentType.Application.Json)
//                    header("Authorization", "Bearer $OPEN_AI_KEY_USER")
//                    setBody(gptRequest)
//                }
//
//                Log.d("TAG", "Response: ${response.body<String>()}")
//            } catch (e: Exception) {
//                Log.d("TAG", "Error: $e")
//            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
