package com.hfad.palamarchuksuperapp.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.size
import java.io.File
import javax.inject.Inject


class AppImages @Inject constructor(val context: Context) {

    val mainImage by lazy { MainImage(context) }


    class MainImage @Inject constructor(val context: Context) {

        fun getIntentToUpdatePhoto () : Intent {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            return cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUriForIntent)
        }

        private val myFilesDir: File = File(context.filesDir, "app_images")

        val mainPhoto: File = File(myFilesDir, "MainPhoto")

        private val tempImageFileForIntent: File by lazy {
            File.createTempFile(
                "image",
                "temp",
                myFilesDir
            )
        }

        val tempUriForIntent: Uri by lazy {
            FileProvider.getUriForFile(
                context, "${context.packageName}.provider",
                tempImageFileForIntent
            )
        }

        suspend fun updateMainPhoto() {
            val compressedMainPhoto: File = Compressor.compress(context, tempImageFileForIntent)
            { size(2_097_152) }
            compressedMainPhoto.renameTo(mainPhoto)
        }
    }
}


