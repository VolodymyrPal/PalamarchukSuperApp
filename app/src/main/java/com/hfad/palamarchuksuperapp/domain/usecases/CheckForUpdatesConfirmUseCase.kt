package com.hfad.palamarchuksuperapp.domain.usecases

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import com.hfad.palamarchuksuperapp.core.ui.composables.elements.StrongAlertDialog

/**
 * Use case for checking for updates if it's available.
 *
 */
class CheckForUpdatesConfirmUseCase(private val context: Context) {

    private val appUpdateManager = AppUpdateManagerFactory.create(context)
    private val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    fun checkForUpdatesConfirm(): Boolean {
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                StrongAlertDialog(context).show("Update available", "Please update to continue") //TODO
//                appUpdateManager.startUpdateFlowForResult(
//                    appUpdateInfo, activityResultLaucher, AppUpdateOptions.newBuilder(
//                        AppUpdateType.IMMEDIATE
//                    ).build()
//                )
            }
        }
        return true
    }
}