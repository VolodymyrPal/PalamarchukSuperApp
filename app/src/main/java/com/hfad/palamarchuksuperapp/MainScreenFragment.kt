package com.hfad.palamarchuksuperapp

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.FileObserver
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.MediaStore
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.findFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hfad.palamarchuksuperapp.databinding.MainScreenFragmentBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File


class MainScreenFragment : Fragment() {

    private var _binding: MainScreenFragmentBinding? = null
    private val binding get() = _binding!!
    private val preferencesRepository = PreferencesRepository.get()
    private lateinit var filesDir: File
    private lateinit var mainPhoto: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("MainScreenFragment", "STARTED")
        _binding = MainScreenFragmentBinding.inflate(inflater, container, false)
        val view = binding.root


        val vibe: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                requireActivity().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            requireActivity().getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        }

        fun onClickVibro() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibe.vibrate(VibrationEffect.createOneShot(2, 60))
            } else {
                vibe.vibrate(1)
            }
        }

        filesDir = File(binding.root.context.filesDir, "app_images")
        if (!filesDir.exists()) {
            filesDir.mkdirs()
        }
        mainPhoto = File(filesDir, "MainImage.jpg")

        updatePhoto()

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


        binding.skillButton1.setOnClickListener {
            findNavController().navigate(MainScreenFragmentDirections.toSkillsFragment()    )
            onClickVibro()
        }
        binding.skillButton2.setOnClickListener {
            onClickVibro()
        }
        binding.skillButton3.setOnClickListener {
            onClickVibro()
        }
        binding.skillButton4.setOnClickListener {
            onClickVibro()
        }


        return view
    }


    fun updatePhoto (tempImage: File? = null) {
        Log.d("MainScreenFragment Photo", "was called")
        if (tempImage == null) {
            if (mainPhoto.exists()) {
                val mainPhotoUri = mainPhoto.toUri()
                binding.imageView.setImageURI(mainPhotoUri)
            } else {
                binding.imageView.setImageResource(R.drawable.lion_jpg_21)
            }
        } else {
            binding.imageView.setImageResource(R.drawable.lion_jpg_21)
            mainPhoto.delete()
            tempImage.renameTo(mainPhoto)
            binding.imageView.setImageURI(mainPhoto.toUri())
        }
    }

    override fun onResume() {
        super.onResume()
        if (mainPhoto.exists()) {
            val mainPhotoUri = mainPhoto.toUri()
            binding.imageView.setImageURI(mainPhotoUri)
        } else {
            binding.imageView.setImageResource(R.drawable.lion_jpg_21)
        }
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
