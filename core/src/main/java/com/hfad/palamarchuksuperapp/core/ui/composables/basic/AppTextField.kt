package com.hfad.palamarchuksuperapp.core.ui.composables.basic

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfad.palamarchuksuperapp.ui.compose.theme.AppTheme
import com.hfad.palamarchuksuperapp.R

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
 * @param supportingText Компонент для отображения вспомогательного текста.
 * @param enable Включено ли текстовое поле.
 */
@Suppress("LongParameterList", "UNUSED_ANONYMOUS_PARAMETER")
@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes labelRes: Int? = null,
    @StringRes placeholderRes: Int? = null,
    label: @Composable (() -> Unit)? = labelRes?.let { labelRes ->
        {
            AppText(
                labelRes, appTextConfig = appTextConfig(
                    textStyle = MaterialTheme.typography.bodySmall
                )
            )
        }
    },
    placeholder: @Composable (() -> Unit)? = placeholderRes?.let {
        { AppText(it, Modifier.alpha(0.33f)) }
    },
    colors: TextFieldColors = TextFieldDefaults.appColors(),
    shape: Shape = TextFieldDefaults.shape,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    editTextConfig: AppTextFieldConfig = rememberTextFieldConfig(),
) {
    TextField(
        value = value,
        onValueChange = { text: String -> onValueChange(text) },
        modifier = modifier,
        shape = shape,
        colors = colors,
        label = label,
        textStyle = textStyle,
        placeholder = placeholder,
        enabled = editTextConfig.enabled,
        readOnly = editTextConfig.readOnly,
        leadingIcon = editTextConfig.leadingIcon,
        trailingIcon = editTextConfig.trailingIcon,
        prefix = editTextConfig.prefix,
        suffix = editTextConfig.suffix,
        supportingText = editTextConfig.supportingText,
        isError = editTextConfig.isError,
        visualTransformation = editTextConfig.visualTransformation,
        keyboardOptions = editTextConfig.keyboardOptions,
        keyboardActions = editTextConfig.keyboardActions,
        singleLine = editTextConfig.singleLine,
        maxLines = editTextConfig.maxLines,
        minLines = editTextConfig.minLines,
        interactionSource = editTextConfig.interactionSource,
    )
}


/**
 * Preview для компонента [AppTextField].
 * Отображает компонент в светлой и темной темах.
 */
@Composable
@Preview(showBackground = false)
internal fun AppTextFieldPreview() {
    Column {
        val text = remember { mutableStateOf("Text Text Text Text Text Text") }
        AppTheme(useDarkTheme = true) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AppTextField(
                    modifier = Modifier.padding(16.dp),
                    value = text.value,
                    onValueChange = { text.value = it },
                    label = {
                        AppText(
                            R.string.model_hint,
                        )
                    },
                )
            }
        }
        AppTheme(useDarkTheme = false) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AppTextField(
                    modifier = Modifier.padding(16.dp),
                    value = text.value,
                    onValueChange = { text.value = it },
                    label = {
                        AppText(
                            R.string.model_hint,
                        )
                    },
                )
            }
        }
    }
}