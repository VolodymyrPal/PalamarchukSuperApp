package com.hfad.palamarchuksuperapp.view.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

    private val mainPhoto: File by lazy {
        File(myFilesDir, "MainPhoto")
    }
    private val tempImageFile: File by lazy {
        File.createTempFile("image", "temp", myFilesDir)
    }

//    @Inject
//    lateinit var newClass: NewClass
//    @Inject
//    lateinit var auto: Automobile
//    @Inject
//    lateinit var shitFactory: Shitt.Factory

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
                    false
                }

                else -> {
                    onClickVibro()

                    false
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun compressImageToWebP(image: File): File? {
        return try {
            val bitmap = BitmapFactory.decodeFile(image.path)
            val compressedFile = File(myFilesDir, "tempPhoto.jpg")
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.WEBP, 10, outputStream)
            outputStream.flush()
            outputStream.close()
            compressedFile.writeBytes(outputStream.toByteArray())
            Log.d("My photo compressed weight - ", "${compressedFile.length()}")
            compressedFile
        } catch (e: Exception) {
            Log.e("MainActivity", "Ошибка сжатия изображения", e)
            null
        }
    }


    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d("My photo weight - ", "${tempImageFile.length()}")
                val compressedFile = compressImageToWebP(tempImageFile)
                val navHost =
                    supportFragmentManager.findFragmentById(R.id.fragment_container_view)
                when (val frag = navHost?.childFragmentManager?.primaryNavigationFragment) {
                    is MainScreenFragment -> frag.updatePhoto(compressedFile)
                    else -> {
                        mainPhoto.delete()
                        if (compressedFile != null) {
                            compressedFile.renameTo(mainPhoto)
                        } else {
                            Log.e("MainActivity", "Ошибка сжатия изображения")
                        }

                        // mainPhoto.delete()
                        // tempImageFile.renameTo(mainPhoto)
                        // val a = BitmapFactory.decodeFile(mainPhoto.path).compress(Bitmap.CompressFormat.WEBP, 100, out)
                    }
                }
            }
        }
}