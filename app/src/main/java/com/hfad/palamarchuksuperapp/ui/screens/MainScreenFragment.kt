package com.hfad.palamarchuksuperapp.ui.screens

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.hfad.palamarchuksuperapp.data.repository.PreferencesRepository
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.domain.models.AppImages
import com.hfad.palamarchuksuperapp.databinding.MainScreenFragmentBinding
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.domain.usecases.ActivityKey
import com.hfad.palamarchuksuperapp.domain.usecases.ChangeDayNightModeUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.SwitchToActivityUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainScreenFragment : Fragment() {

    private var _binding: MainScreenFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var preferencesRepository: PreferencesRepository
    @Inject lateinit var appImages: AppImages
    @Inject lateinit var vibe: AppVibrator

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = MainScreenFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        updatePhoto()

        binding.dayNightButton.setOnClickListener {
            vibe.standardClickVibration()
            lifecycleScope.launch {
                ChangeDayNightModeUseCase()()
            }
        }

        binding.composeButton.setOnClickListener {
            vibe.standardClickVibration()
            SwitchToActivityUseCase()(
                oldActivity = requireActivity(),
                key = ActivityKey.ActivityCompose
            )
        }

        binding.let {
            it.skillButton1.setOnClickListener {
                findNavController().navigate(MainScreenFragmentDirections.toSkillsFragment())
                vibe.standardClickVibration()
            }
            it.skillButton2.setOnClickListener {
                findNavController().navigate(MainScreenFragmentDirections.actionMainScreenFragmentToStoreFragment())
                vibe.standardClickVibration() }
            it.skillButton3.setOnClickListener { vibe.standardClickVibration() }
            it.skillButton4.setOnClickListener { vibe.standardClickVibration() }
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
