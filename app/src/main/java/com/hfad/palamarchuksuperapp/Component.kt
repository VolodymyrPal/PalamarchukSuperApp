package com.hfad.palamarchuksuperapp

import dagger.Component
import java.io.File

class Component {

    private fun getFilesDir(path: File): File {
        val filesDir = File(path, "app_images")
        if (!filesDir.exists()) {
            filesDir.mkdirs()
        }
        return filesDir
    }

    private fun getMainPhoto (filesDir: File): File {
        return File(filesDir, "MainPhoto.jpg")
    }

    fun getTempFile (filesDir: File) : File {
        return File.createTempFile("image", "temp", filesDir)
    }

    fun mainActivityInjections (activity: MainActivity) {
        activity.myFilesDir = getFilesDir(activity.applicationContext.filesDir)
        activity.mainPhoto = getMainPhoto(activity.filesDir)
    }
}