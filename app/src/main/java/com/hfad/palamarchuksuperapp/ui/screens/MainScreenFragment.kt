package com.hfad.palamarchuksuperapp.ui.screens

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.data.repository.PreferencesRepository
import com.hfad.palamarchuksuperapp.data.services.GeminiApiHandler
import com.hfad.palamarchuksuperapp.data.services.GeminiContentBuilder
import com.hfad.palamarchuksuperapp.databinding.FragmentMainScreenBinding
import com.hfad.palamarchuksuperapp.domain.models.AppImages
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.domain.usecases.ActivityKey
import com.hfad.palamarchuksuperapp.domain.usecases.SwitchToActivityUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
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

//        CoroutineScope(Dispatchers.IO).launch {
//            var photoMi : Bitmap? = null
//            try {
//                val url = URL("https://bagrut-ru.com/wp-content/uploads/2023/06/28-1024x508.png")
//                photoMi = BitmapFactory.decodeStream(url.openConnection().getInputStream())
//            } catch (e: IOException) {
//                println(e)
//            }
//            Log.d("Photo: ", "$photoMi")
//            val imgByteCode = ByteArrayOutputStream().let {
//                photoMi?.compress(Bitmap.CompressFormat.JPEG, 80, it)
//                Base64.encodeToString(it.toByteArray(), Base64.NO_WRAP)
//            }
//
//            val request = GeminiContentBuilder.Builder()
//                .image(imgByteCode)
//                .text("It is image with math problem. Provide full answer in Russian. ")
//                .build()
//
//            val response =
//                GeminiApiHandler(context?.applicationContext?.appComponent?.getHttpClient()!!).sendRequestWithResponse(
//                    geminiRequest = request
//                )
//            Log.d("TAG", "onResume: $response")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
