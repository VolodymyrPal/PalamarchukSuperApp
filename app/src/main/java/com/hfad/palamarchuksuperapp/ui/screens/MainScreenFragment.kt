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
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.entities.Role
import com.hfad.palamarchuksuperapp.data.repository.PreferencesRepository
import com.hfad.palamarchuksuperapp.data.services.GeminiBuilder
import com.hfad.palamarchuksuperapp.data.services.toGeminiRequest
import com.hfad.palamarchuksuperapp.databinding.FragmentMainScreenBinding
import com.hfad.palamarchuksuperapp.domain.models.AppImages
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.domain.usecases.ActivityKey
import com.hfad.palamarchuksuperapp.domain.usecases.SwitchToActivityUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
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

        CoroutineScope(Dispatchers.IO).launch {
            var photoMi: Bitmap? = null
            try {
                val url = URL("https://e7.pngegg.com/pngimages/624/18/png-clipart-cute-little-star-little-stars-star-thumbnail.png")
                photoMi = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: IOException) {
                println(e)
            }
            val imgByteCode = ByteArrayOutputStream().let {
                photoMi?.compress(Bitmap.CompressFormat.JPEG, 80, it)
                Base64.encodeToString(it.toByteArray(), Base64.NO_WRAP)
            }

            val request = GeminiBuilder.RequestBuilder()
                .image("iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAApgAAAKYB3X3/OAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAANCSURBVEiJtZZPbBtFFMZ/M7ubXdtdb1xSFyeilBapySVU8h8OoFaooFSqiihIVIpQBKci6KEg9Q6H9kovIHoCIVQJJCKE1ENFjnAgcaSGC6rEnxBwA04Tx43t2FnvDAfjkNibxgHxnWb2e/u992bee7tCa00YFsffekFY+nUzFtjW0LrvjRXrCDIAaPLlW0nHL0SsZtVoaF98mLrx3pdhOqLtYPHChahZcYYO7KvPFxvRl5XPp1sN3adWiD1ZAqD6XYK1b/dvE5IWryTt2udLFedwc1+9kLp+vbbpoDh+6TklxBeAi9TL0taeWpdmZzQDry0AcO+jQ12RyohqqoYoo8RDwJrU+qXkjWtfi8Xxt58BdQuwQs9qC/afLwCw8tnQbqYAPsgxE1S6F3EAIXux2oQFKm0ihMsOF71dHYx+f3NND68ghCu1YIoePPQN1pGRABkJ6Bus96CutRZMydTl+TvuiRW1m3n0eDl0vRPcEysqdXn+jsQPsrHMquGeXEaY4Yk4wxWcY5V/9scqOMOVUFthatyTy8QyqwZ+kDURKoMWxNKr2EeqVKcTNOajqKoBgOE28U4tdQl5p5bwCw7BWquaZSzAPlwjlithJtp3pTImSqQRrb2Z8PHGigD4RZuNX6JYj6wj7O4TFLbCO/Mn/m8R+h6rYSUb3ekokRY6f/YukArN979jcW+V/S8g0eT/N3VN3kTqWbQ428m9/8k0P/1aIhF36PccEl6EhOcAUCrXKZXXWS3XKd2vc/TRBG9O5ELC17MmWubD2nKhUKZa26Ba2+D3P+4/MNCFwg59oWVeYhkzgN/JDR8deKBoD7Y+ljEjGZ0sosXVTvbc6RHirr2reNy1OXd6pJsQ+gqjk8VWFYmHrwBzW/n+uMPFiRwHB2I7ih8ciHFxIkd/3Omk5tCDV1t+2nNu5sxxpDFNx+huNhVT3/zMDz8usXC3ddaHBj1GHj/As08fwTS7Kt1HBTmyN29vdwAw+/wbwLVOJ3uAD1wi/dUH7Qei66PfyuRj4Ik9is+hglfbkbfR3cnZm7chlUWLdwmprtCohX4HUtlOcQjLYCu+fzGJH2QRKvP3UNz8bWk1qMxjGTOMThZ3kvgLI5AzFfo379UAAAAASUVORK5CYII=")
                .text("Что на картинке? Provide full answer in Russian. ")
                .buildSingleRequest()

            val listMessages = listOf<MessageAI>(
                MessageAI(role = Role.USER, content = "Картинка", type = MessageType.IMAGE),
                MessageAI(role = Role.MODEL, content = "Some info"),
                MessageAI(role = Role.USER, content = "Картинка"),
            )
            Log.d("My list:", "${listMessages.toGeminiRequest()}")

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
