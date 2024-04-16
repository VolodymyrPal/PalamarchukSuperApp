package com.hfad.palamarchuksuperapp

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.hfad.palamarchuksuperapp.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tempImageFile: File
    lateinit var myFilesDir: File
    lateinit var mainPhoto: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        Component().mainActivityInjections(this)

        val badge = binding.bottomNavigation.getOrCreateBadge(R.id.bnav_settings)
        badge.isVisible = true
        badge.number = 10

        val vibe: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        }

        fun onClickVibro() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibe.vibrate(VibrationEffect.createOneShot(2, 60))
            } else {
                vibe.vibrate(1)
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.bnav_home -> {
                    onClickVibro()
                    Navigation.findNavController(this, R.id.fragment_container_view)
                        .navigate(R.id.mainScreenFragment)
                    true
                }

                R.id.bnav_camera -> {
                    onClickVibro()
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    tempImageFile = Component().getTempFile(myFilesDir)

                    val tempUri = FileProvider.getUriForFile(
                        view.context, "$packageName.provider",
                        tempImageFile
                    )

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri)

                    getContent.launch(cameraIntent)

                    true
                }

                R.id.bnav_settings -> {
                    onClickVibro()
                    badge.clearNumber()
                    badge.isVisible = false
                    true
                }

                else -> {
                    onClickVibro()

                    false
                }
            }
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val navHost =
                    supportFragmentManager.findFragmentById(R.id.fragment_container_view)
                when (val frag = navHost?.childFragmentManager?.primaryNavigationFragment) {
                    is MainScreenFragment -> frag.updatePhoto(tempImageFile)
                    else -> {
                        mainPhoto.delete()
                        tempImageFile.renameTo(mainPhoto)
                    }
                }
            }
        }

}