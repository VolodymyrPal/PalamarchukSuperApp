package com.hfad.palamarchuksuperapp.domain.usecases

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class CheckForUpdatesConfirmUseCase (private val context: Context) {

    val appUpdateManager = AppUpdateManagerFactory.create(context)
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    fun checkForUpdatesConfirm(): Boolean {
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateManager.startUpdateFlowForResult (appUpdateInfo, activityResultLaucher, AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
            }
        }
        return true
    }

    fun startUpdating() {
    }
}