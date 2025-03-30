package com.hfad.palamarchuksuperapp.feature.bone.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.daggerViewModel
import com.hfad.palamarchuksuperapp.feature.bone.di.BoneDepsProvider
import com.hfad.palamarchuksuperapp.feature.bone.di.DaggerBoneComponent
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.BoneFeatureViewModel

@Composable
fun FeatureBoneScreen() {
    val component = rememberSaveable {
        DaggerBoneComponent.builder().deps(BoneDepsProvider.deps).build()
    }

    BoneScreen(
        viewModel = daggerViewModel<BoneFeatureViewModel>(
            factory = component.viewModelFactory
        ),
    )

    Surface(
        modifier = Modifier.fillMaxSize(), content =
            {

                AppText(
                    modifier = Modifier,
                    value = "123",
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

@Composable
fun BoneScreen(
    viewModel: BoneFeatureViewModel,
) {
    FeatureBoneScreen()
}