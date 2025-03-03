package com.hfad.palamarchuksuperapp.ui.screens

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.hfad.palamarchuksuperapp.DataStoreHandler
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.databinding.FragmentMainScreenBinding
import com.hfad.palamarchuksuperapp.domain.models.AppImages
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.domain.usecases.ActivityKey
import com.hfad.palamarchuksuperapp.domain.usecases.SwitchToActivityUseCase
import com.hfad.palamarchuksuperapp.ui.compose.ComposeMainActivity
import com.hfad.palamarchuksuperapp.ui.compose.Routes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainScreenFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var dataStoreHandler: DataStoreHandler

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
            binding.dayNightButton.isChecked = dataStoreHandler.storedQuery.first()
        }


        binding.dayNightButton.setOnCheckedChangeListener { _, isChecked ->
            vibe.standardClickVibration()
            lifecycleScope.launch {
                dataStoreHandler.setStoredNightMode(isChecked)
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
                scale.doOnEnd {
                    val intent = Intent(context, ComposeMainActivity::class.java).apply {
                        putExtra("startDestination", Routes.ChatBotScreen::class.qualifiedName)
                    }
                    startActivity(intent)
                }

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
