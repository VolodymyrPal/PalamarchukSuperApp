package com.hfad.palamarchuksuperapp.view.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.hfad.palamarchuksuperapp.compose.ComposeMainActivity
import com.hfad.palamarchuksuperapp.PreferencesRepository
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.data.AppImages
import com.hfad.palamarchuksuperapp.databinding.MainScreenFragmentBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainScreenFragment : Fragment() {

    private var _binding: MainScreenFragmentBinding? = null
    private val binding get() = _binding!!


    @Inject lateinit var preferencesRepository: PreferencesRepository
    @Inject lateinit var appImages : AppImages
    @Inject lateinit var vibe: Vibrator

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = MainScreenFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        updatePhoto()

        fun onClickVibro() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibe.vibrate(VibrationEffect.createOneShot(2, 60))
            } else {
                vibe.vibrate(1)
            }
        }

        binding.dayNightButton.setOnClickListener {
            onClickVibro()
            lifecycleScope.launch {
                preferencesRepository.setStoredNightMode(!preferencesRepository.storedQuery.first())
            }
        }

        binding.composeButton.setOnClickListener {
            onClickVibro()
            val a = Intent(view.context, ComposeMainActivity::class.java)
            startActivity(a)
            requireActivity().finish()
        }

        binding.let {
            it.skillButton1.setOnClickListener {
                findNavController().navigate(MainScreenFragmentDirections.toSkillsFragment())
                onClickVibro()
            }
            it.skillButton2.setOnClickListener { onClickVibro() }
            it.skillButton3.setOnClickListener { onClickVibro() }
            it.skillButton4.setOnClickListener { onClickVibro() }
        }

        return view
    }


    fun updatePhoto() {
        binding.imageView.load (appImages.mainImage.mainPhoto) {
            crossfade(true).transformations()
            placeholder(R.drawable.lion_jpg_21) } }

    override fun onResume() {
        super.onResume()
        updatePhoto()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("MainScreenFragment", "DESTROYED")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainScreenFragment", "ON PAUSE")
    }
}
