package com.hfad.palamarchuksuperapp.core.ui.composables.basic

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.core.R
import com.hfad.palamarchuksuperapp.core.ui.theme.AppTheme

/**
 * Компонент для редактирования текста с поддержкой метки и подсказки.
 *
 * @param modifier Модификатор для настройки внешнего вида.
 * @param value Текущее значение текста.
 * @param onValueChange Обратный вызов при изменении текста.
 * @param labelRes Идентификатор строкового ресурса для метки.
 * @param label Компонент для отображения метки.
 * @param placeholderRes Идентификатор строкового ресурса для подсказки.
 * @param placeholder Компонент для отображения подсказки.
 * @param textStyle Стиль текста.
 * @param colors Цвета для текстового поля.
 */
@Composable
@Suppress("LongParameterList")
fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    @StringRes labelRes: Int? = null,
    @StringRes placeholderRes: Int? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.appColors(),
    label: @Composable (() -> Unit)? = labelRes?.let {
        {
            AppText(
                it,
                color = colors.focusedTextColor,
                appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.bodySmall,
                )
            )
        }
    },
    placeholder: @Composable (() -> Unit)? = placeholderRes?.let {
        { AppText(it, Modifier.alpha(0.33f)) }
    },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    outlinedTextConfig: AppEditOutlinedTextConfig = appEditOutlinedTextConfig(),
) {
    OutlinedTextField(
        enabled = outlinedTextConfig.enabled,
        value = value,
        onValueChange = { onValueChange(it) },
        label = label,
        colors = colors,
        placeholder = placeholder,
        modifier = modifier,
        textStyle = textStyle,
        readOnly = outlinedTextConfig.readOnly,
        leadingIcon = outlinedTextConfig.leadingIcon,
        trailingIcon = outlinedTextConfig.trailingIcon,
        prefix = outlinedTextConfig.prefix,
        suffix = outlinedTextConfig.suffix,
        supportingText = outlinedTextConfig.supportingText,
        isError = outlinedTextConfig.isError,
        visualTransformation = outlinedTextConfig.visualTransformation,
        keyboardOptions = outlinedTextConfig.keyboardOptions,
        keyboardActions = outlinedTextConfig.keyboardActions,
        singleLine = outlinedTextConfig.singleLine,
        maxLines = outlinedTextConfig.maxLines,
        minLines = outlinedTextConfig.minLines,
        interactionSource = outlinedTextConfig.interactionSource,
        shape = shape,
    )
}

/**
 * Preview для компонента [AppOutlinedTextField].
 * Отображает компонент в светлой и темной темах.
 */
@Composable
@Preview(showBackground = false)
private fun AppOutlinedTextPreview() {
    Column(
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val text = remember { mutableStateOf("Text Text Text Text Text Text") }
        AppTheme(useDarkTheme = true) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp)
            ) {
                AppOutlinedTextField(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.Center),
                    value = text.value,
                    labelRes = R.string.place_holder_text,
                    placeholderRes = R.string.place_holder_text,
                    onValueChange = { text.value = it },
                )
            }
        }
        AppTheme(useDarkTheme = false) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp)
            ) {
                AppOutlinedTextField(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.TopCenter),
                    value = text.value,
                    labelRes = R.string.place_holder_text,
                    placeholderRes = R.string.place_holder_text,
                    onValueChange = { text.value = it },
                )
            }
        }
    }
}