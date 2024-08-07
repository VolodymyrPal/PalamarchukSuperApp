@file:Suppress("ConvertObjectToDataObject", "ConvertObjectToDataObject",
    "ConvertObjectToDataObject", "ConvertObjectToDataObject", "ConvertObjectToDataObject",
    "ConvertObjectToDataObject", "ConvertObjectToDataObject", "ConvertObjectToDataObject"
)

package com.hfad.palamarchuksuperapp.domain.usecases

import android.app.Activity
import android.content.Intent
import com.hfad.palamarchuksuperapp.ui.compose.ComposeMainActivity
import com.hfad.palamarchuksuperapp.ui.screens.MainActivity

class SwitchToActivityUseCase {
    operator fun invoke(oldActivity: Activity, key: ActivityKey) {
        when (key) {
            ActivityKey.ActivityXML -> {
                val newActivity = Intent(oldActivity, MainActivity::class.java)
                oldActivity.startActivity(newActivity)
                oldActivity.finish()
            }

            ActivityKey.ActivityCompose -> {
                val moveToCompose = Intent(oldActivity, ComposeMainActivity::class.java)
                oldActivity.startActivity(moveToCompose)
                oldActivity.finish()
            }

            else -> throw IllegalArgumentException("Unknown activity key: $key")
        }
    }
}
@Suppress("ConvertObjectToDataObject", "ConvertObjectToDataObject", "ConvertObjectToDataObject",
    "ConvertObjectToDataObject", "ConvertObjectToDataObject", "ConvertObjectToDataObject",
    "ConvertObjectToDataObject", "ConvertObjectToDataObject"
)
sealed interface ActivityKey {
    object ActivityXML : ActivityKey
    object ActivityCompose : ActivityKey
}