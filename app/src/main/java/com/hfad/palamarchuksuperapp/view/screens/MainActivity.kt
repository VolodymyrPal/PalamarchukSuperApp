package com.hfad.palamarchuksuperapp.view.screens

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.data.AppImages
import com.hfad.palamarchuksuperapp.databinding.ActivityMainBinding
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    @Inject lateinit var appImages: AppImages
    @Inject lateinit var vibe: Vibrator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        applicationContext.appComponent.inject(this)



        val badge = binding.bottomNavigation.getOrCreateBadge(R.id.bnav_settings)
        badge.isVisible = true
        badge.number = 10

        @Suppress("DEPRECATION")
        fun onClickVibro() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibe.vibrate(VibrationEffect.createOneShot(2, 60))
            } else {
                vibe.vibrate(1)
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bnav_home -> {
                    onClickVibro()
                    Navigation.findNavController(this, R.id.fragment_container_view)
                        .navigate(R.id.mainScreenFragment)
                    true
                }

                R.id.bnav_camera -> {
                    onClickVibro()
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, appImages.mainImage.tempUriForIntent)
                    getContent.launch(cameraIntent)
                    true
                }

                R.id.bnav_settings -> {
                    onClickVibro()
                    badge.clearNumber()
                    badge.isVisible = false
                    false
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
                runBlocking { appImages.mainImage.updateMainPhoto() }

                /*
                when (val frag = navHost?.childFragmentManager?.primaryNavigationFragment) {
                    is MainScreenFragment -> frag.updatePhoto(tempImage = appImages.mainImage.tempImageFile)
                    else -> {
                        appImages.mainImage.mainPhoto.delete()
                        if (appImages.mainImage.tempImageFile != null) {
                            appImages.mainImage.tempImageFile.renameTo(appImages.mainImage.mainPhoto)
                        } else {
                            Log.e("MainActivity", "Ошибка сжатия изображения")
                        }



                // mainPhoto.delete()
                // tempImageFile.renameTo(mainPhoto)
                // val a = BitmapFactory.decodeFile(mainPhoto.path).compress(Bitmap.CompressFormat.WEBP, 100, out)
            }
        }*/
            }
        }

}