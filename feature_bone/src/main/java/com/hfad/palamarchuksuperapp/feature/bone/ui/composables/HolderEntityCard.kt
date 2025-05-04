package com.hfad.palamarchuksuperapp.feature.bone.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ClientEntity

@Composable
fun HolderEntityCard(
    modifier: Modifier = Modifier,
    entity: ClientEntity,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(IntrinsicSize.Max),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {
            AppText(
                modifier = Modifier.align(Alignment.TopStart),
                value = "Name: ${entity.name}",
            )
            AppText(
                modifier = Modifier.align(Alignment.BottomEnd),
                value = "Type: ${entity.type}",
            )
            AppText(
                modifier = Modifier.align(Alignment.Center),
                value = "Manager: ${entity.manager}",
            )
        }
    }
}

@Preview
@Composable
fun HolderEntityCardPreview(
    modifier: Modifier = Modifier,
    entity: ClientEntity = ClientEntity(
        name = "Client #1",
        manager = "VP +3806338875"
    ),
) {
    AppTheme {
        HolderEntityCard(
            modifier = modifier.size(125.dp),
            entity = entity
        )
    }
}