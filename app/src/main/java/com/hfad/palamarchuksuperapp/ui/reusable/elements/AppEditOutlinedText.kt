package com.hfad.palamarchuksuperapp.ui.reusable.elements

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.hfad.palamarchuksuperapp.R

/**
 * Компонент для редактирования текста с поддержкой метки и подсказки.
 *
 * @param modifier Модификатор для настройки внешнего вида.
 * @param text Текущее значение текста.
 * @param onValueChanged Обратный вызов при изменении текста.
 * @param labelRes Идентификатор строкового ресурса для метки.
 * @param label Компонент для отображения метки.
 * @param placeholderRes Идентификатор строкового ресурса для подсказки.
 * @param placeholder Компонент для отображения подсказки.
 * @param textStyle Стиль текста.
 * @param colors Цвета для текстового поля.
 * @param enable Включено ли текстовое поле.
 */
@Composable
@Suppress("LongParameterList")
fun AppEditOutlinedText(
    modifier: Modifier = Modifier,
    text: String,
    onValueChanged: (String) -> Unit,
    @StringRes labelRes: Int? = R.string.model_hint,
    @StringRes placeholderRes: Int? = R.string.edit,
    label: @Composable (() -> Unit)? = labelRes?.let { { AppText(it) } },
    placeholder: @Composable (() -> Unit)? = placeholderRes?.let {
        { AppText(it, Modifier.alpha(0.33f)) }
    },
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    colors: TextFieldColors = OutlinedTextFieldDefaults.appColors(),
    enable: Boolean = true,
) {
    OutlinedTextField(
        enabled = enable,
        value = text,
        onValueChange = { onValueChanged(it) },
        label = label,
        colors = colors,
        placeholder = placeholder,
        modifier = modifier,
        textStyle = textStyle
    )
}

/**
 * Preview для компонента [AppEditOutlinedText].
 * Отображает компонент в светлой и темной темах.
 */
@Composable
@Preview(showBackground = false)
fun AppOutlinedTextPreview() {
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
                AppEditOutlinedText(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.Center),
                    text = text.value,
                    labelRes = R.string.model_hint,
                    placeholderRes = R.string.edit,
                    onValueChanged = { text.value = it },
                )
            }
        }
        AppTheme(useDarkTheme = false) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp)
            ) {
                AppEditOutlinedText(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.TopCenter),
                    text = text.value,
                    labelRes = R.string.model_hint,
                    placeholderRes = R.string.edit,
                    onValueChanged = { text.value = it },
                )
            }
        }
    }
}