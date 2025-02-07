package com.hfad.palamarchuksuperapp.ui.reusable.elements

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.hfad.palamarchuksuperapp.R

/**
 * Компонент для редактирования текста с поддержкой метки и подсказки.
 *
 * @param modifier Модификатор для настройки внешнего вида.
 * @param value Текущее значение текста.
 * @param onValueChanged Обратный вызов при изменении текста.
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
fun AppEditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    @StringRes labelRes: Int? = null,
    @StringRes placeholderRes: Int? = null,
    label: @Composable (() -> Unit)? = labelRes?.let { labelRes ->
        {
            AppText(
                labelRes, appTextConfig = rememberAppTextConfig(
                    textStyle = MaterialTheme.typography.bodySmall
                )
            )
        }
    },
    placeholder: @Composable (() -> Unit)? = placeholderRes?.let {
        { AppText(it, Modifier.alpha(0.33f)) }
    },
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    colors: TextFieldColors = TextFieldDefaults.appColors(),
    supportingText: @Composable (() -> Unit)? = null, //It is rarely used
    enable: Boolean = true,
) {
    TextField(
        modifier = modifier,
        enabled = enable,
        value = value,
        onValueChange = { onValueChanged(it) },
        supportingText = supportingText,
        label = label,
        placeholder = placeholder,
        colors = colors,
        textStyle = textStyle,
    )
}



/**
 * Preview для компонента [AppEditText].
 * Отображает компонент в светлой и темной темах.
 */
@Composable
@Preview(showBackground = false)
private fun AppTextFieldPreview() {
    Column {
        val text = remember { mutableStateOf("Text Text Text Text Text Text") }
        AppTheme(useDarkTheme = true) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AppEditText(
                    modifier = Modifier.padding(16.dp),
                    value = text.value,
                    onValueChanged = { text.value = it },
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
                AppEditText(
                    modifier = Modifier.padding(16.dp),
                    value = text.value,
                    onValueChanged = { text.value = it },
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