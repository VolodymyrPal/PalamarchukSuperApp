package com.hfad.palamarchuksuperapp.feature.bone.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneComponent
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneDeps
import com.hfad.palamarchuksuperapp.feature.bone.di.DaggerBoneComponent
class FeatureScope {

}
@Composable
fun FeatureBoneScreen(
    diDependendies: BoneDeps,
) {
    var featureScope = rememberSaveable { FeatureScope() }
    val component = remember {
        DaggerBoneComponent.builder().deps(diDependendies).build()
    }
    CompositionLocalProvider(
        LocalBoneDependencies provides component
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
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
    }
}

internal val LocalBoneDependencies = compositionLocalOf<BoneComponent> {
    error("BoneDependencies not provided!")
}