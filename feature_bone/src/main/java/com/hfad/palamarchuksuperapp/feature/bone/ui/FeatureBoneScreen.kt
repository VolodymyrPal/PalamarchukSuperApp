package com.hfad.palamarchuksuperapp.feature.bone.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneDeps
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneDepsProvider
import com.hfad.palamarchuksuperapp.feature.bone.di.DaggerBoneComponent

@Composable
fun FeatureBoneScreen() {
    val component = remember {
        DaggerBoneComponent.builder().deps(BoneDepsProvider.deps).build()
    }


    Surface(
        modifier = Modifier.fillMaxSize(), content =
            {
                AppText(
                    modifier = Modifier,
                    value = "viewModel.hashCode()",
                    appTextConfig = appTextConfig(
                        textStyle = TextStyle.Default.copy(
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                        ),
                    )
                )
            }
    )
}

internal val LocalBoneDependencies = compositionLocalOf<BoneDeps> {
    error("BoneDependencies not provided!")
}