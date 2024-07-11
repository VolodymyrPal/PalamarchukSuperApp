package com.hfad.palamarchuksuperapp.presentation.screens

import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.data.services.PlatziApi
import com.hfad.palamarchuksuperapp.domain.models.AppImages
import com.hfad.palamarchuksuperapp.databinding.ActivityMainBinding
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var mainImage: AppImages.MainImage

    @Inject
    lateinit var vibe: AppVibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        applicationContext.appComponent.inject(this)

        val badge = binding.bottomNavigation.getOrCreateBadge(R.id.bnav_settings)
        badge.isVisible = true
        badge.number = 10

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bnav_home -> {
                    vibe.standardClickVibration()
                    Navigation.findNavController(this, R.id.fragment_container_view)
                        .navigate(R.id.mainScreenFragment)
                    true
                }

                R.id.bnav_camera -> {
                    vibe.standardClickVibration()
                    getContent.launch(mainImage.getIntentToUpdatePhoto())
                    true
                }

                R.id.bnav_settings -> {
                    vibe.standardClickVibration()
                    badge.clearNumber()
                    badge.isVisible = false
                    false
                }

                else -> {
                    vibe.standardClickVibration()
                    false
                }
            }
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                runBlocking { mainImage.updateMainPhoto() }
            }
        }
}