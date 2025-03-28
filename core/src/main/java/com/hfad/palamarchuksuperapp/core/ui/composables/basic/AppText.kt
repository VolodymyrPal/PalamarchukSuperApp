package com.hfad.palamarchuksuperapp.core.ui.composables.basic

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.ui.compose.theme.AppTheme

/**
 * Базовый компонент для отображения текста с поддержкой конфигурации.
 *
 * @param value Идентификатор строкового ресурса.
 * @param modifier Модификатор для настройки внешнего вида.
 * @param color Цвет текста.
 * @param appTextConfig Конфигурация текста.
 */
@Composable
@Suppress("LongParameterList", "FunctionNaming")
fun AppText(
    @StringRes value: Int,
    modifier: Modifier = Modifier,
    color: Color = defaultAppTextColor(),
    appTextConfig: AppTextConfig = appTextConfig(),
) {
    val mergedStyle: TextStyle = appTextConfig.textStyle.copy(color = color)

    Text(
        text = stringResource(value),
        modifier = modifier,
        color = Color.Unspecified,
        fontSize = appTextConfig.fontSize,
        fontStyle = appTextConfig.fontStyle,
        fontWeight = appTextConfig.fontWeight,
        fontFamily = appTextConfig.fontFamily,
        letterSpacing = appTextConfig.letterSpacing,
        textDecoration = appTextConfig.textDecoration,
        textAlign = appTextConfig.textAlign,
        lineHeight = appTextConfig.lineHeight,
        overflow = appTextConfig.overflow,
        softWrap = appTextConfig.softWrap,
        maxLines = appTextConfig.maxLines,
        minLines = appTextConfig.minLines,
        onTextLayout = appTextConfig.onTextLayout,
        style = mergedStyle,
    )
}
@Composable
fun defaultAppTextColor() = MaterialTheme.colorScheme.onPrimaryContainer

@Composable
@Suppress("LongParameterList", "FunctionNaming")
fun AppText(
    value: String,
    modifier: Modifier = Modifier,
    color: Color = defaultAppTextColor(),
    appTextConfig: AppTextConfig = appTextConfig(),
) {
    val mergedStyle = if (appTextConfig.textStyle.brush != null) {
        appTextConfig.textStyle
    } else {
        appTextConfig.textStyle.copy(color = color)
    }

    Text(
        text = value,
        modifier = modifier,
        style = mergedStyle,
        overflow = appTextConfig.overflow,
        softWrap = appTextConfig.softWrap,
        maxLines = appTextConfig.maxLines,
        minLines = appTextConfig.minLines,
        onTextLayout = appTextConfig.onTextLayout,
        fontSize = appTextConfig.fontSize,
        fontStyle = appTextConfig.fontStyle,
        fontWeight = appTextConfig.fontWeight,
        fontFamily = appTextConfig.fontFamily,
        letterSpacing = appTextConfig.letterSpacing,
        textDecoration = appTextConfig.textDecoration,
        textAlign = appTextConfig.textAlign,
        lineHeight = appTextConfig.lineHeight,
    )
}

/**
 * Preview для компонента [AppText].
 * Отображает компонент в светлой и темной темах.
 */
@Composable
@Preview(showBackground = false)
private fun AppTextPreview() {
    Column {
        val text = remember { mutableStateOf("Text Text Text Text Text Text") }
        AppTheme(useDarkTheme = true) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp)
            ) {
                AppText(
                    modifier = Modifier.padding(4.dp), value = "Some text to check preview",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                )
            }
        }
        AppTheme(useDarkTheme = false) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp)
            ) {
                AppText(
                    modifier = Modifier.padding(4.dp), value = "Some text to check preview",
                    appTextConfig = appTextConfig(
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                )
            }
        }
    }
}