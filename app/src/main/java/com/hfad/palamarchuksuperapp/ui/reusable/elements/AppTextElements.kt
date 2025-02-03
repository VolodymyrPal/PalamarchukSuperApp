package com.hfad.palamarchuksuperapp.ui.reusable.elements

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Suppress("LongParameterList", "FunctionNaming")

@Composable
fun AppText(
    @StringRes stringRes: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    style: TextStyle = MaterialTheme.typography.headlineSmall,
    appTextConfig: AppTextConfig = rememberAppTextConfig(),
) {
    val mergedStyle = style.merge(appTextConfig.textStyle).copy(color = color)

    Text(
        text = stringResource(stringRes),
        modifier = modifier,
        style = mergedStyle,
        overflow = appTextConfig.overflow,
        softWrap = appTextConfig.softWrap,
        maxLines = appTextConfig.maxLines,
        minLines = appTextConfig.minLines,
        onTextLayout = appTextConfig.onTextLayout

    )
}

@Immutable
data class AppTextConfig(
    val textStyle: TextStyle = TextStyle.Default,
    val overflow: TextOverflow = TextOverflow.Clip,
    val softWrap: Boolean = true,
    val maxLines: Int = Int.MAX_VALUE,
    val minLines: Int = 1,
    val onTextLayout: ((TextLayoutResult) -> Unit)? = null,
)

@Suppress("LongParameterList")
@Composable
fun rememberAppTextConfig(
    textStyle: TextStyle = TextStyle.Default,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
): AppTextConfig {
    return remember {
        AppTextConfig(
            textStyle,
            overflow,
            softWrap,
            maxLines,
            minLines,
            onTextLayout
        )
    }
}