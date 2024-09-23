package com.hfad.palamarchuksuperapp.ui.reusable

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class StrongAlertDialog(private val context: Context) {
    private val alertBuilder = AlertDialog.Builder(context)

    fun show(title: String = "StrongAlertDialog", message: String = "StrongAlertDialog") { //TODO
        alertBuilder.setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Update") { _: DialogInterface, _: Int ->
                when (context) {
                    is Activity -> context.finish()
                    else -> {}
                }
            }
            .setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
                when (context) {
                    is Activity -> context.finish()
                    else -> {}
                }
            }
            .create()
            .show()
    }
}